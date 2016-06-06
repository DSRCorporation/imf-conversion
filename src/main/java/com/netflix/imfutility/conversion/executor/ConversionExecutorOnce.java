package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.conversion.executor.strategy.ExecuteStrategyFactory;
import com.netflix.imfutility.conversion.executor.strategy.OperationInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.xsd.conversion.ExecOnceType;

import java.io.IOException;

/**
 * An executor for {@link ExecOnceType} conversion operation.
 * It simply starts the conversion operation and waits until it's finished.
 */
public class ConversionExecutorOnce extends AbstractConversionExecutor {

    private final ExecOnceType operation;

    public ConversionExecutorOnce(TemplateParameterContextProvider contextProvider, ExecuteStrategyFactory strategyProvider, ExecOnceType operation) {
        super(contextProvider, strategyProvider);
        this.operation = operation;
    }

    @Override
    public void execute() throws IOException {
        OperationInfo operationInfo = new OperationInfo(operation.getValue(), operation.getName(), ContextInfo.EMPTY);
        executeStrategyFactory.createExecuteOnceStrategy(contextProvider).execute(operationInfo);
    }


}
