package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.conversion.executor.strategy.ExecuteStrategyFactory;
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
 * An executor for {@link ExecEachSegmentSequenceType} conversion operation.
 * It reads all sub-conversion operations for the input segment conversion operation and executes them
 * either once or in a pipe using an appropriate execute strategy.
 */
public class ConversionExecutorSegment extends AbstractConversionExecutor {

    private final ExecEachSegmentSequenceType execEachSegm;
    private final int segmentNum;

    public ConversionExecutorSegment(TemplateParameterContextProvider contextProvider, ExecuteStrategyFactory strategyProvider,
                                     ExecEachSegmentSequenceType execEachSegm) {
        super(contextProvider, strategyProvider);
        this.execEachSegm = execEachSegm;
        this.segmentNum = contextProvider.getSegmentContext().getSegmentsNum();
    }

    @Override
    public void execute() throws IOException {
        for (int segment = 0; segment < segmentNum; segment++) {
            for (Object operation : execEachSegm.getPipeOrExecOnceOrExecEachSequence()) {
                if (operation instanceof PipeSegmentType) {
                    execPipe((PipeSegmentType) operation, segment);
                } else if (operation instanceof ExecOnceType) {
                    execOnce((ExecOnceType) operation, segment);
                } else if (operation instanceof ExecEachSequenceType) {
                    execSequence((ExecEachSequenceType) operation, segment);
                } else if (operation instanceof DynamicParameterConcatType) {
                    addDynamicParameter((DynamicParameterConcatType) operation, segment);
                } else {
                    throw new RuntimeException(String.format("Unknown Conversion Operation type: %s", operation.toString()));
                }
            }
        }
    }

    private void execOnce(ExecOnceType execOnce, int segment) throws IOException {
        executeStrategyFactory.createExecuteOnceStrategy(contextProvider).execute(getExecOnceOperation(execOnce, segment));
    }

    private void execSequence(ExecEachSequenceType execSequence, int segment) throws IOException {
        for (OperationInfo seqOperation : getExecSequenceOperations(execSequence, segment)) {
            executeStrategyFactory.createExecuteOnceStrategy(contextProvider).execute(seqOperation);
        }
    }

    private void execPipe(PipeSegmentType pipe, int segment) throws IOException {
        // 1. prepare operation to be executed in a pipe
        PipeOperationInfo pipeInfo = new PipeOperationInfo();

        for (ExecOnceType tailOperation : pipe.getExecOnce()) {
            pipeInfo.getTailOperations().add(getExecOnceOperation(tailOperation, segment));
        }
        if (pipe.getCycle() != null) {
            for (Object cycleOperation : pipe.getCycle().getExecEachSegmentOrExecOnce()) {
                if (cycleOperation instanceof ExecOnceType) {
                    pipeInfo.getCycleOperations().add(
                            getExecOnceOperation((ExecOnceType) cycleOperation, segment));
                } else if (cycleOperation instanceof ExecEachSequenceType) {
                    pipeInfo.getCycleOperations().addAll(
                            getExecSequenceOperations((ExecEachSequenceType) cycleOperation, segment));
                }
            }
        }

        // 2. execute in a pipe
        executeStrategyFactory.createExecutePipeStrategy(contextProvider).execute(pipeInfo);
    }

    private void addDynamicParameter(DynamicParameterConcatType dynamicParam, int segment) {
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegment(segment)
                .build();
        contextProvider.getDynamicContext().addParameter(dynamicParam, contextInfo);
    }

    private OperationInfo getExecOnceOperation(ExecOnceType execOnce, int segment) {
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegment(segment)
                .build();
        return new OperationInfo(execOnce.getValue(), execOnce.getName(), execOnce.getClass(),
                contextInfo);
    }

    private List<OperationInfo> getExecSequenceOperations(ExecEachSequenceType execSequence, int segment) {
        List<OperationInfo> result = new ArrayList<>();

        // 1. get sequence type
        SequenceType seqType = execSequence.getType();

        //2. get sequence number
        int seqNum = contextProvider.getSequenceContext().getSequenceCount(seqType);

        // 2. process operation for each sequence within segment
        for (int seq = 0; seq < seqNum; seq++) {
            // 2.1 get resource number for the the given (segment, sequence):
            int resourceNum = contextProvider.getResourceContext().getResourceCount(segment, seqNum, seqType);

            // 2.2 process operations for each resource within segment and sequence
            for (int resource = 0; resource < resourceNum; resource++) {
                // context info
                ContextInfo contextInfo = new ContextInfoBuilder()
                        .setSequence(seqNum)
                        .setSequenceType(seqType)
                        .setSegment(segment)
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
                    for (DynamicParameterConcatType dynamicParam : execSequence.getDynamicParameter()) {
                        contextProvider.getDynamicContext().addParameter(dynamicParam, contextInfo);
                    }
                }
            }
        }

        return result;
    }

}
