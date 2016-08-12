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

/**
 * Tests main source asset processing.
 * (see {@link SourceAssetProcessor}).
 */
public class SourceAssetProcessorTest {

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
    public void testCorrectSource() throws Exception {
        MetadataXmlProvider metadataXmlProvider = AssetUtils.createMetadataXmlProvider();
        SourceAssetProcessor processor = new SourceAssetProcessor(metadataXmlProvider, TemplateParameterContextCreator.getWorkingDir());

        processor.setLocale(AssetUtils.createLocale("en-US"))
                .process(TestUtils.getTestFile());

        AssetType sourceAsset = metadataXmlProvider.getPackageType().getVideo().getAssets().getAsset().get(0);
        assertEquals(AssetTypeType.FULL, sourceAsset.getType());

        DataFileType sourceDataFile = sourceAsset.getDataFile().get(0);
        assertEquals("test-file", sourceDataFile.getFileName());
        assertEquals(DataFileRoleType.SOURCE, sourceDataFile.getRole());
        assertEquals("en-US", sourceDataFile.getLocale().getName());
    }

    @Test(expected = AssetValidationException.class)
    public void testInvalidPath() throws Exception {
        SourceAssetProcessor processor = new SourceAssetProcessor(AssetUtils.createMetadataXmlProvider(),
                TemplateParameterContextCreator.getWorkingDir());

        processor.setLocale(AssetUtils.createLocale("en-US"))
                .process(new File("invalid_path"));
    }

    @Test(expected = AssetValidationException.class)
    public void testParametersNotSet() throws Exception {
        SourceAssetProcessor processor = new SourceAssetProcessor(AssetUtils.createMetadataXmlProvider(),
                TemplateParameterContextCreator.getWorkingDir());

        //  locale is required
        processor.process(TestUtils.getTestFile());
    }
}
