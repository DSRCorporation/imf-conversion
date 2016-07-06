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
 * Sequence (virtual track) types as specified in CPL.
 */
public enum SequenceTypeCpl {

    AUDIO("MainAudioSequence"),

    IMAGE("MainImageSequence"),

    SUBTITLE("SubtitlesSequence");

    private final String name;

    SequenceTypeCpl(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SequenceTypeCpl fromName(String name) {
        for (SequenceTypeCpl e : values()) {
            if (e.getName().equals(name)) {
                return e;
            }
        }
        return null;
    }

    public static String getSupportedTypes() {
        return Arrays.stream(SequenceTypeCpl.values())
                .map(SequenceTypeCpl::getName)
                .collect(Collectors.joining(" ", "[", "]"));
    }

    public com.netflix.imfutility.generated.conversion.SequenceType toSequenceType() {
        switch (this) {
            case AUDIO:
                return com.netflix.imfutility.generated.conversion.SequenceType.AUDIO;
            case IMAGE:
                return com.netflix.imfutility.generated.conversion.SequenceType.VIDEO;
            case SUBTITLE:
                return com.netflix.imfutility.generated.conversion.SequenceType.SUBTITLE;
        }
        return null;
    }
}
