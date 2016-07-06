/**
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

import com.netflix.imfutility.conversion.executor.strategy.ExecuteStrategyFactory;
import com.netflix.imfutility.conversion.executor.strategy.OperationInfo;
import com.netflix.imfutility.conversion.executor.strategy.PipeOperationInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.generated.conversion.ExecOnceType;
import com.netflix.imfutility.generated.conversion.PipeType;
import com.netflix.imfutility.generated.conversion.SubPipeType;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Executor of {@link PipeType} conversion operation.
 * Execute all operations in a pipeline (see {@link com.netflix.imfutility.conversion.executor.strategy.ExecutePipeStrategy}).
 */
public class ConversionExecutorPipe extends AbstractConversionExecutor {

    private final PipeType pipe;

    public ConversionExecutorPipe(TemplateParameterContextProvider contextProvider, ExecuteStrategyFactory strategyProvider, PipeType pipe) {
        super(contextProvider, strategyProvider);
        this.pipe = pipe;
    }

    @Override
    public void execute() throws IOException {
        // 1. prepare operation to be executed in a pipe
        PipeOperationInfo pipeInfo = new PipeOperationInfo();

        for (ExecOnceType tailOperation : pipe.getExecOnce()) {
            pipeInfo.getTailOperations().add(
                    getExecOnceOperation(tailOperation));
        }
        if (pipe.getCycle() != null) {
            for (Object cycleOperation : pipe.getCycle().getPipeOrExecOnce()) {
                if (cycleOperation instanceof ExecOnceType) {
                    pipeInfo.addCycleOperation(getExecOnceOperation((ExecOnceType) cycleOperation));
                } else if (cycleOperation instanceof SubPipeType) {
                    pipeInfo.addCycleOperation(getSubPipeOperations((SubPipeType) cycleOperation));
                }
            }
        }

        // 2. execute in a pipe
        executeStrategyFactory.createExecutePipeStrategy(contextProvider).execute(pipeInfo);
    }


    private OperationInfo getExecOnceOperation(ExecOnceType execOnce) {
        return new OperationInfo(execOnce.getValue(), execOnce.getName(), ContextInfo.EMPTY);
    }

    private List<OperationInfo> getSubPipeOperations(SubPipeType subPipe) {
        return subPipe.getExecOnce().stream()
                .map(execOnce -> new OperationInfo(execOnce.getValue(), execOnce.getName(), ContextInfo.EMPTY))
                .collect(Collectors.toList());
    }

}
