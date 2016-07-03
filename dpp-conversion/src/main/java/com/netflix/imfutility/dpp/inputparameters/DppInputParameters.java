package com.netflix.imfutility.dpp.inputparameters;

import com.netflix.imfutility.inputparameters.ImfUtilityInputParameters;

import java.io.File;

/**
 * A wrapper on command line arguments with helper methods to get input parameters obtained from the command line.
 */
public class DppInputParameters extends ImfUtilityInputParameters {

    private final DppCmdLineArgs cmdLineArgs;

    public DppInputParameters(DppCmdLineArgs cmdLineArgs) {
        super(cmdLineArgs);
        this.cmdLineArgs = cmdLineArgs;
    }

    /**
     * @return a metadata file as specified via command line arguments or null if it's not specified.
     */
    public File getMetadataFile() {
        if (cmdLineArgs.getMetadata() == null) {
            return null;
        }
        return new File(cmdLineArgs.getMetadata());
    }

    /**
     * @return an audiomap file as specified via command line arguments or null if it's not specified.
     */
    public File getAudiomapFile() {
        if (cmdLineArgs.getAudioMap() == null) {
            return null;
        }
        return new File(cmdLineArgs.getAudioMap());
    }

    @Override
    public DppCmdLineArgs getCmdLineArgs() {
        return cmdLineArgs;
    }
}
