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
package com.netflix.imfutility.itunes.inputparameters;

import com.lexicalscope.jewel.cli.Option;
import com.netflix.imfutility.inputparameters.ImfUtilityCmdLineArgs;
import com.netflix.imfutility.itunes.ITunesMode;
import com.netflix.imfutility.itunes.videoformat.ITunesVideoFormat;

import java.util.List;

/**
 * Defines command line arguments specific for iTunes format. Most of the parameters are optional.
 */
public interface ITunesCmdLineArgs extends ImfUtilityCmdLineArgs {

    @Option(description = "iTunes format: iTunes mode. Possible values: [convert, metadata, audiomap, chapters]. "
            + "'convert' mode performs conversion to iTunes format. "
            + "'metadata' mode generates an empty metadata.xml. "
            + "'audiomap' mode generates a default audiomap.xml. "
            + "'chapters' mode generates a default chapters.xml.",
            shortName = {"m"}, longName = {"mode"}, defaultValue = "convert")
    ITunesMode getMode();

    @Option(description = "iTunes format: 'convert' mode only. A vendor identifier. "
            + "Can only contain alphanumeric symbols or underscores"
            + "Must be at least 6 characters in length",
            longName = {"vendor-id"}, defaultToNull = true)
    String getVendorId();

    @Option(description = "iTunes format: 'convert' mode only. A full path to metadata.xml",
            longName = {"metadata"}, defaultToNull = true)
    String getMetadata();

    @Option(description = "iTunes format: 'convert' mode only. A full path to audiomap.xml",
            longName = {"audiomap"}, defaultToNull = true)
    String getAudioMap();

    @Option(description = "iTunes format: 'convert' mode only. A destination encoded video format",
            shortName = {"f"}, longName = {"format"}, defaultToNull = true)
    ITunesVideoFormat getVideoFormat();

    @Option(description = "iTunes format: 'convert' mode only. A full path to trailer. "
            + "Must be MOV container",
            longName = {"trailer"}, defaultToNull = true)
    String getTrailer();

    @Option(description = "iTunes format: 'convert' mode only. A full path to poster. "
            + "Must be JPG file",
            longName = {"poster"}, defaultToNull = true)
    String getPoster();

    @Option(description = "iTunes format: 'convert' mode only. A full path to chapters.xml",
            longName = {"chapters"}, defaultToNull = true)
    String getChapters();

    @Option(description = "iTunes format: 'convert' mode only. A paths to external caption assets. "
            + "If not set, then only CPL TTML will be processed, else only specified. "
            + "TTML will be converted to ITT. SCC will be passed through",
            longName = {"cc"}, defaultToNull = true)
    List<String> getCc();

    @Option(description = "iTunes format: 'convert' mode only. A main locale of iTunes package. "
            + "Will be used if CPL or metadata locale are not set",
            longName = {"fallback-locale"}, defaultToNull = true)
    String getFallbackLocale();

    @Option(description = "iTunes format: 'metadata', 'audiomap' and 'chapters' modes only. "
            + "A full path to the generated file",
            shortName = {"o"}, longName = {"output"}, defaultToNull = true)
    String getOutput();
}
