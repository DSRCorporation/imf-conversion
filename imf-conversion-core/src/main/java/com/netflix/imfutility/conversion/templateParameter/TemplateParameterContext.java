/**
 * Copyright (C) 2016 Netflix, Inc.
 *
 *     This file is part of IMF Conversion Utility.
 *
 *     IMF Conversion Utility is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     IMF Conversion Utility is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with IMF Conversion Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
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
