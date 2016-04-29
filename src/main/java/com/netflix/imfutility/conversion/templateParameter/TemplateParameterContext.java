package com.netflix.imfutility.conversion.templateParameter;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by Alexander on 4/25/2016.
 */
public enum TemplateParameterContext {


    TMP("tmp"),

    DYNAMIC("dynamic"),

    TOOL("tool"),

    SEGMENT("segment");

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
                .map(v -> v.getName())
                .collect(Collectors.joining(" "));
    }

}
