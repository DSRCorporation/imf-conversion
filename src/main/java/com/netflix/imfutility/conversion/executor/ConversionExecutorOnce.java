package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.conversion.executor.strategy.ExecuteOnceStrategy;
import com.netflix.imfutility.conversion.executor.strategy.OperationInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.xsd.conversion.ExecOnceType;

import java.io.IOException;

/**
 * Executor of {@link ExecOnceType} conversion operation.
 * <ul>
 * <li>Simply starts the external process and waits until it's finished synchronously.</li>
 * </ul>
 */
public class ConversionExecutorOnce implements IConversionExecutor {

    private final TemplateParameterContextProvider contextProvider;
    private final ExecOnceType operation;

    public ConversionExecutorOnce(TemplateParameterContextProvider contextProvider, ExecOnceType operation) {
        this.contextProvider = contextProvider;
        this.operation = operation;
    }

    @Override
    public void execute() throws IOException {
        OperationInfo operationInfo = new OperationInfo(operation.getValue(), operation.getName(), operation.getClass(),
                ContextInfo.EMPTY);
        new ExecuteOnceStrategy(contextProvider).execute(operationInfo);
    }


}
