package com.netflix.imfutility.conversion.executor;

/**
 * An exception thrown when an external process execution fails (either due to IOException, or exit code is non-zero).
 */
public class ProcessFailedException extends RuntimeException {

    public ProcessFailedException(ExternalProcess process, int errorCode) {
        super(String.format("Execution of '%s' process failed: exit code '%d'. See log folder for details.", process.toString(), errorCode));
    }

    public ProcessFailedException(String message, Throwable e) {
        super(message, e);
    }
}
