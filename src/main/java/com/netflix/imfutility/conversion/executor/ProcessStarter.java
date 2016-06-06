package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * An entity responsible for creation and start of a Process.
 * It also creates a log file and redirects process's stderr to this file.
 */
public class ProcessStarter {

    private final Logger logger = LoggerFactory.getLogger(ProcessStarter.class);

    public Process startProcess(ExternalProcess.ExternalProcessInfo processInfo, List<String> execAndParams, String workingDir, File output) throws IOException {
        logger.info("Starting {}", processInfo.toString());
        logger.info("\t{}", processInfo.getProcessString());

        ProcessBuilder pb = new ProcessBuilder(execAndParams);
        pb.directory(new File(workingDir));

        File logFile = createLogFile(processInfo, workingDir);
        if (logFile != null) {
            logger.info("\tRedirecting stderr to {}", logFile.getAbsolutePath());
            pb.redirectError(ProcessBuilder.Redirect.to(logFile));
        }
        if (output != null) {
            pb.redirectOutput(ProcessBuilder.Redirect.to(output));
        }

        return pb.start();
    }

    private File createLogFile(ExternalProcess.ExternalProcessInfo processInfo, String workingDir) {
        File logsDir = new File(workingDir, Constants.LOGS_DIR);
        String logFileName = String.format(
                Constants.LOG_TEMPLATE,
                processInfo.getProcessNum(), processInfo.getOperationName(), processInfo.getOperationType(), processInfo.getProgramName());
        File logFile = new File(logsDir, logFileName);

        String errorDesc = String.format("Couldn't create log file '%s' for %s", logFile.getAbsolutePath(), processInfo.toString());
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
