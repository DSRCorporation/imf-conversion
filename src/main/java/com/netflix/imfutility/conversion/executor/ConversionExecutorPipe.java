package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.conversion.executor.strategy.ExecuteStrategyFactory;
import com.netflix.imfutility.conversion.executor.strategy.OperationInfo;
import com.netflix.imfutility.conversion.executor.strategy.PipeOperationInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.xsd.conversion.ExecOnceType;
import com.netflix.imfutility.xsd.conversion.PipeType;

import java.io.IOException;

/**
 * Executor of {@link PipeType} conversion operation.
 * Execute all operations in a pipeline.
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
            for (ExecOnceType cycleOperation : pipe.getCycle().getExecOnce()) {
                pipeInfo.getCycleOperations().add(
                        getExecOnceOperation(cycleOperation));
            }
        }

        // 2. execute in a pipe
        executeStrategyFactory.createExecutePipeStrategy(contextProvider).execute(pipeInfo);
    }


    private OperationInfo getExecOnceOperation(ExecOnceType execOnce) {
        return new OperationInfo(execOnce.getValue(), execOnce.getName(), execOnce.getClass(),
                ContextInfo.EMPTY);
    }

}
