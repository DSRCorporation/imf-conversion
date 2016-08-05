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
package com.netflix.imfutility.ttmltostl.stl;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks whether the encoded byte is in the allowed range.
 */
public class Iso6937Helper {

    private static final List<Integer> EMPTY_MAPPING = new ArrayList<>();

    static {
        for (int i = 0x00; i <= 0x1f; i++) {
            addEmpty(i);
        }
        addEmpty(0x7f);
        for (int i = 0x80; i <= 0x9f; i++) {
            addEmpty(i);
        }
        addEmpty(0xa6);
        addEmpty(0xa8);
        addEmpty(0xc0);
        addEmpty(0xc9);
        addEmpty(0xd8);
        addEmpty(0xd9);
        addEmpty(0xda);
        addEmpty(0xdb);
        addEmpty(0xe5);
    }

    private static void addEmpty(int x) {
        EMPTY_MAPPING.add(x);
    }

    public Byte fixIso6937(byte ch) {
        int chInt = (ch & 0xff);

        if (EMPTY_MAPPING.contains(chInt)) {
            return null;
        }

        return ch;
    }

}
