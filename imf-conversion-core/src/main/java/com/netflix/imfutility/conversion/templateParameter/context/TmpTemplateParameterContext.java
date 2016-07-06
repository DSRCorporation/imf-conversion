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
package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;
import com.netflix.imfutility.generated.conversion.FormatType;
import com.netflix.imfutility.generated.conversion.TmpParamType;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Tmp Template Parameter Context.
 * <ul>
 * <li>It's used to replace tmp template parameters in conversion operations</li>
 * <li>Created from conversion.xml (see {@link com.netflix.imfutility.generated.conversion.TmpContextType})</li>
 * </ul>
 */
public class TmpTemplateParameterContext implements ITemplateParameterContext {

    private final FormatType format;

    public TmpTemplateParameterContext(FormatType format) {
        this.format = format;
    }

    @Override
    public String resolveTemplateParameter(TemplateParameter templateParameter, ContextInfo contextInfo) {
        return getParameterValueAsString(templateParameter);
    }

    /**
     * @param templateParameterName parameter name
     * @return a parameter value as a string
     */
    public String getParameterValueAsString(String templateParameterName) {
        return getParameterValueAsString(new TemplateParameter(TemplateParameterContext.TMP, templateParameterName));
    }

    /**
     * @param templateParameter a enum defining the parameter name
     * @return a parameter value as a string
     */
    public String getParameterValueAsString(TemplateParameter templateParameter) {
        return getParameterValue(templateParameter).getValue();
    }

    /**
     * Gets the parameter value as {@link CustomParameterValue} instance.
     * The returned parameter value contains additional information about the parameter, such as whether it should be deleted on exit.
     *
     * @param templateParameter a enum defining the parameter name
     * @return a parameter value instance with additional information besides parameter value string.
     */
    public CustomParameterValue getParameterValue(TemplateParameter templateParameter) {
        if (format.getTmpContext() == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(), "Conversion.xml doesn't contain any tmp context parameters.");
        }

        TmpParamType param = format.getTmpContext().getMap().get(templateParameter.getName());
        if (param == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(), "Conversion.xml doesn't contain '%s' tmp parameter.");
        }

        String paramValue = param.getValue();
        if (StringUtils.isEmpty(paramValue)) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(), "Conversion.xml contains an empty '%s' tmp parameter value.");
        }
        paramValue = paramValue.trim();
        if (StringUtils.isEmpty(paramValue)) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(), "Conversion.xml contains an empty '%s' tmp parameter value.");
        }

        return new CustomParameterValue(paramValue, param.isDeleteOnExit());
    }

    /**
     * @return Gets all template parameter values as a string
     */
    public Collection<String> getAllParametersAsString() {
        return format.getTmpContext().getMap().values().stream()
                .map(TmpParamType::getValue)
                .collect(Collectors.toList());

    }

    /**
     * @return gets all parameter value instances with additional information besides parameter value string.
     */
    public Collection<CustomParameterValue> getAllParameters() {
        return format.getTmpContext().getMap().values().stream()
                .map((TmpParamType param) -> new CustomParameterValue(param.getValue(), param.isDeleteOnExit()))
                .collect(Collectors.toList());
    }

}
