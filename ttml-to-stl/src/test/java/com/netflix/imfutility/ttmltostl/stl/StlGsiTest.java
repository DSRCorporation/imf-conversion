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
import com.netflix.imfutility.ttmltostl.util.StlTestUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertArrayEquals;

/**
 * Tests building of STL GSI block.
 */
public class StlGsiTest {

    @Test
    public void testBlocksAndSize() throws Exception {
        TimedTextObject tto = StlTestUtil.buildTto(
                "10:00:00:00", "10:00:05:00", "text1",
                "10:00:05:00", "10:00:10:00", "text2",
                "10:00:10:00", "10:01:10:00", "text3"
        );
        byte[][] stl = StlTestUtil.build(tto, StlTestUtil.getMetadataXml());

        assertEquals(2, stl.length); // gsi and tti blocks
        assertEquals(1024, stl[0].length); // gsi size
        assertEquals(4 * 128, stl[1].length);  // tti size (for 3 captions + subtitle zero)
    }

    @Test
    public void testEmptyBlockAndSize() throws Exception {
        TimedTextObject tto = StlTestUtil.buildTto(
        );
        byte[][] stl = StlTestUtil.build(tto, StlTestUtil.getMetadataXml());

        assertEquals(2, stl.length); // gsi and tti blocks
        assertEquals(1024, stl[0].length); // gsi size
        assertEquals(128, stl[1].length);  // subtitle zero only
    }

