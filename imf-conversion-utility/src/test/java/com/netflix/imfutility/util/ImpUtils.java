package com.netflix.imfutility.util;

import java.io.File;

/**
 * Test utility for CPL.xml.
 */
public final class ImpUtils {

    private ImpUtils() {
    }

    public static File getCorrectCpl() {
        return new File(ClassLoader.getSystemClassLoader().getResource("imp/CPL.xml").getPath());
    }

    public static File getCorrectCplOneEssence() {
        return new File(ClassLoader.getSystemClassLoader().getResource("imp/CPL-one-essence.xml").getPath());
    }

    public static File getBrokenXmlCpl() {
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/invalid/broken-xml-CPL.xml").getPath());
    }

    public static File getInvalidXsdCpl() {
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/invalid/invalid-xsd-CPL.xml").getPath());
    }

    public static String getAbsolutePath(String file) {
        return new File(getImpFolder(), file).getAbsolutePath();
    }

    public static File getImpFolder() {
        return new File(ClassLoader.getSystemClassLoader().getResource("imp").getPath());
    }

    public static File getCorrectAssetmap() {
        return new File(ClassLoader.getSystemClassLoader().getResource("imp/ASSETMAP.xml").getPath());
    }

    public static File getBrokenXmlAssetmap() {
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/invalid/broken-xml-ASSETMAP.xml").getPath());
    }

    public static File getInvalidXsdAssetmap() {
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/invalid/invalid-xsd-ASSETMAP.xml").getPath());
    }


    public static File getCorrectImpForValidation() {
        return new File(ClassLoader.getSystemClassLoader().getResource("imp-validate-correct").getPath());
    }

    public static File getCorrectCplForValidation() {
        return new File(ClassLoader.getSystemClassLoader().getResource("imp-validate-correct/CPL_a453b63a-cf4d-454a-8c34-141f560c0100.xml").getPath());
    }

    public static File getInvalidImpForValidation() {
        return new File(ClassLoader.getSystemClassLoader().getResource("imp-validate-invalid").getPath());
    }

    public static File getInvalidCplForValidation() {
        return new File(ClassLoader.getSystemClassLoader().getResource("imp-validate-invalid/CPL_a453b63a-cf4d-454a-8c34-141f560c0100.xml").getPath());
    }


}
