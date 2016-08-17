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
    private final List<TtEltype> tts;
    private final TtmlConverterCmdLineParams parsedCliParams;

    /**
     * Constructor.
     *
     * @param params parsed command line parameters
     *
     * @throws ParseException
     */
    public TtmlConverter(TtmlConverterCmdLineParams params) throws ParseException {
        parsedCliParams = params;

        tts = params.getTtmlOptions().stream().map((o) -> {
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
     * Validates TTML document with iTT restrictions.
     */
    public void validate() {
    }

    /**
     * Entry point.
     *
     * @param args cmd line args
     */
    public static void main(String[] args) {
        TtmlConverterCmdLineParams parsedParams = null;

        try {
            parsedParams = new TtmlConverterCmdLineParser().parse(args);
        } catch (Exception e) {
            System.err.println("Parsing of command line arguments failed. Reason: " + e.getLocalizedMessage());
            System.exit(-1);
        }
        if (parsedParams == null) { //help
            System.exit(0);
        }

        try {
            new TtmlConverter(parsedParams).validate();
        } catch (Exception e) {
            System.err.println(String.format("File is not valid. %s", e.getLocalizedMessage()));
            System.exit(-1);
        }
    }
}
