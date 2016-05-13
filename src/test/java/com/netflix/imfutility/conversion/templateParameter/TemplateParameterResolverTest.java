package com.netflix.imfutility.conversion.templateParameter;

import com.netflix.imfutility.ConfigProvider;
import com.netflix.imfutility.Format;
import com.netflix.imfutility.conversion.ConversionProvider;
import com.netflix.imfutility.conversion.templateParameter.context.DynamicTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.OutputTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.exception.InvalidTemplateParameterException;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;
import com.netflix.imfutility.conversion.templateParameter.exception.UnknownTemplateParameterContextException;
import com.netflix.imfutility.conversion.templateParameter.exception.UnknownTemplateParameterNameException;
import com.netflix.imfutility.util.ConfigUtils;
import com.netflix.imfutility.util.ConversionUtils;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import com.netflix.imfutility.xsd.conversion.SequenceType;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * <ul>
 * <li>Tests the template parameters can be resolved successfully for each context.</li>
 * <li>Tests that expected exceptions are thrown if template parameters can not be resolved due to different reasons.</li>
 * </ul>
 */
public class TemplateParameterResolverTest {

    private static final int SEGMENT_COUNT = 2;
    private static final int SEQ_COUNT = 2;
    private static final int RESOURCE_COUNT = 2;
    private static TemplateParameterResolver resolver;

    @BeforeClass
    public static void setUpAll() throws Exception {
        ConfigProvider configProvider = new ConfigProvider(ConfigUtils.getCorrectConfigXml());
        ConversionProvider conversionProvider = new ConversionProvider(ConversionUtils.getCorrectConversionXml(), Format.DPP);

        TemplateParameterContextProvider contextProvider = new TemplateParameterContextProvider(
                configProvider.getConfig(), conversionProvider.getFormat(), ".");
        fillDynamic(contextProvider);
        fillOutput(contextProvider);
        TemplateParameterContextCreator.fillCPLContext(contextProvider,
                SEGMENT_COUNT,
                SEQ_COUNT,
                RESOURCE_COUNT,
                EnumSet.of(SequenceType.VIDEO, SequenceType.AUDIO)); // do not fill subtitle type!

        resolver = new TemplateParameterResolver(contextProvider);
    }

    private static void fillDynamic(TemplateParameterContextProvider contextProvider) {
        DynamicTemplateParameterContext dynamicContext = contextProvider.getDynamicContext();
        dynamicContext.addParameter("dynamic1", "dynamicValue1", ContextInfo.EMPTY);
        dynamicContext.addParameter("dynamic2", "dynamicValue2", ContextInfo.EMPTY);
    }

    private static void fillOutput(TemplateParameterContextProvider contextProvider) {
        OutputTemplateParameterContext outputContext = contextProvider.getOutputContext();
        outputContext.addParameter("output1", "outputValue1");
        outputContext.addParameter("output2", "outputValue2");
    }

    @Test
    public void resolvesCorrectToolsContext() {
        String resolved = resolver.resolveTemplateParameter("%{tool.toolSimple}", ContextInfo.EMPTY);
        assertNotNull(resolved);
        assertEquals("root\\toolSimple", resolved);
    }

    @Test
    public void trimsCorrectToolsContext() {
        String tool2 = resolver.resolveTemplateParameter("%{tool.toolWhitespace}", ContextInfo.EMPTY);
        String tool3 = resolver.resolveTemplateParameter("%{tool.toolNewline}", ContextInfo.EMPTY);

        assertNotNull(tool2);
        assertEquals("root\\tool whitespace", tool2);
        assertNotNull(tool3);
        assertEquals("root\\tool newline", tool3);
    }

    @Test
    public void resolvesCorrectTmpContext() {
        String resolved = resolver.resolveTemplateParameter("%{tmp.tmpParamSimple}", ContextInfo.EMPTY);
        assertNotNull(resolved);
        assertEquals("tmpParamSimple", resolved);
    }

    @Test
    public void trimsCorrectTmpContext() {
        String tmp2 = resolver.resolveTemplateParameter("%{tmp.tmpParamWhitespace}", ContextInfo.EMPTY);
        String tmp3 = resolver.resolveTemplateParameter("%{tmp.tmpParamNewline}", ContextInfo.EMPTY);

        assertNotNull(tmp2);
        assertEquals("tmpParam whitespace", tmp2);
        assertNotNull(tmp3);
        assertEquals("tmpParam newline", tmp3);
    }

