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
package com.netflix.imfutility.itunes.mediainfo;

import com.netflix.imfutility.FakeFormat;
import com.netflix.imfutility.config.ConfigXmlProvider;
import com.netflix.imfutility.conversion.ConversionXmlProvider;
import com.netflix.imfutility.conversion.executor.strategy.AbstractExecuteStrategy;
import com.netflix.imfutility.conversion.templateParameter.context.DynamicTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.itunes.util.SimpleMediaInfoUtils;
import com.netflix.imfutility.itunes.util.TestUtils;
import com.netflix.imfutility.mediainfo.MediaInfoException;
import com.netflix.imfutility.util.ConfigUtils;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import com.netflix.imfutility.util.conversion.executor.TestExecuteStrategyFactory;
import com.netflix.imfutility.util.conversion.executor.TestExecutorLogger;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests media info builder for random asset.
 * (see {@link SimpleMediaInfoBuilder}
 */
public class SimpleMediaInfoBuilderTest {

    @BeforeClass
    public static void setupAll() throws IOException {
        // create both working directory and logs folder.
        FileUtils.deleteDirectory(TemplateParameterContextCreator.getWorkingDir());
        File workingDir = TemplateParameterContextCreator.getWorkingDir();
        if (!workingDir.mkdir()) {
            throw new RuntimeException("Could not create a working dir within tmp folder");
        }
    }

    @AfterClass
    public static void teardownAll() throws IOException {
        FileUtils.deleteDirectory(TemplateParameterContextCreator.getWorkingDir());
    }

    @Before
    public void setUp() {
        AbstractExecuteStrategy.resetCount();
    }


    private static TemplateParameterContextProvider createContextProvider() throws Exception {
        ConfigXmlProvider configProvider = new ConfigXmlProvider(
                ConfigUtils.getCorrectConfigXml(),
                ConfigUtils.getCorrectConfigXmlPath());
        ConversionXmlProvider conversionProvider = new ConversionXmlProvider(
                SimpleMediaInfoUtils.getConversionXmlStream(),
                SimpleMediaInfoUtils.getConversionXmlPath(),
                new FakeFormat());
        return new TemplateParameterContextProvider(configProvider, conversionProvider,
                TemplateParameterContextCreator.getWorkingDir());
    }

    @Test
    public void testMediaInfoCommandExecution() throws Exception {
        TemplateParameterContextProvider contextProvider = createContextProvider();

        // build media info
        TestExecutorLogger testExecutorLogger = new TestExecutorLogger();
        new TestMediaInfoBuilder(contextProvider, testExecutorLogger, SimpleMediaInfoUtils.getMediaInfoFile())
                .setCommandName("other1")
                .setInputDynamicParam("mediaInfoInput1")
                .setOutputDynamycParam("mediaInfoOutput1")
                .build(TestUtils.getTestFile());

        assertEquals(
                "START: External Process 1: MediaInfoCommandOtherType_test-file, TestExecuteOnceStrategy, mediaInfoOther1 FILE",
                testExecutorLogger.getNext());
        assertEquals(
                "FINISH: External Process 1: MediaInfoCommandOtherType_test-file, TestExecuteOnceStrategy, mediaInfoOther1 FILE",
                testExecutorLogger.getNext());

        assertFalse("There are more executed processes than expected!", testExecutorLogger.hasNext());
    }

    @Test
    public void testCorrectParseFormatTag() throws Exception {
        TemplateParameterContextProvider contextProvider = createContextProvider();

        // build media info
        TestExecutorLogger testExecutorLogger = new TestExecutorLogger();
        new TestMediaInfoBuilder(contextProvider, testExecutorLogger, SimpleMediaInfoUtils.getMediaInfoFile())
                .setCommandName("other1")
                .setInputDynamicParam("mediaInfoInput1")
                .setOutputDynamycParam("mediaInfoOutput1")
                .build(TestUtils.getTestFile());

        assertEquals(
                "START: External Process 1: MediaInfoCommandOtherType_test-file, TestExecuteOnceStrategy, mediaInfoOther1 FILE",
                testExecutorLogger.getNext());
        assertEquals(
                "FINISH: External Process 1: MediaInfoCommandOtherType_test-file, TestExecuteOnceStrategy, mediaInfoOther1 FILE",
                testExecutorLogger.getNext());

        assertFalse("There are more executed processes than expected!", testExecutorLogger.hasNext());
    }

    @Test
    public void testDynamicParametersFilled() throws Exception {
        TemplateParameterContextProvider contextProvider = createContextProvider();

        // build media info
        TestExecutorLogger testExecutorLogger = new TestExecutorLogger();
        new TestMediaInfoBuilder(contextProvider, testExecutorLogger, SimpleMediaInfoUtils.getMediaInfoFile())
                .setCommandName("other1")
                .setInputDynamicParam("mediaInfoInput1")
                .setOutputDynamycParam("mediaInfoOutput1")
                .build(TestUtils.getTestFile());

        DynamicTemplateParameterContext dynamicContext = contextProvider.getDynamicContext();

        assertEquals(TestUtils.getTestFile().getAbsolutePath(),
                dynamicContext.getParameterValueAsString("mediaInfoInput1"));
        assertEquals(SimpleMediaInfoUtils.getMediaInfoFile().getAbsolutePath(),
                dynamicContext.getParameterValueAsString("mediaInfoOutput1"));

        assertTrue(dynamicContext.getParameterValue("mediaInfoOutput1").isDeleteOnExit());
    }

    @Test(expected = FileNotFoundException.class)
    public void testOutputFileNotFound() throws Exception {
        TemplateParameterContextProvider contextProvider = createContextProvider();

        // build media info
        TestExecutorLogger testExecutorLogger = new TestExecutorLogger();
        new TestMediaInfoBuilder(contextProvider, testExecutorLogger, SimpleMediaInfoUtils.getMediaInfoFile())
                .setCommandName("other1")
                .setInputDynamicParam("mediaInfoInput1")
                .setOutputDynamycParam("mediaInfoOutput1")
                .build(new File("invalid-path"));
    }

    @Test(expected = MediaInfoException.class)
    public void testMediaInfoCommandNotFound() throws Exception {
        TemplateParameterContextProvider contextProvider = createContextProvider();

        // build media info
        TestExecutorLogger testExecutorLogger = new TestExecutorLogger();
        new TestMediaInfoBuilder(contextProvider, testExecutorLogger, SimpleMediaInfoUtils.getMediaInfoFile())
                .setCommandName("other3") // no such command
                .setInputDynamicParam("mediaInfoInput1")
                .setOutputDynamycParam("mediaInfoOutput1")
                .build(TestUtils.getTestFile());
    }

    private static class TestMediaInfoBuilder extends SimpleMediaInfoBuilder {

        private final File mediaInfoXml;

        public TestMediaInfoBuilder(TemplateParameterContextProvider contextProvider,
                                    TestExecutorLogger testExecutorLogger,
                                    File mediaInfoXml) {
            super(contextProvider, new TestExecuteStrategyFactory(testExecutorLogger));
            this.mediaInfoXml = mediaInfoXml;
        }

        @Override
        protected File getOutputFile() {
            return mediaInfoXml;
        }
    }
}
