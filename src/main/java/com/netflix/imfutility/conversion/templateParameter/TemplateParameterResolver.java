package com.netflix.imfutility.conversion.templateParameter;

import com.netflix.imfutility.conversion.templateParameter.context.ITemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.exception.UnknownTemplateParameterContextException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String unresolvedParam = parameterStr;
        String resolvedParam = null;
        // resolve all sub-parameters, such as %{dynamic.%{segm.num}}
        while (!unresolvedParam.equals(resolvedParam)) {
            unresolvedParam = resolvedParam != null ? resolvedParam : unresolvedParam;
            resolvedParam = doResolveSubParameters(unresolvedParam, contextInfo);
        }
        return resolvedParam;
    }

    private String doResolveSubParameters(String parameterStr, ContextInfo contextInfo) {
        String resolvedParam = parameterStr;

        // resolve each template parameter the param contains
        Matcher m = Pattern.compile(TemplateParameter.TEMPLATE_PARAM).matcher(parameterStr);
        while (m.find()) {
            String templateParam = m.group();
            String resolvedTemplateParam = doResolveTemplateParameter(new TemplateParameter(templateParam), contextInfo);
            resolvedParam = resolvedParam.replace(templateParam, resolvedTemplateParam);
        }

        return resolvedParam;
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
