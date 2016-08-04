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
package com.netflix.imfutility.ttmltostl.stl;

import com.netflix.imfutility.ttmltostl.ttml.Style;
import com.netflix.imfutility.ttmltostl.ttml.TimedTextObject;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertArrayEquals;

/**
 * Tests building of STL TTI blocks.
 */
public class StlTtiTest {

    @Test
    public void testSimpleTti() throws Exception {
        TimedTextObject tto = StlTestUtil.buildTto(
                "00:00:00:00", "00:00:05:00", "text1",
                "00:00:05:00", "00:00:10:12", "text2",
                "00:04:59:00", "23:59:59:24", "text3"
        );
        byte[][] stl = StlTestUtil.build(tto, StlTestUtil.getMetadataXml());
        byte[] tti = stl[1];

        // 1st block: information
        assertArrayEquals(
                new byte[]{
                        0x00, // group number - 0
                        0x00, 0x00, // subtitle number - 0
                        (byte) 0xff, // extension block - default
                        0x00, // cumulative status - 00 (no cumulative)
                        0x00, 0x00, 0x00, 0x00, // code in: 00:00:00:00
                        0x00, 0x00, 0x05, 0x00, // code out: 00:00:05:00
                        0x16, // vertical position
                        0x02, // centered by default
                        0x00, // comment - 00 (contains subtitle)
                },
                Arrays.copyOfRange(tti, 0, 16));

        // 1st block: text
        assertArrayEquals(
                fillExpectedText(new byte[]{0x74, 0x65, 0x78, 0x74, 0x31}),
                Arrays.copyOfRange(tti, 16, 128));

        // 2d block: information
        int offset = 128;
        assertArrayEquals(
                new byte[]{
                        0x00, // group number - 0
                        0x01, 0x00, // subtitle number - 1
                        (byte) 0xff, // extension block - default
                        0x00, // cumulative status - 00 (no cumulative)
                        0x00, 0x00, 0x05, 0x00, // code in: 00:00:05:00
                        0x00, 0x00, 0x0a, 0x0c, // code out: 00:00:10:12
                        0x16, // vertical position
                        0x02, // centered by default
                        0x00, // comment - 00 (contains subtitle)
                },
                Arrays.copyOfRange(tti, offset, offset + 16));

        // 2d block: text
        assertArrayEquals(
                fillExpectedText(new byte[]{0x74, 0x65, 0x78, 0x74, 0x32}),
                Arrays.copyOfRange(tti, offset + 16, offset + 128));

        // 3d block: information
        offset += 128;
        assertArrayEquals(
                new byte[]{
                        0x00, // group number - 0
                        0x02, 0x00, // subtitle number - 2
                        (byte) 0xff, // extension block - default
                        0x00, // cumulative status - 00 (no cumulative)
                        0x00, 0x04, 0x3b, 0x00, // code in: 00:04:59:00
                        0x17, 0x3b, 0x3b, 0x18, // code out: 23:59:59:24
                        0x16, // vertical position
                        0x02, // centered by default
                        0x00, // comment - 00 (contains subtitle)
                },
                Arrays.copyOfRange(tti, offset, offset + 16));

        // 3d block: text
        assertArrayEquals(
                fillExpectedText(new byte[]{0x74, 0x65, 0x78, 0x74, 0x33}),
                Arrays.copyOfRange(tti, offset + 16, offset + 128));
    }

