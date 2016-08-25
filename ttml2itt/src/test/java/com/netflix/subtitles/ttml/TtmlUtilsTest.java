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

import static com.netflix.subtitles.TtmlConverterConstants.STYLE_FIELD;
import com.netflix.subtitles.util.PBuilder;
import static com.netflix.subtitles.util.TtmlTestUtils.createStyle;
import static com.netflix.subtitles.util.TtmlTestUtils.getPBegin;
import static com.netflix.subtitles.util.TtmlTestUtils.getPEnd;
import static com.netflix.subtitles.util.TtmlTestUtils.p;
import java.lang.reflect.Field;
import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.w3.ns.ttml.BodyEltype;
import org.w3.ns.ttml.DivEltype;
import org.w3.ns.ttml.HeadEltype;
import org.w3.ns.ttml.StylingEltype;
import org.w3.ns.ttml.TtEltype;

/**
 * Tests TTML utilities methods: reduce document by time, style mover and etc.
 */
public class TtmlUtilsTest {

    @Test
    public void reduceDocumentAccordingToGivenOffsetAndStartAndEnd() {
        /* PREPARATION */
        TtEltype tt = new TtEltype();
        tt.setBody(new BodyEltype());

        BodyEltype body = tt.getBody();
        body.setBegin("-01:00:00");

        body.getDiv().add(new DivEltype());
        body.getDiv().add(new DivEltype());

        DivEltype div1 = body.getDiv().get(0);
        div1.getBlockClass().add(new PBuilder().withBegin("01:00:05:00").withEnd("01:00:10:00").build());
        div1.getBlockClass().add(new PBuilder().withBegin("01:00:13:00").withEnd("01:00:17:00").withDur("5s").build());
        div1.getBlockClass().add(new PBuilder().withBegin("01:00:18:00").withEnd("01:00:25:00").withDur("6s").build());

        DivEltype div2 = body.getDiv().get(1);
        div2.getBlockClass().add(new PBuilder().withBegin("01:00:07:00").withDur("1s").build());
        div2.getBlockClass().add(new PBuilder().withBegin("01:00:21:00").withEnd("01:00:26:00").build());
        div2.getBlockClass().add(new PBuilder().withBegin("01:00:30:00").withEnd("01:00:58:00"));

        /* EXECUTION */
        TtmlUtils.reduceAccordingSegment(tt, 0, 8000, 23000);

        /* VALIDATION */
        assertEquals(null, tt.getBody().getBegin());
        assertEquals(null, tt.getBody().getEnd());
        assertEquals(null, tt.getBody().getDur());
        assertEquals(null, div1.getBegin());
        assertEquals(null, div1.getEnd());
        assertEquals(null, div1.getDur());
        assertEquals(null, div2.getBegin());
        assertEquals(null, div2.getEnd());
        assertEquals(null, div2.getDur());

        assertEquals(3, div1.getBlockClass().size());
        assertEquals("00:00:00:00", getPBegin(div1.getBlockClass().get(0)));
        assertEquals("00:00:02:00", getPEnd(div1.getBlockClass().get(0)));
        assertEquals("00:00:05:00", getPBegin(div1.getBlockClass().get(1)));
        assertEquals("00:00:09:00", getPEnd(div1.getBlockClass().get(1)));
        assertEquals("00:00:10:00", getPBegin(div1.getBlockClass().get(2)));
        assertEquals("00:00:15:00", getPEnd(div1.getBlockClass().get(2)));

        assertEquals(2, div2.getBlockClass().size());
        assertEquals("00:00:00:00", getPBegin(div2.getBlockClass().get(0)));
        assertEquals("00:00:00:00", getPEnd(div2.getBlockClass().get(0)));
        assertEquals("00:00:13:00", getPBegin(div2.getBlockClass().get(1)));
        assertEquals("00:00:15:00", getPEnd(div2.getBlockClass().get(1)));
    }

    @Test
    public void moveAllStyleReferencesFromBodyAndDivToP() throws Exception {
        /* PREPARATION */
        TtEltype tt = new TtEltype();
        tt.setHead(new HeadEltype());
        tt.setBody(new BodyEltype());

        HeadEltype head = tt.getHead();
        head.setStyling(new StylingEltype());

        StylingEltype styling = head.getStyling();
        styling.getStyle().add(createStyle("style0"));
        styling.getStyle().add(createStyle("style1"));
        styling.getStyle().add(createStyle("style2"));
        styling.getStyle().add(createStyle("style3"));
        styling.getStyle().add(createStyle("style4"));

        BodyEltype body = tt.getBody();
        body.getStyle().add(styling.getStyle().get(0).getId());
        body.getStyle().add(styling.getStyle().get(1).getId());

        body.getDiv().add(new DivEltype());

        DivEltype div = body.getDiv().get(0);
        div.getStyle().add(styling.getStyle().get(2).getId());

        div.getBlockClass().add(new PBuilder().withStyle(styling.getStyle().get(3).getId()).build());
        div.getBlockClass().add(new PBuilder().withStyle(styling.getStyle().get(4).getId()).build());

        /* EXECUTION */
        TtmlUtils.moveStyleRefToP(tt);

        /* VALIDATION */
        Field bodyField = body.getClass().getDeclaredField(STYLE_FIELD);
        bodyField.setAccessible(true);
        Field divField = body.getClass().getDeclaredField(STYLE_FIELD);
        divField.setAccessible(true);

        assertEquals(null, bodyField.get(body));
        assertEquals(null, divField.get(body));

        assertEquals("style0 style1 style2 style3", p(div.getBlockClass().get(0))
                .getStyle().stream().map(Object::toString).collect(Collectors.joining(" ")));
        assertEquals("style0 style1 style2 style4", p(div.getBlockClass().get(1))
                .getStyle().stream().map(Object::toString).collect(Collectors.joining(" ")));
    }
}
