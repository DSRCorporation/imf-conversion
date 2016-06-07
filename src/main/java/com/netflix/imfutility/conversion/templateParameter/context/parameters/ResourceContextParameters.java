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

    // in time code
    START_TIME_TIMECODE("startTimeTC"),

    // in edit units (audio or video depending on the type)
    START_TIME_EDIT_UNIT("startTimeEU"),

    // in time code
    DURATION_TIMECODE("durationTC"),

    // in edit units (audio or video depending on the type)
    DURATION_EDIT_UNIT("durationEU"),

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
