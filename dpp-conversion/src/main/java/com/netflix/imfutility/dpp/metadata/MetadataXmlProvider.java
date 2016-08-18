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
package com.netflix.imfutility.dpp.metadata;

import com.netflix.imfutility.generated.dpp.metadata.DppType;
import com.netflix.imfutility.generated.dpp.metadata.ObjectFactory;
import com.netflix.imfutility.resources.ResourceHelper;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.netflix.imfutility.dpp.DppConversionConstants.BMX_FRAMEWORK_PARAM;
import static com.netflix.imfutility.dpp.DppConversionConstants.BMX_PARAMETERS_TRANSFORMATION;
import static com.netflix.imfutility.dpp.DppConversionConstants.XSLT2_TRANSFORMER_IMPLEMENTATION;
import static com.netflix.imfutility.dpp.DppConversionXsdConstants.ISO_639_2_CODES_XML_SCHEME;
import static com.netflix.imfutility.dpp.DppConversionXsdConstants.METADATA_PACKAGE;
import static com.netflix.imfutility.dpp.DppConversionXsdConstants.METADATA_XML_SCHEME;
import static com.netflix.imfutility.dpp.DppConversionXsdConstants.TYPES_XML_SCHEME;

/**
 * Created by Alexandr on 4/28/2016.
 * Provides functionality read and transform metadata.xml into BMXLib parameters.
 */
public class MetadataXmlProvider {

    public static final String DEST_FRAME_RATE = "25 1";

    /**
     * MXF frameworks enumeration.
     */
    public enum DMFramework {
        UKDPP("UKDPP"),
        AS11CORE("AS11Core"),
        AS11Segmentation("AS11Segmentation");

        private final String value;

        DMFramework(String v) {
            value = v;
        }

        private String value() {
            return value;
        }
    }

    private final DppType dpp;
    private final File workingDir;
    private Map<DMFramework, File> bmxDppParameters = new HashMap<>();

    /**
     * Loads and validates metadata.xml.
     * Transforms metadata.xml into a set of parameter files for BMXLib tool.
     * The parameter files are created within the provided working directory.
     *
     * @param metadataFile a path to the metadata.xml file
     * @param workingDir   current working directory where parameter files are created.
     * @throws XmlParsingException   an exception in case of metadata.xml parsing error
     * @throws FileNotFoundException if the metadataXml doesn't define an existing file.
     */
    public MetadataXmlProvider(File metadataFile, File workingDir) throws XmlParsingException, FileNotFoundException {
        this.workingDir = workingDir;
        this.dpp = loadDpp(metadataFile);
    }

    /**
     * Gets the loaded DPP instances created from a provided metadata.xml.
     *
     * @return a loaded DPP instances created from a provided metadata.xml
     */
    public DppType getDpp() {
        return dpp;
    }

    /**
     * Transform metadata.xml into a set of parameter files for BMXLib tool.
     * The parameter files are created within the provided working directory.
     */
    public void createBmxDppParameterFiles() {
        JAXBSource source = dppToJaxbSource(dpp);

        bmxDppParameters = new HashMap<>();
        bmxDppParameters.put(DMFramework.UKDPP, createBmxFrameworkParameterFile(source, DMFramework.UKDPP, workingDir));
        bmxDppParameters.put(DMFramework.AS11CORE, createBmxFrameworkParameterFile(source, DMFramework.AS11CORE, workingDir));
        bmxDppParameters.put(DMFramework.AS11Segmentation, createBmxFrameworkParameterFile(
                source, DMFramework.AS11Segmentation, workingDir));
    }

    /**
     * Gets a parameter files for BMXLib tool for the given framework.
     * The parameter files are created within the provided working directory.
     *
     * @param framework a framework get a parameter file for.
     * @return a parameter file withing the current working directory.
     */
    public File getBmxDppParameterFile(DMFramework framework) {
        return bmxDppParameters.get(framework);
    }

    /**
     * Gets all parameter files for BMXLib tool for all frameworks (see {@link DMFramework}).
     * The parameter files are created within the provided working directory.
     *
     * @return a collection of all parameter files for BMXLib within the working directory.
     */
    public Collection<File> getBmxDppParameterFiles() {
        return bmxDppParameters.values();
    }

    private DppType loadDpp(File metadataFile) throws XmlParsingException, FileNotFoundException {
        if (!metadataFile.isFile()) {
            throw new FileNotFoundException(String.format("Invalid metadata.xml file: '%s' not found", metadataFile.getAbsolutePath()));
        }

        return XmlParser.parse(metadataFile,
                new String[]{TYPES_XML_SCHEME, ISO_639_2_CODES_XML_SCHEME, METADATA_XML_SCHEME},
                METADATA_PACKAGE, DppType.class);
    }

    /**
     * Transforms metadata.xml into a set of parameters for particular MXF framework.
     *
     * @param source    loaded and validated JAXBSource with metadata.xml
     * @param framework the framework for which the parameters must be transformed.
     * @return a temporary file to be used as BMXLib input parameter for particular framework.
     */
    private File createBmxFrameworkParameterFile(JAXBSource source, DMFramework framework, File workingDir) {
        // Create Transformer
        Transformer transformer;
        try {
            // Create Transformer
            TransformerFactory tf = TransformerFactory.newInstance(XSLT2_TRANSFORMER_IMPLEMENTATION, null);
            InputStream transformationStream = ResourceHelper.getResourceInputStream(BMX_PARAMETERS_TRANSFORMATION);
            if (transformationStream == null) {
                throw new FileNotFoundException(String.format(
                        "Metadata.xml to BMX transformation file is absent: %s", BMX_PARAMETERS_TRANSFORMATION));
            }
            StreamSource xslt = new StreamSource(transformationStream);
            transformer = tf.newTransformer(xslt);

            //Set framework
            transformer.setParameter(BMX_FRAMEWORK_PARAM, framework.value());

        } catch (TransformerException | IOException e) {
            throw new RuntimeException(e);
        }

        //Prepare a parameter file
        File result = new File(workingDir, framework.value + ".txt");

        // Transform
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(result), "UTF-8"))) {
            StreamResult streamResult = new StreamResult(writer);
            transformer.transform(source, streamResult);
            writer.flush();
            return result;
        } catch (TransformerException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JAXBSource dppToJaxbSource(DppType dpp) {
        try {
            JAXBElement<DppType> dppJaxb = new ObjectFactory().createDpp(dpp);
            return new JAXBSource(JAXBContext.newInstance(METADATA_PACKAGE), dppJaxb);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}

