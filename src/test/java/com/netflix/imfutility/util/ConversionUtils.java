package com.netflix.imfutility.util;

/**
 * Test utility for conversion.xml.
 */
public final class ConversionUtils {

    private ConversionUtils() {
    }

    public static String getCorrectConversionXml() {
        return ClassLoader.getSystemClassLoader().getResource("xml/test-conversion.xml").getPath();
    }

    public static String getBrokenXmlConversionXml() {
        return ClassLoader.getSystemClassLoader().getResource("xml/broken-xml-conversion.xml").getPath();
    }

    public static String getInvalidXsdConversionXml() {
        return ClassLoader.getSystemClassLoader().getResource("xml/invalid-xsd-conversion.xml").getPath();
    }


}
