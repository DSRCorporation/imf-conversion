package com.netflix.imfutility.conversion.templateParameter;

import com.netflix.imfutility.conversion.templateParameter.exception.InvalidTemplateParameterException;
import com.netflix.imfutility.conversion.templateParameter.exception.UnknownTemplateParameterContextException;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represent a template parameter from conversion operation in the following form: ${paramContext.paramName}.
 */
public class TemplateParameter {

    public static final String TEMPLATE_PARAM = "%\\{([^.%]+?)\\.([^.%]+?)\\}"; // use reluctant quantifiers!
    private static final String TEMPLATE_PARAM_WITH_SUBPARAM = "%\\{(.+?)\\.(.+?)\\}"; // use reluctant quantifiers!

    private final TemplateParameterContext context;
    private final String name;

    public static boolean isTemplateParameter(String parameterString) {
        return parameterString.matches(TEMPLATE_PARAM_WITH_SUBPARAM);
    }

    public TemplateParameter(TemplateParameterContext context, String name) {
        this.context = context;
        this.name = name;
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

        if (StringUtils.isEmpty(contextStr) || StringUtils.isEmpty(this.name)) {
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
