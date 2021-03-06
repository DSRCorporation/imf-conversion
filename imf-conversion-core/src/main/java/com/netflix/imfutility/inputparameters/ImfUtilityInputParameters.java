/**
 * Copyright (C) 2016 Netflix, Inc.
 *
 *     This file is part of IMF Conversion Utility.
 *
 *     IMF Conversion Utility is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     IMF Conversion Utility is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with IMF Conversion Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.netflix.imfutility.inputparameters;

import java.io.File;
import java.io.InputStream;

/**
 * A wrapper on command line arguments with helper methods to get input parameters obtained from the command line.
 * Some of the parameters (such as CPL, IMP, working directory paths) can be defined either in config.xml or via command line arguments.
 * Setting a parameter via command line argument overwrites value from config.xml.
 */
public abstract class ImfUtilityInputParameters {

    private final ImfUtilityCmdLineArgs cmdLineArgs;
    private final IDefaultTools defaultTools;
    private String defaultImp;
    private String defaultCpl;
    private String defaultWorkingDir;
    private String customValidationTool;

    public ImfUtilityInputParameters(ImfUtilityCmdLineArgs cmdLineArgs, IDefaultTools defaultTools) {
        this.cmdLineArgs = cmdLineArgs;
        this.defaultTools = defaultTools;
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
        // cmd line argument has first priority
        String cpl = cmdLineArgs.getCpl() != null ? cmdLineArgs.getCpl() : defaultCpl;
        if (cpl == null) {
            return null;
        }
        // try if a valid path for CPL is specified.
        File cplFile = new File(cpl);
        if (cplFile.isFile()) {
            return cplFile;
        }
        // assume that the CPL is relative to IMP.
        File impDirectory = getImpDirectoryFile();
        if (impDirectory == null) {
            return cplFile;
        }
        return new File(impDirectory, cpl);
    }

    /**
     * Gets a full path to IMP directory. It can be defined either in config.xml or via command line arguments.
     * Setting a parameter via command line argument overwrites value from config.xml.
     *
     * @return a file defining a full path to IMP directory or null if it's not specified neither in config.xml nor via
     * command line arguments
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
     * @return a file defining a full path to working directory or null if it's not specified neither in config.xml
     * nor via command line arguments
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
     * A default conversion XML.
     *
     * @return a default conversion.xml
     */
    public abstract InputStream getDefaultConversionXml();

    /**
     * A default conversion XML path.
     *
     * @return a default conversion.xml path.
     */
    public abstract String getDefaultConversionXmlPath();


    /**
     * Gets an IMF validation tool executable. Usually a default value is used (distributed with the utility),
     * but it can be overridden in config.xml.
     *
     * @return IMF validation executable
     */
    public String getImfValidationTool() {
        // custom has first priority
        if (customValidationTool != null) {
            return customValidationTool;
        }
        return defaultTools.getImfValidationTool();
    }

    /**
     * Sets a custom validation tool executable (usually a value from config.xml).
     *
     * @param customValidationTool a custom validation tool executable (usually a value from config.xml).
     */
    public void setCustomImfValidationTool(String customValidationTool) {
        this.customValidationTool = customValidationTool;
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
