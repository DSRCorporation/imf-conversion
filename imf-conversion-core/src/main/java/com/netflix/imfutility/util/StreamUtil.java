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
package com.netflix.imfutility.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedHashMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Extends java stream utility methods.
 */
public final class StreamUtil {
    /**
     * Creates linked hash map from simple entries.
     *
     * @param <K> key type
     * @param <U> value type
     * @param entries entries array
     * @return linked hash map from simple entries
     */
    public static <K, U> LinkedHashMap<K, U> createLinkedMap(SimpleEntry<K, U>... entries) {
        return Stream.of(entries).collect(entryToLinkedMap());
    }

    /**
     * Gets collector to linked hash map.
     *
     * @param <T> type of input elements
     * @param <K> key type
     * @param <U> value type
     * @return collector to linked hash map
     */
    public static <T, K, U> Collector<SimpleEntry<K, U>, ?, LinkedHashMap<K, U>> entryToLinkedMap() {

        return Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue(),
                (u, v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                },
                LinkedHashMap::new);
    }

    /**
     * Constructor.
     */
    private StreamUtil() {
    }
}
