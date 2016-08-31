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
package com.netflix.imfutility.itunes.metadata.film;

import com.netflix.imfutility.generated.itunes.metadata.film.AssetFile;
import com.netflix.imfutility.generated.itunes.metadata.film.AssetItem;
import com.netflix.imfutility.generated.itunes.metadata.film.ChapterItem;
import com.netflix.imfutility.generated.itunes.metadata.film.Checksum;
import com.netflix.imfutility.generated.itunes.metadata.film.DataFileRole;
import com.netflix.imfutility.generated.itunes.metadata.film.PackageType;
import com.netflix.imfutility.itunes.asset.type.Asset;
import com.netflix.imfutility.itunes.asset.type.AssetRole;
import com.netflix.imfutility.itunes.asset.type.AssetType;
import com.netflix.imfutility.itunes.asset.type.ChapterAsset;
import com.netflix.imfutility.itunes.chapters.builder.ChaptersXmlSampleBuilder;
import com.netflix.imfutility.itunes.metadata.film.wrap.ChapterItemWrapper;
import com.netflix.imfutility.itunes.metadata.film.wrap.FileWrapper;
import com.netflix.imfutility.itunes.util.MetadataUtils;
import com.netflix.imfutility.itunes.util.TestUtils;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import com.netflix.imfutility.xml.XmlParsingException;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;


/**
 * Tests parsing and processing metadata.xml.
 * (see {@link FilmMetadataXmlProvider ).
 */
public class FilmMetadataXmlProviderTest {

    private static JAXBContext context;

