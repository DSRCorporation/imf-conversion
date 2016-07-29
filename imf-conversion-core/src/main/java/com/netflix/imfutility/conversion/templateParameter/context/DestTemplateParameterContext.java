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
package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;
import com.netflix.imfutility.generated.conversion.DestContextParamType;
import com.netflix.imfutility.xsd.conversion.DestContextTypeMap;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Destination Template Parameter Context.
 * <ul>
 * <li>It's used to replace destination video/audio parameters in conversion operations</li>
 * <li>May contain any key-value map</li>
 * <li>May be created dynamically in the code</li>
 * </ul>
 */
public class DestTemplateParameterContext implements ITemplateParameterContext {
    private DestContextTypeMap destContextMap;

    public DestTemplateParameterContext() {
    }

    public DestContextTypeMap getDestContextMap() {
        return destContextMap;
    }

    public void setDestContextMap(DestContextTypeMap destContextMap) {
        this.destContextMap = destContextMap;
    }

    @Override
    public String resolveTemplateParameter(TemplateParameter templateParameter, ContextInfo contextInfo) {
        if (!destContextMap.getMap().containsKey(templateParameter.getName())) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(), "Conversion.xml doesn't contain '%s' dest parameter.");
        }
        return getParameterValue(templateParameter);
    }

    /**
     * Gets a parameter value.
     *
     * @param destParameter pre-defined dest parameter
     * @return a parameter value as a string
     */
    public String getParameterValue(DestContextParameters destParameter) {
        return getParameterValue(destParameter.getName());
    }

    /**
     * Gets a parameter value.
     *
     * @param templateParameterName parameter name
     * @return a parameter value as a string
     */
    public String getParameterValue(String templateParameterName) {
        return getParameterValue(new TemplateParameter(TemplateParameterContext.DEST, templateParameterName));
    }

    /**
     * Gets the parameter value.
     *
     * @param templateParameter a enum defining the parameter name
     * @return a parameter value.
     */
    public String getParameterValue(TemplateParameter templateParameter) {
        if (destContextMap == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(), "Dest context for configuration doesn't set.");
        }
        DestContextParamType param = destContextMap.getMap().get(templateParameter.getName());
        return param != null ? param.getValue() : null;
    }


    /**
     * Gets all template parameter values as a string.
     *
     * @return Gets all template parameter values as a string
     */
    public Collection<String> getAllParameters() {
        return destContextMap.getMap().values().stream()
                .map(DestContextParamType::getValue)
                .collect(Collectors.toList());
    }

}
