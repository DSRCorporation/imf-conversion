package com.netflix.imfutility.util;

import com.netflix.imfutility.resources.ResourceHelper;

import java.io.InputStream;

/**
 * Test utility for config.xml.
 */
public final class ConfigUtils {

    private ConfigUtils() {
    }

    public static InputStream getCorrectConfigXml() {
        return ResourceHelper.getResourceInputStream(getCorrectConfigXmlPath());
    }

    public static String getCorrectConfigXmlPath() {
        return "xml/test-config.xml";
    }

    public static InputStream getBrokenXmlConfigXml() {
        return ResourceHelper.getResourceInputStream(getBrokenXmlConfigXmlPath());
    }

    public static String getBrokenXmlConfigXmlPath() {
        return "xml/invalid/broken-xml-config.xml";
    }

    public static InputStream getInvalidXsdConfigXml() {
        return ResourceHelper.getResourceInputStream(getInvalidXsdConfigXmlPath());
    }

    public static String getInvalidXsdConfigXmlPath() {
        return "xml/invalid/invalid-xsd-config.xml";
    }

}
