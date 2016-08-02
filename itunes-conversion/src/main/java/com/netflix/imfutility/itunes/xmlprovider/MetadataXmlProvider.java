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
import com.netflix.imfutility.generated.itunes.metadata.DataFileType;
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

import static com.netflix.imfutility.itunes.ITunesConversionXsdConstants.METADATA_PACKAGE;
import static com.netflix.imfutility.itunes.ITunesConversionXsdConstants.METADATA_XML_SCHEME;
import static com.netflix.imfutility.itunes.ITunesConversionXsdConstants.METADATA_XML_STRICT_SCHEME;

/**
 * Provides functionality to generate empty metadata.xml for iTunes format.
 */
public class MetadataXmlProvider {

    private final PackageType packageType;
    private final File workingDir;

    public MetadataXmlProvider(File workingDir, File metadataFile) throws FileNotFoundException, XmlParsingException {
        this.workingDir = workingDir;
        this.packageType = loadMetadata(metadataFile);
    }

    public MetadataXmlProvider(File workingDir, PackageType packageType) {
        this.workingDir = workingDir;
        this.packageType = packageType;
    }

    private PackageType loadMetadata(File metadataFile) throws FileNotFoundException, XmlParsingException {
        if (!metadataFile.isFile()) {
            throw new FileNotFoundException(String.format(
                    "Invalid metadata.xml file: '%s' not found", metadataFile.getAbsolutePath()));
        }
        return XmlParser.parse(metadataFile, new String[]{METADATA_XML_SCHEME}, METADATA_PACKAGE, PackageType.class);
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
     * Get basic language specified in metadata.
     *
     * @return basic language
     */
    public String getLanguage() {
        return packageType.getLanguage();
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

    public void appendAsset(DataFileType dataFile, AssetTypeType assetType) {
        //  set WW territory by default
        TerritoriesType territories = new TerritoriesType();
        territories.getTerritory().add("WW");

        AssetType asset = new AssetType();
        asset.setType(assetType);
        asset.setTerritories(territories);
        asset.getDataFile().add(dataFile);

        ensureAssetsCreated().getAsset().add(asset);
    }

    /**
     * Save managed metadata to file.
     * Within marshalling metadata will be validated by strict schema.
     * Assets and chapters full info is required.
     *
     * @param path a path to destination dir relative to working dir
     * @return metadata.xml file
     */
    public File saveMetadata(String path) {
        File relativeDir = new File(workingDir, path);
        if (!relativeDir.exists()) {
            if (!relativeDir.mkdir()) {
                throw new ConversionException(String.format("Couldn't create %s directory for metadata!", relativeDir));
            }
        }
        File file = new File(relativeDir, "metadata.xml");
        marshallMetadata(packageType, METADATA_XML_STRICT_SCHEME, file);
        return file;
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
            throw new RuntimeException(e);
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

