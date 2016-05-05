package com.netflix.imfutility.conversion;

import com.netflix.imfutility.Format;
import com.netflix.imfutility.util.ConversionUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xml.sax.SAXParseException;

import javax.xml.bind.UnmarshalException;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.core.Is.isA;

/**
 * <ul>
 * <li>Tests the conversion.xml can be parsed and mapped to Java model successfully.</li>
 * <li>Tests the XSD validation is applied to the conversion.xml and an exception is thrown is validation doesn't pass.</li>
 * </ul>
 */
public class ConversionProviderTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testParseCorrectConversion() throws Exception {
        ConversionProvider conversionProvider = new ConversionProvider(ConversionUtils.getCorrectConversionXml(), Format.DPP);

        assertNotNull(conversionProvider.getFormat());
        assertNotNull(conversionProvider.getFormat().getFormatConfigurations());
        assertFalse(conversionProvider.getFormat().getFormatConfigurations().getMap().isEmpty());
    }

    @Test
    public void testParseBrokenXml() throws Exception {
        expectedEx.expect(UnmarshalException.class);
        expectedEx.expectCause(isA(SAXParseException.class));

        new ConversionProvider(ConversionUtils.getBrokenXmlConversionXml(), Format.DPP);
    }

    @Test
    public void testParseInvalidXsd() throws Exception {
        expectedEx.expect(UnmarshalException.class);
        expectedEx.expectCause(isA(SAXParseException.class));

        new ConversionProvider(ConversionUtils.getInvalidXsdConversionXml(), Format.DPP);
    }

}
