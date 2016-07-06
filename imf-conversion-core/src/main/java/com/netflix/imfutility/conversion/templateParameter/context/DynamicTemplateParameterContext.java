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

import com.netflix.imfutility.CoreConstants;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterResolver;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.DynamicContextParameters;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;
import com.netflix.imfutility.generated.conversion.DynamicParameterConcatType;
import com.netflix.imfutility.generated.conversion.DynamicParameterType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Dynamic Template Parameter Context.
 * <ul>
 * <li>It's used to replace dynamic template parameters in conversion operations</li>
 * <li>May contain any key-value map</li>
 * <li>May be created dynamically in the code</li>
 * <li>May be created dynamically in conversion.xml</li>
 * <li>Supports deleteOnExit flag to delete a tmp file on exit (if the parameter defines a valid path).</li>
 * <li>It's possible to either add a parameter value for the given parameter name, or append the parameter value.
 * In the first case the previous value for the parameter name will be replaced. In the second case the new value will be appended to the previous value.</li>
 * <li>A dynamic parameter value may contain template parameters. All template parameters are resolved before adding a parameter.</li>
 * <li>A dynamic parameter name may contain template parameters. All template parameters are resolved before adding a parameter.</li>
 * </ul>
 */
public class DynamicTemplateParameterContext implements ITemplateParameterContext {

    private final Map<String, CustomParameterValue> params = new HashMap<>();
    private final TemplateParameterResolver parameterResolver;


    public DynamicTemplateParameterContext(TemplateParameterContextProvider contextProvider) {
        this.parameterResolver = new TemplateParameterResolver(contextProvider);
        initDefaultParameters(contextProvider);
    }

    private void initDefaultParameters(TemplateParameterContextProvider contextProvider) {
        addParameter(DynamicContextParameters.WORKING_DIR, contextProvider.getWorkingDir().getAbsolutePath());
        addParameter(DynamicContextParameters.OUTPUT_VALIDATION_FILE, CoreConstants.DEFAULT_OUTPUT_VALIDATION_FILE, true);
    }

    /**
     * Adds a dynamic parameter defined in conversion.xml.
     * <ul>
     * <li>All template parameters within the parameter value and name are resolved.</li>
     * <li>The parameter value replaces the previous parameter value.</li>
     * </ul>
     *
     * @param dynamicParameter a dynamic parameter defined in the conversion.xml.
     * @param contextInfo      a context info to resolved template parameters within the given parameter value.
     * @return this dynamic parameter context.
     */
    public DynamicTemplateParameterContext addParameter(DynamicParameterType dynamicParameter, ContextInfo contextInfo) {
        return addParameter(
                dynamicParameter.getName().trim(),
                dynamicParameter.getValue().trim(),
                dynamicParameter.isDeleteOnExit(),
                contextInfo);
    }

    /**
     * Adds a dynamic parameter defined in conversion.xml, that supports concatenation.
     * <ul>
     * <li>All template parameters within the parameter value and name are resolved.</li>
     * <li>The parameter value either replaces the previous parameter value or is appended to the previous parameter value
     * depending on the flag.</li>
     * </ul>
     *
     * @param dynamicParameter a dynamic parameter defined in the conversion.xml.
     * @param contextInfo      a context info to resolved template parameters within the given parameter value.
     * @return this dynamic parameter context.
     */
    public DynamicTemplateParameterContext addParameter(DynamicParameterConcatType dynamicParameter, ContextInfo contextInfo) {
        String paramName = dynamicParameter.getName().trim();
        String paramValue = dynamicParameter.getValue().trim();
        if (dynamicParameter.isConcat() != null && dynamicParameter.isConcat()) {
            appendParameter(paramName, paramValue, dynamicParameter.isDeleteOnExit(), contextInfo);
        } else {
            addParameter(paramName, paramValue, dynamicParameter.isDeleteOnExit(), contextInfo);
        }
        return this;
    }

    /**
     * Adds a dynamic parameter value.
     * <ul>
     * <li>All template parameters within the parameter value and name are resolved.</li>
     * <li>The parameter value replaces the previous parameter value.</li>
     * </ul>
     *
     * @param param        enum defining the parameter name.
     * @param paramValue   parameter value
     * @param deleteOnExit whether a file defined by the parameter value must be deleted on exit (must be true for tmp files).
     * @param contextInfo  a context info to resolved template parameters within the given parameter value.
     * @return this dynamic parameter context.
     */
    public DynamicTemplateParameterContext addParameter(DynamicContextParameters param, String paramValue, boolean deleteOnExit, ContextInfo contextInfo) {
        return addParameter(param.getName(), paramValue, deleteOnExit, contextInfo);
    }