    @Test
    public void resolvesCorrectDynamicContext() {
        String resolved1 = resolver.resolveTemplateParameter("%{dynamic.dynamic1}", ContextInfo.EMPTY);
        String resolved2 = resolver.resolveTemplateParameter("%{dynamic.dynamic2}", ContextInfo.EMPTY);

        assertNotNull(resolved1);
        assertEquals("dynamicValue1", resolved1);
        assertNotNull(resolved2);
        assertEquals("dynamicValue2", resolved2);
    }

    @Test
    public void resolvesCorrectOutputContext() {
        String resolved1 = resolver.resolveTemplateParameter("%{output.output1}", ContextInfo.EMPTY);
        String resolved2 = resolver.resolveTemplateParameter("%{output.output2}", ContextInfo.EMPTY);

        assertNotNull(resolved1);
        assertEquals("outputValue1", resolved1);
        assertNotNull(resolved2);
        assertEquals("outputValue2", resolved2);
    }

    @Test
    public void resolvesCorrectSegmentContext() {
        String resolvedNum0 = resolver.resolveTemplateParameter("%{segm.num}", new ContextInfoBuilder().setSegment(0).build());
        String resolvedNum1 = resolver.resolveTemplateParameter("%{segm.num}", new ContextInfoBuilder().setSegment(1).build());

        assertNotNull(resolvedNum0);
        assertEquals("0", resolvedNum0);
        assertNotNull(resolvedNum1);
        assertEquals("1", resolvedNum1);
    }

    @Test
    public void resolvesCorrectSequenceContext() {
        String resolvedAudioNum0 = resolver.resolveTemplateParameter("%{seq.num}",
                new ContextInfoBuilder().setSequence(0).setSequenceType(SequenceType.AUDIO).build());
        String resolvedAudioType0 = resolver.resolveTemplateParameter("%{seq.type}",
                new ContextInfoBuilder().setSequence(0).setSequenceType(SequenceType.AUDIO).build());

        String resolvedAudioNum1 = resolver.resolveTemplateParameter("%{seq.num}",
                new ContextInfoBuilder().setSequence(1).setSequenceType(SequenceType.AUDIO).build());
        String resolvedAudioType1 = resolver.resolveTemplateParameter("%{seq.type}",
                new ContextInfoBuilder().setSequence(1).setSequenceType(SequenceType.AUDIO).build());

        String resolvedVideoNum0 = resolver.resolveTemplateParameter("%{seq.num}",
                new ContextInfoBuilder().setSequence(0).setSequenceType(SequenceType.VIDEO).build());
        String resolvedVideoType0 = resolver.resolveTemplateParameter("%{seq.type}",
                new ContextInfoBuilder().setSequence(0).setSequenceType(SequenceType.VIDEO).build());

        String resolvedVideoNum1 = resolver.resolveTemplateParameter("%{seq.num}",
                new ContextInfoBuilder().setSequence(1).setSequenceType(SequenceType.VIDEO).build());
        String resolvedVideoType1 = resolver.resolveTemplateParameter("%{seq.type}",
                new ContextInfoBuilder().setSequence(1).setSequenceType(SequenceType.VIDEO).build());

        assertNotNull(resolvedAudioNum0);
        assertEquals("0", resolvedAudioNum0);

        assertNotNull(resolvedAudioType0);
        assertEquals("audio", resolvedAudioType0);

        assertNotNull(resolvedAudioNum1);
        assertEquals("1", resolvedAudioNum1);

        assertNotNull(resolvedAudioType1);
        assertEquals("audio", resolvedAudioType1);

        assertNotNull(resolvedVideoNum0);
        assertEquals("0", resolvedVideoNum0);

        assertNotNull(resolvedVideoType0);
        assertEquals("video", resolvedVideoType0);

        assertNotNull(resolvedVideoNum1);
        assertEquals("1", resolvedVideoNum1);

        assertNotNull(resolvedVideoType1);
        assertEquals("video", resolvedVideoType1);
    }

