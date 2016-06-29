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
        ConfigXmlProvider configProvider = new ConfigXmlProvider(ConfigUtils.getCorrectConfigXml());

        assertNotNull(configProvider.getConfig());
        assertNotNull(configProvider.getConfig().getExternalTools());

        assertNotNull(configProvider.getConfig().getExternalTools().getMap().get("toolSimple"));
        assertEquals("root\\toolSimple", configProvider.getConfig().getExternalTools().getMap().get("toolSimple").getValue());

        assertNotNull(configProvider.getConfig().getExternalTools().getMap().get("toolWhitespace"));
        assertEquals("root\\tool whitespace", configProvider.getConfig().getExternalTools().getMap().get("toolWhitespace").getValue());
    }

    @Test(expected = XmlParsingException.class)
    public void testParseBrokenXml() throws Exception {
        new ConfigXmlProvider(ConfigUtils.getBrokenXmlConfigXml());
    }

    @Test(expected = XmlParsingException.class)
    public void testParseInvalidXsd() throws Exception {
        new ConfigXmlProvider(ConfigUtils.getInvalidXsdConfigXml());
    }

    @Test(expected = FileNotFoundException.class)
    public void testParseInvalidFilePath() throws Exception {
        new ConfigXmlProvider(new File("C:/invalid-path"));
    }

}
