package com.netflix.imfutility.conversion.templateParameter;

import com.netflix.imfutility.conversion.templateParameter.context.ITemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
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

    public String resolveTemplateParameter(String parameterStr, int segment, SegmentType segmentType) {
        return doResolveTemplateParameter(new TemplateParameter(parameterStr, segment, segmentType));
    }

    public String resolveTemplateParameter(String parameterStr) {
        return doResolveTemplateParameter(new TemplateParameter(parameterStr));
    }

    private String doResolveTemplateParameter(TemplateParameter templateParameter) {
        ITemplateParameterContext context = contextProvider.getContext(templateParameter.getContext());
        if (context == null) {
            throw new UnknownTemplateParameterContextException(
                    templateParameter.toString(),
                    String.format("'%s' context not defined.", templateParameter.getContext().getName()));
        }
        return context.resolveTemplateParameter(templateParameter);
    }
}
