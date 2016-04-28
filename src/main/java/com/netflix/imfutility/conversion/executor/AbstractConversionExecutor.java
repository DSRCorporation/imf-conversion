package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.Constants;
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

    private static int count = 1;

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

    protected Process startProcess(List<String> resolvedParams, String operationName, Class<?> operationClass) throws IOException {
        if (resolvedParams.isEmpty()) {
            throw new RuntimeException(String.format("No parameters for process '%s'", operationName));
        }

        ProcessBuilder pb = new ProcessBuilder(resolvedParams);
        pb.directory(new File(parameterResolver.getContextProvider().getWorkingDir()));

        File logFile = createLogFile(operationName, operationClass.getSimpleName(), resolvedParams.get(0));
        pb.redirectError(ProcessBuilder.Redirect.to(logFile));

        return pb.start();
    }

    private File createLogFile(String operationName, String operationType, String programPath) throws IOException {
        File logsDir = new File(parameterResolver.getContextProvider().getWorkingDir(), Constants.LOGS_DIR);
        String programName = new File(programPath.replaceAll("\"", "")).getName();
        String logFileName = String.format(Constants.LOG_TEMPLATE, count++, operationName, operationType, programName);
        File logFile = new File(logsDir, logFileName);
        logFile.createNewFile();
        return logFile;
    }

    private String[] splitParameters(String convertionOperation) {
        convertionOperation = convertionOperation.replaceFirst("\\s+|\\n+|\\r+", "");
        return convertionOperation.split("\\s+");
    }

}
