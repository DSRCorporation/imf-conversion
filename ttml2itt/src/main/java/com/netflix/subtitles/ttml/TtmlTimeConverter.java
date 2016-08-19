/**
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
package com.netflix.subtitles.ttml;

import com.netflix.imfutility.util.ConversionHelper;
import java.util.Objects;
import org.apache.commons.math3.fraction.BigFraction;
import org.w3.ns.ttml.TtEltype;
import org.w3.ns.ttml_datatype.DropMode;

/**
 * Includes ttml timeExpression manipulation functions.
 */
public final class TtmlTimeConverter {
    private final int frameRate;
    private final int frNumerator;
    private final int frDenominator;
    private final DropMode dropMode;
    private final int tickRate;
    private final int subFrameRate;

    /**
     * Constructor.
     *
     * @param tt ttml root element
     */
    public TtmlTimeConverter(TtEltype tt) {
        frameRate = (tt.getFrameRate() == null) ? 30 : tt.getFrameRate().intValue();

        if (tt.getFrameRateMultiplier() != null) {
            String[] mul = tt.getFrameRateMultiplier().split("\\s+");
            frNumerator = Integer.parseInt(mul[0]);
            frDenominator = Integer.parseInt(mul[1]);
        } else {
            frNumerator = 1;
            frDenominator = 1;
        }

        tickRate = (tt.getTickRate() == null) ? 1 : tt.getTickRate().intValue();
        subFrameRate = (tt.getSubFrameRate() == null) ? 1 : tt.getSubFrameRate().intValue();

        // TOTO: currently supported only nonDrop, later shoul be used from tt.getDropMode()
        dropMode = DropMode.NON_DROP;
    }

    /**
     * Parses ttml timeExpression.
     * <p></p>
     * <pre>
     * &lt;timeExpression&gt;
     *   : clock-time
     *   | offset-time
     *
     * clock-time
     *   : hours ":" minutes ":" seconds ( fraction | ":" frames ( "." sub-frames )? )?
     *
     * offset-time
     *   : time-count fraction? metric
     *
     * hours
     *   : &lt;digit&gt; &lt;digit&gt;
     *   | &lt;digit&gt; &lt;digit&gt; &lt;digit&gt;+
     *
     * minutes | seconds
     *   : &lt;digit&gt; &lt;digit&gt;
     *
     * frames
     *   : &lt;digit&gt; &lt;digit&gt;
     *   | &lt;digit&gt; &lt;digit&gt; &lt;digit&gt;+
     *
     * sub-frames
     *   : &lt;digit&gt;+
     *
     * fraction
     *   : "." &lt;digit&gt;+
     *
     * time-count
     *   : &lt;digit&gt;+
     *
     * metric
     *   : "h"                 // hours
     *   | "m"                 // minutes
     *   | "s"                 // seconds
     *   | "ms"                // milliseconds
     *   | "f"                 // frames
     *   | "t"                 // ticks
     * </pre>
     *
     * @param timeExpression string representation of q ttml time expression
     * @return parsed time expression in millis or 0
     */
    public long parseTimeExpression(String timeExpression) {
        int mSeconds = 0;
        if (timeExpression == null || timeExpression.isEmpty()) {
            return mSeconds;
        }

        if (timeExpression.contains(":")) {
            // clock time
            String[] parts = timeExpression.split(":");
            if (parts.length == 3) {
                int h, m;
                float s;
                h = Integer.parseInt(parts[0]);
                m = Integer.parseInt(parts[1]);
                s = Float.parseFloat(parts[2]);
                mSeconds = h * 3600000 + m * 60000 + (int) (s * 1000);
            } else if (parts.length == 4) {
                // TODO: dropMode should be considered
                int h, m, s;
                float f;
                String[] fParts = (parts[3].contains(".")) ? parts[3].split("\\.") : new String[]{parts[3], "0"};
                h = Integer.parseInt(parts[0]);
                m = Integer.parseInt(parts[1]);
                s = Integer.parseInt(parts[2]);
                f = Float.parseFloat(fParts[0]) + Float.parseFloat(fParts[1]) / subFrameRate;
                mSeconds = h * 3600000 + m * 60000 + s * 1000
                        + (int) (f * 1000 * frDenominator / (frameRate * frNumerator));
            } else {
                // unrecognized  clock time format, nothing to do
            }
        } else {
            // offset time
            int metricLength = (timeExpression.contains("ms")) ? 2 : 1;
            String metric = timeExpression.substring(timeExpression.length() - metricLength);
            timeExpression =
                    timeExpression.substring(0, timeExpression.length() - metricLength).replace(',', '.').trim();
            double time;
            try {
                time = Double.parseDouble(timeExpression);
                if (metric.equalsIgnoreCase("h")) {
                    mSeconds = (int) (time * 3600000);
                } else if (metric.equalsIgnoreCase("m")) {
                    mSeconds = (int) (time * 60000);
                } else if (metric.equalsIgnoreCase("s")) {
                    mSeconds = (int) (time * 1000);
                } else if (metric.equalsIgnoreCase("ms")) {
                    mSeconds = (int) time;
                } else if (metric.equalsIgnoreCase("f")) {
                    mSeconds = (int) (time * 1000 * frDenominator / (frameRate * frNumerator));
                } else if (metric.equalsIgnoreCase("t")) {
                    mSeconds = (int) (time * 1000 / tickRate);
                } else {
                    //invalid metric
                }
            } catch (NumberFormatException e) {
                //incorrect format for offset time
            }
        }

        return mSeconds;
    }

    /**
     * Gets units in second string in the following format: &lt;frameRate * numerator&gt; &lt;denominator&gt;.
     * @return units in second string in the following format: &lt;frameRate * numerator&gt; &lt;denominator&gt;
     */
    public String getUnitsInSecStr() {
        return String.valueOf(frameRate * frNumerator) + " " + String.valueOf(frDenominator);
    }

    /**
     * Returns a fraction corresponding to the frameRate.
     *
     * @return fraction corresponding to the frameRate
     */
    public BigFraction getUnitsInSec() {
        return ConversionHelper.parseEditRate(getUnitsInSecStr());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof TtmlTimeConverter)) {
            return false;
        }

        TtmlTimeConverter ttConverter = (TtmlTimeConverter) o;

        return this.dropMode == ttConverter.dropMode
                && this.frDenominator == ttConverter.frDenominator
                && this.frNumerator == ttConverter.frNumerator
                && this.frameRate == ttConverter.frameRate
                && this.subFrameRate == ttConverter.subFrameRate
                && this.tickRate == ttConverter.tickRate;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.frameRate;
        hash = 89 * hash + this.frNumerator;
        hash = 89 * hash + this.frDenominator;
        hash = 89 * hash + Objects.hashCode(this.dropMode);
        hash = 89 * hash + this.tickRate;
        hash = 89 * hash + this.subFrameRate;
        return hash;
    }
}
