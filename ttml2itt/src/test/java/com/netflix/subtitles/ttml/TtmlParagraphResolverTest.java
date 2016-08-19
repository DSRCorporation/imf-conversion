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
package com.netflix.subtitles.ttml;

import com.netflix.subtitles.util.TtmlUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.w3.ns.ttml.PEltype;
import org.w3.ns.ttml.TtEltype;

import java.io.Serializable;
import java.util.Iterator;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/**
 * Tests that overlapped ttml paragraphs &lt;p&gt; correctly resolved.
 */
public class TtmlParagraphResolverTest {

    @Test
    public void testStuckedParagraphs() {
        PEltype p1 = TtmlUtils.createP("00:00:01:00", "00:00:02:00", "p1");
        PEltype p2 = TtmlUtils.createP("00:00:02:00", "00:00:03:00", "p2");
        PEltype p3 = TtmlUtils.createP("00:00:03:00", "00:00:04:00", "p3");
        TtEltype tt = TtmlUtils.wrapPs(p1, p2, p3);

        new TtmlParagraphResolver(tt).resolveTimeOverlaps();

        Iterator<Object> iterator;

        iterator = tt.getBody().getDiv().get(0).getBlockClass().iterator();
        assertEquals("00:00:01:00", TtmlUtils.getPBegin(iterator.next()));
        assertEquals("00:00:02:00", TtmlUtils.getPBegin(iterator.next()));
        assertEquals("00:00:03:00", TtmlUtils.getPBegin(iterator.next()));
        assertFalse(iterator.hasNext());

        iterator = tt.getBody().getDiv().get(0).getBlockClass().iterator();
        assertEquals("00:00:02:00", TtmlUtils.getPEnd(iterator.next()));
        assertEquals("00:00:03:00", TtmlUtils.getPEnd(iterator.next()));
        assertEquals("00:00:04:00", TtmlUtils.getPEnd(iterator.next()));

        iterator = tt.getBody().getDiv().get(0).getBlockClass().iterator();
        assertArrayEquals(new Serializable[]{"p1"}, TtmlUtils.getPContent(iterator.next()));
        assertArrayEquals(new Serializable[]{"p2"}, TtmlUtils.getPContent(iterator.next()));
        assertArrayEquals(new Serializable[]{"p3"}, TtmlUtils.getPContent(iterator.next()));
    }

    @Test
    public void testOverlappedParagraphs() {
        PEltype p1 = TtmlUtils.createP("00:00:01:00", "00:00:03:00", "p1");
        PEltype p2 = TtmlUtils.createP("00:00:02:00", "00:00:04:00", "p2");
        PEltype p3 = TtmlUtils.createP("00:00:03:00", "00:00:05:00", "p3");
        TtEltype tt = TtmlUtils.wrapPs(p1, p2, p3);

        new TtmlParagraphResolver(tt).resolveTimeOverlaps();

        Iterator<Object> iterator;

        iterator = tt.getBody().getDiv().get(0).getBlockClass().iterator();
        assertEquals("00:00:01:00", TtmlUtils.getPBegin(iterator.next()));
        assertEquals("00:00:02:00", TtmlUtils.getPBegin(iterator.next()));
        assertEquals("00:00:03:00", TtmlUtils.getPBegin(iterator.next()));
        assertEquals("00:00:04:00", TtmlUtils.getPBegin(iterator.next()));
        assertFalse(iterator.hasNext());

        iterator = tt.getBody().getDiv().get(0).getBlockClass().iterator();
        assertEquals("00:00:02:00", TtmlUtils.getPEnd(iterator.next()));
        assertEquals("00:00:03:00", TtmlUtils.getPEnd(iterator.next()));
        assertEquals("00:00:04:00", TtmlUtils.getPEnd(iterator.next()));
        assertEquals("00:00:05:00", TtmlUtils.getPEnd(iterator.next()));

        iterator = tt.getBody().getDiv().get(0).getBlockClass().iterator();
        assertArrayEquals(new Serializable[]{"p1"}, TtmlUtils.getPContent(iterator.next()));
        assertArrayEquals(new Serializable[]{"p1", "p2"}, TtmlUtils.getPContent(iterator.next()));
        assertArrayEquals(new Serializable[]{"p2", "p3"}, TtmlUtils.getPContent(iterator.next()));
        assertArrayEquals(new Serializable[]{"p3"}, TtmlUtils.getPContent(iterator.next()));
    }

