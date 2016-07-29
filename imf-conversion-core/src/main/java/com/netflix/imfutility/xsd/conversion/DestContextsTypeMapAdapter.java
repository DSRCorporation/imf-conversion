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

import com.netflix.imfutility.generated.conversion.DestContextsType;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Maps generated {@link DestContextsType} to {@link DestContextTypeMap}.
 */
public class DestContextsTypeMapAdapter extends
        XmlAdapter<DestContextsType, DestContextsTypeMap> {

    @Override
    public DestContextsTypeMap unmarshal(DestContextsType destContexts)
            throws Exception {
        DestContextsTypeMap map = new DestContextsTypeMap();
        for (DestContextTypeMap destContext : destContexts.getDestContext()) {
            map.getMap().put(destContext.getName(), destContext);
        }
        return map;
    }

    @Override
    public DestContextsType marshal(DestContextsTypeMap map) throws Exception {
        DestContextsType destContexts = new DestContextsType();
        map.getMap().values().stream().forEach(destContexts.getDestContext()::add);
        return destContexts;
    }
}
