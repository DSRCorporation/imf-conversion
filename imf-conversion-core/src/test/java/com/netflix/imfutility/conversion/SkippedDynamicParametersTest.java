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
package com.netflix.imfutility.conversion;

import com.netflix.imfutility.FakeFormat;
import com.netflix.imfutility.config.ConfigXmlProvider;
import com.netflix.imfutility.conversion.templateParameter.context.DynamicTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.util.ConfigUtils;
import com.netflix.imfutility.util.ConversionUtils;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import com.netflix.imfutility.util.conversion.executor.TestConversionEngine;
import org.junit.Before;
import org.junit.Test;

import java.util.EnumSet;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

/**
 * Tests that dynamic parameters are skipped correctly.
 */
public class SkippedDynamicParametersTest {

    private static final int SEGMENT_COUNT = 2;
    private static final int SEQ_COUNT = 2;
    private static final int RESOURCE_COUNT = 2;
    private static final int REPEAT_COUNT = 2;

    private ConversionXmlProvider conversionProvider;
    private TemplateParameterContextProvider contextProvider;
    private TestConversionEngine conversionEngine;

    @Before
    public void setUp() throws Exception {
        initContext();

        TemplateParameterContextCreator.fillCPLContext(contextProvider,
                SEGMENT_COUNT,
                SEQ_COUNT,
                RESOURCE_COUNT,
                REPEAT_COUNT,
                EnumSet.of(SequenceType.VIDEO, SequenceType.AUDIO)); // do not fill subtitle type!

        fillDynamicContext();

        conversionEngine = new TestConversionEngine();
    }

    private void initContext() throws Exception {
        conversionProvider = new ConversionXmlProvider(ConversionUtils.getConversionXmlSkipDynamicParams(),
                ConversionUtils.getCorrectConversionXmlPath(), new FakeFormat());
        ConfigXmlProvider configProvider = new ConfigXmlProvider(ConfigUtils.getCorrectConfigXml(),
                ConfigUtils.getCorrectConfigXmlPath());
        contextProvider = new TemplateParameterContextProvider(configProvider, conversionProvider,
                TemplateParameterContextCreator.getCurrentTmpDir());
    }

    private void fillDynamicContext() throws Exception {
        contextProvider.getDynamicContext().addParameter("skipped", "false");
        contextProvider.getDynamicContext().addParameter("notSkipped", "true");
    }

    @Test
    public void testSimpleIf() throws Exception {
        // run conversion (process dynamic parameters)
        conversionEngine.convert(conversionProvider.getFormatConfigurationType("skippedSimpleIf"), contextProvider);

        // check that dynamic parameters were skipped correctly
        DynamicTemplateParameterContext dynamicCtxt = contextProvider.getDynamicContext();

        assertDynamicParamAbsent(dynamicCtxt, "skippedIfName");
        assertDynamicParamPresent(dynamicCtxt, "nonSkippedIfName", "nonSkippedIfValue");
    }

    @Test
    public void testSimpleUnless() throws Exception {
        // run conversion (process dynamic parameters)
        conversionEngine.convert(conversionProvider.getFormatConfigurationType("skippedSimpleUnless"), contextProvider);

        // check that dynamic parameters were skipped correctly
        DynamicTemplateParameterContext dynamicCtxt = contextProvider.getDynamicContext();

        assertDynamicParamAbsent(dynamicCtxt, "skippedUnlessName");
        assertDynamicParamPresent(dynamicCtxt, "nonSkippedUnlessName", "nonSkippedUnlessValue");
    }

    @Test
    public void testIfWithDynamicParam() throws Exception {
        // run conversion (process dynamic parameters)
        conversionEngine.convert(conversionProvider.getFormatConfigurationType("skippedParam"), contextProvider);

        // check that dynamic parameters were skipped correctly
        DynamicTemplateParameterContext dynamicCtxt = contextProvider.getDynamicContext();

        assertDynamicParamAbsent(dynamicCtxt, "skippedIfParamName");
        assertDynamicParamAbsent(dynamicCtxt, "skippedUnlessParamName");
        assertDynamicParamPresent(dynamicCtxt, "nonSkippedIfParamName", "nonSkippedIfParamValue");
        assertDynamicParamPresent(dynamicCtxt, "nonSkippedUnlessParamName", "nonSkippedUnlessParamValue");
    }

