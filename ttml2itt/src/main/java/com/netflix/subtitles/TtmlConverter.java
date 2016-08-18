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
package com.netflix.subtitles;

import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import static com.netflix.subtitles.TtmlConverterConstants.TTML_PACKAGES;
import static com.netflix.subtitles.TtmlConverterConstants.TTML_SCHEMA;
import com.netflix.subtitles.cli.TtmlConverterCmdLineParams;
import com.netflix.subtitles.cli.TtmlConverterCmdLineParser;
import com.netflix.subtitles.cli.TtmlOption;
import com.netflix.subtitles.exception.ParseException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import org.w3.ns.ttml.TtEltype;

/**
 * Validates TTML against iTT specification and converts to iTT format in simple cases.
 */
public final class TtmlConverter {

    private final List<TtEltype> ttmlTts;
    private final File outputFile;
//    private List<TtEltype> convertedItts;
//    private TtEltype mergedItt;

    /**
     * Entry point.
     *
     * @param args cmd line args
     */
    public static void main(String[] args) {
        TtmlConverterCmdLineParams parsedParams = null;
        TtmlConverter converter = null;

        try {
            parsedParams = new TtmlConverterCmdLineParser().parse(args);
        } catch (Exception e) {
            System.err.println(String.format("Parsing of command line arguments failed. Reason: %s",
                    e.getLocalizedMessage()));
            System.exit(-1);
        }
        if (parsedParams == null) { //help
            System.exit(0);
        }

        printStartMessage(parsedParams);

        try {
            converter = new TtmlConverter(parsedParams);
        } catch (Exception e) {
            System.err.println(String.format("Input file/s is not valid. %s", e.getLocalizedMessage()));
            System.exit(-1);
        }

        try {
            converter.convertInputsToItt();
        } catch (Exception e) {
            System.err.println(String.format("Input file/s cannot be converted to itt. %s", e.getLocalizedMessage()));
            System.exit(-1);
        }

        try {
            converter.mergeInputs();
        } catch (Exception e) {
            System.err.println(String.format("Input file/s cannot be converted to itt. %s", e.getLocalizedMessage()));
            System.exit(-1);
        }

        try {
            converter.writeToFile();
        } catch (Exception e) {
            System.err.println(String.format("Output iTT file cannot be saved. %s", e.getLocalizedMessage()));
            System.exit(-1);
        }

        System.out.println("Conversion done.");
    }

    private static void printStartMessage(TtmlConverterCmdLineParams parsedParams) {
        String mergeMsg = "";
        String fileMsg = " file.";
        String startMsg;

        if (parsedParams.getTtmlOptions().size() > 1) {
            mergeMsg = "and merging";
            fileMsg = " files.";
        }

        startMsg = "Start converting " + mergeMsg + " of "
                + parsedParams.getTtmlOptions().stream()
                        .map(TtmlOption::getFileName).collect(Collectors.joining(", ", "[", "]")) + fileMsg;
        System.out.println(startMsg);
    }

    /**
     * Constructor.
     *
     * @param params parsed command line parameters
     *
     * @throws ParseException
     */
    public TtmlConverter(TtmlConverterCmdLineParams params) throws ParseException {
        outputFile = new File(params.getOutputFile());
        if (!outputFile.canWrite()) {
            throw new ParseException(String.format(
                    "Output file %s cannot be written. Please check access rights.", params.getOutputFile()));
        }

        ttmlTts = params.getTtmlOptions().stream().map((o) -> {
            TtEltype tt;
            try {
                tt = XmlParser.parse(new File(o.getFileName()), new String[]{TTML_SCHEMA}, TTML_PACKAGES, TtEltype.class);
            } catch (XmlParsingException | FileNotFoundException e) {
                throw new ParseException(e);
            }
            return tt;
        }).collect(Collectors.toList());
    }

    /**
     * Converts all TTML input documents to corresponding iTT.
     */
    public void convertInputsToItt() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void mergeInputs() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void writeToFile() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
