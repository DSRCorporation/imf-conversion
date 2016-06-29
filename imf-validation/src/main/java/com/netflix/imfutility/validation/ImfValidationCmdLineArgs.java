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
