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
package com.netflix.imfutility.ttmltostl.ttml;

import com.netflix.imfutility.ttmltostl.util.TtmlTestUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * Tests for TTML parsing.
 */
public class ParseTtmlTest {

    @Test
    public void testParsedCaptionsCount() throws Exception {
        TimedTextObject tto = new FormatTTML().parseFile(TtmlTestUtil.getTtml("xml/debate.xml"), 0, 0, 0);
        assertEquals(5, tto.getCaptions().size());
    }

    @Test
    public void testParseTimesFractions() throws Exception {
        TimedTextObject tto = new FormatTTML().parseFile(TtmlTestUtil.getTtml("xml/debate.xml"), 0, 0, 0);
        List<Caption> captions = new ArrayList<>(tto.getCaptions().values());

        Caption caption1 = captions.get(0);
        assertEquals(7800, caption1.getStart().getMseconds());
        assertEquals(11360, caption1.getEnd().getMseconds());

        Caption caption2 = captions.get(1);
        assertEquals(13160, caption2.getStart().getMseconds());
        assertEquals(15040, caption2.getEnd().getMseconds());

        Caption caption3 = captions.get(2);
        assertEquals(94640, caption3.getStart().getMseconds());
        assertEquals(98120, caption3.getEnd().getMseconds());

        Caption caption4 = captions.get(3);
        assertEquals(36064600, caption4.getStart().getMseconds());
        assertEquals(36766007, caption4.getEnd().getMseconds());

        Caption caption5 = captions.get(4);
        assertEquals(359999998, caption5.getStart().getMseconds());
        assertEquals(359999999, caption5.getEnd().getMseconds());
    }

    @Test
    public void testParseTimesFrames() throws Exception {
        TimedTextObject tto = new FormatTTML().parseFile(TtmlTestUtil.getTtml("xml/debate1.xml"), 0, 0, 0);
        List<Caption> captions = new ArrayList<>(tto.getCaptions().values());

        Caption caption1 = captions.get(0);
        assertEquals(7000, caption1.getStart().getMseconds());
        assertEquals(11500, caption1.getEnd().getMseconds());

        Caption caption2 = captions.get(1);
        assertEquals(13533, caption2.getStart().getMseconds());
        assertEquals(15966, caption2.getEnd().getMseconds());

        Caption caption3 = captions.get(2);
        assertEquals(94166, caption3.getStart().getMseconds());
        assertEquals(98300, caption3.getEnd().getMseconds());

        Caption caption4 = captions.get(3);
        assertEquals(36064000, caption4.getStart().getMseconds());
        assertEquals(36064966, caption4.getEnd().getMseconds());

        Caption caption5 = captions.get(4);
        assertEquals(359999933, caption5.getStart().getMseconds());
        assertEquals(359999966, caption5.getEnd().getMseconds());
    }

    @Test
    public void testParseText() throws Exception {
        TimedTextObject tto = new FormatTTML().parseFile(TtmlTestUtil.getTtml("xml/debate.xml"), 0, 0, 0);
        List<Caption> captions = new ArrayList<>(tto.getCaptions().values());

        assertEquals("...sobre Discapacidad, CESyA y UC3 M", captions.get(1).getContent());
        assertEquals("", captions.get(4).getContent());
    }

    @Test
    public void testTrimText() throws Exception {
        TimedTextObject tto = new FormatTTML().parseFile(TtmlTestUtil.getTtml("xml/prueba_angel2.xml"), 0, 0, 0);
        List<Caption> captions = new ArrayList<>(tto.getCaptions().values());

        assertEquals("Pues buen viaje.", captions.get(0).getContent());
    }

    /**
     * All <br /> are replaces with \n. Tailing and leading separators are trimmed.
     *
     * @throws Exception
     */
    @Test
    public void testParseMultilineText() throws Exception {
        TimedTextObject tto = new FormatTTML().parseFile(TtmlTestUtil.getTtml("xml/debate.xml"), 0, 0, 0);
        List<Caption> captions = new ArrayList<>(tto.getCaptions().values());

        assertEquals("Subtitulado para personas sordas\npatrocinado por el Real Patronato...", captions.get(0).getContent());
        assertEquals("Moderadora: Bueno, señores, muchas\n gracias.\n Hay que abandonar la...", captions.get(2).getContent());
        assertEquals("...Señores.\n Usted allí, usted, aquí.", captions.get(3).getContent());
    }

