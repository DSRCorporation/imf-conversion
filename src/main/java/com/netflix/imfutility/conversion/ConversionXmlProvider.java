package com.netflix.imfutility.conversion;

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.Format;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import com.netflix.imfutility.xsd.conversion.ConversionType;
import com.netflix.imfutility.xsd.conversion.FormatConfigurationType;
import com.netflix.imfutility.xsd.conversion.FormatType;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.netflix.imfutility.Constants.CONVERSION_PACKAGE;
import static com.netflix.imfutility.Constants.CONVERSION_XSD;

/**
 * Conversion.xml parser.
 * <ul>
 * <li>Maps conversion.xml to a Java model</li>
 * <li>Performs XSD validation and throws {@link XmlParsingException} if conversion.xml is not a valid XML according to conversion.xsd</li>
 * </ul>
 */
public class ConversionXmlProvider {

    private FormatType formatType;

    /**
     * Parses the given conversion.xml file to a Java model. Performs XSD validation.
     *
     * @param conversionXml a full path to the input conversion.xml
     * @param format        a destination format within conversion.xml
     * @throws XmlParsingException   if the input is not a valid XML or it doesn't pass XSD validation
     * @throws FileNotFoundException if the input path doesn't define a file.
     */
    public ConversionXmlProvider(String conversionXml, Format format) throws XmlParsingException, FileNotFoundException {
        File conversionFile = new File(conversionXml);
        if (!conversionFile.isFile()) {
            throw new FileNotFoundException(String.format("Invalid conversion.xml file: '%s' not found", conversionFile.getAbsolutePath()));
        }

        ConversionType conversion = XmlParser.parse(conversionFile, CONVERSION_XSD, CONVERSION_PACKAGE, ConversionType.class);
        this.formatType = conversion.getFormats().getMap().get(format.getName());

        if (this.formatType == null) {
            throw new ConversionException(String.format("'%s' doesn't contain configuration for '%s' format.",
                    conversionFile.getAbsolutePath(), format.getName()));
        }
    }

    /**
     * @return a format instance corresponding to the input conversion.xml
     */
    public FormatType getFormat() {
        return formatType;
    }

    /**
     * @return a list of all conversion configuration for the given format ({@link #getFormat()}).
     */
    public List<String> getConvertConfiguration() {
        if (formatType == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(formatType.getFormatConfigurations().getMap().keySet());
    }

    /**
     * Gets a Format Configuration instance corresponding to the given format configuration name.
     *
     * @param configuration a format configuration name.
     * @return a format configuration instance.
     */
    public FormatConfigurationType getFormatConfigurationType(String configuration) {
        FormatConfigurationType formatConfigurationType = formatType.getFormatConfigurations().getMap().get(configuration);
        if (formatConfigurationType == null) {
            throw new ConversionException(String.format("No configuration '%s' found for format '%s'.", configuration, formatType.getName()));
        }
        return formatConfigurationType;
    }


}
