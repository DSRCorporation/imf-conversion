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
package com.netflix.imfutility;

import com.netflix.imfutility.util.ConversionHelper;
import org.apache.commons.math3.fraction.BigFraction;
import org.junit.Test;

import java.math.BigInteger;

import static junit.framework.TestCase.assertEquals;

/**
 * Tests that conversion helper utility methods work properly.
 */
public class ConversionHelperTest {

    @Test
    public void editUnitsToTimecode() {
        assertEquals("01:23:35.080", ConversionHelper.editUnitToTimecode(BigInteger.valueOf(125377), new BigFraction(25)));
        assertEquals("00:00:00.000", ConversionHelper.editUnitToTimecode(BigInteger.valueOf(0), new BigFraction(25)));
        assertEquals("00:00:02.000", ConversionHelper.editUnitToTimecode(BigInteger.valueOf(100), new BigFraction(50)));
        assertEquals("00:00:00.200", ConversionHelper.editUnitToTimecode(BigInteger.valueOf(10), new BigFraction(50)));
    }

    @Test
    public void toNewEditRate() {
        assertEquals(100, ConversionHelper.toNewEditRate(BigInteger.valueOf(160160), new BigFraction(48000), new BigFraction(30000, 1001)));
        assertEquals(50, ConversionHelper.toNewEditRate(BigInteger.valueOf(100), new BigFraction(50), new BigFraction(25)));
        assertEquals(55, ConversionHelper.toNewEditRate(BigInteger.valueOf(110), new BigFraction(50), new BigFraction(25)));
    }

    @Test
    public void toEditRate() {
        assertEquals("30000 1001", ConversionHelper.toEditRate(new BigFraction(30000, 1001)));
        assertEquals("50 1", ConversionHelper.toEditRate(new BigFraction(50)));
    }

    @Test
    public void rFrameRateToEditRate() {
        assertEquals("50 1", ConversionHelper.rFrameRateToEditRate("50/1"));
        assertEquals("50 1", ConversionHelper.rFrameRateToEditRate("50"));
        assertEquals("25 2", ConversionHelper.rFrameRateToEditRate("25/2"));
    }

    @Test
    public void parseCorrectEditRate() {
        assertEquals(new BigFraction(30000, 1001), ConversionHelper.parseEditRate("30000 1001"));
        assertEquals(new BigFraction(50, 1), ConversionHelper.parseEditRate("50"));
        assertEquals(new BigFraction(30000, 1001), ConversionHelper.parseEditRate("30000/1001"));
        assertEquals(new BigFraction(50, 1), ConversionHelper.parseEditRate("50 1"));
    }

    @Test
    public void editUnitsToMilliseconds() {
        assertEquals(4000L, ConversionHelper.editUnitToMilliSeconds(BigInteger.valueOf(100), new BigFraction(25)));
        assertEquals(500L, ConversionHelper.editUnitToMilliSeconds(BigInteger.valueOf(25), new BigFraction(50)));
        assertEquals(3336L, ConversionHelper.editUnitToMilliSeconds(BigInteger.valueOf(100), new BigFraction(30000, 1001)));
    }

    @Test
    public void smpteTimecodeToMilliseconds() {
        assertEquals(4000L, ConversionHelper.smpteTimecodeToMilliSeconds("00:00:04:00", "25"));
        assertEquals(4000L, ConversionHelper.smpteTimecodeToMilliSeconds("00:00:04:00", "25 1"));

        assertEquals(0L, ConversionHelper.smpteTimecodeToMilliSeconds("00:00:00:00", "25"));

        assertEquals(4000L, ConversionHelper.smpteTimecodeToMilliSeconds("00;00;04;00", "25"));
        assertEquals(4000L, ConversionHelper.smpteTimecodeToMilliSeconds("00.00.04.00", "25"));
        assertEquals(4000L, ConversionHelper.smpteTimecodeToMilliSeconds("00:00:04;00", "25"));
        assertEquals(4000L, ConversionHelper.smpteTimecodeToMilliSeconds("00:00:04.00", "25"));
        assertEquals(4000L, ConversionHelper.smpteTimecodeToMilliSeconds("0:0:4.0", "25"));
        assertEquals(4000L, ConversionHelper.smpteTimecodeToMilliSeconds("0:0:4:00", "25"));

        assertEquals(960L, ConversionHelper.smpteTimecodeToMilliSeconds("00:00:00:24", "25"));
        assertEquals(500L, ConversionHelper.smpteTimecodeToMilliSeconds("00:00:00:25", "50"));
        assertEquals(200L, ConversionHelper.smpteTimecodeToMilliSeconds("00:00:00:10", "50"));

        assertEquals(18243000L, ConversionHelper.smpteTimecodeToMilliSeconds("05:04:03:00", "25"));
        assertEquals(18243960L, ConversionHelper.smpteTimecodeToMilliSeconds("05:04:03:24", "25"));
        assertEquals(18243500L, ConversionHelper.smpteTimecodeToMilliSeconds("05:04:03:25", "50"));
        assertEquals(18243200L, ConversionHelper.smpteTimecodeToMilliSeconds("05:04:03:10", "50"));
        assertEquals(243200L, ConversionHelper.smpteTimecodeToMilliSeconds("00:4:3:10", "50"));
    }

