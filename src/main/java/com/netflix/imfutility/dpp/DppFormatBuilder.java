package com.netflix.imfutility.dpp;

import com.netflix.imfutility.AbstractFormatBuilder;
import com.netflix.imfutility.Format;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.context.*;
import com.netflix.imfutility.xsd.conversion.SequenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DPP format builder (see {@link AbstractFormatBuilder}). It's used for conversion to DPP format.
 */
public class DppFormatBuilder extends AbstractFormatBuilder {

    private final Logger logger = LoggerFactory.getLogger(AbstractFormatBuilder.class);

    public DppFormatBuilder() {
        super(Format.DPP);
    }

    @Override
    protected void fillOutputContext() {
        logger.info("Creating Output context...");

        // FIXME

        OutputTemplateParameterContext outputContext = contextProvider.getOutputContext();
        outputContext.addParameter("mxf", "output.mxf");

        logger.info("Created Output context: OK\n");
    }

    @Override
    protected void fillDynamicContext() {
        logger.info("Creating Dynamic context...");

        // FIXME

        DynamicTemplateParameterContext dynamicContext = contextProvider.getDynamicContext();
        dynamicContext.addParameter("audioChannels", "2", ContextInfo.EMPTY);

        logger.info("Created Dynamic context: OK\n");
    }

    @Override
    protected void fillCplContext() {
        logger.info("Creating CPL contexts (segment, sequence, resource)...");

        //FIXME

        String pathToMedia = "G:\\Netflix\\test\\encode\\Aladdin_trailer_ATT.ts";


        int segmentCount = 2;
        int videoSeqCount = 1;
        int audioSeqCount = 1;
        int startOffset = 10;
        int segmDuration = 5;
        int resorceVideoCount = 1;
        int resorceAudioCount = 1;


        SegmentTemplateParameterContext segmentContext = contextProvider.getSegmentContext();
        segmentContext.initDefaultSegmentParameters(segmentCount);

        SequenceTemplateParameterContext sequenceContext = contextProvider.getSequenceContext();
        sequenceContext.initDefaultSequenceParameters(SequenceType.VIDEO, videoSeqCount);
        sequenceContext.initDefaultSequenceParameters(SequenceType.AUDIO, audioSeqCount);

        ResourceTemplateParameterContext resourceContext = contextProvider.getResourceContext();
        for (int segm = 0; segm < segmentCount; segm++) {
            for (int seq = 0; seq < videoSeqCount; seq++) {
                for (int res = 0; res < resorceVideoCount; res++) {
                    ResourceKey resourceKey = new ResourceKey(
                            segm, seq, SequenceType.VIDEO);
                    resourceContext.initDefaultResourceParameters(resourceKey, resorceVideoCount);
                    resourceContext.addResourceParameter(resourceKey, res, ResourceContextParameters.ESSENCE, pathToMedia);
                    resourceContext.addResourceParameter(resourceKey, res, ResourceContextParameters.START_TIME,
                            String.valueOf(startOffset + (res + 1) * segm * segmDuration));
                    resourceContext.addResourceParameter(resourceKey, res, ResourceContextParameters.DURATION,
                            String.valueOf(segmDuration));
                }
            }
            for (int seq = 0; seq < audioSeqCount; seq++) {
                for (int res = 0; res < resorceAudioCount; res++) {
                    ResourceKey resourceKey = new ResourceKey(
                            segm, seq, SequenceType.AUDIO);
                    resourceContext.initDefaultResourceParameters(resourceKey, resorceAudioCount);
                    resourceContext.addResourceParameter(resourceKey, res, ResourceContextParameters.ESSENCE, pathToMedia);
                    resourceContext.addResourceParameter(resourceKey, res, ResourceContextParameters.START_TIME,
                            String.valueOf(startOffset + (res + 1) * segm * segmDuration));
                    resourceContext.addResourceParameter(resourceKey, res, ResourceContextParameters.DURATION,
                            String.valueOf(segmDuration));
                }
            }
        }

        logger.info("Created CPL contexts (segment, sequence, resource): OK\n");
    }

    @Override
    protected String getConversionConfiguration() {
        return conversionProvider.getConvertConfiguration().get(0);
    }

}
