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
package com.netflix.imfutility.conversion.templateParameter.context.parameters;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Pre-defined dynamic template parameter names (it's possible to define another ones).
 */
public enum DynamicContextParameters {

    MEDIA_INFO_INPUT("mediaInfoInput"),

    MEDIA_INFO_OUTPUT("mediaInfoOutput"),

    VALIDATION_TOOL("validateTool"),

    IMP("imp"),

    CPL("cpl"),

    WORKING_DIR("workingDir"),

    HAS_AUDIO("hasAudio"),

    HAS_VIDEO("hasVideo"),

    HAS_SUBTITLE("hasSubtitle"),

    HAS_AUDIO_AND_VIDEO("hasAudioAndVideo"),

    HAS_AUDIO_ONLY("hasAudioOnly"),

    HAS_VIDEO_ONLY("hasVideoOnly"),

    OUTPUT_VALIDATION_FILE("outputValidationFile"),

    SINGLE_AUDIO("singleAudio"),

    SINGLE_SUBTITLE("singleSubtitle");

    private final String name;

    DynamicContextParameters(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static DynamicContextParameters fromName(String name) {
        for (DynamicContextParameters e : values()) {
            if (e.getName().equals(name)) {
                return e;
            }
        }
        return null;
    }

    public static String getSupportedContextParameters() {
        return Arrays.stream(DynamicContextParameters.values())
                .map(DynamicContextParameters::getName)
                .collect(Collectors.joining(" ", "[", "]"));
    }

}
