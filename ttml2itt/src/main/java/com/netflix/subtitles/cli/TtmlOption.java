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
package com.netflix.subtitles.cli;

/**
 * --ttml option descriptor.
 */
public class TtmlOption {
    private String fileName;
    private long startMS = 0;
    private long endMS = Long.MAX_VALUE;
    private long offsetMS = 0;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getStartMS() {
        return startMS;
    }

    public void setStartMS(long startMS) {
        this.startMS = startMS;
    }

    public long getEndMS() {
        return endMS;
    }

    public void setEndMS(long endMS) {
        this.endMS = endMS;
    }

    public long getOffsetMS() {
        return offsetMS;
    }

    public void setOffsetMS(long offsetMS) {
        this.offsetMS = offsetMS;
    }
}
