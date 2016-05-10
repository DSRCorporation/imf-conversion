package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.conversion.templateParameter.TemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterResolver;
import com.netflix.imfutility.conversion.templateParameter.context.SegmentTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.xsd.conversion.ExecEachSegmentType;
import com.netflix.imfutility.xsd.conversion.ExecOnceType;
import com.netflix.imfutility.xsd.conversion.PipeType;
import com.netflix.imfutility.xsd.conversion.SequenceType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Executor of {@link PipeType} conversion operation.
 * <ul>
 * <li>Execute all operations in a pipeline</li>
 * <li>Supports {@link SequenceType}</li>
 * </ul>
 */
public class ConversionExecutorPipe extends AbstractConversionExecutor {

    public ConversionExecutorPipe(TemplateParameterContextProvider contextProvider) {
        super(contextProvider);
    }

    public void execute(PipeType operation) throws IOException {
        // 1. start all tailing operation
        List<ExternalProcess> tailProcesses = getTailProcesses(operation);

        // 2. start all first processes in a sequence subsequently in pipelines
        try {
            SequenceType seq = operation.getSequence();
            if (seq == null) {
                processNonSeq(tailProcesses);
            } else {
                processSeq(seq, tailProcesses);
            }
        } finally {
            // 3. close all tail processes.
            tailProcesses.forEach(ExternalProcess::finishClose);
        }
    }

    private List<ExternalProcess> getTailProcesses(PipeType operation) throws IOException {
        List<ExternalProcess> pipeline = new ArrayList<>();
        for (ExecOnceType execOnce : operation.getExecOnce()) {
            List<String> execAndParams = conversionOperationParser.parseOperation(execOnce.getValue());
            pipeline.add(startProcess(execAndParams, execOnce.getName(), execOnce.getClass()));
        }
        return pipeline;
    }

    private void processNonSeq(List<ExternalProcess> pipeline) {
        pipe(pipeline);
    }

    private void processSeq(SequenceType seq, List<ExternalProcess> tailProcesses) throws IOException {
        for (Object seqOperation : seq.getExecEachSegmentOrExecOnce()) {
            if (seqOperation instanceof ExecOnceType) {
                processSeqExecOnce((ExecOnceType) seqOperation, tailProcesses);
            } else if (seqOperation instanceof ExecEachSegmentType) {
                processSeqSegments((ExecEachSegmentType) seqOperation, tailProcesses);
            }
        }
    }

    private void processSeqExecOnce(ExecOnceType execOnce, List<ExternalProcess> tail) throws IOException {
        // 1. start the first Process
        List<String> execAndParams = conversionOperationParser.parseOperation(execOnce.getValue());
        ExternalProcess execOnceProc = startProcess(execAndParams, execOnce.getName(), execOnce.getClass());

        // 2. create a pipeline: first + tail
        pipe(execOnceProc, tail);
    }


    private void processSeqSegments(ExecEachSegmentType execSegm, List<ExternalProcess> tail) throws IOException {
        // 1. get segments number
        SegmentTemplateParameterContext segmContext = parameterResolver.getContextProvider().getSegmentContext();
        if (segmContext == null) {
            throw new RuntimeException(String.format("'%s' context not found!", TemplateParameterContext.SEGMENT.getName()));
        }
        int segmentNum = segmContext.getSegmentsNum();

        // 2. for each segment: create a pipeline: segmentProc + tail
        for (int segment = 0; segment < segmentNum; segment++) {
            List<String> resolvedParams = conversionOperationParser.parseOperation(execSegm.getValue(), segment, execSegm.getType());
            ExternalProcess execSegmProc = startProcess(resolvedParams, execSegm.getName(), execSegm.getClass());
            pipe(execSegmProc, tail);
        }
    }

    private void pipe(ExternalProcess firstProc, List<ExternalProcess> tail) {
        List<ExternalProcess> pipeline = new ArrayList<>();
        pipeline.add(firstProc);
        pipeline.addAll(tail);
        pipe(pipeline);
    }

    private void pipe(List<ExternalProcess> pipeline) {
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


    private static class Piper implements Runnable {

        private static final int BUF_SIZE = 512;

        private final ExternalProcess inputProcess;
        private final ExternalProcess outputProcess;

        public Piper(ExternalProcess inputProcess, ExternalProcess outputProcess) {
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
                throw new RuntimeException(
                        String.format("Broken pipe. Input process: %s. Output Process: %s", inputProcess.toString(), outputProcess.toString()),
                        e);
            }
        }

    }

}
