package com.netflix.imfutility.xml;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.net.URL;

/**
 * A base .xml parser.
 * <ul>
 * <li>Maps XML to a Java model</li>
 * <li>Performs XSD validation and throws {@link RuntimeException} if config.xml is not a valid XML according to XSD.</li>
 * </ul>
 */
public abstract class AbstractXmlProvider {

    protected Object unmarshalResult;

    public AbstractXmlProvider(String xml, String pkg, String xsd) throws JAXBException, SAXException {
        JAXBContext jaxbContext = JAXBContext.newInstance(pkg);

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setSchema(getSchema(xsd));
        unmarshaller.setEventHandler(new MyValidationEventHandler());

        this.unmarshalResult = unmarshaller.unmarshal(new File(xml));
    }

    private Schema getSchema(String xsd) throws SAXException {
        URL xsdResource = ClassLoader.getSystemClassLoader().getResource(xsd);
        if (xsdResource == null) {
            throw new RuntimeException(String.format("'%s' schema not found.", xsd));
        }
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        return sf.newSchema(new File(xsdResource.getPath()));
    }

    protected static class MyValidationEventHandler implements ValidationEventHandler {

        public boolean handleEvent(ValidationEvent event) {
            throw new RuntimeException(event.getMessage());
        }

    }

}
