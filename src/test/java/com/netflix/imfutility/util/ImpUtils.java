package com.netflix.imfutility.util;

import java.io.File;

/**
 * Test utility for CPL.xml.
 */
public final class ImpUtils {

    private ImpUtils() {
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

    public static String getAbsolutePath(String file) {
        return new File(getImpFolder(), file).getAbsolutePath();
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


    public static String getCorrectImpForValidation() {
        return ClassLoader.getSystemClassLoader().getResource("imp-validate-correct").getPath();
    }

    public static String getCorrectCplForValidation() {
        return ClassLoader.getSystemClassLoader().getResource("imp-validate-correct/CPL_a453b63a-cf4d-454a-8c34-141f560c0100.xml").getPath();
    }

    public static String getInvalidImpForValidation() {
        return ClassLoader.getSystemClassLoader().getResource("imp-validate-invalid").getPath();
    }

    public static String getInvalidCplForValidation() {
        return ClassLoader.getSystemClassLoader().getResource("imp-validate-invalid/CPL_a453b63a-cf4d-454a-8c34-141f560c0100.xml").getPath();
    }


}
