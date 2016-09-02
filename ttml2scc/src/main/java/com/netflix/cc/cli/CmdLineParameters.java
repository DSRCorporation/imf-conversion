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

package com.netflix.cc.cli;

import java.util.ArrayList;
import java.util.List;

/**
 * A class describing all possible command line parameters.
 *
 * Created by Alexander on 7/28/2016.
 */
public class CmdLineParameters {

    private Boolean doOuputTtml = false;
    private Boolean doOutputScc = false;
    private String outputTtmlFile = null;
    private String outputSccFile = null;
    private final List<TtmlInDescriptor> ttmlInDescriptors = new ArrayList<>();

    /**
     * Whether to output as a single TTML file.
     *
     * @return whether to output as a single TTML file
     */
    public Boolean doOuputTTML() {
        return doOuputTtml;
    }

    /**
     * The output TTML file name.
     *
     * @return the output TTML file name
     */
    public String getOutputTtmlFile() {
        return outputTtmlFile;
    }

    /**
     * Whether to output as an SCC file.
     *
     * @return whether to output as an SCC file
     */
    public Boolean doOutputScc() {
        return doOutputScc;
    }

    /**
     * The output SCC file name.
     *
     * @return the output SCC file name
     */
    public String getOutputSccFile() {
        return outputSccFile;
    }

    /**
     * A list of Input TTML file parameters.
     * <ul>
     *     <li>file - The TTML file path;</li>
     *     <li>offsetTC - Offset timecode to shift captions of the TTML file;</li>
     *     <li>startTC - The TTML file path;</li>
     *     <li>endTC - End timecode to get captions of the TTML file.</li>
     * </ul>
     *
     * @return A list of Input TTML file parameters
     */
    public List<TtmlInDescriptor> getTtmlInDescriptors() {
        return ttmlInDescriptors;
    }

    void setDoOuputTtml(Boolean doOuputTtml) {
        this.doOuputTtml = doOuputTtml;
    }

    void setDoOutputScc(Boolean doOutputScc) {
        this.doOutputScc = doOutputScc;
    }

    void setOutputSccFile(String outputSccFile) {
        this.outputSccFile = outputSccFile;
    }

    void setOutputTtmlFile(String outputTtmlFile) {
        this.outputTtmlFile = outputTtmlFile;
    }
}
