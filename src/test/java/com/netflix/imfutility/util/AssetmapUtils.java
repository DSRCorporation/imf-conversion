package com.netflix.imfutility.util;

/**
 * Test utility for ASSETMAP.xml.
 */
public final class AssetmapUtils {

    private AssetmapUtils() {
    }

    public static String getCorrectAssetmap() {
        return ClassLoader.getSystemClassLoader().getResource("xml/ASSETMAP.xml").getPath();
    }

    public static String getBrokenXmlAssetmap() {
        return ClassLoader.getSystemClassLoader().getResource("xml/invalid/broken-xml-ASSETMAP.xml").getPath();
    }

    public static String getInvalidXsdAssetmap() {
        return ClassLoader.getSystemClassLoader().getResource("xml/invalid/invalid-xsd-ASSETMAP.xml").getPath();
    }

}
