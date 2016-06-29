package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.conversion.executor.strategy.ExecuteStrategyFactory;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;

/**
 * Base conversion executor. A specific conversion executor is created for each conversion operation type.
 */
public abstract class AbstractConversionExecutor implements IConversionExecutor {

    protected final TemplateParameterContextProvider contextProvider;
    protected final ExecuteStrategyFactory executeStrategyFactory;

    public AbstractConversionExecutor(TemplateParameterContextProvider contextProvider, ExecuteStrategyFactory executeStrategyFactory) {
        this.contextProvider = contextProvider;
        this.executeStrategyFactory = executeStrategyFactory;
    }

}
