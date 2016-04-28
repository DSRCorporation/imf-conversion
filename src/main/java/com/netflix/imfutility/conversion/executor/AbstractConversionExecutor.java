package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterResolver;
import com.netflix.imfutility.xsd.conversion.SegmentType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 4/26/2016.
 */
public abstract class AbstractConversionExecutor {

    protected final TemplateParameterResolver parameterResolver;

    public AbstractConversionExecutor(TemplateParameterResolver parameterResolver) {
        this.parameterResolver = parameterResolver;
    }

    protected List<String> resolveParameters(String conversionOperation) {
        String[] params = splitParameters(conversionOperation);
        List<String> execAndParams = new ArrayList<>();
        for (String param : params) {
            if (TemplateParameter.isTemplateParameter(param)) {
                param = parameterResolver.resolveTemplateParameter(param);
            }
            param = String.format("\"%s\"", param);
            execAndParams.add(param);
        }
        return execAndParams;
    }

    protected List<String> resolveSegmentParameters(String conversionOperation, int segment, SegmentType segmentType) {
        String[] params = splitParameters(conversionOperation);
        List<String> execAndParams = new ArrayList<>();
        for (String param : params) {
            if (TemplateParameter.isTemplateParameter(param)) {
                param = parameterResolver.resolveSegmentTemplateParameter(param, segment, segmentType);
            }
            param = String.format("\"%s\"", param);
            execAndParams.add(param);
        }
        return execAndParams;
    }

    protected Process startProcess(List<String> resolvedParams) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(resolvedParams);
        // pb.redirectError(ProcessBuilder.Redirect.to(log));
        pb.directory(new File(parameterResolver.getContextProvider().getWorkingDir()));
        return pb.start();
    }

    private String[] splitParameters(String convertionOperation) {
        convertionOperation = convertionOperation.replaceFirst("\\s+|\\n+|\\r+", "");
        return convertionOperation.split("\\s+");
    }

}
