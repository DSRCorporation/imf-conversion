package com.netflix.imfutility.util;

import java.io.InputStream;

/**
 * Test utility for conversion.xml.
 */
public final class ConversionUtils {

    private ConversionUtils() {
    }

    public static InputStream getCorrectConversionXml() {
        return ConversionUtils.class.getClassLoader().getResourceAsStream("xml/test-conversion.xml");
    }

    public static String getCorrectConversionXmlPath() {
        return "xml/test-conversion.xml";
    }

    public static InputStream getBrokenXmlConversionXml() {
        return ConversionUtils.class.getClassLoader().getResourceAsStream(getBrokenXmlConversionXmlPath());
    }

    public static String getBrokenXmlConversionXmlPath() {
        return "xml/invalid/broken-xml-conversion.xml";
    }

    public static InputStream getInvalidXsdConversionXml() {
        return ConversionUtils.class.getClassLoader().getResourceAsStream(getInvalidXsdConversionXmlPath());
    }

    public static String getInvalidXsdConversionXmlPath() {
        return "xml/invalid/invalid-xsd-conversion.xml";
    }


}
