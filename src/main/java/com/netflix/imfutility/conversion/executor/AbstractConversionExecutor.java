package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.Constants;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterResolver;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Base Conversion Operation Executor.
 * <ul>
 * <li>Resolves template parameters using Template Parameter Context</li>
 * <li>Creates a new external process</li>
 * </ul>
 */
public abstract class AbstractConversionExecutor {

    private final Logger logger = LoggerFactory.getLogger(ConversionExecutorOnce.class);

    protected final ConversionOperationParser conversionOperationParser;
    protected final TemplateParameterResolver parameterResolver;

    private static int count = 1;

    public AbstractConversionExecutor(TemplateParameterContextProvider contextProvider) {
        this.parameterResolver = new TemplateParameterResolver(contextProvider);
        this.conversionOperationParser = new ConversionOperationParser(parameterResolver);
    }

    protected ExternalProcess startProcess(List<String> execAndParams, String operationName, Class<?> operationClass) throws IOException {
        if (execAndParams.isEmpty()) {
            throw new RuntimeException(String.format("No parameters for process '%s'", operationName));
        }

        int processNum = count++;
        String operationType = operationClass.getSimpleName();
        String programPath = execAndParams.get(0);
        String programName = new File(programPath.replaceAll("\"", "")).getName();
        ExternalProcess.ExternalProcessInfo processInfo = new ExternalProcess.ExternalProcessInfo(
                processNum, operationName, operationType, programName, execAndParams);

        logger.info("Starting {}", processInfo.toString());
        logger.info("\t{}", processInfo.getProcessString());

        ProcessBuilder pb = new ProcessBuilder(execAndParams);
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

}
