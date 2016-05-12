package com.netflix.imfutility.conversion.executor.strategy;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;

/**
 * Created by Alexander on 5/12/2016.
 */
public final class OperationInfo {

    private final String operation;
    private final String operaitonName;
    private final Class<?> operationClass;
    private ContextInfo contextInfo;

    public OperationInfo(String operation, String operaitonName, Class<?> operationClass, ContextInfo contextInfo) {
        this.operation = operation;
        this.operaitonName = operaitonName;
        this.operationClass = operationClass;
        this.contextInfo = contextInfo;
    }

    public String getOperation() {
        return operation;
    }

    public String getOperaitonName() {
        return operaitonName;
    }

    public Class<?> getOperationClass() {
        return operationClass;
    }

    public ContextInfo getContextInfo() {
        return contextInfo;
    }
}
