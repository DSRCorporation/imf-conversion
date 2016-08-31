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
import com.netflix.imfutility.itunes.asset.type.AssetRole;
import com.netflix.imfutility.itunes.asset.type.AssetType;
import com.netflix.imfutility.itunes.asset.type.VideoAsset;
import com.netflix.imfutility.itunes.util.AssetUtils;
import com.netflix.imfutility.itunes.util.FakeMetadataXmlProvider;
import com.netflix.imfutility.itunes.util.TestUtils;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests trailer asset processing.
 * (see {@link TrailerAssetProcessor}).
 */
public class TrailerAssetProcessorTest {

    private FakeMetadataXmlProvider metadataXmlProvider;

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
        metadataXmlProvider = new FakeMetadataXmlProvider();
    }

    @Test
    public void testCorrectTrailer() throws Exception {
        TrailerAssetProcessor processor = new TrailerAssetProcessor(metadataXmlProvider, TemplateParameterContextCreator.getWorkingDir());

        processor.setVendorId("vendor_id")
                .setLocale(Locale.US)
                .setFormat(AssetUtils.createCorrectVideoFormat())
                .process(TestUtils.getTestFile());

        File asset = new File(TemplateParameterContextCreator.getWorkingDir(), "vendor_id-preview.mov");
        assertTrue(asset.exists());
        assertTrue(asset.isFile());

        VideoAsset trailerAsset = (VideoAsset) metadataXmlProvider.getRootElement().getAssets().get(0);
        assertEquals(AssetType.PREVIEW, trailerAsset.getType());
        assertEquals(AssetRole.SOURCE, trailerAsset.getRole());
        assertEquals(Locale.US, trailerAsset.getLocale());
        assertEquals("vendor_id-preview.mov", trailerAsset.getFileName());
    }

    @Test(expected = AssetValidationException.class)
    public void testInvalidFormat() throws Exception {
        TrailerAssetProcessor processor = new TrailerAssetProcessor(metadataXmlProvider,
                TemplateParameterContextCreator.getWorkingDir());

        processor.setVendorId("vendor_id")
                .setLocale(Locale.US)
                .setFormat(AssetUtils.createIncorrectVideoFormat())
                .process(TestUtils.getTestFile());
    }

    @Test(expected = ConversionException.class)
    public void testInvalidPath() throws Exception {
        TrailerAssetProcessor processor = new TrailerAssetProcessor(metadataXmlProvider,
                TemplateParameterContextCreator.getWorkingDir());

        processor.setVendorId("vendor_id")
                .setLocale(Locale.US)
                .setFormat(AssetUtils.createCorrectVideoFormat())
                .process(new File("invalid_path"));
    }

    @Test(expected = AssetValidationException.class)
    public void testParametersNotSet() throws Exception {
        TrailerAssetProcessor processor = new TrailerAssetProcessor(metadataXmlProvider,
                TemplateParameterContextCreator.getWorkingDir());

        //  vendor_id, locale and format are required
        processor.process(TestUtils.getTestFile());
    }
}