    @Test
    public void testParseTextWithSpans() throws Exception {
        TimedTextObject tto = new FormatTTML().parseFile(TtmlTestUtil.getTtml("xml/prueba_angel.xml"), 0, 0, 0);
        List<Caption> captions = new ArrayList<>(tto.getCaptions().values());

        assertEquals("A la casa de socorro,\ncomo una bala.", captions.get(0).getContent());
        assertEquals("�Has abierto la puerta? \n S�.", captions.get(2).getContent());
        assertEquals("�Qui�n era?Un vendedor.", captions.get(3).getContent());
    }

    /**
     * Offset is added to all timestamps.
     *
     * @throws Exception
     */
    @Test
    public void testOffset() throws Exception {
        TimedTextObject tto = new FormatTTML().parseFile(TtmlTestUtil.getTtml("xml/debate1.xml"), 0, 0, 2000);
        List<Caption> captions = new ArrayList<>(tto.getCaptions().values());

        assertEquals(5, captions.size());

        Caption caption1 = captions.get(0);
        assertEquals(9000, caption1.getStart().getMseconds());
        assertEquals(13500, caption1.getEnd().getMseconds());

        Caption caption2 = captions.get(1);
        assertEquals(15533, caption2.getStart().getMseconds());
        assertEquals(17966, caption2.getEnd().getMseconds());

        Caption caption3 = captions.get(2);
        assertEquals(96166, caption3.getStart().getMseconds());
        assertEquals(100300, caption3.getEnd().getMseconds());

        Caption caption4 = captions.get(3);
        assertEquals(36066000, caption4.getStart().getMseconds());
        assertEquals(36066966, caption4.getEnd().getMseconds());

        Caption caption5 = captions.get(4);
        assertEquals(360001933, caption5.getStart().getMseconds());
        assertEquals(360001966, caption5.getEnd().getMseconds());
    }

    /**
     * Only the captions with start time > 100000.
     * start time is divided from all timestamps.
     *
     * @throws Exception
     */
    @Test
    public void testStart() throws Exception {
        TimedTextObject tto = new FormatTTML().parseFile(TtmlTestUtil.getTtml("xml/debate1.xml"), 100000, 0, 0);
        List<Caption> captions = new ArrayList<>(tto.getCaptions().values());

        assertEquals(2, captions.size());

        Caption caption1 = captions.get(0);
        assertEquals(35964000, caption1.getStart().getMseconds());
        assertEquals(35964966, caption1.getEnd().getMseconds());

        Caption caption2 = captions.get(1);
        assertEquals(359899933, caption2.getStart().getMseconds());
        assertEquals(359899966, caption2.getEnd().getMseconds());
    }

    /**
     * If the start time is in between caption's begin/end, then we cut the caption.
     *
     * @throws Exception
     */
    @Test
    public void testStartInBetween() throws Exception {
        TimedTextObject tto = new FormatTTML().parseFile(TtmlTestUtil.getTtml("xml/debate1.xml"), 95000, 0, 0);
        List<Caption> captions = new ArrayList<>(tto.getCaptions().values());

        assertEquals(3, captions.size());

        Caption caption1 = captions.get(0);
        assertEquals(0, caption1.getStart().getMseconds());
        assertEquals(3300, caption1.getEnd().getMseconds());

        Caption caption2 = captions.get(1);
        assertEquals(35969000, caption2.getStart().getMseconds());
        assertEquals(35969966, caption2.getEnd().getMseconds());

        Caption caption3 = captions.get(2);
        assertEquals(359904933, caption3.getStart().getMseconds());
        assertEquals(359904966, caption3.getEnd().getMseconds());
    }

    /**
     * Only the captions with end time < 90000.
     *
     * @throws Exception
     */
    @Test
    public void testEndTime() throws Exception {
        TimedTextObject tto = new FormatTTML().parseFile(TtmlTestUtil.getTtml("xml/debate.xml"), 0, 90000, 0);
        List<Caption> captions = new ArrayList<>(tto.getCaptions().values());

        assertEquals(2, captions.size());

        Caption caption1 = captions.get(0);
        assertEquals(7800, caption1.getStart().getMseconds());
        assertEquals(11360, caption1.getEnd().getMseconds());

        Caption caption2 = captions.get(1);
        assertEquals(13160, caption2.getStart().getMseconds());
        assertEquals(15040, caption2.getEnd().getMseconds());
    }

