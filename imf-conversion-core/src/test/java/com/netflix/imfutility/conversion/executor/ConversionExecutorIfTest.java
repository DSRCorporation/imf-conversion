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

import com.netflix.imfutility.conversion.executor.strategy.AbstractExecuteStrategy;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;
import com.netflix.imfutility.util.ConversionUtils;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import com.netflix.imfutility.util.conversion.executor.TestConversionEngine;
import com.netflix.imfutility.util.conversion.executor.TestExecutorLogger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests 'for' conversion operation.
 */
public class ConversionExecutorIfTest {

    private static TemplateParameterContextProvider contextProvider;
    private static TestConversionEngine conversionEngine;
    private static TestExecutorLogger executorLogger;

    @BeforeClass
    public static void setUpAll() throws Exception {
        contextProvider = TemplateParameterContextCreator.createDefaultContextProvider(
                ConversionUtils.getIfConversionXmlPath());
        TemplateParameterContextCreator.fillCPLContext(contextProvider, 1, 1, 1);

        conversionEngine = new TestConversionEngine();
        executorLogger = conversionEngine.getExecutorLogger();
    }

    @Before
    public void setUp() {
        AbstractExecuteStrategy.resetCount();
        executorLogger.reset();
    }

    @Test
    public void testSimpleTrueIf() throws Exception {
        conversionEngine.convert(contextProvider.getConversionProvider().getFormatConfigurationType("simpleTrue"), contextProvider);

        executorLogger.assertNextStart("execOnceTrue, TestExecuteOnceStrategy, execOnceTrueExec ERR_LOG", 1);
        executorLogger.assertNextFinish("execOnceTrue, TestExecuteOnceStrategy, execOnceTrueExec ERR_LOG", 1);
        assertFalse("There are more executed processes than expected!", executorLogger.hasNext());
    }

    @Test
    public void testSimpleFalseIf() throws Exception {
        conversionEngine.convert(contextProvider.getConversionProvider().getFormatConfigurationType("simpleFalse"), contextProvider);

        assertFalse("There are more executed processes than expected!", executorLogger.hasNext());
    }

    @Test
    public void testTmpContextIf() throws Exception {
        conversionEngine.convert(contextProvider.getConversionProvider().getFormatConfigurationType("tmpContext"), contextProvider);

        executorLogger.assertNextStart("execOnceTmpTrue, TestExecuteOnceStrategy, execOnceTmpTrueExec ERR_LOG", 1);
        executorLogger.assertNextFinish("execOnceTmpTrue, TestExecuteOnceStrategy, execOnceTmpTrueExec ERR_LOG", 1);
        assertFalse("There are more executed processes than expected!", executorLogger.hasNext());
    }

    @Test
    public void testDynamicContextIf() throws Exception {
        conversionEngine.convert(contextProvider.getConversionProvider().getFormatConfigurationType("dynamicContext"), contextProvider);

        executorLogger.assertNextStart("execOnceDynamicTrue, TestExecuteOnceStrategy, execOnceDynamicTrueExec ERR_LOG", 1);
        executorLogger.assertNextFinish("execOnceDynamicTrue, TestExecuteOnceStrategy, execOnceDynamicTrueExec ERR_LOG", 1);
        assertFalse("There are more executed processes than expected!", executorLogger.hasNext());
    }

    @Test
    public void testDynamicContextCodeIf() throws Exception {
        contextProvider.getDynamicContext().addParameter("falseCodeParam", Boolean.FALSE.toString());
        contextProvider.getDynamicContext().addParameter("trueCodeParam", Boolean.TRUE.toString());

        conversionEngine.convert(contextProvider.getConversionProvider().getFormatConfigurationType("dynamicContextCode"), contextProvider);

        executorLogger.assertNextStart("execOnceDynamicTrue, TestExecuteOnceStrategy, execOnceDynamicTrueExec ERR_LOG", 1);
        executorLogger.assertNextFinish("execOnceDynamicTrue, TestExecuteOnceStrategy, execOnceDynamicTrueExec ERR_LOG", 1);
        assertFalse("There are more executed processes than expected!", executorLogger.hasNext());
    }

