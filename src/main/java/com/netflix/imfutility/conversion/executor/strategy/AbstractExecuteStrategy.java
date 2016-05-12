package com.netflix.imfutility.conversion.executor.strategy;

import com.netflix.imfutility.Constants;
import com.netflix.imfutility.conversion.executor.ConversionOperationParser;
import com.netflix.imfutility.conversion.executor.ExternalProcess;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterResolver;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Alexander on 5/12/2016.
 */
public class AbstractExecuteStrategy {

    private final Logger logger = LoggerFactory.getLogger(AbstractExecuteStrategy.class);

    protected final ConversionOperationParser conversionOperationParser;
    protected final TemplateParameterResolver parameterResolver;

    private static int count = 1;

    public AbstractExecuteStrategy(TemplateParameterContextProvider contextProvider) {
        this.parameterResolver = new TemplateParameterResolver(contextProvider);
        this.conversionOperationParser = new ConversionOperationParser(parameterResolver);
    }

    protected ExternalProcess startProcess(OperationInfo operationInfo) throws IOException {
        List<String> execAndParams = conversionOperationParser.parseOperation(operationInfo.getOperation(), operationInfo.getContextInfo());
        if (execAndParams.isEmpty()) {
            throw new RuntimeException(String.format("No parameters for process '%s'", operationInfo.getOperaitonName()));
        }

        int processNum = count++;
        String operationType = operationInfo.getOperationClass().getSimpleName();
        String programPath = execAndParams.get(0);
        String programName = new File(programPath.replaceAll("\"", "")).getName();
        ExternalProcess.ExternalProcessInfo processInfo = new ExternalProcess.ExternalProcessInfo(
                processNum, operationInfo.getOperaitonName(), operationType, programName, execAndParams);

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