    @Test
    public void testEbnBlocksForLongSubtitle() throws Exception {
        // prepare long subtitles, so that one subtitles is stored in two tti blocks
        TimedTextObject tto = StlTestUtil.buildTto(
                "00:00:00:00", "00:00:05:00", StringUtils.rightPad("", 200, '1'), // in 2 tti
                "00:00:10:00", "00:01:10:00", StringUtils.rightPad("", 400, '2') // in 4 tti
        );
        byte[][] stl = StlTestUtil.build(tto, StlTestUtil.getMetadataXml());
        byte[] tti = stl[1];

        // 1st subtitle 1st block
        assertArrayEquals(
                new byte[]{
                        0x00, 0x00, // subtitle number - 0
                        (byte) 0x00, // extension block - 1st
                        0x00, // cumulative status - 00 (no cumulative)
                        0x00, 0x00, 0x00, 0x00, // code in: 00:00:00:00
                        0x00, 0x00, 0x05, 0x00, // code out: 00:00:05:00
                        0x16 // vertical position
                },
                Arrays.copyOfRange(tti, 1, 14));

        // 1st subtitle 2d block
        int offset = 128;
        assertArrayEquals(
                new byte[]{
                        0x00, 0x00, // subtitle number - 0
                        (byte) 0xff, // extension block - last
                        0x00, // cumulative status - 00 (no cumulative)
                        0x00, 0x00, 0x00, 0x00, // code in: 00:00:00:00
                        0x00, 0x00, 0x05, 0x00, // code out: 00:00:05:00
                        0x16 // vertical position
                },
                Arrays.copyOfRange(tti, offset + 1, offset + 14));

        // 2d subtitle 1st block
        offset += 128;
        assertArrayEquals(
                new byte[]{
                        0x01, 0x00, // subtitle number - 0
                        (byte) 0x00, // extension block - 1st
                        0x00, // cumulative status - 00 (no cumulative)
                        0x00, 0x00, 0x0a, 0x00, // code in: 00:00:10:00
                        0x00, 0x01, 0x0a, 0x00, // code out: 00:01:10:00
                        0x16 // vertical position
                },
                Arrays.copyOfRange(tti, offset + 1, offset + 14));

        // 2d subtitle 2d block
        offset += 128;
        assertArrayEquals(
                new byte[]{
                        0x01, 0x00, // subtitle number - 0
                        (byte) 0x01, // extension block - 2d
                        0x00, // cumulative status - 00 (no cumulative)
                        0x00, 0x00, 0x0a, 0x00, // code in: 00:00:10:00
                        0x00, 0x01, 0x0a, 0x00, // code out: 00:01:10:00
                        0x16 // vertical position
                },
                Arrays.copyOfRange(tti, offset + 1, offset + 14));

        // 2d subtitle 3d block
        offset += 128;
        assertArrayEquals(
                new byte[]{
                        0x01, 0x00, // subtitle number - 0
                        (byte) 0x02, // extension block - 3d
                        0x00, // cumulative status - 00 (no cumulative)
                        0x00, 0x00, 0x0a, 0x00, // code in: 00:00:10:00
                        0x00, 0x01, 0x0a, 0x00, // code out: 00:01:10:00
                        0x16 // vertical position
                },
                Arrays.copyOfRange(tti, offset + 1, offset + 14));

        // 2d subtitle 4th block
        offset += 128;
        assertArrayEquals(
                new byte[]{
                        0x01, 0x00, // subtitle number - 0
                        (byte) 0xff, // extension block - last
                        0x00, // cumulative status - 00 (no cumulative)
                        0x00, 0x00, 0x0a, 0x00, // code in: 00:00:10:00
                        0x00, 0x01, 0x0a, 0x00, // code out: 00:01:10:00
                        0x16 // vertical position
                },
                Arrays.copyOfRange(tti, offset + 1, offset + 14));
    }

