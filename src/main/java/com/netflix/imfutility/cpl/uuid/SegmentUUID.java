package com.netflix.imfutility.cpl.uuid;

/**
 * Segment UUID.
 */
public class SegmentUUID extends UUID {

    public static SegmentUUID create(String uuid) {
        return new SegmentUUID(uuid);
    }

    protected SegmentUUID(String uuid) {
        super(uuid);
    }
}
