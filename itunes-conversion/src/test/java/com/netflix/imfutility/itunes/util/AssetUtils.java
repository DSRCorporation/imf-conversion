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
package com.netflix.imfutility.itunes.util;

import com.netflix.imfutility.generated.mediainfo.FormatType;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Test utility for asset processing.
 */
public final class AssetUtils {

    private AssetUtils() {
    }

    public static File getTestCorrectPosterFile() throws URISyntaxException {
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/image/test-image-1400-2100.jpg").toURI());
    }

    public static File getTestIncorrectPosterFile() throws URISyntaxException {
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/image/test-image-1920-1080.jpg").toURI());
    }

    public static File getTestCorrectChapterFile() throws URISyntaxException {
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/image/test-image-1920-1080.jpg").toURI());
    }

    public static File getTestCorrectCcUSFile() throws URISyntaxException {
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/scc/cc-en_US.scc").toURI());
    }

    public static File getTestCorrectCcGBFile() throws URISyntaxException {
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/scc/cc-en_US.scc").toURI());
    }

    public static File getTestInvalidLocaleCcFile() throws URISyntaxException {
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/scc/cc_no_locale.scc").toURI());
    }

    public static File getTestInvalidSignatureCcFile() throws URISyntaxException {
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/scc/cc_no_signature.scc").toURI());
    }

    public static FormatType createCorrectVideoFormat() {
        FormatType format = new FormatType();
        format.setFilename("file_name");
        format.setFormatLongName("QuickTime / MOV");
        return format;
    }

    public static FormatType createIncorrectVideoFormat() {
        FormatType format = new FormatType();
        format.setFilename("file_name");
        format.setFormatLongName("Not MOV");
        return format;
    }

}
