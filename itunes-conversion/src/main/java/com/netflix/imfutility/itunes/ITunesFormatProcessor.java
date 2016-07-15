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

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.itunes.inputparameters.ITunesCmdLineArgs;
import com.netflix.imfutility.itunes.inputparameters.ITunesDefaultTools;
import com.netflix.imfutility.itunes.inputparameters.ITunesInputParameters;
import com.netflix.imfutility.itunes.inputparameters.ITunesInputParametersValidator;
import com.netflix.imfutility.itunes.xmlprovider.AudioMapXmlProvider;
import com.netflix.imfutility.itunes.xmlprovider.ChaptersXmlProvider;
import com.netflix.imfutility.itunes.xmlprovider.MetadataXmlProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An input point for dealing with iTunes format. It parses command line arguments and calls appropriate methods depending on the mode
 * (conversion, generation of a sample metadata.xml, generation of a sample audiomap.xml, generation of a sample chapters.xml).
 */
public class ITunesFormatProcessor {

    private final Logger logger = LoggerFactory.getLogger(ITunesFormatProcessor.class);

    private final ITunesDefaultTools defaultTools;

    public ITunesFormatProcessor(ITunesDefaultTools defaultTools) {
        this.defaultTools = defaultTools;
    }

    public int process(ITunesCmdLineArgs cmdLineArgs) {
        logger.info("iTunes format\n");

        // 1. wrap cmd line args to an input parameters object
        ITunesInputParameters inputParameters = new ITunesInputParameters(cmdLineArgs, defaultTools);

        // 2. validate cmd line arguments
        logger.info("Validating command line arguments...");
        ITunesInputParametersValidator.validateCmdLineArguments(inputParameters);
        logger.info("Validated command line arguments: OK\n");

        // 3. execute the specified mode
        switch (inputParameters.getCmdLineArgs().getMode()) {
            case convert:
                return processConversionMode(inputParameters);
            case metadata:
                return processMetadataMode(inputParameters);
            case audiomap:
                return processAudiomapMode(inputParameters);
            case chapters:
                return processChaptersMode(inputParameters);
            default: // nothing
        }

        throw new ConversionException("Unsupported ITunes mode" + inputParameters.getCmdLineArgs().getMode().toString());
    }

    private int processMetadataMode(ITunesInputParameters inputParameters) {
        logger.info("Metadata mode\n");
        logger.info("Generating a sample Metadata.xml file {}", inputParameters.getCmdLineArgs().getOutput());
        MetadataXmlProvider.generateSampleXml(inputParameters.getCmdLineArgs().getOutput());
        logger.info("Generated a sample Metadata.xml file: OK");
        return 0;
    }

    private int processAudiomapMode(ITunesInputParameters inputParameters) {
        logger.info("Audiomap mode\n");
        logger.info("Generating a sample Audiomap.xml file {}", inputParameters.getCmdLineArgs().getOutput());
        AudioMapXmlProvider.generateSampleXml(inputParameters.getCmdLineArgs().getOutput());
        logger.info("Generated a sample Audiomap.xml file: OK");
        return 0;
    }

    private int processChaptersMode(ITunesInputParameters inputParameters) {
        logger.info("Chapters mode\n");
        logger.info("Generating a sample Chapters.xml file {}", inputParameters.getCmdLineArgs().getOutput());
        ChaptersXmlProvider.generateSampleXml(inputParameters.getCmdLineArgs().getOutput());
        logger.info("Generated a sample Chapters.xml file: OK");
        return 0;
    }

    private int processConversionMode(ITunesInputParameters inputParameters) {
        logger.info("Conversion mode\n");
        //  TODO: implement ITunesFormatBuilder
        return 0;
    }

}
