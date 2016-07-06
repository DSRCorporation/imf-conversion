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
package com.netflix.imfutility.validation;

import com.lexicalscope.jewel.cli.Option;

/**
 * An interface describing all Command Line arguments (Jewel CLI framework).
 */
public interface ImfValidationCmdLineArgs {

    @Option(description = "a full path to IMP folder to be validated", shortName = {"i"}, longName = {"imp"})
    String getImpFolder();

    @Option(description = "a full path to a CPL within the IMP folder to be validated", shortName = {"c"}, longName = {"cpl"})
    String getCpl();

    @Option(description = "a working directory where the output xml file with result of validation is created", shortName = {"d"}, longName = {"outdir"})
    String getOutputDirectory();

    @Option(description = "an output xml file with result of validation (default: errors.xml)", shortName = {"f"}, longName = {"outfile"}, defaultValue = "errors.xml")
    String getOutputFileName();

    @Option(helpRequest = true, description = "display help", shortName = {"h"}, longName = {"help"})
    boolean getHelp();

}
