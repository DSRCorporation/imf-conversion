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
