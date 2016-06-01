package com.netflix.imfutility.inputparameters;

/**
 * Defines all command line parameters common for all formats. Some of the parameters are optional.
 */
public abstract class InputParameters {

    private final String configXml;
    private final String impDirectory;
    private final String cplXml;
    private final String defaultWorkingDirectory;
    private final boolean deleteTmpFilesOnExit;
    private final boolean deleteTmpFilesOnFail;

    public InputParameters(String configXml, String impDirectory, String cplXml, String defaultWorkingDirectory, boolean deleteTmpFilesOnExit, boolean deleteTmpFilesOnFail) {
        this.configXml = configXml;
        this.impDirectory = impDirectory;
        this.cplXml = cplXml;
        this.defaultWorkingDirectory = defaultWorkingDirectory;
        this.deleteTmpFilesOnExit = deleteTmpFilesOnExit;
        this.deleteTmpFilesOnFail = deleteTmpFilesOnFail;
    }

    public String getConfigXml() {
        return configXml;
    }

    public String getCplXml() {
        return cplXml;
    }

    public String getImpDirectory() {
        return impDirectory;
    }

    public boolean isDeleteTmpFilesOnExit() {
        return deleteTmpFilesOnExit;
    }

    public boolean isDeleteTmpFilesOnFail() {
        return deleteTmpFilesOnFail;
    }

    public String getDefaultWorkingDirectory() {
        return defaultWorkingDirectory;
    }
}