    /**
     * Each text must end with 0x8f!
     *
     * @throws Exception
     */
    @Test
    public void testTextEndsWith8H() throws Exception {
        // prepare long subtitles, so that one subtitles is stored in two tti blocks
        TimedTextObject tto = StlTestUtil.buildTto(
                "00:00:00:00", "00:00:05:00", StringUtils.rightPad("", 111, '1'), // 1 block
                "00:00:05:00", "00:00:10:00", StringUtils.rightPad("", 112, '2'), // 2 blocks
                "00:00:10:00", "00:00:15:00", StringUtils.rightPad("", 222, '3'), // 2 blocks
                "00:00:15:00", "00:00:20:00", StringUtils.rightPad("", 223, '4') // 3 blocks
        );
        byte[][] stl = StlTestUtil.build(tto, StlTestUtil.getMetadataXml());
        byte[] tti = stl[1];

        // 1st subtitle  - 1 block
        byte[] textWithPadding = new byte[112];
        Arrays.fill(textWithPadding, (byte) 0x31);
        textWithPadding[111] = (byte) 0x8f;
        assertArrayEquals(
                new byte[]{
                        0x00, 0x00, // subtitle number - 0
                        (byte) 0xff, // extension block - last
                },
                Arrays.copyOfRange(tti, 1, 4));
        assertArrayEquals(
                textWithPadding,
                Arrays.copyOfRange(tti, 16, 128));

        // 2d subtitle  - 2 blocks
        int offset = 128;
        textWithPadding = new byte[112];
        Arrays.fill(textWithPadding, (byte) 0x32);
        textWithPadding[111] = (byte) 0x8f;
        assertArrayEquals(
                new byte[]{
                        0x01, 0x00, // subtitle number - 1
                        (byte) 0x00, // extension block - 1st
                },
                Arrays.copyOfRange(tti, offset + 1, offset + 4));
        assertArrayEquals(
                textWithPadding,
                Arrays.copyOfRange(tti, offset + 16, offset + 128));

        offset += 128;
        textWithPadding = new byte[112];
        Arrays.fill(textWithPadding, (byte) 0x8f);
        textWithPadding[0] = (byte) 0x32;
        assertArrayEquals(
                new byte[]{
                        0x01, 0x00, // subtitle number - 1
                        (byte) 0xff, // extension block - last
                },
                Arrays.copyOfRange(tti, offset + 1, offset + 4));
        assertArrayEquals(
                textWithPadding,
                Arrays.copyOfRange(tti, offset + 16, offset + 128));

        // 3d subtitle  - 2 blocks
        offset += 128;
        textWithPadding = new byte[112];
        Arrays.fill(textWithPadding, (byte) 0x33);
        textWithPadding[111] = (byte) 0x8f;
        assertArrayEquals(
                new byte[]{
                        0x02, 0x00, // subtitle number - 2
                        (byte) 0x00, // extension block - 1st
                },
                Arrays.copyOfRange(tti, offset + 1, offset + 4));
        assertArrayEquals(
                textWithPadding,
                Arrays.copyOfRange(tti, offset + 16, offset + 128));

        offset += 128;
        assertArrayEquals(
                new byte[]{
                        0x02, 0x00, // subtitle number - 2
                        (byte) 0xff, // extension block - last
                },
                Arrays.copyOfRange(tti, offset + 1, offset + 4));
        assertArrayEquals(
                textWithPadding,
                Arrays.copyOfRange(tti, offset + 16, offset + 128));

        // 4th subtitle  - 3 blocks
        offset += 128;
        textWithPadding = new byte[112];
        Arrays.fill(textWithPadding, (byte) 0x34);
        textWithPadding[111] = (byte) 0x8f;
        assertArrayEquals(
                new byte[]{
                        0x03, 0x00, // subtitle number - 3
                        (byte) 0x00, // extension block - 1st
                },
                Arrays.copyOfRange(tti, offset + 1, offset + 4));
        assertArrayEquals(
                textWithPadding,
                Arrays.copyOfRange(tti, offset + 16, offset + 128));

        offset += 128;
        assertArrayEquals(
                new byte[]{
                        0x03, 0x00, // subtitle number - 3
                        (byte) 0x01, // extension block - last
                },
                Arrays.copyOfRange(tti, offset + 1, offset + 4));
        assertArrayEquals(
                textWithPadding,
                Arrays.copyOfRange(tti, offset + 16, offset + 128));

        offset += 128;
        textWithPadding = new byte[112];
        Arrays.fill(textWithPadding, (byte) 0x8f);
        textWithPadding[0] = (byte) 0x34;
        assertArrayEquals(
                new byte[]{
                        0x03, 0x00, // subtitle number - 3
                        (byte) 0xff, // extension block - last
                },
                Arrays.copyOfRange(tti, offset + 1, offset + 4));
        assertArrayEquals(
                textWithPadding,
                Arrays.copyOfRange(tti, offset + 16, offset + 128));

    }

    /**
     * Each line ends with 0x8a.
     * Vertical position is calculated correctly.
     *
     * @throws Exception
     */
    @Test
    public void testMultilineText() throws Exception {
        TimedTextObject tto = StlTestUtil.buildTto(
                "00:00:00:00", "00:00:05:00", "line1\n2\n3\n\n\n4"
        );
        byte[][] stl = StlTestUtil.build(tto, StlTestUtil.getMetadataXml());
        byte[] tti = stl[1];

        assertArrayEquals(
                new byte[]{
                        0x00, 0x00, // subtitle number - 0
                        (byte) 0xff, // extension block - default
                        0x00, // cumulative status - 00 (no)
                        0x00, 0x00, 0x00, 0x00, // code in: 00:00:00:00
                        0x00, 0x00, 0x05, 0x00, // code out: 00:00:05:00
                        0x0c // vertical position
                },
                Arrays.copyOfRange(tti, 1, 14));

        assertArrayEquals(
                fillExpectedText(
                        new byte[]{
                                0x6c, 0x69, 0x6e, 0x65, 0x31, (byte) 0x8a,
                                0x32, (byte) 0x8a,
                                0x33, (byte) 0x8a,
                                (byte) 0x8a,
                                (byte) 0x8a,
                                0x34}
                ),
                Arrays.copyOfRange(tti, 16, 128));
    }

