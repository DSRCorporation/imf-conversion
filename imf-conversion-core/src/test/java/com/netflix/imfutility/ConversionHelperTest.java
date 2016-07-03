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
    }

    @Test(expected = com.netflix.imfutility.ConversionException.class)
    public void parseIncorrectEditRate() {
        ConversionHelper.parseEditRate("30000 1001 1");
        ConversionHelper.parseEditRate("");
        ConversionHelper.parseEditRate("aaaaa");
    }


}
