package com.netflix.imfutility.conversion;

import com.netflix.imfutility.Format;
import com.netflix.imfutility.util.ConversionUtils;
import com.netflix.imfutility.xml.XmlParsingException;
import org.junit.Test;

import java.io.FileNotFoundException;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;

/**
 * <ul>
 * <li>Tests the conversion.xml can be parsed and mapped to Java model successfully.</li>
 * <li>Tests the XSD validation is applied to the conversion.xml and an exception is thrown is validation doesn't pass.</li>
 * </ul>
 */
public class ConversionProviderTest {

    @Test
    public void testParseCorrectConversion() throws Exception {
        ConversionXmlProvider conversionProvider = new ConversionXmlProvider(ConversionUtils.getCorrectConversionXml(), Format.DPP);

        assertNotNull(conversionProvider.getFormat());
        assertNotNull(conversionProvider.getFormat().getFormatConfigurations());
        assertFalse(conversionProvider.getFormat().getFormatConfigurations().getMap().isEmpty());
    }

    @Test(expected = XmlParsingException.class)
    public void testParseBrokenXml() throws Exception {
        new ConversionXmlProvider(ConversionUtils.getBrokenXmlConversionXml(), Format.DPP);
    }

    @Test(expected = XmlParsingException.class)
    public void testParseInvalidXsd() throws Exception {
        new ConversionXmlProvider(ConversionUtils.getInvalidXsdConversionXml(), Format.DPP);
    }

    @Test(expected = FileNotFoundException.class)
    public void testParseInvalidFilePath() throws Exception {
        new ConversionXmlProvider("C:/invalid-path", Format.DPP);
    }

}
