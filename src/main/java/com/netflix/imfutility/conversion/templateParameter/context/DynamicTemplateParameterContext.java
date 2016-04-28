package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexander on 4/25/2016.
 */
public class DynamicTemplateParameterContext implements ITemplateParameterContext {

    private Map<String, String> params = new HashMap<>();

    public void addParameter(String paramName, String paramValue) {
        params.put(paramName, paramValue);
    }

    @Override
    public String resolveTemplateParameter(TemplateParameter templateParameter) {
        return params.get(templateParameter.getName());
    }

}
