package com.netflix.imfutility.conversion.executor.strategy;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;

/**
 * Information about the conversion operation to create a Process for.
 */
public final class OperationInfo {

    private final String operation;
    private final String operationName;
    private final Class<?> operationClass;
    private final ContextInfo contextInfo;

    public OperationInfo(String operation, String operationName, Class<?> operationClass, ContextInfo contextInfo) {
        this.operation = operation;
        this.operationName = operationName;
        this.operationClass = operationClass;
        this.contextInfo = contextInfo;
    }

    public String getOperation() {
        return operation;
    }

    public String getOperationName() {
        return operationName;
    }

    public Class<?> getOperationClass() {
        return operationClass;
    }

    public ContextInfo getContextInfo() {
        return contextInfo;
    }
}
