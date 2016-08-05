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
public class ExecuteConversionOperationTest {

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
        next = 1;
    }

    @Test
    public void testSimpleExec() throws Exception {
        conversionEngine.convert(conversionProvider.getFormatConfigurationType("execOnce"), contextProvider);

        executorLogger.assertNextStart("execOnce1, TestExecuteOnceStrategy, execOnce1Exec ERR_LOG", next);
        executorLogger.assertNextFinish("execOnce1, TestExecuteOnceStrategy, execOnce1Exec ERR_LOG", next++);
        assertFalse("There are more executed processes than expected!", executorLogger.hasNext());
    }

    @Test
    public void testSimpleExecEachSeq() throws Exception {
        conversionEngine.convert(conversionProvider.getFormatConfigurationType("execEachSequence"), contextProvider);

        for (int i = 1; i <= SEQ_COUNT; i++) {
            executorLogger.assertNextStart("seqVideoExecOnce1, TestExecuteOnceStrategy, seqVideoExecOnce1Exec ERR_LOG", next);
            executorLogger.assertNextFinish("seqVideoExecOnce1, TestExecuteOnceStrategy, seqVideoExecOnce1Exec ERR_LOG", next++);

            for (int j = 1; j <= SEGMENT_COUNT * RESOURCE_COUNT * REPEAT_COUNT; j++) {
                executorLogger.assertNextStart("seqVideoExecSegment, TestExecuteOnceStrategy, seqVideoExecSegmentExec ERR_LOG", next);
                executorLogger.assertNextFinish("seqVideoExecSegment, TestExecuteOnceStrategy, seqVideoExecSegmentExec ERR_LOG", next++);
            }

            executorLogger.assertNextStart("seqVideoExecOnce2, TestExecuteOnceStrategy, seqVideoExecOnce2Exec ERR_LOG", next);
            executorLogger.assertNextFinish("seqVideoExecOnce2, TestExecuteOnceStrategy, seqVideoExecOnce2Exec ERR_LOG", next++);
        }

        assertFalse("There are more executed processes than expected!", executorLogger.hasNext());
    }

    @Test
    public void testSimpleExecEachSegm() throws Exception {
        conversionEngine.convert(conversionProvider.getFormatConfigurationType("execEachSegment"), contextProvider);

        for (int i = 1; i <= SEGMENT_COUNT; i++) {
            executorLogger.assertNextStart("segmExecOnce1, TestExecuteOnceStrategy, segmExecOnce1Exec ERR_LOG", next);
            executorLogger.assertNextFinish("segmExecOnce1, TestExecuteOnceStrategy, segmExecOnce1Exec ERR_LOG", next++);

            for (int j = 1; j <= SEQ_COUNT * RESOURCE_COUNT * REPEAT_COUNT; j++) {
                executorLogger.assertNextStart("segmAudioExecSeq, TestExecuteOnceStrategy, segmAudioExecSeqExecOnce ERR_LOG", next);
                executorLogger.assertNextFinish("segmAudioExecSeq, TestExecuteOnceStrategy, segmAudioExecSeqExecOnce ERR_LOG", next++);
            }

            executorLogger.assertNextStart("segmExecOnce2, TestExecuteOnceStrategy, segmExecOnce2Exec ERR_LOG", next);
            executorLogger.assertNextFinish("segmExecOnce2, TestExecuteOnceStrategy, segmExecOnce2Exec ERR_LOG", next++);
        }

        assertFalse("There are more executed processes than expected!", executorLogger.hasNext());
    }

    @Test
    public void testSimplePipe() throws Exception {
        conversionEngine.convert(conversionProvider.getFormatConfigurationType("execPipe"), contextProvider);

        int startPipe = next;
        executorLogger.assertNextStart("pipe4, TestExecutePipeStrategy, pipe4 ERR_LOG", next++);

        executorLogger.assertNextStart("cyclePipe1, TestExecutePipeStrategy, cyclePipe1 PIPE", next);
        executorLogger.assertNextFinish("cyclePipe1, TestExecutePipeStrategy, cyclePipe1 PIPE", next++);

        executorLogger.assertNextStart("cyclePipe2, TestExecutePipeStrategy, cyclePipe2 PIPE", next);
        executorLogger.assertNextFinish("cyclePipe2, TestExecutePipeStrategy, cyclePipe2 PIPE", next++);

        executorLogger.assertNextStart("cyclePipe3, TestExecutePipeStrategy, cyclePipe3 PIPE", next);
        executorLogger.assertNextFinish("cyclePipe3, TestExecutePipeStrategy, cyclePipe3 PIPE", next++);

        executorLogger.assertNextFinish("pipe4, TestExecutePipeStrategy, pipe4 ERR_LOG", startPipe);

        assertFalse("There are more executed processes than expected!", executorLogger.hasNext());
    }

    @Test
    public void testPipeSequence() throws Exception {
        conversionEngine.convert(conversionProvider.getFormatConfigurationType("execEachSequencePipe"), contextProvider);

        for (int i = 1; i <= SEQ_COUNT; i++) {
            int startPipe1 = next;
            executorLogger.assertNextStart("seqVideoPipeExecOnce1, TestExecutePipeStrategy, seqVideoPipeExecOnce1Exec PIPE", next++);
            int startPipe2 = next;
            executorLogger.assertNextStart("seqVideoPipeExecOnce2, TestExecutePipeStrategy, seqVideoPipeExecOnce2Exec PIPE", next++);
            int startPipe3 = next;
            executorLogger.assertNextStart("seqVideoPipeExecOnce3, TestExecutePipeStrategy, seqVideoPipeExecOnce3Exec ERR_LOG", next++);

            executorLogger.assertNextStart("seqVideoPipeCycleExecOnce1, TestExecutePipeStrategy, seqVideoPipeCycleExecOnce1Exec PIPE", next);
            executorLogger.assertNextFinish("seqVideoPipeCycleExecOnce1, TestExecutePipeStrategy, seqVideoPipeCycleExecOnce1Exec PIPE", next++);

            for (int j = 1; j <= SEGMENT_COUNT * RESOURCE_COUNT * REPEAT_COUNT; j++) {
                executorLogger.assertNextStart("seqVideoPipeCycleExecSegment1, TestExecutePipeStrategy, seqVideoPipeCycleExecSegmentExec1 PIPE", next);
                executorLogger.assertNextFinish("seqVideoPipeCycleExecSegment1, TestExecutePipeStrategy, seqVideoPipeCycleExecSegmentExec1 PIPE", next++);
            }

            for (int j = 1; j <= SEGMENT_COUNT * RESOURCE_COUNT * REPEAT_COUNT; j++) {
                executorLogger.assertNextStart("seqVideoPipeCycleExecSegment2, TestExecutePipeStrategy, seqVideoPipeCycleExecSegmentExec2 PIPE", next);
                executorLogger.assertNextFinish("seqVideoPipeCycleExecSegment2, TestExecutePipeStrategy, seqVideoPipeCycleExecSegmentExec2 PIPE", next++);
            }

            executorLogger.assertNextFinish("seqVideoPipeExecOnce1, TestExecutePipeStrategy, seqVideoPipeExecOnce1Exec PIPE", startPipe1);
            executorLogger.assertNextFinish("seqVideoPipeExecOnce2, TestExecutePipeStrategy, seqVideoPipeExecOnce2Exec PIPE", startPipe2);
            executorLogger.assertNextFinish("seqVideoPipeExecOnce3, TestExecutePipeStrategy, seqVideoPipeExecOnce3Exec ERR_LOG", startPipe3);
        }

        assertFalse("There are more executed processes than expected!", executorLogger.hasNext());
    }

    @Test
    public void testPipeSegment() throws Exception {
        conversionEngine.convert(conversionProvider.getFormatConfigurationType("execEachSegmentPipe"), contextProvider);

        for (int i = 1; i <= SEGMENT_COUNT; i++) {
            int startPipe1 = next;
            executorLogger.assertNextStart("segmPipeExecOnce1, TestExecutePipeStrategy, segmPipeExecOnceExec1 PIPE", next++);
            int startPipe2 = next;
            executorLogger.assertNextStart("segmPipeExecOnce2, TestExecutePipeStrategy, segmPipeExecOnceExec2 PIPE", next++);
            int startPipe3 = next;
            executorLogger.assertNextStart("segmPipeExecOnce3, TestExecutePipeStrategy, segmPipeExecOnceExec3 ERR_LOG", next++);

            executorLogger.assertNextStart("segmPipeCycleExecOnce1, TestExecutePipeStrategy, segmPipeCycleExecOnce1 PIPE", next);
            executorLogger.assertNextFinish("segmPipeCycleExecOnce1, TestExecutePipeStrategy, segmPipeCycleExecOnce1 PIPE", next++);

            for (int j = 1; j <= SEQ_COUNT * RESOURCE_COUNT * REPEAT_COUNT; j++) {
                executorLogger.assertNextStart("segmVideoPipeCycleExecSeq1, TestExecutePipeStrategy, segmVideoPipeCycleExecSegmentExec1 PIPE", next);
                executorLogger.assertNextFinish("segmVideoPipeCycleExecSeq1, TestExecutePipeStrategy, segmVideoPipeCycleExecSegmentExec1 PIPE", next++);
            }

            for (int j = 1; j <= SEQ_COUNT * RESOURCE_COUNT * REPEAT_COUNT; j++) {
                executorLogger.assertNextStart("segmVideoPipeCycleExecSeq2, TestExecutePipeStrategy, segmVideoPipeCycleExecSegmentExec2 PIPE", next);
                executorLogger.assertNextFinish("segmVideoPipeCycleExecSeq2, TestExecutePipeStrategy, segmVideoPipeCycleExecSegmentExec2 PIPE", next++);
            }

            executorLogger.assertNextFinish("segmPipeExecOnce1, TestExecutePipeStrategy, segmPipeExecOnceExec1 PIPE", startPipe1);
            executorLogger.assertNextFinish("segmPipeExecOnce2, TestExecutePipeStrategy, segmPipeExecOnceExec2 PIPE", startPipe2);
            executorLogger.assertNextFinish("segmPipeExecOnce3, TestExecutePipeStrategy, segmPipeExecOnceExec3 ERR_LOG", startPipe3);
        }

        assertFalse("There are more executed processes than expected!", executorLogger.hasNext());
    }

    @Test
    public void testComplex() throws Exception {
        conversionEngine.convert(conversionProvider.getFormatConfigurationType("complex"), contextProvider);

        executorLogger.assertNextStart("execOnce1, TestExecuteOnceStrategy, execOnce1Exec ERR_LOG", next);
        executorLogger.assertNextFinish("execOnce1, TestExecuteOnceStrategy, execOnce1Exec ERR_LOG", next++);

        for (int i = 1; i <= SEQ_COUNT; i++) {

            executorLogger.assertNextStart("seqVideoExecOnce1, TestExecuteOnceStrategy, seqVideoExecOnce1Exec ERR_LOG", next);
            executorLogger.assertNextFinish("seqVideoExecOnce1, TestExecuteOnceStrategy, seqVideoExecOnce1Exec ERR_LOG", next++);

            // start pipe

            //  start

            int startPipe1 = next;
            executorLogger.assertNextStart("seqVideoPipeExecOnce1, TestExecutePipeStrategy, seqVideoPipeExecOnce1Exec PIPE", next++);
            int startPipe2 = next;
            executorLogger.assertNextStart("seqVideoPipeExecOnce2, TestExecutePipeStrategy, seqVideoPipeExecOnce2Exec ERR_LOG", next++);

            // pipe cycle
            executorLogger.assertNextStart("seqVideoPipeCycleExecOnce1, TestExecutePipeStrategy, seqVideoPipeCycleExecOnce1Exec PIPE", next);
            executorLogger.assertNextFinish("seqVideoPipeCycleExecOnce1, TestExecutePipeStrategy, seqVideoPipeCycleExecOnce1Exec PIPE", next++);

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

        assertFalse("There are more executed processes than expected!", executorLogger.hasNext());
    }

}
