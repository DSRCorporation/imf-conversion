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
package com.netflix.imfutility.itunes.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Common test utility.
 */
public class TestUtils {

    private TestUtils() {
    }

    public static File getTestFile() throws URISyntaxException {
        return new File(ClassLoader.getSystemClassLoader().getResource("xml/test-file").toURI());
    }

    public static File createFile(File dir, String name) throws IOException {
        File file = new File(dir, name);
        if (!file.createNewFile()) {
            throw new IOException(String.format("Cannot create test file %s", file.getAbsolutePath()));
        }
        file.deleteOnExit();
        return file;
    }

    public static File createDirectory(File dir, String name) throws IOException {
        File destDir = new File(dir, name);
        if (!destDir.mkdir()) {
            throw new IOException(String.format("Cannot create test directory %s", destDir.getAbsolutePath()));
        }
        destDir.deleteOnExit();
        return destDir;
    }
}
