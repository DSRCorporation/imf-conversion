package com.netflix.imfutility.conversion.templateParameter.context.parameters;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Pre-defined dynamic template parameter names (it's possible to define another ones).
 */
public enum DynamicContextParameters {

    MEDIA_INFO_INPUT("mediaInfoInput"),

    MEDIA_INFO_OUTPUT("mediaInfoOutput");

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
