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
package com.netflix.subtitles;

import com.netflix.subtitles.cli.TtmlConverterCmdLineParams;
import com.netflix.subtitles.util.TtmlOptionBuilder;
import java.io.File;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.w3.ns.ttml.BodyEltype;
import org.w3.ns.ttml.LayoutEltype;
import org.w3.ns.ttml.RegionEltype;
import org.w3.ns.ttml.StyleEltype;
import org.w3.ns.ttml.TtEltype;
import org.w3.ns.ttml_datatype.DisplayAlign;
import static org.w3.ns.ttml_datatype.DropMode.NON_DROP;
import org.w3.ns.ttml_datatype.FontStyle;
import org.w3.ns.ttml_datatype.FontWeight;
import org.w3.ns.ttml_datatype.TextAlign;
import static org.w3.ns.ttml_datatype.TimeBase.SMPTE;

/**
 * Main logic of ttml to itt conversion tests.
 */
public class TtmlConverterTest {

    @Test
    public void convertingToIttPasses() throws Exception {
        /* PREPARATION */
        File tmpOut = File.createTempFile("ttl2itt", ".itt");
        tmpOut.deleteOnExit();

        TtmlConverterCmdLineParams params = new TtmlConverterCmdLineParams();
        params.setOutputFile(tmpOut.getAbsolutePath());
        params.getTtmlOptions()
                .add(new TtmlOptionBuilder().withFileName(getTtmlFile("xml/valid-ttml-2997-ndf.xml"))
                        .withStartMS(15000)
                        .withEndMS(19000)
                        .build());
        params.getTtmlOptions()
                .add(new TtmlOptionBuilder().withFileName(getTtmlFile("xml/valid-itt-2397-ndf.itt"))
                        .withOffsetMS(5000)
                        .withStartMS(256000)
                        .build());

        TtmlConverter converter = new TtmlConverter(params);

        /* EXECUTION */
        converter.convertInputsToItt();

        /* VALIDATION */
        assertEquals(2, converter.getConvertedItts().size());

        TtEltype tt0 = converter.getConvertedItts().get(0);
        // tt attributes
        assertEquals("en-US", tt0.getLang());
        assertEquals(SMPTE, tt0.getTimeBase());
        assertEquals(30, tt0.getFrameRate().intValue());
        assertEquals("1000 1001", tt0.getFrameRateMultiplier());
        assertEquals(NON_DROP, tt0.getDropMode());

        // head style and layout
        assertNotNull(tt0.getHead().getStyling());
        assertTrue(tt0.getHead().getStyling().getStyle().size() >= 1);
        assertStyleEquals(FontWeight.NORMAL, FontStyle.NORMAL, "white", tt0.getHead().getStyling().getStyle().get(0));
        assertDefaultLayout(tt0.getHead().getLayout());

        // body
        BodyEltype body0 = tt0.getBody();
        assertEquals(4, body0.getDiv().get(0).getBlockClass().size());

        TtEltype tt1 = converter.getConvertedItts().get(1);
        // tt attributes
        assertEquals("en-US", tt1.getLang());
        assertEquals(SMPTE, tt1.getTimeBase());
        assertEquals(24, tt1.getFrameRate().intValue());
        assertEquals("999 1000", tt1.getFrameRateMultiplier());
        assertEquals(NON_DROP, tt1.getDropMode());

        // head style and layout
        assertNotNull(tt1.getHead().getStyling());
        assertTrue(tt1.getHead().getStyling().getStyle().size() >= 1);
        assertStyleEquals(FontWeight.NORMAL, FontStyle.NORMAL, "white", tt1.getHead().getStyling().getStyle().get(0));
        assertStyleEquals(FontWeight.BOLD, FontStyle.NORMAL, "white", tt1.getHead().getStyling().getStyle().get(1));
        assertStyleEquals(FontWeight.NORMAL, FontStyle.ITALIC, "white", tt1.getHead().getStyling().getStyle().get(2));
        assertStyleEquals(FontWeight.NORMAL, FontStyle.NORMAL, "rgb(255,255,0)",
                tt1.getHead().getStyling().getStyle().get(3));
        assertDefaultLayout(tt1.getHead().getLayout());

        // body
        BodyEltype body1 = tt1.getBody();
        assertEquals(1, body1.getDiv().get(0).getBlockClass().size());
    }

    @Test
    public void mergingIttsPasses() throws Exception {
        /* PREPARATION */
        File tmpOut = File.createTempFile("ttl2itt", ".itt");
        tmpOut.deleteOnExit();

        TtmlConverterCmdLineParams params = new TtmlConverterCmdLineParams();
        params.setOutputFile(tmpOut.getAbsolutePath());
        params.getTtmlOptions()
                .add(new TtmlOptionBuilder().withFileName(getTtmlFile("xml/valid-ttml-2997-ndf.xml"))
                        .withStartMS(15000)
                        .withEndMS(19000)
                        .build());
        params.getTtmlOptions()
                .add(new TtmlOptionBuilder().withFileName(getTtmlFile("xml/valid-itt-2397-ndf.itt"))
                        .withOffsetMS(5000)
                        .withStartMS(256000)
                        .build());

        TtmlConverter converter = new TtmlConverter(params);
        converter.convertInputsToItt();

        /* EXECUTION */
        converter.mergeConvertedItts();

        /* VALIDATION */
        TtEltype mergedItt = converter.getConvertedItts().get(0);

        // tt attributes
        assertEquals("en-US", mergedItt.getLang());
        assertEquals(SMPTE, mergedItt.getTimeBase());
        assertEquals(30, mergedItt.getFrameRate().intValue());
        assertEquals("1000 1001", mergedItt.getFrameRateMultiplier());
        assertEquals(NON_DROP, mergedItt.getDropMode());

        // head style and layout
        assertEquals(5, mergedItt.getHead().getStyling().getStyle().size());
        assertDefaultLayout(mergedItt.getHead().getLayout());

        // body
        assertEquals(1, mergedItt.getBody().getDiv().size());
        assertEquals(5, mergedItt.getBody().getDiv().get(0).getBlockClass().size());
    }


    private String getTtmlFile(String resourcePath) {
        return ClassLoader.getSystemClassLoader().getResource(resourcePath).getFile();
    }

    private void assertDefaultLayout(LayoutEltype layout) {
        RegionEltype rt = layout.getRegion().get(0);
        assertEquals("top", rt.getId());
        assertEquals("0% 0%", rt.getOrigin());
        assertEquals("100% 15%", rt.getExtent());
        assertEquals(TextAlign.CENTER, rt.getTextAlign());
        assertEquals(DisplayAlign.BEFORE, rt.getDisplayAlign());

        RegionEltype rb = layout.getRegion().get(1);
        assertEquals("bottom", rb.getId());
        assertEquals("0% 85%", rb.getOrigin());
        assertEquals("100% 15%", rb.getExtent());
        assertEquals(TextAlign.CENTER, rb.getTextAlign());
        assertEquals(DisplayAlign.AFTER, rb.getDisplayAlign());
    }

    private void assertStyleEquals(FontWeight fontWeight, FontStyle fontStyle, String color, StyleEltype actualSt) {

        assertNotNull(actualSt.getId());
        assertEquals("sansSerif", actualSt.getFontFamily());
        assertEquals(fontWeight, actualSt.getFontWeight());
        assertEquals(fontStyle, actualSt.getFontStyle());
        assertEquals(color, actualSt.getColor());
        assertEquals("100%", actualSt.getFontSize());
    }
}