    /**
     * Adds a dynamic parameter value.
     * <ul>
     * <li>All template parameters within the parameter value and name are resolved.</li>
     * <li>The parameter value replaces the previous parameter value.</li>
     * <li>A file defined by the parameter will not be deleted on exit.</li>
     * </ul>
     *
     * @param param       enum defining the parameter name.
     * @param paramValue  parameter value
     * @param contextInfo a context info to resolved template parameters within the given parameter value.
     * @return this dynamic parameter context.
     */
    public DynamicTemplateParameterContext addParameter(DynamicContextParameters param, String paramValue, ContextInfo contextInfo) {
        return addParameter(param, paramValue, false, contextInfo);
    }

    /**
     * Adds a dynamic parameter value.
     * <ul>
     * <li>It doesn't expect any template parameters within the parameter value.</li>
     * <li>The parameter value replaces the previous parameter value.</li>
     * </ul>
     *
     * @param param        enum defining the parameter name.
     * @param paramValue   parameter value
     * @param deleteOnExit whether a file defined by the parameter value must be deleted on exit (must be true for tmp files).
     * @return this dynamic parameter context.
     */
    public DynamicTemplateParameterContext addParameter(DynamicContextParameters param, String paramValue, boolean deleteOnExit) {
        return addParameter(param, paramValue, deleteOnExit, ContextInfo.EMPTY);
    }

    /**
     * Adds a dynamic parameter value.
     * <ul>
     * <li>It doesn't expect any template parameters within the parameter value.</li>
     * <li>The parameter value replaces the previous parameter value.</li>
     * <li>A file defined by the parameter will not be deleted on exit.</li>
     * </ul>
     *
     * @param param      enum defining the parameter name.
     * @param paramValue parameter value
     * @return this dynamic parameter context.
     */
    public DynamicTemplateParameterContext addParameter(DynamicContextParameters param, String paramValue) {
        return addParameter(param, paramValue, ContextInfo.EMPTY);
    }

    /**
     * Adds a dynamic parameter value.
     * <ul>
     * <li>All template parameters within the parameter value and name are resolved.</li>
     * <li>The parameter value replaces the previous parameter value.</li>
     * </ul>
     *
     * @param paramName    parameter name
     * @param paramValue   parameter value
     * @param deleteOnExit whether a file defined by the parameter value must be deleted on exit (must be true for tmp files).
     * @param contextInfo  a context info to resolved template parameters within the given parameter value.
     * @return this dynamic parameter context.
     */
    public DynamicTemplateParameterContext addParameter(String paramName, String paramValue, boolean deleteOnExit, ContextInfo contextInfo) {
        paramName = parameterResolver.resolveTemplateParameter(paramName, contextInfo);
        paramValue = parameterResolver.resolveTemplateParameter(paramValue, contextInfo);
        params.put(paramName, new CustomParameterValue(paramValue, deleteOnExit));
        return this;
    }

    /**
     * Adds a dynamic parameter value.
     * <ul>
     * <li>It doesn't expect any template parameters within the parameter value.</li>
     * <li>The parameter value replaces the previous parameter value.</li>
     * </ul>
     *
     * @param paramName    parameter name
     * @param paramValue   parameter value
     * @param deleteOnExit whether a file defined by the parameter value must be deleted on exit (must be true for tmp files).
     * @return this dynamic parameter context.
     */
    public DynamicTemplateParameterContext addParameter(String paramName, String paramValue, boolean deleteOnExit) {
        return addParameter(paramName, paramValue, deleteOnExit, ContextInfo.EMPTY);
    }

    /**
     * Adds a dynamic parameter value.
     * <ul>
     * <li>All template parameters within the parameter value and name are resolved.</li>
     * <li>The parameter value replaces the previous parameter value.</li>
     * <li>A file defined by the parameter will not be deleted on exit.</li>
     * </ul>
     *
     * @param paramName   parameter name
     * @param paramValue  parameter value
     * @param contextInfo a context info to resolved template parameters within the given parameter value.
     * @return this dynamic parameter context.
     */
    public DynamicTemplateParameterContext addParameter(String paramName, String paramValue, ContextInfo contextInfo) {
        return addParameter(paramName, paramValue, false, contextInfo);
    }

    /**
     * Adds a dynamic parameter value.
     * <ul>
     * <li>It doesn't expect any template parameters within the parameter value.</li>
     * <li>The parameter value replaces the previous parameter value.</li>
     * <li>A file defined by the parameter will not be deleted on exit.</li>
     * </ul>
     *
     * @param paramName  parameter name
     * @param paramValue parameter value
     * @return this dynamic parameter context.
     */
    public DynamicTemplateParameterContext addParameter(String paramName, String paramValue) {
        return addParameter(paramName, paramValue, ContextInfo.EMPTY);
    }