    @Test(expected = com.netflix.imfutility.ConversionException.class)
    public void incorrectSmpteTimecodeToMillisecondsIncorrectEditRateNonNumber() {
        ConversionHelper.smpteTimecodeToMilliSeconds("00:00:04:00", "aaa");

    }

    @Test(expected = com.netflix.imfutility.ConversionException.class)
    public void incorrectSmpteTimecodeToMillisecondsIncorrectEditRateMoreArguments() {
        ConversionHelper.smpteTimecodeToMilliSeconds("00:00:04:00", "30000 1001 1");
    }

    @Test(expected = com.netflix.imfutility.ConversionException.class)
    public void incorrectSmpteTimecodeToMillisecondsIncorrectEditRateEmpty() {
        ConversionHelper.smpteTimecodeToMilliSeconds("00:00:04:00", "");
    }

    @Test(expected = com.netflix.imfutility.ConversionException.class)
    public void incorrectSmpteTimecodeToMillisecondsEmptyFrames() {
        ConversionHelper.smpteTimecodeToMilliSeconds("00:00:04:", "25");
    }

    @Test(expected = com.netflix.imfutility.ConversionException.class)
    public void incorrectSmpteTimecodeToMillisecondsNoFrame() {
        ConversionHelper.smpteTimecodeToMilliSeconds("00:00:04", "25");
    }

    @Test(expected = com.netflix.imfutility.ConversionException.class)
    public void incorrectSmpteTimecodeToMillisecondsNotNumbers() {
        ConversionHelper.smpteTimecodeToMilliSeconds("00:00:04:nn", "25");
    }

    @Test(expected = com.netflix.imfutility.ConversionException.class)
    public void parseIncorrectEditRateMoreArguments() {
        ConversionHelper.parseEditRate("30000 1001 1");
    }

    @Test(expected = com.netflix.imfutility.ConversionException.class)
    public void parseIncorrectEditRateEmpty() {
        ConversionHelper.parseEditRate("");
    }

    @Test(expected = com.netflix.imfutility.ConversionException.class)
    public void parseIncorrectEditRateNotNumber() {
        ConversionHelper.parseEditRate("aaaaa");
    }

    @Test
    public void safeParseCorrectEditRate() {
        assertEquals(new BigFraction(30000, 1001), ConversionHelper.safeParseEditRate("30000 1001"));
        assertEquals(new BigFraction(50, 1), ConversionHelper.safeParseEditRate("50"));
        assertEquals(new BigFraction(30000, 1001), ConversionHelper.safeParseEditRate("30000/1001"));
        assertEquals(new BigFraction(50, 1), ConversionHelper.safeParseEditRate("50 1"));
    }

    @Test(expected = com.netflix.imfutility.ConversionException.class)
    public void safeParseIncorrectEditRateMoreArguments() {
        ConversionHelper.safeParseEditRate("30000 1001 1");
    }

    @Test(expected = com.netflix.imfutility.ConversionException.class)
    public void safeParseIncorrectEditRateEmpty() {
        ConversionHelper.safeParseEditRate("");
    }

    @Test(expected = com.netflix.imfutility.ConversionException.class)
    public void safeParseIncorrectEditRateNotNumber() {
        ConversionHelper.safeParseEditRate("aaaaa");
    }

    @Test
    public void safeParseCorrectAspectRatio() {
        assertEquals(new BigFraction(16, 9), ConversionHelper.parseAspectRatio("16/9"));
        assertEquals(new BigFraction(2), ConversionHelper.parseAspectRatio("2"));
    }

    @Test(expected = com.netflix.imfutility.ConversionException.class)
    public void safeParseIncorrectAspectRatioMoreArguments() {
        ConversionHelper.parseAspectRatio("30000 1001 1");
    }

    @Test(expected = com.netflix.imfutility.ConversionException.class)
    public void safeParseIncorrectAspectRatioEmpty() {
        ConversionHelper.parseAspectRatio("");
    }

    @Test(expected = com.netflix.imfutility.ConversionException.class)
    public void safeParseIncorrectAspectRatioNotNumber() {
        ConversionHelper.parseAspectRatio("aaaaa");
    }
}
