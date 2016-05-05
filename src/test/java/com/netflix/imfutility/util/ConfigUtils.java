package com.netflix.imfutility.util;

/**
 * Test utility for config.xml.
 */
public final class ConfigUtils {

    private ConfigUtils() {
    }

    public static String getCorrectConfigXml() {
        return ClassLoader.getSystemClassLoader().getResource("xml/test-config.xml").getPath();
    }

    public static String getBrokenXmlConfigXml() {
        return ClassLoader.getSystemClassLoader().getResource("xml/broken-xml-config.xml").getPath();
    }

    public static String getInvalidXsdConfigXml() {
        return ClassLoader.getSystemClassLoader().getResource("xml/invalid-xsd-config.xml").getPath();
    }

}
