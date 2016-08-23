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
package com.netflix.imfutility.itunes.xmlprovider;

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.generated.itunes.metadata.ArtWorkFileType;
import com.netflix.imfutility.generated.itunes.metadata.AssetType;
import com.netflix.imfutility.generated.itunes.metadata.AssetTypeType;
import com.netflix.imfutility.generated.itunes.metadata.AssetsType;
import com.netflix.imfutility.generated.itunes.metadata.ChapterInputType;
import com.netflix.imfutility.generated.itunes.metadata.ChapterType;
import com.netflix.imfutility.generated.itunes.metadata.ChaptersType;
import com.netflix.imfutility.generated.itunes.metadata.DataFileRoleType;
import com.netflix.imfutility.generated.itunes.metadata.DataFileType;
import com.netflix.imfutility.generated.itunes.metadata.LocaleType;
import com.netflix.imfutility.generated.itunes.metadata.ObjectFactory;
import com.netflix.imfutility.generated.itunes.metadata.PackageType;
import com.netflix.imfutility.generated.itunes.metadata.TerritoriesType;
import com.netflix.imfutility.itunes.xmlprovider.builder.MetadataXmlSampleBuilder;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.validation.Schema;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import static com.netflix.imfutility.itunes.ITunesConversionConstants.DEFAULT_METADATA_FILENAME;
import static com.netflix.imfutility.itunes.ITunesConversionXsdConstants.METADATA_PACKAGE;
import static com.netflix.imfutility.itunes.ITunesConversionXsdConstants.METADATA_XML_SCHEME;
import static com.netflix.imfutility.itunes.ITunesConversionXsdConstants.METADATA_XML_STRICT_SCHEME;

/**
 * Provides functionality to generate empty metadata.xml for iTunes format.
 */
public class MetadataXmlProvider implements LocalizedXmlProvider {

    private final boolean customized;
    private final PackageType packageType;

    public MetadataXmlProvider(String vendorId, File metadataFile) throws FileNotFoundException, XmlParsingException {
        this.customized = metadataFile != null;
        this.packageType = customized ? loadMetadata(metadataFile) : generateSampleMetadata();

        this.packageType.getVideo().setVendorId(vendorId);
        ensureChaptersCreated();
        ensureFullAssetCreated();
    }

    private PackageType loadMetadata(File metadataFile) throws FileNotFoundException, XmlParsingException {
        if (!metadataFile.isFile()) {
            throw new FileNotFoundException(String.format(
                    "Invalid metadata.xml file: '%s' not found", metadataFile.getAbsolutePath()));
        }
        return XmlParser.parse(metadataFile, new String[]{METADATA_XML_SCHEME}, METADATA_PACKAGE, PackageType.class);
    }

    @Override
    public void setLocale(String locale) {
        packageType.setLanguage(locale);
        packageType.getVideo().setOriginalSpokenLocale(locale);
    }

    @Override
    public String getLocale() {
        return packageType.getLanguage();
    }

    public boolean isCustomized() {
        return customized;
    }

    /**
     * Get root metadata element.
     *
     * @return root package tag
     */
    public PackageType getPackageType() {
        return packageType;
    }

    /**
     * Get default locale based on language set in language tag.
     *
     * @return default locale
     */
    public LocaleType getDefaultLocale() {
        return getLocale(packageType.getLanguage());
    }

    //  Chapters processing

    private ChaptersType ensureChaptersCreated() {
        if (packageType.getVideo().getChapters() == null) {
            packageType.getVideo().setChapters(new ChaptersType());
        }
        return packageType.getVideo().getChapters();
    }

    public void appendChaptersTimeCode(String timeCode) {
        ensureChaptersCreated().setTimecodeFormat(timeCode);
    }

    public void appendChapter(ArtWorkFileType artWorkFile, ChapterInputType chapterInput) {
        ChapterType chapter = new ChapterType();
        chapter.setTitle(chapterInput.getTitle());
        if (chapterInput.getTitles() != null) {
            chapter.setTitles(chapterInput.getTitles());
        }
        chapter.setStartTime(chapterInput.getStartTime());
        chapter.setArtworkFile(artWorkFile);

        ensureChaptersCreated().getChapter().add(chapter);
    }

