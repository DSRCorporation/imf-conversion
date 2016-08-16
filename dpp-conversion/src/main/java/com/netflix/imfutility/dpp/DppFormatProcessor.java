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

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.dpp.audio.AudioMapXmlCreator;
import com.netflix.imfutility.dpp.inputparameters.DppCmdLineArgs;
import com.netflix.imfutility.dpp.inputparameters.DppInputParameters;
import com.netflix.imfutility.dpp.inputparameters.DppInputParametersValidator;
import com.netflix.imfutility.dpp.inputparameters.IDppDefaultTools;
import com.netflix.imfutility.dpp.metadata.MetadataXmlCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An input point for dealing with DPP format. It parses command line arguments and calls appropriate methods depending on the mode
 * (conversion, generation of a sample metadata.xml, generation of a sample audiomap.xml).
 */
public class DppFormatProcessor {

    private final Logger logger = LoggerFactory.getLogger(DppFormatProcessor.class);

    private final IDppDefaultTools defaultTools;

    public DppFormatProcessor(IDppDefaultTools defaultTools) {
        this.defaultTools = defaultTools;
    }

    public int process(DppCmdLineArgs cmdLineArgs) {
        logger.info("DPP format\n");

        // 1. wrap cmd line args to an input parameters object
        DppInputParameters inputParameters = new DppInputParameters(cmdLineArgs, defaultTools);

        // 2. validate cmd line arguments
        logger.info("Validating command line arguments...");
        DppInputParametersValidator.validateCmdLineArguments(inputParameters);
        logger.info("Validated command line arguments: OK\n");

        // 3. execute the specified mode
        switch (inputParameters.getCmdLineArgs().getMode()) {
            case convert:
                return processConversionMode(inputParameters);
            case metadata:
                return processMetadataMode(inputParameters);
            case audiomap:
                return processAudiomapMode(inputParameters);
            default: // nothing
        }

        throw new ConversionException("Unsupported DPP mode" + inputParameters.getCmdLineArgs().getMode().toString());
    }

    private int processMetadataMode(DppInputParameters inputParameters) {
        logger.info("Metadata mode\n");
        logger.info("Generating a sample Metadata.xml file {}", inputParameters.getCmdLineArgs().getOutput());
        MetadataXmlCreator.generateEmptyXml(inputParameters.getCmdLineArgs().getOutput());
        logger.info("Generated a sample Metadata.xml file: OK");
        return 0;
    }

    private int processAudiomapMode(DppInputParameters inputParameters) {
        logger.info("Audiomap mode\n");
        logger.info("Generating a sample Audiomap.xml file {}", inputParameters.getCmdLineArgs().getOutput());
        AudioMapXmlCreator.generateSampleXml(inputParameters.getCmdLineArgs().getOutput());
        logger.info("Generated a sample Audiomap.xml file: OK");
        return 0;
    }

    private int processConversionMode(DppInputParameters inputParameters) {
        logger.info("Conversion mode\n");
        return new DppFormatBuilder(inputParameters).build();
    }


}