    @Test
    public void resolvesCorrectResourceContext() {
        String resolvedVideoEssence000 = resolver.resolveTemplateParameter("%{resource.essence}",
                new ContextInfoBuilder().setSegment(0).setSequence(0).setSequenceType(SequenceType.VIDEO).setResource(0).build());
        String resolvedVideoEssence001 = resolver.resolveTemplateParameter("%{resource.essence}",
                new ContextInfoBuilder().setSegment(0).setSequence(0).setSequenceType(SequenceType.VIDEO).setResource(1).build());
        String resolvedVideoEssence111 = resolver.resolveTemplateParameter("%{resource.essence}",
                new ContextInfoBuilder().setSegment(1).setSequence(1).setSequenceType(SequenceType.VIDEO).setResource(1).build());

        String resolvedVideoStartTime000 = resolver.resolveTemplateParameter("%{resource.startTime}",
                new ContextInfoBuilder().setSegment(0).setSequence(0).setSequenceType(SequenceType.VIDEO).setResource(0).build());
        String resolvedVideoStartTime001 = resolver.resolveTemplateParameter("%{resource.startTime}",
                new ContextInfoBuilder().setSegment(0).setSequence(0).setSequenceType(SequenceType.VIDEO).setResource(1).build());
        String resolvedVideoStartTime111 = resolver.resolveTemplateParameter("%{resource.startTime}",
                new ContextInfoBuilder().setSegment(1).setSequence(1).setSequenceType(SequenceType.VIDEO).setResource(1).build());

        String resolvedVideoDuration000 = resolver.resolveTemplateParameter("%{resource.duration}",
                new ContextInfoBuilder().setSegment(0).setSequence(0).setSequenceType(SequenceType.VIDEO).setResource(0).build());
        String resolvedVideoDuration001 = resolver.resolveTemplateParameter("%{resource.duration}",
                new ContextInfoBuilder().setSegment(0).setSequence(0).setSequenceType(SequenceType.VIDEO).setResource(1).build());
        String resolvedVideoDuration111 = resolver.resolveTemplateParameter("%{resource.duration}",
                new ContextInfoBuilder().setSegment(1).setSequence(1).setSequenceType(SequenceType.VIDEO).setResource(1).build());

        String resolvedVideoNum000 = resolver.resolveTemplateParameter("%{resource.num}",
                new ContextInfoBuilder().setSegment(0).setSequence(0).setSequenceType(SequenceType.VIDEO).setResource(0).build());
        String resolvedVideoNum001 = resolver.resolveTemplateParameter("%{resource.num}",
                new ContextInfoBuilder().setSegment(0).setSequence(0).setSequenceType(SequenceType.VIDEO).setResource(1).build());
        String resolvedVideoNum111 = resolver.resolveTemplateParameter("%{resource.num}",
                new ContextInfoBuilder().setSegment(1).setSequence(1).setSequenceType(SequenceType.VIDEO).setResource(1).build());

        String resolvedAudioEssence000 = resolver.resolveTemplateParameter("%{resource.essence}",
                new ContextInfoBuilder().setSegment(0).setSequence(0).setSequenceType(SequenceType.AUDIO).setResource(0).build());
        String resolvedAudioEssence001 = resolver.resolveTemplateParameter("%{resource.essence}",
                new ContextInfoBuilder().setSegment(0).setSequence(0).setSequenceType(SequenceType.AUDIO).setResource(1).build());
        String resolvedAudioEssence111 = resolver.resolveTemplateParameter("%{resource.essence}",
                new ContextInfoBuilder().setSegment(1).setSequence(1).setSequenceType(SequenceType.AUDIO).setResource(1).build());

        String resolvedAudioStartTime000 = resolver.resolveTemplateParameter("%{resource.startTime}",
                new ContextInfoBuilder().setSegment(0).setSequence(0).setSequenceType(SequenceType.AUDIO).setResource(0).build());
        String resolvedAudioStartTime001 = resolver.resolveTemplateParameter("%{resource.startTime}",
                new ContextInfoBuilder().setSegment(0).setSequence(0).setSequenceType(SequenceType.AUDIO).setResource(1).build());
        String resolvedAudioStartTime111 = resolver.resolveTemplateParameter("%{resource.startTime}",
                new ContextInfoBuilder().setSegment(1).setSequence(1).setSequenceType(SequenceType.AUDIO).setResource(1).build());

        String resolvedAudioDuration000 = resolver.resolveTemplateParameter("%{resource.duration}",
                new ContextInfoBuilder().setSegment(0).setSequence(0).setSequenceType(SequenceType.AUDIO).setResource(0).build());
        String resolvedAudioDuration001 = resolver.resolveTemplateParameter("%{resource.duration}",
                new ContextInfoBuilder().setSegment(0).setSequence(0).setSequenceType(SequenceType.AUDIO).setResource(1).build());
        String resolvedAudioDuration111 = resolver.resolveTemplateParameter("%{resource.duration}",
                new ContextInfoBuilder().setSegment(1).setSequence(1).setSequenceType(SequenceType.AUDIO).setResource(1).build());

        String resolvedAudioNum000 = resolver.resolveTemplateParameter("%{resource.num}",
                new ContextInfoBuilder().setSegment(0).setSequence(0).setSequenceType(SequenceType.AUDIO).setResource(0).build());
        String resolvedAudioNum001 = resolver.resolveTemplateParameter("%{resource.num}",
                new ContextInfoBuilder().setSegment(0).setSequence(0).setSequenceType(SequenceType.AUDIO).setResource(1).build());
        String resolvedAudioNum111 = resolver.resolveTemplateParameter("%{resource.num}",
                new ContextInfoBuilder().setSegment(1).setSequence(1).setSequenceType(SequenceType.AUDIO).setResource(1).build());

        assertNotNull(resolvedVideoEssence000);
        assertEquals("video_essence_0_0_0", resolvedVideoEssence000);

        assertNotNull(resolvedVideoEssence001);
        assertEquals("video_essence_0_0_1", resolvedVideoEssence001);

        assertNotNull(resolvedVideoEssence111);
        assertEquals("video_essence_1_1_1", resolvedVideoEssence111);

        assertNotNull(resolvedVideoStartTime000);
        assertEquals("video_startTime_0_0_0", resolvedVideoStartTime000);

        assertNotNull(resolvedVideoStartTime001);
        assertEquals("video_startTime_0_0_1", resolvedVideoStartTime001);

        assertNotNull(resolvedVideoStartTime111);
        assertEquals("video_startTime_1_1_1", resolvedVideoStartTime111);

        assertNotNull(resolvedVideoDuration000);
        assertEquals("video_duration_0_0_0", resolvedVideoDuration000);

        assertNotNull(resolvedVideoDuration001);
        assertEquals("video_duration_0_0_1", resolvedVideoDuration001);

        assertNotNull(resolvedVideoDuration111);
        assertEquals("video_duration_1_1_1", resolvedVideoDuration111);

        assertNotNull(resolvedVideoNum000);
        assertEquals("0", resolvedVideoNum000);

        assertNotNull(resolvedVideoNum001);
        assertEquals("1", resolvedVideoNum001);

        assertNotNull(resolvedVideoNum111);
        assertEquals("1", resolvedVideoNum111);


        assertNotNull(resolvedAudioEssence000);
        assertEquals("audio_essence_0_0_0", resolvedAudioEssence000);

        assertNotNull(resolvedAudioEssence001);
        assertEquals("audio_essence_0_0_1", resolvedAudioEssence001);

        assertNotNull(resolvedAudioEssence111);
        assertEquals("audio_essence_1_1_1", resolvedAudioEssence111);

        assertNotNull(resolvedAudioStartTime000);
        assertEquals("audio_startTime_0_0_0", resolvedAudioStartTime000);

        assertNotNull(resolvedAudioStartTime001);
        assertEquals("audio_startTime_0_0_1", resolvedAudioStartTime001);

        assertNotNull(resolvedAudioStartTime111);
        assertEquals("audio_startTime_1_1_1", resolvedAudioStartTime111);

        assertNotNull(resolvedAudioDuration000);
        assertEquals("audio_duration_0_0_0", resolvedAudioDuration000);

        assertNotNull(resolvedAudioDuration001);
        assertEquals("audio_duration_0_0_1", resolvedAudioDuration001);

        assertNotNull(resolvedAudioDuration111);
        assertEquals("audio_duration_1_1_1", resolvedAudioDuration111);

        assertNotNull(resolvedAudioNum000);
        assertEquals("0", resolvedVideoNum000);

        assertNotNull(resolvedAudioNum001);
        assertEquals("1", resolvedVideoNum001);

        assertNotNull(resolvedAudioNum111);
        assertEquals("1", resolvedVideoNum111);
    }

