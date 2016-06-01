package com.netflix.imfutility.util;

/**
 * Test utility for audiomap.xml.
 */
public final class AudioMapUtils {

    private AudioMapUtils() {
    }

    public static String getCorrectAudiomapXml() {
        return ClassLoader.getSystemClassLoader().getResource("xml/test-audiomap.xml").getPath();
    }

    public static String getBrokenXmlAudiomapXml() {
        return ClassLoader.getSystemClassLoader().getResource("xml/invalid/broken-xml-audiomap.xml").getPath();
    }

    public static String getInvalidXsdAudiomapXml() {
        return ClassLoader.getSystemClassLoader().getResource("xml/invalid/invalid-xsd-audiomap.xml").getPath();
    }

}
