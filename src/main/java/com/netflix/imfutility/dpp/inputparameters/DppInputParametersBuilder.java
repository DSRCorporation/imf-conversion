package com.netflix.imfutility.dpp.inputparameters;

import com.netflix.imfutility.inputparameters.InputParametersBuilder;

/**
 * A builder for {@link DppInputParameters}.
 */
public class DppInputParametersBuilder extends InputParametersBuilder {

    private String metadataXml;
    private String audiomapXml;

    @Override
    public DppInputParametersBuilder setConfigXml(String configXml) {
        super.setConfigXml(configXml);
        return this;
    }

    @Override
    public DppInputParametersBuilder setImpDirectory(String impDirectory) {
        super.setImpDirectory(impDirectory);
        return this;
    }

    @Override
    public DppInputParametersBuilder setCplXml(String cplXml) {
        super.setCplXml(cplXml);
        return this;
    }

    @Override
    public DppInputParametersBuilder setDefaultWorkingDirectory(String defaultWorkingDirectory) {
        super.setDefaultWorkingDirectory(defaultWorkingDirectory);
        return this;
    }

    @Override
    public DppInputParametersBuilder setDeleteTmpFilesOnExit(boolean deleteTmpFilesOnExit) {
        super.setDeleteTmpFilesOnExit(deleteTmpFilesOnExit);
        return this;
    }

    @Override
    public DppInputParametersBuilder setDeleteTmpFilesOnFail(boolean deleteTmpFilesOnFail) {
        super.setDeleteTmpFilesOnFail(deleteTmpFilesOnFail);
        return this;
    }

    public DppInputParametersBuilder setMetadataXml(String metadataXml) {
        this.metadataXml = metadataXml;
        return this;
    }

    public DppInputParametersBuilder setAudiomapXml(String audiomapXml) {
        this.audiomapXml = audiomapXml;
        return this;
    }

    @Override
    public DppInputParameters build() {
        return new DppInputParameters(configXml, impDirectory, cplXml, defaultWorkingDirectory, deleteTmpFilesOnExit, deleteTmpFilesOnFail, metadataXml, audiomapXml);
    }
}