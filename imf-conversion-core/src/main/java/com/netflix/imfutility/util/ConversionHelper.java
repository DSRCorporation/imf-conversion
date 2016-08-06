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
     * Transforms a timecode string (hh:mm:ss:ff) to milliseconds according to the given edit rate (frame rate).
     * <p>
     * Currently works with non-drop timecodes only.
     * </p>
     *
     * @param tc            an SMPTE timecode (hh:mm:ss:ff)
     * @param unitsInSecStr edit unit rate in a form "25 1"
     * @return a number of milliseconds
     */
    public static long smpteTimecodeToMilliSeconds(String tc, String unitsInSecStr) {
        BigFraction unitsInSec = parseEditRate(unitsInSecStr);
        return smpteTimecodeToMilliSeconds(tc, unitsInSec);
    }

    /**
     * Transforms a timecode string (hh:mm:ss:ff) to milliseconds according to the given edit rate (frame rate).
     * <p>
     *     Currently works with non-drop timecodes only.
     * </p>
     * @param tc an SMPTE timecode (hh:mm:ss:ff)
     * @param unitsInSec edit unit rate in a form "25 1"
     * @return a number of milliseconds
     */
    public static long smpteTimecodeToMilliSeconds(String tc, BigFraction unitsInSec) {
        String[] parts = tc.split("[:;\\.]");
        if (parts.length != 4) {
            throw new ConversionException(
                    String.format("Incorrect SMPTE timecode '%s'. Expected in a form 'HH[:;.]MM[:;.]SS[:;.]FF'", tc));
        }

        int hours;
        int mins;
        int secs;
        int frames;
        try {
            hours = Integer.parseInt(parts[0]);
            mins = Integer.parseInt(parts[1]);
            secs = Integer.parseInt(parts[2]);
            frames = Integer.parseInt(parts[3]);
        } catch (NumberFormatException e) {
            throw new ConversionException(
                    String.format("Incorrect SMPTE timecode '%s'! Expected in a form 'HH[:;.]MM[:;.]SS[:;.]FF'"
                            + " where HH,MM,SS and FF are non-negative integers", tc),
                    e);
        }

        long total = 0L;
        total += hours * 60 * 60 * 1000L;
        total += mins * 60 * 1000L;
        total += secs * 1000L;
        total += new BigFraction(frames).divide(unitsInSec).multiply(1000).longValue();
        return total;
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
     * Transforms milliseconds to an SMPTE timecode according to the given edit unit rate.
     * <ul>
     * <li>An example of edit units is a frame.</li>
     * <li>The output timecode has the following format 'hh:mm:ss:ff'.</li>
     * </ul>
     *
     * @param milliseconds         milliseconds to be transformed
     * @param unitsInSec edit unit rate
     * @return timecode as a string in "hh:mm:ss:ff" format.
     */
    public static String msToSmpteTimecode(long milliseconds, BigFraction unitsInSec) {
        BigFraction ms = new BigFraction(milliseconds);
        BigFraction msInMin = new BigFraction(60 * 1000);
        BigFraction msInHour = new BigFraction(60 * 60 * 1000);
        BigFraction msInSec = new BigFraction(1000);
        BigFraction unitsInMs = unitsInSec.divide(msInSec);


        int hours = ms
                .divide(msInHour)
                .intValue();
        int minutes = ms
                .subtract(msInHour.multiply(hours))
                .divide(msInMin)
                .intValue();
        int seconds = ms
                .subtract(msInHour.multiply(hours))
                .subtract(msInMin.multiply(minutes))
                .divide(msInSec)
                .intValue();
        int units = ms
                .subtract(msInHour.multiply(hours))
                .subtract(msInMin.multiply(minutes))
                .subtract(msInSec.multiply(seconds))
                .multiply(unitsInMs)
                .intValue();

        return String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds, units);
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
     * Converts the edit untis to milliseconds according to the given edit rate.
     *
     * @param eu         edit units number
     * @param unitsInSec edit rate
     * @return milliseconds
     */
    public static long editUnitToMilliSeconds(BigInteger eu, BigFraction unitsInSec) {
        BigFraction editUnits = new BigFraction(eu);
        return editUnits.divide(unitsInSec).multiply(1000).longValue();
    }

    /**
     * Converts the edit untis to seconds according to the given edit rate.
     *
     * @param eu         edit units number
     * @param unitsInSec edit rate
     * @return seconds
     */
    public static long toSeconds(BigInteger eu, BigFraction unitsInSec) {
        BigFraction editUnits = new BigFraction(eu);
        return editUnits.divide(unitsInSec).longValue();
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
     * Converts the edit rate as a fraction instance to the edit rate form such as "50/1".
     *
     * @param editRate input
     * @return output in a form "50/1"
     */
    public static String toREditRate(BigFraction editRate) {
        return String.format("%s/%s", String.valueOf(editRate.getNumeratorAsLong()), String.valueOf(editRate.getDenominatorAsLong()));
    }

    /**
     * Returns a fraction corresponding to the given edit rate string.
     *
     * @param editRate input in both forms "50 1" and "50/1"
     * @return a fraction object representing the edit rate.
     */
    public static BigFraction parseEditRate(String editRate) {
        editRate = editRate.contains("/") ? rFrameRateToEditRate(editRate) : editRate;
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

    /**
     * Returns a fraction corresponding to the given aspect ratio string.
     *
     * @param aspectRatio input in a form "16/9"
     * @return a fraction object representing the aspect ratio.
     */
    public static BigFraction parseAspectRatio(String aspectRatio) {
        String[] parts = aspectRatio.split("/");
        try {
            if (parts.length == 2) {
                return new BigFraction(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
            } else if (parts.length == 1) {
                return new BigFraction(Long.parseLong(parts[0]));
            }
        } catch (NumberFormatException e) {
            throw new ConversionException("Incorrect aspect ratio! Aspect ratio must consist of two numbers.", e);
        }
        throw new ConversionException("Incorrect aspect ratio! Aspect ratio must consist of two values.");
    }

    public static String zeroTimecode() {
        return "00:00:00.000";
    }

}


