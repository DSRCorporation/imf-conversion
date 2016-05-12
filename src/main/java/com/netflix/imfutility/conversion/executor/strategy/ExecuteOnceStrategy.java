package com.netflix.imfutility.conversion.executor.strategy;

import com.netflix.imfutility.conversion.executor.ExternalProcess;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;

import java.io.IOException;

/**
 * Created by Alexander on 5/12/2016.
 */
public class ExecuteOnceStrategy extends AbstractExecuteStrategy {

    public ExecuteOnceStrategy(TemplateParameterContextProvider contextProvider) {
        super(contextProvider);
    }

    public void execute(OperationInfo operationInfo) throws IOException {
        ExternalProcess process = startProcess(operationInfo);
        process.finishWaitFor();
    }

}
