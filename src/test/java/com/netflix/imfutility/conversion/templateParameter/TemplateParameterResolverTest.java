package com.netflix.imfutility.conversion.templateParameter;

import com.netflix.imfutility.Format;
import com.netflix.imfutility.config.ConfigProvider;
import com.netflix.imfutility.conversion.ConversionProvider;
import com.netflix.imfutility.conversion.templateParameter.context.DynamicTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.OutputTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.exception.InvalidTemplateParameterException;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;
import com.netflix.imfutility.conversion.templateParameter.exception.UnknownTemplateParameterContextException;
import com.netflix.imfutility.conversion.templateParameter.exception.UnknownTemplateParameterNameException;
import com.netflix.imfutility.cpl.uuid.ResourceUUID;
import com.netflix.imfutility.util.ConfigUtils;
import com.netflix.imfutility.util.ConversionUtils;
import com.netflix.imfutility.xsd.conversion.SequenceType;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.EnumSet;

import static com.netflix.imfutility.util.TemplateParameterContextCreator.*;
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
    private static final EnumSet<SequenceType> SEQUENCE_TYPES = EnumSet.of(SequenceType.VIDEO, SequenceType.AUDIO); // do not fill subtitle type!

    private static TemplateParameterResolver resolver;
    private static TemplateParameterContextProvider contextProvider;

    @BeforeClass
    public static void setUpAll() throws Exception {
        ConfigProvider configProvider = new ConfigProvider(ConfigUtils.getCorrectConfigXml());
        ConversionProvider conversionProvider = new ConversionProvider(ConversionUtils.getCorrectConversionXml(), Format.DPP);

        contextProvider = new TemplateParameterContextProvider(
                configProvider.getConfig(), conversionProvider.getFormat(), ".");
        fillDynamic(contextProvider);
        fillOutput(contextProvider);
        fillCPLContext(contextProvider,
                SEGMENT_COUNT,
                SEQ_COUNT,
                RESOURCE_COUNT,
                SEQUENCE_TYPES);

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
    public void resolvesCorrectDynamicContextSubparameters() {
        contextProvider.getDynamicContext().addParameter(
                "name-%{segm.num}-%{seq.num}-%{seq.type}-%{resource.num}-%{tmp.tmpParamSimple}",
                "valueWithSubparameters",
                new ContextInfoBuilder()
                        .setSegmentUuid(getSegmentUuid(0))
                        .setSequenceUuid(getSequenceUuid(0, SequenceType.AUDIO))
                        .setResourceUuid(getResourceUuid(0, 0, SequenceType.AUDIO, 0))
                        .setSequenceType(SequenceType.AUDIO).build());

        String resolved1 = resolver.resolveTemplateParameter("%{dynamic.name-%{segm.num}-%{seq.num}-%{seq.type}-%{resource.num}-%{tmp.tmpParamSimple}}",
                new ContextInfoBuilder()
                        .setSegmentUuid(getSegmentUuid(0))
                        .setSequenceUuid(getSequenceUuid(0, SequenceType.AUDIO))
                        .setResourceUuid(getResourceUuid(0, 0, SequenceType.AUDIO, 0))
                        .setSequenceType(SequenceType.AUDIO).build());

        assertNotNull(resolved1);
        assertEquals("valueWithSubparameters", resolved1);
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
        for (int segm = 0; segm < SEGMENT_COUNT; segm++) {
            String resolvedNum = resolver.resolveTemplateParameter("%{segm.num}",
                    new ContextInfoBuilder().setSegmentUuid(getSegmentUuid(segm)).build());
            String resolvedUuid = resolver.resolveTemplateParameter("%{segm.uuid}",
                    new ContextInfoBuilder().setSegmentUuid(getSegmentUuid(segm)).build());

            assertNotNull(resolvedNum);
            assertEquals(String.valueOf(segm), resolvedNum);
            assertNotNull(resolvedUuid);
            assertEquals(getSegmentUuid(segm).getUuid(), resolvedUuid);
        }
    }

    @Test
    public void resolvesCorrectSequenceContext() {
        for (SequenceType seqType : SEQUENCE_TYPES) {
            for (int seq = 0; seq < SEQ_COUNT; seq++) {
                ContextInfo contextInfo = new ContextInfoBuilder()
                        .setSequenceUuid(getSequenceUuid(seq, seqType))
                        .setSequenceType(seqType).build();
                String resolvedNum = resolver.resolveTemplateParameter("%{seq.num}", contextInfo);
                String resolvedType = resolver.resolveTemplateParameter("%{seq.type}", contextInfo);
                String resolvedUuid = resolver.resolveTemplateParameter("%{seq.uuid}", contextInfo);

                assertNotNull(resolvedNum);
                assertEquals(String.valueOf(seq), resolvedNum);

                assertNotNull(resolvedType);
                assertEquals(seqType.value(), resolvedType);

                assertNotNull(resolvedUuid);
                assertEquals(getSequenceUuid(seq, seqType).getUuid(), resolvedUuid);
            }
        }
    }

    @Test
    public void resolvesCorrectResourceContext() {
        for (int segm = 0; segm < SEGMENT_COUNT; segm++) {
            for (SequenceType seqType : SEQUENCE_TYPES) {
                for (int seq = 0; seq < SEQ_COUNT; seq++) {
                    for (int res = 0; res < RESOURCE_COUNT; res++) {
                        ResourceUUID resourceUuid = getResourceUuid(segm, seq, seqType, res);
                        ContextInfo contextInfo = new ContextInfoBuilder()
                                .setSegmentUuid(getSegmentUuid(segm))
                                .setSequenceUuid(getSequenceUuid(seq, seqType))
                                .setSequenceType(seqType)
                                .setResourceUuid(resourceUuid)
                                .build();

                        String resolvedNum = resolver.resolveTemplateParameter("%{resource.num}", contextInfo);
                        String resolvedUuid = resolver.resolveTemplateParameter("%{resource.uuid}", contextInfo);
                        String resolvedEssence = resolver.resolveTemplateParameter("%{resource.essence}", contextInfo);
                        String resolvedStartTime = resolver.resolveTemplateParameter("%{resource.startTime}", contextInfo);
                        String resolvedDuration = resolver.resolveTemplateParameter("%{resource.duration}", contextInfo);

                        assertNotNull(resolvedNum);
                        assertEquals(String.valueOf(res), resolvedNum);

                        assertNotNull(resolvedUuid);
                        assertEquals(resourceUuid.getUuid(), resolvedUuid);

                        assertResourceParameter(resolvedEssence, resourceUuid, ResourceContextParameters.ESSENCE);
                        assertResourceParameter(resolvedStartTime, resourceUuid, ResourceContextParameters.START_TIME);
                        assertResourceParameter(resolvedDuration, resourceUuid, ResourceContextParameters.DURATION);
                    }
                }
            }
        }
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
                new ContextInfoBuilder()
                        .setSegmentUuid(getSegmentUuid(0))
                        .build());
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnIncorrectSegmentParameterSegment() {
        // we have only 2 segments (0 and 1)
        resolver.resolveTemplateParameter("%{segm.num}",
                new ContextInfoBuilder().setSegmentUuid(
                        getSegmentUuid(10))
                        .build());
    }

    @Test(expected = UnknownTemplateParameterNameException.class)
    public void exceptionOnIncorrectSequenceParameterName() {
        resolver.resolveTemplateParameter("%{seq.xxxx}",
                new ContextInfoBuilder()
                        .setSequenceUuid(getSequenceUuid(0, SequenceType.AUDIO))
                        .setSequenceType(SequenceType.AUDIO)
                        .build());
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnIncorrectSequenceParameterSequence() {
        // we have only 2 sequences (0 and 1)
        resolver.resolveTemplateParameter("%{seq.num}",
                new ContextInfoBuilder()
                        .setSequenceUuid(getSequenceUuid(10, SequenceType.AUDIO))
                        .setSequenceType(SequenceType.AUDIO)
                        .build());
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnIncorrectSequenceParameterType() {
        // we didn't fill subtitle type
        resolver.resolveTemplateParameter("%{seq.num}",
                new ContextInfoBuilder()
                        .setSequenceUuid(getSequenceUuid(0, SequenceType.SUBTITLE))
                        .setSequenceType(SequenceType.SUBTITLE)
                        .build());
    }

    @Test(expected = UnknownTemplateParameterNameException.class)
    public void exceptionOnIncorrectResourceParameterName() {
        resolver.resolveTemplateParameter("%{resource.xxxx}",
                new ContextInfoBuilder()
                        .setSegmentUuid(getSegmentUuid(0))
                        .setSequenceUuid(getSequenceUuid(0, SequenceType.VIDEO))
                        .setSequenceType(SequenceType.VIDEO)
                        .setResourceUuid(getResourceUuid(0, 0, SequenceType.VIDEO, 0))
                        .build());
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnIncorrectResourceParameterSegment() {
        // we have only 2 segments (0 and 1)
        resolver.resolveTemplateParameter("%{resource.essence}",
                new ContextInfoBuilder()
                        .setSegmentUuid(getSegmentUuid(10))
                        .setSequenceUuid(getSequenceUuid(0, SequenceType.VIDEO))
                        .setSequenceType(SequenceType.VIDEO)
                        .setResourceUuid(getResourceUuid(10, 0, SequenceType.VIDEO, 0))
                        .build());
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnIncorrectResourceParameterSequence() {
        // we have only 2 sequences (0 and 1)
        resolver.resolveTemplateParameter("%{resource.essence}",
                new ContextInfoBuilder()
                        .setSegmentUuid(getSegmentUuid(0))
                        .setSequenceUuid(getSequenceUuid(10, SequenceType.VIDEO))
                        .setSequenceType(SequenceType.VIDEO)
                        .setResourceUuid(getResourceUuid(0, 10, SequenceType.VIDEO, 0))
                        .build());
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnIncorrectResourceParameterResource() {
        // we have only 2 resources (0 and 1)
        resolver.resolveTemplateParameter("%{resource.essence}",
                new ContextInfoBuilder()
                        .setSegmentUuid(getSegmentUuid(0))
                        .setSequenceUuid(getSequenceUuid(0, SequenceType.VIDEO))
                        .setSequenceType(SequenceType.VIDEO)
                        .setResourceUuid(getResourceUuid(0, 0, SequenceType.VIDEO, 10))
                        .build());
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void exceptionOnIncorrectResourceParameterType() {
        // we didn't fill subtitle type
        resolver.resolveTemplateParameter("%{resource.essence}",
                new ContextInfoBuilder()
                        .setSegmentUuid(getSegmentUuid(0))
                        .setSequenceUuid(getSequenceUuid(0, SequenceType.SUBTITLE))
                        .setSequenceType(SequenceType.SUBTITLE)
                        .setResourceUuid(getResourceUuid(0, 0, SequenceType.SUBTITLE, 0))
                        .build());
    }


}
