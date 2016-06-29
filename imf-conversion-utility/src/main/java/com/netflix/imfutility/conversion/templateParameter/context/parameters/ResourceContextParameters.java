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

    REPEAT_COUNT("repeatCount"),

    // in time code
    START_TIME_TIMECODE("startTimeTC"),

    // in edit units (audio or video depending on the type)
    START_TIME_EDIT_UNIT("startTimeEU"),

    // in time code
    END_TIME_TIMECODE("endTimeTC"),

    // in edit units (audio or video depending on the type)
    END_TIME_EDIT_UNIT("endTimeEU"),

    // in time code
    DURATION_TIMECODE("durationTC"),

    // in edit units (audio or video depending on the type)
    DURATION_EDIT_UNIT("durationEU"),

    // offset of the segment the resource belongs to (in edit units)
    // it equals to the sum of durations of all segments prior the one containing the resource
    OFFSET_TIMECODE("offsetTC"),

    // offset of the segment the resource belongs to (in time code)
    // it equals to the sum of durations of all segments prior the one containing the resource
    OFFSET_EDIT_UNIT("offsetEU"),

    // in frame edit units (audio samples if essence contains audio only; video frames if essence contains video only or both video and audio)
    START_TIME_FRAME_EDIT_UNIT("startTimeFrameEU"),

    // in frame edit units (audio samples if essence contains audio only; video frames if essence contains video only or both video and audio)
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
