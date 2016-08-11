/*
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
import java.net.URISyntaxException;

/**
 * Test utility for CPL.xml.
 */
public final class ImpUtils {

    private ImpUtils() {
    }

    public static File getCplSequence() throws URISyntaxException {
        //noinspection ConstantConditions,ConstantConditions
        return new File(ClassLoader.getSystemClassLoader().getResource("imp/CPL-sequence.xml").toURI());
    }

    public static File getCorrectCpl() throws URISyntaxException {
        //noinspection ConstantConditions,ConstantConditions
        return new File(ClassLoader.getSystemClassLoader().getResource("imp/CPL.xml").toURI());
    }

    public static File getCorrectCplNonZeroStart() throws URISyntaxException {
        //noinspection ConstantConditions,ConstantConditions
        return new File(ClassLoader.getSystemClassLoader().getResource("imp/CPL-non-zero-start.xml").toURI());
    }

    public static File getCorrectCplNoStart() throws URISyntaxException {
        //noinspection ConstantConditions,ConstantConditions
        return new File(ClassLoader.getSystemClassLoader().getResource("imp/CPL-no-start.xml").toURI());
    }

    public static File getCorrectCplOneEssence() throws URISyntaxException {
        //noinspection ConstantConditions
        return new File(ClassLoader.getSystemClassLoader().getResource("imp/CPL-one-essence.xml").toURI());
    }

    public static File getCplLanguages() throws URISyntaxException {
        //noinspection ConstantConditions,ConstantConditions
        return new File(ClassLoader.getSystemClassLoader().getResource("imp/CPL-languages.xml").toURI());
    }

    public static File getBrokenXmlCpl() throws URISyntaxException {
        //noinspection ConstantConditions
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/invalid/broken-xml-CPL.xml").toURI());
    }

    public static File getInvalidXsdCpl() throws URISyntaxException {
        //noinspection ConstantConditions
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/invalid/invalid-xsd-CPL.xml").toURI());
    }

    public static String getAbsolutePath(String file) throws URISyntaxException {
        return new File(getImpFolder(), file).getAbsolutePath();
    }

    public static File getImpFolder() throws URISyntaxException {
        //noinspection ConstantConditions
        return new File(ClassLoader.getSystemClassLoader().getResource("imp").toURI());
    }

    public static File getCorrectAssetmap() throws URISyntaxException {
        //noinspection ConstantConditions
        return new File(ClassLoader.getSystemClassLoader().getResource("imp/ASSETMAP.xml").toURI());
    }

    public static File getBrokenXmlAssetmap() throws URISyntaxException {
        //noinspection ConstantConditions
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/invalid/broken-xml-ASSETMAP.xml").toURI());
    }

    public static File getInvalidXsdAssetmap() throws URISyntaxException {
        //noinspection ConstantConditions
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/invalid/invalid-xsd-ASSETMAP.xml").toURI());
    }

}
