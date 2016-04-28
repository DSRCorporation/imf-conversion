package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterResolver;
import com.netflix.imfutility.xsd.conversion.ExecOnceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 4/26/2016.
 */
public class ConvertionExecutorOnce extends AbstractConvertionExecutor {

    final Logger logger = LoggerFactory.getLogger(ConvertionExecutorOnce.class);

    public ConvertionExecutorOnce(TemplateParameterResolver parameterResolver) {
        super(parameterResolver);
    }

    public void execute(ExecOnceType operation) throws IOException, InterruptedException {
        List<String> resolvedParams = resolveParameters(operation.getValue());
        startProcess(resolvedParams).waitFor();
    }


}
