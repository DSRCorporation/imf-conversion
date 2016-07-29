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
import com.netflix.imfutility.generated.conversion.ExecEachSegmentType;
import com.netflix.imfutility.generated.conversion.ExecEachSequenceSegmentType;
import com.netflix.imfutility.generated.conversion.ExecOnceType;
import com.netflix.imfutility.generated.conversion.PipeSequenceType;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.generated.conversion.SubPipeType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An executor for {@link ExecEachSequenceSegmentType} conversion operation.
 * <p>
 * It reads all sub-conversion operations for the input sequence conversion operation and executes them
 * either once or in a pipe using an appropriate execute strategy for each segment.
 * A sub-conversion operation may be executed either once, in a pipeline, or for each segment.
 * <p>
 * See {@link com.netflix.imfutility.conversion.executor.strategy.ExecuteOnceStrategy} and
 * {@link com.netflix.imfutility.conversion.executor.strategy.ExecutePipeStrategy}.
 * </p>
 */
public class ConversionExecutorSequence extends AbstractConversionExecutor {

    private final ExecEachSequenceSegmentType execEachSeq;
    private final SequenceType seqType;

    private SequenceUUID currentSeqUuid;

    public ConversionExecutorSequence(TemplateParameterContextProvider contextProvider, ExecuteStrategyFactory strategyProvider,
                                      ExecEachSequenceSegmentType execEachSeq) {
        super(contextProvider, strategyProvider);
        this.execEachSeq = execEachSeq;

        this.seqType = execEachSeq.getType();
    }

