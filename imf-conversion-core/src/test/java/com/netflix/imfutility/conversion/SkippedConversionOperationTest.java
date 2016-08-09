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
import com.netflix.imfutility.conversion.executor.strategy.AbstractExecuteStrategy;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.util.ConfigUtils;
import com.netflix.imfutility.util.ConversionUtils;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import com.netflix.imfutility.util.conversion.executor.TestConversionEngine;
import com.netflix.imfutility.util.conversion.executor.TestExecutorLogger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.assertFalse;

/**
 * Tests that external processes are started and executed in a correct order.
 */
public class SkippedConversionOperationTest {

    private static final int SEGMENT_COUNT = 3;
    private static final int SEQ_COUNT = 2;
    private static final int RESOURCE_COUNT = 2;
    private static final int REPEAT_COUNT = 2;

    private static ConversionXmlProvider conversionProvider;
    private static TemplateParameterContextProvider contextProvider;

    private static TestConversionEngine conversionEngine;
    private static TestExecutorLogger executorLogger;

    private int next;

    @BeforeClass
    public static void setUpAll() throws Exception {
        initContext();
        TemplateParameterContextCreator.fillCPLContext(contextProvider,
                SEGMENT_COUNT,
                SEQ_COUNT,
                RESOURCE_COUNT,
                REPEAT_COUNT,
                EnumSet.of(SequenceType.VIDEO, SequenceType.AUDIO)); // do not fill subtitle type!

        conversionEngine = new TestConversionEngine();
        executorLogger = conversionEngine.getExecutorLogger();
    }

    private static void initContext() throws Exception {
        conversionProvider = new ConversionXmlProvider(ConversionUtils.getCorrectConversionXmlSkipped(),
                ConversionUtils.getCorrectConversionXmlSkippedPath(), new FakeFormat());
        ConfigXmlProvider configProvider = new ConfigXmlProvider(ConfigUtils.getCorrectConfigXml(),
                ConfigUtils.getCorrectConfigXmlPath());
        contextProvider = new TemplateParameterContextProvider(configProvider, conversionProvider,
                TemplateParameterContextCreator.getCurrentTmpDir());
    }

    @Before
    public void setUp() {
        executorLogger.reset();
        AbstractExecuteStrategy.resetCount();
        next = 1;
    }