    //   Asset processing

    private AssetsType ensureAssetsCreated() {
        if (packageType.getVideo().getAssets() == null) {
            packageType.getVideo().setAssets(new AssetsType());
        }
        return packageType.getVideo().getAssets();
    }

    private AssetType createAsset(AssetTypeType assetType, TerritoriesType territories) {
        AssetType asset = new AssetType();
        asset.setType(assetType);
        asset.setTerritories(territories);

        ensureAssetsCreated().getAsset().add(asset);
        return asset;
    }

    private AssetType ensureFullAssetCreated() {
        return ensureAssetsCreated().getAsset().stream()
                .filter(asset -> asset.getType() == AssetTypeType.FULL)
                .findFirst()
                .orElseGet(() -> createAsset(AssetTypeType.FULL, null));
    }

    public void appendAssetDataFile(DataFileType dataFile, AssetTypeType assetType) {
        AssetType asset = assetType == AssetTypeType.FULL
                ? ensureFullAssetCreated()
                : createAsset(assetType, getDefaultTerritories());

        asset.getDataFile().add(dataFile);
    }

    public List<DataFileType> getFullAssetDataFilesByRole(DataFileRoleType role) {
        return ensureFullAssetCreated().getDataFile().stream()
                .filter(dataFile -> dataFile.getRole() == role)
                .collect(Collectors.toList());
    }

    private TerritoriesType getDefaultTerritories() {
        //  get WW territory by default
        TerritoriesType territories = new TerritoriesType();
        territories.getTerritory().add("WW");
        return territories;
    }

    /**
     * Save managed metadata to file.
     * Within marshalling metadata will be validated by strict schema.
     * Assets and chapters full info is required.
     *
     * @param dir a directory to save metadata
     * @return metadata.xml file
     */
    public File saveMetadata(File dir) {
        File file = new File(dir, DEFAULT_METADATA_FILENAME);
        marshallMetadata(packageType, METADATA_XML_STRICT_SCHEME, file);
        return file;
    }

    /**
     * Get locale based on provided language.
     *
     * @return locale
     */
    public static LocaleType getLocale(String language) {
        LocaleType locale = new LocaleType();
        locale.setName(language);
        return locale;
    }

    /**
     * Generates a sample metadata.xml file.
     *
     * @param path a path to the output metadata.xml file
     */
    public static void generateSampleXml(String path) {
        generateSampleXml(new File(path));
    }

    /**
     * Generates a sample metadata.xml file.
     *
     * @param file metadata.xml file
     */
    public static void generateSampleXml(File file) {
        marshallMetadata(generateSampleMetadata(), METADATA_XML_SCHEME, file);
    }

    /**
     * Generates a sample metadata structure.
     */
    public static PackageType generateSampleMetadata() {
        try {
            return MetadataXmlSampleBuilder.buildPackage();
        } catch (DatatypeConfigurationException e) {
            throw new ConversionException("Sample metadata.xml cannot be generated.", e);
        }
    }

    /**
     * Create marshaller based on schema for iTunes specific metadata format.
     *
     * @param jaxbContext context
     * @param schemaPath  path to schema
     * @return marshaller that supports iTunes schema validation
     * @throws SAXException
     * @throws JAXBException
     */
    private static Marshaller createMarshaller(JAXBContext jaxbContext, String schemaPath) throws SAXException, JAXBException {
        Schema schema = XmlParser.getSchema(new String[]{schemaPath});

        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setSchema(schema);
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        return jaxbMarshaller;
    }

    /**
     * Marshall iTunes package.
     *
     * @param packageType package to marshall
     * @param schemaPath  path to schema
     */
    private static void marshallMetadata(PackageType packageType, String schemaPath, File file) {
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(PackageType.class);
            Marshaller jaxbMarshaller = createMarshaller(jaxbContext, schemaPath);

            JAXBElement<PackageType> metadataJaxb = new ObjectFactory().createPackage(packageType);
            jaxbMarshaller.marshal(metadataJaxb, file);
        } catch (SAXException | JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}

