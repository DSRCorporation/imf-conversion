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
package com.netflix.imfutility.ttmltostl.stl;

import com.netflix.imfutility.ttmltostl.ttml.Caption;
import com.netflix.imfutility.ttmltostl.ttml.Style;
import com.netflix.imfutility.ttmltostl.ttml.Time;

/**
 * An STL subtitle essence.
 */
public class StlSubtitle {

    //Teletext usually - 25 lines (top is 0).
    //STL says it uses for teletext:  1-23 decimal (01h-17h)
    //BBS spec says: The normally accepted position for subtitles is towards the bottom of the screen
    //(Teletext lines 20 and 22. Line 18 is used if three subtitle lines are required).
    public static final int BOTTOM_TELETEXT_LINE_TO_USE = 22;
    //BBC probably use only 11 lines with step 2. In samples I didn't see any odd line number.
    public static final int TELETEXT_LINE_STEP = 2;

    private final Caption caption;
    private final int linesCount;
    private final byte[][] extensionBlocks;

    private Time start;
    private Time end;
    private int lineNum;

    private Boolean isCumulative = false;
    private Boolean cumulativeStartFlag = false;
    private Boolean cumulativeEndFlag = false;

    public StlSubtitle(Caption caption, int linesCount, byte[][] extensionBlocks) {
        this.caption = caption;
        this.linesCount = linesCount;
        this.extensionBlocks = extensionBlocks;
        this.start = caption.getStart();
        this.end = caption.getEnd();
        this.lineNum = BOTTOM_TELETEXT_LINE_TO_USE; //default Teletext line num for BBC recommendation.
    }

    public byte[][] getExtensionBlocks() {
        return extensionBlocks;
    }

    public Style getCaptionStyle() {
        return caption.getStyle();
    }

    public int getLinesCount() {
        return linesCount;
    }

    public Time getStart() {
        return start;
    }

    public void setStart(Time start) {
        this.start = start;
    }

    public Time getEnd() {
        return end;
    }

    public void setEnd(Time end) {
        this.end = end;
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public Boolean getCumulative() {
        return isCumulative;
    }

    public void setCumulative(Boolean cumulative) {
        isCumulative = cumulative;
    }

    public void setCumulativeStartFlag(boolean cumulativeStartFlag) {
        this.cumulativeStartFlag = cumulativeStartFlag;
    }

    public void setCumulativeEndFlag(boolean cumulativeEndFlag) {
        this.cumulativeEndFlag = cumulativeEndFlag;
    }

    public Boolean getCumulativeStartFlag() {
        return cumulativeStartFlag;
    }

    public Boolean getCumulativeEndFlag() {
        return cumulativeEndFlag;
    }

}
