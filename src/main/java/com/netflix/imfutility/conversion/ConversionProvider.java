package com.netflix.imfutility.conversion;

import com.netflix.imfutility.AbstractXmlProvider;
import com.netflix.imfutility.Format;
import com.netflix.imfutility.xsd.conversion.ConversionType;
import com.netflix.imfutility.xsd.conversion.FormatType;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alexander on 4/26/2016.
 */
public class ConversionProvider extends AbstractXmlProvider {

    private FormatType formatType;


    public ConversionProvider(String configXml, Format format) throws JAXBException, SAXException {
        super(configXml, "com.netflix.imfutility.xsd.conversion", "xsd/conversion.xsd");
        ConversionType conversion = ((JAXBElement<ConversionType>) unmarshalResult).getValue();
        this.formatType = conversion.getFormats().getMap().get(format.getName());
    }

    public FormatType getFormat() {
        return formatType;
    }

    public List<String> getConvertConfiguration(Format format) {
        if (formatType == null) {
            return Collections.EMPTY_LIST;
        }
        return new ArrayList<>(formatType.getFormatConfigurations().getMap().keySet());
    }


}
