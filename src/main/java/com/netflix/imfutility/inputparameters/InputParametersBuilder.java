package com.netflix.imfutility.inputparameters;

import com.netflix.imfutility.Constants;

/**
 * A builder for {@link InputParameters}.
 */
public abstract class InputParametersBuilder {

    protected String configXml;
    protected String impDirectory;
    protected String cplXml;
    protected String defaultWorkingDirectory;
    protected boolean deleteTmpFilesOnExit = Constants.DEFAULT_DELETE_TMP_FILES_ON_EXIT;
    protected boolean deleteTmpFilesOnFail = Constants.DEFAULT_DELETE_TMP_FILES_ON_FAIL;

    public InputParametersBuilder setConfigXml(String configXml) {
        this.configXml = configXml;
        return this;
    }

    public InputParametersBuilder setImpDirectory(String impDirectory) {
        this.impDirectory = impDirectory;
        return this;
    }

    public InputParametersBuilder setCplXml(String cplXml) {
        this.cplXml = cplXml;
        return this;
    }

    public InputParametersBuilder setDeleteTmpFilesOnExit(boolean deleteTmpFilesOnExit) {
        this.deleteTmpFilesOnExit = deleteTmpFilesOnExit;
        return this;
    }

    public InputParametersBuilder setDeleteTmpFilesOnFail(boolean deleteTmpFilesOnFail) {
        this.deleteTmpFilesOnFail = deleteTmpFilesOnFail;
        return this;
    }

    public InputParametersBuilder setDefaultWorkingDirectory(String defaultWorkingDirectory) {
        this.defaultWorkingDirectory = defaultWorkingDirectory;
        return this;
    }

    public abstract InputParameters build();
}