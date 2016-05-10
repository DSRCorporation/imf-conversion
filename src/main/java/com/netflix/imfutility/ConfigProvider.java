package com.netflix.imfutility;

import com.netflix.imfutility.xml.AbstractXmlProvider;
import com.netflix.imfutility.xsd.config.ConfigType;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

/**
 * Config.xml parser.
 * <ul>
 * <li>Maps config.xml to a Java model</li>
 * <li>Performs XSD validation and throws {@link RuntimeException} if config.xml is not a valid XML according to config.xsd</li>
 * </ul>
 */
public class ConfigProvider extends AbstractXmlProvider {

    private static final String XSD_CONFIG_XSD = "xsd/config.xsd";
    private static final String CONFIG_PACKAGE = "com.netflix.imfutility.xsd.config";

    private ConfigType config;

    public ConfigProvider(String configXml) throws JAXBException, SAXException {
        super(configXml, CONFIG_PACKAGE, XSD_CONFIG_XSD);
        //noinspection unchecked
        this.config = ((JAXBElement<ConfigType>) unmarshalResult).getValue();
    }

    public ConfigType getConfig() {
        return config;
    }

}
