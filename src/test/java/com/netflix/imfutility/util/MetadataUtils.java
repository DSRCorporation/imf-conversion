package com.netflix.imfutility.util;

/**
 * Test utility for metadata.xml.
 */
public final class MetadataUtils {

    private MetadataUtils() {
    }

    public static String getCorrectMetadataXml() {
        return ClassLoader.getSystemClassLoader().getResource("xml/test-metadata.xml").getPath();
    }

    public static String getBrokenXmlMetadataXml() {
        return ClassLoader.getSystemClassLoader().getResource("xml/invalid/broken-xml-metadata.xml").getPath();
    }

    public static String getInvalidXsdMetadataXml() {
        return ClassLoader.getSystemClassLoader().getResource("xml/invalid/invalid-xsd-metadata.xml").getPath();
    }

}
