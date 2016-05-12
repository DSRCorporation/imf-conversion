package com.netflix.imfutility.conversion.templateParameter;

import com.netflix.imfutility.ConfigProvider;
import com.netflix.imfutility.Format;
import com.netflix.imfutility.conversion.ConversionProvider;
import com.netflix.imfutility.conversion.templateParameter.context.*;
import com.netflix.imfutility.conversion.templateParameter.exception.InvalidTemplateParameterException;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;
import com.netflix.imfutility.conversion.templateParameter.exception.UnknownTemplateParameterContextException;
import com.netflix.imfutility.conversion.templateParameter.exception.UnknownTemplateParameterNameException;
import com.netflix.imfutility.util.ConfigUtils;
import com.netflix.imfutility.util.ConversionUtils;
import com.netflix.imfutility.xsd.conversion.SequenceType;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * <ul>
 * <li>Tests the template parameters can be resolved successfully for each context.</li>
 * <li>Tests that expected exceptions are thrown if template parameters can not be resolved due to different reasons.</li>
 * </ul>
 */
public class TemplateParameterResolverTest {

    private static TemplateParameterResolver resolver;

    @BeforeClass
    public static void setUpAll() throws Exception {
        ConfigProvider configProvider = new ConfigProvider(ConfigUtils.getCorrectConfigXml());
        ConversionProvider conversionProvider = new ConversionProvider(ConversionUtils.getCorrectConversionXml(), Format.DPP);

        TemplateParameterContextProvider contextProvider = new TemplateParameterContextProvider(
                configProvider.getConfig(), conversionProvider.getFormat(), ".");
        fillDynamic(contextProvider);
        fillCpl(contextProvider);

        resolver = new TemplateParameterResolver(contextProvider);
    }

    private static void fillDynamic(TemplateParameterContextProvider contextProvider) {
        DynamicTemplateParameterContext dynamicContext = contextProvider.getDynamicContext();
        dynamicContext.addParameter("dynamic1", "dynamicValue1", ContextInfo.EMPTY);
        dynamicContext.addParameter("dynamic2", "dynamicValue2", ContextInfo.EMPTY);
    }

    private static void fillCpl(TemplateParameterContextProvider contextProvider) {
        int segmentCount = 2;
        int seqCount = 2;
        int resourceCount = 2;

        SegmentTemplateParameterContext segmentContext = contextProvider.getSegmentContext();
        segmentContext.initDefaultSegmentParameters(segmentCount);

        SequenceTemplateParameterContext sequenceContext = contextProvider.getSequenceContext();
        sequenceContext.initDefaultSequenceParameters(SequenceType.VIDEO, seqCount);
        sequenceContext.initDefaultSequenceParameters(SequenceType.AUDIO, seqCount);

        ResourceTemplateParameterContext resourceContext = contextProvider.getResourceContext();
        for (int segm = 0; segm < segmentCount; segm++) {
            for (int seq = 0; seq < seqCount; seq++) {
                for (int res = 0; res < resourceCount; res++) {
                    resourceContext.initDefaultResourceParameters(new ResourceKey(segm, seq, SequenceType.VIDEO),
                            resourceCount);
                    resourceContext.initDefaultResourceParameters(new ResourceKey(segm, seq, SequenceType.AUDIO),
                            resourceCount);

                    // do not fill subtitle type!
                    fillResourceParam(resourceContext, segm, seq, res, ResourceContextParameters.ESSENCE, SequenceType.VIDEO);
                    fillResourceParam(resourceContext, segm, seq, res, ResourceContextParameters.DURATION, SequenceType.VIDEO);
                    fillResourceParam(resourceContext, segm, seq, res, ResourceContextParameters.START_TIME, SequenceType.VIDEO);

                    fillResourceParam(resourceContext, segm, seq, res, ResourceContextParameters.ESSENCE, SequenceType.AUDIO);
                    fillResourceParam(resourceContext, segm, seq, res, ResourceContextParameters.DURATION, SequenceType.AUDIO);
                    fillResourceParam(resourceContext, segm, seq, res, ResourceContextParameters.START_TIME, SequenceType.AUDIO);
                }
            }
        }
    }

