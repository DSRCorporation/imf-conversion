package com.netflix.imfutility;

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.HelpRequestedException;
import com.netflix.imfutility.dpp.DppFormatProcessor;
import com.netflix.imfutility.inputparameters.DppTools;
import com.netflix.imfutility.inputparameters.ImfUtilityAllCmdLineArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main class.
 * <ul>
 * <li>Invokes command line parsing logic</li>
 * <li>Invokes an appropriate processor depending on the input format and mode</li>
 * </ul>
 */
public class ImfUtility {

    private static final Logger logger = LoggerFactory.getLogger(ImfUtility.class);

    public static void main(String... args) {
        try {
            logger.info("Parsing command line arguments...");
            ImfUtilityAllCmdLineArgs imfArgs = CliFactory.parseArguments(ImfUtilityAllCmdLineArgs.class, args);
            logger.info("Parsed command line arguments: OK\n");

            switch (imfArgs.getFormat()) {
                case dpp:
                    int exitCode = new DppFormatProcessor(new DppTools()).process(imfArgs);
                    System.exit(exitCode);
                    break;
                default:
                    throw new ConversionException("Unsupported format " + imfArgs.getFormat());
            }

        } catch (HelpRequestedException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

    }

}
