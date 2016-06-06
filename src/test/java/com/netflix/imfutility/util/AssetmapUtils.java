package com.netflix.imfutility.util;

import java.io.File;

/**
 * Test utility for ASSETMAP.xml.
 */
public final class AssetmapUtils {

    private AssetmapUtils() {
    }

    public static String getAbsolutePath(String file) {
        return new File(AssetmapUtils.getImpFolder(), file).getAbsolutePath();
    }

    public static File getImpFolder() {
        return new File(ClassLoader.getSystemClassLoader().getResource("imp").getPath());
    }


    public static String getCorrectAssetmap() {
        return ClassLoader.getSystemClassLoader().getResource("imp/ASSETMAP.xml").getPath();
    }

    public static String getBrokenXmlAssetmap() {
        return ClassLoader.getSystemClassLoader().getResource("xml/invalid/broken-xml-ASSETMAP.xml").getPath();
    }

    public static String getInvalidXsdAssetmap() {
        return ClassLoader.getSystemClassLoader().getResource("xml/invalid/invalid-xsd-ASSETMAP.xml").getPath();
    }

}
