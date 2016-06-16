package com.netflix.imfutility.dpp;

import com.lexicalscope.jewel.cli.CliFactory;
import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.dpp.inputparameters.DppCmdLineArgs;
import com.netflix.imfutility.dpp.inputparameters.DppInputParameters;
import com.netflix.imfutility.dpp.inputparameters.DppInputParametersValidator;

/**
 * An input point for dealing with DPP format. It parses command line arguments and calls appropriate methods depending on the mode
 * (conversion, generation of a sample metadata.xml, generation of a sample audiomap.xml).
 */
public class DppFormatProcessor {

    public int process(String... args) {
        // 1. parse command line args
        DppCmdLineArgs cmdLineArgs = CliFactory.parseArguments(DppCmdLineArgs.class, args);

        // 2. wrap cmd line args to an input parameters object
        DppInputParameters inputParameters = new DppInputParameters(cmdLineArgs);

        // 2. validate cmd line arguments
        DppInputParametersValidator.validateCmdLineArguments(inputParameters);

        // 3. execute the specified mode
        switch (inputParameters.getCmdLineArgs().getMode()) {
            case convert:
                return new DppFormatBuilder(inputParameters).build();
            case metadata:
                MetadataXmlProvider.generateEmptyXml(inputParameters.getCmdLineArgs().getOutput());
                return 0;
            case audiomap:
                AudioMapXmlProvider.generateSampleXml(inputParameters.getCmdLineArgs().getOutput());
                return 0;
        }

        throw new ConversionException("Unsupported DPP mode" + inputParameters.getCmdLineArgs().getMode().toString());
    }

}
