package com.netflix.imfutility.conversion.templateParameter;

import com.netflix.imfutility.Format;
import com.netflix.imfutility.config.ConfigProvider;
import com.netflix.imfutility.conversion.ConversionProvider;
import com.netflix.imfutility.conversion.templateParameter.context.ResourceKey;
import com.netflix.imfutility.conversion.templateParameter.context.SegmentContextParameters;
import com.netflix.imfutility.conversion.templateParameter.context.SequenceContextParameters;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.util.ConfigUtils;
import com.netflix.imfutility.util.ConversionUtils;
import com.netflix.imfutility.xsd.conversion.SequenceType;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests whether each context can initialize or create parameters correctly.
 */
public class TemplateParameterInitializationTest {

    private static ConfigProvider configProvider;
    private static ConversionProvider conversionProvider;


    @BeforeClass
    public static void setUpAll() throws Exception {
        configProvider = new ConfigProvider(ConfigUtils.getCorrectConfigXml());
        conversionProvider = new ConversionProvider(ConversionUtils.getCorrectConversionXml(), Format.DPP);
    }

    @Test
    public void testAddOutputParameter() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        contextProvider.getOutputContext()
                .addParameter("outputParam1", "outputParamValue1")
                .addParameter("outputParam2", "outputParamValue2");

