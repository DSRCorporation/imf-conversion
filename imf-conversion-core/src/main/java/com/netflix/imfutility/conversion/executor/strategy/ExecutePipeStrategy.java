/*
 * Copyright (C) 2016 Netflix, Inc.
 *
 *     This file is part of IMF Conversion Utility.
 *
 *     IMF Conversion Utility is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     IMF Conversion Utility is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with IMF Conversion Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.netflix.imfutility.conversion.executor.strategy;

import com.netflix.imfutility.conversion.executor.ExternalProcess;
import com.netflix.imfutility.conversion.executor.OutputRedirect;
import com.netflix.imfutility.conversion.executor.ProcessStarter;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.util.ImfLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Execute all operations in a pipeline.
 * <p>
 * Example of the input {@link PipeOperationInfo}:
 * <ul>
 * <li>cycleOperations: {{cycle11, cycle12, cycle13}, {cycle21, cycle22}},</li>
 * <li>pipeOperations: {pipe1, pipe2, pipe3}.</li>
 * <li>Execution order:
 * <ol>
 * <li>start pipe1, pipe2, pipe3;</li>
 * <li>start cycle11, cycle12, cycle13</li>
 * <li>create a pipeline:  cycle11 -> cycle12 -> cycle13 -> pipe1 -> pipe2 -> pipe3</li>
 * <li>wait until the first operation (cycle11) is finished</li>
 * <li>finish cycle 12 and cycle 13 (pipe1, pipe2 and pipe3 are still running)</li>
 * <li>start cycle21, cycle22</li>
 * <li>create a pipeline:  cycle21 -> cycle22 -> pipe1 -> pipe2 -> pipe3</li>
 * <li>wait until the first operation (cycle21) is finished</li>
 * <li>finish cycle 22</li>
 * <li>finish pipe1, pipe2 and pipe3</li>
 * </ol>
 * </li>
 * </ul>
 * </p>
 */
public class ExecutePipeStrategy extends AbstractExecuteStrategy {

    private static final Logger LOGGER = new ImfLogger(LoggerFactory.getLogger(ExecutePipeStrategy.class));

    public ExecutePipeStrategy(TemplateParameterContextProvider contextProvider, ProcessStarter processStarter) {
        super(contextProvider, processStarter);
    }

    public void execute(PipeOperationInfo operations) throws IOException {
        PipeOperationInfo actualOperations = skipPipeOperations(operations);

        List<ExternalProcess> tailProcesses = new ArrayList<>();

        try {
            // 1. start all tailing operation
            startTailProcesses(actualOperations, tailProcesses);

            // 2. start all cycle processes in a sequence subsequently in pipelines
            if (actualOperations.getCycleOperations().isEmpty()) {
                processNonCycle(tailProcesses);
            } else {
                processCycle(actualOperations, tailProcesses);
            }
        } finally {
            // 3. close all tail processes.
            tailProcesses.forEach(ExternalProcess::finishClose);
        }
    }

    private void startTailProcesses(PipeOperationInfo operations, List<ExternalProcess> tailProcesses) throws IOException {
        int i = 0;
        for (OperationInfo tailOperation : operations.getTailOperations()) {
            // output can be safely re-directed to stderr's log, if it's the last element in the pipeline.
            // otherwise output must not be re-directed, as it goes to the next element in the pipeline.
            OutputRedirect outputRedirect = i < operations.getTailOperations().size() - 1 ? OutputRedirect.PIPE : OutputRedirect.ERR_LOG;
            i++;
            tailProcesses.add(startProcess(tailOperation, outputRedirect));
        }
    }

    private void processNonCycle(List<ExternalProcess> pipeline) {
        pipe(pipeline);
    }

    private void processCycle(PipeOperationInfo operations, List<ExternalProcess> tailProcesses) throws IOException {
        for (List<OperationInfo> cyclePipeOperations : operations.getCycleOperations()) {
            processCyclePipe(cyclePipeOperations, tailProcesses);
        }
    }

    private void processCyclePipe(List<OperationInfo> cyclePipeOperations, List<ExternalProcess> tailProcesses) throws IOException {
        List<ExternalProcess> headProcesses = new ArrayList<>();
        try {
            for (OperationInfo headOperation : cyclePipeOperations) {
                // output must not be re-directed, as it goes to the next element in the pipeline.
                headProcesses.add(startProcess(headOperation, OutputRedirect.PIPE));
            }
            pipe(headProcesses, tailProcesses);
        } finally {
            headProcesses.forEach(ExternalProcess::finishClose);
        }
    }

    private void pipe(List<ExternalProcess> head, List<ExternalProcess> tail) {
        List<ExternalProcess> pipeline = new ArrayList<>(head);
        pipeline.addAll(tail);
        pipe(pipeline);
    }

    private void pipe(List<ExternalProcess> pipeline) {
        if (pipeline.isEmpty()) {
            return;
        }
        // 1. start a new thread to copy input - output in a pipeline
        ExternalProcess p1;
        ExternalProcess p2;
        for (int i = 0; i < pipeline.size(); i++) {
            p1 = pipeline.get(i);
            if (i + 1 < pipeline.size()) {
                p2 = pipeline.get(i + 1);
                new Thread(new Piper(p1, p2)).start();
            }
        }

        // 2. Wait for the first process in chain
        ExternalProcess firstProcess = pipeline.get(0);
        firstProcess.finishWaitFor();
    }

    protected PipeOperationInfo skipPipeOperations(PipeOperationInfo operations) {
        PipeOperationInfo newOperations = new PipeOperationInfo();
        for (List<OperationInfo> cycleOperations : operations.getCycleOperations()) {
            List<OperationInfo> executable = skipOperations(cycleOperations);
            if (!executable.isEmpty()) {
                newOperations.addCycleOperation(executable);
            }
        }
        newOperations.addTailOperations(skipOperations(operations.getTailOperations()));
        return newOperations;
    }

    private static class Piper implements Runnable {

        private static final int BUF_SIZE = 512;

        private final ExternalProcess inputProcess;
        private final ExternalProcess outputProcess;

        Piper(ExternalProcess inputProcess, ExternalProcess outputProcess) {
            this.inputProcess = inputProcess;
            this.outputProcess = outputProcess;
        }

        public void run() {
            // don't use buffered streams!
            InputStream input = inputProcess.getProcess().getInputStream(); //new BufferedInputStream(input);
            OutputStream output = outputProcess.getProcess().getOutputStream(); //new BufferedOutputStream(output);
            try {
                byte[] b = new byte[BUF_SIZE];
                int read = 1;
                while (read > -1) {
                    read = input.read(b, 0, b.length);
                    if (read > -1) {
                        output.write(b, 0, read);
                    }
                }
            } catch (IOException e) {
                try {
                    input.close();
                } catch (IOException e1) {
                    LOGGER.error(
                            "Can not close " + inputProcess.toString(),
                            e1);
                }

                try {
                    output.close();
                } catch (IOException e1) {
                    LOGGER.error(
                            "Can not close " + outputProcess.toString(),
                            e1);
                }

                LOGGER.error(
                        String.format("Broken pipe. Input process: %s. Output Process: %s",
                                inputProcess.toString(), outputProcess.toString()),
                        e);
            }
        }

    }
}