    private static void fillResourceParam(ResourceTemplateParameterContext resourceContext,
                                          int segm, int seq, int res, ResourceContextParameters resParam, SequenceType seqType) {
        resourceContext.addResourceParameter(
                new ResourceKey(segm, seq, seqType),
                res, resParam,
                seqType.value() + "_" + resParam.getName() + "_" + segm + "_" + seq + "_" + res);
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
        assertEquals("root\\tool   newline", tool3);
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
        assertEquals("tmpParam   newline", tmp3);
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
    public void resolvesCorrectResourceContext() {
        String resolvedVideoEssence000 = resolver.resolveTemplateParameter("%{resource.essence}",
                new ContextInfoBuilder().setSegment(0).setSequence(0).setSequenceType(SequenceType.VIDEO).setResource(0).build());
        String resolvedVideoEssence111 = resolver.resolveTemplateParameter("%{resource.essence}",
                new ContextInfoBuilder().setSegment(1).setSequence(1).setSequenceType(SequenceType.VIDEO).setResource(1).build());
        String resolvedVideoStartTime000 = resolver.resolveTemplateParameter("%{resource.startTime}",
                new ContextInfoBuilder().setSegment(0).setSequence(0).setSequenceType(SequenceType.VIDEO).setResource(0).build());
        String resolvedVideoStartTime111 = resolver.resolveTemplateParameter("%{resource.startTime}",
                new ContextInfoBuilder().setSegment(1).setSequence(1).setSequenceType(SequenceType.VIDEO).setResource(1).build());
        String resolvedVideoDuration000 = resolver.resolveTemplateParameter("%{resource.duration}",
                new ContextInfoBuilder().setSegment(0).setSequence(0).setSequenceType(SequenceType.VIDEO).setResource(0).build());
        String resolvedVideoDuration111 = resolver.resolveTemplateParameter("%{resource.duration}",
                new ContextInfoBuilder().setSegment(1).setSequence(1).setSequenceType(SequenceType.VIDEO).setResource(1).build());
        String resolvedVideoNum000 = resolver.resolveTemplateParameter("%{resource.num}",
                new ContextInfoBuilder().setSegment(0).setSequence(0).setSequenceType(SequenceType.VIDEO).setResource(0).build());
        String resolvedVideoNum111 = resolver.resolveTemplateParameter("%{resource.num}",
                new ContextInfoBuilder().setSegment(1).setSequence(1).setSequenceType(SequenceType.VIDEO).setResource(1).build());

        String resolvedAudioEssence000 = resolver.resolveTemplateParameter("%{resource.essence}",
                new ContextInfoBuilder().setSegment(0).setSequence(0).setSequenceType(SequenceType.AUDIO).setResource(0).build());
        String resolvedAudioEssence111 = resolver.resolveTemplateParameter("%{resource.essence}",
                new ContextInfoBuilder().setSegment(1).setSequence(1).setSequenceType(SequenceType.AUDIO).setResource(1).build());
        String resolvedAudioStartTime000 = resolver.resolveTemplateParameter("%{resource.startTime}",
                new ContextInfoBuilder().setSegment(0).setSequence(0).setSequenceType(SequenceType.AUDIO).setResource(0).build());
        String resolvedAudioStartTime111 = resolver.resolveTemplateParameter("%{resource.startTime}",
                new ContextInfoBuilder().setSegment(1).setSequence(1).setSequenceType(SequenceType.AUDIO).setResource(1).build());
        String resolvedAudioDuration000 = resolver.resolveTemplateParameter("%{resource.duration}",
                new ContextInfoBuilder().setSegment(0).setSequence(0).setSequenceType(SequenceType.AUDIO).setResource(0).build());
        String resolvedAudioDuration111 = resolver.resolveTemplateParameter("%{resource.duration}",
                new ContextInfoBuilder().setSegment(1).setSequence(1).setSequenceType(SequenceType.AUDIO).setResource(1).build());
        String resolvedAudioNum000 = resolver.resolveTemplateParameter("%{resource.num}",
                new ContextInfoBuilder().setSegment(0).setSequence(0).setSequenceType(SequenceType.AUDIO).setResource(0).build());
        String resolvedAudioNum111 = resolver.resolveTemplateParameter("%{resource.num}",
                new ContextInfoBuilder().setSegment(1).setSequence(1).setSequenceType(SequenceType.AUDIO).setResource(1).build());

        assertNotNull(resolvedVideoEssence000);
        assertEquals("video_essence_0_0_0", resolvedVideoEssence000);

        assertNotNull(resolvedVideoEssence111);
        assertEquals("video_essence_1_1_1", resolvedVideoEssence111);

        assertNotNull(resolvedVideoStartTime000);
        assertEquals("video_startTime_0_0_0", resolvedVideoStartTime000);

        assertNotNull(resolvedVideoStartTime111);
        assertEquals("video_startTime_1_1_1", resolvedVideoStartTime111);

        assertNotNull(resolvedVideoDuration000);
        assertEquals("video_duration_0_0_0", resolvedVideoDuration000);

        assertNotNull(resolvedVideoDuration111);
        assertEquals("video_duration_1_1_1", resolvedVideoDuration111);

        assertNotNull(resolvedVideoNum000);
        assertEquals("0", resolvedVideoNum000);

        assertNotNull(resolvedVideoNum111);
        assertEquals("1", resolvedVideoNum111);


        assertNotNull(resolvedAudioEssence000);
        assertEquals("audio_essence_0_0_0", resolvedAudioEssence000);

        assertNotNull(resolvedAudioEssence111);
        assertEquals("audio_essence_1_1_1", resolvedAudioEssence111);

        assertNotNull(resolvedAudioStartTime000);
        assertEquals("audio_startTime_0_0_0", resolvedAudioStartTime000);

        assertNotNull(resolvedAudioStartTime111);
        assertEquals("audio_startTime_1_1_1", resolvedAudioStartTime111);

        assertNotNull(resolvedAudioDuration000);
        assertEquals("audio_duration_0_0_0", resolvedAudioDuration000);

        assertNotNull(resolvedAudioDuration111);
        assertEquals("audio_duration_1_1_1", resolvedAudioDuration111);

        assertNotNull(resolvedAudioNum000);
        assertEquals("0", resolvedVideoNum000);

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
    public void exceptionOnIncorrectResourceParameterType() {
        // we didn't fill subtitle segments
        resolver.resolveTemplateParameter("%{resource.essence}",
                new ContextInfoBuilder().setSegment(10).setSequence(0).setSequenceType(SequenceType.SUBTITLE).setResource(0).build());
    }


}
