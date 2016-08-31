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

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.itunes.asset.type.Asset;
import com.netflix.imfutility.itunes.asset.type.AssetRole;
import com.netflix.imfutility.itunes.asset.type.AssetType;
import com.netflix.imfutility.itunes.util.FakeMetadataXmlProvider;
import com.netflix.imfutility.itunes.util.TestUtils;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests subtitles asset processing.
 * (see {@link SubtitlesAssetProcessor}).
 */
public class SubtitlesAssetProcessorTest {

    private FakeMetadataXmlProvider metadataXmlProvider;
    private File destDir;
    private File inputAsset;

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

    @Before
    public void setup() throws Exception {
        destDir = TestUtils.createDirectory(TemplateParameterContextCreator.getWorkingDir(), "destDir");
        inputAsset = TestUtils.createFile(TemplateParameterContextCreator.getWorkingDir(), "subtitles");

        metadataXmlProvider = new FakeMetadataXmlProvider();
    }

    @After
    public void teardown() throws IOException {
        FileUtils.deleteDirectory(destDir);
        if (inputAsset.exists()) {
            FileUtils.forceDelete(inputAsset);
        }
    }

    @Test
    public void testCorrectSubtitles() throws Exception {
        SubtitlesAssetProcessor processor = new SubtitlesAssetProcessor(metadataXmlProvider, destDir);

        processor.setLocale(Locale.CANADA_FRENCH)
                .process(inputAsset);

        // input asset must be moved to dest dir
        assertFalse(inputAsset.exists());

        File outputAsset = new File(destDir, "subtitles_FR_CA.itt");
        assertTrue(outputAsset.exists());
        assertTrue(outputAsset.isFile());

        Asset subtitlesAsset = metadataXmlProvider.getRootElement().getAssets().get(0);
        assertEquals(AssetType.FULL, subtitlesAsset.getType());
        assertEquals(AssetRole.SUBTITLES, subtitlesAsset.getRole());
        assertEquals(Locale.CANADA_FRENCH, subtitlesAsset.getLocale());
        assertEquals("subtitles_FR_CA.itt", subtitlesAsset.getFileName());
    }

    @Test(expected = ConversionException.class)
    public void testInvalidPath() throws Exception {
        SubtitlesAssetProcessor processor = new SubtitlesAssetProcessor(metadataXmlProvider, destDir);

        processor.setLocale(Locale.US)
                .process(new File("invalid_path"));
    }

    @Test(expected = AssetValidationException.class)
    public void testParametersNotSet() throws Exception {
        SubtitlesAssetProcessor processor = new SubtitlesAssetProcessor(metadataXmlProvider, destDir);

        //  locale is required
        processor.process(inputAsset);
    }

    @Test(expected = AssetValidationException.class)
    public void testDuplicateLocales() throws Exception {
        File anotherInputAsset = TestUtils.createFile(TemplateParameterContextCreator.getWorkingDir(), "anotherSubtitles");

        SubtitlesAssetProcessor processor = new SubtitlesAssetProcessor(metadataXmlProvider, destDir);

        //  locale is duplicated
        processor.setLocale(Locale.US)
                .process(inputAsset);
        processor.setLocale(Locale.US)
                .process(anotherInputAsset);
    }
}