    @Test
    public void testGsiAll() throws Exception {
        TimedTextObject tto = StlTestUtil.buildTto(
                "10:00:15:10", "10:00:16:00", "text1",
                "10:00:16:00", "10:00:17:00", "text2",
                "10:00:17:00", "10:01:20:00", "text3"
        );
        byte[][] stl = StlTestUtil.build(tto, StlTestUtil.getMetadataXml());
        byte[] gsi = stl[0];

        assertArrayEquals(
                new byte[]{
                        0x38, 0x35, 0x30, // 850
                        0x53, 0x54, 0x4c, 0x32, 0x35, 0x2e, 0x30, 0x31, // STL25.01
                        0x31, // 1 - teletext
                        0x30, 0x30, // 00 - latin
                        0x30, 0x39 // 09 - English
                },
                Arrays.copyOfRange(gsi, 0, 16));

        assertArrayEquals(
                new byte[]{
                        0x50, 0x72, 0x6f, 0x67, 0x72, 0x61, 0x6d, 0x6d, 0x65,  // 'Programme' as in metadata.xml
                        0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,
                        0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,
                        0x20, 0x20, 0x20, 0x20, 0x20
                },
                Arrays.copyOfRange(gsi, 16, 48));

        assertArrayEquals(
                new byte[]{
                        0x45, 0x70, 0x69, 0x73, 0x6f, 0x64, 0x65,   // 'Episode' as in metadata.xml
                        0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,
                        0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,
                        0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20
                },
                Arrays.copyOfRange(gsi, 48, 80));

        byte[] translated = new byte[128];
        Arrays.fill(translated, (byte) 0x20);
        assertArrayEquals(
                translated,
                Arrays.copyOfRange(gsi, 80, 208));

        byte[] slr = new byte[16];
        Arrays.fill(slr, (byte) 0x20);
        assertArrayEquals(
                slr,
                Arrays.copyOfRange(gsi, 208, 224));

        byte[] currentDate = new SimpleDateFormat("yyMMdd").format(new Date()).getBytes();
        assertArrayEquals(
                currentDate,
                Arrays.copyOfRange(gsi, 224, 230));
        assertArrayEquals(
                currentDate,
                Arrays.copyOfRange(gsi, 230, 236));

        assertArrayEquals(
                new byte[]{
                        0x30, 0x31 // revision number = 01
                },
                Arrays.copyOfRange(gsi, 236, 238));

        assertArrayEquals(
                new byte[]{
                        0x30, 0x30, 0x30, 0x30, 0x34 // number of tti blocks - 3 + subtitle zero = 4
                },
                Arrays.copyOfRange(gsi, 238, 243));

        assertArrayEquals(
                new byte[]{
                        0x30, 0x30, 0x30, 0x30, 0x34 // number of subtitles - 3 + subtitle zero = 4
                },
                Arrays.copyOfRange(gsi, 243, 248));

        assertArrayEquals(
                new byte[]{
                        0x30, 0x30, 0x31 // number of subtitle groups - 1
                },
                Arrays.copyOfRange(gsi, 248, 251));

        assertArrayEquals(
                new byte[]{
                        0x35, 0x38 // number of displayable chars - 58
                },
                Arrays.copyOfRange(gsi, 251, 253));

        assertArrayEquals(
                new byte[]{
                        0x31, 0x31 // number of displayable rows - 11
                },
                Arrays.copyOfRange(gsi, 253, 255));

        assertArrayEquals(
                new byte[]{
                        0x31 // tc status - 1
                },
                Arrays.copyOfRange(gsi, 255, 256));

        assertArrayEquals(
                new byte[]{
                        0x31, 0x30, 0x30, 0x30, 0x30, 0x30, 0x32, 0x30 // start time of program: 10:00:00:20 (as in metadata.xml)
                },
                Arrays.copyOfRange(gsi, 256, 264));

        assertArrayEquals(
                new byte[]{
                        0x31, 0x30, 0x30, 0x30, 0x31, 0x35, 0x31, 0x30 // start time of subtitles: 10:00:15:10 (as in metadata.xml)
                },
                Arrays.copyOfRange(gsi, 264, 272));

        assertArrayEquals(
                new byte[]{
                        0x31 // number of disks - 1
                },
                Arrays.copyOfRange(gsi, 272, 273));

        assertArrayEquals(
                new byte[]{
                        0x31 // disk seq number - 1
                },
                Arrays.copyOfRange(gsi, 273, 274));

        assertArrayEquals(
                new byte[]{
                        0x47, 0x42, 0x52 // country - GBR
                },
                Arrays.copyOfRange(gsi, 274, 277));

        assertArrayEquals(
                new byte[]{
                        0x4f, 0x72, 0x69, 0x67, 0x69, 0x6e, 0x61, 0x74, 0x6f, 0x72, // Publisher: Originator (as in metadata.xml)
                        0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,
                        0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,
                        0x20, 0x20
                },
                Arrays.copyOfRange(gsi, 277, 309));

        assertArrayEquals(
                new byte[]{
                        0x44, 0x69, 0x73, 0x74, 0x72, 0x69, 0x62, 0x75, 0x74, 0x6f, 0x72, // Editor: Distributor (as in metadata.xml)
                        0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,
                        0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20
                },
                Arrays.copyOfRange(gsi, 309, 341));

        assertArrayEquals(
                new byte[]{
                        0x61, 0x63, 0x63, 0x6f, 0x75, 0x6e, 0x74, 0x40,  // Contact: account@myemail.com (as in metadata.xml)
                        0x6d, 0x79, 0x65, 0x6d, 0x61, 0x69, 0x6c, 0x2e, 0x63, 0x6f, 0x6d,
                        0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,
                        0x20, 0x20, 0x20
                },
                Arrays.copyOfRange(gsi, 341, 373));

        byte[] spare = new byte[651];
        Arrays.fill(spare, (byte) 0x20);
        assertArrayEquals(
                spare,
                Arrays.copyOfRange(gsi, 373, 1024));

    }

