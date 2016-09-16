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
package com.netflix.imfutility.inputparameters;

import com.lexicalscope.jewel.cli.Option;

/**
 * Defines all command line parameters common for all formats. Some of the parameters are optional.
 */
public interface ImfUtilityCmdLineArgs {

    @Option(
            helpRequest = true,
            description = "display help",
            shortName = {"h"},
            longName = {"help"}
    )
    boolean getHelp();

    @Option(
            description =
                    "a path to the IMP folder",
            longName = {"imp"},
            defaultToNull = true
    )
    String getImp();

    @Option(
            description =
                    "a path to the CPL within the IMP folder."
                            + " Either a CPL name within the IMP folder or a path to the CPL can be specified.",
            longName = {"cpl"},
            defaultToNull = true
    )
    String getCpl();

    @Option(
            description = "a path to config.xml",
            shortName = {"c"},
            longName = {"config"},
            defaultToNull = true
    )
    String getConfig();

    @Option(
            description =
                    "a path to the working directory where conversion is performed and output flatten file(s) are placed."
                            + " If the directory doesn't exist, it will be created.",
            shortName = {"w"},
            longName = {"working-dir"},
            defaultToNull = true
    )
    String getWorkingDirectory();

    @Option(
            description =
                    "Log level."
                            + "\n\t\tPossible values: [error, warn, info (default), debug].",
            shortName = {"l"},
            longName = {"log-level"},
            defaultValue = "info"
    )
    LogLevel getLogLevel();

    @Option(
            description =
                    "Disable IMF package validation."
                            + "\n\t\tIf flag not set validation will be provided in accordance with config.xml value.",
            shortName = {"d"},
            longName = {"disable-validation"}
    )
    boolean getDisableValidation();

}
