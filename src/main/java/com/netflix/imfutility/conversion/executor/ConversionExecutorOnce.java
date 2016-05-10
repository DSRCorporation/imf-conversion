package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.conversion.templateParameter.TemplateParameterResolver;
import com.netflix.imfutility.xsd.conversion.ExecOnceType;

import java.io.IOException;
import java.util.List;

/**
 * Executor of {@link ExecOnceType} conversion operation.
 * <ul>
 * <li>Simply starts the external process and waits until it's finished synchronously.</li>
 * </ul>
 */
public class ConversionExecutorOnce extends AbstractConversionExecutor {

    public ConversionExecutorOnce(TemplateParameterResolver parameterResolver) {
        super(parameterResolver);
    }

    public void execute(ExecOnceType operation) throws IOException {
        List<String> resolvedParams = resolveParameters(operation.getValue());
        ExternalProcess process = startProcess(resolvedParams, operation.getName(), operation.getClass());
        process.finishWaitFor();
    }


}
