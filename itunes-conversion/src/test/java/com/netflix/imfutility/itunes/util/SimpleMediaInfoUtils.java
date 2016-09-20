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

import com.netflix.imfutility.resources.ResourceHelper;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;

/**
 * Test utility for testing {@link com.netflix.imfutility.itunes.mediainfo.SimpleMediaInfoBuilder}.
 */
public final class SimpleMediaInfoUtils {
    private SimpleMediaInfoUtils() {
    }

    public static String getConversionXmlPath() throws URISyntaxException {
        return "xml/mediainfo/test-media-info-conversion.xml";
    }

    public static InputStream getConversionXmlStream() throws URISyntaxException {
        return ResourceHelper.getResourceInputStream(getConversionXmlPath());
    }

    public static File getMediaInfoFile() throws URISyntaxException {
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/mediainfo/test-media-info.xml").toURI());
    }

}
