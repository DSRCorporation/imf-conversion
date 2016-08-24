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
package com.netflix.subtitles.util;

import java.io.Serializable;
import org.w3.ns.ttml.PEltype;

/**
 * P builder helper class.
 */
public final class PBuilder {
    private final PEltype p = new PEltype();

    public PBuilder() {
    }

    public PBuilder withBegin(String begin) {
        p.setBegin(begin);
        return this;
    }

    public PBuilder withEnd(String end) {
        p.setEnd(end);
        return this;
    }

    public PBuilder withDur(String dur) {
        p.setDur(dur);
        return this;
    }

    public PBuilder withStyle(String style) {
        p.getStyle().add(style);
        return this;
    }

    public PBuilder withRegion(String region) {
        p.setRegion(region);
        return this;
    }

    public PBuilder withContent(Serializable content) {
        p.getContent().add(content);
        return this;
    }

    public PEltype build() {
        return p;
    }
}