    @Test
    public void testParallelParagraphs() {
        PEltype p1 = TtmlUtils.createP("00:00:01:00", "00:00:04:00", "p1");
        PEltype p2 = TtmlUtils.createP("00:00:01:00", "00:00:04:00", "p2");
        TtEltype tt = TtmlUtils.wrapPs(p1, p2);

        new TtmlParagraphResolver(tt).resolveTimeOverlaps();

        Iterator<Object> iterator;

        iterator = tt.getBody().getDiv().get(0).getBlockClass().iterator();
        assertEquals("00:00:01:00", TtmlUtils.getPBegin(iterator.next()));
        assertFalse(iterator.hasNext());

        iterator = tt.getBody().getDiv().get(0).getBlockClass().iterator();
        assertEquals("00:00:04:00", TtmlUtils.getPEnd(iterator.next()));

        iterator = tt.getBody().getDiv().get(0).getBlockClass().iterator();
        assertArrayEquals(new Serializable[]{"p1", "p2"}, TtmlUtils.getPContent(iterator.next()));
    }

    @Test
    public void testGapBetweenParagraphs() {
        PEltype p1 = TtmlUtils.createP("00:00:01:00", "00:00:04:00", "p1");
        PEltype p2 = TtmlUtils.createP("00:00:10:00", "00:00:14:00", "p2");
        TtEltype tt = TtmlUtils.wrapPs(p1, p2);

        new TtmlParagraphResolver(tt).resolveTimeOverlaps();

        Iterator<Object> iterator;

        iterator = tt.getBody().getDiv().get(0).getBlockClass().iterator();
        assertEquals("00:00:01:00", TtmlUtils.getPBegin(iterator.next()));
        assertEquals("00:00:10:00", TtmlUtils.getPBegin(iterator.next()));
        assertFalse(iterator.hasNext());

        iterator = tt.getBody().getDiv().get(0).getBlockClass().iterator();
        assertEquals("00:00:04:00", TtmlUtils.getPEnd(iterator.next()));
        assertEquals("00:00:14:00", TtmlUtils.getPEnd(iterator.next()));

        iterator = tt.getBody().getDiv().get(0).getBlockClass().iterator();
        assertArrayEquals(new Serializable[]{"p1"}, TtmlUtils.getPContent(iterator.next()));
        assertArrayEquals(new Serializable[]{"p2"}, TtmlUtils.getPContent(iterator.next()));
    }

    @Test
    public void testNestedParagraphs() {
        PEltype p1 = TtmlUtils.createP("00:00:01:00", "00:00:10:00", "p1");
        PEltype p2 = TtmlUtils.createP("00:00:03:00", "00:00:08:00", "p2");
        PEltype p3 = TtmlUtils.createP("00:00:05:00", "00:00:06:00", "p3");
        TtEltype tt = TtmlUtils.wrapPs(p1, p2, p3);

        new TtmlParagraphResolver(tt).resolveTimeOverlaps();

        Iterator<Object> iterator;

        iterator = tt.getBody().getDiv().get(0).getBlockClass().iterator();
        assertEquals("00:00:01:00", TtmlUtils.getPBegin(iterator.next()));
        assertEquals("00:00:03:00", TtmlUtils.getPBegin(iterator.next()));
        assertEquals("00:00:05:00", TtmlUtils.getPBegin(iterator.next()));
        assertEquals("00:00:06:00", TtmlUtils.getPBegin(iterator.next()));
        assertEquals("00:00:08:00", TtmlUtils.getPBegin(iterator.next()));
        assertFalse(iterator.hasNext());

        iterator = tt.getBody().getDiv().get(0).getBlockClass().iterator();
        assertEquals("00:00:03:00", TtmlUtils.getPEnd(iterator.next()));
        assertEquals("00:00:05:00", TtmlUtils.getPEnd(iterator.next()));
        assertEquals("00:00:06:00", TtmlUtils.getPEnd(iterator.next()));
        assertEquals("00:00:08:00", TtmlUtils.getPEnd(iterator.next()));
        assertEquals("00:00:10:00", TtmlUtils.getPEnd(iterator.next()));

        iterator = tt.getBody().getDiv().get(0).getBlockClass().iterator();
        assertArrayEquals(new Serializable[]{"p1"}, TtmlUtils.getPContent(iterator.next()));
        assertArrayEquals(new Serializable[]{"p1", "p2"}, TtmlUtils.getPContent(iterator.next()));
        assertArrayEquals(new Serializable[]{"p1", "p2", "p3"}, TtmlUtils.getPContent(iterator.next()));
        assertArrayEquals(new Serializable[]{"p1", "p2"}, TtmlUtils.getPContent(iterator.next()));
        assertArrayEquals(new Serializable[]{"p1"}, TtmlUtils.getPContent(iterator.next()));
    }

