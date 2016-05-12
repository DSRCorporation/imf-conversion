package com.netflix.imfutility.conversion.templateParameter;

import com.netflix.imfutility.conversion.templateParameter.context.ITemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.exception.UnknownTemplateParameterContextException;

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

    public String resolveTemplateParameter(String parameterStr, ContextInfo contextInfo) {
        return doResolveTemplateParameter(new TemplateParameter(parameterStr), contextInfo);
    }

    private String doResolveTemplateParameter(TemplateParameter templateParameter, ContextInfo contextInfo) {
        ITemplateParameterContext context = contextProvider.getContext(templateParameter.getContext());
        if (context == null) {
            throw new UnknownTemplateParameterContextException(
                    templateParameter.toString(),
                    String.format("'%s' context not defined.", templateParameter.getContext().getName()));
        }
        return context.resolveTemplateParameter(templateParameter, contextInfo);
    }
}
