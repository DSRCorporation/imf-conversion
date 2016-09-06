/*
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
package com.netflix.imfutility.itunes;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Destination iTunes itmsp package type.
 */
public enum ITunesPackageType {
    film("film"),
    tv("tv");

    private final String name;

    ITunesPackageType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ITunesPackageType fromName(String name) {
        return Stream.of(values())
                .filter(value -> Objects.equals(value.getName(), name))
                .findFirst()
                .orElse(null);
    }

    public static String getSupportedPackageTypes() {
        return Stream.of(values())
                .map(ITunesPackageType::getName)
                .collect(Collectors.joining(" ", "[", "]"));
    }
}
