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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a fix for fr.noop.charset implementation.
 * <ul>
 *     <li>It matches $ sign correctly (not at 0x24, but at 0xa4 as defined by ISO 6937/2-1983, Addendum 1-1989).</li>
 *     <li>Adds some missing matches.</li>
 *     <li>Checks whether the encoded byte is in the allowed range.</li>
 * </ul>
 */
public class Iso6937Helper {

    private static Map<Integer, Integer> encodingMapping = new HashMap();
    private static List<Integer> emptyMapping = new ArrayList<>();

    static {
        add(0x24, 0xa4);
        add(0xa8, 0x24);
        add(0xa4, 0x24);
        add(0xaf, 0xc5);
        add(0xb4, 0xc2);
        add(0xb8, 0xc8);
        add(0xba, 0xeb);
    }

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
    }

    private static void add(int x, int y) {
        encodingMapping.put(x, y);
    }

    private static void addEmpty(int x) {
        emptyMapping.add(x);
    }

    public Byte fixIso6937(byte ch) {
        int chInt = (ch & 0xff);
        if (encodingMapping.containsKey(chInt)) {
            chInt = encodingMapping.get(chInt);
            ch = (byte) chInt;
        }

        if (emptyMapping.contains(chInt)) {
            return null;
        }

        return ch;
    }

}
