package com.netflix.imfutility.util;

import org.apache.commons.math3.fraction.BigFraction;

import java.math.BigInteger;
import java.util.List;

/**
 * A helper class with conversion utility methods.
 */
public final class ConversionHelper {

    private ConversionHelper() {
    }

    /**
     * Transforms edit units to a timecode according to the given edit unit rate.
     * <ul>
     * <li>An example of edit units is a frame.</li>
     * <li>The output timecode has the following format 'hh:mm:ss.xxx', where xxx is milliseconds.</li>
     * </ul>
     *
     * @param eu         edit units to be transformed
     * @param unitsInSec edit unit rate
     * @return timecode as a string in "hh:mm:ss.mss" format.
     */
    public static String editUnitToTimecode(BigInteger eu, BigFraction unitsInSec) {
        BigFraction editUnits = new BigFraction(eu);
        BigFraction unitsInMin = unitsInSec.multiply(new BigFraction(60));
        BigFraction unitsInHour = unitsInSec.multiply(new BigFraction(60 * 60));


        int hours = editUnits
                .divide(unitsInHour)
                .intValue();
        int minutes = editUnits
                .subtract(unitsInHour.multiply(hours))
                .divide(unitsInMin)
                .intValue();
        int seconds = editUnits
                .subtract(unitsInHour.multiply(hours))
                .subtract(unitsInMin.multiply(minutes))
                .divide(unitsInSec)
                .intValue();
        BigFraction units = editUnits
                .subtract(unitsInHour.multiply(hours))
                .subtract(unitsInMin.multiply(minutes))
                .subtract(unitsInSec.multiply(seconds));
        int milliseconds = new BigFraction(1000).divide(unitsInSec).multiply(units).intValue();

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
    }

    /**
     * Returns a fraction corresponding to the given edit rate string.
     * The edit rate is assumed to be a list containing of two elements: numerator and denominator.
     *
     * @param editRate the edit rate as a list containing of two elements: numerator and denominator.
     * @return a fraction object representing the edit rate.
     */
    public static BigFraction parseEditRate(List<Long> editRate) {
        if (editRate.size() != 2) {
            throw new RuntimeException("Incorrect edit rate! Edit rate must consist of two values.");
        }
        return new BigFraction(editRate.get(0), editRate.get(1));
    }

    public static String zeroTimecode() {
        return "00:00:00.000";
    }

}


