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
package com.netflix.imfutility.conversion.templateParameter.context.parameters;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Pre-defined dest context parameter names (it's possible to define another ones).
 */
public enum DestContextParameters {
    START_TIME("startTime"),
    WIDTH("width"),
    HEIGHT("height"),
    FRAME_RATE("frameRate"),
    INTERLACED("interlaced"),
    ASPECT_RATIO("aspectRatio"),
    DURATION("duration"),
    DAR("dar"),
    SAMPLE_RATE("sampleRate"),
    BITS_SAMPLE("bitsSample");

    private final String name;

    DestContextParameters(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static DestContextParameters fromName(String name) {
        for (DestContextParameters e : values()) {
            if (e.getName().equals(name)) {
                return e;
            }
        }
        return null;
    }

    public static String getSupportedContextParameters() {
        return Arrays.stream(DestContextParameters.values())
                .map(DestContextParameters::getName)
                .collect(Collectors.joining(" ", "[", "]"));
    }

}

