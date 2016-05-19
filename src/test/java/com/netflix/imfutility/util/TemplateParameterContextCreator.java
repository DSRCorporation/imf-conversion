package com.netflix.imfutility.util;

import com.netflix.imfutility.conversion.templateParameter.context.*;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters;
import com.netflix.imfutility.cpl.uuid.ResourceUUID;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.xsd.conversion.SequenceType;

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

    private TemplateParameterContextCreator() {
    }

    public static void fillCPLContext(TemplateParameterContextProvider contextProvider, int segmentCount, int seqCount, int resourceCount) {
        fillCPLContext(contextProvider, segmentCount, seqCount, resourceCount, EnumSet.allOf(SequenceType.class));
    }

    public static void fillCPLContext(TemplateParameterContextProvider contextProvider, int segmentCount, int seqCount, int resourceCount,
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
                        fillResourceParam(resourceContext, resourceKey, resourceUuid, ResourceContextParameters.START_TIME_TIMECODE);
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

    private static void fillResourceParam(ResourceTemplateParameterContext resourceContext, ResourceKey resourceKey,
                                          ResourceUUID resourceUuid, ResourceContextParameters resParam) {
        resourceContext.addResourceParameter(
                resourceKey,
                resourceUuid,
                resParam,
                String.format(RESOURCE_PARAMETER_FORMAT, resourceUuid, resParam.getName())
        );
    }

    public static void assertResourceParameter(String value, ResourceUUID resourceUuid, ResourceContextParameters resParam) {
        String expectedValue = String.format(RESOURCE_PARAMETER_FORMAT, resourceUuid.getUuid(), resParam.getName());
        assertNotNull(value);
        assertEquals(expectedValue, value);
    }

}
