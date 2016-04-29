package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.Constants;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterResolver;
import com.netflix.imfutility.xsd.conversion.SegmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Created by Alexander on 4/26/2016.
 */
public abstract class AbstractConversionExecutor {

    final Logger logger = LoggerFactory.getLogger(ConversionExecutorOnce.class);

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

        logger.info("Starting external process: {}", getProcessString(resolvedParams));

        ProcessBuilder pb = new ProcessBuilder(resolvedParams);
        pb.directory(new File(parameterResolver.getContextProvider().getWorkingDir()));

        File logFile = createLogFile(operationName, operationClass.getSimpleName(), resolvedParams.get(0));
        if (logFile != null) {
            logger.info("\tRedirecting stderr to {}", logFile.getAbsolutePath());
            pb.redirectError(ProcessBuilder.Redirect.to(logFile));
        }

        Process process = pb.start();
        logger.info("\tStared external process");
        return process;
    }

    private String getProcessString(List<String> resolvedParams) {
        return resolvedParams.stream()
                .collect(Collectors.joining(" "));
    }

    private File createLogFile(String operationName, String operationType, String programPath) {
        File logsDir = new File(parameterResolver.getContextProvider().getWorkingDir(), Constants.LOGS_DIR);
        String programName = new File(programPath.replaceAll("\"", "")).getName();
        String logFileName = String.format(Constants.LOG_TEMPLATE, count++, operationName, operationType, programName);
        File logFile = new File(logsDir, logFileName);
        try {
            logFile.createNewFile();
        } catch (IOException e) {
            logger.warn("Couldn't create log file for external process {} for operation {}", programName, operationName, e);
            return null;
        }
        return logFile;
    }

    private String[] splitParameters(String convertionOperation) {
        convertionOperation = convertionOperation.replaceFirst("\\s+|\\n+|\\r+", "");
        return convertionOperation.split("\\s+");
    }

}