    @Test
    public void testGsiAllEmpty() throws Exception {
        TimedTextObject tto = StlTestUtil.buildTto(
        );
        byte[][] stl = StlTestUtil.build(tto, StlTestUtil.getMetadataXml());
        byte[] gsi = stl[0];

        assertArrayEquals(
                new byte[]{
                        0x38, 0x35, 0x30, // 850
                        0x53, 0x54, 0x4c, 0x32, 0x35, 0x2e, 0x30, 0x31, // STL25.01
                        0x31, // 1 - teletext
                        0x30, 0x30, // 00 - latin
                        0x30, 0x39 // 09 - English
                },
                Arrays.copyOfRange(gsi, 0, 16));

        assertArrayEquals(
                new byte[]{
                        0x50, 0x72, 0x6f, 0x67, 0x72, 0x61, 0x6d, 0x6d, 0x65,  // 'Programme' as in metadata.xml
                        0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,
                        0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,
                        0x20, 0x20, 0x20, 0x20, 0x20
                },
                Arrays.copyOfRange(gsi, 16, 48));

        assertArrayEquals(
                new byte[]{
                        0x45, 0x70, 0x69, 0x73, 0x6f, 0x64, 0x65,   // 'Episode' as in metadata.xml
                        0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,
                        0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,
                        0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20
                },
                Arrays.copyOfRange(gsi, 48, 80));

        byte[] translated = new byte[128];
        Arrays.fill(translated, (byte) 0x20);
        assertArrayEquals(
                translated,
                Arrays.copyOfRange(gsi, 80, 208));

        byte[] slr = new byte[16];
        Arrays.fill(slr, (byte) 0x20);
        assertArrayEquals(
                slr,
                Arrays.copyOfRange(gsi, 208, 224));

        byte[] currentDate = new SimpleDateFormat("yyMMdd").format(new Date()).getBytes();
        assertArrayEquals(
                currentDate,
                Arrays.copyOfRange(gsi, 224, 230));
        assertArrayEquals(
                currentDate,
                Arrays.copyOfRange(gsi, 230, 236));

        assertArrayEquals(
                new byte[]{
                        0x30, 0x31 // revision number = 01
                },
                Arrays.copyOfRange(gsi, 236, 238));

        assertArrayEquals(
                new byte[]{
                        0x30, 0x30, 0x30, 0x30, 0x31 // 1 subtitle zero
                },
                Arrays.copyOfRange(gsi, 238, 243));

        assertArrayEquals(
                new byte[]{
                        0x30, 0x30, 0x30, 0x30, 0x31 // 1 subtitle zero
                },
                Arrays.copyOfRange(gsi, 243, 248));

        assertArrayEquals(
                new byte[]{
                        0x30, 0x30, 0x31 // number of subtitle groups - 1
                },
                Arrays.copyOfRange(gsi, 248, 251));

        assertArrayEquals(
                new byte[]{
                        0x35, 0x38 // number of displayable chars - 58
                },
                Arrays.copyOfRange(gsi, 251, 253));

        assertArrayEquals(
                new byte[]{
                        0x31, 0x31 // number of displayable rows - 11
                },
                Arrays.copyOfRange(gsi, 253, 255));

        assertArrayEquals(
                new byte[]{
                        0x31 // tc status - 1
                },
                Arrays.copyOfRange(gsi, 255, 256));

        assertArrayEquals(
                new byte[]{
                        0x31, 0x30, 0x30, 0x30, 0x30, 0x30, 0x32, 0x30 // start time of program: 10:00:00:20 (as in metadata.xml)
                },
                Arrays.copyOfRange(gsi, 256, 264));

        assertArrayEquals(
                new byte[]{
                        0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30 // start time of subtitles: 00:00:00:00 (zero subtitle)
                },
                Arrays.copyOfRange(gsi, 264, 272));

        assertArrayEquals(
                new byte[]{
                        0x31 // number of disks - 1
                },
                Arrays.copyOfRange(gsi, 272, 273));

        assertArrayEquals(
                new byte[]{
                        0x31 // disk seq number - 1
                },
                Arrays.copyOfRange(gsi, 273, 274));

        assertArrayEquals(
                new byte[]{
                        0x47, 0x42, 0x52 // country - GBR
                },
                Arrays.copyOfRange(gsi, 274, 277));

        assertArrayEquals(
                new byte[]{
                        0x4f, 0x72, 0x69, 0x67, 0x69, 0x6e, 0x61, 0x74, 0x6f, 0x72, // Publisher: Originator (as in metadata.xml)
                        0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,
                        0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,
                        0x20, 0x20
                },
                Arrays.copyOfRange(gsi, 277, 309));

        assertArrayEquals(
                new byte[]{
                        0x44, 0x69, 0x73, 0x74, 0x72, 0x69, 0x62, 0x75, 0x74, 0x6f, 0x72, // Editor: Distributor (as in metadata.xml)
                        0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,
                        0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20
                },
                Arrays.copyOfRange(gsi, 309, 341));

        assertArrayEquals(
                new byte[]{
                        0x61, 0x63, 0x63, 0x6f, 0x75, 0x6e, 0x74, 0x40,  // Contact: account@myemail.com (as in metadata.xml)
                        0x6d, 0x79, 0x65, 0x6d, 0x61, 0x69, 0x6c, 0x2e, 0x63, 0x6f, 0x6d,
                        0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,
                        0x20, 0x20, 0x20
                },
                Arrays.copyOfRange(gsi, 341, 373));

        byte[] spare = new byte[651];
        Arrays.fill(spare, (byte) 0x20);
        assertArrayEquals(
                spare,
                Arrays.copyOfRange(gsi, 373, 1024));

    }


