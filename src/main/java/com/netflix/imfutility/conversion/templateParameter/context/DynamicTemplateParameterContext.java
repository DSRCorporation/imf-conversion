package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterResolver;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.DynamicContextParameters;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;
import com.netflix.imfutility.xsd.conversion.DynamicParameterConcatType;
import com.netflix.imfutility.xsd.conversion.DynamicParameterType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Dynamic Template Parameter Context.
 * <ul>
 * <li>It's used to replace dynamic template parameters in conversion operations</li>
 * <li>May contain any key-value map</li>
 * <li>Created dynamically in the code</li>
 * </ul>
 */
public class DynamicTemplateParameterContext implements ITemplateParameterContext {

    private final Map<String, CustomParameterValue> params = new HashMap<>();
    private final TemplateParameterResolver parameterResolver;


    public DynamicTemplateParameterContext(TemplateParameterContextProvider contextProvider) {
        this.parameterResolver = new TemplateParameterResolver(contextProvider);
    }

    public DynamicTemplateParameterContext addParameter(DynamicParameterType dynamicParameter, ContextInfo contextInfo) {
        return addParameter(
                dynamicParameter.getName().trim(),
                dynamicParameter.getValue().trim(),
                dynamicParameter.isDeleteOnExit(),
                contextInfo);
    }

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

    public DynamicTemplateParameterContext addParameter(DynamicContextParameters param, String paramValue, boolean deleteOnExit, ContextInfo contextInfo) {
        return addParameter(param.getName(), paramValue, deleteOnExit, contextInfo);
    }

    public DynamicTemplateParameterContext addParameter(DynamicContextParameters param, String paramValue, ContextInfo contextInfo) {
        return addParameter(param, paramValue, false, contextInfo);
    }

    public DynamicTemplateParameterContext addParameter(DynamicContextParameters param, String paramValue, boolean deleteOnExit) {
        return addParameter(param, paramValue, deleteOnExit, ContextInfo.EMPTY);
    }

    public DynamicTemplateParameterContext addParameter(DynamicContextParameters param, String paramValue) {
        return addParameter(param, paramValue, ContextInfo.EMPTY);
    }

    public DynamicTemplateParameterContext addParameter(String paramName, String paramValue, boolean deleteOnExit, ContextInfo contextInfo) {
        paramName = parameterResolver.resolveTemplateParameter(paramName, contextInfo);
        paramValue = parameterResolver.resolveTemplateParameter(paramValue, contextInfo);
        params.put(paramName, new CustomParameterValue(paramValue, deleteOnExit));
        return this;
    }

    public DynamicTemplateParameterContext addParameter(String paramName, String paramValue, boolean deleteOnExit) {
        return addParameter(paramName, paramValue, deleteOnExit, ContextInfo.EMPTY);
    }

    public DynamicTemplateParameterContext addParameter(String paramName, String paramValue, ContextInfo contextInfo) {
        return addParameter(paramName, paramValue, false, contextInfo);
    }

    public DynamicTemplateParameterContext addParameter(String paramName, String paramValue) {
        return addParameter(paramName, paramValue, ContextInfo.EMPTY);
    }

    public DynamicTemplateParameterContext appendParameter(DynamicContextParameters param, String paramValue, boolean deleteOnExit, ContextInfo contextInfo) {
        return appendParameter(param.getName(), paramValue, deleteOnExit, contextInfo);
    }

    public DynamicTemplateParameterContext appendParameter(DynamicContextParameters param, String paramValue, ContextInfo contextInfo) {
        return appendParameter(param, paramValue, false, contextInfo);
    }

    public DynamicTemplateParameterContext appendParameter(DynamicContextParameters param, String paramValue, boolean deleteOnExit) {
        return appendParameter(param, paramValue, deleteOnExit, ContextInfo.EMPTY);
    }

    public DynamicTemplateParameterContext appendParameter(DynamicContextParameters param, String paramValue) {
        return appendParameter(param, paramValue, ContextInfo.EMPTY);
    }

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

    public DynamicTemplateParameterContext appendParameter(String paramName, String paramValue, ContextInfo contextInfo) {
        return appendParameter(paramName, paramValue, false, contextInfo);
    }

    public DynamicTemplateParameterContext appendParameter(String paramName, String paramValue, boolean deleteOnExit) {
        return appendParameter(paramName, paramValue, deleteOnExit, ContextInfo.EMPTY);
    }

    public DynamicTemplateParameterContext appendParameter(String paramName, String paramValue) {
        return appendParameter(paramName, paramValue, ContextInfo.EMPTY);
    }

    @Override
    public String resolveTemplateParameter(TemplateParameter templateParameter, ContextInfo contextInfo) {
        return getParameterValueAsString(templateParameter);
    }

    public String getParameterValueAsString(String templateParameterName) {
        return getParameterValueAsString(new TemplateParameter(TemplateParameterContext.DYNAMIC, templateParameterName));
    }

    public String getParameterValueAsString(DynamicContextParameters dynamicParameter) {
        return getParameterValueAsString(new TemplateParameter(TemplateParameterContext.DYNAMIC, dynamicParameter.getName()));
    }

    public String getParameterValueAsString(TemplateParameter templateParameter) {
        return getParameterValue(templateParameter).getValue();
    }

    public CustomParameterValue getParameterValue(String templateParameterName) {
        return getParameterValue(new TemplateParameter(TemplateParameterContext.DYNAMIC, templateParameterName));
    }

    public CustomParameterValue getParameterValue(TemplateParameter templateParameter) {
        CustomParameterValue paramValue = params.get(templateParameter.getName());
        if (paramValue == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("'%s' parameter is not defined.", templateParameter.getName()));
        }
        return paramValue;
    }

    public Collection<String> getAllParametersAsString() {
        return params.values().stream()
                .map(CustomParameterValue::getValue)
                .collect(Collectors.toList());
    }

    public Collection<CustomParameterValue> getAllParameters() {
        return params.values();
    }

}
