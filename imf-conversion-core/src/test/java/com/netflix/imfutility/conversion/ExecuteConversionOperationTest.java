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

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests that external processes are started and executed in a correct order.
 */
public class ExecuteConversionOperationTest {

    private static final int SEGMENT_COUNT = 2;
    private static final int SEQ_COUNT = 2;
    private static final int RESOURCE_COUNT = 2;
    private static final int REPEAT_COUNT = 2;

    private static ConversionXmlProvider conversionProvider;
    private static TemplateParameterContextProvider contextProvider;

    private static TestConversionEngine conversionEngine;
    private static TestExecutorLogger executorLogger;

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
        conversionProvider = new ConversionXmlProvider(ConversionUtils.getCorrectConversionXml(),
                ConversionUtils.getCorrectConversionXmlPath(), new FakeFormat());
        ConfigXmlProvider configProvider = new ConfigXmlProvider(ConfigUtils.getCorrectConfigXml(),
                ConfigUtils.getCorrectConfigXmlPath());
        contextProvider = new TemplateParameterContextProvider(configProvider, conversionProvider,
                TemplateParameterContextCreator.getCurrentTmpDir());
    }

    @Before
    public void setUp() {
        executorLogger.reset();
        AbstractExecuteStrategy.resetCount();
    }

    @Test
    public void testExec() throws Exception {
        conversionEngine.convert(conversionProvider.getFormatConfigurationType("1"), contextProvider);

        assertEquals("START: External Process 1: execOnce1, TestExecuteOnceStrategy, execOnce1Exec ERR_LOG",
                executorLogger.getNext());
        assertEquals("FINISH: External Process 1: execOnce1, TestExecuteOnceStrategy, execOnce1Exec ERR_LOG",
                executorLogger.getNext());

        // Start Sequence 0:

        assertEquals("START: External Process 2: seqVideoExecOnce1, TestExecuteOnceStrategy, seqVideoExecOnce1Exec ERR_LOG",
                executorLogger.getNext());
        assertEquals("FINISH: External Process 2: seqVideoExecOnce1, TestExecuteOnceStrategy, seqVideoExecOnce1Exec ERR_LOG",
                executorLogger.getNext());
        assertEquals("SKIPPED: seqVideoExecOnceSkip [ seqVideoExecOnceSkipExec - param ]",
                executorLogger.getNext());

        // start pipe

        // skip operations
        // we have 2 segments and 2 resources in each segment which is repeated to 2 times
        for (int i = 1; i <= SEGMENT_COUNT * RESOURCE_COUNT * REPEAT_COUNT; i++) {
            assertEquals("SKIPPED: seqVideoPipeCycleExecSegmentSkip [ seqVideoPipeCycleExecSegmentSkipExec - param ]",
                    executorLogger.getNext());
        }
        assertEquals("SKIPPED: seqVideoPipeExecOnceSkip [ seqVideoPipeExecOnceSkipExec - param ]",
                executorLogger.getNext());

        //  start

        assertEquals("START: External Process 3: seqVideoPipeExecOnce1, TestExecutePipeStrategy, seqVideoPipeExecOnce1Exec PIPE",
                executorLogger.getNext());
        assertEquals("START: External Process 4: seqVideoPipeExecOnce2, TestExecutePipeStrategy, seqVideoPipeExecOnce2Exec ERR_LOG",
                executorLogger.getNext());

        // pipe cycle
        assertEquals("START: External Process 5: seqVideoPipeCycleExecOnce1, TestExecutePipeStrategy, seqVideoPipeCycleExecOnce1Exec PIPE",
                executorLogger.getNext());
        assertEquals("FINISH: External Process 5: seqVideoPipeCycleExecOnce1, TestExecutePipeStrategy, seqVideoPipeCycleExecOnce1Exec PIPE",
                executorLogger.getNext());

        // we have 2 segments and 2 resources in each segment which is repeated to 2 times
        for (int i = 1; i <= SEGMENT_COUNT * RESOURCE_COUNT * REPEAT_COUNT; i++) {
            assertEquals(String.format(
                    "START: External Process %d: seqVideoPipeCycleExecSegment, TestExecutePipeStrategy, seqVideoPipeCycleExecSegmentExec PIPE",
                    5 + i),
                    executorLogger.getNext());
            assertEquals(String.format(
                    "FINISH: External Process %d: seqVideoPipeCycleExecSegment, TestExecutePipeStrategy, seqVideoPipeCycleExecSegmentExec PIPE",
                    5 + i),
                    executorLogger.getNext());
        }

        // finish pipe
        assertEquals("FINISH: External Process 3: seqVideoPipeExecOnce1, TestExecutePipeStrategy, seqVideoPipeExecOnce1Exec PIPE",
                executorLogger.getNext());
        assertEquals("FINISH: External Process 4: seqVideoPipeExecOnce2, TestExecutePipeStrategy, seqVideoPipeExecOnce2Exec ERR_LOG",
                executorLogger.getNext());

        assertEquals("START: External Process 14: seqVideoExecOnce2, TestExecuteOnceStrategy, seqVideoExecOnce2Exec ERR_LOG",
                executorLogger.getNext());
        assertEquals("FINISH: External Process 14: seqVideoExecOnce2, TestExecuteOnceStrategy, seqVideoExecOnce2Exec ERR_LOG",
                executorLogger.getNext());

        // End Sequence 0:

        // Start Sequence 1:

        assertEquals("START: External Process 15: seqVideoExecOnce1, TestExecuteOnceStrategy, seqVideoExecOnce1Exec ERR_LOG",
                executorLogger.getNext());
        assertEquals("FINISH: External Process 15: seqVideoExecOnce1, TestExecuteOnceStrategy, seqVideoExecOnce1Exec ERR_LOG",
                executorLogger.getNext());
        assertEquals("SKIPPED: seqVideoExecOnceSkip [ seqVideoExecOnceSkipExec - param ]",
                executorLogger.getNext());

        // start pipe

        // skip operations
        // we have 2 segments and 2 resources in each segment which is repeated to 2 times
        for (int i = 1; i <= SEGMENT_COUNT * RESOURCE_COUNT * REPEAT_COUNT; i++) {
            assertEquals("SKIPPED: seqVideoPipeCycleExecSegmentSkip [ seqVideoPipeCycleExecSegmentSkipExec - param ]",
                    executorLogger.getNext());
        }
        assertEquals("SKIPPED: seqVideoPipeExecOnceSkip [ seqVideoPipeExecOnceSkipExec - param ]",
                executorLogger.getNext());

        //  start

        assertEquals("START: External Process 16: seqVideoPipeExecOnce1, TestExecutePipeStrategy, seqVideoPipeExecOnce1Exec PIPE",
                executorLogger.getNext());
        assertEquals("START: External Process 17: seqVideoPipeExecOnce2, TestExecutePipeStrategy, seqVideoPipeExecOnce2Exec ERR_LOG",
                executorLogger.getNext());

        // pipe cycle
        assertEquals("START: External Process 18: seqVideoPipeCycleExecOnce1, TestExecutePipeStrategy, seqVideoPipeCycleExecOnce1Exec PIPE",
                executorLogger.getNext());
        assertEquals("FINISH: External Process 18: seqVideoPipeCycleExecOnce1, TestExecutePipeStrategy, seqVideoPipeCycleExecOnce1Exec PIPE",
                executorLogger.getNext());

        // we have 2 segments and 2 resources in each segment which is repeated to 2 times
        for (int i = 1; i <= SEGMENT_COUNT * RESOURCE_COUNT * REPEAT_COUNT; i++) {
            assertEquals(String.format(
                    "START: External Process %d: seqVideoPipeCycleExecSegment, TestExecutePipeStrategy, seqVideoPipeCycleExecSegmentExec PIPE",
                    18 + i),
                    executorLogger.getNext());
            assertEquals(String.format(
                    "FINISH: External Process %d: seqVideoPipeCycleExecSegment, TestExecutePipeStrategy, seqVideoPipeCycleExecSegmentExec PIPE",
                    18 + i),
                    executorLogger.getNext());
        }

        // finish pipe
        assertEquals("FINISH: External Process 16: seqVideoPipeExecOnce1, TestExecutePipeStrategy, seqVideoPipeExecOnce1Exec PIPE",
                executorLogger.getNext());
        assertEquals("FINISH: External Process 17: seqVideoPipeExecOnce2, TestExecutePipeStrategy, seqVideoPipeExecOnce2Exec ERR_LOG",
                executorLogger.getNext());

        assertEquals("START: External Process 27: seqVideoExecOnce2, TestExecuteOnceStrategy, seqVideoExecOnce2Exec ERR_LOG",
                executorLogger.getNext());
        assertEquals("FINISH: External Process 27: seqVideoExecOnce2, TestExecuteOnceStrategy, seqVideoExecOnce2Exec ERR_LOG",
                executorLogger.getNext());

        // End Sequence 1

        assertEquals("START: External Process 28: execOnce2, TestExecuteOnceStrategy, execOnce2Exec ERR_LOG",
                executorLogger.getNext());
        assertEquals("FINISH: External Process 28: execOnce2, TestExecuteOnceStrategy, execOnce2Exec ERR_LOG",
                executorLogger.getNext());

        //  Test sequence with 1 noncycle exec (other must be skipped)

        // Start Sequence 0:

        // start pipe
        // skip operations
        // we have 2 segments and 2 resources in each segment which is repeated to 2 times
        for (int i = 1; i <= SEGMENT_COUNT * RESOURCE_COUNT * REPEAT_COUNT; i++) {
            assertEquals("SKIPPED: seqAudioPipeCycleExecSeq [ seqAudioPipeCycleExecSeqExecOnceExec -param ]",
                    executorLogger.getNext());
        }
        //start
        assertEquals("START: External Process 29: seqAudioPipeExecOnce, TestExecutePipeStrategy, seqAudioPipeExecOnceExec ERR_LOG",
                executorLogger.getNext());
        // finish pipe
        assertEquals("FINISH: External Process 29: seqAudioPipeExecOnce, TestExecutePipeStrategy, seqAudioPipeExecOnceExec ERR_LOG",
                executorLogger.getNext());

        // End Sequence 0

        // Start Sequence 1:

        // start pipe
        // skip operations
        // we have 2 segments and 2 resources in each segment which is repeated to 2 times
        for (int i = 1; i <= SEGMENT_COUNT * RESOURCE_COUNT * REPEAT_COUNT; i++) {
            assertEquals("SKIPPED: seqAudioPipeCycleExecSeq [ seqAudioPipeCycleExecSeqExecOnceExec -param ]",
                    executorLogger.getNext());
        }
        //start
        assertEquals("START: External Process 30: seqAudioPipeExecOnce, TestExecutePipeStrategy, seqAudioPipeExecOnceExec ERR_LOG",
                executorLogger.getNext());
        // finish pipe
        assertEquals("FINISH: External Process 30: seqAudioPipeExecOnce, TestExecutePipeStrategy, seqAudioPipeExecOnceExec ERR_LOG",
                executorLogger.getNext());

        // End Sequence 1

        //  Test sequence with all skipped operations
        //  Log skipped operations only

        // Start Sequence 0:
        for (int i = 1; i <= SEGMENT_COUNT * RESOURCE_COUNT * REPEAT_COUNT; i++) {
            assertEquals("SKIPPED: seqSubtitlePipeCycleExecSeq [ seqSubtitlePipeCycleExecSeqExecOnceExec -param ]",
                    executorLogger.getNext());
        }
        assertEquals("SKIPPED: seqSubtitlePipeExecOnce [ seqSubtitlePipeExecOnceExec - param ]",
                executorLogger.getNext());
        // End Sequence 0

        // Start Sequence 1:
        for (int i = 1; i <= SEGMENT_COUNT * RESOURCE_COUNT * REPEAT_COUNT; i++) {
            assertEquals("SKIPPED: seqSubtitlePipeCycleExecSeq [ seqSubtitlePipeCycleExecSeqExecOnceExec -param ]",
                    executorLogger.getNext());
        }
        assertEquals("SKIPPED: seqSubtitlePipeExecOnce [ seqSubtitlePipeExecOnceExec - param ]",
                executorLogger.getNext());
        // End Sequence 1

        assertFalse("There are more executed processes than expected!", executorLogger.hasNext());
    }


}
