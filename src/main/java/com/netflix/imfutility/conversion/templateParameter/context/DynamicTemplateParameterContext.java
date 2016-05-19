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

/**
 * Dynamic Template Parameter Context.
 * <ul>
 * <li>It's used to replace dynamic template parameters in conversion operations</li>
 * <li>May contain any key-value map</li>
 * <li>Created dynamically in the code</li>
 * </ul>
 */
public class DynamicTemplateParameterContext implements ITemplateParameterContext {

    private final Map<String, String> params = new HashMap<>();
    private final TemplateParameterResolver parameterResolver;


    public DynamicTemplateParameterContext(TemplateParameterContextProvider contextProvider) {
        this.parameterResolver = new TemplateParameterResolver(contextProvider);
    }

    public DynamicTemplateParameterContext addParameter(DynamicParameterType dynamicParameter, ContextInfo contextInfo) {
        String paramName = dynamicParameter.getName();
        String paramValue = dynamicParameter.getValue();
        return addParameter(paramName, paramValue, contextInfo);
    }

    public DynamicTemplateParameterContext addParameter(DynamicParameterConcatType dynamicParameter, ContextInfo contextInfo) {
        String paramName = dynamicParameter.getName();
        String paramValue = dynamicParameter.getValue();
        if (dynamicParameter.isConcat() != null && dynamicParameter.isConcat()) {
            appendParameter(paramName, paramValue, contextInfo);
        } else {
            addParameter(paramName, paramValue, contextInfo);
        }
        return this;
    }

    public DynamicTemplateParameterContext addParameter(String paramName, String paramValue, ContextInfo contextInfo) {
        paramName = parameterResolver.resolveTemplateParameter(paramName, contextInfo);
        paramValue = parameterResolver.resolveTemplateParameter(paramValue, contextInfo);
        params.put(paramName, paramValue);
        return this;
    }

    public DynamicTemplateParameterContext addParameter(DynamicContextParameters paramName, String paramValue, ContextInfo contextInfo) {
        return addParameter(paramName.getName(), paramValue, contextInfo);
    }

    public DynamicTemplateParameterContext appendParameter(DynamicContextParameters paramName, String paramValue, ContextInfo contextInfo) {
        return appendParameter(paramName.getName(), paramValue, contextInfo);
    }

    public DynamicTemplateParameterContext appendParameter(String paramName, String paramValue, ContextInfo contextInfo) {
        paramName = parameterResolver.resolveTemplateParameter(paramName, contextInfo);
        paramValue = parameterResolver.resolveTemplateParameter(paramValue, contextInfo);
        if (!params.containsKey(paramName)) {
            params.put(paramName, paramValue);
        } else {
            paramValue = params.get(paramName).concat(paramValue);
            params.put(paramName, paramValue);
        }
        return this;
    }

    public String getParameterValue(String templateParameterName) {
        return getParameterValue(new TemplateParameter(TemplateParameterContext.DYNAMIC, templateParameterName));
    }

    public String getParameterValue(DynamicContextParameters dynamicParameter) {
        return getParameterValue(new TemplateParameter(TemplateParameterContext.DYNAMIC, dynamicParameter.getName()));
    }


    @Override
    public String resolveTemplateParameter(TemplateParameter templateParameter, ContextInfo contextInfo) {
        return getParameterValue(templateParameter);
    }

    public Collection<String> getAllParameters() {
        return params.values();
    }

    private String getParameterValue(TemplateParameter templateParameter) {
        String paramValue = params.get(templateParameter.getName());
        if (paramValue == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("'%s' parameter is not defined.", templateParameter.getName()));
        }
        return paramValue;
    }

}
