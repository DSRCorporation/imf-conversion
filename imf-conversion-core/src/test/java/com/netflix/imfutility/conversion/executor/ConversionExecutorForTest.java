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
package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.FakeFormat;
import com.netflix.imfutility.config.ConfigXmlProvider;
import com.netflix.imfutility.conversion.ConversionXmlProvider;
import com.netflix.imfutility.conversion.executor.strategy.AbstractExecuteStrategy;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.resources.ResourceHelper;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import com.netflix.imfutility.util.conversion.executor.TestConversionEngine;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests 'for' conversion operation.
 */
public class ConversionExecutorForTest {

    private static TestConversionEngine conversionEngine;

    @BeforeClass
    public static void setUpAll() throws Exception {
        conversionEngine = new TestConversionEngine();
    }

    @Before
    public void setUp() {
        AbstractExecuteStrategy.resetCount();
        conversionEngine.getExecutorLogger().reset();
    }

    /**
     * Checks that iterators are resolved in expected places: dynamic names/values, to/from/count of for operation.
     *
     * @throws Exception unexpected exceptions
     */
    @Test
    public void allIteratorResolvedCorrectly() throws Exception {
        /* INITIALIZATION */
        String configXml = "xml/test-config.xml";
        String inputConversionXml = "xml/for-operation/test-for-operation-basic.xml";
        ConversionXmlProvider conversionProvider = new ConversionXmlProvider(
                ResourceHelper.getResourceInputStream(inputConversionXml),
                inputConversionXml, new FakeFormat());
        ConfigXmlProvider configProvider =
                new ConfigXmlProvider(ResourceHelper.getResourceInputStream(configXml), configXml);
        TemplateParameterContextProvider contextProvider =
                new TemplateParameterContextProvider(configProvider, conversionProvider,
                TemplateParameterContextCreator.getCurrentTmpDir());

        contextProvider.getDynamicContext().addParameter("fromI", "0");
        contextProvider.getDynamicContext().addParameter("toI", "2");
        contextProvider.getDynamicContext().addParameter("fromJ", "2");
        contextProvider.getDynamicContext().addParameter("countJ0", "1");
        contextProvider.getDynamicContext().addParameter("countJ1", "3");

        /* PERFORMING */
        conversionEngine.convert(conversionProvider.getFormatConfigurationType("test"), contextProvider);

        /* VERIFICATION */
        assertEquals("|#test02|#test13#test14#test15",
                contextProvider.getDynamicContext().getParameterValueAsString("output"));
        assertEquals("6", contextProvider.getDynamicContext().getParameterValueAsString("fromJ"));
    }
}
