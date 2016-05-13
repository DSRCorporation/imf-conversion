package com.netflix.imfutility.config;

import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import com.netflix.imfutility.xsd.config.ConfigType;

import java.io.File;

/**
 * Config.xml parser.
 * <ul>
 * <li>Maps config.xml to a Java model</li>
 * <li>Performs XSD validation and throws {@link RuntimeException} if config.xml is not a valid XML according to config.xsd</li>
 * </ul>
 */
public class ConfigProvider {

    private static final String XSD_CONFIG_XSD = "xsd/config.xsd";
    private static final String CONFIG_PACKAGE = "com.netflix.imfutility.xsd.config";

    private ConfigType config;

    public ConfigProvider(String configXml) throws XmlParsingException {
        this.config = new XmlParser().parse(
                new File(configXml), XSD_CONFIG_XSD, CONFIG_PACKAGE, ConfigType.class);
    }

    public ConfigType getConfig() {
        return config;
    }

}
