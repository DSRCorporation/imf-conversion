/**
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
package com.netflix.imfutility.dpp;

/**
 * Constants related to DPP format.
 */
public final class DppConversionXsdConstants {

/* XSD */

    // 1: audiomap.xml
    public static final String AUDIOMAP_XML_SCHEME = "xsd/audiomap/audiomap.xsd";
    public static final String AUDIOMAP_PACKAGE = "com.netflix.imfutility.generated.dpp.audiomap";

    // 2: metadata.xml
    public static final String METADATA_XML_SCHEME = "xsd/metadata/metadata.xsd";
    public static final String ISO_639_2_CODES_XML_SCHEME = "xsd/metadata/iso-639-2-codes.xsd";
    public static final String TYPES_XML_SCHEME = "xsd/metadata/types.xsd";
    public static final String METADATA_PACKAGE = "com.netflix.imfutility.generated.dpp.metadata";


    private DppConversionXsdConstants() {

    }

}
