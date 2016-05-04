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

    private final Logger logger = LoggerFactory.getLogger(ConversionExecutorOnce.class);

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

    protected ExternalProcess startProcess(List<String> resolvedParams, String operationName, Class<?> operationClass) throws IOException {
        if (resolvedParams.isEmpty()) {
            throw new RuntimeException(String.format("No parameters for process '%s'", operationName));
        }

        int processNum = count++;
        String operationType = operationClass.getSimpleName();
        String programPath = resolvedParams.get(0);
        String programName = new File(programPath.replaceAll("\"", "")).getName();
        ExternalProcess.ExternalProcessInfo processInfo = new ExternalProcess.ExternalProcessInfo(
                processNum, operationName, operationType, programName, resolvedParams);

        logger.info("Starting {}", processInfo.toString());
        logger.info("\t{}", processInfo.getProcessString());

        ProcessBuilder pb = new ProcessBuilder(resolvedParams);
        pb.directory(new File(parameterResolver.getContextProvider().getWorkingDir()));

        File logFile = createLogFile(processInfo);
        if (logFile != null) {
            logger.info("\tRedirecting stderr to {}", logFile.getAbsolutePath());
            pb.redirectError(ProcessBuilder.Redirect.to(logFile));
        }

        Process process = pb.start();
        return new ExternalProcess(process, processInfo);
    }

    private File createLogFile(ExternalProcess.ExternalProcessInfo processInfo) {
        File logsDir = new File(parameterResolver.getContextProvider().getWorkingDir(), Constants.LOGS_DIR);
        String logFileName = String.format(
                Constants.LOG_TEMPLATE,
                processInfo.getProcessNum(), processInfo.getOperationName(), processInfo.getOperationType(), processInfo.getProgramName());
        File logFile = new File(logsDir, logFileName);

        String errorDesc = String.format("Couldn't create log file for %s", toString());
        try {
            boolean created = logFile.createNewFile();
            if (!created) {
                logger.warn(errorDesc);
                return null;
            }
        } catch (IOException e) {
            logger.warn(errorDesc, e);
            return null;
        }
        return logFile;
    }

    private String[] splitParameters(String convertionOperation) {
        convertionOperation = convertionOperation.replaceFirst("\\s+|\\n+|\\r+", "");
        return convertionOperation.split("\\s+");
    }

}
