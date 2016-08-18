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

import com.netflix.imfutility.resources.ResourceHelper;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import static com.netflix.subtitles.TtmlConverterConstants.TTML_PACKAGES;
import static com.netflix.subtitles.TtmlConverterConstants.TTML_SCHEMA;
import static com.netflix.subtitles.TtmlConverterConstants.TTML_TO_ITT_TRANSFORMATION;
import static com.netflix.subtitles.TtmlConverterConstants.XSLT2_TRANSFORMER_IMPLEMENTATION;
import com.netflix.subtitles.cli.TtmlConverterCmdLineParams;
import com.netflix.subtitles.cli.TtmlConverterCmdLineParser;
import com.netflix.subtitles.cli.TtmlOption;
import com.netflix.subtitles.exception.ConvertException;
import com.netflix.subtitles.exception.ParseException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import org.w3.ns.ttml.ObjectFactory;
import org.w3.ns.ttml.TtEltype;

/**
 * Validates TTML against iTT specification and converts to iTT format in simple cases.
 */
public final class TtmlConverter {

    private final List<TtEltype> ttmlTts;
    private final File outputFile;
    private List<TtEltype> convertedItts;
    private TtEltype mergedItt;

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
            converter.mergeConvertedItts();
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

    private static JAXBContext createTtmlJaxbContext() throws JAXBException {
        return JAXBContext.newInstance(TTML_PACKAGES);
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
        if (!Files.isWritable(Paths.get((outputFile.getParent() == null) ? "." : outputFile.getParent()))) {
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
     *
     * @throws TransformerConfigurationException
     * @throws ConvertException
     */
    public void convertInputsToItt() throws TransformerConfigurationException {
        TransformerFactory tf = TransformerFactory.newInstance(XSLT2_TRANSFORMER_IMPLEMENTATION, null);
        InputStream transformationStream = ResourceHelper.getResourceInputStream(TTML_TO_ITT_TRANSFORMATION);
        if (transformationStream == null) {
            throw new ConvertException(String.format(
                    "TTML to iTT transformation file is absent: %s", TTML_TO_ITT_TRANSFORMATION));
        }
        Transformer transformer = tf.newTransformer(new StreamSource(transformationStream));

        convertedItts = ttmlTts.stream().map((tt) -> {
            TtEltype res;
            JAXBElement<TtEltype> ttJaxb = new ObjectFactory().createTt(tt);

            try {
                JAXBContext jaxbc = createTtmlJaxbContext();
                JAXBSource source = new JAXBSource(jaxbc, ttJaxb);
                JAXBResult result = new JAXBResult(jaxbc);

                // transform
                transformer.transform(source, result);

                res = (TtEltype) ((JAXBElement<TtEltype>) result.getResult()).getValue();
            } catch (JAXBException | TransformerException e) {
                throw new ConvertException(e);
            }

            return res;
        }).collect(Collectors.toCollection(ArrayList::new));

        mergedItt = convertedItts.get(0);
    }

    /**
     * Merges converted from TTML to iTTs documents.
     */
    public void mergeConvertedItts() {
        convertedItts.stream().skip(0).forEach((itt) -> {
            mergedItt.setId("test");
        });
    }

    /**
     * Writes resulting iTT document to output file.
     *
     * @throws JAXBException
     */
    public void writeToFile() throws JAXBException {
        if (mergedItt == null) {
            System.out.println("Resulting iTT is empty. No data to write.");
            return;
        }

        Marshaller jaxbMarshaller = createTtmlJaxbContext().createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        jaxbMarshaller.marshal(new ObjectFactory().createTt(mergedItt), outputFile);
    }
}