    @Test(expected = InvalidTemplateParameterException.class)
    public void exceptionOnIncorrectParameterFormat() {
        new TemplateParameter("%{tool.toolSimple");
    }

    @Test(expected = UnknownTemplateParameterContextException.class)
    public void exceptionOnIncorrectParameterContext() {
        resolver.resolveTemplateParameter("%{xxxx.toolSimple}", ContextInfo.EMPTY);
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnIncorrectToolParameterName() {
        resolver.resolveTemplateParameter("%{tool.xxxx}", ContextInfo.EMPTY);
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnEmptyToolParameter() {
        resolver.resolveTemplateParameter("%{tool.toolEmpty}", ContextInfo.EMPTY);
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnEmptyWithNewlinesToolParameter() {
        resolver.resolveTemplateParameter("%{tool.toolEmptyNewlines}", ContextInfo.EMPTY);
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnIncorrectTmpParameterName() {
        resolver.resolveTemplateParameter("%{tmp.xxxx}", ContextInfo.EMPTY);
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnEmptyTmpParameter() {
        resolver.resolveTemplateParameter("%{tmp.tmpParamEmpty}", ContextInfo.EMPTY);
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnEmptyWithNewlinesTmpParameter() {
        resolver.resolveTemplateParameter("%{tmp.tmpParamEmptyNewline}", ContextInfo.EMPTY);
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnIncorrectDynamicParameterName() {
        resolver.resolveTemplateParameter("%{dynamic.xxxx}", ContextInfo.EMPTY);
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnIncorrectOutputParameterName() {
        resolver.resolveTemplateParameter("%{output.xxxx}", ContextInfo.EMPTY);
    }

    @Test(expected = UnknownTemplateParameterNameException.class)
    public void exceptionOnIncorrectSegmentParameterName() {
        resolver.resolveTemplateParameter("%{segm.xxxx}",
                new ContextInfoBuilder().setSegment(0).build());
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnIncorrectSegmentParameterSegment() {
        // we have only 2 segments (0 and 1)
        resolver.resolveTemplateParameter("%{segm.num}",
                new ContextInfoBuilder().setSegment(10).build());
    }

    @Test(expected = UnknownTemplateParameterNameException.class)
    public void exceptionOnIncorrectSequenceParameterName() {
        resolver.resolveTemplateParameter("%{seq.xxxx}",
                new ContextInfoBuilder().setSequence(0).setSequenceType(SequenceType.AUDIO).build());
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnIncorrectSequenceParameterSequence() {
        // we have only 2 sequences (0 and 1)
        resolver.resolveTemplateParameter("%{seq.num}",
                new ContextInfoBuilder().setSequence(10).setSequenceType(SequenceType.AUDIO).build());
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnIncorrectSequenceParameterType() {
        // we didn't fill subtitle type
        resolver.resolveTemplateParameter("%{seq.num}",
                new ContextInfoBuilder().setSequence(0).setSequenceType(SequenceType.SUBTITLE).build());
    }

    @Test(expected = UnknownTemplateParameterNameException.class)
    public void exceptionOnIncorrectResourceParameterName() {
        resolver.resolveTemplateParameter("%{resource.xxxx}",
                new ContextInfoBuilder().setSegment(0).setSequence(0).setSequenceType(SequenceType.VIDEO).setResource(0).build());
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnIncorrectResourceParameterSegment() {
        // we have only 2 segments (0 and 1)
        resolver.resolveTemplateParameter("%{resource.essence}",
                new ContextInfoBuilder().setSegment(10).setSequence(0).setSequenceType(SequenceType.VIDEO).setResource(0).build());
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnIncorrectResourceParameterSequence() {
        // we have only 2 sequences (0 and 1)
        resolver.resolveTemplateParameter("%{resource.essence}",
                new ContextInfoBuilder().setSegment(0).setSequence(10).setSequenceType(SequenceType.VIDEO).setResource(0).build());
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnIncorrectResourceParameterType() {
        // we didn't fill subtitle type
        resolver.resolveTemplateParameter("%{resource.essence}",
                new ContextInfoBuilder().setSegment(10).setSequence(0).setSequenceType(SequenceType.SUBTITLE).setResource(0).build());
    }


}
