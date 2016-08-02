/*
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
package com.netflix.imfutility.resources;

import java.io.InputStream;

/**
 * A helper for resource loading.
 */
public final class ResourceHelper {

    static InputStream getResourceInputStreamImpl(String resource) {
        // 1. try Thread Context Class Loader.
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null) {
            InputStream resourceInputStream = classLoader.getResourceAsStream(resource);
            if (resourceInputStream != null) {
                return resourceInputStream;
            }
        }

        // 2. try the classloader that loaded this class.
        classLoader = ResourceHelper.class.getClassLoader();
        if (classLoader != null) {
            InputStream resourceInputStream = classLoader.getResourceAsStream(resource);
            if (resourceInputStream != null) {
                return resourceInputStream;
            }
        }

        // 3. try system classloader
        classLoader = ClassLoader.getSystemClassLoader();
        if (classLoader != null) {
            InputStream resourceInputStream = classLoader.getResourceAsStream(resource);
            if (resourceInputStream != null) {
                return resourceInputStream;
            }
        }

        // 4. try the resource from the classpath
        return ClassLoader.getSystemResourceAsStream(resource);
    }

    public static InputStream getResourceInputStream(String resource) {
        InputStream resourceInputStream = getResourceInputStreamImpl(resource);
        if (resourceInputStream == null) {
            throw new RuntimeException(String.format("Can not load resource '%s'", resource));
        }
        return resourceInputStream;
    }

    private ResourceHelper() {
    }
}
