package com.netflix.imfutility.conversion.templateParameter.context.parameters;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Pre-defined output template parameter names (it's possible to define another ones)
 */
public enum OutputContextParameters {

    MEDIA_INFO_INPUT("mediaInfoInput");

    private final String name;

    OutputContextParameters(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static OutputContextParameters fromName(String name) {
        for (OutputContextParameters e : values()) {
            if (e.getName().equals(name)) {
                return e;
            }
        }
        return null;
    }

    public static String getSupportedContextParameters() {
        return Arrays.stream(OutputContextParameters.values())
                .map(OutputContextParameters::getName)
                .collect(Collectors.joining(" ", "[", "]"));
    }

}
