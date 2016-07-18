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
package com.netflix.imfutility.cpl;

import com.netflix.imfutility.asset.AssetMap;
import com.netflix.imfutility.asset.AssetMapParser;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.util.ImpUtils;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import com.netflix.imfutility.xml.XmlParsingException;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * <ul>
 * <li>Tests that cpl.xml can be parsed and mapped to Java model successfully.</li>
 * <li>Tests the XSD validation is applied to the cpl.xml and an exception is thrown is validation doesn't pass.</li>
 * </ul>
 */
public class CplContextBuilderParserTest {

    @Test
    public void testParseCorrectConfigWithoutErrors() throws Exception {
        createCplContextBuilder().build(ImpUtils.getCorrectCpl());
    }

    @Test(expected = XmlParsingException.class)
    public void testParseBrokenXml() throws Exception {
        createCplContextBuilder().build(ImpUtils.getBrokenXmlCpl());
    }

    @Test(expected = XmlParsingException.class)
    public void testParseInvalidXsd() throws Exception {
        createCplContextBuilder().build(ImpUtils.getInvalidXsdCpl());
    }

    @Test(expected = FileNotFoundException.class)
    public void testParseInvalidFilePath() throws Exception {
        createCplContextBuilder().build(new File("invalid-path"));
    }

    private CplContextBuilder createCplContextBuilder() throws Exception {
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        AssetMap assetMap = new AssetMapParser().parse(ImpUtils.getImpFolder(), ImpUtils.getCorrectAssetmap());
        return new CplContextBuilder(contextProvider, assetMap);
    }

}
