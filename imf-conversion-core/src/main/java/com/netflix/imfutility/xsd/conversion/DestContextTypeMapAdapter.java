/*
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
package com.netflix.imfutility.xsd.conversion;

import com.netflix.imfutility.generated.conversion.DestContextParamType;
import com.netflix.imfutility.generated.conversion.DestContextType;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Maps generated {@link DestContextType} to {@link DestContextParamType}.
 */
public class DestContextTypeMapAdapter extends XmlAdapter<DestContextType, DestContextTypeMap> {
    @Override
    public DestContextTypeMap unmarshal(DestContextType destContext) throws Exception {
        DestContextTypeMap map = new DestContextTypeMap();
        map.setName(destContext.getName());
        for (DestContextParamType destParam : destContext.getParam()) {
            map.getMap().put(destParam.getName(), destParam);
        }
        return map;
    }

    @Override
    public DestContextType marshal(DestContextTypeMap map) throws Exception {
        DestContextType destContext = new DestContextType();
        map.getMap().values().stream().forEach(destContext.getParam()::add);
        return destContext;
    }
}
