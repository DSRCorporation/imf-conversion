/**
 * Copyright (C) 2016 Netflix, Inc.
 *
 *     This file is part of IMF Conversion Utility.
 *
 *     IMF Conversion Utility is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     IMF Conversion Utility is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with IMF Conversion Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
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
