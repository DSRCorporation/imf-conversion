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

import com.netflix.imfutility.ttmltostl.ttml.TimedTextObject;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.Arrays;

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
        byte[] textWithPadding = new byte[112];
        Arrays.fill(textWithPadding, (byte) 0x8f);
        byte[] text = new byte[]{0x74, 0x65, 0x78, 0x74, 0x31};
        System.arraycopy(text, 0, textWithPadding, 0, text.length);
        assertArrayEquals(
                textWithPadding,
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
        textWithPadding = new byte[112];
        Arrays.fill(textWithPadding, (byte) 0x8f);
        text = new byte[]{0x74, 0x65, 0x78, 0x74, 0x32};
        System.arraycopy(text, 0, textWithPadding, 0, text.length);
        assertArrayEquals(
                textWithPadding,
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
        textWithPadding = new byte[112];
        Arrays.fill(textWithPadding, (byte) 0x8f);
        text = new byte[]{0x74, 0x65, 0x78, 0x74, 0x33};
        System.arraycopy(text, 0, textWithPadding, 0, text.length);
        assertArrayEquals(
                textWithPadding,
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
                        0x00, // group number - 0
                        0x00, 0x00, // subtitle number - 0
                        (byte) 0x00, // extension block - 1st
                        0x00, // cumulative status - 00 (no cumulative)
                        0x00, 0x00, 0x00, 0x00, // code in: 00:00:00:00
                        0x00, 0x00, 0x05, 0x00, // code out: 00:00:05:00
                        0x16, // vertical position
                        0x02, // centered by default
                        0x00, // comment - 00 (contains subtitle)
                },
                Arrays.copyOfRange(tti, 0, 16));

        // 1st subtitle 2d block
        int offset = 128;
        assertArrayEquals(
                new byte[]{
                        0x00, // group number - 0
                        0x00, 0x00, // subtitle number - 0
                        (byte) 0xff, // extension block - last
                        0x00, // cumulative status - 00 (no cumulative)
                        0x00, 0x00, 0x00, 0x00, // code in: 00:00:00:00
                        0x00, 0x00, 0x05, 0x00, // code out: 00:00:05:00
                        0x16, // vertical position
                        0x02, // centered by default
                        0x00, // comment - 00 (contains subtitle)
                },
                Arrays.copyOfRange(tti, offset, offset + 16));

        // 2d subtitle 1st block
        offset += 128;
        assertArrayEquals(
                new byte[]{
                        0x00, // group number - 0
                        0x01, 0x00, // subtitle number - 0
                        (byte) 0x00, // extension block - 1st
                        0x00, // cumulative status - 00 (no cumulative)
                        0x00, 0x00, 0x0a, 0x00, // code in: 00:00:10:00
                        0x00, 0x01, 0x0a, 0x00, // code out: 00:01:10:00
                        0x16, // vertical position
                        0x02, // centered by default
                        0x00, // comment - 00 (contains subtitle)
                },
                Arrays.copyOfRange(tti, offset, offset + 16));

        // 2d subtitle 2d block
        offset += 128;
        assertArrayEquals(
                new byte[]{
                        0x00, // group number - 0
                        0x01, 0x00, // subtitle number - 0
                        (byte) 0x01, // extension block - 2d
                        0x00, // cumulative status - 00 (no cumulative)
                        0x00, 0x00, 0x0a, 0x00, // code in: 00:00:10:00
                        0x00, 0x01, 0x0a, 0x00, // code out: 00:01:10:00
                        0x16, // vertical position
                        0x02, // centered by default
                        0x00, // comment - 00 (contains subtitle)
                },
                Arrays.copyOfRange(tti, offset, offset + 16));

        // 2d subtitle 3d block
        offset += 128;
        assertArrayEquals(
                new byte[]{
                        0x00, // group number - 0
                        0x01, 0x00, // subtitle number - 0
                        (byte) 0x02, // extension block - 3d
                        0x00, // cumulative status - 00 (no cumulative)
                        0x00, 0x00, 0x0a, 0x00, // code in: 00:00:10:00
                        0x00, 0x01, 0x0a, 0x00, // code out: 00:01:10:00
                        0x16, // vertical position
                        0x02, // centered by default
                        0x00, // comment - 00 (contains subtitle)
                },
                Arrays.copyOfRange(tti, offset, offset + 16));

        // 2d subtitle 4th block
        offset += 128;
        assertArrayEquals(
                new byte[]{
                        0x00, // group number - 0
                        0x01, 0x00, // subtitle number - 0
                        (byte) 0xff, // extension block - last
                        0x00, // cumulative status - 00 (no cumulative)
                        0x00, 0x00, 0x0a, 0x00, // code in: 00:00:10:00
                        0x00, 0x01, 0x0a, 0x00, // code out: 00:01:10:00
                        0x16, // vertical position
                        0x02, // centered by default
                        0x00, // comment - 00 (contains subtitle)
                },
                Arrays.copyOfRange(tti, offset, offset + 16));
    }

    //@Test
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

        // 2d subtitle  - 3 blocks
        int offset = 128;
        textWithPadding = new byte[112];
        Arrays.fill(textWithPadding, (byte) 0x31);
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
        textWithPadding[0] = (byte) 0x31;
        assertArrayEquals(
                new byte[]{
                        0x01, 0x00, // subtitle number - 1
                        (byte) 0xf6, // extension block - last
                },
                Arrays.copyOfRange(tti, offset + 1, offset + 4));
        assertArrayEquals(
                textWithPadding,
                Arrays.copyOfRange(tti, offset + 16, offset + 128));
    }

    @Test
    public void testSubtitleWithManyLines() throws Exception {

    }

    @Test
    public void testCumulativeSubtitle() throws Exception {

    }

    @Test
    public void testIntersectedSubtitles() throws Exception {

    }

    @Test
    public void testManyIntersectedSubtitles() throws Exception {

    }

    @Test
    public void testStyles() throws Exception {

    }

}
