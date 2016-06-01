package com.netflix.imfutility;

/**
 * All constants to be used in the application.
 */
public final class Constants {

/* 1. logging */

    public static final String LOGS_DIR = "logs";
    public static final String LOG_TEMPLATE = "%d-%s-%s-%s.log";

/* 2. IMF */

    public static final String ASSETMAP_FILE = "ASSETMAP.xml";

/* 3. XSD */

    // 3.1: assetmap.xml
    public static final String XSD_ASSETMAP_XSD = "xsd/imf/asset-map.xsd";
    public static final String ASSETMAP_PACKAGE = "com.netflix.imfutility.xsd.imf.assetmap";

    // 3.2: config.xml
    public static final String CONFIG_XSD = "xsd/config.xsd";
    public static final String CONFIG_PACKAGE = "com.netflix.imfutility.xsd.config";

    // 3.3: conversion.xml
    public static final String CONVERSION_XSD = "xsd/conversion.xsd";
    public static final String CONVERSION_PACKAGE = "com.netflix.imfutility.xsd.conversion";

    // 3.4: audiomap.xml
    public static final String AUDIOMAP_XML_SCHEME = "xsd/dpp/audiomap.xsd";
    public static final String AUDIOMAP_PACKAGE = "com.netflix.imfutility.xsd.dpp.audiomap";

    // 3.5: metadata.xml
    public static final String METADATA_XML_SCHEME = "xsd/dpp/metadata.xsd";
    public static final String METADATA_PACKAGE = "com.netflix.imfutility.xsd.dpp.metadata";

    // 3.6 media-info.xml
    public static final String MEDIAINFO_XSD = "xsd/media-info.xsd";
    public static final String MEDIAINFO_PACKAGE = "com.netflix.imfutility.xsd.mediainfo";

    // 3.7 IMF 2013 cpl.xml
    public static final String XSD_CPL_2013_XSD = "xsd/imf/2013/imf-cpl-2013.xsd";
    public static final String CPL_2013_PACKAGE = "com.netflix.imfutility.xsd.imf._2013.cpl";

/* 4. Command line args*/

    // 4.1 default values
    public static final boolean DEFAULT_DELETE_TMP_FILES_ON_EXIT = true;
    public static final boolean DEFAULT_DELETE_TMP_FILES_ON_FAIL = false;

/* 5. Other */

    public static final String DEFAULT_CONVERSION_XML = "xml/conversion.xml";
    public static final String MEDIA_INFO_SUFFIX = "mediaInfo";

/* 6. XSLT */

    public static final String BMX_PARAMETERS_TRANSFORMATION = "xsd/dpp/bmx-parameters.xsl";
    public static final String XSLT2_TRANSFORMER_IMPLEMENTATION = "net.sf.saxon.TransformerFactoryImpl";
    public static final String BMX_FRAMEWORK_PARAM = "framework";

    private Constants() {

    }
}