    /**
     * It doesn't fail. Sets vertical position to 0.
     *
     * @throws Exception
     */
    @Test
    public void testMultilineTextWithMoreThanMNR() throws Exception {
        TimedTextObject tto = StlTestUtil.buildTto(
                "00:00:00:00", "00:00:05:00", "1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n11\n12\n13\n14" // 14 > 11
        );
        byte[][] stl = StlTestUtil.build(tto, StlTestUtil.getMetadataXml());
        byte[] tti = stl[1];

        assertArrayEquals(
                new byte[]{
                        0x00, 0x00, // subtitle number - 0
                        (byte) 0xff, // extension block - default
                        0x00, // cumulative status - 00 (no)
                        0x00, 0x00, 0x00, 0x00, // code in: 00:00:00:00
                        0x00, 0x00, 0x05, 0x00, // code out: 00:00:05:00
                        0x02 // vertical position (min value!)
                },
                Arrays.copyOfRange(tti, 1, 14));

        assertArrayEquals(
                fillExpectedText(
                        new byte[]{
                                0x31, (byte) 0x8a,
                                0x32, (byte) 0x8a,
                                0x33, (byte) 0x8a,
                                0x34, (byte) 0x8a,
                                0x35, (byte) 0x8a,
                                0x36, (byte) 0x8a,
                                0x37, (byte) 0x8a,
                                0x38, (byte) 0x8a,
                                0x39, (byte) 0x8a,
                                0x31, 0x30, (byte) 0x8a,
                                0x31, 0x31, (byte) 0x8a,
                                0x31, 0x32, (byte) 0x8a,
                                0x31, 0x33, (byte) 0x8a,
                                0x31, 0x34
                        }
                ),
                Arrays.copyOfRange(tti, 16, 128));
    }

    /**
     * Two subtitles with the same interval are assumed to be cumulative.
     *
     * @throws Exception
     */
    @Test
    public void testCumulativeSubtitleSameTime() throws Exception {
        TimedTextObject tto = StlTestUtil.buildTto(
                "00:00:00:00", "00:00:05:00", "text1",
                "00:00:00:00", "00:00:05:00", "text2"
        );
        byte[][] stl = StlTestUtil.build(tto, StlTestUtil.getMetadataXml());
        byte[] tti = stl[1];

        assertArrayEquals(
                new byte[]{
                        0x00, 0x00, // subtitle number - 0
                        (byte) 0xff, // extension block - default
                        0x01, // cumulative status - 01 (first)
                        0x00, 0x00, 0x00, 0x00, // code in: 00:00:00:00
                        0x00, 0x00, 0x05, 0x00, // code out: 00:00:05:00
                        0x14 // vertical position
                },
                Arrays.copyOfRange(tti, 1, 14));

        int offset = 128;
        assertArrayEquals(
                new byte[]{
                        0x01, 0x00, // subtitle number - 1
                        (byte) 0xff, // extension block - default
                        0x03, // cumulative status - 03 (last)
                        0x00, 0x00, 0x00, 0x00, // code in: 00:00:00:00
                        0x00, 0x00, 0x05, 0x00, // code out: 00:00:05:00
                        0x16 // vertical position
                },
                Arrays.copyOfRange(tti, offset + 1, offset + 14));
    }

