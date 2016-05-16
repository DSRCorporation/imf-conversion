package com.netflix.imfutility.cpl.uuid;

/**
 * Created by Alexander on 5/16/2016.
 */
public class SequenceUUID extends UUID {

    public static SequenceUUID create(String uuid) {
        return new SequenceUUID(uuid);
    }

    protected SequenceUUID(String uuid) {
        super(uuid);
    }
}
