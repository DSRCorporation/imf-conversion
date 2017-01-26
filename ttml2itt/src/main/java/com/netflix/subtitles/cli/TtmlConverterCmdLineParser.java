/*
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
package com.netflix.subtitles.cli;

import com.netflix.imfutility.exception.ConversionHelperException;
import com.netflix.imfutility.util.ConversionHelper;
import com.netflix.subtitles.exception.ParseException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.math3.fraction.BigFraction;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Defines and parses all command line parameters for converter.
 */
public final class TtmlConverterCmdLineParser {
    private final Options options = new Options();
    private Option help;
    private Option ttml;
    private Option out;
    private Option frameRate;

    public TtmlConverterCmdLineParser() {
        help = new Option("h", "help", false, "Print this message");

        ttml = Option.builder("t")
                .longOpt("ttml")
                .desc("Input TTML file parameters:"
                        + "\n<file> - The TTML file path."
                        + "\n<offsetMS> - Offset in milliseconds to shift captions of the TTML file."
                        + "\n<startMS> - Start time in milliseconds to get captions of the TTML file."
                        + "\n<endMS> - End time in milliseconds to get captions of the TTML file.")
                .numberOfArgs(4)
                .optionalArg(true)
                .argName("file> <offsetTC> <startTC> <endTC")
                .build();
        out = Option.builder("o")
                .longOpt("outputFile")
                .desc("Output iTT file")
                .hasArg()
                .argName("outputFile")
                .build();
        frameRate = Option.builder("f")
                .longOpt("frameRate")
                .desc("Frame rate of result iTT file")
                .hasArg()
                .argName("frameRate")
                .build();

        Stream.of(help, ttml, out, frameRate).forEach(options::addOption);
    }

    /**
     * Parses command line.
     *
     * @param args cli arguments
     * @return parsed parameters object
     * @throws ParseException
     */
    public TtmlConverterCmdLineParams parse(String[] args) throws ParseException {
        TtmlConverterCmdLineParams params = new TtmlConverterCmdLineParams();

        CommandLineParser parser = new DefaultParser();
        CommandLine line;
        try {
            line = parser.parse(options, args);
        } catch (org.apache.commons.cli.ParseException e) {
            throw new ParseException(e);
        }

        // help
        if (line.hasOption(help.getOpt())) {
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.setWidth(120);
            formatter.printHelp("ttml2stl", options);

            return null;
        }

        // outputFile
        if (line.hasOption(out.getOpt())) {
            params.setOutputFile(line.getOptionValue(out.getOpt()));
        } else {
            throw new ParseException("Output file option must be provided.");
        }

        // frameRate
        if (line.hasOption(frameRate.getOpt())) {
            params.setFrameRate(parseFrameRate(line.getOptionValue(frameRate.getOpt())));
        }

        // ttml
        params.getTtmlOptions().addAll(Stream.of(line.getOptions()).filter((o) -> o.equals(ttml)).map((o) -> {
            TtmlOption ttmlOption = new TtmlOption();

            try {
                ttmlOption.setFileName(o.getValue(0));
                ttmlOption.setOffsetMS(parseTtmlParameter(o, 1, "offsetMS"));
                ttmlOption.setStartMS(parseTtmlParameter(o, 2, "startMS"));
                ttmlOption.setEndMS(parseTtmlParameter(o, 3, "endMS"));
            } catch (IndexOutOfBoundsException e) {
                //It is error only if don't have file name
                //For required file it may not be thrown. We will check it later.
            }

            if (ttmlOption.getFileName() == null) {
                throw new ParseException("--ttml parameter must have at least <file> attribute defined.");
            }

            return ttmlOption;
        }).collect(Collectors.toCollection(ArrayList::new)));
        if (params.getTtmlOptions().isEmpty()) {
            throw new ParseException("At least one input TTML file must be provided.");
        }

        return params;
    }

    private int parseTtmlParameter(Option option, int optionIndex, String parameterName) {
        try {
            return Integer.parseInt(option.getValue(optionIndex));
        } catch (NumberFormatException e) {
            throw new ParseException(parameterName + " in --ttml must be an integer");
        }
    }

    private BigFraction parseFrameRate(String frameRate) {
        try {
            return ConversionHelper.parseEditRate(frameRate);
        } catch (ConversionHelperException e) {
            throw new ParseException("Value of -f(--frameRate) option must be correct frame rate in format numerator/[denominator])");
        }
    }
}
