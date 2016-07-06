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
