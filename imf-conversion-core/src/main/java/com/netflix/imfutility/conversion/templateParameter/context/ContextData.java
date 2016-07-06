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
