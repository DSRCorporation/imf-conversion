package com.netflix.imfutility.util;

/**
 * Created by Alexander on 5/4/2016.
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
