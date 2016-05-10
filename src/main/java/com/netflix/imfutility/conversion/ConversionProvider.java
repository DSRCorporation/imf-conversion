package com.netflix.imfutility.conversion;

import com.netflix.imfutility.Format;
import com.netflix.imfutility.xml.AbstractXmlProvider;
import com.netflix.imfutility.xsd.conversion.ConversionType;
import com.netflix.imfutility.xsd.conversion.FormatType;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Conversion.xml parser.
 * <ul>
 * <li>Maps conversion.xml to a Java model</li>
 * <li>Performs XSD validation and throws {@link RuntimeException} if conversion.xml is not a valid XML according to conversion.xsd</li>
 * </ul>
 */
public class ConversionProvider extends AbstractXmlProvider {

    private static final String XSD_CONVERSION_XSD = "xsd/conversion.xsd";
    private static final String CONVERSION_PACKAGE = "com.netflix.imfutility.xsd.conversion";

    private FormatType formatType;


    public ConversionProvider(String configXml, Format format) throws JAXBException, SAXException {
        super(configXml, CONVERSION_PACKAGE, XSD_CONVERSION_XSD);
        @SuppressWarnings("unchecked") ConversionType conversion = ((JAXBElement<ConversionType>) unmarshalResult).getValue();
        this.formatType = conversion.getFormats().getMap().get(format.getName());
    }

    public FormatType getFormat() {
        return formatType;
    }

    public List<String> getConvertConfiguration() {
        if (formatType == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(formatType.getFormatConfigurations().getMap().keySet());
    }


}