    /**
     * Classical case of cumulative subtitles (equal end time).
     *
     * @throws Exception
     */
    @Test
    public void testCumulativeSubtitleEqualEndTime() throws Exception {
        TimedTextObject tto = StlTestUtil.buildTto(
                "00:00:10:00", "00:00:20:00", "text3",
                "00:00:14:00", "00:00:20:00", "text4",
                "00:00:18:00", "00:00:20:00", "text4"
        );
        byte[][] stl = StlTestUtil.build(tto, StlTestUtil.getMetadataXml());
        byte[] tti = stl[1];

        assertArrayEquals(
                new byte[]{
                        0x00, 0x00, // subtitle number - 0
                        (byte) 0xff, // extension block - default
                        0x01, // cumulative status - 01 (first)
                        0x00, 0x00, 0x0a, 0x00, // code in: 00:00:10:00
                        0x00, 0x00, 0x14, 0x00, // code out: 00:00:20:00
                        0x12 // vertical position
                },
                Arrays.copyOfRange(tti, 1, 14));

        int offset = 128;
        assertArrayEquals(
                new byte[]{
                        0x01, 0x00, // subtitle number - 1
                        (byte) 0xff, // extension block - default
                        0x02, // cumulative status - 02 (intermediate)
                        0x00, 0x00, 0x0e, 0x00, // code in: 00:00:14:00
                        0x00, 0x00, 0x14, 0x00, // code out: 00:00:20:00
                        0x14 // vertical position
                },
                Arrays.copyOfRange(tti, offset + 1, offset + 14));

        offset += 128;
        assertArrayEquals(
                new byte[]{
                        0x02, 0x00, // subtitle number - 2
                        (byte) 0xff, // extension block - default
                        0x03, // cumulative status - 03 (last)
                        0x00, 0x00, 0x12, 0x00, // code in: 00:00:18:00
                        0x00, 0x00, 0x14, 0x00, // code out: 00:00:20:00
                        0x16 // vertical position
                },
                Arrays.copyOfRange(tti, offset + 1, offset + 14));
    }

    /**
     * When two subtitles are intersected, then the first subtitle is made longer to match cumulative case.
     *
     * @throws Exception
     */
    @Test
    public void testTwoIntersectedSubtitlesMadeCumulative() throws Exception {
        TimedTextObject tto = StlTestUtil.buildTto(
                "00:00:00:00", "00:00:10:00", "text1",
                "00:00:05:00", "00:00:15:00", "text2"
        );
        byte[][] stl = StlTestUtil.build(tto, StlTestUtil.getMetadataXml());
        byte[] tti = stl[1];

        assertArrayEquals(
                new byte[]{
                        0x00, 0x00, // subtitle number - 0
                        (byte) 0xff, // extension block - default
                        0x01, // cumulative status - 01 (first)
                        0x00, 0x00, 0x00, 0x00, // code in: 00:00:00:00
                        0x00, 0x00, 0x0f, 0x00, // code out: 00:00:15:00, not 00:00:10:00!!!
                        0x14 // vertical position
                },
                Arrays.copyOfRange(tti, 1, 14));

        int offset = 128;
        assertArrayEquals(
                new byte[]{
                        0x01, 0x00, // subtitle number - 1
                        (byte) 0xff, // extension block - default
                        0x03, // cumulative status - 03 (last)
                        0x00, 0x00, 0x05, 0x00, // code in: 00:00:05:00
                        0x00, 0x00, 0x0f, 0x00, // code out: 00:00:15:00
                        0x16 // vertical position
                },
                Arrays.copyOfRange(tti, offset + 1, offset + 14));
    }

