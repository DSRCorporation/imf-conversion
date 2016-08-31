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

import com.netflix.imfutility.generated.itunes.metadata.film.PackageType;
import com.netflix.imfutility.itunes.metadata.MetadataDescriptor;

import static com.netflix.imfutility.itunes.ITunesConversionXsdConstants.FILM_METADATA_NAMESPACE;
import static com.netflix.imfutility.itunes.ITunesConversionXsdConstants.FILM_METADATA_PACKAGE;
import static com.netflix.imfutility.itunes.ITunesConversionXsdConstants.FILM_METADATA_ROOT_ELEMENT;
import static com.netflix.imfutility.itunes.ITunesConversionXsdConstants.FILM_METADATA_XML_SCHEME;

/**
 * Metadata descriptor for iTunes film specification.
 */
public final class FilmMetadataDescriptor implements MetadataDescriptor<PackageType> {

    public static final FilmMetadataDescriptor INSTANCE = new FilmMetadataDescriptor();

    private FilmMetadataDescriptor() {
    }

    @Override
    public Class<PackageType> getMetadataClass() {
        return PackageType.class;
    }

    @Override
    public String getMetadataSchema() {
        return FILM_METADATA_XML_SCHEME;
    }

    @Override
    public String getMetadataPackage() {
        return FILM_METADATA_PACKAGE;
    }

    @Override
    public String getMetadataNamespace() {
        return FILM_METADATA_NAMESPACE;
    }

    @Override
    public String getMetadataRoot() {
        return FILM_METADATA_ROOT_ELEMENT;
    }
}
