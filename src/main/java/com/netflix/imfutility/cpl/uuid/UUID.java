package com.netflix.imfutility.cpl.uuid;

/**
 * Created by Alexander on 5/16/2016.
 */
public abstract class UUID {

    private final String uuid;

    protected UUID(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return uuid;
    }
}
