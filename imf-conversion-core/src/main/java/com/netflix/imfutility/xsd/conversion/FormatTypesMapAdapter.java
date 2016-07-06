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

import com.netflix.imfutility.generated.conversion.FormatType;
import com.netflix.imfutility.generated.conversion.FormatTypes;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Map;

/**
 * Maps generated {@link FormatTypes} to {@link FormatTypesMap}.
 */
public class FormatTypesMapAdapter extends XmlAdapter<FormatTypes, FormatTypesMap<String, FormatType>> {

    @Override
    public FormatTypesMap<String, FormatType> unmarshal(FormatTypes formatTypes) throws Exception {
        FormatTypesMap<String, FormatType> map = new FormatTypesMap<>();
        for (FormatType ft : formatTypes.getFormat()) {
            map.getMap().put(ft.getName(), ft);
        }
        return map;
    }

    @Override
    public FormatTypes marshal(FormatTypesMap<String, FormatType> map) throws Exception {
        FormatTypes formatTypes = new FormatTypes();
        for (Map.Entry<String, FormatType> entry : map.getMap().entrySet()) {
            formatTypes.getFormat().add(entry.getValue());
        }
        return formatTypes;
    }
}
