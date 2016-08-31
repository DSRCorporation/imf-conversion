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
package com.netflix.imfutility.itunes.chapters;

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.itunes.util.ChaptersUtils;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import com.netflix.imfutility.xml.XmlParsingException;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;

/**
 * Tests parsing chapters.xml.
 * (see {@link ChaptersXmlProvider).
 */
public class ChaptersXmlProviderTest {

    private static String userDir;

    @BeforeClass
    public static void setupAll() throws IOException {
        // create both working directory and logs folder.
        FileUtils.deleteDirectory(TemplateParameterContextCreator.getWorkingDir());
        File workingDir = TemplateParameterContextCreator.getWorkingDir();
        if (!workingDir.mkdir()) {
            throw new RuntimeException("Could not create a working dir within tmp folder");
        }
        //  set user.dir to working directory
        userDir = System.getProperty("user.dir");
        System.setProperty("user.dir", workingDir.getAbsolutePath());

        ChaptersUtils.createChapterFile("chapter01.jpg");
        ChaptersUtils.createChapterFile("chapter02.jpg");
    }

    @AfterClass
    public static void teardownAll() throws IOException {
        FileUtils.deleteDirectory(TemplateParameterContextCreator.getWorkingDir());

        System.setProperty("user.dir", userDir);
    }

    @Test
    public void testParseCorrectChapters() throws Exception {
        ChaptersXmlProvider provider = new ChaptersXmlProvider(ChaptersUtils.getCorrectChaptersXml());

        assertFalse(provider.getChapters().isEmpty());
        assertEquals("24/999 1000/nonDrop", provider.getTimecodeFormat());

        assertEquals("chapter01.jpg", provider.getChapters().get(0).getFileName());
        assertEquals("chapter02.jpg", provider.getChapters().get(1).getFileName());
    }

    @Test(expected = XmlParsingException.class)
    public void testParseBrokenChapters() throws Exception {
        new ChaptersXmlProvider(ChaptersUtils.getBrokenChaptersXml());
    }

    @Test(expected = XmlParsingException.class)
    public void testParseInvalidChapters() throws Exception {
        new ChaptersXmlProvider(ChaptersUtils.getInvalidChaptersXml());
    }

    @Test(expected = FileNotFoundException.class)
    public void testParseInvalidFilePath() throws Exception {
        new ChaptersXmlProvider(new File("invalid-path"));
    }

    @Test(expected = ConversionException.class)
    public void testChapterFileNotFound() throws Exception {
        new File("chapter02.jpg").delete();

        new ChaptersXmlProvider(ChaptersUtils.getCorrectChaptersXml());
    }

    @Test
    public void testGenerateSampleChapters() throws Exception {
        File chaptersFile = new File(TemplateParameterContextCreator.getWorkingDir(), "sample_chapters.xml");
        ChaptersXmlProvider.generateSampleXml(chaptersFile);

        assertEquals(new File(TemplateParameterContextCreator.getWorkingDir(), "sample_chapters.xml"), chaptersFile);

        new ChaptersXmlProvider(chaptersFile);
    }

}
