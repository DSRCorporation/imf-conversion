package com.netflix.imfutility.dpp.inputparameters;

import com.netflix.imfutility.inputparameters.InputParameters;

/**
 * Defines command line parameters specific for DPP format. Some of the parameters are optional.
 */
public class DppInputParameters extends InputParameters {

    private final String metadataXml;
    private final String audiomapXml;

    DppInputParameters(String configXml, String impDirectory, String cplXml, String defaultWorkingDirectory, boolean deleteTmpFilesOnExit, boolean deleteTmpFilesOnFail,
                       String metadataXml, String audiomapXml) {
        super(configXml, impDirectory, cplXml, defaultWorkingDirectory, deleteTmpFilesOnExit, deleteTmpFilesOnFail);
        this.metadataXml = metadataXml;
        this.audiomapXml = audiomapXml;
    }

    public String getMetadataXml() {
        return metadataXml;
    }

    public String getAudiomapXml() {
        return audiomapXml;
    }

}
