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
package com.netflix.imfutility.itunes.metadata;

import com.netflix.imfutility.itunes.util.MetadataUtils;
import com.netflix.imfutility.itunes.xmlprovider.MetadataXmlProvider;
import com.netflix.imfutility.itunes.xmlprovider.builder.MetadataXmlSampleBuilder;
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
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;

/**
 * Tests parsing and processing metadata.xml.
 * (see {@link MetadataXmlProvider).
 */
public class MetadataXmlProviderTest {

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

    private MetadataXmlProvider createMetadataXmlProvider(File file) throws XmlParsingException, FileNotFoundException {
        return new MetadataXmlProvider(TemplateParameterContextCreator.getWorkingDir(), file);
    }

    @Test
    public void testParseCorrectMetadata() throws Exception {
        MetadataXmlProvider provider = createMetadataXmlProvider(MetadataUtils.getCorrectMetadataXml());

        assertNotNull(provider.getPackageType());
        assertNotNull(provider.getPackageType().getProvider());
        assertNotNull(provider.getPackageType().getLanguage());
        assertNotNull(provider.getPackageType().getVideo());

        assertNull(provider.getPackageType().getVideo().getLocales());

        assertEquals("09736156444", provider.getPackageType().getVideo().getVendorId());
    }


    @Test
    public void testParseCorrectMultipleLocaleMetadata() throws Exception {
        MetadataXmlProvider provider = createMetadataXmlProvider(MetadataUtils.getCorrectMultipleLocaleMetadataXml());

        assertNotNull(provider.getPackageType());
        assertNotNull(provider.getPackageType().getProvider());
        assertNotNull(provider.getPackageType().getLanguage());
        assertNotNull(provider.getPackageType().getVideo());

        assertNotNull(provider.getPackageType().getVideo().getLocales());
    }


    @Test
    public void testParseCorrectConcertMetadata() throws Exception {
        MetadataXmlProvider provider = createMetadataXmlProvider(MetadataUtils.getCorrectConcertMetadataXml());

        assertNotNull(provider.getPackageType());
        assertNotNull(provider.getPackageType().getProvider());
        assertNotNull(provider.getPackageType().getLanguage());
        assertNotNull(provider.getPackageType().getVideo());

        assertNotNull(provider.getPackageType().getVideo().getArtists());
        assertNull(provider.getPackageType().getVideo().getCast());
    }

    @Test
    public void testParseCorrectIntervalsMetadata() throws Exception {
        MetadataXmlProvider provider = createMetadataXmlProvider(MetadataUtils.getCorrectIntervalsMetadataXml());

        assertNotNull(provider.getPackageType());
        assertNotNull(provider.getPackageType().getProvider());
        assertNotNull(provider.getPackageType().getLanguage());
        assertNotNull(provider.getPackageType().getVideo());

        assertNotNull(provider.getPackageType().getVideo().getProducts().getProduct().get(0).getIntervals());
    }

    @Test(expected = XmlParsingException.class)
    public void testParseBrokenMetadata() throws Exception {
        createMetadataXmlProvider(MetadataUtils.getBrokenMetadataXml());
    }

    @Test(expected = XmlParsingException.class)
    public void testParseInvalidMetadata() throws Exception {
        createMetadataXmlProvider(MetadataUtils.getInvalidMetadataXml());
    }

    @Test(expected = FileNotFoundException.class)
    public void testParseInvalidFilePath() throws Exception {
        createMetadataXmlProvider(new File("invalid-path"));
    }

    @Test
    public void testGenerateSampleMetadata() {
        File metadataFile = new File(TemplateParameterContextCreator.getWorkingDir(), "sample_metadata.xml");
        MetadataXmlProvider.generateSampleXml(metadataFile);

        assertEquals(new File(TemplateParameterContextCreator.getWorkingDir(), "sample_metadata.xml"), metadataFile);
    }

    @Test
    public void testSaveCorrectMetadata() throws Exception {
        MetadataXmlProvider provider = createMetadataXmlProvider(MetadataUtils.getCorrectMetadataXml());

        new File(TemplateParameterContextCreator.getWorkingDir(), "vendor_id").mkdir();

        assertEquals(new File(TemplateParameterContextCreator.getWorkingDir(), "vendor_id/metadata.xml"), provider.saveMetadata("vendor_id"));
    }

    @Test(expected = RuntimeException.class)
    public void testSaveIncorrectMetadata() throws Exception {
        MetadataXmlProvider provider = new MetadataXmlProvider(TemplateParameterContextCreator.getWorkingDir(),
                MetadataXmlSampleBuilder.buildPackage());

        new File(TemplateParameterContextCreator.getWorkingDir(), "vendor_id").mkdir();

        // sample package will fail strict validation
        provider.saveMetadata("vendor_id");
    }
}
