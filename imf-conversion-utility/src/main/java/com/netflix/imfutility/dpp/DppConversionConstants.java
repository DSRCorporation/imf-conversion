package com.netflix.imfutility.dpp;

/**
 * Constants related to DPP format.
 */
public final class DppConversionConstants {

/* 1. Conversion.xml */

    // 1.1 dynamic parameters:
    public static final String DYNAMIC_PARAM_PAN = "panParameter";
    public static final String DYNAMIC_PARAM_EBU_AUDIO_TRACKS = "ebuAudioTracks";
    public static final String DYNAMIC_PARAM_UK_DPP_FILE = "ukDppFramework";
    public static final String DYNAMIC_PARAM_AS11_CORE_FILE = "as11CoreFramework";
    public static final String DYNAMIC_PARAM_AS11_SEGM_FILE = "as11SegmentationFramework";
    public static final String DYNAMIC_AUDIO_MAP_XML = "audioMapXml";
    public static final String DYNAMIC_PARAM_OUTPUT_MXF = "output";

    // 1.2 dynamic parameters default values:
    public static final String DYNAMIC_PARAM_VALUE_OUTPUT_MXF = "output.mxf";

/* 2. default values */

    public static final String DEFAULT_AUDIO_MAP = "audiomap.xml";

/* 3. XSD */

    // 3.1: audiomap.xml
    public static final String AUDIOMAP_XML_SCHEME = "xsd/dpp/audiomap/audiomap.xsd";
    public static final String AUDIOMAP_PACKAGE = "com.netflix.imfutility.dpp.audiomap";

    // 3.2: metadata.xml
    public static final String METADATA_XML_SCHEME = "xsd/dpp/metadata/metadata.xsd";
    public static final String METADATA_PACKAGE = "com.netflix.imfutility.dpp.metadata";

/* 4. XSLT */

    public static final String BMX_PARAMETERS_TRANSFORMATION = "xsd/dpp/metadata/bmx-parameters.xsl";
    public static final String XSLT2_TRANSFORMER_IMPLEMENTATION = "net.sf.saxon.TransformerFactoryImpl";
    public static final String BMX_FRAMEWORK_PARAM = "framework";

/* 5. xsd namespaces */

    public static final String AUDIOMAP_NAMESPACE = "http://audiomap.dpp.imfutility.netflix.com";
    public static final String METADATA_NAMESPACE = "http://metadata.dpp.imfutility.netflix.com";

    private DppConversionConstants() {

    }

}
