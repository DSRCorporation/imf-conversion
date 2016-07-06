package com.netflix.imfutility.util;

import com.netflix.imfutility.FakeFormat;
import com.netflix.imfutility.config.ConfigXmlProvider;
import com.netflix.imfutility.conversion.ConversionXmlProvider;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfoBuilder;
import com.netflix.imfutility.conversion.templateParameter.context.*;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SegmentContextParameters;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters;
import com.netflix.imfutility.cpl.uuid.ResourceUUID;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.SequenceType;

import java.io.File;
import java.util.EnumSet;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Helper class to fill segment, sequence and resource contexts (it emulates parsing a CPL).
 */
public final class TemplateParameterContextCreator {

    public static final String SEGMENT_UUID_FORMAT = "urn:uuid:segm:%d";
    public static final String SEQUENCE_UUID_FORMAT = "urn:uuid:seq:%s:%d";
    public static final String RESOURCE_UUID_FORMAT = "urn:uuid:res:segm-%d-seq-%d-%s-%d";
    public static final String RESOURCE_PARAMETER_FORMAT = "%s-%s";

    public static final String WORKING_DIR = "ImfUtilityTest";

    private TemplateParameterContextCreator() {
    }

    public static TemplateParameterContextProvider createDefaultContextProvider() throws Exception {
        ConfigXmlProvider configProvider = new ConfigXmlProvider(ConfigUtils.getCorrectConfigXml(), ConfigUtils.getCorrectConfigXmlPath());
        ConversionXmlProvider conversionProvider = new ConversionXmlProvider(ConversionUtils.getCorrectConversionXml(),
                ConversionUtils.getCorrectConversionXmlPath(), new FakeFormat());
        return new TemplateParameterContextProvider(configProvider, conversionProvider, getWorkingDir());
    }

    public static File getWorkingDir() {
        return new File(TemplateParameterContextCreator.getCurrentTmpDir(), WORKING_DIR);
    }


    public static TemplateParameterContextProvider createDefaultContextProviderWithCPLContext(
            int segmentCount, int seqCount, int resourceCount) throws Exception {
        TemplateParameterContextProvider contextProvider = createDefaultContextProvider();
        fillCPLContext(contextProvider, segmentCount, seqCount, resourceCount);
        return contextProvider;
    }

    public static TemplateParameterContextProvider createDefaultContextProviderWithCPLContext(
            int segmentCount, int seqCount, int resourceCount, EnumSet<SequenceType> sequenceTypes) throws Exception {
        TemplateParameterContextProvider contextProvider = createDefaultContextProvider();
        fillCPLContext(contextProvider, segmentCount, seqCount, resourceCount, 1, sequenceTypes);
        return contextProvider;
    }

    public static TemplateParameterContextProvider createDefaultContextProviderWithCPLContext(
            int segmentCount, int seqCount, int resourceCount, int repeatCountForResource, EnumSet<SequenceType> sequenceTypes) throws Exception {
        TemplateParameterContextProvider contextProvider = createDefaultContextProvider();
        fillCPLContext(contextProvider, segmentCount, seqCount, resourceCount, repeatCountForResource, sequenceTypes);
        return contextProvider;
    }


    public static File getCurrentTmpDir() {
        String tempDir = System.getProperty("java.io.tmpdir");
        if (tempDir == null) {
            return new File(".");
        }
        return new File(tempDir);
    }

    public static void fillCPLContext(TemplateParameterContextProvider contextProvider, int segmentCount, int seqCount, int resourceCount) {
        fillCPLContext(contextProvider, segmentCount, seqCount, resourceCount, 1, EnumSet.allOf(SequenceType.class));
    }


    public static void fillCPLContext(TemplateParameterContextProvider contextProvider, int segmentCount, int seqCount, int resourceCount, int repeatCountForResource) {
        fillCPLContext(contextProvider, segmentCount, seqCount, resourceCount, repeatCountForResource, EnumSet.allOf(SequenceType.class));
    }

