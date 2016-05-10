package com.netflix.imfutility.conversion.templateParameter;

import com.netflix.imfutility.ConfigProvider;
import com.netflix.imfutility.Format;
import com.netflix.imfutility.conversion.ConversionProvider;
import com.netflix.imfutility.conversion.templateParameter.context.DynamicTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.SegmentContextParameters;
import com.netflix.imfutility.conversion.templateParameter.context.SegmentTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.exception.InvalidTemplateParameterException;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;
import com.netflix.imfutility.conversion.templateParameter.exception.UnknownTemplateParameterContextException;
import com.netflix.imfutility.conversion.templateParameter.exception.UnknownTemplateParameterNameException;
import com.netflix.imfutility.util.ConfigUtils;
import com.netflix.imfutility.util.ConversionUtils;
import com.netflix.imfutility.xsd.conversion.SegmentType;
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
        fillSegment(contextProvider);

        resolver = new TemplateParameterResolver(contextProvider);
    }

    private static void fillDynamic(TemplateParameterContextProvider contextProvider) {
        DynamicTemplateParameterContext dynamicContext = contextProvider.getDynamicContext();
        dynamicContext.addParameter("dynamic1", "dynamicValue1");
        dynamicContext.addParameter("dynamic2", "dynamicValue2");
    }

    private static void fillSegment(TemplateParameterContextProvider contextProvider) {
        SegmentTemplateParameterContext segmentContext = contextProvider.getSegmentContext();

        for (int i = 0; i < 2; i++) {
            for (SegmentContextParameters contextParam : SegmentContextParameters.values()) {
                // do not fill subtitle type!
                segmentContext.addSegmentParameter(i, SegmentType.VIDEO, contextParam, SegmentType.VIDEO.value() + "_" + contextParam.getName() + "_" + i);
                segmentContext.addSegmentParameter(i, SegmentType.AUDIO, contextParam, SegmentType.AUDIO.value() + "_" + contextParam.getName() + "_" + i);
            }
        }
    }

    @Test
    public void resolvesCorrectToolsContext() {
        String resolved = resolver.resolveTemplateParameter("%{tool.toolSimple}");
        assertNotNull(resolved);
        assertEquals("root\\toolSimple", resolved);
    }

    @Test
    public void trimsCorrectToolsContext() {
        String tool2 = resolver.resolveTemplateParameter("%{tool.toolWhitespace}");
        String tool3 = resolver.resolveTemplateParameter("%{tool.toolNewline}");

        assertNotNull(tool2);
        assertEquals("root\\tool whitespace", tool2);
        assertNotNull(tool3);
        assertEquals("root\\tool   newline", tool3);
    }

    @Test
    public void resolvesCorrectTmpContext() {
        String resolved = resolver.resolveTemplateParameter("%{tmp.tmpParamSimple}");
        assertNotNull(resolved);
        assertEquals("tmpParamSimple", resolved);
    }

    @Test
    public void trimsCorrectTmpContext() {
        String tmp2 = resolver.resolveTemplateParameter("%{tmp.tmpParamWhitespace}");
        String tmp3 = resolver.resolveTemplateParameter("%{tmp.tmpParamNewline}");

        assertNotNull(tmp2);
        assertEquals("tmpParam whitespace", tmp2);
        assertNotNull(tmp3);
        assertEquals("tmpParam   newline", tmp3);
    }

    @Test
    public void resolvesCorrectDynamicContext() {
        String resolved1 = resolver.resolveTemplateParameter("%{dynamic.dynamic1}");
        String resolved2 = resolver.resolveTemplateParameter("%{dynamic.dynamic2}");

        assertNotNull(resolved1);
        assertEquals("dynamicValue1", resolved1);
        assertNotNull(resolved2);
        assertEquals("dynamicValue2", resolved2);
    }

    @Test
    public void resolvesCorrectSegmentContext() {
        String resolvedVideoEssence0 = resolver.resolveTemplateParameter("%{segment.essence}", 0, SegmentType.VIDEO);
        String resolvedVideoDuration0 = resolver.resolveTemplateParameter("%{segment.duration}", 0, SegmentType.VIDEO);
        String resolvedVideoStartTime0 = resolver.resolveTemplateParameter("%{segment.startTime}", 0, SegmentType.VIDEO);
        String resolvedAudioEssence0 = resolver.resolveTemplateParameter("%{segment.essence}", 0, SegmentType.AUDIO);
        String resolvedAudioDuration0 = resolver.resolveTemplateParameter("%{segment.duration}", 0, SegmentType.AUDIO);
        String resolvedAudioStartTime0 = resolver.resolveTemplateParameter("%{segment.startTime}", 0, SegmentType.AUDIO);

        String resolvedVideoEssence1 = resolver.resolveTemplateParameter("%{segment.essence}", 1, SegmentType.VIDEO);
        String resolvedVideoDuration1 = resolver.resolveTemplateParameter("%{segment.duration}", 1, SegmentType.VIDEO);
        String resolvedVideoStartTime1 = resolver.resolveTemplateParameter("%{segment.startTime}", 1, SegmentType.VIDEO);
        String resolvedAudioEssence1 = resolver.resolveTemplateParameter("%{segment.essence}", 1, SegmentType.AUDIO);
        String resolvedAudioDuration1 = resolver.resolveTemplateParameter("%{segment.duration}", 1, SegmentType.AUDIO);
        String resolvedAudioStartTime1 = resolver.resolveTemplateParameter("%{segment.startTime}", 1, SegmentType.AUDIO);

        assertNotNull(resolvedVideoEssence0);
        assertEquals("video_essence_0", resolvedVideoEssence0);
        assertNotNull(resolvedVideoDuration0);
        assertEquals("video_duration_0", resolvedVideoDuration0);
        assertNotNull(resolvedVideoStartTime0);
        assertEquals("video_startTime_0", resolvedVideoStartTime0);

        assertNotNull(resolvedAudioEssence0);
        assertEquals("audio_essence_0", resolvedAudioEssence0);
        assertNotNull(resolvedVideoDuration0);
        assertEquals("audio_duration_0", resolvedAudioDuration0);
        assertNotNull(resolvedVideoStartTime0);
        assertEquals("audio_startTime_0", resolvedAudioStartTime0);

        assertNotNull(resolvedVideoEssence1);
        assertEquals("video_essence_1", resolvedVideoEssence1);
        assertNotNull(resolvedVideoDuration1);
        assertEquals("video_duration_1", resolvedVideoDuration1);
        assertNotNull(resolvedVideoStartTime1);
        assertEquals("video_startTime_1", resolvedVideoStartTime1);

        assertNotNull(resolvedAudioEssence1);
        assertEquals("audio_essence_1", resolvedAudioEssence1);
        assertNotNull(resolvedVideoDuration1);
        assertEquals("audio_duration_1", resolvedAudioDuration1);
        assertNotNull(resolvedVideoStartTime1);
        assertEquals("audio_startTime_1", resolvedAudioStartTime1);
    }

    @Test
    public void resolvesCorrectAllContextsWhenSegmentSpecified() {
        String tool = resolver.resolveTemplateParameter("%{tool.toolSimple}", 0, SegmentType.VIDEO);
        String tmp = resolver.resolveTemplateParameter("%{tmp.tmpParamSimple}", 0, SegmentType.AUDIO);
        String dynamic = resolver.resolveTemplateParameter("%{dynamic.dynamic1}", 0, SegmentType.VIDEO);

        assertNotNull(tool);
        assertEquals("root\\toolSimple", tool);
        assertNotNull(tmp);
        assertEquals("tmpParamSimple", tmp);
        assertNotNull(dynamic);
        assertEquals("dynamicValue1", dynamic);
    }

    @Test(expected = InvalidTemplateParameterException.class)
    public void exceptionOnIncorrectParameterFormat() {
        resolver.resolveTemplateParameter("%{tool.toolSimple");
    }

    @Test(expected = UnknownTemplateParameterContextException.class)
    public void exceptionOnIncorrectParameterContext() {
        resolver.resolveTemplateParameter("%{xxxx.toolSimple}");
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnIncorrectToolParameterName() {
        resolver.resolveTemplateParameter("%{tool.xxxx}");
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnEmptyToolParameter() {
        resolver.resolveTemplateParameter("%{tool.toolEmpty}");
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnEmptyWithNewlinesToolParameter() {
        resolver.resolveTemplateParameter("%{tool.toolEmptyNewlines}");
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnIncorrectTmpParameterName() {
        resolver.resolveTemplateParameter("%{tmp.xxxx}");
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnEmptyTmpParameter() {
        resolver.resolveTemplateParameter("%{tmp.tmpParamEmpty}");
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnEmptyWithNewlinesTmpParameter() {
        resolver.resolveTemplateParameter("%{tmp.tmpParamEmptyNewline}");
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnIncorrectDynamicParameterName() {
        resolver.resolveTemplateParameter("%{dynamic.xxxx}");
    }

    @Test(expected = UnknownTemplateParameterNameException.class)
    public void exceptionOnIncorrectSegmentParameterName() {
        resolver.resolveTemplateParameter("%{segment.xxxx}", 0, SegmentType.VIDEO);
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnIncorrectSegmentParameterSegment() {
        // we have only 2 segments (0 and 1)
        resolver.resolveTemplateParameter("%{segment.essence}", 5, SegmentType.VIDEO);
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnIncorrectSegmentParameterType() {
        // we didn't fill subtitle segments
        resolver.resolveTemplateParameter("%{segment.essence}", 0, SegmentType.SUBTITLE);
    }


}
