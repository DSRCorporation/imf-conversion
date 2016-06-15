package com.netflix.imfutility.conversion;

import com.netflix.imfutility.Format;
import com.netflix.imfutility.config.ConfigXmlProvider;
import com.netflix.imfutility.conversion.executor.strategy.AbstractExecuteStrategy;
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
        conversionProvider = new ConversionXmlProvider(ConversionUtils.getCorrectConversionXml(), Format.DPP);
        ConfigXmlProvider configProvider = new ConfigXmlProvider(ConfigUtils.getCorrectConfigXml());
        contextProvider = new TemplateParameterContextProvider(configProvider, conversionProvider,
                TemplateParameterContextCreator.getCurrentTmpDir());
    }

    @Before
    public void setUp() throws Exception {
        executorLogger.reset();
        AbstractExecuteStrategy.resetCount();
    }

    @Test
    // TODO: create more specific and granulated tests
    public void testExec() throws Exception {
        conversionEngine.convert(conversionProvider.getFormatConfigurationType("1"), contextProvider);

        assertEquals("START: External Process 1: execOnce1, ExecuteOnceStrategy, execOnce1Exec", executorLogger.getNext());
        assertEquals("FINISH: External Process 1: execOnce1, ExecuteOnceStrategy, execOnce1Exec", executorLogger.getNext());

        // Start Sequence 0:

        assertEquals("START: External Process 2: seqVideoExecOnce1, ExecuteOnceStrategy, seqVideoExecOnce1Exec", executorLogger.getNext());
        assertEquals("FINISH: External Process 2: seqVideoExecOnce1, ExecuteOnceStrategy, seqVideoExecOnce1Exec", executorLogger.getNext());

        // start pipe
        assertEquals("START: External Process 3: seqVideoPipeExecOnce1, ExecutePipeStrategy, seqVideoPipeExecOnce1Exec", executorLogger.getNext());
        assertEquals("START: External Process 4: seqVideoPipeExecOnce2, ExecutePipeStrategy, seqVideoPipeExecOnce2Exec", executorLogger.getNext());

        // pipe cycle
        assertEquals("START: External Process 5: seqVideoPipeCycleExecOnce1, ExecutePipeStrategy, seqVideoPipeCycleExecOnce1Exec", executorLogger.getNext());
        assertEquals("FINISH: External Process 5: seqVideoPipeCycleExecOnce1, ExecutePipeStrategy, seqVideoPipeCycleExecOnce1Exec", executorLogger.getNext());

        // we have 2 segments and 2 resources in each segment which is repeated to 2 times
        for (int i = 1; i <= SEGMENT_COUNT * RESOURCE_COUNT * REPEAT_COUNT; i++) {
            assertEquals(String.format(
                    "START: External Process %d: seqVideoPipeCycleExecSegment, ExecutePipeStrategy, seqVideoPipeCycleExecSegmentExec",
                    5 + i),
                    executorLogger.getNext());
            assertEquals(String.format(
                    "FINISH: External Process %d: seqVideoPipeCycleExecSegment, ExecutePipeStrategy, seqVideoPipeCycleExecSegmentExec",
                    5 + i),
                    executorLogger.getNext());
        }

        // finish pipe
        assertEquals("FINISH: External Process 3: seqVideoPipeExecOnce1, ExecutePipeStrategy, seqVideoPipeExecOnce1Exec", executorLogger.getNext());
        assertEquals("FINISH: External Process 4: seqVideoPipeExecOnce2, ExecutePipeStrategy, seqVideoPipeExecOnce2Exec", executorLogger.getNext());

        assertEquals("START: External Process 14: seqVideoExecOnce2, ExecuteOnceStrategy, seqVideoExecOnce2Exec", executorLogger.getNext());
        assertEquals("FINISH: External Process 14: seqVideoExecOnce2, ExecuteOnceStrategy, seqVideoExecOnce2Exec", executorLogger.getNext());

        // End Sequence 0:

        // Start Sequence 1:

        assertEquals("START: External Process 15: seqVideoExecOnce1, ExecuteOnceStrategy, seqVideoExecOnce1Exec", executorLogger.getNext());
        assertEquals("FINISH: External Process 15: seqVideoExecOnce1, ExecuteOnceStrategy, seqVideoExecOnce1Exec", executorLogger.getNext());

        // start pipe
        assertEquals("START: External Process 16: seqVideoPipeExecOnce1, ExecutePipeStrategy, seqVideoPipeExecOnce1Exec", executorLogger.getNext());
        assertEquals("START: External Process 17: seqVideoPipeExecOnce2, ExecutePipeStrategy, seqVideoPipeExecOnce2Exec", executorLogger.getNext());

        // pipe cycle
        assertEquals("START: External Process 18: seqVideoPipeCycleExecOnce1, ExecutePipeStrategy, seqVideoPipeCycleExecOnce1Exec", executorLogger.getNext());
        assertEquals("FINISH: External Process 18: seqVideoPipeCycleExecOnce1, ExecutePipeStrategy, seqVideoPipeCycleExecOnce1Exec", executorLogger.getNext());

        // we have 2 segments and 2 resources in each segment which is repeated to 2 times
        for (int i = 1; i <= SEGMENT_COUNT * RESOURCE_COUNT * REPEAT_COUNT; i++) {
            assertEquals(String.format(
                    "START: External Process %d: seqVideoPipeCycleExecSegment, ExecutePipeStrategy, seqVideoPipeCycleExecSegmentExec",
                    18 + i),
                    executorLogger.getNext());
            assertEquals(String.format(
                    "FINISH: External Process %d: seqVideoPipeCycleExecSegment, ExecutePipeStrategy, seqVideoPipeCycleExecSegmentExec",
                    18 + i),
                    executorLogger.getNext());
        }

        // finish pipe
        assertEquals("FINISH: External Process 16: seqVideoPipeExecOnce1, ExecutePipeStrategy, seqVideoPipeExecOnce1Exec", executorLogger.getNext());
        assertEquals("FINISH: External Process 17: seqVideoPipeExecOnce2, ExecutePipeStrategy, seqVideoPipeExecOnce2Exec", executorLogger.getNext());

        assertEquals("START: External Process 27: seqVideoExecOnce2, ExecuteOnceStrategy, seqVideoExecOnce2Exec", executorLogger.getNext());
        assertEquals("FINISH: External Process 27: seqVideoExecOnce2, ExecuteOnceStrategy, seqVideoExecOnce2Exec", executorLogger.getNext());

        // End Sequence 1

        assertEquals("START: External Process 28: execOnce2, ExecuteOnceStrategy, execOnce2Exec", executorLogger.getNext());
        assertEquals("FINISH: External Process 28: execOnce2, ExecuteOnceStrategy, execOnce2Exec", executorLogger.getNext());

        assertFalse("There are more executed processes than expected!", executorLogger.hasNext());
    }


}
