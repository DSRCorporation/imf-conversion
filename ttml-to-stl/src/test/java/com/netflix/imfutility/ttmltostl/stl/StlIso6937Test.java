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
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;

/**
 * Tests for encoding to ISO6937/2 (default one for STL TTI blocks).
 */
public class StlIso6937Test {

    @Test
    public void testTtiIso6937AnsiChars() throws Exception {
        byte[] tti = build(" !\"#%&'()*+,-./");
        assertEncoding(
                new byte[]{
                        0x20, 0x21, 0x22, 0x23, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2a, 0x2b, 0x2c, 0x2d, 0x2e, 0x2f
                },
                tti
        );
    }

    @Test
    public void testTtiIso6937AnsiNumbers() throws Exception {
        byte[] tti = build("0123456789:;<=>?");
        assertEncoding(
                new byte[]{
                        0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3a, 0x3b, 0x3c, 0x3d, 0x3e, 0x3f
                },
                tti
        );
    }

    @Test
    public void testTtiIso6937AnsiLetters() throws Exception {
        byte[] tti = build("@ABCMNOPQRXYZ[\\]^_`abcmnopqrxyz{|}~");
        assertEncoding(
                new byte[]{
                        0x40, //@
                        0x41, 0x42, 0x43, //ABC
                        0x4d, 0x4e, 0x4f, //MNO
                        0x50, 0x51, 0x52, //PQR
                        0x58, 0x59, 0x5a, //XYZ
                        0x5b, 0x5c, 0x5d, 0x5e, 0x5f, //[\]^_
                        0x60, //`
                        0x61, 0x62, 0x63, //abc
                        0x6d, 0x6e, 0x6f, //mno
                        0x70, 0x71, 0x72, //pqr
                        0x78, 0x79, 0x7a, //xyz
                        0x7b, 0x7c, 0x7d, 0x7e //{|}~
                },
                tti
        );
    }

    @Test
    public void testTtiIso6937Currency() throws Exception {
        byte[] tti = build("¢$¤¥£");
        assertEncoding(
                new byte[]{
                        (byte) 0xa2, (byte) 0xa4, 0x24, (byte) 0xa5, (byte) 0xa3
                },
                tti
        );
    }

    @Test
    public void testTtiIso6937SymbolsA() throws Exception {
        byte[] tti = build("§‘“«←↑→↓");
        assertEncoding(
                new byte[]{
                        (byte) 0xa7, (byte) 0xa9, (byte) 0xaa, (byte) 0xab, (byte) 0xac,
                        (byte) 0xad, (byte) 0xae, (byte) 0xaf,
                },
                tti
        );
    }

    @Test
    public void testTtiIso6937SymbolsB() throws Exception {
        byte[] tti = build("°±²³×µ¶·÷’”»¼½¾¿");
        assertEncoding(
                new byte[]{
                        (byte) 0xb0, (byte) 0xb1, (byte) 0xb2, (byte) 0xb3, (byte) 0xb4, (byte) 0xb5, (byte) 0xb6, (byte) 0xb7,
                        (byte) 0xb8, (byte) 0xb9, (byte) 0xba, (byte) 0xbb, (byte) 0xbc, (byte) 0xbd, (byte) 0xbe, (byte) 0xbf
                },
                tti
        );
    }

    @Test
    public void testTtiIso6937SymbolsC() throws Exception {
        byte[] tti = build("ÀÁÂÃĀĂĊÄÅÇŐĄČ");
        assertEncoding(
                new byte[]{
                        (byte) 0xc1, 0x41, (byte) 0xc2, 0x41, (byte) 0xc3, 0x41, (byte) 0xc4, 0x41,
                        (byte) 0xc5, 0x41, (byte) 0xc6, 0x41, (byte) 0xc7, 0x43,
                        (byte) 0xc8, 0x41, (byte) 0xca, 0x41, (byte) 0xcb, 0x43,
                        (byte) 0xcd, 0x4f, (byte) 0xce, 0x41, (byte) 0xcf, 0x43
                },
                tti
        );
    }

    @Test
    public void testTtiIso6937SymbolsD() throws Exception {
        byte[] tti = build("―¹®©™♪¬¦⅛⅜⅝⅞");
        assertEncoding(
                new byte[]{
                        (byte) 0xd0, (byte) 0xd1, (byte) 0xd2, (byte) 0xd3, (byte) 0xd4, (byte) 0xd5, (byte) 0xd6,
                        (byte) 0xd7, (byte) 0xdc, (byte) 0xdd, (byte) 0xde, (byte) 0xdf
                },
                tti
        );
    }

    @Test
    public void testTtiIso6937SymbolsE() throws Exception {
        byte[] tti = build("ΩÆĐªĦĲĿŁØŒºÞŦŊŉ");
        assertEncoding(
                new byte[]{
                        (byte) 0xe0, (byte) 0xe1, (byte) 0xe2, (byte) 0xe3, (byte) 0xe4, (byte) 0xe6,
                        (byte) 0xe7, (byte) 0xe8, (byte) 0xe9, (byte) 0xea, (byte) 0xeb, (byte) 0xec, (byte) 0xed,
                        (byte) 0xee, (byte) 0xef
                },
                tti
        );
    }

    @Test
    public void testTtiIso6937SymbolsF() throws Exception {
        byte[] tti = build("ĸæđðħıĳŀłøœßþŧŋ");
        assertEncoding(
                new byte[]{
                        (byte) 0xf0, (byte) 0xf1, (byte) 0xf2, (byte) 0xf3, (byte) 0xf4, (byte) 0xf5, (byte) 0xf6,
                        (byte) 0xf7, (byte) 0xf8, (byte) 0xf9, (byte) 0xfa, (byte) 0xfb, (byte) 0xfc, (byte) 0xfd,
                        (byte) 0xfe
                },
                tti
        );
    }

    private byte[] build(String subtitle) throws Exception {
        System.out.println(subtitle);
        TimedTextObject tto = StlTestUtil.buildTto(
                "00:00:00:00", "00:00:05:00", subtitle
        );
        byte[][] stl = StlTestUtil.build(tto, StlTestUtil.getMetadataXml());
        return stl[1];
    }

    private void assertEncoding(byte[] expected, byte[] actual) {
        byte[] textWithPadding = new byte[112];
        Arrays.fill(textWithPadding, (byte) 0x8f);
        System.arraycopy(expected, 0, textWithPadding, 0, expected.length);

        assertArrayEquals(
                textWithPadding,
                Arrays.copyOfRange(actual, 16, 128));
    }

}
