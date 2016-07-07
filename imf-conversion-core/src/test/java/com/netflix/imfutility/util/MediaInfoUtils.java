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

/**
 * Test utility for mediaInfo.xml.
 */
public final class MediaInfoUtils {

    private MediaInfoUtils() {
    }

    public static String getCorrectMediaInfoAudio() {
        //noinspection ConstantConditions
        return ClassLoader.getSystemClassLoader().getResource("xml/mediaInfoAudio.xml").getPath();
    }

    public static String getCorrectMediaInfoAudio2() {
        //noinspection ConstantConditions
        return ClassLoader.getSystemClassLoader().getResource("xml/mediaInfoAudio2.xml").getPath();
    }


    public static String getCorrectMediaInfoVideo() {
        //noinspection ConstantConditions
        return ClassLoader.getSystemClassLoader().getResource("xml/mediaInfoVideo.xml").getPath();
    }

    public static String getCorrectMediaInfoVideo2() {
        //noinspection ConstantConditions
        return ClassLoader.getSystemClassLoader().getResource("xml/mediaInfoVideo2.xml").getPath();
    }

    public static String getBrokenXmlMediaInfoAudio() {
        //noinspection ConstantConditions
        return ClassLoader.getSystemClassLoader().getResource("xml/invalid/broken-xml-mediaInfoAudio.xml").getPath();
    }

    public static String getBrokenXmlMediaInfoVideo() {
        //noinspection ConstantConditions
        return ClassLoader.getSystemClassLoader().getResource("xml/invalid/broken-xml-mediaInfoVideo.xml").getPath();
    }

    public static String getInvalidXsdMediaInfoAudio() {
        //noinspection ConstantConditions
        return ClassLoader.getSystemClassLoader().getResource("xml/invalid/invalid-xsd-mediaInfoAudio.xml").getPath();
    }

    public static String getInvalidXsdMediaInfoVideo() {
        //noinspection ConstantConditions
        return ClassLoader.getSystemClassLoader().getResource("xml/invalid/invalid-xsd-mediaInfoVideo.xml").getPath();
    }

}
