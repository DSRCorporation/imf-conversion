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
package com.netflix.imfutility.config;

import com.netflix.imfutility.util.ConfigUtils;
import com.netflix.imfutility.xml.XmlParsingException;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;


/**
 * <ul>
 * <li>Tests the config.xml can be parsed and mapped to Java model successfully.</li>
 * <li>Tests the XSD validation is applied to the config.xml and an exception is thrown is validation doesn't pass.</li>
 * </ul>
 */

public class ConfigProviderTest {

    @Test
    public void testParseCorrectConfig() throws Exception {
        ConfigXmlProvider configProvider = new ConfigXmlProvider(ConfigUtils.getCorrectConfigXml(), ConfigUtils.getCorrectConfigXmlPath());

        assertNotNull(configProvider.getConfig());
        assertNotNull(configProvider.getConfig().getExternalTools());

        assertNotNull(configProvider.getConfig().getExternalTools().getMap().get("toolSimple"));
        assertEquals("root\\toolSimple", configProvider.getConfig().getExternalTools().getMap().get("toolSimple").getValue());

        assertNotNull(configProvider.getConfig().getExternalTools().getMap().get("toolWhitespace"));
        assertEquals("root\\tool whitespace", configProvider.getConfig().getExternalTools().getMap().get("toolWhitespace").getValue());
    }

    @Test(expected = XmlParsingException.class)
    public void testParseBrokenXml() throws Exception {
        new ConfigXmlProvider(ConfigUtils.getBrokenXmlConfigXml(), ConfigUtils.getBrokenXmlConfigXmlPath());
    }

    @Test(expected = XmlParsingException.class)
    public void testParseInvalidXsd() throws Exception {
        new ConfigXmlProvider(ConfigUtils.getInvalidXsdConfigXml(), ConfigUtils.getInvalidXsdConfigXmlPath());
    }

    @Test(expected = FileNotFoundException.class)
    public void testParseInvalidFilePath() throws Exception {
        new ConfigXmlProvider(new File("invalid-path"));
    }

}
