package com.netflix.imfutility.conversion.executor.strategy;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;

import java.io.File;

/**
 * Information about the conversion operation to create a Process for.
 */
public final class OperationInfo {

    private final String operation;
    private final String operationName;
    private final ContextInfo contextInfo;
    private final File output;

    public OperationInfo(String operation, String operationName, ContextInfo contextInfo) {
        this(operation, operationName, contextInfo, null);
    }

    public OperationInfo(String operation, String operationName, ContextInfo contextInfo, File output) {
        this.operation = operation;
        this.operationName = operationName;
        this.contextInfo = contextInfo;
        this.output = output;
    }

    public String getOperation() {
        return operation;
    }

    public String getOperationName() {
        return operationName;
    }

    public ContextInfo getContextInfo() {
        return contextInfo;
    }

    public File getOutput() {
        return output;
    }
}
