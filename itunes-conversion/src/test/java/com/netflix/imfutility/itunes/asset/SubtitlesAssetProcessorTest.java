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

import com.netflix.imfutility.generated.itunes.metadata.AssetType;
import com.netflix.imfutility.generated.itunes.metadata.AssetTypeType;
import com.netflix.imfutility.generated.itunes.metadata.DataFileRoleType;
import com.netflix.imfutility.generated.itunes.metadata.DataFileType;
import com.netflix.imfutility.itunes.util.AssetUtils;
import com.netflix.imfutility.itunes.util.TestUtils;
import com.netflix.imfutility.itunes.xmlprovider.MetadataXmlProvider;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * Tests subtitles asset processing.
 * (see {@link SubtitlesAssetProcessor}).
 */
public class SubtitlesAssetProcessorTest {
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

    @Test
    public void testCorrectSubtitles() throws Exception {
        File destDir = new File(TemplateParameterContextCreator.getWorkingDir(), "destDir");
        destDir.mkdir();

        File inputAsset = new File(TemplateParameterContextCreator.getWorkingDir(), "subtitles");
        inputAsset.createNewFile();

        MetadataXmlProvider metadataXmlProvider = AssetUtils.createMetadataXmlProvider();
        SubtitlesAssetProcessor processor = new SubtitlesAssetProcessor(metadataXmlProvider, destDir);

        processor.setLocale(AssetUtils.createLocale("fr-CA"))
                .process(inputAsset);

        // input asset must be moved to dest dir
        assertFalse(inputAsset.exists());

        File outputAsset = new File(destDir, "Subtitles_FR_CA.itt");
        assertTrue(outputAsset.exists());
        assertTrue(outputAsset.isFile());

        AssetType subtitlesAsset = metadataXmlProvider.getPackageType().getVideo().getAssets().getAsset().get(0);
        assertEquals(AssetTypeType.FULL, subtitlesAsset.getType());

        DataFileType subtitlesDataFile = subtitlesAsset.getDataFile().get(0);
        assertEquals("Subtitles_FR_CA.itt", subtitlesDataFile.getFileName());
        assertEquals(DataFileRoleType.SUBTITLES, subtitlesDataFile.getRole());
        assertEquals("fr-CA", subtitlesDataFile.getLocale().getName());
    }

    @Test(expected = AssetValidationException.class)
    public void testInvalidPath() throws Exception {
        SubtitlesAssetProcessor processor = new SubtitlesAssetProcessor(AssetUtils.createMetadataXmlProvider(),
                TemplateParameterContextCreator.getWorkingDir());

        processor.setLocale(AssetUtils.createLocale("en-US"))
                .process(new File("invalid_path"));
    }

    @Test(expected = AssetValidationException.class)
    public void testParametersNotSet() throws Exception {
        File inputAsset = new File(TemplateParameterContextCreator.getWorkingDir(), "subtitles");
        inputAsset.createNewFile();

        SubtitlesAssetProcessor processor = new SubtitlesAssetProcessor(AssetUtils.createMetadataXmlProvider(),
                TemplateParameterContextCreator.getWorkingDir());

        //  locale is required
        processor.process(TestUtils.getTestFile());
    }
}
