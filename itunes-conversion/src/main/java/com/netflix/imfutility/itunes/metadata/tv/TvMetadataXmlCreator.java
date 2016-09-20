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


import com.apple.itunes.importer.tv.ObjectFactory;
import com.apple.itunes.importer.tv.PackageType;
import com.netflix.imfutility.itunes.metadata.tv.builder.TvMetadataXmlSampleBuilder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;

/**
 * Provides functionality to generate sample metadata.xml for iTunes tv specification.
 */
public final class TvMetadataXmlCreator {

    private TvMetadataXmlCreator() {
    }

    /**
     * Generates sample metadata.xml file.
     *
     * @param path a path to the output metadata.xml file
     */
    public static void generateSampleXml(String path) {
        File file = new File(path);
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(TvMetadataDescriptor.INSTANCE.getMetadataClass());
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            JAXBElement<PackageType> metadataJaxb = new ObjectFactory().createPackage(TvMetadataXmlSampleBuilder.buildPackage());
            jaxbMarshaller.marshal(metadataJaxb, file);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

}