    @Test
    public void testComplex() throws Exception {
        conversionEngine.convert(conversionProvider.getFormatConfigurationType("skippedComplex"), contextProvider);

        executorLogger.assertNextStart("execOnce1, TestExecuteOnceStrategy, execOnce1Exec ERR_LOG", next);
        executorLogger.assertNextFinish("execOnce1, TestExecuteOnceStrategy, execOnce1Exec ERR_LOG", next++);

        for (int i = 1; i <= SEQ_COUNT; i++) {
            executorLogger.assertNextStart("seqVideoExecOnce1, TestExecuteOnceStrategy, seqVideoExecOnce1Exec ERR_LOG", next);
            executorLogger.assertNextFinish("seqVideoExecOnce1, TestExecuteOnceStrategy, seqVideoExecOnce1Exec ERR_LOG", next++);
            executorLogger.assertSkipped("seqVideoExecOnceSkip [ seqVideoExecOnceSkipExec - param ]");

            // start pipe

            // skip operations
            // we have 2 segments and 2 resources in each segment which is repeated to 2 times
            for (int j = 1; j <= SEGMENT_COUNT * RESOURCE_COUNT * REPEAT_COUNT; j++) {
                executorLogger.assertSkipped("seqVideoPipeCycleExecSegmentSkip [ seqVideoPipeCycleExecSegmentSkipExec - param ]");
            }
            executorLogger.assertSkipped("seqVideoPipeExecOnceSkip [ seqVideoPipeExecOnceSkipExec - param ]");

            //  start

            int startPipe1 = next;
            executorLogger.assertNextStart("seqVideoPipeExecOnce1, TestExecutePipeStrategy, seqVideoPipeExecOnce1Exec PIPE", next++);
            int startPipe2 = next;
            executorLogger.assertNextStart("seqVideoPipeExecOnce2, TestExecutePipeStrategy, seqVideoPipeExecOnce2Exec ERR_LOG", next++);

            // pipe cycle
            executorLogger.assertNextStart("seqVideoPipeCycleExecOnce1, TestExecutePipeStrategy, seqVideoPipeCycleExecOnce1Exec PIPE", next);
            executorLogger.assertNextFinish("seqVideoPipeCycleExecOnce1, TestExecutePipeStrategy, seqVideoPipeCycleExecOnce1Exec PIPE", next++);

            // we have 2 segments and 2 resources in each segment which is repeated to 2 times
            for (int j = 1; j <= SEGMENT_COUNT * RESOURCE_COUNT * REPEAT_COUNT; j++) {
                executorLogger.assertNextStart("seqVideoPipeCycleExecSegment, TestExecutePipeStrategy, seqVideoPipeCycleExecSegmentExec PIPE", next);
                executorLogger.assertNextFinish("seqVideoPipeCycleExecSegment, TestExecutePipeStrategy, seqVideoPipeCycleExecSegmentExec PIPE", next++);
            }

            // finish pipe
            executorLogger.assertNextFinish("seqVideoPipeExecOnce1, TestExecutePipeStrategy, seqVideoPipeExecOnce1Exec PIPE", startPipe1);
            executorLogger.assertNextFinish("seqVideoPipeExecOnce2, TestExecutePipeStrategy, seqVideoPipeExecOnce2Exec ERR_LOG", startPipe2);

            executorLogger.assertNextStart("seqVideoExecOnce2, TestExecuteOnceStrategy, seqVideoExecOnce2Exec ERR_LOG", next);
            executorLogger.assertNextFinish("seqVideoExecOnce2, TestExecuteOnceStrategy, seqVideoExecOnce2Exec ERR_LOG", next++);
        }

        executorLogger.assertNextStart("execOnce2, TestExecuteOnceStrategy, execOnce2Exec ERR_LOG", next);
        executorLogger.assertNextFinish("execOnce2, TestExecuteOnceStrategy, execOnce2Exec ERR_LOG", next++);

    }

    @Test
    public void testSkipAllInPipeCycle() throws Exception {
        conversionEngine.convert(conversionProvider.getFormatConfigurationType("skippedPipeAllCycle"), contextProvider);
        //  Test sequence with 1 noncycle exec (other must be skipped)

        for (int i = 1; i <= SEQ_COUNT; i++) {
            // start pipe
            // skip operations
            // we have 2 segments and 2 resources in each segment which is repeated to 2 times
            for (int j = 1; j <= SEGMENT_COUNT * RESOURCE_COUNT * REPEAT_COUNT; j++) {
                executorLogger.assertSkipped("seqAudioPipeCycleExecSeq [ seqAudioPipeCycleExecSeqExecOnceExec -param ]");
            }
            //start
            executorLogger.assertNextStart("seqAudioPipeExecOnce, TestExecutePipeStrategy, seqAudioPipeExecOnceExec ERR_LOG", next);
            // finish pipe
            executorLogger.assertNextFinish("seqAudioPipeExecOnce, TestExecutePipeStrategy, seqAudioPipeExecOnceExec ERR_LOG", next++);
        }

    }

    @Test
    public void testSkipAllInPipe() throws Exception {
        conversionEngine.convert(conversionProvider.getFormatConfigurationType("skippedAllPipe"), contextProvider);
        //  Test sequence with all skipped operations
        //  Log skipped operations only

        for (int i = 1; i <= SEQ_COUNT; i++) {
            for (int j = 1; j <= SEGMENT_COUNT * RESOURCE_COUNT * REPEAT_COUNT; j++) {
                executorLogger.assertSkipped("seqSubtitlePipeCycleExecSeq [ seqSubtitlePipeCycleExecSeqExecOnceExec -param ]");
            }
            executorLogger.assertSkipped("seqSubtitlePipeExecOnce [ seqSubtitlePipeExecOnceExec - param ]");
        }

        assertFalse("There are more executed processes than expected!", executorLogger.hasNext());
    }

}
