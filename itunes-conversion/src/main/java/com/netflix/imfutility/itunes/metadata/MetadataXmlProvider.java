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

import com.netflix.imfutility.itunes.asset.type.Asset;
import com.netflix.imfutility.itunes.asset.type.AssetRole;
import com.netflix.imfutility.itunes.asset.type.ChapterAsset;
import com.netflix.imfutility.itunes.xmlprovider.LocalizedXmlProvider;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Locale;

import static com.netflix.imfutility.itunes.ITunesConversionConstants.DEFAULT_METADATA_FILENAME;

/**
 * Provides functionality to manage asset metadata info.
 *
 * @param <T> type of metadata root element.
 */
public abstract class MetadataXmlProvider<T> implements LocalizedXmlProvider {

    private final boolean customized;

    protected final MetadataDescriptor<T> metadataDescriptor;
    protected final T rootElement;
    protected final JAXBContext context;

    public MetadataXmlProvider(MetadataDescriptor<T> metadataDescriptor) throws FileNotFoundException, XmlParsingException {
        this(metadataDescriptor, null);
    }

    public MetadataXmlProvider(MetadataDescriptor<T> metadataDescriptor, File metadataFile)
            throws FileNotFoundException, XmlParsingException {
        this.customized = metadataFile != null;

        this.metadataDescriptor = metadataDescriptor;
        this.rootElement = customized ? loadMetadata(metadataFile) : generateDefaultMetadata();
        try {
            this.context = JAXBContext.newInstance(metadataDescriptor.getMetadataClass());
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private T loadMetadata(File metadataFile) throws FileNotFoundException, XmlParsingException {
        if (!metadataFile.isFile()) {
            throw new FileNotFoundException(String.format(
                    "Invalid metadata.xml file: '%s' not found", metadataFile.getAbsolutePath()));
        }
        return XmlParser.parse(metadataFile,
                new String[]{metadataDescriptor.getMetadataSchema()},
                metadataDescriptor.getMetadataPackage(),
                metadataDescriptor.getMetadataClass());
    }

    public boolean isCustomized() {
        return customized;
    }

    /**
     * Get root metadata element.
     *
     * @return root package tag
     */
    public T getRootElement() {
        return rootElement;
    }

    // Common metadata methods

    public abstract void updateVendorId(String vendorId);

    protected abstract T generateDefaultMetadata();

    //   Asset processing

    public abstract void appendAsset(Asset asset);

    public abstract List<Locale> getLocalesByRole(AssetRole role);

    // Chapters processing

    public abstract void appendChaptersTimeCode(String timeCode);

    public abstract void appendChapterAsset(ChapterAsset chapterAsset);

    /**
     * Save managed metadata to file.
     * Within marshalling metadata will be validated by schema.
     *
     * @param dir a directory to save metadata
     * @return metadata.xml file
     */
    public File saveMetadata(File dir) {
        File file = new File(dir, DEFAULT_METADATA_FILENAME);
        marshallMetadata(rootElement, file);
        return file;
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
    private Marshaller createMarshaller(JAXBContext jaxbContext, String schemaPath) throws SAXException, JAXBException {
        Schema schema = XmlParser.getSchema(new String[]{schemaPath});

        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setSchema(schema);
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        return jaxbMarshaller;
    }

    /**
     * Marshall metadata package.
     *
     * @param root element to marshall
     */
    private void marshallMetadata(T root, File file) {
        try {
            Marshaller jaxbMarshaller = createMarshaller(context, metadataDescriptor.getMetadataSchema());

            QName qName = new QName(metadataDescriptor.getMetadataNamespace(), metadataDescriptor.getMetadataRoot());
            JAXBElement<T> metadataJaxb = new JAXBElement<>(qName, metadataDescriptor.getMetadataClass(), null, root);
            jaxbMarshaller.marshal(metadataJaxb, file);
        } catch (SAXException | JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    protected static <T> void setSingleValue(List<T> list, T value) {
        list.clear();
        list.add(value);
    }

    protected static <T> T getSingleValue(List<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

}

