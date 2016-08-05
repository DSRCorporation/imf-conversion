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
package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.conversion.executor.strategy.ExecuteStrategyFactory;
import com.netflix.imfutility.conversion.executor.strategy.OperationInfo;
import com.netflix.imfutility.conversion.executor.strategy.PipeOperationInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfoBuilder;
import com.netflix.imfutility.conversion.templateParameter.context.ResourceKey;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.cpl.uuid.ResourceUUID;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.DynamicParameterConcatType;
import com.netflix.imfutility.generated.conversion.ExecEachSegmentSequenceType;
import com.netflix.imfutility.generated.conversion.ExecEachSequenceType;
import com.netflix.imfutility.generated.conversion.ExecOnceType;
import com.netflix.imfutility.generated.conversion.PipeSegmentType;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.generated.conversion.SubPipeType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An executor for {@link ExecEachSegmentSequenceType} conversion operation.
 * It reads all sub-conversion operations for the input segment conversion operation and executes them
 * either once or in a pipe using an appropriate execute strategy for each segment.
 * A sub-conversion operation may be executed either once, in a pipeline, or for each sequence.
 * <p>
 * See {@link com.netflix.imfutility.conversion.executor.strategy.ExecuteOnceStrategy} and
 * {@link com.netflix.imfutility.conversion.executor.strategy.ExecutePipeStrategy}.
 * </p>
 */
public class ConversionExecutorSegment extends AbstractConversionExecutor {

    private final ExecEachSegmentSequenceType execEachSegm;

    private SegmentUUID currentSegmentUuid;

    public ConversionExecutorSegment(TemplateParameterContextProvider contextProvider, ExecuteStrategyFactory strategyProvider,
                                     ExecEachSegmentSequenceType execEachSegm) {
        super(contextProvider, strategyProvider);
        this.execEachSegm = execEachSegm;
    }

