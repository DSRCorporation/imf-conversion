package com.netflix.imfutility.conversion.executor.strategy;

import com.netflix.imfutility.conversion.executor.ExternalProcess;
import com.netflix.imfutility.conversion.executor.ProcessStarter;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;

import java.io.IOException;

/**
 * Simply starts the conversion operation and waits until it's finished.
 */
public class ExecuteOnceStrategy extends AbstractExecuteStrategy {

    public ExecuteOnceStrategy(TemplateParameterContextProvider contextProvider, ProcessStarter processStarter) {
        super(contextProvider, processStarter);
    }

    public void execute(OperationInfo operationInfo) throws IOException {
        ExternalProcess process = startProcess(operationInfo);
        process.finishWaitFor();
    }

}
