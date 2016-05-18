package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.conversion.executor.strategy.ExecuteStrategyFactory;
import com.netflix.imfutility.conversion.executor.strategy.OperationInfo;
import com.netflix.imfutility.conversion.executor.strategy.PipeOperationInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.xsd.conversion.ExecOnceType;
import com.netflix.imfutility.xsd.conversion.PipeType;
import com.netflix.imfutility.xsd.conversion.SubPipeType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        return new OperationInfo(execOnce.getValue(), execOnce.getName(), execOnce.getClass(),
                ContextInfo.EMPTY);
    }

    private List<OperationInfo> getSubPipeOperations(SubPipeType subPipe) {
        List<OperationInfo> result = new ArrayList<>();
        for (ExecOnceType execOnce : subPipe.getExecOnce()) {
            result.add(new OperationInfo(execOnce.getValue(), execOnce.getName(), execOnce.getClass(),
                    ContextInfo.EMPTY));
        }
        return result;
    }

}
