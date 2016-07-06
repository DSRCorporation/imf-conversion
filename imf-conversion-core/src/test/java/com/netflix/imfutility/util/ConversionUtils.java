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

import com.netflix.imfutility.resources.ResourceHelper;

import java.io.InputStream;

/**
 * Test utility for conversion.xml.
 */
public final class ConversionUtils {

    private ConversionUtils() {
    }

    public static InputStream getCorrectConversionXml() {
        return ResourceHelper.getResourceInputStream(getCorrectConversionXmlPath());
    }

    public static String getCorrectConversionXmlPath() {
        return "xml/test-conversion.xml";
    }

    public static InputStream getBrokenXmlConversionXml() {
        return ResourceHelper.getResourceInputStream(getBrokenXmlConversionXmlPath());
    }

    public static String getBrokenXmlConversionXmlPath() {
        return "xml/invalid/broken-xml-conversion.xml";
    }

    public static InputStream getInvalidXsdConversionXml() {
        return ResourceHelper.getResourceInputStream(getInvalidXsdConversionXmlPath());
    }

    public static String getInvalidXsdConversionXmlPath() {
        return "xml/invalid/invalid-xsd-conversion.xml";
    }


}
