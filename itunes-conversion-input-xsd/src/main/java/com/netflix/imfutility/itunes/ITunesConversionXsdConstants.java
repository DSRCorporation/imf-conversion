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
    public static final String ISO_3166_1_XML_SCHEME = "xsd/metadata/iso-3166-1.xsd";
    public static final String METADATA_XML_SCHEME = "xsd/metadata/itunes-metadata.xsd";
    public static final String METADATA_XML_STRICT_SCHEME = "xsd/metadata/itunes-strict-metadata.xsd";
    public static final String METADATA_PACKAGE = "com.netflix.imfutility.generated.itunes.metadata";

    // audiomap.xml
    public static final String AUDIOMAP_DEFAUL_FILE = "audiomap.xml";
    public static final String AUDIOMAP_XML_SCHEME = "xsd/audiomap/itunes-audiomap.xsd";
    public static final String AUDIOMAP_PACKAGE = "com.netflix.imfutility.generated.itunes.audiomap";

    public static final String CHAPTERS_XML_SCHEME = "xsd/chapters/itunes-chapters.xsd";
    public static final String CHAPTERS_PACKAGE = "com.netflix.imfutility.generated.itunes.metadata";
}
