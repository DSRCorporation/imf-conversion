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
package com.netflix.imfutility.conversion.templateParameter.context.parameters;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * All supported sequence (virtual track) template parameter names.
 */
public enum SequenceContextParameters {

    // common
    UUID("uuid"),
    NUM("num"),
    TYPE("type"),
    LANGUAGE("language"),

    // we assume that all resources within audio sequence has the same number of channels
    CHANNELS_NUM("channels_num"),
    // we assume that all resources within video sequence has the same fps
    FRAME_RATE("frame_rate");

    private final String name;

    SequenceContextParameters(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SequenceContextParameters fromName(String name) {
        for (SequenceContextParameters e : values()) {
            if (e.getName().equals(name)) {
                return e;
            }
        }
        return null;
    }

    public static String getSupportedContextParameters() {
        return Arrays.stream(SequenceContextParameters.values())
                .map(SequenceContextParameters::getName)
                .collect(Collectors.joining(" ", "[", "]"));
    }

}
