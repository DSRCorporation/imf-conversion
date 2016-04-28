package com.netflix.imfutility.conversion.templateParameter.context.segment;

/**
 * Created by Alexander on 4/27/2016.
 */
public enum SegmentContextParameters {

    ESSENCE("essence"),

    START_TIME("startTime"),

    DURATION("duration");

    private String name;

    private SegmentContextParameters(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SegmentContextParameters fromName(String name) {
        for (SegmentContextParameters e : values()) {
            if (e.getName().equals(name)) {
                return e;
            }
        }
        return null;
    }

}
