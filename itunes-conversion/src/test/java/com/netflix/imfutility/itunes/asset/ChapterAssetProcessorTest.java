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
package com.netflix.imfutility.itunes.asset;

import com.netflix.imfutility.generated.itunes.metadata.ChapterType;
import com.netflix.imfutility.itunes.image.ImageValidationException;
import com.netflix.imfutility.itunes.util.AssetUtils;
import com.netflix.imfutility.itunes.util.TestUtils;
import com.netflix.imfutility.itunes.xmlprovider.MetadataXmlProvider;
import com.netflix.imfutility.itunes.xmlprovider.builder.ChaptersXmlSampleBuilder;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.fraction.BigFraction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Tests chapter asset processing.
 * (see {@link ChapterAssetProcessor}).
 */
public class ChapterAssetProcessorTest {

    @BeforeClass
    public static void setupAll() throws IOException {
        // create both working directory and logs folder.
        FileUtils.deleteDirectory(TemplateParameterContextCreator.getWorkingDir());
        File workingDir = TemplateParameterContextCreator.getWorkingDir();
        if (!workingDir.mkdir()) {
            throw new RuntimeException("Could not create a working dir within tmp folder");
        }
    }

    @AfterClass
    public static void teardownAll() throws IOException {
        FileUtils.deleteDirectory(TemplateParameterContextCreator.getWorkingDir());
    }

    private static MetadataXmlProvider createMetadataXmlProvider() {
        return new MetadataXmlProvider(TemplateParameterContextCreator.getWorkingDir(),
                MetadataXmlProvider.generateSampleMetadata());
    }

    @Test
    public void testCorrectChapter() throws Exception {
        MetadataXmlProvider metadataXmlProvider = createMetadataXmlProvider();
        ChapterAssetProcessor processor = new ChapterAssetProcessor(metadataXmlProvider, TemplateParameterContextCreator.getWorkingDir());

        processor.setInputChapter(ChaptersXmlSampleBuilder.buildInputChapter())
                .setAspectRatio(new BigFraction(16).divide(9))
                .setChapterIndex(1)
                .process(AssetUtils.getTestCorrectChapterFile());

        File asset = new File(TemplateParameterContextCreator.getWorkingDir(), "chapter01.jpg");
        assertTrue(asset.exists());
        assertTrue(asset.isFile());

        ChapterType chapter = metadataXmlProvider.getPackageType().getVideo().getChapters().getChapter().get(0);
        assertEquals("Required title", chapter.getTitle().getValue());
        assertEquals("00:00:00", chapter.getStartTime());
        assertEquals("chapter01.jpg", chapter.getArtworkFile().getFileName());
    }

    @Test(expected = ImageValidationException.class)
    public void testInvalidChapter() throws Exception {
        ChapterAssetProcessor processor = new ChapterAssetProcessor(createMetadataXmlProvider(),
                TemplateParameterContextCreator.getWorkingDir());

        processor.setInputChapter(ChaptersXmlSampleBuilder.buildInputChapter())
                //  aspect ratio of image 16:9
                .setAspectRatio(new BigFraction(4).divide(3))
                .setChapterIndex(1)
                .process(AssetUtils.getTestCorrectChapterFile());
    }

    @Test(expected = AssetValidationException.class)
    public void testInvalidFile() throws Exception {
        ChapterAssetProcessor processor = new ChapterAssetProcessor(createMetadataXmlProvider(),
                TemplateParameterContextCreator.getWorkingDir());

        processor.setInputChapter(ChaptersXmlSampleBuilder.buildInputChapter())
                .setAspectRatio(new BigFraction(16).divide(9))
                .setChapterIndex(1)
                .process(TestUtils.getTestFile());
    }

    @Test(expected = AssetValidationException.class)
    public void testInvalidPath() throws Exception {
        ChapterAssetProcessor processor = new ChapterAssetProcessor(createMetadataXmlProvider(),
                TemplateParameterContextCreator.getWorkingDir());

        processor.setInputChapter(ChaptersXmlSampleBuilder.buildInputChapter())
                .setAspectRatio(new BigFraction(16).divide(9))
                .setChapterIndex(1)
                .process(new File("invalid_path"));
    }

    @Test(expected = AssetValidationException.class)
    public void testParametersNotSet() throws Exception {
        ChapterAssetProcessor processor = new ChapterAssetProcessor(createMetadataXmlProvider(),
                TemplateParameterContextCreator.getWorkingDir());

        processor.process(AssetUtils.getTestCorrectChapterFile());
    }

    @Test(expected = AssetValidationException.class)
    public void testChapterIndexOutOfBound() throws Exception {
        ChapterAssetProcessor processor = new ChapterAssetProcessor(createMetadataXmlProvider(),
                TemplateParameterContextCreator.getWorkingDir());

        processor.setInputChapter(ChaptersXmlSampleBuilder.buildInputChapter())
                .setAspectRatio(new BigFraction(16).divide(9))
                //  chapter index must be from 1 to 99
                .setChapterIndex(100)
                .process(AssetUtils.getTestCorrectChapterFile());
    }
}
