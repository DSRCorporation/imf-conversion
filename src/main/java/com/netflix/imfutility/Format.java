package com.netflix.imfutility;

/**
 * Supported destination formats.
 */
public enum Format {

    DPP("dpp");

    private final String name;

    Format(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
