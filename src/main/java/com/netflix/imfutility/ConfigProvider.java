package com.netflix.imfutility;

import com.netflix.imfutility.Format;
import com.netflix.imfutility.xsd.config.IMFUtilityConfigType;
import com.netflix.imfutility.xsd.conversion.IMFUtilityConversionType;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;

/**
 * Created by Alexander on 4/27/2016.
 */
public class ConfigProvider {

    private IMFUtilityConfigType config;

    public ConfigProvider(String configXml) throws JAXBException, SAXException {
        JAXBContext jaxbContext = JAXBContext.newInstance("com.netflix.imfutility.xsd.config");
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(new File("G:\\Netflix\\dev\\imf-utility\\src\\main\\xsd\\config.xsd"));
        unmarshaller.setSchema(schema);
        unmarshaller.setEventHandler(new MyValidationEventHandler());

        JAXBElement<IMFUtilityConfigType> configElement =
                (JAXBElement<IMFUtilityConfigType>) unmarshaller.unmarshal(new File(configXml));
        this.config = configElement.getValue();
    }

    public IMFUtilityConfigType getConfig() {
        return config;
    }

    private static class MyValidationEventHandler implements ValidationEventHandler {

        public boolean handleEvent(ValidationEvent event) {
            System.out.println("\nEVENT");
            System.out.println("SEVERITY:  " + event.getSeverity());
            System.out.println("MESSAGE:  " + event.getMessage());
            System.out.println("LINKED EXCEPTION:  " + event.getLinkedException());
            System.out.println("LOCATOR");
            System.out.println("    LINE NUMBER:  " + event.getLocator().getLineNumber());
            System.out.println("    COLUMN NUMBER:  " + event.getLocator().getColumnNumber());
            System.out.println("    OFFSET:  " + event.getLocator().getOffset());
            System.out.println("    OBJECT:  " + event.getLocator().getObject());
            System.out.println("    NODE:  " + event.getLocator().getNode());
            System.out.println("    URL:  " + event.getLocator().getURL());
            return true;
        }

    }
}