    /**
     * All subtitles are made cumulative and are shown until the last in the cumulative set.
     *
     * @throws Exception
     */
    @Test
    public void testManyIntersectedSubtitlesMadeCumulative() throws Exception {
        TimedTextObject tto = StlTestUtil.buildTto(
                "00:00:00:00", "00:00:10:00", "text1",
                "00:00:05:00", "00:00:15:00", "text2",
                "00:00:07:00", "00:00:20:00", "text3",
                "00:00:08:00", "00:00:20:00", "text4",
                "00:00:09:00", "00:00:22:00", "text5"
        );
        byte[][] stl = StlTestUtil.build(tto, StlTestUtil.getMetadataXml());
        byte[] tti = stl[1];

        assertArrayEquals(
                new byte[]{
                        0x00, 0x00, // subtitle number - 0
                        (byte) 0xff, // extension block - default
                        0x01, // cumulative status - 01 (first)
                        0x00, 0x00, 0x00, 0x00, // code in: 00:00:00:00
                        0x00, 0x00, 0x16, 0x00, // code out: 00:00:22:00, not 00:00:10:00!!!
                        0x0e // vertical position
                },
                Arrays.copyOfRange(tti, 1, 14));

        int offset = 128;
        assertArrayEquals(
                new byte[]{
                        0x01, 0x00, // subtitle number - 1
                        (byte) 0xff, // extension block - default
                        0x02, // cumulative status - 02 (intermediate)
                        0x00, 0x00, 0x05, 0x00, // code in: 00:00:05:00
                        0x00, 0x00, 0x16, 0x00, // code out: 00:00:22:00 not 00:00:15:00!!!
                        0x10 // vertical position
                },
                Arrays.copyOfRange(tti, offset + 1, offset + 14));

        offset += 128;
        assertArrayEquals(
                new byte[]{
                        0x02, 0x00, // subtitle number - 2
                        (byte) 0xff, // extension block - default
                        0x02, // cumulative status - 02 (intermediate)
                        0x00, 0x00, 0x07, 0x00, // code in: 00:00:07:00
                        0x00, 0x00, 0x16, 0x00, // code out: 00:00:22:00 not 00:00:20:00!!!
                        0x12 // vertical position
                },
                Arrays.copyOfRange(tti, offset + 1, offset + 14));

        offset += 128;
        assertArrayEquals(
                new byte[]{
                        0x03, 0x00, // subtitle number - 3
                        (byte) 0xff, // extension block - default
                        0x02, // cumulative status - 02 (intermediate)
                        0x00, 0x00, 0x08, 0x00, // code in: 00:00:08:00
                        0x00, 0x00, 0x16, 0x00, // code out: 00:00:22:00 not 00:00:20:00!!!
                        0x14 // vertical position
                },
                Arrays.copyOfRange(tti, offset + 1, offset + 14));

        offset += 128;
        assertArrayEquals(
                new byte[]{
                        0x04, 0x00, // subtitle number - 4
                        (byte) 0xff, // extension block - default
                        0x03, // cumulative status - 03 (last)
                        0x00, 0x00, 0x09, 0x00, // code in: 00:00:09:00
                        0x00, 0x00, 0x16, 0x00, // code out: 00:00:22:00
                        0x16 // vertical position
                },
                Arrays.copyOfRange(tti, offset + 1, offset + 14));
    }

    /**
     * When maximum number of lines (MNR parameter) is reached, then subtitles from the same cumulative set
     * are separated to two cumulative sets.
     *
     * @throws Exception
     */
    @Test
    public void testMultilineIntersectedSubtitlesCheckedAgainstMNR() throws Exception {
        TimedTextObject tto = StlTestUtil.buildTto(
                "00:00:00:00", "00:00:10:00", "1\n2\n3\n4\n5",
                "00:00:05:00", "00:00:15:00", "6\n7\n8\n9\n10\n11",
                "00:00:07:00", "00:00:22:00", "14\n15",
                "00:00:08:00", "00:00:22:00", "16\n17"
        );
        byte[][] stl = StlTestUtil.build(tto, StlTestUtil.getMetadataXml());
        byte[] tti = stl[1];

        assertArrayEquals(
                new byte[]{
                        0x00, 0x00, // subtitle number - 0
                        (byte) 0xff, // extension block - default
                        0x01, // cumulative status - 01 (first)
                        0x00, 0x00, 0x00, 0x00, // code in: 00:00:00:00
                        0x00, 0x00, 0x0f, 0x00, // code out: 00:00:15:00, not 00:00:10:00!!!
                        0x02 // vertical position
                },
                Arrays.copyOfRange(tti, 1, 14));

        int offset = 128;
        assertArrayEquals(
                new byte[]{
                        0x01, 0x00, // subtitle number - 1
                        (byte) 0xff, // extension block - default
                        0x03, // cumulative status - 03 (last)
                        0x00, 0x00, 0x05, 0x00, // code in: 00:00:05:00
                        0x00, 0x00, 0x0f, 0x00, // code out: 00:00:15:00
                        0x0c // vertical position
                },
                Arrays.copyOfRange(tti, offset + 1, offset + 14));

        offset += 128;
        assertArrayEquals(
                new byte[]{
                        0x02, 0x00, // subtitle number - 2
                        (byte) 0xff, // extension block - default
                        0x01, // cumulative status - 01 (first)
                        0x00, 0x00, 0x0f, 0x00, // code in: 00:00:15:00, not 00:00:07:00!!!
                        0x00, 0x00, 0x16, 0x00, // code out: 00:00:22:00
                        0x10 // vertical position
                },
                Arrays.copyOfRange(tti, offset + 1, offset + 14));

        offset += 128;
        assertArrayEquals(
                new byte[]{
                        0x03, 0x00, // subtitle number - 3
                        (byte) 0xff, // extension block - default
                        0x03, // cumulative status - 02 (last)
                        0x00, 0x00, 0x0f, 0x00, // code in: 00:00:15:00, not 00:00:08:00!!!
                        0x00, 0x00, 0x16, 0x00, // code out: 00:00:22:00
                        0x14 // vertical position
                },
                Arrays.copyOfRange(tti, offset + 1, offset + 14));
    }

