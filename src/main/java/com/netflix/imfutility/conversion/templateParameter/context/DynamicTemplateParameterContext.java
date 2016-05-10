package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;

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

    public void addParameter(String paramName, String paramValue) {
        params.put(paramName, paramValue);
    }

    @Override
    public String resolveTemplateParameter(TemplateParameter templateParameter) {
        return params.get(templateParameter.getName());
    }

}
