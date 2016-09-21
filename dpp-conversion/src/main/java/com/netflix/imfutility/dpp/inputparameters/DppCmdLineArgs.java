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
package com.netflix.imfutility.dpp.inputparameters;

import com.lexicalscope.jewel.cli.Option;
import com.netflix.imfutility.dpp.DppMode;
import com.netflix.imfutility.inputparameters.ImfUtilityCmdLineArgs;

/**
 * Defines command line arguments specific for DPP format. Some of the parameters are optional.
 */
public interface DppCmdLineArgs extends ImfUtilityCmdLineArgs {

    @Option(
            description =
                    "DPP mode."
                            + "\n\t\tPossible values: [convert, metadata, audiomap]."
                            + "\n\t\t\t'convert' mode performs conversion to DPP format."
                            + "\n\t\t\t'metadata' mode generates an empty metadata.xml."
                            + "\n\t\t\t'audiomap' mode generates a default audiomap.xml.",
            shortName = {"m"},
            longName = {"mode"},
            defaultValue = "convert"
    )
    DppMode getMode();

    @Option(
            description =
                    "a path to metadata.xml"
                            + " ('convert' mode only).",
            longName = {"metadata"},
            defaultToNull = true
    )
    String getMetadata();

    @Option(
            description =
                    "a path to audiomap.xml"
                            + " ('convert' mode only).",
            longName = {"audiomap"},
            defaultToNull = true
    )
    String getAudioMap();

    @Option(
            description =
                    "the output name"
                            + "\n\t\t'convert' mode: the output .mxf file name (without .mxf extension)."
                            + "\n\t\t'metadata' mode: a path to the generated metadata.xml (including 'metadata.xml' file name)."
                            + "\n\t\t'audiomap' mode: a path to the generated audiomap.xml (including 'audiomap.xml' file name)."
            ,
            shortName = "o",
            longName = {"output"},
            defaultToNull = true
    )
    String getOutput();

    @Option(
            description =
                    "a pre/post watershed version"
                            + " ('convert' mode only).",
            longName = {"watershed"},
            defaultValue = "Watershed version"
    )
    String getWatershedVersion();

}
