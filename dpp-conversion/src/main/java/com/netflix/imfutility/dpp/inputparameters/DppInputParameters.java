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
package com.netflix.imfutility.dpp.inputparameters;

import com.netflix.imfutility.dpp.DppConversionConstants;
import com.netflix.imfutility.inputparameters.ImfUtilityInputParameters;
import com.netflix.imfutility.resources.ResourceHelper;

import java.io.File;
import java.io.InputStream;

/**
 * A wrapper on command line arguments with helper methods to get input parameters obtained from the command line.
 */
public class DppInputParameters extends ImfUtilityInputParameters {

    private final DppCmdLineArgs cmdLineArgs;
    private final IDppDefaultTools defaultTools;
    private String customTtmlToStlTool;

    public DppInputParameters(DppCmdLineArgs cmdLineArgs, IDppDefaultTools defaultTools) {
        super(cmdLineArgs, defaultTools);
        this.defaultTools = defaultTools;
        this.cmdLineArgs = cmdLineArgs;
    }

    @Override
    public InputStream getDefaultConversionXml() {
        return ResourceHelper.getResourceInputStream(DppConversionConstants.CONVERSION_XML);
    }

    @Override
    public String getDefaultConversionXmlPath() {
        return DppConversionConstants.CONVERSION_XML;
    }

    /**
     * Gets a metadata file as specified via command line arguments or null if it's not specified.
     *
     * @return a metadata file as specified via command line arguments or null if it's not specified.
     */
    public File getMetadataFile() {
        if (cmdLineArgs.getMetadata() == null) {
            return null;
        }
        return new File(cmdLineArgs.getMetadata());
    }

    /**
     * Gets an audiomap file as specified via command line arguments or null if it's not specified.
     *
     * @return an audiomap file as specified via command line arguments or null if it's not specified.
     */
    public File getAudiomapFile() {
        if (cmdLineArgs.getAudioMap() == null) {
            return null;
        }
        return new File(cmdLineArgs.getAudioMap());
    }

    /**
     * Gets a custom name for the output.mxf file (without .mxf extension).
     *
     * @return a custom name for the output .mxf file (without .mxf extension).
     */
    public String getOutputName() {
        return cmdLineArgs.getOutput();
    }

    @Override
    public DppCmdLineArgs getCmdLineArgs() {
        return cmdLineArgs;
    }

    /**
     * Sets a custom ttml to stl subtitle conversion tool executable (usually a value from config.xml).
     *
     * @param customTtmlToStlTool a custom ttml to stl subtitle conversion tool executable (usually a value from config.xml).
     */
    public void setCustomTtmlToStlTool(String customTtmlToStlTool) {
        this.customTtmlToStlTool = customTtmlToStlTool;
    }

    /**
     * Gets an ttml to stl subtitle conversion tool executable. Usually a default value is used (distributed with the utility),
     * but it can be overridden in config.xml.
     *
     * @return ttml to stl subtitle conversion tool executable
     */
    public String getTtmlToStlTool() {
        // custom has first priority
        if (customTtmlToStlTool != null) {
            return customTtmlToStlTool;
        }
        return defaultTools.getTtmlToStlTool();
    }
}
