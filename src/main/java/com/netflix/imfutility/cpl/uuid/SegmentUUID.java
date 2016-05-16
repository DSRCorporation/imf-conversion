package com.netflix.imfutility.cpl.uuid;

/**
 * Created by Alexander on 5/16/2016.
 */
public class SegmentUUID extends UUID {

    public static SegmentUUID create(String uuid) {
        return new SegmentUUID(uuid);
    }

    protected SegmentUUID(String uuid) {
        super(uuid);
    }
}
