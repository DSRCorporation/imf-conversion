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
import com.netflix.imfutility.xml.XmlParsingException;
import stl.DefaultTtiStrategy;
import stl.StlBuilder;
import stl.bbc.BbcGsiStrategy;
import ttml.FatalParsingException;
import ttml.FormatTTML;
import ttml.TimedTextFileFormat;
import ttml.TimedTextObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class to parametrize and perform the conversion.
 */
public class Convert {


    /**
     * Convert TTML format to STL.
     *
     * @param args see command line help.
     * @return conversion result
     */

    public static Boolean convertTTML(String[] args) {

        //Parse input parameters
        CmdParametersHelper cmdHelper = new CmdParametersHelper();
        if (!cmdHelper.parseCmdOptions(args)) {
            //parsing error occured
            return false;
        }

        try {
            TimedTextFileFormat ttff = new FormatTTML();
            TimedTextObject tto = null;
            //Parse all TTML files
            for (TtmlInDescriptor ttmlInDescriptor : cmdHelper.getTtmlInDescriptors()) {
                System.out.println("Processing input TTML: " + ttmlInDescriptor.getFile());
                File file = new File(ttmlInDescriptor.getFile());
                InputStream is = new FileInputStream(file);
                tto = ttff.parseFile(file.getName(), is, ttmlInDescriptor.getStartTC(),
                        ttmlInDescriptor.getEndTC(), ttmlInDescriptor.getOffsetTC());
            }

            //Generate output
            if (tto != null) {
                if (cmdHelper.doOuputTTML()) {
                    String outputTTMLFile = cmdHelper.getOutputTTMLFile();
                    System.out.println("Generating output TTML: " + outputTTMLFile);
                    IOClass.writeFileTxt(outputTTMLFile, new FormatTTML().toFile(tto));
                }

                if (cmdHelper.doOutputSTL()) {
                    String outputSTLFile = cmdHelper.getOutputSTLFile();
                    System.out.println("Generating output STL: " + outputSTLFile);
                    try (OutputStream output = new BufferedOutputStream(new FileOutputStream(outputSTLFile))) {
                        byte[] stl = new StlBuilder()
                                .build(tto, new BbcGsiStrategy(cmdHelper.getMetadataXml()), new DefaultTtiStrategy());
                        output.write(stl);
                    }
                }

                return true;
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (FatalParsingException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (XmlParsingException e) {
            System.err.println(e.getMessage());
        }

        return false;
    }

    /**
     * Entry point.
     *
     * @param args
     */
    public static void main(String[] args) {
        if (!convertTTML(args)) {
            System.exit(1);
        }
    }
}
