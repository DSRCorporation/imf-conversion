package com.netflix.imfutility.conversion.templateParameter;

import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SegmentContextParameters;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.xsd.conversion.SequenceType;
import org.junit.Test;

import static com.netflix.imfutility.util.TemplateParameterContextCreator.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Tests whether each context can initialize or create parameters correctly.
 */
public class TemplateParameterInitializationTest {

    @Test
    public void testAddDynamicParameterSimple() throws Exception {
        TemplateParameterContextProvider contextProvider = createDefaultTemplateParameterContextProvider();
        contextProvider.getDynamicContext()
                .addParameter("addDynamicSimple1", "addDynamicValue1", ContextInfo.EMPTY)
                .addParameter("addDynamicSimple2", "addDynamicValue2", ContextInfo.EMPTY);

        assertEquals("addDynamicValue1", contextProvider.getDynamicContext().getParameterValueAsString("addDynamicSimple1"));
        assertEquals("addDynamicValue2", contextProvider.getDynamicContext().getParameterValueAsString("addDynamicSimple2"));
        assertEquals(2, contextProvider.getDynamicContext().getAllParametersAsString().size());
    }

    @Test
    public void testAppendDynamicParameterSimple() throws Exception {
        TemplateParameterContextProvider contextProvider = createDefaultTemplateParameterContextProvider();

        contextProvider.getDynamicContext()
                .appendParameter("appendDynamicSimple1", "appendDynamicValue1_1", ContextInfo.EMPTY)
                .appendParameter("appendDynamicSimple1", "_2", ContextInfo.EMPTY)
                .appendParameter("appendDynamicSimple1", "_3", ContextInfo.EMPTY)

                .appendParameter("appendDynamicSimple2", "appendDynamicValue2_1", ContextInfo.EMPTY)
                .appendParameter("appendDynamicSimple2", "_2", ContextInfo.EMPTY)
                .appendParameter("appendDynamicSimple2", "_3", ContextInfo.EMPTY);

        assertEquals("appendDynamicValue1_1_2_3", contextProvider.getDynamicContext().getParameterValueAsString("appendDynamicSimple1"));
        assertEquals("appendDynamicValue2_1_2_3", contextProvider.getDynamicContext().getParameterValueAsString("appendDynamicSimple2"));
        assertEquals(2, contextProvider.getDynamicContext().getAllParametersAsString().size());
    }

    @Test
    public void testAddDynamicParameterWithParamsInValue() throws Exception {
        TemplateParameterContextProvider contextProvider = createDefaultTemplateParameterContextProvider();
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

        assertEquals("tmpParamSimple", contextProvider.getDynamicContext().getParameterValueAsString("addDynamicWithParam1"));
        assertEquals("0-1-audio-1-tmpParamSimple", contextProvider.getDynamicContext().getParameterValueAsString("addDynamicWithParam2"));
    }

    @Test
    public void testAddDynamicParameterWithParamsInName() throws Exception {
        TemplateParameterContextProvider contextProvider = createDefaultTemplateParameterContextProvider();
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

        assertEquals("value1", contextProvider.getDynamicContext().getParameterValueAsString("name-tmpParamSimple"));
        assertEquals("value2", contextProvider.getDynamicContext().getParameterValueAsString("name-0-1-audio-1-tmpParamSimple"));
    }

    @Test
    public void testAddDynamicParameterWithParamsInNameAndValue() throws Exception {
        TemplateParameterContextProvider contextProvider = createDefaultTemplateParameterContextProvider();
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

        assertEquals("tmpParamSimple", contextProvider.getDynamicContext().getParameterValueAsString("name-tmpParamSimple"));
        assertEquals("0-1-audio-1-tmpParamSimple", contextProvider.getDynamicContext().getParameterValueAsString("name-0-1-audio-1-tmpParamSimple"));
    }

    @Test
    public void testAppendDynamicParameterWithParamsInValue() throws Exception {
        TemplateParameterContextProvider contextProvider = createDefaultTemplateParameterContextProvider();
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
                contextProvider.getDynamicContext().getParameterValueAsString("appendDynamicWithParam1"));
        assertEquals("0-0-audio-0-tmpParamSimple_1-1-video-1-tmpParamSimple",
                contextProvider.getDynamicContext().getParameterValueAsString("appendDynamicWithParam2"));
    }

    @Test
    public void testAppendDynamicParameterWithParamsInName() throws Exception {
        TemplateParameterContextProvider contextProvider = createDefaultTemplateParameterContextProvider();
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
                contextProvider.getDynamicContext().getParameterValueAsString("tmpParamSimple"));
        assertEquals("appendDynamicWithParam2_appendDynamicWithParam2",
                contextProvider.getDynamicContext().getParameterValueAsString("0-0-audio-0-tmpParamSimple"));
    }

    @Test
    public void testAppendDynamicParameterWithParamsInNameAndValue() throws Exception {
        TemplateParameterContextProvider contextProvider = createDefaultTemplateParameterContextProvider();
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
                contextProvider.getDynamicContext().getParameterValueAsString("name-0-0-audio-0-tmpParamSimple"));
    }

    @Test
    public void testInitSegmentsOrder() throws Exception {
        TemplateParameterContextProvider contextProvider = createDefaultTemplateParameterContextProvider();
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
    public void testInitSegmentsNoDuplicate() throws Exception {
        TemplateParameterContextProvider contextProvider = createDefaultTemplateParameterContextProvider();
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
    public void testInitDefaultSegmentParameters() throws Exception {
        TemplateParameterContextProvider contextProvider = createDefaultTemplateParameterContextProvider();
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
    public void testAddSegmentParametersInitsSegment() throws Exception {
        TemplateParameterContextProvider contextProvider = createDefaultTemplateParameterContextProvider();
        contextProvider.getSegmentContext().addSegmentParameter(
                SegmentUUID.create("urn:uuid:3333"), SegmentContextParameters.NUM, "5");

        assertEquals(1, contextProvider.getSegmentContext().getSegmentsNum());
        assertArrayEquals(
                new SegmentUUID[]{SegmentUUID.create("urn:uuid:3333")},
                contextProvider.getSegmentContext().getUuids().toArray());

        assertDefaultSegmentParameters(contextProvider, 5, SegmentUUID.create("urn:uuid:3333"));
    }

    @Test
    public void testInitSequenceOrder() throws Exception {
        TemplateParameterContextProvider contextProvider = createDefaultTemplateParameterContextProvider();
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
    public void testInitSequenceNoDuplicate() throws Exception {
        TemplateParameterContextProvider contextProvider = createDefaultTemplateParameterContextProvider();
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
    public void testInitDefaultSequenceParameters() throws Exception {
        TemplateParameterContextProvider contextProvider = createDefaultTemplateParameterContextProvider();
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
    public void testAddSequenceParameterInitsSequence() throws Exception {
        TemplateParameterContextProvider contextProvider = createDefaultTemplateParameterContextProvider();
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("urn:uuid:3333"),
                SequenceContextParameters.NUM, "5");

        assertEquals(1, contextProvider.getSequenceContext().getSequenceCount(SequenceType.VIDEO));
        assertArrayEquals(
                new SequenceUUID[]{SequenceUUID.create("urn:uuid:3333")},
                contextProvider.getSequenceContext().getUuids(SequenceType.VIDEO).toArray());

        assertDefaultSequenceParameters(contextProvider, 5, SequenceUUID.create("urn:uuid:3333"), SequenceType.VIDEO);
    }

}
