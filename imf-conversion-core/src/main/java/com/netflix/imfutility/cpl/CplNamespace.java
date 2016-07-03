package com.netflix.imfutility.cpl;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Supported CPL namespaces.
 */
public enum CplNamespace {

    CPL_2013("http://www.smpte-ra.org/schemas/2067-3/2013"),

    CPL_2016("http://www.smpte-ra.org/schemas/2067-3/2016");

    private final String name;

    CplNamespace(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static CplNamespace fromName(String name) {
        for (CplNamespace e : values()) {
            if (e.getName().equals(name)) {
                return e;
            }
        }
        return null;
    }

    public static String getSupportedNamespaces() {
        return Arrays.stream(CplNamespace.values())
                .map(CplNamespace::getName)
                .collect(Collectors.joining(" ", "[", "]"));
    }

}
