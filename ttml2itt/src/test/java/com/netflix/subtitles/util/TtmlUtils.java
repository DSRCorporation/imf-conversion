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
package com.netflix.subtitles.util;

import org.w3.ns.ttml.BodyEltype;
import org.w3.ns.ttml.DivEltype;
import org.w3.ns.ttml.HeadEltype;
import org.w3.ns.ttml.PEltype;
import org.w3.ns.ttml.StyleEltype;
import org.w3.ns.ttml.StylingEltype;
import org.w3.ns.ttml.TtEltype;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Test utils for ttml needs.
 */
public class TtmlUtils {

    public static PEltype createP(String begin, String end, String content) {
        PEltype p = new PEltype();
        p.setBegin(begin);
        p.setEnd(end);
        p.getContent().add(content);
        return p;
    }

    public static PEltype createPWithRegionAndStyle(String begin, String end, String content, String region, String style) {
        PEltype p = createP(begin, end, content);
        p.setRegion(region);
        p.getStyle().add(style);
        return p;
    }

    public static TtEltype wrapPs(PEltype... ps) {
        DivEltype div = new DivEltype();
        div.getBlockClass().addAll(Arrays.asList(ps));

        BodyEltype body = new BodyEltype();
        body.getDiv().add(div);

        TtEltype tt = new TtEltype();
        tt.setBody(body);
        tt.setFrameRate(new BigInteger("30"));
        tt.setFrameRateMultiplier("1000 1001");
        return tt;
    }

    public static void ensureFakeStylesCreated(TtEltype tt, String... styles) {
        StylingEltype styling = new StylingEltype();
        Stream.of(styles).map(style -> {
            StyleEltype styleEl = new StyleEltype();
            styleEl.setId(style);
            return styleEl;
        }).forEach(styling.getStyle()::add);

        HeadEltype head = new HeadEltype();
        head.setStyling(styling);

        tt.setHead(head);
    }

    public static String getPBegin(Object obj) {
        return ((PEltype) obj).getBegin();
    }

    public static String getPEnd(Object obj) {
        return ((PEltype) obj).getEnd();
    }

    public static Serializable[] getPContent(Object obj) {
        return ((PEltype) obj).getContent().toArray(new Serializable[]{});
    }

    public static Object[] getPStyle(Object obj) {
        return ((PEltype) obj).getStyle().toArray(new Object[]{});
    }

    public static Object getPRegion(Object obj) {
        return ((PEltype) obj).getRegion();
    }
}
