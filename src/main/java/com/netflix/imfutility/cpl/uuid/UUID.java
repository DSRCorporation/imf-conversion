package com.netflix.imfutility.cpl.uuid;

/**
 * Base UUI representation.
 */
public class UUID {

    private final String uuid;

    public static UUID create(String uuid) {
        return new UUID(uuid);
    }

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
