package com.netflix.imfutility.util;

import java.io.InputStream;

/**
 * Test utility for config.xml.
 */
public final class ConfigUtils {

    private ConfigUtils() {
    }

    public static InputStream getCorrectConfigXml() {
        return ConfigUtils.class.getClassLoader().getResourceAsStream(getCorrectConfigXmlPath());
    }

    public static String getCorrectConfigXmlPath() {
        return "xml/test-config.xml";
    }

    public static InputStream getBrokenXmlConfigXml() {
        return ConfigUtils.class.getClassLoader().getResourceAsStream(getBrokenXmlConfigXmlPath());
    }

    public static String getBrokenXmlConfigXmlPath() {
        return "xml/invalid/broken-xml-config.xml";
    }

    public static InputStream getInvalidXsdConfigXml() {
        return ConfigUtils.class.getClassLoader().getResourceAsStream(getInvalidXsdConfigXmlPath());
    }

    public static String getInvalidXsdConfigXmlPath() {
        return "xml/invalid/invalid-xsd-config.xml";
    }

}
