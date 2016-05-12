package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.conversion.executor.strategy.ExecutePipeStrategy;
import com.netflix.imfutility.conversion.executor.strategy.OperationInfo;
import com.netflix.imfutility.conversion.executor.strategy.PipeOperationInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.xsd.conversion.ExecOnceType;
import com.netflix.imfutility.xsd.conversion.PipeType;
import com.netflix.imfutility.xsd.conversion.SequenceType;

import java.io.IOException;

/**
 * Executor of {@link } conversion operation.
 * <ul>
 * <li>Execute all operations in a pipeline</li>
 * <li>Supports {@link SequenceType}</li>
 * </ul>
 */
public class ConversionExecutorPipe implements IConversionExecutor {

    private final TemplateParameterContextProvider contextProvider;
    private final PipeType pipe;

    public ConversionExecutorPipe(TemplateParameterContextProvider contextProvider, PipeType pipe) {
        this.contextProvider = contextProvider;
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
        new ExecutePipeStrategy(contextProvider).execute(pipeInfo);
    }


    private OperationInfo getExecOnceOperation(ExecOnceType execOnce) {
        return new OperationInfo(execOnce.getValue(), execOnce.getName(), execOnce.getClass(),
                ContextInfo.EMPTY);
    }

}
