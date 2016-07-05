package com.netflix.imfutility.dpp;

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.dpp.inputparameters.DppCmdLineArgs;
import com.netflix.imfutility.dpp.inputparameters.DppInputParameters;
import com.netflix.imfutility.dpp.inputparameters.DppInputParametersValidator;
import com.netflix.imfutility.dpp.inputparameters.IDppDefaultTools;
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
        }

        throw new ConversionException("Unsupported DPP mode" + inputParameters.getCmdLineArgs().getMode().toString());
    }

    private int processMetadataMode(DppInputParameters inputParameters) {
        logger.info("Metadata mode\n");
        logger.info("Generating a sample Metadata.xml file {}", inputParameters.getCmdLineArgs().getOutput());
        MetadataXmlProvider.generateEmptyXml(inputParameters.getCmdLineArgs().getOutput());
        logger.info("Generated a sample Metadata.xml file: OK");
        return 0;
    }

    private int processAudiomapMode(DppInputParameters inputParameters) {
        logger.info("Audiomap mode\n");
        logger.info("Generating a sample Audiomap.xml file {}", inputParameters.getCmdLineArgs().getOutput());
        AudioMapXmlProvider.generateSampleXml(inputParameters.getCmdLineArgs().getOutput());
        logger.info("Generated a sample Audiomap.xml file: OK");
        return 0;
    }

    private int processConversionMode(DppInputParameters inputParameters) {
        logger.info("Conversion mode\n");
        return new DppFormatBuilder(inputParameters).build();
    }


}
