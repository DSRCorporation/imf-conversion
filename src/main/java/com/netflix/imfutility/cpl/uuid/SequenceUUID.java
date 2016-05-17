package com.netflix.imfutility.cpl.uuid;

/**
 * Sequence UUID.
 */
public class SequenceUUID extends UUID {

    public static SequenceUUID create(String uuid) {
        return new SequenceUUID(uuid);
    }

    protected SequenceUUID(String uuid) {
        super(uuid);
    }
}
