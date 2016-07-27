/*
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
package com.netflix.imfutility.util;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterResolver;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;
import com.netflix.imfutility.generated.conversion.ExecComplexType;
import com.netflix.imfutility.generated.conversion.ExecSimpleType;

import java.util.Arrays;

/**
 * Defines if and unless parameters of executions.
 * (see {@link ExecSimpleType} {@link ExecComplexType}).
 */
public class SkipOperationResolver {
    private final TemplateParameterResolver parameterResolver;
    private ContextInfo contextInfo;

    public SkipOperationResolver(TemplateParameterResolver parameterResolver) {
        this.parameterResolver = parameterResolver;
    }

    public ContextInfo getContextInfo() {
        return contextInfo;
    }

    public SkipOperationResolver setContextInfo(ContextInfo contextInfo) {
        this.contextInfo = contextInfo;
        return this;
    }

    public boolean isSkip(ExecSimpleType execType, ExecComplexType... parentExecTypes) {
        if (parentExecTypes == null || parentExecTypes.length == 0) {
            return isSkip(execType);
        }

        return Arrays.asList(parentExecTypes).stream().anyMatch((execComplexType) -> isSkip(execComplexType)) || isSkip(execType);
    }

    public boolean isSkip(ExecComplexType execType, ExecComplexType... parentExecTypes) {
        if (parentExecTypes == null || parentExecTypes.length == 0) {
            return isSkip(execType);
        }

        return Arrays.asList(parentExecTypes).stream().anyMatch((execComplexType) -> isSkip(execComplexType)) || isSkip(execType);
    }

    public boolean isSkip(ExecSimpleType execType) {
        return isSkip(execType.getIf(), execType.getUnless());
    }

    public boolean isSkip(ExecComplexType execType) {
        return isSkip(execType.getIf(), execType.getUnless());
    }

    private boolean isSkip(String ifValue, String unlessValue) throws TemplateParameterNotFoundException {
        return !Boolean.parseBoolean(parameterResolver.resolveTemplateParameter(ifValue, contextInfo))
                || Boolean.parseBoolean(parameterResolver.resolveTemplateParameter(unlessValue, contextInfo));
    }

}
