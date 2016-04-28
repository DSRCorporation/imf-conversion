package com.netflix.imfutility;

import com.netflix.imfutility.xsd.config.ConfigType;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

/**
 * Created by Alexander on 4/27/2016.
 */
public class ConfigProvider extends AbstractXmlProvider {

    private ConfigType config;

    public ConfigProvider(String configXml) throws JAXBException, SAXException {
        super(configXml, "com.netflix.imfutility.xsd.config", "xsd/config.xsd");
        this.config = ((JAXBElement<ConfigType>) unmarshalResult).getValue();
    }

    public ConfigType getConfig() {
        return config;
    }

}
