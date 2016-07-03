package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.conversion.ExecOnceType;
import com.netflix.imfutility.conversion.PipeType;
import com.netflix.imfutility.conversion.SubPipeType;
import com.netflix.imfutility.conversion.executor.strategy.ExecuteStrategyFactory;
import com.netflix.imfutility.conversion.executor.strategy.OperationInfo;
import com.netflix.imfutility.conversion.executor.strategy.PipeOperationInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;

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
