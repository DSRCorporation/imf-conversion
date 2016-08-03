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
 * All supported resource template parameter names.
 */
public enum ResourceContextParameters {

    NUM("num"),

    UUID("uuid"),

    ESSENCE("essence"),

    EDIT_RATE("editRate"),

    // total repeat count
    REPEAT_COUNT("repeatCount"),

    // current repeat num
    REPEAT("repeat"),

    // in time code
    START_TIME_TIMECODE("startTimeTC"),

    // in edit units (audio or video depending on the type)
    START_TIME_EDIT_UNIT("startTimeEU"),

    // in milliseconds
    START_TIME_MS("startTimeMS"),

    // in time code
    END_TIME_TIMECODE("endTimeTC"),

    // in edit units (audio or video depending on the type)
    END_TIME_EDIT_UNIT("endTimeEU"),

    // in milliseconds
    END_TIME_MS("endTimeMS"),

    // in time code
    DURATION_TIMECODE("durationTC"),

    // in edit units (audio or video depending on the type)
    DURATION_EDIT_UNIT("durationEU"),

    // in milliseconds
    DURATION_MS("durationMS"),

    // offset of the segment the resource belongs to (in milliseconds)
    // it equals to the sum of durations (in ms) of all segments prior the one containing the resource
    OFFSET_MS("offsetMS"),

    // in frame edit units (audio samples if essence contains audio only; video frames if essence contains video only
    // or both video and audio)
    START_TIME_FRAME_EDIT_UNIT("startTimeFrameEU"),

    // in frame edit units (audio samples if essence contains audio only; video frames if essence contains video only
    // or both video and audio)
    DURATION_FRAME_EDIT_UNIT("durationFrameEU");


    private final String name;

    ResourceContextParameters(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ResourceContextParameters fromName(String name) {
        for (ResourceContextParameters e : values()) {
            if (e.getName().equals(name)) {
                return e;
            }
        }
        return null;
    }

    public static String getSupportedContextParameters() {
        return Arrays.stream(ResourceContextParameters.values())
                .map(ResourceContextParameters::getName)
                .collect(Collectors.joining(" ", "[", "]"));
    }

}