    @Test
    public void testInnerExecEachSequence() throws Exception {
        conversionEngine.convert(contextProvider.getConversionProvider().getFormatConfigurationType("innerExecEachSequence"), contextProvider);

        executorLogger.assertNextStart("execEachSequence, TestExecuteOnceStrategy, execEachSequenceExec ERR_LOG", 1);
        executorLogger.assertNextFinish("execEachSequence, TestExecuteOnceStrategy, execEachSequenceExec ERR_LOG", 1);
        assertFalse("There are more executed processes than expected!", executorLogger.hasNext());
    }

    @Test
    public void testInnerExecEachSegment() throws Exception {
        conversionEngine.convert(contextProvider.getConversionProvider().getFormatConfigurationType("innerExecEachSegment"),
                contextProvider);

        executorLogger.assertNextStart("execEachSegment, TestExecuteOnceStrategy, execEachSegmentExec ERR_LOG", 1);
        executorLogger.assertNextFinish("execEachSegment, TestExecuteOnceStrategy, execEachSegmentExec ERR_LOG", 1);
        assertFalse("There are more executed processes than expected!", executorLogger.hasNext());
    }

    @Test
    public void testInnerPipe() throws Exception {
        conversionEngine.convert(contextProvider.getConversionProvider().getFormatConfigurationType("innerPipe"), contextProvider);

        executorLogger.assertNextStart("pipeExecOnce, TestExecutePipeStrategy, pipeExecOnceExec ERR_LOG", 1);
        executorLogger.assertNextFinish("pipeExecOnce, TestExecutePipeStrategy, pipeExecOnceExec ERR_LOG", 1);
        assertFalse("There are more executed processes than expected!", executorLogger.hasNext());
    }

    @Test
    public void testInnerDynamicParameter() throws Exception {
        conversionEngine.convert(contextProvider.getConversionProvider().getFormatConfigurationType("innerDynamicParameter"),
                contextProvider);

        assertFalse("There are more executed processes than expected!", executorLogger.hasNext());

        assertEquals("paramValue", contextProvider.getDynamicContext().getParameterValueAsString("param"));
    }

    @Test
    public void testInnerFor() throws Exception {
        conversionEngine.convert(contextProvider.getConversionProvider().getFormatConfigurationType("innerFor"),
                contextProvider);

        assertFalse("There are more executed processes than expected!", executorLogger.hasNext());

        assertEquals("0", contextProvider.getDynamicContext().getParameterValueAsString("forResult"));
    }

    @Test
    public void testInnerIf() throws Exception {
        conversionEngine.convert(contextProvider.getConversionProvider().getFormatConfigurationType("innerIf"), contextProvider);

        executorLogger.assertNextStart("execOnceInnerTrue1, TestExecuteOnceStrategy, execOnceInnerTrue1Exec ERR_LOG", 1);
        executorLogger.assertNextFinish("execOnceInnerTrue1, TestExecuteOnceStrategy, execOnceInnerTrue1Exec ERR_LOG", 1);
        assertFalse("There are more executed processes than expected!", executorLogger.hasNext());
    }

    @Test
    public void testInnerMultipleOperations() throws Exception {
        conversionEngine.convert(contextProvider.getConversionProvider().getFormatConfigurationType("innerMultipleExec"), contextProvider);

        int next = 1;

        executorLogger.assertNextStart("execOnce1, TestExecuteOnceStrategy, execOnce1Exec ERR_LOG", next);
        executorLogger.assertNextFinish("execOnce1, TestExecuteOnceStrategy, execOnce1Exec ERR_LOG", next++);
        executorLogger.assertNextStart("execOnce2, TestExecuteOnceStrategy, execOnce2Exec ERR_LOG", next);
        executorLogger.assertNextFinish("execOnce2, TestExecuteOnceStrategy, execOnce2Exec ERR_LOG", next++);
        executorLogger.assertNextStart("execOnce3, TestExecuteOnceStrategy, execOnce3Exec ERR_LOG", next);
        executorLogger.assertNextFinish("execOnce3, TestExecuteOnceStrategy, execOnce3Exec ERR_LOG", next++);
        assertFalse("There are more executed processes than expected!", executorLogger.hasNext());
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void testUnresolvedIfCondition() throws Exception {
        conversionEngine.convert(contextProvider.getConversionProvider().getFormatConfigurationType("unresolvedIfCondition"), contextProvider);
    }
}
