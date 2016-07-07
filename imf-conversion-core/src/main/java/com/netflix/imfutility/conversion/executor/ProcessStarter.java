/**
 * Copyright (C) 2016 Netflix, Inc.
 *
 *     This file is part of IMF Conversion Utility.
 *
 *     IMF Conversion Utility is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     IMF Conversion Utility is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with IMF Conversion Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.CoreConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * An entity responsible for creation and start of a Process.
 * It creates a log file and redirects process's stderr to this file.
 * It also redirected process's stdout to the specified destination (either a file, a log file, IMF utility stdout, or no redirect).
 */
public class ProcessStarter {

    private final Logger logger = LoggerFactory.getLogger(ProcessStarter.class);

    public Process startProcess(ExternalProcess.ExternalProcessInfo processInfo, List<String> execAndParams) throws IOException {
        logger.info("Starting {}", processInfo.toString());
        logger.info("\t{}", processInfo.getProcessString());

        // 1. create process builder
        ProcessBuilder pb = new ProcessBuilder(execAndParams);

        // 2. set working dir
        pb.directory(processInfo.getWorkingDir());

        // both stderr and stdout must be redirected to either a file or INHERIT unless it's a pipe!
        // Otherwise a Process may hang if it writes to stderr/stdout

        // 3. redirect stderr
        File logFile = getLogFile(processInfo);
        logger.info("\tRedirecting stderr to {}", logFile.getAbsolutePath());
        pb.redirectError(ProcessBuilder.Redirect.to(logFile));

        // 4. redirect stdout
        redirectStdout(pb, processInfo, logFile);

        // 5. start process
        return pb.start();
    }

    private void redirectStdout(ProcessBuilder pb, ExternalProcess.ExternalProcessInfo processInfo, File logFile) {
        switch (processInfo.getOutputRedirect()) {
            case ERR_LOG:
                logger.info("\tRedirecting stdout to {}", logFile.getAbsolutePath());
                pb.redirectOutput(ProcessBuilder.Redirect.to(logFile));
                break;
            case FILE:
                if (processInfo.getOutputRedirectFile() == null) {
                    throw new ConversionException(String.format(
                            "stdout must be redirected to a file, but the file is not specified (process: %s)",
                            processInfo.toString()));
                }
                logger.info("\tRedirecting stdout to {}", processInfo.getOutputRedirectFile().getAbsolutePath());
                pb.redirectOutput(ProcessBuilder.Redirect.to(processInfo.getOutputRedirectFile()));
                break;
            case PIPE:
                logger.info("\tStdout is not redirected, as it participates in a pipeline");
                break;
            case INHERIT:
            default:
                logger.info("\tRedirecting stdout to IMF Utility stdout");
                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                break;
        }
    }


    private File getLogFile(ExternalProcess.ExternalProcessInfo processInfo) {
        File logsDir = new File(processInfo.getWorkingDir(), CoreConstants.LOGS_DIR);
        String logFileName = String.format(
                CoreConstants.LOG_TEMPLATE,
                processInfo.getProcessNum(), processInfo.getOperationName(), processInfo.getOperationType(), processInfo.getProgramName());
        return new File(logsDir, logFileName);
    }

}
