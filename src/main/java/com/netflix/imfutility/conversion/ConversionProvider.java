package com.netflix.imfutility.conversion;

import com.netflix.imfutility.Format;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import com.netflix.imfutility.xsd.conversion.ConversionType;
import com.netflix.imfutility.xsd.conversion.FormatType;

import java.io.File;
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
public class ConversionProvider {

    private static final String XSD_CONVERSION_XSD = "xsd/conversion.xsd";
    private static final String CONVERSION_PACKAGE = "com.netflix.imfutility.xsd.conversion";

    private FormatType formatType;

    public ConversionProvider(String conversionXml, Format format) throws XmlParsingException {
        ConversionType conversion = XmlParser.parse(
                new File(conversionXml), XSD_CONVERSION_XSD, CONVERSION_PACKAGE, ConversionType.class);
        this.formatType = conversion.getFormats().getMap().get(format.getName());
        if (this.formatType == null) {
            throw new RuntimeException(String.format("'%s' doesn't contain configuration for '%s' format.",
                    conversionXml, format.getName()));

        }
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
