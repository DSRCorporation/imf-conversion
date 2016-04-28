package com.netflix.imfutility.conversion;

import com.netflix.imfutility.Format;
import com.netflix.imfutility.xsd.conversion.ConversionType;
import com.netflix.imfutility.xsd.conversion.FormatType;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alexander on 4/26/2016.
 */
public class ConversionProvider {

    private FormatType formatType;

    public ConversionProvider(String conversionXml, Format format) throws JAXBException, SAXException {
        ConversionType conversion = getConversionModel(conversionXml);
        this.formatType = conversion.getFormats().getMap().get(format.getName());
    }

    private ConversionType getConversionModel(String conversionXml) throws JAXBException, SAXException {
        JAXBContext jaxbContext = JAXBContext.newInstance("com.netflix.imfutility.xsd.conversion");
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        String configXsd = ClassLoader.getSystemClassLoader().getResource("xsd/conversion.xsd").getPath();
        Schema schema = sf.newSchema(new File(configXsd));
        unmarshaller.setSchema(schema);
        unmarshaller.setEventHandler(new MyValidationEventHandler());

        JAXBElement<ConversionType> conversionElement =
                (JAXBElement<ConversionType>) unmarshaller.unmarshal(new File(conversionXml));
        return conversionElement.getValue();
    }

    public FormatType getFormat() {
        return formatType;
    }

    public List<String> getConvertConfiguration(Format format) {
        if (formatType == null) {
            return Collections.EMPTY_LIST;
        }
        return new ArrayList(formatType.getFormatConfigurations().getMap().keySet());
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
