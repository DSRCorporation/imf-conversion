package com.netflix.imfutility.resources;

import java.io.InputStream;

/**
 * A helper for resource loading.
 */
public final class ResourceHelper {

    public static InputStream getResourceInputStream(String resource) {
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
        InputStream resourceInputStream = ClassLoader.getSystemResourceAsStream(resource);
        if (resourceInputStream == null) {
            throw new RuntimeException(String.format("Can not load resource '%s'", resource));
        }
        return resourceInputStream;


    }

    private ResourceHelper() {
    }
}
