package com.netflix.imfutility;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.net.URL;

/**
 * Created by Alexander on 4/28/2016.
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
//            System.out.println("\nEVENT");
//            System.out.println("SEVERITY:  " + event.getSeverity());
//            System.out.println("MESSAGE:  " + event.getMessage());
//            System.out.println("LINKED EXCEPTION:  " + event.getLinkedException());
//            System.out.println("LOCATOR");
//            System.out.println("    LINE NUMBER:  " + event.getLocator().getLineNumber());
//            System.out.println("    COLUMN NUMBER:  " + event.getLocator().getColumnNumber());
//            System.out.println("    OFFSET:  " + event.getLocator().getOffset());
//            System.out.println("    OBJECT:  " + event.getLocator().getObject());
//            System.out.println("    NODE:  " + event.getLocator().getNode());
//            System.out.println("    URL:  " + event.getLocator().getURL());
//            return true;
        }

    }

}
