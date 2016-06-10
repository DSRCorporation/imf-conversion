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

        // 1. create process builder
        ProcessBuilder pb = new ProcessBuilder(execAndParams);

        // 2. set working dir
        pb.directory(new File(workingDir));

        // both stderr and stdout must be redirected to either a file or INHERIT!
        // Otherwise a Process may hang if it writes to stderr/stdout

        // 3. redirect stderr
        File logFile = getLogFile(processInfo, workingDir);
        if (logFile != null) {
            logger.info("\tRedirecting stderr to {}", logFile.getAbsolutePath());
            pb.redirectError(ProcessBuilder.Redirect.to(logFile));
        } else {
            logger.info("\tRedirecting stderr to IMF Utility stderr");
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        }

        // 4. redirect stdout
        if (output != null) {
            logger.info("\tRedirecting stdout to {}", output.getAbsolutePath());
            pb.redirectOutput(ProcessBuilder.Redirect.to(output));
        } else if (logFile != null) {
            logger.info("\tRedirecting stdout to {}", logFile.getAbsolutePath());
            pb.redirectOutput(ProcessBuilder.Redirect.to(logFile));
        } else {
            logger.info("\tRedirecting stdout to IMF Utility stdout");
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        }

        // 5. start process
        return pb.start();
    }

    private File getLogFile(ExternalProcess.ExternalProcessInfo processInfo, String workingDir) {
        File logsDir = new File(workingDir, Constants.LOGS_DIR);
        String logFileName = String.format(
                Constants.LOG_TEMPLATE,
                processInfo.getProcessNum(), processInfo.getOperationName(), processInfo.getOperationType(), processInfo.getProgramName());
        return new File(logsDir, logFileName);
    }

}
