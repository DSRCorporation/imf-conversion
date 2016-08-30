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
import com.netflix.imfutility.itunes.asset.bean.Asset;
import com.netflix.imfutility.itunes.asset.bean.AssetType;
import com.netflix.imfutility.itunes.image.ImageValidationException;
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
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

/**
 * Tests poster asset processing.
 * (see {@link PosterAssetProcessor}).
 */
public class PosterAssetProcessorTest {

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
    public void testCorrectPoster() throws Exception {
        PosterAssetProcessor processor = new PosterAssetProcessor(metadataXmlProvider, TemplateParameterContextCreator.getWorkingDir());

        processor.setVendorId("vendor_id")
                .setLocale(Locale.US)
                .process(AssetUtils.getTestCorrectPosterFile());

        File asset = new File(TemplateParameterContextCreator.getWorkingDir(), "vendor_id.jpg");
        assertTrue(asset.exists());
        assertTrue(asset.isFile());

        Asset posterAsset = metadataXmlProvider.getRootElement().getAssets().get(0);
        assertEquals(AssetType.ARTWORK, posterAsset.getType());
        assertNull(posterAsset.getRole());
        assertEquals(Locale.US, posterAsset.getLocale());
        assertEquals("vendor_id.jpg", posterAsset.getFileName());
    }

    @Test(expected = ImageValidationException.class)
    public void testInvalidPoster() throws Exception {
        PosterAssetProcessor processor = new PosterAssetProcessor(metadataXmlProvider,
                TemplateParameterContextCreator.getWorkingDir());

        processor.setVendorId("vendor_id")
                .setLocale(Locale.US)
                .process(AssetUtils.getTestIncorrectPosterFile());
    }

    @Test(expected = AssetValidationException.class)
    public void testInvalidFile() throws Exception {
        PosterAssetProcessor processor = new PosterAssetProcessor(metadataXmlProvider,
                TemplateParameterContextCreator.getWorkingDir());

        processor.setVendorId("vendor_id")
                .setLocale(Locale.US)
                .process(TestUtils.getTestFile());
    }

    @Test(expected = ConversionException.class)
    public void testInvalidPath() throws Exception {
        PosterAssetProcessor processor = new PosterAssetProcessor(metadataXmlProvider,
                TemplateParameterContextCreator.getWorkingDir());

        processor.setVendorId("vendor_id")
                .setLocale(Locale.US)
                .process(new File("invalid_path"));
    }

    @Test(expected = AssetValidationException.class)
    public void testParametersNotSet() throws Exception {
        PosterAssetProcessor processor = new PosterAssetProcessor(metadataXmlProvider,
                TemplateParameterContextCreator.getWorkingDir());

        //  vendor_id required
        processor.setLocale(Locale.US)
                .process(AssetUtils.getTestCorrectPosterFile());
    }
}
