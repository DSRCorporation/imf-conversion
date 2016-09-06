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
package com.netflix.imfutility.itunes.metadata.factory;

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.itunes.ITunesPackageType;
import com.netflix.imfutility.itunes.metadata.MetadataDescriptor;
import com.netflix.imfutility.itunes.metadata.MetadataXmlProvider;
import com.netflix.imfutility.itunes.metadata.film.FilmMetadataDescriptor;
import com.netflix.imfutility.itunes.metadata.film.FilmMetadataXmlProvider;
import com.netflix.imfutility.itunes.metadata.tv.TvMetadataDescriptor;
import com.netflix.imfutility.itunes.metadata.tv.TvMetadataXmlProvider;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Default factory that creates metadata providers depends on packageType (film or tv).
 */
public final class MetadataXmlProviderFactory {

    private MetadataXmlProviderFactory() {
    }

    public static MetadataXmlProvider<?> createProvider(File metadataFile, ITunesPackageType fallbackPackageType)
            throws XmlParsingException, IOException {

        ITunesPackageType packageType = metadataFile != null
                ? resolveTypeFromFile(metadataFile)
                : fallbackPackageType;

        switch (packageType) {
            case film:
                return new FilmMetadataXmlProvider(metadataFile);
            case tv:
                return new TvMetadataXmlProvider(metadataFile);
            default:
                throw new ConversionException("Unsupported iTunes package type " + packageType.getName());
        }
    }

    private static ITunesPackageType resolveTypeFromFile(File metadataFile) {
        if (testAgainstSchema(metadataFile, FilmMetadataDescriptor.INSTANCE)) {
            return ITunesPackageType.film;
        }

        if (testAgainstSchema(metadataFile, TvMetadataDescriptor.INSTANCE)) {
            return ITunesPackageType.tv;
        }

        throw new ConversionException("Unsupported iTunes package type in metadata.xml " + metadataFile.getAbsolutePath());
    }

    private static boolean testAgainstSchema(File metadataFile, MetadataDescriptor<?> descriptor) {
        try (InputStream stream = new FileInputStream(metadataFile)) {
            Schema schema = XmlParser.getSchema(new String[]{descriptor.getMetadataSchema()});
            schema.newValidator().validate(new StreamSource(stream));
        } catch (SAXException e) {
            //  metadata failed validation against schema
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

}
