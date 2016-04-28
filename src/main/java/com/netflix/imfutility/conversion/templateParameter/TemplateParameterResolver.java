package com.netflix.imfutility.conversion.templateParameter;

import com.netflix.imfutility.conversion.templateParameter.context.ITemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.segment.ISegmentTemplateParameterContext;
import com.netflix.imfutility.xsd.conversion.SegmentType;

/**
 * Created by Alexander on 4/26/2016.
 */
public class TemplateParameterResolver {

    private TemplateParameterContextProvider contextProvider;

    public TemplateParameterResolver(TemplateParameterContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    public TemplateParameterContextProvider getContextProvider() {
        return contextProvider;
    }

    public String resolveSegmentTemplateParameter(String parameterStr, int segment, SegmentType segmentType) {
        SegmentTemplateParameter templateParameter = new SegmentTemplateParameter(parameterStr, segment, segmentType);
        ITemplateParameterContext context = contextProvider.getContext(templateParameter.getContext());
        if (context == null) {
            throw new RuntimeException(
                    String.format(
                            "Can not resolve template parameter context '%s' for parameter '%s'",
                            templateParameter.getContext().getName(), parameterStr));
        }

        if (context instanceof ISegmentTemplateParameterContext) {
            return ((ISegmentTemplateParameterContext) context).resolveSegmentTemplateParameter(templateParameter);
        }
        return context.resolveTemplateParameter(templateParameter);
    }

    public String resolveTemplateParameter(String parameterStr) {
        TemplateParameter templateParameter = new TemplateParameter(parameterStr);
        ITemplateParameterContext context = contextProvider.getContext(templateParameter.getContext());
        if (context == null) {
            throw new RuntimeException(
                    String.format(
                            "Can not resolve template parameter context '%s' for parameter '%s'",
                            templateParameter.getContext().getName(), parameterStr));
        }
        return context.resolveTemplateParameter(templateParameter);
    }
}
