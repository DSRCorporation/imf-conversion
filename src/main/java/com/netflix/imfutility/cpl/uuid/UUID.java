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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UUID uuid1 = (UUID) o;

        return uuid != null ? uuid.equals(uuid1.uuid) : uuid1.uuid == null;

    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }
}
