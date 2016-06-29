package com.netflix.imfutility.util;

import java.io.File;

/**
 * Test utility for config.xml.
 */
public final class ConfigUtils {

    private ConfigUtils() {
    }

    public static File getCorrectConfigXml() {
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/test-config.xml").getPath());
    }

    public static File getBrokenXmlConfigXml() {
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/invalid/broken-xml-config.xml").getPath());
    }

    public static File getInvalidXsdConfigXml() {
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/invalid/invalid-xsd-config.xml").getPath());
    }

}
