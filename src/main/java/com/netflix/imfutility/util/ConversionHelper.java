package com.netflix.imfutility.util;

import org.apache.commons.math3.fraction.BigFraction;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by Alexander on 5/15/2016.
 */
public final class ConversionHelper {

    private ConversionHelper() {
    }

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

    public static BigFraction parseEditRate(List<Long> editRate) {
        if (editRate.size() != 2) {
            throw new RuntimeException("Incorrect edit rate! Edit rate must consist of two values.");
        }
        return new BigFraction(editRate.get(1), editRate.get(0));
    }

    public static String zeroTimecode() {
        return "00:00:00.000";
    }

}