    @BeforeClass
    public static void setupAll() throws IOException {
        // create both working directory and logs folder.
        FileUtils.deleteDirectory(TemplateParameterContextCreator.getWorkingDir());
        File workingDir = TemplateParameterContextCreator.getWorkingDir();
        if (!workingDir.mkdir()) {
            throw new RuntimeException("Could not create a working dir within tmp folder");
        }

        try {
            context = JAXBContext.newInstance(PackageType.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public static void teardownAll() throws IOException {
        FileUtils.deleteDirectory(TemplateParameterContextCreator.getWorkingDir());
    }

    @Test
    public void testAppendSourceAsset() throws Exception {
        FilmMetadataXmlProvider metadataXmlProvider = new FilmMetadataXmlProvider();

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

    @Test
    public void testAppendChapterAsset() throws Exception {
        FilmMetadataXmlProvider metadataXmlProvider = new FilmMetadataXmlProvider();

        ChapterAsset asset = new ChapterAsset();
        asset.setFileName("chapter01");
        asset.setSize("1024");
        asset.setChecksum("0123456789ABCDEF");
        asset.setChecksumType("md5");
        asset.setInputChapterItem(ChaptersXmlSampleBuilder.buildInputChapter());

        metadataXmlProvider.appendChapterAsset(asset);

        ChapterItem chapter = (ChapterItem) metadataXmlProvider.getRootElement().getVideo().get(0)
                .getChapters().get(0)
                .getTimecodeFormatOrChapter().get(0);
        ChapterItemWrapper wrapper = new ChapterItemWrapper(context, chapter);
        FileWrapper fileWrapper = new FileWrapper(context, wrapper.getArtworkFile());

        Assert.assertEquals("Required chapter title", wrapper.getTitle().getValue());
        Assert.assertEquals("en-US", wrapper.getTitle().getLocale());
        Assert.assertEquals("00:00:00", wrapper.getStartTime());
        Assert.assertEquals("chapter01", fileWrapper.getFileName());

        metadataXmlProvider.saveMetadata(TemplateParameterContextCreator.getWorkingDir());
    }

    @Test
    public void testGenerateDefaultMetadata() throws Exception {
        FilmMetadataXmlProvider provider = new FilmMetadataXmlProvider();

        assertNotNull(provider.getRootElement());
        assertNotNull(provider.getRootElement().getProvider().get(0));
        assertNotNull(provider.getRootElement().getLanguage().get(0));
        assertNotNull(provider.getRootElement().getVideo().get(0));

        assertEquals("vendor_id", provider.getRootElement().getVideo().get(0).getVendorId().get(0));
    }

    @Test
    public void testParseCorrectMetadata() throws Exception {
        FilmMetadataXmlProvider provider = new FilmMetadataXmlProvider(MetadataUtils.getCorrectMetadataXml());

        assertNotNull(provider.getRootElement());
        assertNotNull(provider.getRootElement().getProvider().get(0));
        assertNotNull(provider.getRootElement().getLanguage().get(0));
        assertNotNull(provider.getRootElement().getVideo().get(0));

        assertTrue(provider.getRootElement().getVideo().get(0).getLocales().isEmpty());
    }

    @Test
    public void testParseCorrectMultipleLocaleMetadata() throws Exception {
        FilmMetadataXmlProvider provider = new FilmMetadataXmlProvider(MetadataUtils.getCorrectMultipleLocaleMetadataXml());

        assertNotNull(provider.getRootElement());
        assertNotNull(provider.getRootElement().getProvider().get(0));
        assertNotNull(provider.getRootElement().getLanguage().get(0));
        assertNotNull(provider.getRootElement().getVideo().get(0));

        assertFalse(provider.getRootElement().getVideo().get(0).getLocales().isEmpty());
    }


    @Test
    public void testParseCorrectConcertMetadata() throws Exception {
        FilmMetadataXmlProvider provider = new FilmMetadataXmlProvider(MetadataUtils.getCorrectConcertMetadataXml());

        assertNotNull(provider.getRootElement());
        assertNotNull(provider.getRootElement().getProvider().get(0));
        assertNotNull(provider.getRootElement().getLanguage().get(0));
        assertNotNull(provider.getRootElement().getVideo().get(0));

        assertFalse(provider.getRootElement().getVideo().get(0).getArtists().isEmpty());
        assertTrue(provider.getRootElement().getVideo().get(0).getCast().isEmpty());
    }

    @Test
    public void testParseCorrectIntervalsMetadata() throws Exception {
        FilmMetadataXmlProvider provider = new FilmMetadataXmlProvider(MetadataUtils.getCorrectIntervalsMetadataXml());

        assertNotNull(provider.getRootElement());
        assertNotNull(provider.getRootElement().getProvider());
        assertNotNull(provider.getRootElement().getLanguage());
        assertNotNull(provider.getRootElement().getVideo());
    }

    @Test(expected = XmlParsingException.class)
    public void testParseBrokenMetadata() throws Exception {
        new FilmMetadataXmlProvider(MetadataUtils.getBrokenMetadataXml());
    }

    @Test(expected = XmlParsingException.class)
    public void testParseInvalidMetadata() throws Exception {
        new FilmMetadataXmlProvider(MetadataUtils.getInvalidMetadataXml());
    }

    @Test(expected = FileNotFoundException.class)
    public void testParseInvalidFilePath() throws Exception {
        new FilmMetadataXmlProvider(new File("invalid-path"));
    }

    @Test
    public void testSaveCorrectMetadata() throws Exception {
        FilmMetadataXmlProvider provider = new FilmMetadataXmlProvider(MetadataUtils.getCorrectMetadataXml());

        File itmspDir = TestUtils.createDirectory(TemplateParameterContextCreator.getWorkingDir(), "correct.itmsp");
        File savedMetadata = provider.saveMetadata(itmspDir);

        assertEquals(new File(itmspDir, "metadata.xml"), savedMetadata);
    }

    @Test
    public void testUpdateLocale() throws Exception {
        FilmMetadataXmlProvider provider = new FilmMetadataXmlProvider();

        provider.setLocale(Locale.CANADA_FRENCH);

        assertEquals("fr-CA", provider.getRootElement().getLanguage().get(0));
        assertEquals("fr-CA", provider.getRootElement().getVideo().get(0).getOriginalSpokenLocale().get(0));
    }

}