    @Override
    public void execute() throws IOException {
        for (SegmentUUID segmentUuid : contextProvider.getSegmentContext().getUuids()) {
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
                    throw new ConversionException(String.format("Unknown Conversion Operation type: %s", operation.toString()));
                }
            }
        }
    }

    private void execOnce(ExecOnceType execOnce) throws IOException {
        executeStrategyFactory.createExecuteOnceStrategy(contextProvider).execute(getExecOnceOperation(execOnce));
    }

    private void execSequence(ExecEachSequenceType execSequence) throws IOException {
        if (execSequence.getExecOnce() != null) {
            for (OperationInfo segmentOperation : getExecSequenceOnceOperations(execSequence)) {
                executeStrategyFactory.createExecuteOnceStrategy(contextProvider).execute(segmentOperation);
            }
        } else if (execSequence.getPipe() != null) {
            for (List<OperationInfo> pipeOperations : getExecSequencePipeOperations(execSequence)) {
                PipeOperationInfo pipeInfo = new PipeOperationInfo();
                pipeInfo.addTailOperations(pipeOperations);
                executeStrategyFactory.createExecutePipeStrategy(contextProvider).execute(pipeInfo);
            }
        }
    }

    private void execPipe(PipeSegmentType pipe) throws IOException {
        // 1. prepare operation to be executed in a pipe
        PipeOperationInfo pipeInfo = new PipeOperationInfo();

        for (ExecOnceType tailOperation : pipe.getExecOnce()) {
            pipeInfo.getTailOperations().add(getExecOnceOperation(tailOperation));
        }
        if (pipe.getCycle() != null) {
            for (Object cycleOperation : pipe.getCycle().getExecEachSequenceOrPipeOrExecOnce()) {
                if (cycleOperation instanceof ExecOnceType) {
                    pipeInfo.addCycleOperation(getExecOnceOperation((ExecOnceType) cycleOperation));
                } else if (cycleOperation instanceof SubPipeType) {
                    pipeInfo.addCycleOperation(getSubPipeOperations((SubPipeType) cycleOperation));
                } else if (cycleOperation instanceof ExecEachSequenceType) {
                    execEachSequenceInPipe((ExecEachSequenceType) cycleOperation, pipeInfo);
                }
            }
        }

        // 2. execute in a pipe
        executeStrategyFactory.createExecutePipeStrategy(contextProvider).execute(pipeInfo);
    }

    private void execEachSequenceInPipe(ExecEachSequenceType execSequence, PipeOperationInfo pipeInfo) {
        if (execSequence.getExecOnce() != null) {
            getExecSequenceOnceOperations(execSequence).forEach(pipeInfo::addCycleOperation);
        } else if (execSequence.getPipe() != null) {
            getExecSequencePipeOperations(execSequence).forEach(pipeInfo::addCycleOperation);
        }
    }

    private void addDynamicParameter(DynamicParameterConcatType dynamicParam) {
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(currentSegmentUuid)
                .build();
        contextProvider.getDynamicContext().addParameter(dynamicParam, contextInfo,
                skipOperationResolver
                        .setContextInfo(contextInfo)
                        .isSkip(dynamicParam, execEachSegm));
    }

    private OperationInfo getExecOnceOperation(ExecOnceType execOnce) {
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(currentSegmentUuid)
                .build();
        return new OperationInfo(execOnce.getValue(), execOnce.getName(), contextInfo,
                skipOperationResolver
                        .setContextInfo(contextInfo)
                        .isSkip(execOnce, execEachSegm));
    }


    private List<OperationInfo> getSubPipeOperations(SubPipeType subPipe) {
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(currentSegmentUuid)
                .build();
        return subPipe.getExecOnce().stream()
                .map(execOnce -> new OperationInfo(execOnce.getValue(), execOnce.getName(), contextInfo,
                        skipOperationResolver
                                .setContextInfo(contextInfo)
                                .isSkip(execOnce, execEachSegm)))
                .collect(Collectors.toList());
    }

    private List<OperationInfo> getExecSequenceOnceOperations(ExecEachSequenceType execSequence) {
        List<OperationInfo> result = new ArrayList<>();

        // 1. get sequence type
        SequenceType seqType = execSequence.getType();

        // 2. process operation for each sequence within segment
        for (SequenceUUID seqUuid : contextProvider.getSequenceContext().getUuids(seqType)) {
            // process operations for each resource within segment and sequence
            ResourceKey resKey = ResourceKey.create(currentSegmentUuid, seqUuid, seqType);
            for (ResourceUUID resourceUuid : contextProvider.getResourceContext().getUuids(resKey)) {
                // context info
                ContextInfo contextInfo = new ContextInfoBuilder()
                        .setSequenceUuid(seqUuid)
                        .setSequenceType(seqType)
                        .setSegmentUuid(currentSegmentUuid)
                        .setResourceUuid(resourceUuid)
                        .build();

                // executable: operation info
                if (execSequence.getExecOnce() != null) {
                    OperationInfo operationInfo = new OperationInfo(execSequence.getExecOnce().getValue(),
                            execSequence.getName(), contextInfo,
                            skipOperationResolver
                                    .setContextInfo(contextInfo)
                                    .isSkip(execSequence.getExecOnce(), execSequence, execEachSegm));
                    result.add(operationInfo);
                }

                // dynamic parameter
                if (execSequence.getDynamicParameter() != null) {
                    for (DynamicParameterConcatType dynamicParam : execSequence.getDynamicParameter()) {
                        contextProvider.getDynamicContext().addParameter(dynamicParam, contextInfo,
                                skipOperationResolver
                                        .setContextInfo(contextInfo)
                                        .isSkip(dynamicParam, execSequence, execEachSegm));
                    }
                }
            }
        }

        return result;
    }

    private List<List<OperationInfo>> getExecSequencePipeOperations(ExecEachSequenceType execSequence) {
        List<List<OperationInfo>> result = new ArrayList<>();

        // 1. get sequence type
        SequenceType seqType = execSequence.getType();

        // 2. process operation for each sequence within segment
        for (SequenceUUID seqUuid : contextProvider.getSequenceContext().getUuids(seqType)) {
            // process operations for each resource within segment and sequence
            ResourceKey resKey = ResourceKey.create(currentSegmentUuid, seqUuid, seqType);
            for (ResourceUUID resourceUuid : contextProvider.getResourceContext().getUuids(resKey)) {
                // context info
                ContextInfo contextInfo = new ContextInfoBuilder()
                        .setSequenceUuid(seqUuid)
                        .setSequenceType(seqType)
                        .setSegmentUuid(currentSegmentUuid)
                        .setResourceUuid(resourceUuid)
                        .build();

                // executable: operation info
                if (execSequence.getPipe() != null) {
                    List<OperationInfo> pipeOperations = new ArrayList<>();
                    for (ExecOnceType execOnceType : execSequence.getPipe().getExecOnce()) {
                        OperationInfo operationInfo = new OperationInfo(execOnceType.getValue(), execOnceType.getName(), contextInfo,
                                skipOperationResolver
                                        .setContextInfo(contextInfo)
                                        .isSkip(execOnceType, execSequence, execEachSegm));
                        pipeOperations.add(operationInfo);
                    }
                    result.add(pipeOperations);
                }

                // dynamic parameter
                if (execSequence.getDynamicParameter() != null) {
                    for (DynamicParameterConcatType dynamicParam : execSequence.getDynamicParameter()) {
                        contextProvider.getDynamicContext().addParameter(dynamicParam, contextInfo,
                                skipOperationResolver
                                        .setContextInfo(contextInfo)
                                        .isSkip(dynamicParam, execSequence, execEachSegm));
                    }
                }
            }
        }

        return result;
    }

}
