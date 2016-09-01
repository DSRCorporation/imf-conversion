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

import com.apple.itunes.importer.tv.PackageType;
import com.netflix.imfutility.itunes.metadata.MetadataDescriptor;

import static com.netflix.imfutility.itunes.ITunesConversionXsdConstants.TV_METADATA_NAMESPACE;
import static com.netflix.imfutility.itunes.ITunesConversionXsdConstants.TV_METADATA_PACKAGE;
import static com.netflix.imfutility.itunes.ITunesConversionXsdConstants.TV_METADATA_ROOT_ELEMENT;
import static com.netflix.imfutility.itunes.ITunesConversionXsdConstants.TV_METADATA_XML_SCHEME;

/**
 * Metadata descriptor for iTunes tv specification.
 */
public final class TvMetadataDescriptor implements MetadataDescriptor<PackageType> {

    public static final TvMetadataDescriptor INSTANCE = new TvMetadataDescriptor();

    private TvMetadataDescriptor() {
    }

    @Override
    public Class<PackageType> getMetadataClass() {
        return PackageType.class;
    }

    @Override
    public String getMetadataSchema() {
        return TV_METADATA_XML_SCHEME;
    }

    @Override
    public String getMetadataPackage() {
        return TV_METADATA_PACKAGE;
    }

    @Override
    public String getMetadataNamespace() {
        return TV_METADATA_NAMESPACE;
    }

    @Override
    public String getMetadataRoot() {
        return TV_METADATA_ROOT_ELEMENT;
    }
}
