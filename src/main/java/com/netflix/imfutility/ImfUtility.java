package com.netflix.imfutility;

import com.netflix.imfutility.conversion.ConversionProvider;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.DynamicTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.segment.SegmentContextParameters;
import com.netflix.imfutility.conversion.templateParameter.context.segment.SegmentTemplateParameterContext;
import com.netflix.imfutility.xsd.conversion.SegmentType;

/**
 * Created by Alexander on 4/22/2016.
 */
public class ImfUtility {

    public static void main(String... args) {
        String configXml = args[0];
        String conversionXml = args[1];
        try {
            ConfigProvider configProvider = new ConfigProvider(configXml);
           /* ConversionProvider conversionProvider = new ConversionProvider(conversionXml, Format.DPP);
            TemplateParameterContextProvider contextProvider =
                    new TemplateParameterContextProvider(configProvider.getConfig(), conversionProvider.getFormat());

            DynamicTemplateParameterContext dynamicContext = (DynamicTemplateParameterContext) contextProvider.getContext(TemplateParameterContext.DYNAMIC);
            dynamicContext.addParameter("outputMxf", "G:\\Netflix\\test\\encode\\tmp\\output.mxf");
            dynamicContext.addParameter("audioChannels", "2");
            dynamicContext.addParameter("audioChannels", "2");

            SegmentTemplateParameterContext segmentContext = (SegmentTemplateParameterContext) contextProvider.getContext(TemplateParameterContext.SEGMENT);
            segmentContext.addSegmentParameter(0, SegmentType.VIDEO, SegmentContextParameters.ESSENCE, "G:\\Netflix\\test\\encode\\Aladdin_trailer_ATT.ts");
            segmentContext.addSegmentParameter(0, SegmentType.VIDEO, SegmentContextParameters.START_TIME, "10");
            segmentContext.addSegmentParameter(0, SegmentType.VIDEO, SegmentContextParameters.DURATION, "5");

            segmentContext.addSegmentParameter(0, SegmentType.AUDIO, SegmentContextParameters.ESSENCE, "G:\\Netflix\\test\\encode\\Aladdin_trailer_ATT.ts");
            segmentContext.addSegmentParameter(0, SegmentType.AUDIO, SegmentContextParameters.START_TIME, "10");
            segmentContext.addSegmentParameter(0, SegmentType.AUDIO, SegmentContextParameters.DURATION, "5");*/


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
