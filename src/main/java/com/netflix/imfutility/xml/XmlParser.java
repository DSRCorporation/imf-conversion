package com.netflix.imfutility.xml;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

/**
 * A base .xml parser.
 * <ul>
 * <li>Maps XML to a Java model</li>
 * <li>Performs XSD validation and throws {@link XmlParsingException} if the xml is not a valid XML according to XSD.</li>
 * </ul>
 */
public class XmlParser {

    public static String getNamespace(File xml) throws XmlParsingException {
        // 1. create an error and content handler
        XmlParsingNamespaceHandler contentErrorHandler = new XmlParsingNamespaceHandler(xml);

        // 2. do parse
        doParse(xml, contentErrorHandler);

        return contentErrorHandler.getNamespace();
    }

    public static <T> T parse(File xml, String xsd, String pkg, Class<T> resultClass) throws XmlParsingException {
        try {
            // 1. create JAXB unmarshaller
            JAXBContext jaxbContext = JAXBContext.newInstance(pkg);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            UnmarshallerHandler unmarshallerHandler = unmarshaller.getUnmarshallerHandler();

            // 2. create a an error and content handler (which is also a bridge between a sax parser and unmarshaller)
            XmlParsingHandler contentErrorHandler = new XmlParsingHandlerWrapper(unmarshallerHandler, xml);

            // 3. do parse
            doParse(xml, xsd, contentErrorHandler);

            // 4. get unmarshall result
            Object result = JAXBIntrospector.getValue(unmarshallerHandler.getResult());
            if (!resultClass.isInstance(result)) {
                throw new RuntimeException(String.format("A root element in '%s' must be an instance of %s type.",
                        xml.getAbsoluteFile(), resultClass.getSimpleName()));

            }
            return (T) result;
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private static void doParse(File xml, XmlParsingHandler parsingHandler) throws XmlParsingException {
        doParse(xml, null, parsingHandler);
    }

    private static void doParse(File xml, String xsd, XmlParsingHandler parsingHandler) throws XmlParsingException {
        try (FileReader fileReader = new FileReader(xml)) {
            // 1. get schema
            Schema schema = null;
            if (xsd != null) {
                schema = getSchema(xsd);
            }

            // 2. create a SAX parser a assign the error handler
            XMLReader xr = getXmlReader(schema);
            xr.setErrorHandler(parsingHandler);
            xr.setContentHandler(parsingHandler);

            // 3. parse XML
            xr.parse(new InputSource(fileReader));

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

    private static Schema getSchema(String xsd) throws SAXException {
        URL xsdResource = ClassLoader.getSystemClassLoader().getResource(xsd);
        if (xsdResource == null) {
            throw new RuntimeException(String.format("'%s' schema not found.", xsd));
        }
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        return sf.newSchema(new File(xsdResource.getPath()));
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

}