    @Test
    public void testSkippedWithinEachSequence() throws Exception {
        // run conversion (process dynamic parameters)
        conversionEngine.convert(conversionProvider.getFormatConfigurationType("skippedEachSeq"), contextProvider);

        // check that dynamic parameters were skipped correctly
        DynamicTemplateParameterContext dynamicCtxt = contextProvider.getDynamicContext();

        assertDynamicParamAbsent(dynamicCtxt, "skippedSeqName1");
        assertDynamicParamAbsent(dynamicCtxt, "skippedSeqSegmName1");
        assertDynamicParamAbsent(dynamicCtxt, "skippedSeqSegmName2");
        assertDynamicParamAbsent(dynamicCtxt, "skippedSeqSegmName3");
        assertDynamicParamAbsent(dynamicCtxt, "skippedSeqName2");
        assertDynamicParamAbsent(dynamicCtxt, "skippedSeqSegmName4");
    }

    @Test
    public void testSkippedWithinEachSegment() throws Exception {
        // run conversion (process dynamic parameters)
        conversionEngine.convert(conversionProvider.getFormatConfigurationType("skippedEachSegm"), contextProvider);

        // check that dynamic parameters were skipped correctly
        DynamicTemplateParameterContext dynamicCtxt = contextProvider.getDynamicContext();

        assertDynamicParamAbsent(dynamicCtxt, "skippedSegmName1");
        assertDynamicParamAbsent(dynamicCtxt, "skippedSegmSeqName1");
        assertDynamicParamAbsent(dynamicCtxt, "skippedSegmSeqName2");
        assertDynamicParamAbsent(dynamicCtxt, "skippedSegmSeqName3");
        assertDynamicParamAbsent(dynamicCtxt, "skippedSegmName2");
        assertDynamicParamAbsent(dynamicCtxt, "skippedSegmSeqName4");
    }

    @Test
    public void testNotSkippedWithinEachSequence() throws Exception {
        // run conversion (process dynamic parameters)
        conversionEngine.convert(conversionProvider.getFormatConfigurationType("nonSkippedEachSeq"), contextProvider);

        // check that dynamic parameters were skipped correctly
        DynamicTemplateParameterContext dynamicCtxt = contextProvider.getDynamicContext();

        assertDynamicParamPresent(dynamicCtxt, "nonSkippedSeqName", "nonSkippedSeqValue");
        assertDynamicParamPresent(dynamicCtxt, "nonSkippedSeqSegmName", "nonSkippedSeqSegmValue");
    }

    @Test
    public void testNotSkippedWithinEachSegment() throws Exception {
        // run conversion (process dynamic parameters)
        conversionEngine.convert(conversionProvider.getFormatConfigurationType("nonSkippedEachSegm"), contextProvider);

        // check that dynamic parameters were skipped correctly
        DynamicTemplateParameterContext dynamicCtxt = contextProvider.getDynamicContext();

        assertDynamicParamPresent(dynamicCtxt, "nonSkippedSegmName", "nonSkippedSegmValue");
        assertDynamicParamPresent(dynamicCtxt, "nonSkippedSegmSeqName", "nonSkippedSegmSeqValue");
    }

    private void assertDynamicParamPresent(DynamicTemplateParameterContext dynamicCtxt, String paramName, String paramValue) {
        assertEquals(paramValue, dynamicCtxt.getParameterValueAsString(paramName));
    }

    private void assertDynamicParamAbsent(DynamicTemplateParameterContext dynamicCtxt, String paramName) {
        try {
            dynamicCtxt.getParameterValueAsString(paramName);
            fail(String.format("Template parameter '%s' is present but expected to be absent", paramName));
        } catch (TemplateParameterNotFoundException e) {
            // expected
        }
    }

}
