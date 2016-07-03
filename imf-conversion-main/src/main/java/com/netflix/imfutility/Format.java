package com.netflix.imfutility;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Supported destination formats.
 */
public enum Format implements IFormat {

    dpp("dpp");

    private final String name;

    Format(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static String getSupportedFormats() {
        return Arrays.stream(Format.values())
                .map(Format::getName)
                .collect(Collectors.joining(" ", "[", "]"));
    }

}
