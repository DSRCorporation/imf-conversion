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

import com.netflix.imfutility.generated.conversion.MediaInfoCommandOtherType;
import com.netflix.imfutility.generated.conversion.MediaInfoCommandOthersType;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Maps generated {@link MediaInfoCommandOthersType} to {@link MediaInfoCommandOthersTypeMap}.
 */
public class MediaInfoCommandOthersTypeMapAdapter
        extends XmlAdapter<MediaInfoCommandOthersType, MediaInfoCommandOthersTypeMap> {
    @Override
    public MediaInfoCommandOthersTypeMap unmarshal(MediaInfoCommandOthersType commands)
            throws Exception {
        MediaInfoCommandOthersTypeMap map = new MediaInfoCommandOthersTypeMap();
        for (MediaInfoCommandOtherType command : commands.getMediaInfoCommand()) {
            map.getMap().put(command.getName(), command);
        }
        return map;
    }

    @Override
    public MediaInfoCommandOthersType marshal(MediaInfoCommandOthersTypeMap map)
            throws Exception {
        MediaInfoCommandOthersType commands = new MediaInfoCommandOthersType();
        map.getMap().values().stream().forEach(commands.getMediaInfoCommand()::add);
        return commands;
    }
}