    public static void fillCPLContext(TemplateParameterContextProvider contextProvider, int segmentCount, int seqCount, int resourceCount, int repeatCountForResource,
                                      EnumSet<SequenceType> sequenceTypes) {
        // init segment ctxt
        SegmentTemplateParameterContext segmentContext = contextProvider.getSegmentContext();
        for (int i = 0; i < segmentCount; i++) {
            segmentContext.initSegment(getSegmentUuid(i));
        }

        // init sequence ctxt
        SequenceTemplateParameterContext sequenceContext = contextProvider.getSequenceContext();
        for (SequenceType seqType : sequenceTypes) {
            for (int i = 0; i < segmentCount; i++) {
                sequenceContext.initSequence(seqType, getSequenceUuid(i, seqType));
            }
        }

        // init resource ctxt
        ResourceTemplateParameterContext resourceContext = contextProvider.getResourceContext();
        for (int segm = 0; segm < segmentCount; segm++) {
            for (int seq = 0; seq < seqCount; seq++) {
                for (int res = 0; res < resourceCount; res++) {
                    for (SequenceType seqType : sequenceTypes) {
                        ResourceKey resourceKey = ResourceKey.create(getSegmentUuid(segm), getSequenceUuid(seq, seqType), seqType);
                        ResourceUUID resourceUuid = getResourceUuid(segm, seq, seqType, res);

                        // init default params
                        resourceContext.initResource(resourceKey, resourceUuid);

                        // init essence, startTime and duration
                        fillResourceParam(resourceContext, resourceKey, resourceUuid, ResourceContextParameters.ESSENCE);
                        fillResourceParam(resourceContext, resourceKey, resourceUuid, ResourceContextParameters.DURATION_TIMECODE);
                        fillResourceParam(resourceContext, resourceKey, resourceUuid, ResourceContextParameters.DURATION_EDIT_UNIT);
                        fillResourceParam(resourceContext, resourceKey, resourceUuid, ResourceContextParameters.DURATION_FRAME_EDIT_UNIT);
                        fillResourceParam(resourceContext, resourceKey, resourceUuid, ResourceContextParameters.START_TIME_TIMECODE);
                        fillResourceParam(resourceContext, resourceKey, resourceUuid, ResourceContextParameters.START_TIME_EDIT_UNIT);
                        fillResourceParam(resourceContext, resourceKey, resourceUuid, ResourceContextParameters.START_TIME_FRAME_EDIT_UNIT);
                        fillResourceParam(resourceContext, resourceKey, resourceUuid, ResourceContextParameters.REPEAT_COUNT, String.valueOf(repeatCountForResource));
                    }
                }
            }
        }
    }

    public static SegmentUUID getSegmentUuid(int segm) {
        return SegmentUUID.create(
                String.format(SEGMENT_UUID_FORMAT, segm));
    }

    public static SequenceUUID getSequenceUuid(int seq, SequenceType seqType) {
        return SequenceUUID.create(
                String.format(SEQUENCE_UUID_FORMAT, seqType.value(), seq));
    }

    public static ResourceUUID getResourceUuid(int segm, int seq, SequenceType seqType, int res) {
        return ResourceUUID.create(
                String.format(RESOURCE_UUID_FORMAT, segm, seq, seqType.value(), res));
    }

    public static void addResourceContextParameter(TemplateParameterContextProvider contextProvider, int segm, int seq, SequenceType seqType, int res,
                                                   ResourceContextParameters param, String paramValue) {
        contextProvider.getResourceContext().addResourceParameter(
                ResourceKey.create(getSegmentUuid(segm), getSequenceUuid(seq, seqType), seqType),
                getResourceUuid(segm, seq, seqType, res),
                param,
                paramValue);
    }

    public static void addSequenceContextParameter(TemplateParameterContextProvider contextProvider, int seq, SequenceType seqType,
                                                   SequenceContextParameters param, String paramValue) {
        contextProvider.getSequenceContext().addSequenceParameter(
                seqType,
                getSequenceUuid(seq, seqType),
                param,
                paramValue);
    }

    public static void addSegmentContextParameter(TemplateParameterContextProvider contextProvider, int segm,
                                                  SegmentContextParameters param, String paramValue) {
        contextProvider.getSegmentContext().addSegmentParameter(
                getSegmentUuid(segm),
                param,
                paramValue);
    }

    public static ContextInfo createResourceContextInfo(int segm, int seq, SequenceType seqType, int res) {
        return new ContextInfoBuilder()
                .setSequenceType(seqType)
                .setSegmentUuid(getSegmentUuid(segm))
                .setSequenceUuid(getSequenceUuid(seq, seqType))
                .setResourceUuid(getResourceUuid(segm, seq, seqType, res))
                .build();
    }

    public static ContextInfo createSequenceContextInfo(int seq, SequenceType seqType) {
        return new ContextInfoBuilder()
                .setSequenceType(seqType)
                .setSequenceType(seqType)
                .build();
    }

    public static ContextInfo createSegmentContextInfo(int segm) {
        return new ContextInfoBuilder()
                .setSegmentUuid(getSegmentUuid(segm))
                .build();
    }

    private static void fillResourceParam(ResourceTemplateParameterContext resourceContext, ResourceKey resourceKey,
                                          ResourceUUID resourceUuid, ResourceContextParameters resParam) {
        resourceContext.addResourceParameter(
                resourceKey,
                resourceUuid,
                resParam,
                String.format(RESOURCE_PARAMETER_FORMAT, resourceUuid, resParam.getName())
        );
    }

    private static void fillResourceParam(ResourceTemplateParameterContext resourceContext, ResourceKey resourceKey,
                                          ResourceUUID resourceUuid, ResourceContextParameters resParam, String value) {
        resourceContext.addResourceParameter(
                resourceKey,
                resourceUuid,
                resParam,
                value
        );
    }

    public static void assertResourceParameter(String value, ResourceUUID resourceUuid, ResourceContextParameters resParam) {
        String expectedValue = String.format(RESOURCE_PARAMETER_FORMAT, resourceUuid.getUuid(), resParam.getName());
        assertNotNull(value);
        assertEquals(expectedValue, value);
    }

}
