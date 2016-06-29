package com.netflix.imfutility.util;

import java.io.File;

/**
 * Test utility for metadata.xml.
 */
public final class MetadataUtils {

    private MetadataUtils() {
    }

    public static File getCorrectMetadataXml() {
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/test-metadata.xml").getPath());
    }

    public static File getBrokenXmlMetadataXml() {
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/invalid/broken-xml-metadata.xml").getPath());
    }

    public static File getInvalidXsdMetadataXml() {
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/invalid/invalid-xsd-metadata.xml").getPath());
    }

}
