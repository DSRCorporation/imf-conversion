package com.netflix.imfutility.inputparameters;

import com.lexicalscope.jewel.cli.Option;

/**
 * Defines all command line parameters common for all formats. Some of the parameters are optional.
 */
public interface ImfUtilityCmdLineArgs {

    @Option(helpRequest = true, description = "display help", shortName = {"h"}, longName = {"help"})
    boolean getHelp();

    @Option(description = "a full path to the IMP folder", longName = {"imp"}, defaultToNull = true)
    String getImp();

    @Option(description = "a name of the CPL within the IMP folder", longName = {"cpl"}, defaultToNull = true)
    String getCpl();

    @Option(description = "a full path to a config.xml", shortName = {"c"}, longName = {"config"}, defaultToNull = true)
    String getConfig();

    @Option(description = "a working directory where conversion is performed and output flatten file(s) are placed",
            shortName = {"w"}, longName = {"working-dir"}, defaultToNull = true)
    String getWorkingDirectory();

}
