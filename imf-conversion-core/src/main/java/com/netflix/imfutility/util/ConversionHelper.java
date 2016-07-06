package com.netflix.imfutility.util;

import com.netflix.imfutility.ConversionException;
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
     * Converts the given number of edit units in old edit rate to a new edit rate.
     * It can be used to convert audio samples to video frames.
     *
     * @param eu            edit units number in oldUnitsInSec edit rate
     * @param oldUnitsInSec old edit rate specifying the current edit units
     * @param newUnitsInSec new edit rate
     * @return edit units number in newUnitsInSec edit rate.
     */
    public static long toNewEditRate(BigInteger eu, BigFraction oldUnitsInSec, BigFraction newUnitsInSec) {
        BigFraction editUnits = new BigFraction(eu);
        return editUnits.divide(oldUnitsInSec).multiply(newUnitsInSec).longValue();
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
            throw new ConversionException("Incorrect edit rate! Edit rate must consist of two values.");
        }
        return new BigFraction(editRate.get(0), editRate.get(1));
    }

    /**
     * Converts the edit rate as a fraction instance to the edit rate form such as "50 1".
     *
     * @param editRate input
     * @return output in a form "50 1"
     */
    public static String toEditRate(BigFraction editRate) {
        return String.format("%s %s", String.valueOf(editRate.getNumeratorAsLong()), String.valueOf(editRate.getDenominatorAsLong()));
    }

    /**
     * Returns a fraction corresponding to the given edit rate string.
     *
     * @param editRate input in a form "50 1"
     * @return a fraction object representing the edit rate.
     */
    public static BigFraction parseEditRate(String editRate) {
        String[] parts = editRate.split(" ");
        try {
            if (parts.length == 2) {
                return new BigFraction(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
            } else if (parts.length == 1) {
                return new BigFraction(Long.parseLong(parts[0]));
            }
        } catch (NumberFormatException e) {
            throw new ConversionException("Incorrect edit rate! Edit rate must consist of two numbers.", e);
        }
        throw new ConversionException("Incorrect edit rate! Edit rate must consist of two values.");
    }

    /**
     * Converts rFrameRate in a form "50/1" or "50" to the edit rate form "50 1".
     *
     * @param rFrameRate input in a form "50/1" or "50"
     * @return output in a form "50 1"
     */
    public static String rFrameRateToEditRate(String rFrameRate) {
        String[] parts = rFrameRate.split("/");
        if (parts.length == 2) {
            return String.format("%s %s", parts[0], parts[1]);
        }
        return String.format("%s %s", rFrameRate, 1);
    }

    public static String zeroTimecode() {
        return "00:00:00.000";
    }

}


