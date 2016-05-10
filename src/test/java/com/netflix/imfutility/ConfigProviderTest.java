package com.netflix.imfutility;

import com.netflix.imfutility.util.ConfigUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xml.sax.SAXParseException;

import javax.xml.bind.UnmarshalException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.core.Is.isA;


/**
 * <ul>
 * <li>Tests the config.xml can be parsed and mapped to Java model successfully.</li>
 * <li>Tests the XSD validation is applied to the config.xml and an exception is thrown is validation doesn't pass.</li>
 * </ul>
 */

public class ConfigProviderTest {

    @Rule
    public final ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testParseCorrectConfig() throws Exception {
        ConfigProvider configProvider = new ConfigProvider(ConfigUtils.getCorrectConfigXml());

        assertNotNull(configProvider.getConfig());
        assertNotNull(configProvider.getConfig().getExternalTools());

        assertNotNull(configProvider.getConfig().getExternalTools().getMap().get("toolSimple"));
        assertEquals("root\\toolSimple", configProvider.getConfig().getExternalTools().getMap().get("toolSimple").getValue());

        assertNotNull(configProvider.getConfig().getExternalTools().getMap().get("toolWhitespace"));
        assertEquals("root\\tool whitespace", configProvider.getConfig().getExternalTools().getMap().get("toolWhitespace").getValue());
    }

    @Test
    public void testParseBrokenXml() throws Exception {
        expectedEx.expect(UnmarshalException.class);
        expectedEx.expectCause(isA(SAXParseException.class));

        new ConfigProvider(ConfigUtils.getBrokenXmlConfigXml());
    }

    @Test
    public void testParseInvalidXsd() throws Exception {
        expectedEx.expect(UnmarshalException.class);
        expectedEx.expectCause(isA(SAXParseException.class));

        new ConfigProvider(ConfigUtils.getInvalidXsdConfigXml());
    }

}
