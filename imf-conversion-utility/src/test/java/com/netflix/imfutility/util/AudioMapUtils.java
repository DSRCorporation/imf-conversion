package com.netflix.imfutility.util;

import java.io.File;

/**
 * Test utility for audiomap.xml.
 */
public final class AudioMapUtils {

    private AudioMapUtils() {
    }

    public static File getCorrectAudiomapXml() {
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/test-audiomap.xml").getPath());
    }

    public static File getBrokenXmlAudiomapXml() {
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/invalid/broken-xml-audiomap.xml").getPath());
    }

    public static File getInvalidXsdAudiomapXml() {
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/invalid/invalid-xsd-audiomap.xml").getPath());
    }

}
