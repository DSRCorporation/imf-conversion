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
import java.net.URISyntaxException;

/**
 * Test utility for mediaInfo.xml.
 */
public final class MediaInfoUtils {

    private MediaInfoUtils() {
    }

    public static File getCorrectMediaInfoAudio() throws URISyntaxException {
        //noinspection ConstantConditions
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/mediaInfoAudio.xml").toURI());
    }

    public static File getCorrectMediaInfoAudio2() throws URISyntaxException {
        //noinspection ConstantConditions
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/mediaInfoAudio2.xml").toURI());
    }

    public static File getCorrectMediaInfoAudio3() throws URISyntaxException {
        //noinspection ConstantConditions
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/mediaInfoAudio3.xml").toURI());
    }

    public static File getCorrectMediaInfoVideo() throws URISyntaxException {
        //noinspection ConstantConditions
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/mediaInfoVideo.xml").toURI());
    }

    public static File getCorrectMediaInfoVideo2() throws URISyntaxException {
        //noinspection ConstantConditions
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/mediaInfoVideo2.xml").toURI());
    }

    public static File getBrokenXmlMediaInfoAudio() throws URISyntaxException {
        //noinspection ConstantConditions
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/invalid/broken-xml-mediaInfoAudio.xml").toURI());
    }

    public static File getBrokenXmlMediaInfoVideo() throws URISyntaxException {
        //noinspection ConstantConditions
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/invalid/broken-xml-mediaInfoVideo.xml").toURI());
    }

    public static File getInvalidXsdMediaInfoAudio() throws URISyntaxException {
        //noinspection ConstantConditions
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/invalid/invalid-xsd-mediaInfoAudio.xml").toURI());
    }

    public static File getInvalidXsdMediaInfoVideo() throws URISyntaxException {
        //noinspection ConstantConditions
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/invalid/invalid-xsd-mediaInfoVideo.xml").toURI());
    }

}
