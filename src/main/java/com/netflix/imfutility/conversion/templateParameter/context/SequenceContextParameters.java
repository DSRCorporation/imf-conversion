package com.netflix.imfutility.conversion.templateParameter.context;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * All supported segment template parameter names.
 */
public enum SequenceContextParameters {

    NUM("num"),

    TYPE("type");

    private final String name;

    private SequenceContextParameters(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SequenceContextParameters fromName(String name) {
        for (SequenceContextParameters e : values()) {
            if (e.getName().equals(name)) {
                return e;
            }
        }
        return null;
    }

    public static String getSupportedContextParameters() {
        return Arrays.stream(SequenceContextParameters.values())
                .map(SequenceContextParameters::getName)
                .collect(Collectors.joining(" ", "[", "]"));
    }

}
