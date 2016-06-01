package com.netflix.imfutility.conversion.executor;

/**
 * A general exception thrown during execution of conversion operations from conversion.xml.
 */
public class ExecutionException extends RuntimeException {

    public ExecutionException(String message) {
        super(message);
    }

    public ExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

}
