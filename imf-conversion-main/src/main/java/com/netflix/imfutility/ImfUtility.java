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
package com.netflix.imfutility;

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.HelpRequestedException;
import com.netflix.imfutility.dpp.DppFormatProcessor;
import com.netflix.imfutility.dpp.inputparameters.DppCmdLineArgs;
import com.netflix.imfutility.inputparameters.DppTools;
import com.netflix.imfutility.inputparameters.ITunesTools;
import com.netflix.imfutility.inputparameters.ImfUtilityCmdLineArgs;
import com.netflix.imfutility.itunes.ITunesFormatProcessor;
import com.netflix.imfutility.itunes.inputparameters.ITunesCmdLineArgs;
import com.netflix.imfutility.util.ImfLogger;
import com.netflix.imfutility.util.LogHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * The main class.
 * <ul>
 * <li>Invokes command line parsing logic</li>
 * <li>Invokes an appropriate processor depending on the input format and mode</li>
 * </ul>
 */
public final class ImfUtility {

    private static final Logger LOGGER = new ImfLogger(LoggerFactory.getLogger(ImfUtility.class));

    private ImfUtility() {
    }

    public static void main(String... args) {
        try {
            if (args == null || args.length == 0 || args[0] == null) {
                throw new IllegalArgumentException(String.format(
                        "Format must be set as the first argument. Supported formats: %s", Format.getSupportedFormats()));
            }

            Format format = Format.fromName(args[0].toLowerCase());

            if (format == null) {
                throw new ConversionException(String.format(
                        "Unsupported format '%s'. Supported formats: %s", args[0], Format.getSupportedFormats()));
            }

            int exitCode;
            switch (format) {
                case dpp:
                    exitCode = new DppFormatProcessor(new DppTools()).process(parseArgs(DppCmdLineArgs.class, args));
                    System.exit(exitCode);
                    break;
                case itunes:
                    exitCode = new ITunesFormatProcessor(new ITunesTools()).process(parseArgs(ITunesCmdLineArgs.class, args));
                    System.exit(exitCode);
                    break;
                default:
                    throw new ConversionException(String.format(
                            "Unsupported format '%s'. Supported formats: %s", args[0], Format.getSupportedFormats()));
            }
        } catch (HelpRequestedException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        } catch (ConversionException | IllegalArgumentException e) {
            LOGGER.error("Conversion aborted: {}", e.getMessage());
            System.exit(1);
        }
    }

    private static <T extends ImfUtilityCmdLineArgs> T parseArgs(Class<T> clazz, String[] args) {
        LOGGER.debug("Parsing command line arguments...");
        T imfArgs = CliFactory.parseArguments(clazz, Arrays.copyOfRange(args, 1, args.length));
        LogHelper.setLogLevel(imfArgs.getLogLevel().getLogLevel());
        LOGGER.debug("Parsed command line arguments: OK\n");
        return imfArgs;
    }

}
