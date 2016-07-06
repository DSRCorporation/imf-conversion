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
package com.netflix.imfutility.dpp.inputparameters;

import com.lexicalscope.jewel.cli.Option;
import com.netflix.imfutility.dpp.DppMode;
import com.netflix.imfutility.inputparameters.ImfUtilityCmdLineArgs;

/**
 * Defines command line arguments specific for DPP format. Some of the parameters are optional.
 */
public interface DppCmdLineArgs extends ImfUtilityCmdLineArgs {

    @Option(description = "DPP format: DPP mode. Possible values: [convert, metadata, audiomap]. 'convert' mode performs conversion to DPP format. 'metadata' mode generates an empty metadata.xml. 'audiomap' mode generates a default audiomap.xml.",
            shortName = {"m"}, longName = {"mode"}, defaultValue = "convert")
    DppMode getMode();

    @Option(description = "DPP format: 'convert' mode only. A full path to metadata.xml", longName = {"metadata"}, defaultToNull = true)
    String getMetadata();

    @Option(description = "DPP format: 'convert' mode only. A full path to audiomap.xml", longName = {"audiomap"}, defaultToNull = true)
    String getAudioMap();

    @Option(description = "DPP format: 'metadata' and 'audiomap' modes only. A full path to the generated file (either metadata.xml or audiomap.xml)",
            shortName = "o", longName = {"output"}, defaultToNull = true)
    String getOutput();

}