    @Test
    public void testGsiEncodingCp850() throws Exception {
        TimedTextObject tto = StlTestUtil.buildTto(
                "10:00:15:10", "10:00:16:00", "text1",
                "10:00:16:00", "10:00:17:00", "text2",
                "10:00:17:00", "10:01:20:00", "text3"
        );
        byte[][] stl = StlTestUtil.build(tto, StlTestUtil.getMetadataSpecialSymbolsXml());
        byte[] gsi = stl[0];

        // check fields with special symbols (as defined in metadata.xml):

        // All symbols in Programme must be valid 850 symbols
        int ch = Arrays.copyOfRange(gsi, 16, 48)[12] & 0xff;
        assertArrayEquals(
                new byte[]{
                        (byte) 0xb8, (byte) 0xa9, (byte) 0xbe, (byte) 0xab, (byte) 0xac, (byte) 0xc7, (byte) 0xb6,
                        (byte) 0xb5, (byte) 0x9c, (byte) 0x9d, (byte) 0x8e, (byte) 0xf5, (byte) 0xf1, (byte) 0xcf,
                        0x20, 0x20, 0x20, 0x20,
                        0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,
                        0x20, 0x20, 0x20, 0x20, 0x20
                },
                Arrays.copyOfRange(gsi, 16, 48));

        // All symbols in Episode are non-valid, so must be replaced with ?
        assertArrayEquals(
                new byte[]{
                        0x3f, 0x3f, 0x3f, 0x3f, 0x3f, 0x3f, 0x3f,
                        0x3f, 0x3f, 0x3f, 0x3f, 0x3f, 0x58, 0x20, 0x20, 0x20,
                        0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20,
                        0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20
                },
                Arrays.copyOfRange(gsi, 48, 80));
    }

    @Test
    public void testNumberOfSubtitles() throws Exception {
        // prepare long subtitles, so that one subtitles is stored in two tti blocks
        TimedTextObject tto = StlTestUtil.buildTto(
                "10:00:00:00", "10:00:05:00", StringUtils.rightPad("test", 200, '1'), // in 2 tti
                "10:00:05:00", "10:00:10:00", StringUtils.rightPad("test", 300, '2'), // in 3 tti
                "10:00:10:00", "10:01:10:00", StringUtils.rightPad("test", 400, '3') // in 4 tti
        );
        byte[][] stl = StlTestUtil.build(tto, StlTestUtil.getMetadataXml());
        byte[] gsi = stl[0];

        assertArrayEquals(
                new byte[]{
                        0x30, 0x30, 0x30, 0x31, 0x30 // number of tti blocks - 9 + subtitle zero = 10
                },
                Arrays.copyOfRange(gsi, 238, 243));

        assertArrayEquals(
                new byte[]{
                        0x30, 0x30, 0x30, 0x30, 0x34 // number of subtitles - 3 + subtitle zero = 4
                },
                Arrays.copyOfRange(gsi, 243, 248));

        assertArrayEquals(
                new byte[]{
                        0x30, 0x30, 0x31 // number of subtitle groups - 1
                },
                Arrays.copyOfRange(gsi, 248, 251));
    }

}
