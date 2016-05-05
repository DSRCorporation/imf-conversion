package com.netflix.imfutility.conversion.templateParameter;

import com.netflix.imfutility.conversion.templateParameter.context.ITemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.segment.ISegmentTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.segment.SegmentTemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.exception.UnknownTemplateParameterContextException;
import com.netflix.imfutility.xsd.conversion.SegmentType;

/**
 * Resolves a given template parameter using an appropriate template parameter context.
 */
public class TemplateParameterResolver {

    private final TemplateParameterContextProvider contextProvider;

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
            throw new UnknownTemplateParameterContextException(
                    templateParameter.toString(),
                    String.format("'%s' context not defined.", templateParameter.getContext().getName()));
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
            throw new UnknownTemplateParameterContextException(
                    templateParameter.toString(),
                    String.format("'%s' context not defined.", templateParameter.getContext().getName()));
        }
        return context.resolveTemplateParameter(templateParameter);
    }
}
