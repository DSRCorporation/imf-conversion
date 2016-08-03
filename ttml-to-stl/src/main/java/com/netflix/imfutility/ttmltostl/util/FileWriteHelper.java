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
package com.netflix.imfutility.ttmltostl.util;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Class that handles reading and writing to files.
 *
 */
public final class FileWriteHelper {

    private FileWriteHelper() {
    }

    /**
     * Method to get the file name (or path relative to the directory) and file to write to
     * in the form of an array of strings where each string represents a line.
     *
     * @param fileName name of the file (or path relative to directory)
     * @param totalFile array of strings where each string represents a line in the file
     */
    public static void writeFileTxt(String fileName, String[] totalFile) throws IOException {
        try (final Writer writer = Files.newBufferedWriter(Paths.get(fileName), Charset.forName("UTF-8"));
             PrintWriter pw = new PrintWriter(writer)) {
            for (String file : totalFile) {
                pw.println(file);
            }
        }
    }

    /**
     * Method to write raw content to the file.
     *
     * @param fileName name of the file (or path relative to directory)
     * @param contents an arrays of bytes and corresponding charsets.
     */
    public static void writeFileRaw(String fileName, byte[][] contents) throws IOException {
        try (OutputStream output = new BufferedOutputStream(new FileOutputStream(fileName))) {
            for (byte[] content : contents) {
                output.write(content);
            }
            output.flush();
        }
    }

}
