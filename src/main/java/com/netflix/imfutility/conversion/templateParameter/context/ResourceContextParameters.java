package com.netflix.imfutility.conversion.templateParameter.context;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * All supported segment template parameter names.
 */
public enum ResourceContextParameters {

    NUM("num"),

    ESSENCE("essence"),

    START_TIME("startTime"),

    DURATION("duration");

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
