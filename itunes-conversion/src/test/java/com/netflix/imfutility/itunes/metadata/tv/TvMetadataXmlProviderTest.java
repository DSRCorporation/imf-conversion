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
package com.netflix.imfutility.itunes.metadata.tv;

import com.apple.itunes.importer.tv.AssetFile;
import com.apple.itunes.importer.tv.AssetItem;
import com.apple.itunes.importer.tv.Checksum;
import com.apple.itunes.importer.tv.DataFileRole;
import com.netflix.imfutility.itunes.asset.type.Asset;
import com.netflix.imfutility.itunes.asset.type.AssetRole;
import com.netflix.imfutility.itunes.asset.type.AssetType;
import com.netflix.imfutility.itunes.asset.type.ChapterAsset;
import com.netflix.imfutility.itunes.chapters.builder.ChaptersXmlSampleBuilder;
import com.netflix.imfutility.itunes.util.MetadataUtils;
import com.netflix.imfutility.itunes.util.TestUtils;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import com.netflix.imfutility.xml.XmlParsingException;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;


/**
 * Tests parsing and processing metadata.xml.
 * (see {@link TvMetadataXmlProvider ).
 */
public class TvMetadataXmlProviderTest {

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
    public void testAppendSourceAsset() throws Exception {
        TvMetadataXmlProvider metadataXmlProvider = new TvMetadataXmlProvider();

        Asset asset = new Asset();
        asset.setFileName("file_name");
        asset.setSize("1024");
        asset.setChecksum("0123456789ABCDEF");
        asset.setChecksumType("md5");
        asset.setRole(AssetRole.SOURCE);
        asset.setType(AssetType.FULL);
        asset.setLocale(Locale.US);

        metadataXmlProvider.appendAsset(asset);

        AssetItem assetItem = (AssetItem) metadataXmlProvider.getRootElement()
                .getVideo().get(0)
                .getAssets().get(0)
                .getAssetOrAccessibilityInfo().get(0);
        assertEquals("full", assetItem.getType());

        AssetFile assetFile = (AssetFile) assetItem.getReadOnlyInfoOrTerritoriesOrDataFile().get(0);
        assertEquals(DataFileRole.SOURCE, assetFile.getRole());
        assertEquals("en-US", assetFile.getLocale().get(0).getName());
        assertEquals("file_name", assetFile.getFileName().get(0));
        assertEquals("1024", assetFile.getSize().get(0));

        Checksum checksum = assetFile.getChecksum().get(0);
        assertEquals("0123456789ABCDEF", checksum.getValue());
        assertEquals("md5", checksum.getType());

        metadataXmlProvider.saveMetadata(TemplateParameterContextCreator.getWorkingDir());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAppendChapterAsset() throws Exception {
        TvMetadataXmlProvider metadataXmlProvider = new TvMetadataXmlProvider();

        ChapterAsset asset = new ChapterAsset();
        asset.setFileName("chapter01");
        asset.setSize("1024");
        asset.setChecksum("0123456789ABCDEF");
        asset.setChecksumType("md5");
        asset.setInputChapterItem(ChaptersXmlSampleBuilder.buildInputChapter());

        metadataXmlProvider.appendChapterAsset(asset);
    }

    @Test
    public void testGenerateDefaultMetadata() throws Exception {
        TvMetadataXmlProvider provider = new TvMetadataXmlProvider();

        assertNotNull(provider.getRootElement());
        assertNotNull(provider.getRootElement().getProvider().get(0));
        assertNotNull(provider.getRootElement().getLanguage().get(0));
        assertNotNull(provider.getRootElement().getVideo().get(0));

        assertEquals("tv5.2", provider.getRootElement().getVersion());
        assertEquals("tv", provider.getRootElement().getVideo().get(0).getType().get(0));
        assertEquals("vendor_id", provider.getRootElement().getVideo().get(0).getVendorId().get(0));
    }

    @Test
    public void testParseCorrectMetadata() throws Exception {
        TvMetadataXmlProvider provider = new TvMetadataXmlProvider(MetadataUtils.getCorrectTvMetadataXml());

        assertNotNull(provider.getRootElement());
        assertNotNull(provider.getRootElement().getProvider().get(0));
        assertNotNull(provider.getRootElement().getLanguage().get(0));
        assertNotNull(provider.getRootElement().getVideo().get(0));
    }

    @Test(expected = XmlParsingException.class)
    public void testParseBrokenMetadata() throws Exception {
        new TvMetadataXmlProvider(MetadataUtils.getBrokenTvMetadataXml());
    }

    @Test(expected = XmlParsingException.class)
    public void testParseInvalidMetadata() throws Exception {
        new TvMetadataXmlProvider(MetadataUtils.getInvalidTvMetadataXml());
    }

    @Test(expected = FileNotFoundException.class)
    public void testParseInvalidFilePath() throws Exception {
        new TvMetadataXmlProvider(new File("invalid-path"));
    }

    @Test
    public void testSaveCorrectMetadata() throws Exception {
        TvMetadataXmlProvider provider = new TvMetadataXmlProvider(MetadataUtils.getCorrectTvMetadataXml());

        File itmspDir = TestUtils.createDirectory(TemplateParameterContextCreator.getWorkingDir(), "correct.itmsp");
        File savedMetadata = provider.saveMetadata(itmspDir);

        assertEquals(new File(itmspDir, "metadata.xml"), savedMetadata);
    }

    @Test
    public void testUpdateLocale() throws Exception {
        TvMetadataXmlProvider provider = new TvMetadataXmlProvider();

        provider.setLocale(Locale.CANADA_FRENCH);

        assertEquals("fr-CA", provider.getRootElement().getLanguage().get(0));
        assertEquals("fr-CA", provider.getRootElement().getVideo().get(0).getOriginalSpokenLocale().get(0));
    }

}
