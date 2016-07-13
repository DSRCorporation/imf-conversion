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

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;

/**
 * Test utility for config.xml.
 */
public final class ConfigUtils {

    private ConfigUtils() {
    }

    public static InputStream getCorrectConfigXml() {
        return ResourceHelper.getResourceInputStream(getCorrectConfigXmlPath());
    }

    public static String getCorrectConfigXmlPath() {
        return "xml/test-config.xml";
    }

    public static File getCorrectConfigXmlFile() throws URISyntaxException {
        return new File(ConfigUtils.class.getClassLoader().getResource(getCorrectConfigXmlPath()).toURI());
    }

    public static InputStream getBrokenXmlConfigXml() {
        return ResourceHelper.getResourceInputStream(getBrokenXmlConfigXmlPath());
    }

    public static String getBrokenXmlConfigXmlPath() {
        return "xml/invalid/broken-xml-config.xml";
    }

    public static InputStream getInvalidXsdConfigXml() {
        return ResourceHelper.getResourceInputStream(getInvalidXsdConfigXmlPath());
    }

    public static String getInvalidXsdConfigXmlPath() {
        return "xml/invalid/invalid-xsd-config.xml";
    }

}
