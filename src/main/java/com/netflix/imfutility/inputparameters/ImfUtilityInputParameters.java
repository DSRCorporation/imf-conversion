package com.netflix.imfutility.inputparameters;

import java.io.File;

/**
 * A wrapper on command line arguments with helper methods to get input parameters obtained from the command line.
 * Some of the parameters (such as CPL, IMP, working directory paths) can be defined either in config.xml or via command line arguments.
 * Setting a parameter via command line argument overwrites value from config.xml.
 */
public class ImfUtilityInputParameters {

    private final ImfUtilityCmdLineArgs cmdLineArgs;
    private String defaultImp;
    private String defaultCpl;
    private String defaultWorkingDir;

    public ImfUtilityInputParameters(ImfUtilityCmdLineArgs cmdLineArgs) {
        this.cmdLineArgs = cmdLineArgs;
    }

    public ImfUtilityCmdLineArgs getCmdLineArgs() {
        return cmdLineArgs;
    }

    /**
     * Gets a full path to config.xml file.
     *
     * @return a file defining a full path to config.xml or null if it's not specified via command line arguments
     */
    public File getConfigFile() {
        if (cmdLineArgs.getConfig() == null) {
            return null;
        }
        return new File(cmdLineArgs.getConfig());
    }

    /**
     * Gets a full path to CPL file. It can be defined either in config.xml or via command line arguments.
     * Setting a parameter via command line argument overwrites value from config.xml.
     *
     * @return a file defining a full path to CPL or null if it's not specified neither in config.xml nor via command line arguments
     */
    public File getCplFile() {
        File impDirectory = getImpDirectoryFile();
        if (impDirectory == null) {
            return null;
        }
        // cmd line argument has first priority
        String cpl = cmdLineArgs.getCpl() != null ? cmdLineArgs.getCpl() : defaultCpl;
        if (cpl == null) {
            return null;
        }
        return new File(impDirectory, cpl);
    }

    /**
     * Gets a full path to IMP directory. It can be defined either in config.xml or via command line arguments.
     * Setting a parameter via command line argument overwrites value from config.xml.
     *
     * @return a file defining a full path to IMP directory or null if it's not specified neither in config.xml nor via command line arguments
     */
    public File getImpDirectoryFile() {
        // cmd line argument has first priority
        String impDir = cmdLineArgs.getImp() != null ? cmdLineArgs.getImp() : defaultImp;
        if (impDir == null) {
            return null;
        }
        return new File(impDir);
    }

    /**
     * Gets a full path to working directory. It can be defined either in config.xml or via command line arguments.
     * Setting a parameter via command line argument overwrites value from config.xml.
     *
     * @return a file defining a full path to working directory or null if it's not specified neither in config.xml nor via command line arguments
     */
    public File getWorkingDirFile() {
        // cmd line argument has first priority
        String workingDir = cmdLineArgs.getWorkingDirectory() != null ? cmdLineArgs.getWorkingDirectory() : defaultWorkingDir;
        if (workingDir == null) {
            return null;
        }
        return new File(workingDir);
    }

    /**
     * Sets a default value for IMP directory (usually a value from config.xml).
     *
     * @param defaultImp a default value for IMP directory (usually a value from config.xml).
     */
    public void setDefaultImp(String defaultImp) {
        this.defaultImp = defaultImp;
    }

    /**
     * Sets a default value for CPL file (usually a value from config.xml).
     *
     * @param defaultCpl a default value for CPL file (usually a value from config.xml).
     */
    public void setDefaultCpl(String defaultCpl) {
        this.defaultCpl = defaultCpl;
    }

    /**
     * Sets a default value for working directory (usually a value from config.xml).
     *
     * @param defaultWorkingDir a default value for working directory (usually a value from config.xml).
     */
    public void setDefaultWorkingDir(String defaultWorkingDir) {
        this.defaultWorkingDir = defaultWorkingDir;
    }
}
