package com.netflix.imfutility.conversion.templateParameter;

import com.netflix.imfutility.ConfigProvider;
import com.netflix.imfutility.Format;
import com.netflix.imfutility.conversion.ConversionProvider;
import com.netflix.imfutility.conversion.templateParameter.context.ResourceKey;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.util.ConfigUtils;
import com.netflix.imfutility.util.ConversionUtils;
import com.netflix.imfutility.xsd.conversion.SequenceType;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        contextProvider.getOutputContext().addParameter("outputParam1", "outputParamValue1");
        contextProvider.getOutputContext().addParameter("outputParam2", "outputParamValue2");

        assertTrue(contextProvider.getOutputContext().getAllParameters().contains("outputParamValue1"));
        assertTrue(contextProvider.getOutputContext().getAllParameters().contains("outputParamValue2"));
    }

    @Test
    public void testInitDefaultSegmentParameters() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        contextProvider.getSegmentContext().initDefaultSegmentParameters(3);


        assertEquals(3, contextProvider.getSegmentContext().getSegmentsNum());
        assertEquals(
                "0",
                contextProvider.getSegmentContext().resolveTemplateParameter(
                        new TemplateParameter("%{segm.num}"),
                        new ContextInfoBuilder().setSegment(0).build()));
        assertEquals(
                "1",
                contextProvider.getSegmentContext().resolveTemplateParameter(
                        new TemplateParameter("%{segm.num}"),
                        new ContextInfoBuilder().setSegment(1).build()));
        assertEquals(
                "2",
                contextProvider.getSegmentContext().resolveTemplateParameter(
                        new TemplateParameter("%{segm.num}"),
                        new ContextInfoBuilder().setSegment(2).build()));
    }

    @Test
    public void testInitDefaultSequenceParameters() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        contextProvider.getSequenceContext().initDefaultSequenceParameters(SequenceType.VIDEO, 2);
        contextProvider.getSequenceContext().initDefaultSequenceParameters(SequenceType.AUDIO, 3);


        assertEquals(2, contextProvider.getSequenceContext().getSequenceCount(SequenceType.VIDEO));
        assertEquals(3, contextProvider.getSequenceContext().getSequenceCount(SequenceType.AUDIO));

        assertEquals(
                "0",
                contextProvider.getSequenceContext().resolveTemplateParameter(
                        new TemplateParameter("%{seq.num}"),
                        new ContextInfoBuilder().setSequence(0).setSequenceType(SequenceType.VIDEO).build()));
        assertEquals(
                "video",
                contextProvider.getSequenceContext().resolveTemplateParameter(
                        new TemplateParameter("%{seq.type}"),
                        new ContextInfoBuilder().setSequence(0).setSequenceType(SequenceType.VIDEO).build()));
        assertEquals(
                "1",
                contextProvider.getSequenceContext().resolveTemplateParameter(
                        new TemplateParameter("%{seq.num}"),
                        new ContextInfoBuilder().setSequence(1).setSequenceType(SequenceType.VIDEO).build()));
        assertEquals(
                "video",
                contextProvider.getSequenceContext().resolveTemplateParameter(
                        new TemplateParameter("%{seq.type}"),
                        new ContextInfoBuilder().setSequence(1).setSequenceType(SequenceType.VIDEO).build()));

        assertEquals(
                "0",
                contextProvider.getSequenceContext().resolveTemplateParameter(
                        new TemplateParameter("%{seq.num}"),
                        new ContextInfoBuilder().setSequence(0).setSequenceType(SequenceType.AUDIO).build()));
        assertEquals(
                "audio",
                contextProvider.getSequenceContext().resolveTemplateParameter(
                        new TemplateParameter("%{seq.type}"),
                        new ContextInfoBuilder().setSequence(0).setSequenceType(SequenceType.AUDIO).build()));
        assertEquals(
                "1",
                contextProvider.getSequenceContext().resolveTemplateParameter(
                        new TemplateParameter("%{seq.num}"),
                        new ContextInfoBuilder().setSequence(1).setSequenceType(SequenceType.AUDIO).build()));
        assertEquals(
                "audio",
                contextProvider.getSequenceContext().resolveTemplateParameter(
                        new TemplateParameter("%{seq.type}"),
                        new ContextInfoBuilder().setSequence(1).setSequenceType(SequenceType.AUDIO).build()));
        assertEquals(
                "2",
                contextProvider.getSequenceContext().resolveTemplateParameter(
                        new TemplateParameter("%{seq.num}"),
                        new ContextInfoBuilder().setSequence(2).setSequenceType(SequenceType.AUDIO).build()));
        assertEquals(
                "audio",
                contextProvider.getSequenceContext().resolveTemplateParameter(
                        new TemplateParameter("%{seq.type}"),
                        new ContextInfoBuilder().setSequence(2).setSequenceType(SequenceType.AUDIO).build()));
    }

    @Test
    public void testAddDynamicParameterSimple() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        contextProvider.getDynamicContext().addParameter("addDynamicSimple1", "addDynamicValue1", ContextInfo.EMPTY);
        contextProvider.getDynamicContext().addParameter("addDynamicSimple2", "addDynamicValue2", ContextInfo.EMPTY);

        assertTrue(contextProvider.getDynamicContext().getAllParameters().contains("addDynamicValue1"));
        assertTrue(contextProvider.getDynamicContext().getAllParameters().contains("addDynamicValue2"));
    }

    @Test
    public void testAppendDynamicParameterSimple() {
        TemplateParameterContextProvider contextProvider = createContextProvider();

        contextProvider.getDynamicContext().appendParameter("appendDynamicSimple1", "appendDynamicValue1_1", ContextInfo.EMPTY);
        contextProvider.getDynamicContext().appendParameter("appendDynamicSimple1", "_2", ContextInfo.EMPTY);
        contextProvider.getDynamicContext().appendParameter("appendDynamicSimple1", "_3", ContextInfo.EMPTY);

        contextProvider.getDynamicContext().appendParameter("appendDynamicSimple2", "appendDynamicValue2_1", ContextInfo.EMPTY);
        contextProvider.getDynamicContext().appendParameter("appendDynamicSimple2", "_2", ContextInfo.EMPTY);
        contextProvider.getDynamicContext().appendParameter("appendDynamicSimple2", "_3", ContextInfo.EMPTY);

        assertTrue(contextProvider.getDynamicContext().getAllParameters().contains("appendDynamicValue1_1_2_3"));
        assertTrue(contextProvider.getDynamicContext().getAllParameters().contains("appendDynamicValue2_1_2_3"));
    }

    @Test
    public void testAddDynamicParameterWithParams() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        contextProvider.getSegmentContext().initDefaultSegmentParameters(2);
        contextProvider.getSequenceContext().initDefaultSequenceParameters(SequenceType.AUDIO, 2);
        contextProvider.getResourceContext().initDefaultResourceParameters(new ResourceKey(0, 0, SequenceType.AUDIO), 2);
        contextProvider.getResourceContext().initDefaultResourceParameters(new ResourceKey(0, 1, SequenceType.AUDIO), 2);
        contextProvider.getResourceContext().initDefaultResourceParameters(new ResourceKey(1, 0, SequenceType.AUDIO), 2);
        contextProvider.getResourceContext().initDefaultResourceParameters(new ResourceKey(1, 1, SequenceType.AUDIO), 2);

        contextProvider.getDynamicContext().addParameter(
                "addDynamicWithParam1",
                "%{tmp.tmpParamSimple}",
                ContextInfo.EMPTY);
        contextProvider.getDynamicContext().addParameter(
                "addDynamicWithParam2",
                "%{segm.num}-%{seq.num}-%{seq.type}-%{resource.num}-%{tmp.tmpParamSimple}",
                new ContextInfoBuilder().setSegment(0).setSequence(1).setResource(0).setSequenceType(SequenceType.AUDIO).build());

        assertTrue(contextProvider.getDynamicContext().getAllParameters().contains("tmpParamSimple"));
        assertTrue(contextProvider.getDynamicContext().getAllParameters().contains("0-1-audio-0-tmpParamSimple"));
    }

    @Test
    public void testAppendDynamicParameterWithParams() {
        TemplateParameterContextProvider contextProvider = createContextProvider();
        contextProvider.getSegmentContext().initDefaultSegmentParameters(2);
        contextProvider.getSequenceContext().initDefaultSequenceParameters(SequenceType.AUDIO, 2);
        contextProvider.getResourceContext().initDefaultResourceParameters(new ResourceKey(0, 0, SequenceType.AUDIO), 2);
        contextProvider.getResourceContext().initDefaultResourceParameters(new ResourceKey(0, 1, SequenceType.AUDIO), 2);
        contextProvider.getResourceContext().initDefaultResourceParameters(new ResourceKey(1, 0, SequenceType.AUDIO), 2);
        contextProvider.getResourceContext().initDefaultResourceParameters(new ResourceKey(1, 1, SequenceType.AUDIO), 2);

        contextProvider.getDynamicContext().appendParameter("appendDynamicWithParam1", "%{tmp.tmpParamSimple}_1", ContextInfo.EMPTY);
        contextProvider.getDynamicContext().appendParameter("appendDynamicWithParam1", "_%{tmp.tmpParamSimple}_2", ContextInfo.EMPTY);

        contextProvider.getDynamicContext().appendParameter(
                "appendDynamicWithParam2",
                "%{segm.num}-%{seq.num}-%{seq.type}-%{resource.num}-%{tmp.tmpParamSimple}",
                new ContextInfoBuilder().setSegment(0).setSequence(0).setResource(0).setSequenceType(SequenceType.AUDIO).build());
        contextProvider.getDynamicContext().appendParameter(
                "appendDynamicWithParam2",
                "_%{segm.num}-%{seq.num}-%{seq.type}-%{resource.num}-%{tmp.tmpParamSimple}",
                new ContextInfoBuilder().setSegment(1).setSequence(1).setResource(1).setSequenceType(SequenceType.AUDIO).build());

        assertTrue(
                contextProvider.getDynamicContext().getAllParameters().contains(
                        "tmpParamSimple_1_tmpParamSimple_2"));
        assertTrue(
                contextProvider.getDynamicContext().getAllParameters().contains(
                        "0-0-audio-0-tmpParamSimple_1-1-audio-1-tmpParamSimple"));
    }


    private TemplateParameterContextProvider createContextProvider() {
        return new TemplateParameterContextProvider(
                configProvider.getConfig(), conversionProvider.getFormat(), ".");
    }

}
