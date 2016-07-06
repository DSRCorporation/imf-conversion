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
package com.netflix.imfutility.xsd.conversion;

import com.netflix.imfutility.generated.conversion.TmpContextType;
import com.netflix.imfutility.generated.conversion.TmpParamType;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Map;

/**
 * Maps generated {@link TmpContextType} to {@link TmpContextTypeMap}.
 */
public class TmpContextTypeMapAdapter extends XmlAdapter<TmpContextType, TmpContextTypeMap<String, TmpParamType>> {

    @Override
    public TmpContextTypeMap<String, TmpParamType> unmarshal(TmpContextType tmpContextTypes) throws Exception {
        TmpContextTypeMap<String, TmpParamType> map = new TmpContextTypeMap<>();
        for (TmpParamType pt : tmpContextTypes.getParam()) {
            map.getMap().put(pt.getId(), pt);
        }
        return map;
    }

    @Override
    public TmpContextType marshal(TmpContextTypeMap<String, TmpParamType> map) throws Exception {
        TmpContextType tmpContextType = new TmpContextType();
        for (Map.Entry<String, TmpParamType> entry : map.getMap().entrySet()) {
            tmpContextType.getParam().add(entry.getValue());
        }
        return tmpContextType;
    }
}
