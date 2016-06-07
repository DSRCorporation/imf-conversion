package com.netflix.imfutility.util;

/**
 * Test utility for CPL.xml.
 */
public final class CplUtils {

    private CplUtils() {
    }

    public static String getCorrectCpl() {
        return ClassLoader.getSystemClassLoader().getResource("imp/CPL.xml").getPath();
    }

    public static String getCorrectCplOneEssence() {
        return ClassLoader.getSystemClassLoader().getResource("imp/CPL-one-essence.xml").getPath();
    }

    public static String getBrokenXmlCpl() {
        return ClassLoader.getSystemClassLoader().getResource("xml/invalid/broken-xml-CPL.xml").getPath();
    }

    public static String getInvalidXsdCpl() {
        return ClassLoader.getSystemClassLoader().getResource("xml/invalid/invalid-xsd-CPL.xml").getPath();
    }

}
