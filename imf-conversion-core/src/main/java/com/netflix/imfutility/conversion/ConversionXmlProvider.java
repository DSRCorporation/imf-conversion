/**
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
package com.netflix.imfutility.conversion;

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.IFormat;
import com.netflix.imfutility.generated.conversion.ConversionType;
import com.netflix.imfutility.generated.conversion.FormatConfigurationType;
import com.netflix.imfutility.generated.conversion.FormatType;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.netflix.imfutility.CoreConstants.CONVERSION_PACKAGE;
import static com.netflix.imfutility.CoreConstants.CONVERSION_XSD;

/**
 * Conversion.xml parser.
 * <ul>
 * <li>Maps conversion.xml to a Java model</li>
 * <li>Performs XSD validation and throws {@link XmlParsingException} if conversion.xml is not a valid XML according to conversion.xsd</li>
 * </ul>
 */
public class ConversionXmlProvider {

    private FormatType formatType;
    private ConversionType conversion;

    /**
     * Parses the given conversion.xml file to a Java model. Performs XSD validation.
     *
     * @param conversionXml a full path to the input conversion.xml
     * @param format        a destination format within conversion.xml
     * @throws XmlParsingException   if the input is not a valid XML or it doesn't pass XSD validation
     * @throws FileNotFoundException if the input path doesn't define a file.
     */
    public ConversionXmlProvider(String conversionXml, IFormat format) throws XmlParsingException, FileNotFoundException {
        this(new FileInputStream(new File(conversionXml)), new File(conversionXml).getAbsolutePath(), format);
    }

    /**
     * Parses the given conversion.xml file to a Java model. Performs XSD validation.
     *
     * @param conversionXml     an input conversion.xml content
     * @param conversionXmlPath a path to the input conversion.xml
     * @param format            a destination format within conversion.xml
     * @throws XmlParsingException if the input is not a valid XML or it doesn't pass XSD validation
     */
    public ConversionXmlProvider(InputStream conversionXml, String conversionXmlPath, IFormat format) throws XmlParsingException {
        this.conversion = XmlParser.parse(conversionXml, conversionXmlPath, new String[]{CONVERSION_XSD}, CONVERSION_PACKAGE, ConversionType.class);
        this.formatType = conversion.getFormats().getMap().get(format.getName());

        if (this.formatType == null) {
            throw new ConversionException(String.format("'%s' doesn't contain configuration for '%s' format.",
                    conversionXmlPath, format.getName()));
        }
    }

    /**
     * @return a root conversion instance corresponding to the input conversion.xml
     */
    public ConversionType getConversion() {
        return conversion;
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