    @Ignore
    @Test
    public void testStyleAndRegionsMerge() {
        PEltype p1 = TtmlUtils.createPWithRegionAndStyle("00:00:01:00", "00:00:08:00", "p1", "region1", "style1");
        PEltype p2 = TtmlUtils.createPWithRegionAndStyle("00:00:03:00", "00:00:10:00", "p2", "region2", "style2");
        PEltype p3 = TtmlUtils.createP("00:00:08:00", "00:00:12:00", "p2");
        TtEltype tt = TtmlUtils.wrapPs(p1, p2);
        TtmlUtils.ensureFakeStylesCreated(tt, "style1", "style2");

        new TtmlParagraphResolver(tt).resolveTimeOverlaps();

        Iterator<Object> iterator;

        iterator = tt.getBody().getDiv().get(0).getBlockClass().iterator();
        assertEquals("00:00:01:00", TtmlUtils.getPBegin(iterator.next()));
        assertEquals("00:00:03:00", TtmlUtils.getPBegin(iterator.next()));
        assertEquals("00:00:08:00", TtmlUtils.getPBegin(iterator.next()));
        assertEquals("00:00:10:00", TtmlUtils.getPBegin(iterator.next()));
        assertFalse(iterator.hasNext());

        iterator = tt.getBody().getDiv().get(0).getBlockClass().iterator();
        assertEquals("00:00:03:00", TtmlUtils.getPEnd(iterator.next()));
        assertEquals("00:00:08:00", TtmlUtils.getPEnd(iterator.next()));
        assertEquals("00:00:10:00", TtmlUtils.getPEnd(iterator.next()));
        assertEquals("00:00:12:00", TtmlUtils.getPEnd(iterator.next()));

        iterator = tt.getBody().getDiv().get(0).getBlockClass().iterator();
        assertArrayEquals(new Serializable[]{"p1"}, TtmlUtils.getPContent(iterator.next()));
        assertArrayEquals(new Serializable[]{"p1", "p2"}, TtmlUtils.getPContent(iterator.next()));
        assertArrayEquals(new Serializable[]{"p2"}, TtmlUtils.getPContent(iterator.next()));

        iterator = tt.getBody().getDiv().get(0).getBlockClass().iterator();
        assertEquals("region1", TtmlUtils.getPRegion(iterator.next()));
        assertEquals("region1", TtmlUtils.getPRegion(iterator.next()));
        assertEquals("region2", TtmlUtils.getPRegion(iterator.next()));
        assertNull(TtmlUtils.getPRegion(iterator.next()));

        iterator = tt.getBody().getDiv().get(0).getBlockClass().iterator();
        assertArrayEquals(new Object[]{"style1"}, TtmlUtils.getPStyle(iterator.next()));
        assertArrayEquals(new Object[]{"style1"}, TtmlUtils.getPStyle(iterator.next()));
        assertArrayEquals(new Object[]{"style1"}, TtmlUtils.getPStyle(iterator.next()));
        assertArrayEquals(new Object[]{}, TtmlUtils.getPStyle(iterator.next()));
    }

}
