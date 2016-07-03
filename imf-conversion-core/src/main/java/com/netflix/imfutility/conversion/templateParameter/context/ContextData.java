package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.cpl.uuid.UUID;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A helper generic class to store template parameters for each UUID.
 */
public class ContextData<U extends UUID, T> {

    private final Map<U, ContextParameterData<T>> params = new LinkedHashMap<>();

    public Collection<U> getUuids() {
        return params.keySet();
    }

    public ContextParameterData<T> getParameterData(U uuid) {
        return params.get(uuid);
    }

    public int getCount() {
        return params.size();
    }

    public boolean contains(U uuid) {
        return params.containsKey(uuid);
    }

    public void addParameter(U uuid, T paramName, String paramValue) {
        ContextParameterData<T> paramData = params.get(uuid);
        if (paramData == null) {
            paramData = new ContextParameterData<>();
            params.put(uuid, paramData);
        }
        paramData.addParameter(paramName, paramValue);
    }

}
