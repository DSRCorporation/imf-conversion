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
package com.netflix.imfutility.itunes;

/**
 * Xsd constants related to iTunes format.
 */
public final class ITunesConversionXsdConstants {

    private ITunesConversionXsdConstants() {
    }

    // metadata.xml
    public static final String METADATA_XML_SCHEME = "xsd/metadata/itunes-metadata.xsd";
    public static final String METADATA_PACKAGE = "com.netflix.imfutility.generated.itunes.metadata";
    public static final String METADATA_NAMESPACE = "http://apple.com/itunes/importer";
    public static final String METADATA_ROOT_ELEMENT = "package";

    // audiomap.xml
    public static final String AUDIOMAP_XML_SCHEME = "xsd/audiomap/itunes-audiomap.xsd";
    public static final String AUDIOMAP_PACKAGE = "com.netflix.imfutility.generated.itunes.audiomap";

    // chapters.xml
    public static final String CHAPTERS_XML_SCHEME = "xsd/chapters/itunes-chapters.xsd";
    public static final String CHAPTERS_PACKAGE = "com.netflix.imfutility.generated.itunes.chapters";
}
