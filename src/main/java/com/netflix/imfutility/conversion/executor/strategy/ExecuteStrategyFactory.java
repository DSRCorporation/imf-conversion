package com.netflix.imfutility.conversion.executor.strategy;

import com.netflix.imfutility.conversion.executor.ProcessStarter;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;

/**
 * A factory to create an appropriate execute strategy.
 */
public class ExecuteStrategyFactory {

    public ExecuteOnceStrategy createExecuteOnceStrategy(TemplateParameterContextProvider contextProvider) {
        return new ExecuteOnceStrategy(contextProvider, getProcessStarter());
    }

    public ExecutePipeStrategy createExecutePipeStrategy(TemplateParameterContextProvider contextProvider) {
        return new ExecutePipeStrategy(contextProvider, getProcessStarter());
    }

    protected ProcessStarter getProcessStarter() {
        return new ProcessStarter();
    }

}
