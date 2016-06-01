package com.netflix.imfutility.config;

import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import com.netflix.imfutility.xsd.config.ConfigType;

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

    public ConfigXmlProvider(String configXml) throws XmlParsingException, FileNotFoundException {
        File configFile = new File(configXml);
        if (!configFile.isFile()) {
            throw new FileNotFoundException(String.format("Invalid config file: '%s' not found", configFile.getAbsolutePath()));
        }

        this.config = XmlParser.parse(configFile, CONFIG_XSD, CONFIG_PACKAGE, ConfigType.class);
    }

    public ConfigType getConfig() {
        return config;
    }

}
