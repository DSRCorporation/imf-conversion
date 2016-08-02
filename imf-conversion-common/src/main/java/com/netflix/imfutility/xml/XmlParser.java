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
package com.netflix.imfutility.xml;

import com.netflix.imfutility.resources.ResourceHelper;
import com.netflix.imfutility.resources.ResourceResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A base .xml parser.
 * <ul>
 * <li>Maps XML to a Java model</li>
 * <li>Performs XSD validation and throws {@link XmlParsingException} if the xml is not a valid XML according to XSD.</li>
 * </ul>
 */
public final class XmlParser {

    /**
     * Gets the namespace of the root element.
     *
     * @param xml a full path tp the input XML
     * @return a namespace URI string or null if the XML doesn't contain a namespace.
     * @throws XmlParsingException, FileNotFoundException if input is not a valid XML or it doesn't pass XSD validation
     */
    public static String getNamespace(File xml) throws XmlParsingException, FileNotFoundException {
        // 1. create an error and content handler
        XmlParsingNamespaceHandler contentErrorHandler = new XmlParsingNamespaceHandler(xml.getAbsolutePath());

        // 2. do parse
        doParse(xml, contentErrorHandler);

        return contentErrorHandler.getNamespace();
    }

    /**
     * Parses the given XML to a Java model (JAXB unmarshalling).
     * Performs XSD validation.
     *
     * @param xml         a full path to the input XML
     * @param xsds        a full path to all XSDs corresponding to the given XML
     * @param pkg         a package containing the corresponding Java model classes.
     * @param resultClass a corresponding Java model class.
     * @param <T>         a corresponding Java model class.
     * @return a corresponding Java model instance.
     * @throws XmlParsingException, FileNotFoundException if input is not a valid XML or it doesn't pass XSD validation
     */
    public static <T> T parse(File xml, String[] xsds, String pkg, Class<T> resultClass) throws XmlParsingException, FileNotFoundException {
        return parse(new FileInputStream(xml), xml.getAbsolutePath(), xsds, pkg, resultClass);
    }

    /**
     * Parses the given XML to a Java model (JAXB unmarshalling).
     * Performs XSD validation.
     *
     * @param xml         an input XML content
     * @param xmlPath     a path to the input xml
     * @param xsds        a full path to all XSDs corresponding to the given XML
     * @param pkg         a package containing the corresponding Java model classes.
     * @param resultClass a corresponding Java model class.
     * @param <T>         a corresponding Java model class.
     * @return a corresponding Java model instance.
     * @throws XmlParsingException if input is not a valid XML or it doesn't pass XSD validation
     */
    public static <T> T parse(InputStream xml, String xmlPath, String[] xsds, String pkg, Class<T> resultClass) throws XmlParsingException {
        try {
            // 1. create JAXB unmarshaller
            JAXBContext jaxbContext = JAXBContext.newInstance(pkg);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            UnmarshallerHandler unmarshallerHandler = unmarshaller.getUnmarshallerHandler();

            // 2. create a an error and content handler (which is also a bridge between a sax parser and unmarshaller)
            XmlParsingHandler contentErrorHandler = new XmlParsingHandlerWrapper(unmarshallerHandler, xmlPath);

            // 3. do parse
            doParse(xml, xsds, contentErrorHandler);

            // 4. get unmarshall result
            Object result = JAXBIntrospector.getValue(unmarshallerHandler.getResult());
            if (!resultClass.isInstance(result)) {
                throw new RuntimeException(String.format("A root element in '%s' must be an instance of %s type.",
                        xmlPath, resultClass.getSimpleName()));

            }
            //noinspection unchecked
            return (T) result;
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private static void doParse(File xml, XmlParsingHandler parsingHandler) throws XmlParsingException, FileNotFoundException {
        doParse(new FileInputStream(xml), null, parsingHandler);
    }

    private static void doParse(InputStream xml, String[] xsds, XmlParsingHandler parsingHandler) throws XmlParsingException {
        try (InputStream xmlFile = new BufferedInputStream(xml)) {
            // 1. get schema
            Schema schema = null;
            if (xsds != null) {
                schema = getSchema(xsds);
            }

            // 2. create a SAX parser a assign the error handler
            XMLReader xr = getXmlReader(schema);
            xr.setErrorHandler(parsingHandler);
            xr.setContentHandler(parsingHandler);

            // 3. parse XML
            xr.parse(new InputSource(xmlFile));

            // 4. if there are errors during parsing - throw an exception
            if (parsingHandler.getParsingErrors().size() > 0) {
                throw new XmlParsingException(parsingHandler.getParsingErrors());
            }

        } catch (SAXException e) {
            if (parsingHandler != null && parsingHandler.getParsingErrors().size() > 0) {
                throw new XmlParsingException(e, parsingHandler.getParsingErrors());
            } else {
                throw new RuntimeException(e);
            }
        } catch (ParserConfigurationException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Schema getSchema(String[] xsds) throws SAXException {
        List<StreamSource> xsdSchemas = new ArrayList<>();
        for (String xsd : xsds) {
            InputStream xsdSchema = ResourceHelper.getResourceInputStream(xsd);
            xsdSchemas.add(new StreamSource(xsdSchema));
        }
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        ResourceResolver resolver = new ResourceResolver(getXsdsPaths(xsds));
        sf.setResourceResolver(resolver);
        return sf.newSchema(xsdSchemas.toArray(new StreamSource[]{}));
    }

    private static XMLReader getXmlReader(Schema schema) throws ParserConfigurationException, SAXException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        if (schema != null) {
            spf.setSchema(schema); // set XSD schema for validation
        }
        SAXParser sp = spf.newSAXParser();
        return sp.getXMLReader();
    }

    private static Collection<String> getXsdsPaths(String[] xsds) {
        return Arrays.stream(xsds).map(xsd -> xsd.substring(0, xsd.lastIndexOf("/"))).collect(Collectors.toList());
    }

    private XmlParser() {
    }
}
