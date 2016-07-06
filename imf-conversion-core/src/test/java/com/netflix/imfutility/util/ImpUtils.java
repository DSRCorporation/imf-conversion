/**
 * Copyright (C) 2016 Netflix, Inc.
 *
 *     This file is part of IMF Conversion Utility.
 *
 *     IMF Conversion Utility is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     IMF Conversion Utility is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with IMF Conversion Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
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
