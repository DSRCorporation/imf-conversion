package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.conversion.executor.strategy.ExecuteStrategyFactory;
import com.netflix.imfutility.conversion.executor.strategy.OperationInfo;
import com.netflix.imfutility.conversion.executor.strategy.PipeOperationInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfoBuilder;
import com.netflix.imfutility.conversion.templateParameter.context.ResourceKey;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.xsd.conversion.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An executor for {@link ExecEachSequenceSegmentType} conversion operation.
 * It reads all sub-conversion operations for the input sequence conversion operation and executes them
 * either once or in a pipe using an appropriate execute strategy.
 */
public class ConversionExecutorSequence extends AbstractConversionExecutor {

    private final ExecEachSequenceSegmentType execEachSeq;
    private final SequenceType seqType;

    private String currentSeqUuid;

    public ConversionExecutorSequence(TemplateParameterContextProvider contextProvider, ExecuteStrategyFactory strategyProvider,
                                      ExecEachSequenceSegmentType execEachSeq) {
        super(contextProvider, strategyProvider);
        this.execEachSeq = execEachSeq;

        this.seqType = execEachSeq.getType();
    }

    @Override
    public void execute() throws IOException {
        for (String seqUuid : contextProvider.getSequenceContext().getUuids(seqType)) {
            this.currentSeqUuid = seqUuid;

            for (Object operation : execEachSeq.getPipeOrExecOnceOrExecEachSegment()) {
                if (operation instanceof PipeSequenceType) {
                    execPipe((PipeSequenceType) operation);
                } else if (operation instanceof ExecOnceType) {
                    execOnce((ExecOnceType) operation);
                } else if (operation instanceof ExecEachSegmentType) {
                    execSegment((ExecEachSegmentType) operation);
                } else if (operation instanceof DynamicParameterConcatType) {
                    addDynamicParameter((DynamicParameterConcatType) operation);
                } else {
                    throw new RuntimeException(String.format("Unknown Conversion Operation type: %s", operation.toString()));
                }
            }
        }
    }

    private void execOnce(ExecOnceType execOnce) throws IOException {
        executeStrategyFactory.createExecuteOnceStrategy(contextProvider).execute(getExecOnceOperation(execOnce));
    }

    private void execSegment(ExecEachSegmentType execSegment) throws IOException {
        for (OperationInfo segmentOperation : getExecSegmentOperations(execSegment)) {
            executeStrategyFactory.createExecuteOnceStrategy(contextProvider).execute(segmentOperation);
        }
    }

    private void execPipe(PipeSequenceType pipe) throws IOException {
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
                } else if (cycleOperation instanceof ExecEachSegmentType) {
                    pipeInfo.getCycleOperations().addAll(
                            getExecSegmentOperations((ExecEachSegmentType) cycleOperation));
                }
            }
        }

        // 2. execute in a pipe
        executeStrategyFactory.createExecutePipeStrategy(contextProvider).execute(pipeInfo);
    }

    private void addDynamicParameter(DynamicParameterConcatType dynamicParam) {
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSequenceUuid(currentSeqUuid)
                .setSequenceType(seqType)
                .build();
        contextProvider.getDynamicContext().addParameter(dynamicParam, contextInfo);
    }

    private OperationInfo getExecOnceOperation(ExecOnceType execOnce) {
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSequenceUuid(currentSeqUuid)
                .setSequenceType(seqType)
                .build();
        return new OperationInfo(execOnce.getValue(), execOnce.getName(), execOnce.getClass(),
                contextInfo);
    }

    private List<OperationInfo> getExecSegmentOperations(ExecEachSegmentType execSegment) {
        List<OperationInfo> result = new ArrayList<>();

        // process operations for each segment within sequence
        for (String segmUuid : contextProvider.getSegmentContext().getUuids()) {
            // process operations for each resource within segment and sequence
            ResourceKey resKey = ResourceKey.create(segmUuid, currentSeqUuid, seqType);
            for (String resourceUuid : contextProvider.getResourceContext().getUuids(resKey)) {
                // context info
                ContextInfo contextInfo = new ContextInfoBuilder()
                        .setSequenceUuid(currentSeqUuid)
                        .setSequenceType(seqType)
                        .setSegmentUuid(segmUuid)
                        .setResourceUuid(resourceUuid)
                        .build();

                // executable: operation info
                if (execSegment.getExec() != null) {
                    OperationInfo operationInfo = new OperationInfo(execSegment.getExec().getValue(), execSegment.getName(), execSegment.getClass(),
                            contextInfo);
                    result.add(operationInfo);
                }

                // dynamic parameter
                if (execSegment.getDynamicParameter() != null) {
                    for (DynamicParameterConcatType dynamicParam : execSegment.getDynamicParameter()) {
                        contextProvider.getDynamicContext().addParameter(dynamicParam, contextInfo);
                    }
                }
            }
        }

        return result;
    }

}
