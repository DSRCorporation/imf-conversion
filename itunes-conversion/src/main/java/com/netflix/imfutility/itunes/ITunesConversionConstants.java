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
 * CoreConstants related to iTunes format.
 */
public final class ITunesConversionConstants {

    private ITunesConversionConstants() {
    }

    // conversion.xml location
    public static final String CONVERSION_XML = "xml/itunes-conversion.xml";

    // common constants
    public static final String DEFAULT_LOCALE = "en-US";
    public static final String DEFAULT_METADATA_FILENAME = "metadata.xml";

    // common dynamic parameters:
    public static final String DYNAMIC_PARAM_TTML_TO_ITT = "ttml-to-itt";
    public static final String DYNAMIC_PARAM_OUTPUT_ITMSP = "outputItmsp";
    public static final String DYNAMIC_PARAM_VENDOR_ID = "vendorId";
    public static final String DYNAMIC_PARAM_DEST_SOURCE = "destSource";
    public static final String DYNAMIC_PARAM_IS_OSX = "isOSX";
    public static final String DYNAMIC_PARAM_SAME_FPS = "sameFps";

    // trailer dynamic parameters:
    public static final String DYNAMIC_PARAM_TRAILER_MEDIAINFO_INPUT = "trailerMediaInfoInput";
    public static final String DYNAMIC_PARAM_TRAILER_MEDIAINFO_OUTPUT = "trailerMediaInfoOutput";
    public static final String DYNAMIC_PARAM_TRAILER_MEDIAINFO_COMMAND = "trailer";

    // video dest parameters:
    public static final String DEST_PARAM_VIDEO_IFRAME_RATE = "iFrameRate";
    public static final String DEST_PARAM_VIDEO_IS_DAR_SPECIFIED = "isDarSpecified";
    public static final String DEST_PARAM_VIDEO_SPECIFIED_FOR = "specifiedFor";

    // AudioMap constants
    public static final String GEN_MAIN_SEQ_UUID = "urn:uuid:38d52c00-68d3-4056-8858-28eeaf3238d3";
    public static final String GEN_ADDITIONAL_SEQ_UUID = "urn:uuid:38d52c00-68d3-4056-8858-28eeaf3238d4";

    public static final int MONO_CHANNELS = 1;
    public static final int STEREO_CHANNELS = 2;
    public static final int SURROUND51_CHANNELS = 6;
    public static final int SURROUND51_DOWNMIX_CHANNELS = 8;

    // audio dynamic parameters
    public static final String DYNAMIC_PAN_PARAMETER_PREFIX = "panParameter";
    public static final String DYNAMIC_ADDITIONAL_AUDIO_TRACKS_PREFIX = "additionalAudioTracks";
    public static final String DYNAMIC_ADDITIONAL_AUDIO_PREFIX = "additionalAudio";
    public static final String DYNAMIC_MAIN_AUDIO = "mainAudio";
    public static final String DYNAMIC_MAIN_AUDIO_TRACKS = "mainAudioTracks";
    public static final String DYNAMIC_ADDITIONAL_AUDIO_COUNT = "additionalAudioCount";
    public static final String DYNAMIC_PARAM_AUDIO_SILENCE_EXPR_PREFIX = "silenceExpr";

    // subtitle dynamic parameters:
    public static final String DYNAMIC_PARAM_SUBTITLE_IS_CPL_SUB = "isCplSub";
    public static final String DYNAMIC_PARAM_SUBTITLE_IS_INPUT_PARAM_SUB = "isInputParamSub";
    public static final String DYNAMIC_PARAM_SUBTITLE_TTML_COUNT = "ttmlCount";
    public static final String DYNAMIC_PARAM_SUBTITLE_TTML_PREFIX = "ttml-";
    public static final String DYNAMIC_PARAM_SUBTITLE_ITT_PREFIX = "itt-";

    // subtitle common constants:
    public static final String TTML_PACKAGES = "org.w3.ns.ttml"
            + ":org.w3.ns.ttml_parameter"
            + ":org.w3.ns.ttml_datatype"
            + ":org.w3.ns.ttml_metadata";
    public static final String TTML_SCHEMA = "xsd/CR-ttaf1-dfxp-20100223/ttaf1-dfxp.xsd";
}
