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
package com.netflix.imfutility.itunes.inputparameters;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;
import com.netflix.imfutility.itunes.ITunesMode;
import com.netflix.imfutility.itunes.videoformat.ITunesVideoFormat;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * Tests that command line arguments specified for iTunes format are parsed correctly.
 */
public class ITunesCmdLineArgumentsTest {

    @Test
    public void testParseCorrectCmdLineArgumentsShortName() {
        String[] args = new String[]{
                "-m", "audiomap",
                "-f", "hd1080p30",
                "-o", "output"
        };
        ITunesCmdLineArgs cmdLineArgs = CliFactory.parseArguments(ITunesCmdLineArgs.class, args);

        assertEquals(ITunesMode.audiomap, cmdLineArgs.getMode());
        assertEquals(ITunesVideoFormat.hd1080p30, cmdLineArgs.getVideoFormat());
        assertEquals("output", cmdLineArgs.getOutput());
    }

    @Test
    public void testParseCorrectCmdLineArgumentsLongName() {
        String[] args = new String[]{
                "--mode", "convert",
                "--format", "hd1080p30",
                "--metadata", "metadata.xml",
                "--audiomap", "audiomap.xml",
                "--trailer", "trailer.mov",
                "--poster", "poster.jpg",
                "--chapters", "chapters.xml",
                "--cc", "c0.ttml", "c1.ttml", "c2.ttml", "c3.ttml",
                "--fallback-locale", "en-US",
                "--output", "output"
        };
        ITunesCmdLineArgs cmdLineArgs = CliFactory.parseArguments(ITunesCmdLineArgs.class, args);

        assertEquals(ITunesMode.convert, cmdLineArgs.getMode());
        assertEquals(ITunesVideoFormat.hd1080p30, cmdLineArgs.getVideoFormat());
        assertEquals("metadata.xml", cmdLineArgs.getMetadata());
        assertEquals("audiomap.xml", cmdLineArgs.getAudioMap());
        assertEquals("trailer.mov", cmdLineArgs.getTrailer());
        assertEquals("poster.jpg", cmdLineArgs.getPoster());
        assertEquals("chapters.xml", cmdLineArgs.getChapters());
        assertEquals("en-US", cmdLineArgs.getFallbackLocale());
        assertEquals("output", cmdLineArgs.getOutput());

        assertNotNull(cmdLineArgs.getCc());
        assertEquals(4, cmdLineArgs.getCc().size());
        assertEquals("c0.ttml", cmdLineArgs.getCc().get(0));
        assertEquals("c1.ttml", cmdLineArgs.getCc().get(1));
        assertEquals("c2.ttml", cmdLineArgs.getCc().get(2));
        assertEquals("c3.ttml", cmdLineArgs.getCc().get(3));
    }

    @Test(expected = ArgumentValidationException.class)
    public void testExceptionOnUnknownCmdLineArgument() {
        String[] args = new String[]{"--xxx", "xxx"};
        CliFactory.parseArguments(ITunesCmdLineArgs.class, args);
    }

    @Test
    public void testExpectedDefaults() {
        String[] args = new String[]{};
        ITunesCmdLineArgs cmdLineArgs = CliFactory.parseArguments(ITunesCmdLineArgs.class, args);

        assertEquals(ITunesMode.convert, cmdLineArgs.getMode());
    }
}
