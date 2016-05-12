package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.conversion.executor.strategy.ExecuteOnceStrategy;
import com.netflix.imfutility.conversion.executor.strategy.ExecutePipeStrategy;
import com.netflix.imfutility.conversion.executor.strategy.OperationInfo;
import com.netflix.imfutility.conversion.executor.strategy.PipeOperationInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfoBuilder;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.xsd.conversion.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Executor of {@link ExecEachSegmentType} conversion operation.
 * <ul>
 * <li>Execute the operation for each segment (Segment Template Parameter Context is used)</li>
 * </ul>
 */
public class ConversionExecutorSegment implements IConversionExecutor {

    private final TemplateParameterContextProvider contextProvider;
    private final ExecEachSegmentSequenceType execEachSegm;
    private final int segmentNum;

    public ConversionExecutorSegment(TemplateParameterContextProvider contextProvider, ExecEachSegmentSequenceType execEachSegm) {
        this.contextProvider = contextProvider;
        this.execEachSegm = execEachSegm;
        this.segmentNum = contextProvider.getSegmentContext().getSegmentsNum();
    }

    @Override
    public void execute() throws IOException {
        for (int segment = 0; segment < segmentNum; segment++) {
            for (Object operation : execEachSegm.getPipeOrExecOnceOrExecEachSequence()) {
                if (operation instanceof PipeSequenceType) {
                    execPipe((PipeSegmentType) operation);
                } else if (operation instanceof ExecOnceType) {
                    execOnce((ExecOnceType) operation);
                } else if (operation instanceof ExecEachSequenceType) {
                    execSequence((ExecEachSequenceType) operation);
                } else if (operation instanceof DynamicParameterType) {
                    addDynamicParameter((DynamicParameterType) operation);
                } else {
                    throw new RuntimeException(String.format("Unknown Conversion Operation type: %s", operation.toString()));
                }
            }
        }
    }

    private void execOnce(ExecOnceType execOnce) throws IOException {
        new ExecuteOnceStrategy(contextProvider).execute(getExecOnceOperation(execOnce));
    }

    private void execSequence(ExecEachSequenceType execSequence) throws IOException {
        for (OperationInfo seqOperation : getExecSequenceOperations(execSequence)) {
            new ExecuteOnceStrategy(contextProvider).execute(seqOperation);
        }
    }

    private void execPipe(PipeSegmentType pipe) throws IOException {
        // 1. prepare operation to be executed in a pipe
        PipeOperationInfo pipeInfo = new PipeOperationInfo();

        for (ExecOnceType tailOperation : pipe.getExecOnce()) {
            pipeInfo.getTailOperations().add(getExecOnceOperation(tailOperation));
        }
        if (pipe.getCycle() != null) {
            for (Object cycleOperation : pipe.getCycle().getExecEachSegmentOrExecOnce()) {
                if (cycleOperation instanceof ExecOnceType) {
                    pipeInfo.getCycleOperations().add(
                            getExecOnceOperation((ExecOnceType) cycleOperation));
                } else if (cycleOperation instanceof ExecEachSequenceType) {
                    pipeInfo.getCycleOperations().addAll(
                            getExecSequenceOperations((ExecEachSequenceType) cycleOperation));
                }
            }
        }

        // 2. execute in a pipe
        new ExecutePipeStrategy(contextProvider).execute(pipeInfo);
    }

    private void addDynamicParameter(DynamicParameterType dynamicParam) {
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegment(segmentNum)
                .build();
        contextProvider.getDynamicContext().appendParameter(
                dynamicParam.getName(), dynamicParam.getValue(), contextInfo);
    }

    private OperationInfo getExecOnceOperation(ExecOnceType execOnce) {
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegment(segmentNum)
                .build();
        return new OperationInfo(execOnce.getValue(), execOnce.getName(), execOnce.getClass(),
                contextInfo);
    }

    private List<OperationInfo> getExecSequenceOperations(ExecEachSequenceType execSequence) {
        List<OperationInfo> result = new ArrayList<>();

        // 1. get sequence type
        SequenceType seqType = execSequence.getType();

        //2. get sequence number
        int seqNum = contextProvider.getSequenceContext().getSequenceCount(seqType);

        // 2. process operation for each sequence within segment
        for (int seq = 0; seq < seqNum; seq++) {
            // 2.1 get resource number for the the given (segment, sequence):
            int resourceNum = contextProvider.getResourceContext().getResourceCount(segmentNum, seqNum, seqType);

            // 2.2 process operations for each resource within segment and sequence
            for (int resource = 0; resource < resourceNum; resource++) {
                // context info
                ContextInfo contextInfo = new ContextInfoBuilder()
                        .setSequence(seqNum)
                        .setSequenceType(seqType)
                        .setSegment(segmentNum)
                        .setResource(resource)
                        .build();

                // executable: operation info
                if (execSequence.getExec() != null) {
                    OperationInfo operationInfo = new OperationInfo(execSequence.getExec().getValue(), execSequence.getName(), execSequence.getClass(),
                            contextInfo);
                    result.add(operationInfo);
                }

                // dynamic parameter
                if (execSequence.getDynamicParameter() != null) {
                    for (DynamicParameterType dynamicParam : execSequence.getDynamicParameter()) {
                        contextProvider.getDynamicContext().appendParameter(
                                dynamicParam.getName(), dynamicParam.getValue(), contextInfo);
                    }
                }
            }
        }

        return result;
    }

}
