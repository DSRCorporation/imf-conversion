package com.netflix.imfutility.dpp.inputparameters;

import com.lexicalscope.jewel.cli.ArgumentValidationException;

import java.io.File;

/**
 * Validator of input parameters specific for DPP format. It checks whether all required parameters depending on the
 * mode are specified via command lines arguments. Only DPP-specific parameters are validated.
 */
public class DppInputParametersValidator {

    /**
     * Checks whether all required parameters depending on the  mode are specified via command lines arguments.
     * Only DPP-specific parameters are validated.
     *
     * @param inputParameters the input parameters to be validated.
     * @throws ArgumentValidationException if some of the parameters are missing
     */
    public static void validateCmdLineArguments(DppInputParameters inputParameters) throws ArgumentValidationException {
        switch (inputParameters.getCmdLineArgs().getMode()) {
            case convert:
                validateConvertMode(inputParameters);
                break;
            case metadata:
                validateMetadataMode(inputParameters);
                break;
            case audiomap:
                validateAudiomapMode(inputParameters);
                break;
        }
    }

    private static void validateConvertMode(DppInputParameters inputParameters) throws ArgumentValidationException {
        File metadataFile = inputParameters.getMetadataFile();
        if (metadataFile == null) {
            throw new ArgumentValidationException("Metadata.xml (--metadata) must be specified in 'convert' mode. Use 'metadata' mode (-m metadata) to generate a sample metadata.xml");
        }
        if (!metadataFile.isFile()) {
            throw new ArgumentValidationException(String.format(
                    "Working directory '%s' must be an existing folder", metadataFile.getAbsolutePath()));

        }

        File audiomapFile = inputParameters.getAudiomapFile();
        // audiomap.xml may be null. in this case a default audiomap is created.
        if (audiomapFile != null && audiomapFile.isFile()) {
            throw new ArgumentValidationException(String.format(
                    "Working directory '%s' must be an existing folder", audiomapFile.getAbsolutePath()));
        }
    }

    private static void validateMetadataMode(DppInputParameters inputParameters) throws ArgumentValidationException {
        if (inputParameters.getCmdLineArgs().getOutput() == null) {
            throw new ArgumentValidationException("Metadata.xml output path (-o, --output) must be specified in 'metadata' mode");
        }
    }

    private static void validateAudiomapMode(DppInputParameters inputParameters) throws ArgumentValidationException {
        if (inputParameters.getCmdLineArgs().getOutput() == null) {
            throw new ArgumentValidationException("Audiomap.xml output path (-o, --output) must be specified in 'audiomap' mode");
        }
    }

}
