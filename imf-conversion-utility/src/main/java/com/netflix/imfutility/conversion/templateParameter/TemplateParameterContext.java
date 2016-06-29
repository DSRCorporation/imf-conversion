package com.netflix.imfutility.conversion.templateParameter;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * All supported template parameter contexts.
 */
public enum TemplateParameterContext {


    TMP("tmp"),

    DYNAMIC("dynamic"),

    TOOL("tool"),

    SEGMENT("segm"),

    SEQUENCE("seq"),

    RESOURCE("resource");

    private final String name;

    TemplateParameterContext(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static TemplateParameterContext fromName(String name) {
        for (TemplateParameterContext e : values()) {
            if (e.getName().equals(name)) {
                return e;
            }
        }
        return null;
    }

    public static String getSupportedContexts() {
        return Arrays.stream(TemplateParameterContext.values())
                .map(TemplateParameterContext::getName)
                .collect(Collectors.joining(" ", "[", "]"));
    }

}
