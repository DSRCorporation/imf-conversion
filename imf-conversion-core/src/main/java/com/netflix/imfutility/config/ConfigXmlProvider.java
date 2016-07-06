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
package com.netflix.imfutility.config;

import com.netflix.imfutility.generated.config.ConfigType;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.netflix.imfutility.CoreConstants.CONFIG_PACKAGE;
import static com.netflix.imfutility.CoreConstants.CONFIG_XSD;

/**
 * Config.xml parser.
 * <ul>
 * <li>Maps config.xml to a Java model</li>
 * <li>Performs XSD validation and throws {@link XmlParsingException} if config.xml is not a valid XML according to config.xsd</li>
 * </ul>
 */
public class ConfigXmlProvider {

    private ConfigType config;

    /**
     * Parses the given config.xml file to a Java model. Performs XSD validation.
     *
     * @param configFile a full path to the input config.xml
     * @throws XmlParsingException   if the input is not a valid XML or it doesn't pass XSD validation
     * @throws FileNotFoundException if the input path doesn't define a file.
     */
    public ConfigXmlProvider(File configFile) throws XmlParsingException, FileNotFoundException {
        if (!configFile.isFile()) {
            throw new FileNotFoundException(String.format("Invalid config file: '%s' not found", configFile.getAbsolutePath()));
        }

        this.config = XmlParser.parse(configFile, new String[]{CONFIG_XSD}, CONFIG_PACKAGE, ConfigType.class);
    }

    /**
     * Parses the given config.xml file to a Java model. Performs XSD validation.
     *
     * @param configFile an input config.xml content
     * @param xmlPath    a path to the input config.xml
     * @throws XmlParsingException   if the input is not a valid XML or it doesn't pass XSD validation
     * @throws FileNotFoundException if the input path doesn't define a file.
     */
    public ConfigXmlProvider(InputStream configFile, String xmlPath) throws XmlParsingException {
        this.config = XmlParser.parse(configFile, xmlPath, new String[]{CONFIG_XSD}, CONFIG_PACKAGE, ConfigType.class);
    }

    /**
     * @return a Config instance corresponding to the input from config.xml
     */

    public ConfigType getConfig() {
        return config;
    }

}
