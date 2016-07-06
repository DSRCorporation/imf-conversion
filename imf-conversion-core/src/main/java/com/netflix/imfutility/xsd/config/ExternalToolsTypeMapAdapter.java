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
package com.netflix.imfutility.xsd.config;

import com.netflix.imfutility.generated.config.ExternalToolsType;
import com.netflix.imfutility.generated.config.ToolType;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Map;

/**
 * Maps generated {@link ExternalToolsType} to {@link ExternalToolsTypeMap}.
 */
public class ExternalToolsTypeMapAdapter extends XmlAdapter<ExternalToolsType, ExternalToolsTypeMap<String, ToolType>> {

    @Override
    public ExternalToolsTypeMap<String, ToolType> unmarshal(ExternalToolsType externalTools) throws Exception {
        ExternalToolsTypeMap<String, ToolType> map = new ExternalToolsTypeMap<>();
        for (ToolType tt : externalTools.getTool()) {
            map.getMap().put(tt.getId(), tt);
        }
        return map;
    }

    @Override
    public ExternalToolsType marshal(ExternalToolsTypeMap<String, ToolType> map) throws Exception {
        ExternalToolsType externalTools = new ExternalToolsType();
        for (Map.Entry<String, ToolType> entry : map.getMap().entrySet()) {
            externalTools.getTool().add(entry.getValue());
        }
        return externalTools;
    }
}
