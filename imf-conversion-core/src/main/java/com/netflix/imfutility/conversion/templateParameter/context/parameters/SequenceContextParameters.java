package com.netflix.imfutility.conversion.templateParameter.context.parameters;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * All supported sequence (virtual track) template parameter names.
 */
public enum SequenceContextParameters {

    // common
    UUID("uuid"),
    NUM("num"),
    TYPE("type"),

    // audio
    CHANNELS_NUM("channels_num"),
    BITS_PER_SAMPLE("bits_per_sample"),
    SAMPLE_RATE("sample_rate"),

    // video
    WIDTH("width"),
    HEIGHT("height"),
    BIT_DEPTH("bit_depth"),
    PIXEL_FORMAT("pixel_format"),
    FRAME_RATE("frame_rate");


    private final String name;

    SequenceContextParameters(String name) {
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
