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
import com.netflix.imfutility.itunes.ITunesPackageType;

import java.util.List;

/**
 * Defines command line arguments specific for iTunes format. Most of the parameters are optional.
 */
public interface ITunesCmdLineArgs extends ImfUtilityCmdLineArgs {

    @Option(
            description =
                    "iTunes mode. "
                            + "\n\t\tPossible values: [convert, metadata, audiomap, chapters]. "
                            + "\n\t\t\t'convert' mode performs conversion to iTunes format. "
                            + "\n\t\t\t'metadata' mode generates an empty metadata.xml. "
                            + "\n\t\t\t'audiomap' mode generates a default audiomap.xml. "
                            + "\n\t\t\t'chapters' mode generates a default chapters.xml."
                            + "\n\t\t\t'formats'  mode prints supported destination video formats.",
            shortName = {"m"},
            longName = {"mode"},
            defaultValue = "convert"
    )
    ITunesMode getMode();

    @Option(
            description =
                    "a vendor identifier. "
                            + " ('convert' mode only)"
                            + " Can only contain alphanumeric symbols or underscores."
                            + " Must be at least 6 characters in length.",
            longName = {"vendor-id"},
            defaultToNull = true
    )
    String getVendorId();

    @Option(
            description =
                    "a full path to metadata.xml"
                            + " ('convert' mode only).",
            longName = {"metadata"},
            defaultToNull = true
    )
    String getMetadata();

    @Option(
            description =
                    "a full path to audiomap.xml"
                            + " ('convert' mode only).",
            longName = {"audiomap"},
            defaultToNull = true
    )
    String getAudioMap();

    @Option(
            description = "a destination iTunes package type."
                    + " Supported values: film (default), tv."
                    + " ('convert' and 'metadata' modes).",
            shortName = {"p"},
            longName = {"package-type"},
            defaultToNull = true
    )
    ITunesPackageType getPackageType();

    @Option(
            description = "a destination encoded video format"
                    + " ('convert' mode only).",
            shortName = {"f"},
            longName = {"format"},
            defaultToNull = true
    )
    String getFormat();

    @Option(
            description = "a path to trailer. "
                    + " ('convert' mode only)."
                    + " Must be MOV container",
            longName = {"trailer"},
            defaultToNull = true
    )
    String getTrailer();

    @Option(
            description = "a full path to poster. "
                    + " ('convert' mode only)."
                    + " Must be JPG file.",
            longName = {"poster"},
            defaultToNull = true
    )
    String getPoster();

    @Option(
            description = "a full path to chapters.xml"
                    + " ('convert' mode only).",
            longName = {"chapters"},
            defaultToNull = true
    )
    String getChapters();

    @Option(
            description = "a path to external closed captions. "
                    + " ('convert' mode only)."
                    + " Input file must have scc extension.",
            longName = {"cc"},
            defaultToNull = true,
            pattern = ".*\\.scc"
    )
    String getCc();

    @Option(
            description = "a paths to external subtitles. "
                    + " ('convert' mode only)."
                    + " Input files must have one of extensions: xml, ttml, itt."
                    + " Option takes from 1 to 13 files."
                    + " If not set, then only CPL TTML will be processed."
                    + " Otherwise only the specified TTML will be converted to ITT.",
            longName = {"sub"},
            defaultToNull = true,
            minimum = 1,
            maximum = 13,
            pattern = ".*\\.xml|.*\\.ttml|.*\\.itt"
    )
    List<String> getSub();

    @Option(
            description = "A main locale of iTunes package. "
                    + " ('convert' mode only)."
                    + " Locale must fit pattern xx, xx_XX (or xx-XX)."
                    + " It's used if CPL or metadata locale is not set.",
            longName = {"fallback-locale"},
            defaultToNull = true,
            pattern = "[a-z]{2}((-|_)[A-Z]{2})?$"
    )
    String getFallbackLocale();

    @Option(
            description = "a full path to the generated file"
                    + "\n\t\t'metadata' mode: a path to the generated metadata.xml (including 'metadata.xml' file name)."
                    + "\n\t\t'audiomap' mode: a path to the generated audiomap.xml (including 'audiomap.xml' file name)."
                    + "\n\t\t'chapters' mode: a path to the generated chapters.xml (including 'chapters.xml' file name).",
            shortName = {"o"},
            longName = {"output"},
            defaultToNull = true
    )
    String getOutput();
}
