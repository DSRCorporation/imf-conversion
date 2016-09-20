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
package com.netflix.subtitles;

import com.netflix.subtitles.util.SplitUtils;
import com.netflix.subtitles.util.SplitUtils.Slice;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static junit.framework.TestCase.assertEquals;


/**
 * Tests that overlapped intervals splits correctly.
 * (see {@link SplitUtils}).
 */
public class SplitUtilsTest {

    @Test
    public void testStuckedSlices() {
        List<Slice<String>> input = Arrays.asList(
                new Slice<>(1, 2, "I"),
                new Slice<>(2, 4, "II"),
                new Slice<>(4, 5, "III"));

        List<Slice<String>> slices = SplitUtils.split(input);

        Iterator<Slice<String>> iterator = slices.iterator();
        assertEquals(3, slices.size());
        assertEquals("[1,2] {I}", iterator.next().toString());
        assertEquals("[2,4] {II}", iterator.next().toString());
        assertEquals("[4,5] {III}", iterator.next().toString());
    }

    @Test
    public void testOverlappedSlices() {
        List<Slice<String>> input = Arrays.asList(
                new Slice<>(1, 3, "I"),
                new Slice<>(2, 4, "II"),
                new Slice<>(3, 5, "III"));

        List<Slice<String>> slices = SplitUtils.split(input);

        Iterator<Slice<String>> iterator = slices.iterator();
        assertEquals(4, slices.size());
        assertEquals("[1,2] {I}", iterator.next().toString());
        assertEquals("[2,3] {I,II}", iterator.next().toString());
        assertEquals("[3,4] {II,III}", iterator.next().toString());
        assertEquals("[4,5] {III}", iterator.next().toString());
    }

    @Test
    public void testParallelSlices() {
        List<Slice<String>> input = Arrays.asList(
                new Slice<>(1, 2, "I"),
                new Slice<>(1, 2, "II"));

        List<Slice<String>> slices = SplitUtils.split(input);

        assertEquals(1, slices.size());
        assertEquals("[1,2] {I,II}", slices.get(0).toString());
    }

    @Test
    public void testGapBetweenSlices() {
        List<Slice<String>> input = Arrays.asList(
                new Slice<>(1, 2, "I"),
                new Slice<>(5, 6, "II"));

        List<Slice<String>> slices = SplitUtils.split(input);

        Iterator<Slice<String>> iterator = slices.iterator();
        assertEquals(2, slices.size());
        assertEquals("[1,2] {I}", iterator.next().toString());
        assertEquals("[5,6] {II}", iterator.next().toString());
    }

    @Test
    public void testNestedSlices() {
        List<Slice<String>> input = Arrays.asList(
                new Slice<>(1, 6, "I"),
                new Slice<>(3, 4, "II"),
                new Slice<>(2, 5, "III"));

        List<Slice<String>> slices = SplitUtils.split(input);

        Iterator<Slice<String>> iterator = slices.iterator();
        assertEquals(5, slices.size());
        assertEquals("[1,2] {I}", iterator.next().toString());
        assertEquals("[2,3] {I,III}", iterator.next().toString());
        assertEquals("[3,4] {I,II,III}", iterator.next().toString());
        assertEquals("[4,5] {I,III}", iterator.next().toString());
        assertEquals("[5,6] {I}", iterator.next().toString());
    }

    @Test
    public void testCompleteCase() {
        List<Slice<String>> input = Arrays.asList(
                new Slice<>(1, 2, "I"),
                new Slice<>(1, 2, "II"),
                new Slice<>(3, 5, "III"),
                new Slice<>(4, 8, "IV"),
                new Slice<>(6, 9, "V"),
                new Slice<>(7, 11, "VI"),
                new Slice<>(7, 9, "VII"),
                new Slice<>(8, 10, "VIII"),
                new Slice<>(12, 14, "IX"),
                new Slice<>(12, 13, "X"));

        List<Slice<String>> slices = SplitUtils.split(input);

        Iterator<Slice<String>> iterator = slices.iterator();
        assertEquals(11, slices.size());
        assertEquals("[1,2] {I,II}", iterator.next().toString());
        assertEquals("[3,4] {III}", iterator.next().toString());
        assertEquals("[4,5] {III,IV}", iterator.next().toString());
        assertEquals("[5,6] {IV}", iterator.next().toString());
        assertEquals("[6,7] {IV,V}", iterator.next().toString());
        assertEquals("[7,8] {IV,V,VI,VII}", iterator.next().toString());
        assertEquals("[8,9] {V,VI,VII,VIII}", iterator.next().toString());
        assertEquals("[9,10] {VI,VIII}", iterator.next().toString());
        assertEquals("[10,11] {VI}", iterator.next().toString());
        assertEquals("[12,13] {IX,X}", iterator.next().toString());
        assertEquals("[13,14] {IX}", iterator.next().toString());
    }
}

