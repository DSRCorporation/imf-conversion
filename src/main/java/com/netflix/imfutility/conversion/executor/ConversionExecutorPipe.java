package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.conversion.templateParameter.TemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterResolver;
import com.netflix.imfutility.conversion.templateParameter.context.ITemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.segment.ISegmentTemplateParameterContext;
import com.netflix.imfutility.xsd.conversion.ExecEachSegmentType;
import com.netflix.imfutility.xsd.conversion.ExecOnceType;
import com.netflix.imfutility.xsd.conversion.PipeType;
import com.netflix.imfutility.xsd.conversion.SequenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 4/27/2016.
 */
public class ConversionExecutorPipe extends AbstractConversionExecutor {

    final Logger logger = LoggerFactory.getLogger(ConversionExecutorPipe.class);

    public ConversionExecutorPipe(TemplateParameterResolver parameterResolver) {
        super(parameterResolver);
    }

    public void execute(PipeType operation) throws IOException, InterruptedException {
        // 1. start all tailing operation
        List<Process> tailProcesses = getTailProcesses(operation);

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
            for (Process proc : tailProcesses) {
                proc.getOutputStream().close();
            }
        }
    }

    private List<Process> getTailProcesses(PipeType operation) throws IOException {
        List<Process> pipeline = new ArrayList<>();
        for (ExecOnceType execOnce : operation.getExecOnce()) {
            List<String> resolvedParams = resolveParameters(execOnce.getValue());
            pipeline.add(startProcess(resolvedParams, execOnce.getName(), execOnce.getClass()));
        }
        return pipeline;
    }

    private void processNonSeq(List<Process> pipeline) throws InterruptedException {
        pipe(pipeline);
    }

    private void processSeq(SequenceType seq, List<Process> tailProcesses) throws IOException, InterruptedException {
        for (Object seqOperation : seq.getExecEachSegmentOrExecOnce()) {
            if (seqOperation instanceof ExecOnceType) {
                processSeqExecOnce((ExecOnceType) seqOperation, tailProcesses);
            } else if (seqOperation instanceof ExecEachSegmentType) {
                processSeqSegments((ExecEachSegmentType) seqOperation, tailProcesses);
            }
        }
    }

    private void processSeqExecOnce(ExecOnceType execOnce, List<Process> tail) throws IOException, InterruptedException {
        // 1. start the first Process
        List<String> resolvedParams = resolveParameters(execOnce.getValue());
        Process execOnceProc = startProcess(resolvedParams, execOnce.getName(), execOnce.getClass());

        // 2. create a pipeline: first + tail
        List<Process> pipeline = new ArrayList<>();
        pipeline.add(execOnceProc);
        pipeline.addAll(tail);
        pipe(pipeline);
    }


    private void processSeqSegments(ExecEachSegmentType execSegm, List<Process> tail) throws InterruptedException, IOException {
        // 1. get segments number
        ITemplateParameterContext segmContext = parameterResolver.getContextProvider().getContext(TemplateParameterContext.SEGMENT);
        if (!(segmContext instanceof ISegmentTemplateParameterContext)) {
            throw new RuntimeException(String.format("'%s' context not found!", TemplateParameterContext.SEGMENT.getName()));
        }
        int segmentNum = ((ISegmentTemplateParameterContext) segmContext).getSegmentsNum();

        // 2. for each segment: create a pipeline: segmentProc + tail
        for (int segment = 0; segment < segmentNum; segment++) {
            List<String> resolvedParams = resolveSegmentParameters(execSegm.getValue(), segment, execSegm.getType());
            Process execSegmProc = startProcess(resolvedParams, execSegm.getName(), execSegm.getClass());

            List<Process> pipeline = new ArrayList<>();
            pipeline.add(execSegmProc);
            pipeline.addAll(tail);
            pipe(pipeline);
        }
    }

    private void pipe(List<Process> pipeline) throws InterruptedException {
        Process p1;
        Process p2;
        for (int i = 0; i < pipeline.size(); i++) {
            p1 = pipeline.get(i);
            if (i + 1 < pipeline.size()) {
                p2 = pipeline.get(i + 1);
                // close the first Process only once it's finished
                new Thread(new Piper(p1.getInputStream(), p2.getOutputStream(), i == 0, false)).start();
            }
        }

        // Wait for the first process in chain
        Process firstProcess = pipeline.get(0);
        firstProcess.waitFor();
    }


    private class Piper implements Runnable {

        private final InputStream input;
        private final OutputStream output;
        private final boolean closeInput;
        private final boolean closeOutput;

        public Piper(InputStream input, OutputStream output, boolean closeInput, boolean closeOutput) {
            // don't use buffered streams!
            this.input = input; //new BufferedInputStream(input);
            this.output = output; //new BufferedOutputStream(output);
            this.closeInput = closeInput;
            this.closeOutput = closeOutput;
        }

        public void run() {
            try {
                byte[] b = new byte[512];
                int read = 1;
                while (read > -1) {
                    read = input.read(b, 0, b.length);
                    if (read > -1) {
                        output.write(b, 0, read);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Broken pipe", e);
            } finally {
                if (closeInput) {
                    try {
                        input.close();
                    } catch (IOException e) {
                    }
                }
                if (closeOutput) {
                    try {
                        output.close();
                    } catch (IOException e) {
                    }
                }
            }
        }


    }

}
