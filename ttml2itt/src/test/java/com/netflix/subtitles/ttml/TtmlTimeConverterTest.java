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

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.w3.ns.ttml.TtEltype;

/**
 * Tests TTML timeExpression parsing and conversion to ms.
 */
public class TtmlTimeConverterTest {

    @Test
    public void clockTimeWithoutFractionsAndFramesParsedCorrecly() {
        /* PREPARATION */
        TtmlTimeConverter converter = new TtmlTimeConverter(new TtEltype());
        int h = 1;
        int m = 2;
        int s = 3;
        String timeExpression = String.format("%02d:%02d:%02d", h, m, s);
        long expectedMs = (h * 3600 + m * 60 + s) * 1000;

        /* EXECUTION */
        long ms = converter.parseTimeExpression(timeExpression);

        /* VALIDATION */
        assertEquals(expectedMs, ms);
    }

    @Test
    public void clockTimeWithFramesNonDropParsedCorrecly() {
        /* PREPARATION */
        TtmlTimeConverter converter = new TtmlTimeConverter(new TtEltype()); // default frame rate 30
        int h = 1;
        int m = 2;
        int s = 3;
        int f = 10;
        String timeExpression = String.format("%02d:%02d:%02d:%02d", h, m, s, f);
        long expectedMs = (h * 3600 + m * 60 + s) * 1000 + (int)((float) f * 1000 / 30);

        /* EXECUTION */
        long ms = converter.parseTimeExpression(timeExpression);

        /* VALIDATION */
        assertEquals(expectedMs, ms);
    }

    @Test
    public void offsetTimeMsParsedCorrecly() {
        /* PREPARATION */
        TtmlTimeConverter converter = new TtmlTimeConverter(new TtEltype());
        int expectedMs = 10;
        String timeExpression = String.format("%dms", expectedMs);

        /* EXECUTION */
        long ms = converter.parseTimeExpression(timeExpression);

        /* VALIDATION */
        assertEquals(expectedMs, ms);
    }
}
