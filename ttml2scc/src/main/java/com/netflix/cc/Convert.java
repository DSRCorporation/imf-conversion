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

package com.netflix.cc;

import com.netflix.cc.cli.CmdLineParameters;
import com.netflix.cc.cli.CmdLineParametersParser;
import com.netflix.cc.cli.TtmlInDescriptor;
import com.netflix.cc.scc.SccBuilder;
import com.netflix.imfutility.ttmltostl.ttml.FatalParsingException;
import com.netflix.imfutility.ttmltostl.ttml.FormatTTML;
import com.netflix.imfutility.ttmltostl.ttml.TimedTextFileFormat;
import com.netflix.imfutility.ttmltostl.ttml.TimedTextObject;
import com.netflix.imfutility.ttmltostl.util.FileWriteHelper;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * The main class to parametrize and perform the conversion.
 */
public class Convert {


    /**
     * Convert TTML format to STL.
     *
     * @param args see command line help.
     * @return conversion result
     */

    public boolean convertTTML(String[] args) {

        try {
            // 1. Parse input parameters
            CmdLineParametersParser cmdLineParser = new CmdLineParametersParser();
            CmdLineParameters cmdLineParams = cmdLineParser.parseCmdOptions(args);
            if (cmdLineParams == null) {
                // -- help called
                return true;
            }

            // 2. Parse input TTML files
            TimedTextFileFormat ttff = new FormatTTML();
            TimedTextObject tto = null;
            for (TtmlInDescriptor ttmlInDescriptor : cmdLineParams.getTtmlInDescriptors()) {
                System.out.println("Processing input TTML: " + ttmlInDescriptor.getFile());
                File file = new File(ttmlInDescriptor.getFile());
                tto = ttff.parseFile(file, ttmlInDescriptor.getStartMS(),
                        ttmlInDescriptor.getEndMS(), ttmlInDescriptor.getOffsetMS());
            }

            // 3. Convert
            if (tto != null) {
                if (cmdLineParams.doOuputTTML()) {
                    String outputTTMLFile = cmdLineParams.getOutputTtmlFile();
                    System.out.println("Generating output TTML: " + outputTTMLFile);
                    FileWriteHelper.writeFileTxt(outputTTMLFile, new FormatTTML().toFile(tto));
                }

                if (cmdLineParams.doOutputScc()) {
                    String outputSccFile = cmdLineParams.getOutputSccFile();
                    System.out.println("Generating output SCC: " + outputSccFile);
                    String[] scc = new SccBuilder().build(tto);
                    FileWriteHelper.writeFileTxt(outputSccFile, scc);
                }

                return true;
            }
        } catch (ParseException exp) {
            System.err.println("Parsing of command line arguments failed.  Reason: " + exp.getMessage());
        } catch (FatalParsingException | IOException e) {
            System.err.println(e.getMessage());
        }

        return false;
    }

    /**
     * Entry point.
     *
     * @param args cmd line args
     */
    public static void main(String[] args) throws IOException, URISyntaxException {
        if (!new Convert().convertTTML(args)) {
            System.exit(1);
        }
    }
}
