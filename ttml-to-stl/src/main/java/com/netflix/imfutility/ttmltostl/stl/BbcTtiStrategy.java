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

/**
 * BBC implementation of EBU STL TTI block building. It uses vertical positions with step 2.
 */
public class BbcTtiStrategy extends DefaultTtiStrategy {

    //Teletext usually - 25 lines (top is 0).
    //STL says it uses for teletext:  1-23 decimal (01h-17h)
    //BBS spec says: The normally accepted position for subtitles is towards the bottom of the screen
    //(Teletext lines 20 and 22. Line 18 is used if three subtitle lines are required).
    public static final int TOP_TELETEXT_LINE_TO_USE = 1;
    public static final int BOTTOM_TELETEXT_LINE_TO_USE = 22;

    //BBC probably use only 11 lines with step 2. In samples I didn't see any odd line number.
    public static final int TELETEXT_LINE_STEP = 2;

    protected int getLineStep() {
        return TELETEXT_LINE_STEP;
    }

    protected int getBottomLine() {
        return BOTTOM_TELETEXT_LINE_TO_USE;
    }

    protected int getTopLine() {
        return TOP_TELETEXT_LINE_TO_USE;
    }

}
