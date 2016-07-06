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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A wrapper for a started {@link Process} containing information about the process:
 * process sequence number;  executable name; conversion operation name, conversion operation type, etc.
 */
public final class ExternalProcess {

    private final Logger logger = LoggerFactory.getLogger(ExternalProcess.class);

    private final Process process;
    private final ExternalProcessInfo processInfo;

    private volatile boolean closed = false;

    public ExternalProcess(Process process, ExternalProcessInfo processInfo) {
        this.process = process;
        this.processInfo = processInfo;
        logStarted();
    }


    public void finishWaitFor() {
        if (closed) {
            return;
        }
        int result = 0;
        try {
            result = process.waitFor();
            closed = true;
        } catch (InterruptedException e) {
            finishedFailureWithException(e);
        }
        if (result != 0) {
            finishedFailureWithExitCode(result);
        } else {
            logFinishedSuccess();
        }
    }

    public void finishClose() {
        if (closed) {
            return;
        }
        try {
            process.getOutputStream().close();
            finishWaitFor();
        } catch (IOException e) {
            finishedFailureWithException(e);
        }
    }

    private void logStarted() {
        logger.info("Started {}: OK\n", toString());
    }

    private void logFinishedSuccess() {
        logger.info("Finished {}: OK\n", toString());
    }

    private void finishedFailureWithException(Exception e) {
        logger.info("Finished {}: FAILURE\n", toString());
        String errorDesc = String.format("Exception while finishing %s. External Process may be not finished correctly.", toString());
        throw new ProcessFailedException(errorDesc, e);
    }

    private void finishedFailureWithExitCode(int exitCode) {
        logger.info("Finished {}: FAILURE\n", toString());
        throw new ProcessFailedException(this, exitCode);
    }


    public Process getProcess() {
        return process;
    }

    public ExternalProcessInfo getProcessInfo() {
        return processInfo;
    }

    @Override
    public String toString() {
        return processInfo.toString();
    }

    public static class ExternalProcessInfo {

        private final int processNum;
        private final String operationName;
        private final String operationType;
        private final String programName;
        private final String processString;
        private final File workingDir;
        private final OutputRedirect outputRedirect;
        private final File outputRedirectFile;

        public ExternalProcessInfo(int processNum, String operationName, String operationType, String programName,
                                   List<String> resolvedParams, File workingDir, OutputRedirect outputRedirect, File outputRedirectFile) {
            this.processNum = processNum;
            this.operationName = operationName;
            this.operationType = operationType;
            this.programName = programName;
            this.processString = getProcessString(resolvedParams);
            this.workingDir = workingDir;
            this.outputRedirect = outputRedirect;
            this.outputRedirectFile = outputRedirectFile;
        }

        public int getProcessNum() {
            return processNum;
        }

        public String getOperationName() {
            return operationName;
        }

        public String getOperationType() {
            return operationType;
        }

        public String getProgramName() {
            return programName;
        }

        public String getProcessString() {
            return processString;
        }

        public File getWorkingDir() {
            return workingDir;
        }

        public OutputRedirect getOutputRedirect() {
            return outputRedirect;
        }

        public File getOutputRedirectFile() {
            return outputRedirectFile;
        }

        private String getProcessString(List<String> resolvedParams) {
            return resolvedParams.stream()
                    .collect(Collectors.joining(" "));
        }

        @Override
        public String toString() {
            return String.format("External Process %d: %s, %s, %s", processNum, operationName, operationType, programName);
        }

    }

}
