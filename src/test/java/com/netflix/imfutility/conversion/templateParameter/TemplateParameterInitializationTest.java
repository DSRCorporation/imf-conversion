package com.netflix.imfutility.conversion.templateParameter;

import com.netflix.imfutility.Format;
import com.netflix.imfutility.config.ConfigProvider;
import com.netflix.imfutility.conversion.ConversionProvider;
import com.netflix.imfutility.conversion.templateParameter.context.SegmentContextParameters;
import com.netflix.imfutility.conversion.templateParameter.context.SequenceContextParameters;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.util.ConfigUtils;
import com.netflix.imfutility.util.ConversionUtils;
import com.netflix.imfutility.xsd.conversion.SequenceType;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.netflix.imfutility.util.TemplateParameterContextCreator.*;
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
    public void testAddDynamicParameterWithParamsInValue() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        fillCPLContext(contextProvider, 2, 2, 2);

        contextProvider.getDynamicContext().addParameter(
                "addDynamicWithParam1",
                "%{tmp.tmpParamSimple}",
                ContextInfo.EMPTY);
        contextProvider.getDynamicContext().addParameter(
                "addDynamicWithParam2",
                "%{segm.num}-%{seq.num}-%{seq.type}-%{resource.num}-%{tmp.tmpParamSimple}",
                new ContextInfoBuilder()
                        .setSegmentUuid(getSegmentUuid(0))
                        .setSequenceUuid(getSequenceUuid(1, SequenceType.AUDIO))
                        .setResourceUuid(getResourceUuid(0, 1, SequenceType.AUDIO, 1))
                        .setSequenceType(SequenceType.AUDIO).build());

        assertEquals("tmpParamSimple", contextProvider.getDynamicContext().getParameterValue("addDynamicWithParam1"));
        assertEquals("0-1-audio-1-tmpParamSimple", contextProvider.getDynamicContext().getParameterValue("addDynamicWithParam2"));
    }

    @Test
    public void testAddDynamicParameterWithParamsInName() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        fillCPLContext(contextProvider, 2, 2, 2);

        contextProvider.getDynamicContext().addParameter(
                "name-%{tmp.tmpParamSimple}",
                "value1",
                ContextInfo.EMPTY);
        contextProvider.getDynamicContext().addParameter(
                "name-%{segm.num}-%{seq.num}-%{seq.type}-%{resource.num}-%{tmp.tmpParamSimple}",
                "value2",
                new ContextInfoBuilder()
                        .setSegmentUuid(getSegmentUuid(0))
                        .setSequenceUuid(getSequenceUuid(1, SequenceType.AUDIO))
                        .setResourceUuid(getResourceUuid(0, 1, SequenceType.AUDIO, 1))
                        .setSequenceType(SequenceType.AUDIO).build());

        assertEquals("value1", contextProvider.getDynamicContext().getParameterValue("name-tmpParamSimple"));
        assertEquals("value2", contextProvider.getDynamicContext().getParameterValue("name-0-1-audio-1-tmpParamSimple"));
    }

    @Test
    public void testAddDynamicParameterWithParamsInNameAndValue() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        fillCPLContext(contextProvider, 2, 2, 2);

        contextProvider.getDynamicContext().addParameter(
                "name-%{tmp.tmpParamSimple}",
                "%{tmp.tmpParamSimple}",
                ContextInfo.EMPTY);
        contextProvider.getDynamicContext().addParameter(
                "name-%{segm.num}-%{seq.num}-%{seq.type}-%{resource.num}-%{tmp.tmpParamSimple}",
                "%{segm.num}-%{seq.num}-%{seq.type}-%{resource.num}-%{tmp.tmpParamSimple}",
                new ContextInfoBuilder()
                        .setSegmentUuid(getSegmentUuid(0))
                        .setSequenceUuid(getSequenceUuid(1, SequenceType.AUDIO))
                        .setResourceUuid(getResourceUuid(0, 1, SequenceType.AUDIO, 1))
                        .setSequenceType(SequenceType.AUDIO).build());

        assertEquals("tmpParamSimple",
                contextProvider.getDynamicContext().getParameterValue("name-tmpParamSimple"));
        assertEquals("0-1-audio-1-tmpParamSimple",
                contextProvider.getDynamicContext().getParameterValue("name-0-1-audio-1-tmpParamSimple"));
    }

    @Test
    public void testAppendDynamicParameterWithParamsInValue() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        fillCPLContext(contextProvider, 2, 2, 2);

        contextProvider.getDynamicContext().appendParameter("appendDynamicWithParam1", "%{tmp.tmpParamSimple}_1", ContextInfo.EMPTY);
        contextProvider.getDynamicContext().appendParameter("appendDynamicWithParam1", "_%{tmp.tmpParamSimple}_2", ContextInfo.EMPTY);

        contextProvider.getDynamicContext().appendParameter(
                "appendDynamicWithParam2",
                "%{segm.num}-%{seq.num}-%{seq.type}-%{resource.num}-%{tmp.tmpParamSimple}",
                new ContextInfoBuilder()
                        .setSegmentUuid(getSegmentUuid(0))
                        .setSequenceUuid(getSequenceUuid(0, SequenceType.AUDIO))
                        .setResourceUuid(getResourceUuid(0, 0, SequenceType.AUDIO, 0))
                        .setSequenceType(SequenceType.AUDIO).build());

        contextProvider.getDynamicContext().appendParameter(
                "appendDynamicWithParam2",
                "_%{segm.num}-%{seq.num}-%{seq.type}-%{resource.num}-%{tmp.tmpParamSimple}",
                new ContextInfoBuilder()
                        .setSegmentUuid(getSegmentUuid(1))
                        .setSequenceUuid(getSequenceUuid(1, SequenceType.VIDEO))
                        .setResourceUuid(getResourceUuid(1, 1, SequenceType.VIDEO, 1))
                        .setSequenceType(SequenceType.VIDEO).build());

        assertEquals(2, contextProvider.getDynamicContext().getAllParameters().size());
        assertEquals("tmpParamSimple_1_tmpParamSimple_2",
                contextProvider.getDynamicContext().getParameterValue("appendDynamicWithParam1"));
        assertEquals("0-0-audio-0-tmpParamSimple_1-1-video-1-tmpParamSimple",
                contextProvider.getDynamicContext().getParameterValue("appendDynamicWithParam2"));
    }

    @Test
    public void testAppendDynamicParameterWithParamsInName() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        fillCPLContext(contextProvider, 2, 2, 2);

        contextProvider.getDynamicContext().appendParameter("%{tmp.tmpParamSimple}", "appendDynamicWithParam1", ContextInfo.EMPTY);
        contextProvider.getDynamicContext().appendParameter("%{tmp.tmpParamSimple}", "_appendDynamicWithParam1", ContextInfo.EMPTY);

        contextProvider.getDynamicContext().appendParameter(
                "%{segm.num}-%{seq.num}-%{seq.type}-%{resource.num}-%{tmp.tmpParamSimple}",
                "appendDynamicWithParam2",
                new ContextInfoBuilder()
                        .setSegmentUuid(getSegmentUuid(0))
                        .setSequenceUuid(getSequenceUuid(0, SequenceType.AUDIO))
                        .setResourceUuid(getResourceUuid(0, 0, SequenceType.AUDIO, 0))
                        .setSequenceType(SequenceType.AUDIO).build());

        contextProvider.getDynamicContext().appendParameter(
                "%{segm.num}-%{seq.num}-%{seq.type}-%{resource.num}-%{tmp.tmpParamSimple}",
                "_appendDynamicWithParam2",
                new ContextInfoBuilder()
                        .setSegmentUuid(getSegmentUuid(0))
                        .setSequenceUuid(getSequenceUuid(0, SequenceType.AUDIO))
                        .setResourceUuid(getResourceUuid(0, 0, SequenceType.AUDIO, 0))
                        .setSequenceType(SequenceType.AUDIO).build());

        assertEquals(2, contextProvider.getDynamicContext().getAllParameters().size());
        assertEquals("appendDynamicWithParam1_appendDynamicWithParam1",
                contextProvider.getDynamicContext().getParameterValue("tmpParamSimple"));
        assertEquals("appendDynamicWithParam2_appendDynamicWithParam2",
                contextProvider.getDynamicContext().getParameterValue("0-0-audio-0-tmpParamSimple"));
    }

    @Test
    public void testAppendDynamicParameterWithParamsInNameAndValue() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        fillCPLContext(contextProvider, 2, 2, 2);

        contextProvider.getDynamicContext().appendParameter(
                "name-%{segm.num}-%{seq.num}-%{seq.type}-%{resource.num}-%{tmp.tmpParamSimple}",
                "%{segm.num}-%{seq.num}-%{seq.type}-%{resource.num}-%{tmp.tmpParamSimple}",
                new ContextInfoBuilder()
                        .setSegmentUuid(getSegmentUuid(0))
                        .setSequenceUuid(getSequenceUuid(0, SequenceType.AUDIO))
                        .setResourceUuid(getResourceUuid(0, 0, SequenceType.AUDIO, 0))
                        .setSequenceType(SequenceType.AUDIO).build());

        contextProvider.getDynamicContext().appendParameter(
                "name-%{segm.num}-%{seq.num}-%{seq.type}-%{resource.num}-%{tmp.tmpParamSimple}",
                "_%{segm.num}-%{seq.num}-%{seq.type}-%{resource.num}-%{tmp.tmpParamSimple}",
                new ContextInfoBuilder()
                        .setSegmentUuid(getSegmentUuid(0))
                        .setSequenceUuid(getSequenceUuid(0, SequenceType.AUDIO))
                        .setResourceUuid(getResourceUuid(0, 0, SequenceType.AUDIO, 0))
                        .setSequenceType(SequenceType.AUDIO).build());

        assertEquals(1, contextProvider.getDynamicContext().getAllParameters().size());
        assertEquals("0-0-audio-0-tmpParamSimple_0-0-audio-0-tmpParamSimple",
                contextProvider.getDynamicContext().getParameterValue("name-0-0-audio-0-tmpParamSimple"));
    }

    @Test
    public void testInitSegmentsOrder() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        contextProvider.getSegmentContext().initSegment(SegmentUUID.create("urn:uuid:3333"));
        contextProvider.getSegmentContext().initSegment(SegmentUUID.create("urn:uuid:1111"));
        contextProvider.getSegmentContext().initSegment(SegmentUUID.create("urn:uuid:2222"));

        assertEquals(3, contextProvider.getSegmentContext().getSegmentsNum());
        assertArrayEquals(
                new SegmentUUID[]{
                        SegmentUUID.create("urn:uuid:3333"),
                        SegmentUUID.create("urn:uuid:1111"),
                        SegmentUUID.create("urn:uuid:2222")},
                contextProvider.getSegmentContext().getUuids().toArray());
    }

    @Test
    public void testInitSegmentsNoDuplicate() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        contextProvider.getSegmentContext().initSegment(SegmentUUID.create("urn:uuid:3333"));
        contextProvider.getSegmentContext().initSegment(SegmentUUID.create("urn:uuid:1111"));
        contextProvider.getSegmentContext().initSegment(SegmentUUID.create("urn:uuid:2222"));
        contextProvider.getSegmentContext().initSegment(SegmentUUID.create("urn:uuid:3333"));
        contextProvider.getSegmentContext().initSegment(SegmentUUID.create("urn:uuid:1111"));

        assertEquals(3, contextProvider.getSegmentContext().getSegmentsNum());
        assertArrayEquals(
                new SegmentUUID[]{
                        SegmentUUID.create("urn:uuid:3333"),
                        SegmentUUID.create("urn:uuid:1111"),
                        SegmentUUID.create("urn:uuid:2222")},
                contextProvider.getSegmentContext().getUuids().toArray());
    }

    @Test
    public void testInitDefaultSegmentParameters() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        contextProvider.getSegmentContext().initSegment(
                SegmentUUID.create("urn:uuid:3333"));
        contextProvider.getSegmentContext().initSegment(
                SegmentUUID.create("urn:uuid:1111"));
        contextProvider.getSegmentContext().initSegment(
                SegmentUUID.create("urn:uuid:2222"));

        assertDefaultSegmentParameters(contextProvider, 0, SegmentUUID.create("urn:uuid:3333"));
        assertDefaultSegmentParameters(contextProvider, 1, SegmentUUID.create("urn:uuid:1111"));
        assertDefaultSegmentParameters(contextProvider, 2, SegmentUUID.create("urn:uuid:2222"));
    }

    private void assertDefaultSegmentParameters(TemplateParameterContextProvider contextProvider, int segmNum,
                                                SegmentUUID segmUuid) {
        assertEquals(
                String.valueOf(segmNum),
                contextProvider.getSegmentContext().resolveTemplateParameter(
                        new TemplateParameter("%{segm.num}"),
                        new ContextInfoBuilder().setSegmentUuid(segmUuid).build()));
        assertEquals(
                segmUuid.getUuid(),
                contextProvider.getSegmentContext().resolveTemplateParameter(
                        new TemplateParameter("%{segm.uuid}"),
                        new ContextInfoBuilder().setSegmentUuid(segmUuid).build()));
    }

    @Test
    public void testAddSegmentParametersInitsSegment() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        contextProvider.getSegmentContext().addSegmentParameter(
                SegmentUUID.create("urn:uuid:3333"), SegmentContextParameters.NUM, "5");

        assertEquals(1, contextProvider.getSegmentContext().getSegmentsNum());
        assertArrayEquals(
                new SegmentUUID[]{SegmentUUID.create("urn:uuid:3333")},
                contextProvider.getSegmentContext().getUuids().toArray());

        assertDefaultSegmentParameters(contextProvider, 5, SegmentUUID.create("urn:uuid:3333"));
    }

    @Test
    public void testInitSequenceOrder() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        contextProvider.getSequenceContext().initSequence(SequenceType.VIDEO, SequenceUUID.create("urn:uuid:3333"));
        contextProvider.getSequenceContext().initSequence(SequenceType.VIDEO, SequenceUUID.create("urn:uuid:1111"));
        contextProvider.getSequenceContext().initSequence(SequenceType.VIDEO, SequenceUUID.create("urn:uuid:2222"));

        contextProvider.getSequenceContext().initSequence(SequenceType.AUDIO, SequenceUUID.create("urn:uuid:2222"));
        contextProvider.getSequenceContext().initSequence(SequenceType.AUDIO, SequenceUUID.create("urn:uuid:3333"));
        contextProvider.getSequenceContext().initSequence(SequenceType.AUDIO, SequenceUUID.create("urn:uuid:1111"));
        contextProvider.getSequenceContext().initSequence(SequenceType.AUDIO, SequenceUUID.create("urn:uuid:4444"));

        assertEquals(3, contextProvider.getSequenceContext().getSequenceCount(SequenceType.VIDEO));
        assertArrayEquals(
                new SequenceUUID[]{
                        SequenceUUID.create("urn:uuid:3333"),
                        SequenceUUID.create("urn:uuid:1111"),
                        SequenceUUID.create("urn:uuid:2222")},
                contextProvider.getSequenceContext().getUuids(SequenceType.VIDEO).toArray());

        assertEquals(4, contextProvider.getSequenceContext().getSequenceCount(SequenceType.AUDIO));
        assertArrayEquals(
                new SequenceUUID[]{
                        SequenceUUID.create("urn:uuid:2222"),
                        SequenceUUID.create("urn:uuid:3333"),
                        SequenceUUID.create("urn:uuid:1111"),
                        SequenceUUID.create("urn:uuid:4444")},
                contextProvider.getSequenceContext().getUuids(SequenceType.AUDIO).toArray());
    }

    @Test
    public void testInitSequenceNoDuplicate() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        contextProvider.getSequenceContext().initSequence(SequenceType.VIDEO, SequenceUUID.create("urn:uuid:3333"));
        contextProvider.getSequenceContext().initSequence(SequenceType.VIDEO, SequenceUUID.create("urn:uuid:1111"));
        contextProvider.getSequenceContext().initSequence(SequenceType.VIDEO, SequenceUUID.create("urn:uuid:2222"));
        contextProvider.getSequenceContext().initSequence(SequenceType.VIDEO, SequenceUUID.create("urn:uuid:3333"));
        contextProvider.getSequenceContext().initSequence(SequenceType.VIDEO, SequenceUUID.create("urn:uuid:2222"));

        contextProvider.getSequenceContext().initSequence(SequenceType.AUDIO, SequenceUUID.create("urn:uuid:2222"));
        contextProvider.getSequenceContext().initSequence(SequenceType.AUDIO, SequenceUUID.create("urn:uuid:3333"));
        contextProvider.getSequenceContext().initSequence(SequenceType.AUDIO, SequenceUUID.create("urn:uuid:3333"));
        contextProvider.getSequenceContext().initSequence(SequenceType.AUDIO, SequenceUUID.create("urn:uuid:3333"));
        contextProvider.getSequenceContext().initSequence(SequenceType.AUDIO, SequenceUUID.create("urn:uuid:1111"));
        contextProvider.getSequenceContext().initSequence(SequenceType.AUDIO, SequenceUUID.create("urn:uuid:4444"));
        contextProvider.getSequenceContext().initSequence(SequenceType.AUDIO, SequenceUUID.create("urn:uuid:2222"));

        assertEquals(3, contextProvider.getSequenceContext().getSequenceCount(SequenceType.VIDEO));
        assertArrayEquals(
                new SequenceUUID[]{
                        SequenceUUID.create("urn:uuid:3333"),
                        SequenceUUID.create("urn:uuid:1111"),
                        SequenceUUID.create("urn:uuid:2222")},
                contextProvider.getSequenceContext().getUuids(SequenceType.VIDEO).toArray());

        assertEquals(4, contextProvider.getSequenceContext().getSequenceCount(SequenceType.AUDIO));
        assertArrayEquals(
                new SequenceUUID[]{
                        SequenceUUID.create("urn:uuid:2222"),
                        SequenceUUID.create("urn:uuid:3333"),
                        SequenceUUID.create("urn:uuid:1111"),
                        SequenceUUID.create("urn:uuid:4444")},
                contextProvider.getSequenceContext().getUuids(SequenceType.AUDIO).toArray());
    }

    @Test
    public void testInitDefaultSequenceParameters() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        contextProvider.getSequenceContext().initSequence(SequenceType.VIDEO, SequenceUUID.create("urn:uuid:3333"));
        contextProvider.getSequenceContext().initSequence(SequenceType.VIDEO, SequenceUUID.create("urn:uuid:1111"));

        contextProvider.getSequenceContext().initSequence(SequenceType.AUDIO, SequenceUUID.create("urn:uuid:2222"));
        contextProvider.getSequenceContext().initSequence(SequenceType.AUDIO, SequenceUUID.create("urn:uuid:3333"));

        assertDefaultSequenceParameters(contextProvider, 0, SequenceUUID.create("urn:uuid:3333"), SequenceType.VIDEO);
        assertDefaultSequenceParameters(contextProvider, 1, SequenceUUID.create("urn:uuid:1111"), SequenceType.VIDEO);
        assertDefaultSequenceParameters(contextProvider, 0, SequenceUUID.create("urn:uuid:2222"), SequenceType.AUDIO);
        assertDefaultSequenceParameters(contextProvider, 1, SequenceUUID.create("urn:uuid:3333"), SequenceType.AUDIO);
    }

    private void assertDefaultSequenceParameters(TemplateParameterContextProvider contextProvider, int seqNum,
                                                 SequenceUUID seqUuid, SequenceType seqType) {
        assertEquals(
                String.valueOf(seqNum),
                contextProvider.getSequenceContext().resolveTemplateParameter(
                        new TemplateParameter("%{seq.num}"),
                        new ContextInfoBuilder().setSequenceUuid(seqUuid).setSequenceType(seqType).build()));
        assertEquals(
                seqUuid.getUuid(),
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
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("urn:uuid:3333"),
                SequenceContextParameters.NUM, "5");

        assertEquals(1, contextProvider.getSequenceContext().getSequenceCount(SequenceType.VIDEO));
        assertArrayEquals(
                new SequenceUUID[]{SequenceUUID.create("urn:uuid:3333")},
                contextProvider.getSequenceContext().getUuids(SequenceType.VIDEO).toArray());

        assertDefaultSequenceParameters(contextProvider, 5, SequenceUUID.create("urn:uuid:3333"), SequenceType.VIDEO);
    }


    private TemplateParameterContextProvider createContextProvider() {
        return new TemplateParameterContextProvider(
                configProvider.getConfig(), conversionProvider.getFormat(), ".");
    }

}