    /**
     * Appends a dynamic parameter value.
     * <ul>
     * <li>All template parameters within the parameter value and name are resolved.</li>
     * <li>The parameter value is appended to the previous parameter value.</li>
     * </ul>
     *
     * @param param        enum defining the parameter name.
     * @param paramValue   parameter value
     * @param deleteOnExit whether a file defined by the parameter value must be deleted on exit (must be true for tmp files).
     * @param contextInfo  a context info to resolved template parameters within the given parameter value.
     * @return this dynamic parameter context.
     */
    public DynamicTemplateParameterContext appendParameter(DynamicContextParameters param, String paramValue, boolean deleteOnExit, ContextInfo contextInfo) {
        return appendParameter(param.getName(), paramValue, deleteOnExit, contextInfo);
    }

    /**
     * Appends a dynamic parameter value.
     * <ul>
     * <li>All template parameters within the parameter value and name are resolved.</li>
     * <li>The parameter value is appended to the previous parameter value.</li>
     * <li>A file defined by the parameter will not be deleted on exit.</li>
     * </ul>
     *
     * @param param       enum defining the parameter name.
     * @param paramValue  parameter value
     * @param contextInfo a context info to resolved template parameters within the given parameter value.
     * @return this dynamic parameter context.
     */
    public DynamicTemplateParameterContext appendParameter(DynamicContextParameters param, String paramValue, ContextInfo contextInfo) {
        return appendParameter(param, paramValue, false, contextInfo);
    }

    /**
     * Appends a dynamic parameter value.
     * <ul>
     * <li>It doesn't expect any template parameters within the parameter value.</li>
     * <li>The parameter value is appended to the previous parameter value.</li>
     * </ul>
     *
     * @param param        enum defining the parameter name.
     * @param paramValue   parameter value
     * @param deleteOnExit whether a file defined by the parameter value must be deleted on exit (must be true for tmp files).
     * @return this dynamic parameter context.
     */
    public DynamicTemplateParameterContext appendParameter(DynamicContextParameters param, String paramValue, boolean deleteOnExit) {
        return appendParameter(param, paramValue, deleteOnExit, ContextInfo.EMPTY);
    }

    /**
     * Appends a dynamic parameter value.
     * <ul>
     * <li>It doesn't expect any template parameters within the parameter value.</li>
     * <li>The parameter value is appended to the previous parameter value.</li>
     * <li>A file defined by the parameter will not be deleted on exit.</li>
     * </ul>
     *
     * @param param      enum defining the parameter name.
     * @param paramValue parameter value
     * @return this dynamic parameter context.
     */
    public DynamicTemplateParameterContext appendParameter(DynamicContextParameters param, String paramValue) {
        return appendParameter(param, paramValue, ContextInfo.EMPTY);
    }

    /**
     * Appends a dynamic parameter value.
     * <ul>
     * <li>All template parameters within the parameter value and name are resolved.</li>
     * <li>The parameter value is appended to the previous parameter value.</li>
     * </ul>
     *
     * @param paramName    parameter name
     * @param paramValue   parameter value
     * @param deleteOnExit whether a file defined by the parameter value must be deleted on exit (must be true for tmp files).
     * @param contextInfo  a context info to resolved template parameters within the given parameter value.
     * @return this dynamic parameter context.
     */
    public DynamicTemplateParameterContext appendParameter(String paramName, String paramValue, boolean deleteOnExit, ContextInfo contextInfo) {
        paramName = parameterResolver.resolveTemplateParameter(paramName, contextInfo);
        paramValue = parameterResolver.resolveTemplateParameter(paramValue, contextInfo);
        if (!params.containsKey(paramName)) {
            params.put(paramName, new CustomParameterValue(paramValue, deleteOnExit));
        } else {
            paramValue = params.get(paramName).getValue().concat(paramValue);
            params.put(paramName, new CustomParameterValue(paramValue, deleteOnExit));
        }
        return this;
    }

    /**
     * Appends a dynamic parameter value.
     * <ul>
     * <li>All template parameters within the parameter value and name are resolved.</li>
     * <li>The parameter value is appended to the previous parameter value.</li>
     * <li>A file defined by the parameter will not be deleted on exit.</li>
     * </ul>
     *
     * @param paramName   parameter name
     * @param paramValue  parameter value
     * @param contextInfo a context info to resolved template parameters within the given parameter value.
     * @return this dynamic parameter context.
     */
    public DynamicTemplateParameterContext appendParameter(String paramName, String paramValue, ContextInfo contextInfo) {
        return appendParameter(paramName, paramValue, false, contextInfo);
    }

