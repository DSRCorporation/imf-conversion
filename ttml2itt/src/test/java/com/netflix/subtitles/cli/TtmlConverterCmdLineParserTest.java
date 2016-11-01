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
package com.netflix.subtitles.cli;

import com.netflix.subtitles.exception.ParseException;
import org.apache.commons.math3.fraction.BigFraction;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Command line parser test class.
 */
public class TtmlConverterCmdLineParserTest {

    @Test
    public void helpOptionParsedCorrectly() {
        /* PREPARATION */
        String[] args = new String[]{"-h", "-t"};

        /* EXECUTION */
        TtmlConverterCmdLineParams params = new TtmlConverterCmdLineParser().parse(args);

        /* VALIDATION */
        assertEquals("Help option", null, params);
    }

    @Test
    public void outputFileOptionParsedCorrectly() {
        /* PREPARATION */
        String[] args = new String[]{"-t", "test", "-o", "testFile"};

        /* EXECUTION */
        TtmlConverterCmdLineParams params = new TtmlConverterCmdLineParser().parse(args);

        /* VALIDATION */
        assertEquals("Output file option", args[3], params.getOutputFile());
    }

    @Test
    public void frameRateOptionParsedCorrectly() {
        /* PREPARATION */
        String[] args = new String[]{"-t", "test", "-o", "testFile", "-f", "24000/1001"};

        /* EXECUTION */
        TtmlConverterCmdLineParams params = new TtmlConverterCmdLineParser().parse(args);

        /* VALIDATION */
        assertEquals("Frame rate option", new BigFraction(24000).divide(1001), params.getFrameRate());
    }

    @Test
    public void ttmlOptionParsedCorrectly() {
        /* PREPARATION */
        String[] args = new String[]{"-t", "test", "0", "200", "45", "-o", "testFile", "-f", "24000/1001"};

        /* EXECUTION */
        TtmlConverterCmdLineParams params = new TtmlConverterCmdLineParser().parse(args);

        /* VALIDATION */
        assertEquals(1, params.getTtmlOptions().size());

        TtmlOption option = params.getTtmlOptions().get(0);
        assertEquals("ttml option file part", args[1], option.getFileName());
        assertEquals("ttml option offset part", Long.parseLong(args[2]), option.getOffsetMS());
        assertEquals("ttml option start part", Long.parseLong(args[3]), option.getStartMS());
        assertEquals("ttml option end part", Long.parseLong(args[4]), option.getEndMS());
    }

    @Test
    public void manyTtmlOptionsParsedCorrectly() {
        /* PREPARATION */
        String[] args = new String[]{"-t", "test1", "0", "200", "45", "-t", "test2", "10", "40", "67", "-o", "test"};

        /* EXECUTION */
        TtmlConverterCmdLineParams params = new TtmlConverterCmdLineParser().parse(args);

        /* VALIDATION */
        assertEquals(2, params.getTtmlOptions().size());

        TtmlOption option1 = params.getTtmlOptions().get(0);
        assertEquals("ttml option file part", args[1], option1.getFileName());
        assertEquals("ttml option offset part", Long.parseLong(args[2]), option1.getOffsetMS());
        assertEquals("ttml option start part", Long.parseLong(args[3]), option1.getStartMS());
        assertEquals("ttml option end part", Long.parseLong(args[4]), option1.getEndMS());

        TtmlOption option2 = params.getTtmlOptions().get(1);
        assertEquals("ttml option file part", args[6], option2.getFileName());
        assertEquals("ttml option offset part", Long.parseLong(args[7]), option2.getOffsetMS());
        assertEquals("ttml option start part", Long.parseLong(args[8]), option2.getStartMS());
        assertEquals("ttml option end part", Long.parseLong(args[9]), option2.getEndMS());
    }

    @Test
    public void ttmlOptionWithoutOffsetArgsPrasedCorrectly() {
        /* PREPARATION */
        String[] args = new String[]{"-t", "file", "-o", "test"};

        /* EXECUTION */
        TtmlConverterCmdLineParams params = new TtmlConverterCmdLineParser().parse(args);

        /* VALIDATION */
        assertEquals(1, params.getTtmlOptions().size());

        TtmlOption option = params.getTtmlOptions().get(0);
        assertEquals("ttml option file part", args[1], option.getFileName());
        assertEquals("ttml option offset part", 0, option.getOffsetMS());
        assertEquals("ttml option start part", 0, option.getStartMS());
        assertEquals("ttml option end part", Long.MAX_VALUE, option.getEndMS());
    }

    @Test(expected = ParseException.class)
    public void outputFileOptionWithoutArgThrowException() {
        /* PREPARATION */
        String[] args = new String[]{"-t", "test", "-o"};

        /* EXECUTION */
        new TtmlConverterCmdLineParser().parse(args);

        /* VALIDATION */
    }

    @Test(expected = ParseException.class)
    public void outputFileOptionIsNotSetThenThrowException() {
        /* PREPARATION */
        String[] args = new String[]{"-t", "test"};

        /* EXECUTION */
        new TtmlConverterCmdLineParser().parse(args);

        /* VALIDATION */
    }

    @Test(expected = ParseException.class)
    public void frameRateOptionWithoutArgThrowException() {
        /* PREPARATION */
        String[] args = new String[]{"-t", "test", "-o", "test", "-f"};

        /* EXECUTION */
        new TtmlConverterCmdLineParser().parse(args);

        /* VALIDATION */
    }

    @Test(expected = ParseException.class)
    public void frameRateOptionFailParseThrowException() {
        /* PREPARATION */
        String[] args = new String[]{"-t", "test", "-o", "test", "-f", "24000\\1001"};

        /* EXECUTION */
        new TtmlConverterCmdLineParser().parse(args);

        /* VALIDATION */
    }

    @Test(expected = ParseException.class)
    public void ttmlOptionWithoutFileArgThrowException() {
        /* PREPARATION */
        String[] args = new String[]{"-o", "test", "-t"};

        /* EXECUTION */
        new TtmlConverterCmdLineParser().parse(args);

        /* VALIDATION */
    }

    @Test(expected = ParseException.class)
    public void ttmlOptionIsNotSetThenThrowException() {
        /* PREPARATION */
        String[] args = new String[]{"-o", "test"};

        /* EXECUTION */
        new TtmlConverterCmdLineParser().parse(args);

        /* VALIDATION */
    }
}
