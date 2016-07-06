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

import com.netflix.imfutility.generated.conversion.FormatConfigurationType;
import com.netflix.imfutility.generated.conversion.FormatConfigurationTypes;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Map;

/**
 * Maps generated {@link FormatConfigurationTypes} to {@link FormatConfigurationTypesMap}.
 */
public class FormatConfigurationTypesMapAdapter extends XmlAdapter<FormatConfigurationTypes, FormatConfigurationTypesMap<String, FormatConfigurationType>> {

    @Override
    public FormatConfigurationTypesMap<String, FormatConfigurationType> unmarshal(FormatConfigurationTypes formatConfigurations) throws Exception {
        FormatConfigurationTypesMap<String, FormatConfigurationType> map = new FormatConfigurationTypesMap<>();
        for (FormatConfigurationType fct : formatConfigurations.getFormatConfiguration()) {
            map.getMap().put(fct.getName(), fct);
        }
        return map;
    }

    @Override
    public FormatConfigurationTypes marshal(FormatConfigurationTypesMap<String, FormatConfigurationType> map) throws Exception {
        FormatConfigurationTypes formatTypes = new FormatConfigurationTypes();
        for (Map.Entry<String, FormatConfigurationType> entry : map.getMap().entrySet()) {
            formatTypes.getFormatConfiguration().add(entry.getValue());
        }
        return formatTypes;
    }
}
