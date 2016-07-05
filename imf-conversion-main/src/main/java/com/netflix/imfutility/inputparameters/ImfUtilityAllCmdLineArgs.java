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
