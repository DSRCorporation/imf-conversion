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

package com.netflix.imfutility.ttmltostl.inputparameters;

import java.util.ArrayList;
import java.util.List;

/**
 * A class describing all possible command line parameters.
 *
 * Created by Alexander on 7/28/2016.
 */
public class CmdLineParameters {

    private Boolean doOuputTTML = false;
    private Boolean doOutputSTL = false;
    private String outputTTMLFile = null;
    private String outputSTLFile = null;
    private List<TtmlInDescriptor> ttmlInDescriptors = new ArrayList<>();
    private String metadataXml = null;

    /**
     * Whether to output as a single TTML file.
     *
     * @return whether to output as a single TTML file
     */
    public Boolean doOuputTTML() {
        return doOuputTTML;
    }

    /**
     * The output TTML file name.
     *
     * @return the output TTML file name
     */
    public String getOutputTTMLFile() {
        return outputTTMLFile;
    }

    /**
     * Whether to output as an STL file.
     *
     * @return whether to output as an STL file
     */
    public Boolean doOutputSTL() {
        return doOutputSTL;
    }

    /**
     * The output STL file name.
     *
     * @return the output STL file name
     */
    public String getOutputSTLFile() {
        return outputSTLFile;
    }

    /**
     * A list of Input TTML file parameters.
     * <ul>
     *     <li>file - The TTML file path;</li>
     *     <li>offsetTC - Offset timecode to shift captions of the TTML file;</li>
     *     <li>startTC - The TTML file path;</li>;</li>
     *     <li>endTC - End timecode to get captions of the TTML file.</li>
     * </ul>
     *
     * @return A list of Input TTML file parameters
     */
    public List<TtmlInDescriptor> getTtmlInDescriptors() {
        return ttmlInDescriptors;
    }

    /**
     * A path to DPP metadata.xml.
     *
     * @return a path to DPP metadata.xml
     */
    public String getMetadataXml() {
        return metadataXml;
    }

    void setDoOuputTTML(Boolean doOuputTTML) {
        this.doOuputTTML = doOuputTTML;
    }

    void setDoOutputSTL(Boolean doOutputSTL) {
        this.doOutputSTL = doOutputSTL;
    }

    void setMetadataXml(String metadataXml) {
        this.metadataXml = metadataXml;
    }

    void setOutputSTLFile(String outputSTLFile) {
        this.outputSTLFile = outputSTLFile;
    }

    void setOutputTTMLFile(String outputTTMLFile) {
        this.outputTTMLFile = outputTTMLFile;
    }
}