    /**
     * Appends a dynamic parameter value.
     * <ul>
     * <li>It doesn't expect any template parameters within the parameter value.</li>
     * <li>The parameter value is appended to the previous parameter value.</li>
     * </ul>
     *
     * @param paramName    parameter name
     * @param paramValue   parameter value
     * @param deleteOnExit whether a file defined by the parameter value must be deleted on exit (must be true for tmp files).
     * @return this dynamic parameter context.
     */
    public DynamicTemplateParameterContext appendParameter(String paramName, String paramValue, boolean deleteOnExit) {
        return appendParameter(paramName, paramValue, deleteOnExit, ContextInfo.EMPTY);
    }

    /**
     * Appends a dynamic parameter value.
     * <ul>
     * <li>It doesn't expect any template parameters within the parameter value.</li>
     * <li>The parameter value is appended to the previous parameter value.</li>
     * <li>A file defined by the parameter will not be deleted on exit.</li>
     * </ul>
     *
     * @param paramName  parameter name
     * @param paramValue parameter value
     * @return this dynamic parameter context.
     */
    public DynamicTemplateParameterContext appendParameter(String paramName, String paramValue) {
        return appendParameter(paramName, paramValue, ContextInfo.EMPTY);
    }

    @Override
    public String resolveTemplateParameter(TemplateParameter templateParameter, ContextInfo contextInfo) {
        return getParameterValueAsString(templateParameter);
    }

    /**
     * Gets the parameter value as a string.  All template parameters within the parameter value are resolved.
     *
     * @param templateParameterName parameter name
     * @return a parameter value as a string.
     */
    public String getParameterValueAsString(String templateParameterName) {
        return getParameterValueAsString(new TemplateParameter(TemplateParameterContext.DYNAMIC, templateParameterName));
    }

    /**
     * Gets the parameter value as a string.  All template parameters within the parameter value are resolved.
     *
     * @param dynamicParameter enum defining  parameter name
     * @return a parameter value as a string.
     */
    public String getParameterValueAsString(DynamicContextParameters dynamicParameter) {
        return getParameterValueAsString(new TemplateParameter(TemplateParameterContext.DYNAMIC, dynamicParameter.getName()));
    }

    /**
     * Gets the parameter value as a string.  All template parameters within the parameter value are resolved.
     *
     * @param templateParameter template parameter instance.
     * @return a parameter value as a string.
     */
    public String getParameterValueAsString(TemplateParameter templateParameter) {
        return getParameterValue(templateParameter).getValue();
    }

    /**
     * Gets the parameter value as {@link CustomParameterValue} instance.  All template parameters within the parameter value are resolved.
     * The returned parameter value contains additional information about the parameter, such as whether it should be deleted on exit.
     *
     * @param templateParameterName parameter name
     * @return a parameter value instance with additional information besides parameter value string.
     */
    public CustomParameterValue getParameterValue(String templateParameterName) {
        return getParameterValue(new TemplateParameter(TemplateParameterContext.DYNAMIC, templateParameterName));
    }

    /**
     * Gets the parameter value as {@link CustomParameterValue} instance.  All template parameters within the parameter value are resolved.
     * The returned parameter value contains additional information about the parameter, such as whether it should be deleted on exit.
     *
     * @param dynamicParameter enum defining  parameter name
     * @return a parameter value instance with additional information besides parameter value string.
     */
    public CustomParameterValue getParameterValue(DynamicContextParameters dynamicParameter) {
        return getParameterValue(dynamicParameter.getName());
    }

    /**
     * Gets the parameter value as {@link CustomParameterValue} instance.  All template parameters within the parameter value are resolved.
     * The returned parameter value contains additional information about the parameter, such as whether it should be deleted on exit.
     *
     * @param templateParameter template parameter instance.
     * @return a parameter value instance with additional information besides parameter value string.
     */
    public CustomParameterValue getParameterValue(TemplateParameter templateParameter) {
        CustomParameterValue paramValue = params.get(templateParameter.getName());
        if (paramValue == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("'%s' parameter is not defined.", templateParameter.getName()));
        }
        return paramValue;
    }

    /**
     * @return Gets all template parameter values as a string
     */
    public Collection<String> getAllParametersAsString() {
        return params.values().stream()
                .map(CustomParameterValue::getValue)
                .collect(Collectors.toList());
    }

    /**
     * @return gets all parameter value instances with additional information besides parameter value string.
     */
    public Collection<CustomParameterValue> getAllParameters() {
        return params.values();
    }

}