        assertTrue(contextProvider.getOutputContext().getAllParameters().contains("outputParamValue1"));
        assertTrue(contextProvider.getOutputContext().getAllParameters().contains("outputParamValue2"));
    }

    @Test
    public void testAddDynamicParameterSimple() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        contextProvider.getDynamicContext()
                .addParameter("addDynamicSimple1", "addDynamicValue1", ContextInfo.EMPTY)
                .addParameter("addDynamicSimple2", "addDynamicValue2", ContextInfo.EMPTY);

        assertTrue(contextProvider.getDynamicContext().getAllParameters().contains("addDynamicValue1"));
        assertTrue(contextProvider.getDynamicContext().getAllParameters().contains("addDynamicValue2"));
    }

    @Test
    public void testAppendDynamicParameterSimple() {
        TemplateParameterContextProvider contextProvider = createContextProvider();

        contextProvider.getDynamicContext()
                .appendParameter("appendDynamicSimple1", "appendDynamicValue1_1", ContextInfo.EMPTY)
                .appendParameter("appendDynamicSimple1", "_2", ContextInfo.EMPTY)
                .appendParameter("appendDynamicSimple1", "_3", ContextInfo.EMPTY)

                .appendParameter("appendDynamicSimple2", "appendDynamicValue2_1", ContextInfo.EMPTY)
                .appendParameter("appendDynamicSimple2", "_2", ContextInfo.EMPTY)
                .appendParameter("appendDynamicSimple2", "_3", ContextInfo.EMPTY);

        assertTrue(contextProvider.getDynamicContext().getAllParameters().contains("appendDynamicValue1_1_2_3"));
        assertTrue(contextProvider.getDynamicContext().getAllParameters().contains("appendDynamicValue2_1_2_3"));
    }

    @Test
    public void testAddDynamicParameterWithParams() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        contextProvider.getSegmentContext()
                .initSegment("urn:uuid:segm:1111");
        contextProvider.getSequenceContext()
                .initSequence(SequenceType.AUDIO, "urn:uuid:seq:1111")
                .initSequence(SequenceType.AUDIO, "urn:uuid:seq:2222");
        contextProvider.getResourceContext()
                .initResource(ResourceKey.create("urn:uuid:segm:1111", "urn:uuid:seq:1111", SequenceType.AUDIO), "urn:uuid:res:1111")
                .initResource(ResourceKey.create("urn:uuid:segm:1111", "urn:uuid:seq:2222", SequenceType.AUDIO), "urn:uuid:res:2222");

        contextProvider.getDynamicContext().addParameter(
                "addDynamicWithParam1",
                "%{tmp.tmpParamSimple}",
                ContextInfo.EMPTY);
        contextProvider.getDynamicContext().addParameter(
                "addDynamicWithParam2",
                "%{segm.num}-%{seq.num}-%{seq.type}-%{resource.num}-%{tmp.tmpParamSimple}",
                new ContextInfoBuilder()
                        .setSegmentUuid("urn:uuid:segm:1111")
                        .setSequenceUuid("urn:uuid:seq:2222")
                        .setResourceUuid("urn:uuid:res:2222")
                        .setSequenceType(SequenceType.AUDIO).build());

        assertTrue(contextProvider.getDynamicContext().getAllParameters().contains("tmpParamSimple"));
        assertTrue(contextProvider.getDynamicContext().getAllParameters().contains("0-1-audio-0-tmpParamSimple"));
    }

    @Test
    public void testAppendDynamicParameterWithParams() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        contextProvider.getSegmentContext()
                .initSegment("urn:uuid:segm:1111")
                .initSegment("urn:uuid:segm:2222");
        contextProvider.getSequenceContext()
                .initSequence(SequenceType.AUDIO, "urn:uuid:seq:1111")
                .initSequence(SequenceType.AUDIO, "urn:uuid:seq:2222");
        contextProvider.getResourceContext()
                .initResource(ResourceKey.create("urn:uuid:segm:1111", "urn:uuid:seq:1111", SequenceType.AUDIO), "urn:uuid:res:1111")
                .initResource(ResourceKey.create("urn:uuid:segm:2222", "urn:uuid:seq:2222", SequenceType.AUDIO), "urn:uuid:res:4444");

        contextProvider.getDynamicContext().appendParameter("appendDynamicWithParam1", "%{tmp.tmpParamSimple}_1", ContextInfo.EMPTY);
        contextProvider.getDynamicContext().appendParameter("appendDynamicWithParam1", "_%{tmp.tmpParamSimple}_2", ContextInfo.EMPTY);

        contextProvider.getDynamicContext().appendParameter(
                "appendDynamicWithParam2",
                "%{segm.num}-%{seq.num}-%{seq.type}-%{resource.num}-%{tmp.tmpParamSimple}",
                new ContextInfoBuilder()
                        .setSegmentUuid("urn:uuid:segm:1111")
                        .setSequenceUuid("urn:uuid:seq:1111")
                        .setResourceUuid("urn:uuid:res:1111")
                        .setSequenceType(SequenceType.AUDIO).build());

        contextProvider.getDynamicContext().appendParameter(
                "appendDynamicWithParam2",
                "_%{segm.num}-%{seq.num}-%{seq.type}-%{resource.num}-%{tmp.tmpParamSimple}",
                new ContextInfoBuilder()
                        .setSegmentUuid("urn:uuid:segm:2222")
                        .setSequenceUuid("urn:uuid:seq:2222")
                        .setResourceUuid("urn:uuid:res:4444")
                        .setSequenceType(SequenceType.AUDIO).build());

        assertTrue(
                contextProvider.getDynamicContext().getAllParameters().contains(
                        "tmpParamSimple_1_tmpParamSimple_2"));
        assertTrue(
                contextProvider.getDynamicContext().getAllParameters().contains(
                        "0-0-audio-0-tmpParamSimple_1-1-audio-0-tmpParamSimple"));
    }

    @Test
    public void testInitSegmentsOrder() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        contextProvider.getSegmentContext().initSegment("urn:uuid:3333");
        contextProvider.getSegmentContext().initSegment("urn:uuid:1111");
        contextProvider.getSegmentContext().initSegment("urn:uuid:2222");

        assertEquals(3, contextProvider.getSegmentContext().getSegmentsNum());
        assertArrayEquals(
                new String[]{"urn:uuid:3333", "urn:uuid:1111", "urn:uuid:2222"},
                contextProvider.getSegmentContext().getUuids().toArray());
    }

    @Test
    public void testInitSegmentsNoDuplicate() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        contextProvider.getSegmentContext().initSegment("urn:uuid:3333");
        contextProvider.getSegmentContext().initSegment("urn:uuid:1111");
        contextProvider.getSegmentContext().initSegment("urn:uuid:2222");
        contextProvider.getSegmentContext().initSegment("urn:uuid:3333");
        contextProvider.getSegmentContext().initSegment("urn:uuid:1111");

        assertEquals(3, contextProvider.getSegmentContext().getSegmentsNum());
        assertArrayEquals(
                new String[]{"urn:uuid:3333", "urn:uuid:1111", "urn:uuid:2222"},
                contextProvider.getSegmentContext().getUuids().toArray());
    }

    @Test
    public void testInitDefaultSegmentParameters() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        contextProvider.getSegmentContext().initSegment("urn:uuid:3333");
        contextProvider.getSegmentContext().initSegment("urn:uuid:1111");
        contextProvider.getSegmentContext().initSegment("urn:uuid:2222");

        assertDefaultSegmentParameters(contextProvider, 0, "urn:uuid:3333");
        assertDefaultSegmentParameters(contextProvider, 1, "urn:uuid:1111");
        assertDefaultSegmentParameters(contextProvider, 2, "urn:uuid:2222");
    }

    private void assertDefaultSegmentParameters(TemplateParameterContextProvider contextProvider, int segmNum,
                                                String segmUuid) {
        assertEquals(
                String.valueOf(segmNum),
                contextProvider.getSegmentContext().resolveTemplateParameter(
                        new TemplateParameter("%{segm.num}"),
                        new ContextInfoBuilder().setSegmentUuid(segmUuid).build()));
        assertEquals(
                segmUuid,
                contextProvider.getSegmentContext().resolveTemplateParameter(
                        new TemplateParameter("%{segm.uuid}"),
                        new ContextInfoBuilder().setSegmentUuid(segmUuid).build()));
    }

    @Test
    public void testAddSegmentParametersInitsSegment() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        contextProvider.getSegmentContext().addSegmentParameter("urn:uuid:3333", SegmentContextParameters.NUM, "5");

        assertEquals(1, contextProvider.getSegmentContext().getSegmentsNum());
        assertArrayEquals(
                new String[]{"urn:uuid:3333"},
                contextProvider.getSegmentContext().getUuids().toArray());

        assertDefaultSegmentParameters(contextProvider, 5, "urn:uuid:3333");
    }

    @Test
    public void testInitSequenceOrder() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        contextProvider.getSequenceContext().initSequence(SequenceType.VIDEO, "urn:uuid:3333");
        contextProvider.getSequenceContext().initSequence(SequenceType.VIDEO, "urn:uuid:1111");
        contextProvider.getSequenceContext().initSequence(SequenceType.VIDEO, "urn:uuid:2222");

        contextProvider.getSequenceContext().initSequence(SequenceType.AUDIO, "urn:uuid:2222");
        contextProvider.getSequenceContext().initSequence(SequenceType.AUDIO, "urn:uuid:3333");
        contextProvider.getSequenceContext().initSequence(SequenceType.AUDIO, "urn:uuid:1111");
        contextProvider.getSequenceContext().initSequence(SequenceType.AUDIO, "urn:uuid:4444");

        assertEquals(3, contextProvider.getSequenceContext().getSequenceCount(SequenceType.VIDEO));
        assertArrayEquals(
                new String[]{"urn:uuid:3333", "urn:uuid:1111", "urn:uuid:2222"},
                contextProvider.getSequenceContext().getUuids(SequenceType.VIDEO).toArray());

        assertEquals(4, contextProvider.getSequenceContext().getSequenceCount(SequenceType.AUDIO));
        assertArrayEquals(
                new String[]{"urn:uuid:2222", "urn:uuid:3333", "urn:uuid:1111", "urn:uuid:4444"},
                contextProvider.getSequenceContext().getUuids(SequenceType.AUDIO).toArray());
    }

    @Test
    public void testInitSequenceNoDuplicate() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        contextProvider.getSequenceContext().initSequence(SequenceType.VIDEO, "urn:uuid:3333");
        contextProvider.getSequenceContext().initSequence(SequenceType.VIDEO, "urn:uuid:1111");
        contextProvider.getSequenceContext().initSequence(SequenceType.VIDEO, "urn:uuid:2222");
        contextProvider.getSequenceContext().initSequence(SequenceType.VIDEO, "urn:uuid:3333");
        contextProvider.getSequenceContext().initSequence(SequenceType.VIDEO, "urn:uuid:2222");

        contextProvider.getSequenceContext().initSequence(SequenceType.AUDIO, "urn:uuid:2222");
        contextProvider.getSequenceContext().initSequence(SequenceType.AUDIO, "urn:uuid:3333");
        contextProvider.getSequenceContext().initSequence(SequenceType.AUDIO, "urn:uuid:3333");
        contextProvider.getSequenceContext().initSequence(SequenceType.AUDIO, "urn:uuid:3333");
        contextProvider.getSequenceContext().initSequence(SequenceType.AUDIO, "urn:uuid:1111");
        contextProvider.getSequenceContext().initSequence(SequenceType.AUDIO, "urn:uuid:4444");
        contextProvider.getSequenceContext().initSequence(SequenceType.AUDIO, "urn:uuid:2222");

        assertEquals(3, contextProvider.getSequenceContext().getSequenceCount(SequenceType.VIDEO));
        assertArrayEquals(
                new String[]{"urn:uuid:3333", "urn:uuid:1111", "urn:uuid:2222"},
                contextProvider.getSequenceContext().getUuids(SequenceType.VIDEO).toArray());

        assertEquals(4, contextProvider.getSequenceContext().getSequenceCount(SequenceType.AUDIO));
        assertArrayEquals(
                new String[]{"urn:uuid:2222", "urn:uuid:3333", "urn:uuid:1111", "urn:uuid:4444"},
                contextProvider.getSequenceContext().getUuids(SequenceType.AUDIO).toArray());
    }

    @Test
    public void testInitDefaultSequenceParameters() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        contextProvider.getSequenceContext().initSequence(SequenceType.VIDEO, "urn:uuid:3333");
        contextProvider.getSequenceContext().initSequence(SequenceType.VIDEO, "urn:uuid:1111");

        contextProvider.getSequenceContext().initSequence(SequenceType.AUDIO, "urn:uuid:2222");
        contextProvider.getSequenceContext().initSequence(SequenceType.AUDIO, "urn:uuid:3333");

        assertDefaultSequenceParameters(contextProvider, 0, "urn:uuid:3333", SequenceType.VIDEO);
        assertDefaultSequenceParameters(contextProvider, 1, "urn:uuid:1111", SequenceType.VIDEO);
        assertDefaultSequenceParameters(contextProvider, 0, "urn:uuid:2222", SequenceType.AUDIO);
        assertDefaultSequenceParameters(contextProvider, 1, "urn:uuid:3333", SequenceType.AUDIO);
    }

    private void assertDefaultSequenceParameters(TemplateParameterContextProvider contextProvider, int seqNum,
                                                 String seqUuid, SequenceType seqType) {
        assertEquals(
                String.valueOf(seqNum),
                contextProvider.getSequenceContext().resolveTemplateParameter(
                        new TemplateParameter("%{seq.num}"),
                        new ContextInfoBuilder().setSequenceUuid(seqUuid).setSequenceType(seqType).build()));
        assertEquals(
                seqUuid,
                contextProvider.getSequenceContext().resolveTemplateParameter(
                        new TemplateParameter("%{seq.uuid}"),
                        new ContextInfoBuilder().setSequenceUuid(seqUuid).setSequenceType(seqType).build()));
        assertEquals(
                seqType.value(),
                contextProvider.getSequenceContext().resolveTemplateParameter(
                        new TemplateParameter("%{seq.type}"),
                        new ContextInfoBuilder().setSequenceUuid(seqUuid).setSequenceType(seqType).build()));
    }

    @Test
    public void testAddSequenceParameterInitsSequence() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, "urn:uuid:3333",
                SequenceContextParameters.NUM, "5");

        assertEquals(1, contextProvider.getSequenceContext().getSequenceCount(SequenceType.VIDEO));
        assertArrayEquals(
                new String[]{"urn:uuid:3333"},
                contextProvider.getSequenceContext().getUuids(SequenceType.VIDEO).toArray());

        assertDefaultSequenceParameters(contextProvider, 5, "urn:uuid:3333", SequenceType.VIDEO);
    }


    private TemplateParameterContextProvider createContextProvider() {
        return new TemplateParameterContextProvider(
                configProvider.getConfig(), conversionProvider.getFormat(), ".");
    }

}
