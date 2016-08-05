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
package com.netflix.imfutility.ttmltostl.util;

import com.netflix.imfutility.ttmltostl.stl.BbcGsiStrategy;
import com.netflix.imfutility.ttmltostl.stl.DefaultTtiStrategy;
import com.netflix.imfutility.ttmltostl.stl.StlBuilder;
import com.netflix.imfutility.ttmltostl.ttml.Caption;
import com.netflix.imfutility.ttmltostl.ttml.Time;
import com.netflix.imfutility.ttmltostl.ttml.TimedTextObject;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Utility methods used in STL builder tests.
 */
public final class StlTestUtil {

    public static String getMetadataXml() throws URISyntaxException {
        //noinspection ConstantConditions
        return new File(ClassLoader.getSystemClassLoader().getResource("dpp/metadata.xml").toURI()).getAbsolutePath();
    }

    public static String getMetadataSpecialSymbolsXml() throws URISyntaxException {
        //noinspection ConstantConditions
        return new File(ClassLoader.getSystemClassLoader().getResource("dpp/metadata-special-symbols.xml").toURI()).getAbsolutePath();
    }

    public static byte[][] build(TimedTextObject tto, String metadataXml) throws Exception {
        return new StlBuilder()
                .build(tto, new BbcGsiStrategy(metadataXml), new DefaultTtiStrategy());
    }

    public static TimedTextObject buildTto(String... args) {
        if (args.length % 3 != 0 || args.length < 3) {
            throw new RuntimeException("Expected arguments when building TTO: <startTime>, <endTime>, <text>");
        }

        TimedTextObject tto = new TimedTextObject();
        int i = 0;
        int index = 0;
        while (i < args.length) {
            String start = args[i++];
            String end = args[i++];
            String text = args[i++];
            Caption caption = new Caption();
            caption.setStart(new Time("h:m:s:f/fps", start + "/25"));
            caption.setEnd(new Time("h:m:s:f/fps", end + "/25"));
            caption.setContent(text);
            tto.getCaptions().put(index++, caption);
        }
        tto.setBuilt(true);
        return tto;
    }

    private StlTestUtil() {
    }
}
