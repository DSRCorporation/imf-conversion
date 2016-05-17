package com.netflix.imfutility.conversion.templateParameter.context;

import java.util.HashMap;
import java.util.Map;

/**
 * A helper generic class to store template parameters.
 */
public class ContextParameterData<T> {

    private final Map<T, String> params = new HashMap<>();

    public String getParameterValue(T param) {
        return params.get(param);
    }

    public void addParameter(T paramName, String paramValue) {
        params.put(paramName, paramValue);
    }

}
