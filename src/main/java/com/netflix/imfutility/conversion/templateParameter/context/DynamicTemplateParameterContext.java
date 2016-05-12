package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterResolver;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;

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

    public void addParameter(String paramName, String paramValue, ContextInfo contextInfo) {
        paramValue = parameterResolver.resolveTemplateParameter(paramValue, contextInfo);
        params.put(paramName, paramValue);
    }

    public void appendParameter(String paramName, String paramValue, ContextInfo contextInfo) {
        paramValue = parameterResolver.resolveTemplateParameter(paramValue, contextInfo);
        if (!params.containsKey(paramName)) {
            params.put(paramName, paramValue);
        } else {
            paramValue = params.get(paramName).concat(paramValue);
            params.put(paramName, paramValue);
        }

    }

    @Override
    public String resolveTemplateParameter(TemplateParameter templateParameter, ContextInfo contextInfo) {
        String paramValue = params.get(templateParameter.getName());
        if (paramValue == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("'%s' parameter is not defined.", templateParameter.getName()));
        }
        return paramValue;
    }

}
