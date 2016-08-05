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
package com.netflix.imfutility.ttmltostl.ttml;

import java.io.File;
import java.io.IOException;

/**
 * This class specifies the interface for any format supported by the converter, these formats must
 * create a {@link TimedTextObject} from an {@link java.io.InputStream} (so it can process files form standard In or uploads)
 * and return a String array for text formats, or byte array for binary formats.
 * <br><br>
 * Copyright (c) 2012 J. David Requejo <br>
 * j[dot]david[dot]requejo[at] Gmail
 * <br><br>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 * <br><br>
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 * <br><br>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * @author J. David Requejo
 *
 */
public interface TimedTextFileFormat {

    /**
     * This methods receives the path to a file, parses it, and returns a TimedTextObject.
     *
     * @return TimedTextObject representing the parsed file
     * @throws java.io.IOException when having trouble reading the file from the given path
     */
    TimedTextObject parseFile(File file, int startMS, int endMS, int offsetMS)
            throws IOException, FatalParsingException;

    /**
     * This method transforms a given TimedTextObject into a formatted subtitle file.
     *
     * @param tto the object to transform into a file
     * @return NULL if the given TimedTextObject has not been built first,
     *      or String[] where each String is at least a line, if size is 2, then the file has at least two lines.
     *      or byte[] in case the file is a binary (as is the case of STL format)
     */
    Object toFile(TimedTextObject tto);

}