    @Override
    public void execute() throws IOException {
        for (SequenceUUID seqUuid : contextProvider.getSequenceContext().getUuids(seqType)) {
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
                    throw new ConversionException(String.format("Unknown Conversion Operation type: %s", operation.toString()));
                }
            }
        }
    }

    private void execOnce(ExecOnceType execOnce) throws IOException {
        executeStrategyFactory.createExecuteOnceStrategy(contextProvider).execute(getExecOnceOperation(execOnce));
    }

    private void execSegment(ExecEachSegmentType execSegment) throws IOException {
        if (execSegment.getExecOnce() != null) {
            for (OperationInfo segmentOperation : getExecSegmentOnceOperations(execSegment)) {
                executeStrategyFactory.createExecuteOnceStrategy(contextProvider).execute(segmentOperation);
            }
        } else if (execSegment.getPipe() != null) {
            for (List<OperationInfo> pipeOperations : getExecSegmentPipeOperations(execSegment)) {
                PipeOperationInfo pipeInfo = new PipeOperationInfo();
                pipeInfo.addTailOperations(pipeOperations);
                executeStrategyFactory.createExecutePipeStrategy(contextProvider).execute(pipeInfo);
            }
        }
    }

    private void execPipe(PipeSequenceType pipe) throws IOException {
        // 1. prepare operation to be executed in a pipe
        PipeOperationInfo pipeInfo = new PipeOperationInfo();

        for (ExecOnceType tailOperation : pipe.getExecOnce()) {
            pipeInfo.getTailOperations().add(getExecOnceOperation(tailOperation));
        }
        if (pipe.getCycle() != null) {
            for (Object cycleOperation : pipe.getCycle().getExecEachSegmentOrPipeOrExecOnce()) {
                if (cycleOperation instanceof ExecOnceType) {
                    pipeInfo.addCycleOperation(getExecOnceOperation((ExecOnceType) cycleOperation));
                } else if (cycleOperation instanceof SubPipeType) {
                    pipeInfo.addCycleOperation(getSubPipeOperations((SubPipeType) cycleOperation));
                } else if (cycleOperation instanceof ExecEachSegmentType) {
                    execEachSegmentInPipe((ExecEachSegmentType) cycleOperation, pipeInfo);
                }
            }
        }

        // 2. execute in a pipe
        executeStrategyFactory.createExecutePipeStrategy(contextProvider).execute(pipeInfo);
    }

    private void execEachSegmentInPipe(ExecEachSegmentType execSegment, PipeOperationInfo pipeInfo) {
        if (execSegment.getExecOnce() != null) {
            getExecSegmentOnceOperations(execSegment).forEach(pipeInfo::addCycleOperation);
        } else if (execSegment.getPipe() != null) {
            getExecSegmentPipeOperations(execSegment).forEach(pipeInfo::addCycleOperation);
        }
    }

    private void addDynamicParameter(DynamicParameterConcatType dynamicParam) {
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSequenceUuid(currentSeqUuid)
                .setSequenceType(seqType)
                .build();
        contextProvider.getDynamicContext().addParameter(dynamicParam, contextInfo,
                skipOperationResolver
                        .setContextInfo(contextInfo)
                        .isSkip(dynamicParam, execEachSeq));
    }

    private OperationInfo getExecOnceOperation(ExecOnceType execOnce) {
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSequenceUuid(currentSeqUuid)
                .setSequenceType(seqType)
                .build();
        return new OperationInfo(execOnce.getValue(), execOnce.getName(), contextInfo,
                skipOperationResolver
                        .setContextInfo(contextInfo)
                        .isSkip(execOnce, execEachSeq));
    }

    private List<OperationInfo> getSubPipeOperations(SubPipeType subPipe) {
        List<OperationInfo> result = new ArrayList<>();
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSequenceUuid(currentSeqUuid)
                .setSequenceType(seqType)
                .build();
        result.addAll(subPipe.getExecOnce().stream()
                .map(execOnce -> new OperationInfo(execOnce.getValue(), execOnce.getName(), contextInfo,
                        skipOperationResolver
                                .setContextInfo(contextInfo)
                                .isSkip(execOnce, execEachSeq)))
                .collect(Collectors.toList()));
        return result;
    }

    private List<OperationInfo> getExecSegmentOnceOperations(ExecEachSegmentType execSegment) {
        List<OperationInfo> result = new ArrayList<>();

        // process operations for each segment within sequence
        for (SegmentUUID segmUuid : contextProvider.getSegmentContext().getUuids()) {
            // process operations for each resource within segment and sequence
            ResourceKey resKey = ResourceKey.create(segmUuid, currentSeqUuid, seqType);
            for (ResourceUUID resourceUuid : contextProvider.getResourceContext().getUuids(resKey)) {
                // context info
                ContextInfo contextInfo = new ContextInfoBuilder()
                        .setSequenceUuid(currentSeqUuid)
                        .setSequenceType(seqType)
                        .setSegmentUuid(segmUuid)
                        .setResourceUuid(resourceUuid)
                        .build();

                // executable: operation info
                if (execSegment.getExecOnce() != null) {
                    OperationInfo operationInfo = new OperationInfo(execSegment.getExecOnce().getValue(),
                            execSegment.getName(), contextInfo,
                            skipOperationResolver
                                    .setContextInfo(contextInfo)
                                    .isSkip(execSegment.getExecOnce(), execSegment, execEachSeq));
                    result.add(operationInfo);
                }

                // dynamic parameter
                if (execSegment.getDynamicParameter() != null) {
                    for (DynamicParameterConcatType dynamicParam : execSegment.getDynamicParameter()) {
                        contextProvider.getDynamicContext().addParameter(dynamicParam, contextInfo,
                                skipOperationResolver
                                        .setContextInfo(contextInfo)
                                        .isSkip(dynamicParam, execSegment, execEachSeq));
                    }
                }
            }
        }

        return result;
    }

    private List<List<OperationInfo>> getExecSegmentPipeOperations(ExecEachSegmentType execSegment) {
        List<List<OperationInfo>> result = new ArrayList<>();

        // process operations for each segment within sequence
        for (SegmentUUID segmUuid : contextProvider.getSegmentContext().getUuids()) {
            // process operations for each resource within segment and sequence
            ResourceKey resKey = ResourceKey.create(segmUuid, currentSeqUuid, seqType);
            for (ResourceUUID resourceUuid : contextProvider.getResourceContext().getUuids(resKey)) {
                // context info
                ContextInfo contextInfo = new ContextInfoBuilder()
                        .setSequenceUuid(currentSeqUuid)
                        .setSequenceType(seqType)
                        .setSegmentUuid(segmUuid)
                        .setResourceUuid(resourceUuid)
                        .build();

                // executable: operation info
                if (execSegment.getPipe() != null) {
                    List<OperationInfo> pipeOperations = new ArrayList<>();
                    for (ExecOnceType execOnceType : execSegment.getPipe().getExecOnce()) {
                        OperationInfo operationInfo = new OperationInfo(execOnceType.getValue(), execOnceType.getName(), contextInfo,
                                skipOperationResolver
                                        .setContextInfo(contextInfo)
                                        .isSkip(execOnceType, execSegment, execEachSeq));
                        pipeOperations.add(operationInfo);
                    }
                    result.add(pipeOperations);
                }

                // dynamic parameter
                if (execSegment.getDynamicParameter() != null) {
                    for (DynamicParameterConcatType dynamicParam : execSegment.getDynamicParameter()) {
                        contextProvider.getDynamicContext().addParameter(dynamicParam, contextInfo,
                                skipOperationResolver
                                        .setContextInfo(contextInfo)
                                        .isSkip(dynamicParam, execSegment, execEachSeq));
                    }
                }
            }
        }

        return result;
    }

}
