package com.netflix.imfutility.conversion.templateParameter.context.parameters;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * All supported segment template parameter names.
 */
public enum ResourceContextParameters {

    NUM("num"),

    UUID("uuid"),

    ESSENCE("essence"),

    START_TIME_TIMECODE("startTimeTC"),

    START_TIME_EDIT_UNIT("startTimeEU"),

    DURATION_TIMECODE("durationTC"),

    DURATION_EDIT_UNIT("durationEU");

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
