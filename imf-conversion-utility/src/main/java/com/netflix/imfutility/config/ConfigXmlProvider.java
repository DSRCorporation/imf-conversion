package com.netflix.imfutility.config;

import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;

import java.io.File;
import java.io.FileNotFoundException;

import static com.netflix.imfutility.Constants.CONFIG_PACKAGE;
import static com.netflix.imfutility.Constants.CONFIG_XSD;

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

        this.config = XmlParser.parse(configFile, CONFIG_XSD, CONFIG_PACKAGE, ConfigType.class);
    }

    /**
     * @return a Config instance corresponding to the input from config.xml
     */
    public ConfigType getConfig() {
        return config;
    }

}