    /**
     * If the end time is in between caption's begin/end, then we cut the caption.
     *
     * @throws Exception
     */
    @Test
    public void testEndTimeInBetween() throws Exception {
        TimedTextObject tto = new FormatTTML().parseFile(TtmlTestUtil.getTtml("xml/debate.xml"), 0, 95000, 0);
        List<Caption> captions = new ArrayList<>(tto.getCaptions().values());

        assertEquals(3, captions.size());

        Caption caption1 = captions.get(0);
        assertEquals(7800, caption1.getStart().getMseconds());
        assertEquals(11360, caption1.getEnd().getMseconds());

        Caption caption2 = captions.get(1);
        assertEquals(13160, caption2.getStart().getMseconds());
        assertEquals(15040, caption2.getEnd().getMseconds());

        Caption caption3 = captions.get(2);
        assertEquals(94640, caption3.getStart().getMseconds());
        assertEquals(95000, caption3.getEnd().getMseconds());
    }

    /**
     * If both end and start time is in between caption's begin/end, then we cut the caption.
     *
     * @throws Exception
     */
    @Test
    public void testStartAndEndTimeInBetween() throws Exception {
        TimedTextObject tto = new FormatTTML().parseFile(TtmlTestUtil.getTtml("xml/debate.xml"), 14000, 95000, 0);
        List<Caption> captions = new ArrayList<>(tto.getCaptions().values());

        assertEquals(2, captions.size());

        Caption caption1 = captions.get(0);
        assertEquals(0, caption1.getStart().getMseconds());
        assertEquals(1040, caption1.getEnd().getMseconds());

        Caption caption2 = captions.get(1);
        assertEquals(80640, caption2.getStart().getMseconds());
        assertEquals(81000, caption2.getEnd().getMseconds());
    }

    /**
     * If both start time, end time and offset are present.
     *
     * @throws Exception
     */
    @Test
    public void testStartEndOffset() throws Exception {
        TimedTextObject tto = new FormatTTML().parseFile(TtmlTestUtil.getTtml("xml/debate.xml"), 14000, 95000, 5000);
        List<Caption> captions = new ArrayList<>(tto.getCaptions().values());

        assertEquals(2, captions.size());

        Caption caption1 = captions.get(0);
        assertEquals(5000, caption1.getStart().getMseconds());
        assertEquals(6040, caption1.getEnd().getMseconds());

        Caption caption2 = captions.get(1);
        assertEquals(85640, caption2.getStart().getMseconds());
        assertEquals(86000, caption2.getEnd().getMseconds());
    }

    @Test
    public void testColorNumberSign() throws Exception {
        TimedTextObject tto = new FormatTTML().parseFile(TtmlTestUtil.getTtml("xml/debate2.xml"), 0, 0, 0);
        List<Caption> captions = new ArrayList<>(tto.getCaptions().values());

        assertEquals("000000ff", captions.get(0).getStyle().getColor());
        assertEquals("ff0000ff", captions.get(1).getStyle().getColor());
        assertEquals("00ff00ff", captions.get(2).getStyle().getColor());
        assertEquals("ffff00ff", captions.get(3).getStyle().getColor());
        assertEquals("0000ffff", captions.get(4).getStyle().getColor());
        assertEquals("ff00ffff", captions.get(5).getStyle().getColor());
        assertEquals("00ffffff", captions.get(6).getStyle().getColor());
        assertEquals("ffffffff", captions.get(7).getStyle().getColor());
        assertEquals("0a0b0c0d", captions.get(8).getStyle().getColor());
    }

    @Test
    public void testColorName() throws Exception {
        TimedTextObject tto = new FormatTTML().parseFile(TtmlTestUtil.getTtml("xml/debate4.xml"), 0, 0, 0);
        List<Caption> captions = new ArrayList<>(tto.getCaptions().values());

        assertEquals("00000000", captions.get(0).getStyle().getColor());
        assertEquals("ff0000ff", captions.get(1).getStyle().getColor());
        assertEquals("00ff00ff", captions.get(2).getStyle().getColor());
        assertEquals("ffff00ff", captions.get(3).getStyle().getColor());
        assertEquals("0000ffff", captions.get(4).getStyle().getColor());
        assertEquals("ff00ffff", captions.get(5).getStyle().getColor());
        assertEquals("00ffffff", captions.get(6).getStyle().getColor());
        assertEquals("ffffffff", captions.get(7).getStyle().getColor());
        assertEquals("ffffffff", captions.get(8).getStyle().getColor());
    }


}
