package com.netflix.imfutility.conversion;

import com.netflix.imfutility.ConfigProvider;
import com.netflix.imfutility.Format;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.util.ConfigUtils;
import com.netflix.imfutility.util.ConversionUtils;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import com.netflix.imfutility.util.conversion.executor.TestConversionEngine;
import com.netflix.imfutility.util.conversion.executor.TestExecutorLogger;
import com.netflix.imfutility.xsd.conversion.SequenceType;
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

    private static ConversionProvider conversionProvider;
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
                EnumSet.of(SequenceType.VIDEO, SequenceType.AUDIO)); // do not fill subtitle type!

        conversionEngine = new TestConversionEngine();
        executorLogger = conversionEngine.getExecutorLogger();
    }

    private static void initContext() throws Exception {
        conversionProvider = new ConversionProvider(ConversionUtils.getCorrectConversionXml(), Format.DPP);
        ConfigProvider configProvider = new ConfigProvider(ConfigUtils.getCorrectConfigXml());
        contextProvider = new TemplateParameterContextProvider(configProvider.getConfig(), conversionProvider.getFormat(), ".");
    }

    @Before
    public void setUp() throws Exception {
        executorLogger.reset();
    }

    @Test
    // TODO: create more specific and granulated tests
    public void testExec() throws Exception {
        conversionEngine.convert(conversionProvider.getFormat(), "1", contextProvider);

        assertEquals("START: External Process 1: execOnce1, ExecOnceType, execOnce1Exec", executorLogger.getNext());
        assertEquals("FINISH: External Process 1: execOnce1, ExecOnceType, execOnce1Exec", executorLogger.getNext());

        // Start Sequence 0:

        assertEquals("START: External Process 2: seqVideoExecOnce1, ExecOnceType, seqVideoExecOnce1Exec", executorLogger.getNext());
        assertEquals("FINISH: External Process 2: seqVideoExecOnce1, ExecOnceType, seqVideoExecOnce1Exec", executorLogger.getNext());

        // start pipe
        assertEquals("START: External Process 3: seqVideoPipeExecOnce1, ExecOnceType, seqVideoPipeExecOnce1Exec", executorLogger.getNext());
        assertEquals("START: External Process 4: seqVideoPipeExecOnce2, ExecOnceType, seqVideoPipeExecOnce2Exec", executorLogger.getNext());

        // pipe cycle
        assertEquals("START: External Process 5: seqVideoPipeCycleExecOnce1, ExecOnceType, seqVideoPipeCycleExecOnce1Exec", executorLogger.getNext());
        assertEquals("FINISH: External Process 5: seqVideoPipeCycleExecOnce1, ExecOnceType, seqVideoPipeCycleExecOnce1Exec", executorLogger.getNext());

        // we have 2 segments and 2 resources in each segment
        for (int i = 1; i <= SEGMENT_COUNT * RESOURCE_COUNT; i++) {
            assertEquals(String.format(
                    "START: External Process %d: seqVideoPipeCycleExecSegment, ExecEachSegmentType, seqVideoPipeCycleExecSegmentExec",
                    5 + i),
                    executorLogger.getNext());
            assertEquals(String.format(
                    "FINISH: External Process %d: seqVideoPipeCycleExecSegment, ExecEachSegmentType, seqVideoPipeCycleExecSegmentExec",
                    5 + i),
                    executorLogger.getNext());
        }

        // finish pipe
        assertEquals("FINISH: External Process 3: seqVideoPipeExecOnce1, ExecOnceType, seqVideoPipeExecOnce1Exec", executorLogger.getNext());
        assertEquals("FINISH: External Process 4: seqVideoPipeExecOnce2, ExecOnceType, seqVideoPipeExecOnce2Exec", executorLogger.getNext());

        assertEquals("START: External Process 10: seqVideoExecOnce2, ExecOnceType, seqVideoExecOnce2Exec", executorLogger.getNext());
        assertEquals("FINISH: External Process 10: seqVideoExecOnce2, ExecOnceType, seqVideoExecOnce2Exec", executorLogger.getNext());

        // End Sequence 0:

        // Start Sequence 1:

        assertEquals("START: External Process 11: seqVideoExecOnce1, ExecOnceType, seqVideoExecOnce1Exec", executorLogger.getNext());
        assertEquals("FINISH: External Process 11: seqVideoExecOnce1, ExecOnceType, seqVideoExecOnce1Exec", executorLogger.getNext());

        // start pipe
        assertEquals("START: External Process 12: seqVideoPipeExecOnce1, ExecOnceType, seqVideoPipeExecOnce1Exec", executorLogger.getNext());
        assertEquals("START: External Process 13: seqVideoPipeExecOnce2, ExecOnceType, seqVideoPipeExecOnce2Exec", executorLogger.getNext());

        // pipe cycle
        assertEquals("START: External Process 14: seqVideoPipeCycleExecOnce1, ExecOnceType, seqVideoPipeCycleExecOnce1Exec", executorLogger.getNext());
        assertEquals("FINISH: External Process 14: seqVideoPipeCycleExecOnce1, ExecOnceType, seqVideoPipeCycleExecOnce1Exec", executorLogger.getNext());

        // we have 2 segments and 2 resources in each segment
        for (int i = 1; i <= SEGMENT_COUNT * RESOURCE_COUNT; i++) {
            assertEquals(String.format(
                    "START: External Process %d: seqVideoPipeCycleExecSegment, ExecEachSegmentType, seqVideoPipeCycleExecSegmentExec",
                    14 + i),
                    executorLogger.getNext());
            assertEquals(String.format(
                    "FINISH: External Process %d: seqVideoPipeCycleExecSegment, ExecEachSegmentType, seqVideoPipeCycleExecSegmentExec",
                    14 + i),
                    executorLogger.getNext());
        }

        // finish pipe
        assertEquals("FINISH: External Process 12: seqVideoPipeExecOnce1, ExecOnceType, seqVideoPipeExecOnce1Exec", executorLogger.getNext());
        assertEquals("FINISH: External Process 13: seqVideoPipeExecOnce2, ExecOnceType, seqVideoPipeExecOnce2Exec", executorLogger.getNext());

        assertEquals("START: External Process 19: seqVideoExecOnce2, ExecOnceType, seqVideoExecOnce2Exec", executorLogger.getNext());
        assertEquals("FINISH: External Process 19: seqVideoExecOnce2, ExecOnceType, seqVideoExecOnce2Exec", executorLogger.getNext());

        // End Sequence 1

        assertEquals("START: External Process 20: execOnce2, ExecOnceType, execOnce2Exec", executorLogger.getNext());
        assertEquals("FINISH: External Process 20: execOnce2, ExecOnceType, execOnce2Exec", executorLogger.getNext());

        assertFalse("There are more executed processes than expected!", executorLogger.hasNext());
    }


}
