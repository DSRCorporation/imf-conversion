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
package com.netflix.imfutility.itunes.util;

import com.netflix.imfutility.generated.conversion.DestContextParamType;
import com.netflix.imfutility.xsd.conversion.DestContextTypeMap;

import static com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters.FRAME_RATE;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters.HEIGHT;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters.INTERLACED;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters.WIDTH;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DEST_PARAM_VIDEO_SPECIFIED_FOR;

/**
 * Test utility for dest context needs.
 */
public final class DestContextUtils {
    private DestContextUtils() {
    }

    public static DestContextTypeMap createDestContextMap(String name,
                                                          String width,
                                                          String height,
                                                          String frameRate,
                                                          String interlaced,
                                                          String specifiedFor) {
        DestContextTypeMap contextMap = new DestContextTypeMap();
        contextMap.setName(name);

        putDestContextValue(WIDTH.getName(), width, contextMap);
        putDestContextValue(HEIGHT.getName(), height, contextMap);
        putDestContextValue(FRAME_RATE.getName(), frameRate, contextMap);
        putDestContextValue(INTERLACED.getName(), interlaced, contextMap);
        putDestContextValue(DEST_PARAM_VIDEO_SPECIFIED_FOR, specifiedFor, contextMap);

        return contextMap;
    }


    public static void putDestContextValue(String paramName, String paramValue, DestContextTypeMap contextMap) {
        DestContextParamType param = new DestContextParamType();
        param.setName(paramName);
        param.setValue(paramValue);

        contextMap.getMap().put(paramName, param);
    }
}
