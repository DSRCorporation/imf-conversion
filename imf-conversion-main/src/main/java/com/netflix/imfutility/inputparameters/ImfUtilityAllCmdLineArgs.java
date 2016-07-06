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
package com.netflix.imfutility.inputparameters;

import com.lexicalscope.jewel.cli.Option;
import com.netflix.imfutility.Format;
import com.netflix.imfutility.dpp.inputparameters.DppCmdLineArgs;

/**
 * A mixin of all possible command line arguments for all formats. It's needed for initial parsing to obtain the format.
 */
public interface ImfUtilityAllCmdLineArgs extends DppCmdLineArgs {

    @Option(description = "a format for conversion. Possible values: [dpp]", shortName = {"f"}, longName = {"format"})
    Format getFormat();

}
