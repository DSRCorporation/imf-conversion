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
package com.netflix.subtitles.util;

import com.netflix.subtitles.cli.TtmlOption;

/**
 * TtmlOption builder helper class.
 */
public class TtmlOptionBuilder {

    private final TtmlOption o = new TtmlOption();

    public TtmlOptionBuilder() {
    }

    public TtmlOptionBuilder withFileName(String fileName) {
        o.setFileName(fileName);
        return this;
    }

    public TtmlOptionBuilder withStartMS(long startMS) {
        o.setStartMS(startMS);
        return this;
    }

    public TtmlOptionBuilder withEndMS(long endMS) {
        o.setEndMS(endMS);
        return this;
    }

    public TtmlOptionBuilder withOffsetMS(long offsetMS) {
        o.setOffsetMS(offsetMS);
        return this;
    }

    public TtmlOption build() {
        return o;
    }
}
