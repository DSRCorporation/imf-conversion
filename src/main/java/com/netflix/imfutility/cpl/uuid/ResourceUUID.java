package com.netflix.imfutility.cpl.uuid;

/**
 * Resource UUID.
 */
public class ResourceUUID extends UUID {

    public static ResourceUUID create(String uuid) {
        return new ResourceUUID(uuid);
    }

    protected ResourceUUID(String uuid) {
        super(uuid);
    }
}
