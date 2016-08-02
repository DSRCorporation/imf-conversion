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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * These objects can (should) only be created through the implementations of parseFile() in
 * the {@link com.netflix.imfutility.ttmltostl.ttml.TimedTextFileFormat} interface
 * They are an object representation of a subtitle file and contain all the captions and associated styles.
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
public class TimedTextObject {

    /*
     * Attributes
     *
     */
    //meta info
    private String title = "";
    private String description = "";
    private String copyrigth = "";
    private String author = "";
    private String fileName = "";
    private String language = "";

    //list of styles (id, reference)
    private Map<String, Style> styling = new HashMap<>();

    //list of captions (begin time, reference)
    //represented by a tree map to maintain order
    private TreeMap<Integer, Caption> captions = new TreeMap<>();

    //to store non fatal errors produced during parsing
    private String warnings;

    //to know if a parsing method has been applied
    private boolean built = false;


    /**
     * Protected constructor so it can't be created from outside.
     */
    protected TimedTextObject() {
        setWarnings("List of non fatal errors produced during parsing:\n\n");
    }

    /*
     * PROTECTED METHODS
     */

    /**
     * This method simply checks the style list and eliminate any style not referenced by any caption
     * This might come useful when default styles get created and cover too much.
     * It require a unique iteration through all captions.
     *
     */
    protected void cleanUnusedStyles() {
        //here all used styles will be stored
        Hashtable<String, Style> usedStyles = new Hashtable<>();
        //we iterate over the captions
        Iterator<Caption> itrC = getCaptions().values().iterator();
        while (itrC.hasNext()) {
            //new caption
            Caption current = itrC.next();
            //if it has a style
            if (current.getStyle() != null) {
                String iD = current.getStyle().getiD();
                //if we haven't saved it yet
                if (!usedStyles.containsKey(iD)) {
                    usedStyles.put(iD, current.getStyle());
                }
            }
        }
        //we saved the used styles
        this.setStyling(usedStyles);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCopyrigth() {
        return copyrigth;
    }

    public void setCopyrigth(String copyrigth) {
        this.copyrigth = copyrigth;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Map<String, Style> getStyling() {
        return styling;
    }

    public void setStyling(Map<String, Style> styling) {
        this.styling = styling;
    }

    public TreeMap<Integer, Caption> getCaptions() {
        return captions;
    }

    public void setCaptions(TreeMap<Integer, Caption> captions) {
        this.captions = captions;
    }

    public String getWarnings() {
        return warnings;
    }

    public void setWarnings(String warnings) {
        this.warnings = warnings;
    }

    public boolean isBuilt() {
        return built;
    }

    public void setBuilt(boolean built) {
        this.built = built;
    }
}
