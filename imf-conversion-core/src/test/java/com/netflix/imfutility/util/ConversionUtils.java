package com.netflix.imfutility.util;

import com.netflix.imfutility.resources.ResourceHelper;

import java.io.InputStream;

/**
 * Test utility for conversion.xml.
 */
public final class ConversionUtils {

    private ConversionUtils() {
    }

    public static InputStream getCorrectConversionXml() {
        return ResourceHelper.getResourceInputStream(getCorrectConversionXmlPath());
    }

    public static String getCorrectConversionXmlPath() {
        return "xml/test-conversion.xml";
    }

    public static InputStream getBrokenXmlConversionXml() {
        return ResourceHelper.getResourceInputStream(getBrokenXmlConversionXmlPath());
    }

    public static String getBrokenXmlConversionXmlPath() {
        return "xml/invalid/broken-xml-conversion.xml";
    }

    public static InputStream getInvalidXsdConversionXml() {
        return ResourceHelper.getResourceInputStream(getInvalidXsdConversionXmlPath());
    }

    public static String getInvalidXsdConversionXmlPath() {
        return "xml/invalid/invalid-xsd-conversion.xml";
    }


}
