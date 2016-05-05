package com.netflix.imfutility.conversion.templateParameter;

import com.netflix.imfutility.conversion.templateParameter.exception.InvalidTemplateParameterException;
import com.netflix.imfutility.conversion.templateParameter.exception.UnknownTemplateParameterContextException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represent a template parameter from conversion operation in the following form: ${paramContext.paramName}.
 */
public class TemplateParameter {

    private static final String TEMPLATE_PARAM = "%\\{(\\w+)\\.(\\w+)\\}";

    private final TemplateParameterContext context;
    private final String name;

    public static boolean isTemplateParameter(String parameterString) {
        return parameterString.matches(TEMPLATE_PARAM);
    }

    public TemplateParameter(String parameterString) {
        Pattern p = Pattern.compile(TEMPLATE_PARAM);
        Matcher m = p.matcher(parameterString);
        if (!m.matches()) {
            throw new InvalidTemplateParameterException(
                    parameterString, "Template parameter must have the following form: '%%{context.name}'");
        }
        String contextStr = m.group(1);
        this.name = m.group(2);

        if (contextStr == null || this.name == null) {
            throw new InvalidTemplateParameterException(
                    parameterString, "Template parameter must have the following form: '%%{context.name}'");
        }

        this.context = TemplateParameterContext.fromName(contextStr);
        if (this.context == null) {
            throw new UnknownTemplateParameterContextException(
                    parameterString,
                    String.format("Unknown context '%s'. Supported contexts: %s'",
                            contextStr, TemplateParameterContext.getSupportedContexts()));

        }
    }

    public String getName() {
        return name;
    }

    public TemplateParameterContext getContext() {
        return context;
    }

    @Override
    public String toString() {
        return String.format("%%{%s.%s}", getContext().getName(), getName());
    }

}
