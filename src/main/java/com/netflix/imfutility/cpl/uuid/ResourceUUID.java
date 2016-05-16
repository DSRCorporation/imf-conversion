package com.netflix.imfutility.cpl.uuid;

/**
 * Created by Alexander on 5/16/2016.
 */
public class ResourceUUID extends UUID {

    public static ResourceUUID create(String uuid) {
        return new ResourceUUID(uuid);
    }

    protected ResourceUUID(String uuid) {
        super(uuid);
    }
}
