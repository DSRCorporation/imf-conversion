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

}
