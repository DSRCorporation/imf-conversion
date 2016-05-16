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
 * An executor for {@link ExecEachSegmentSequenceType} conversion operation.
 * It reads all sub-conversion operations for the input segment conversion operation and executes them
 * either once or in a pipe using an appropriate execute strategy.
 */
public class ConversionExecutorSegment extends AbstractConversionExecutor {

    private final ExecEachSegmentSequenceType execEachSegm;

    private String currentSegmentUuid;

    public ConversionExecutorSegment(TemplateParameterContextProvider contextProvider, ExecuteStrategyFactory strategyProvider,
                                     ExecEachSegmentSequenceType execEachSegm) {
        super(contextProvider, strategyProvider);
        this.execEachSegm = execEachSegm;
    }

    @Override
    public void execute() throws IOException {
        for (String segmentUuid : contextProvider.getSegmentContext().getUuids()) {
            this.currentSegmentUuid = segmentUuid;

            for (Object operation : execEachSegm.getPipeOrExecOnceOrExecEachSequence()) {
                if (operation instanceof PipeSegmentType) {
                    execPipe((PipeSegmentType) operation);
                } else if (operation instanceof ExecOnceType) {
                    execOnce((ExecOnceType) operation);
                } else if (operation instanceof ExecEachSequenceType) {
                    execSequence((ExecEachSequenceType) operation);
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

    private void execSequence(ExecEachSequenceType execSequence) throws IOException {
        for (OperationInfo seqOperation : getExecSequenceOperations(execSequence)) {
            executeStrategyFactory.createExecuteOnceStrategy(contextProvider).execute(seqOperation);
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
        executeStrategyFactory.createExecutePipeStrategy(contextProvider).execute(pipeInfo);
    }

    private void addDynamicParameter(DynamicParameterConcatType dynamicParam) {
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(currentSegmentUuid)
                .build();
        contextProvider.getDynamicContext().addParameter(dynamicParam, contextInfo);
    }

    private OperationInfo getExecOnceOperation(ExecOnceType execOnce) {
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(currentSegmentUuid)
                .build();
        return new OperationInfo(execOnce.getValue(), execOnce.getName(), execOnce.getClass(),
                contextInfo);
    }

    private List<OperationInfo> getExecSequenceOperations(ExecEachSequenceType execSequence) {
        List<OperationInfo> result = new ArrayList<>();

        // 1. get sequence type
        SequenceType seqType = execSequence.getType();

        // 2. process operation for each sequence within segment
        for (String seqUuid : contextProvider.getSequenceContext().getUuids(seqType)) {
            // process operations for each resource within segment and sequence
            ResourceKey resKey = ResourceKey.create(currentSegmentUuid, seqUuid, seqType);
            for (String resourceUuid : contextProvider.getResourceContext().getUuids(resKey)) {
                // context info
                ContextInfo contextInfo = new ContextInfoBuilder()
                        .setSequenceUuid(seqUuid)
                        .setSequenceType(seqType)
                        .setSegmentUuid(currentSegmentUuid)
                        .setResourceUuid(resourceUuid)
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