    /**
     * Tests correct separation of cumulative set if frist subtitle has more than MNR (11) lines.
     *
     * @throws Exception
     */
    @Test
    public void testMultilineCumulativeFirstMoreThanMNR() throws Exception {
        TimedTextObject tto = StlTestUtil.buildTto(
                "00:00:00:00", "00:00:10:00", "1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n11\n12\n13",
                "00:00:05:00", "00:00:15:00", "6\n7\n8\n9\n10\n11"
        );
        byte[][] stl = StlTestUtil.build(tto, StlTestUtil.getMetadataXml());
        byte[] tti = stl[1];

        assertArrayEquals(
                new byte[]{
                        0x00, 0x00, // subtitle number - 0
                        (byte) 0xff, // extension block - default
                        0x00, // cumulative status - 00 (no)
                        0x00, 0x00, 0x00, 0x00, // code in: 00:00:00:00
                        0x00, 0x00, 0x0a, 0x00, // code out: 00:00:10:00, not 00:00:15:00!!!
                        0x02 // vertical position (min)
                },
                Arrays.copyOfRange(tti, 1, 14));

        int offset = 128;
        assertArrayEquals(
                new byte[]{
                        0x01, 0x00, // subtitle number - 1
                        (byte) 0xff, // extension block - default
                        0x00, // cumulative status - 00 (no)
                        0x00, 0x00, 0x0a, 0x00, // code in: 00:00:10:00, not 00:00:05:00!!!
                        0x00, 0x00, 0x0f, 0x00, // code out: 00:00:15:00
                        0x0c // vertical position
                },
                Arrays.copyOfRange(tti, offset + 1, offset + 14));
    }


    /**
     * Checks that text is aligned correctly
     *
     * @throws Exception
     */
    @Test
    public void testTextAllign() throws Exception {
        TimedTextObject tto = StlTestUtil.buildTto(
                "00:00:00:00", "00:00:05:00", "text1",
                "00:00:05:00", "00:00:10:12", "text2",
                "00:04:59:00", "23:59:59:24", "text3"
        );

        // set styles
        Style style1 = new Style("1");
        style1.setTextAlign("top-left");
        Style style2 = new Style("2");
        style2.setTextAlign("bottom-right");
        Style style3 = new Style("3");
        style3.setTextAlign("center");

        tto.getCaptions().get(0).setStyle(style1);
        tto.getCaptions().get(1).setStyle(style2);
        tto.getCaptions().get(2).setStyle(style3);

        byte[][] stl = StlTestUtil.build(tto, StlTestUtil.getMetadataXml());
        byte[] tti = stl[1];

        assertEquals(0x01, tti[14]); // 01 - left
        assertArrayEquals(
                fillExpectedText(new byte[]{0x74, 0x65, 0x78, 0x74, 0x31}),
                Arrays.copyOfRange(tti, 16, 128));

        int offset = 128;
        assertEquals(0x03, tti[offset + 14]); // 03 - right
        assertArrayEquals(
                fillExpectedText(new byte[]{0x74, 0x65, 0x78, 0x74, 0x32}),
                Arrays.copyOfRange(tti, offset + 16, offset + 128));

        offset += 128;
        assertEquals(0x02, tti[offset + 14]); // 02 - center
        assertArrayEquals(
                fillExpectedText(new byte[]{0x74, 0x65, 0x78, 0x74, 0x33}),
                Arrays.copyOfRange(tti, offset + 16, offset + 128));
    }

    private byte[] fillExpectedText(byte[] text) {
        byte[] textWithPadding = new byte[112];
        Arrays.fill(textWithPadding, (byte) 0x8f);
        System.arraycopy(text, 0, textWithPadding, 0, text.length);
        return textWithPadding;
    }

}
