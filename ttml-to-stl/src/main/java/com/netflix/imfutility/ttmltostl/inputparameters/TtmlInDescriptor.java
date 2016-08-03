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

package com.netflix.imfutility.ttmltostl.inputparameters;

/**
 * Describes input TTML file parameters: file - start timecode - end timecode - offset timecode.
 * Created by Alexandr on 5/27/2016.
 */
public class TtmlInDescriptor {

    private String file = null;
    private int startMS = 0;
    private int endMS = 0;
    private int offsetMS = 0;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getStartMS() {
        return startMS;
    }

    public void setStartMS(int startMS) {
        this.startMS = startMS;
    }

    public int getEndMS() {
        return endMS;
    }

    public void setEndMS(int endMS) {
        this.endMS = endMS;
    }

    public int getOffsetMS() {
        return offsetMS;
    }

    public void setOffsetMS(int offsetMS) {
        this.offsetMS = offsetMS;
    }

}