package com.netflix.imfutility.conversion.templateParameter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alexander on 4/25/2016.
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
            throw new RuntimeException(
                    String.format("Incorrect Template Parameter '%s'. Template parameter must have the following form: '%%{context.name}'", parameterString));
        }
        String contextStr = m.group(1);
        this.name = m.group(2);

        if (contextStr == null || this.name == null) {
            throw new RuntimeException(
                    String.format("Incorrect Template Parameter '%s'. Template parameter must have the following form: '%%{context.name}'", parameterString));
        }

        this.context = TemplateParameterContext.fromName(contextStr);
        if (this.context == null) {
            throw new RuntimeException(
                    String.format("Unknown context '%s' in Template Parameter '%s'. Supported contexts: %s'",
                            contextStr, parameterString, getSupportedContexts()));

        }
    }

    private String getSupportedContexts() {
        StringBuilder supportedContextsBuilder = new StringBuilder();
        supportedContextsBuilder.append("[ ");
        for (TemplateParameterContext e : TemplateParameterContext.values()) {
            supportedContextsBuilder.append(e.getName());
            supportedContextsBuilder.append(" ");
        }
        supportedContextsBuilder.append("]");
        return supportedContextsBuilder.toString();
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
