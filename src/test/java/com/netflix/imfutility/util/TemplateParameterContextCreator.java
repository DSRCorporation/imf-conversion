package com.netflix.imfutility.util;

import com.netflix.imfutility.conversion.templateParameter.context.*;
import com.netflix.imfutility.xsd.conversion.SequenceType;

import java.util.EnumSet;

/**
 * Helper class to fill segment, sequence and resource contexts (it emulates parsing a CPL).
 */
public final class TemplateParameterContextCreator {

    private TemplateParameterContextCreator() {
    }

    public static void fillCPLContext(TemplateParameterContextProvider contextProvider, int segmentCount, int seqCount, int resourceCount,
                                      EnumSet<SequenceType> sequenceTypes) {
        // init segment ctxt
        SegmentTemplateParameterContext segmentContext = contextProvider.getSegmentContext();
        segmentContext.initDefaultSegmentParameters(segmentCount);

        // init sequence ctxt
        SequenceTemplateParameterContext sequenceContext = contextProvider.getSequenceContext();
        for (SequenceType seqType : sequenceTypes) {
            sequenceContext.initDefaultSequenceParameters(seqType, seqCount);
        }

        // init resource ctxt
        ResourceTemplateParameterContext resourceContext = contextProvider.getResourceContext();
        for (int segm = 0; segm < segmentCount; segm++) {
            for (int seq = 0; seq < seqCount; seq++) {
                for (int res = 0; res < resourceCount; res++) {
                    for (SequenceType seqType : sequenceTypes) {
                        // init default params
                        resourceContext.initDefaultResourceParameters(new ResourceKey(segm, seq, seqType), resourceCount);

                        // init essence, startTime and duration
                        fillResourceParam(resourceContext, segm, seq, res, ResourceContextParameters.ESSENCE, seqType);
                        fillResourceParam(resourceContext, segm, seq, res, ResourceContextParameters.DURATION, seqType);
                        fillResourceParam(resourceContext, segm, seq, res, ResourceContextParameters.START_TIME, seqType);
                    }
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

}
