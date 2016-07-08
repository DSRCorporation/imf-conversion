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
package com.netflix.imfutility;

/**
 * All constants to be used in the application.
 */
public final class CoreConstants {

/* 1. logging */

    public static final String LOGS_DIR = "logs";
    public static final String LOG_TEMPLATE = "%d-%s-%s-%s.log";

/* 2. IMF */

    public static final String ASSETMAP_FILE = "ASSETMAP.xml";

/* 3. XSD */

    // 3.1: assetmap.xml
    public static final String XSD_ASSETMAP_XSD = "xsd/imf/assetmap/asset-map.xsd";
    public static final String ASSETMAP_PACKAGE = "com.netflix.imfutility.generated.imf.assetmap";

    // 3.2: config.xml
    public static final String CONFIG_XSD = "xsd/config.xsd";
    public static final String CONFIG_PACKAGE = "com.netflix.imfutility.generated.config";

    // 3.3: conversion.xml
    public static final String CONVERSION_XSD = "xsd/conversion.xsd";
    public static final String CONVERSION_PACKAGE = "com.netflix.imfutility.generated.conversion";

    // 3.4 media-info.xml
    public static final String MEDIAINFO_XSD = "xsd/media-info.xsd";
    public static final String MEDIAINFO_PACKAGE = "com.netflix.imfutility.generated.mediainfo";

    // 3.5 IMF 2013 cpl.xml
    public static final String CPL_2013_XSD = "xsd/imf/2013/imf-cpl.xsd";
    public static final String CORE_CONSTRAINTS_2013_XSD = "xsd/imf/2013/imf-core-constraints.xsd";
    public static final String CPL_2013_PACKAGE = "com.netflix.imfutility.generated.imf._2013";

    // 3.6 IMF common
    public static final String DCML_TYPES_XSD = "xsd/imf/dcmlTypes.xsd";
    public static final String PACKING_LIST_XSD = "xsd/imf/packing-list.xsd";
    public static final String XMLDSIG_CORE_SCHEMA_XSD = "xsd/imf/xmldsig-core-schema.xsd";

/* 4. Command line args*/

    public static final boolean DEFAULT_DELETE_TMP_FILES_ON_EXIT = true;
    public static final boolean DEFAULT_DELETE_TMP_FILES_ON_FAIL = false;
    public static final boolean DEFAULT_CLEAN_WORKING_DIR = false;

/* 5. Default values */

    public static final String DEFAULT_OUTPUT_VALIDATION_FILE = "errors.xml";
    public static final String VALIDATION_OUTPUT_XML_ERROR_TAG = "error";
    public static final String MEDIA_INFO_SUFFIX = "mediaInfo";


/* 6. config.xml */

    public static final String IMF_VALIDATION_TOOL = "imf-validation";


    private CoreConstants() {

    }
}
