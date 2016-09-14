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
package com.netflix.imfutility.itunes.destcontext;

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.itunes.ITunesPackageType;
import com.netflix.imfutility.xsd.conversion.DestContextsTypeMap;
import org.apache.commons.math3.fraction.BigFraction;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.netflix.imfutility.itunes.util.DestContextUtils.createDestContextMap;
import static junit.framework.TestCase.assertEquals;

/**
 * Tests that dest context for input video params resolves correctly.
 */
public class VideoDestContextResolveStrategyTest {

    private static DestContextsTypeMap map;

    @BeforeClass
    public static void setUpAll() throws Exception {
        map = new DestContextsTypeMap();

        map.getMap().put("hd1080p30", createDestContextMap(
                "hd1080p30", "1920", "1080", "30/1", "false", null));
        map.getMap().put("hd1080i2997", createDestContextMap(
                "hd1080i2997", "1920", "1080", "30000/1001", "true", null));
        map.getMap().put("hd720i23976", createDestContextMap(
                "hd720i23976", "1280", "720", "24000/1001", "true", "tv"));
        map.getMap().put("sdfilmntsc480i2997", createDestContextMap(
                "sdfilmntsc480i2997", "640", "480", "30000/1001", "true", "film"));
        map.getMap().put("sdtvntsc480i2997", createDestContextMap(
                "sdtvntsc480i2997", "640", "480", "30000/1001", "true", "tv"));
        map.getMap().put("sdfilmpal576p24", createDestContextMap(
                "sdfilmpal576p24", "720", "576", "24/1", "", "film"));
    }

    @Test
    public void testCorrectResolvingParameters() {
        VideoDestContextResolveStrategy resolveStrategy = new VideoDestContextResolveStrategy();

        resolveStrategy.setPackageType(ITunesPackageType.film)
                .setWidth(4096)
                .setHeight(2160)
                .setFrameRate(new BigFraction(60))
                .setInterlaced(false);
        assertEquals("hd1080p30", resolveStrategy.resolveContext(map).getName());

        resolveStrategy.setPackageType(ITunesPackageType.tv)
                .setWidth(4096)
                .setHeight(2160)
                .setFrameRate(new BigFraction(60))
                .setInterlaced(true);
        assertEquals("hd1080i2997", resolveStrategy.resolveContext(map).getName());

        resolveStrategy.setPackageType(ITunesPackageType.film)
                .setWidth(800)
                .setHeight(600)
                .setFrameRate(new BigFraction(30))
                .setInterlaced(true);
        //  hd 720 not allowed for film. Expected sd film format
        assertEquals("sdfilmntsc480i2997", resolveStrategy.resolveContext(map).getName());

        resolveStrategy.setPackageType(ITunesPackageType.tv)
                .setWidth(1919)
                .setHeight(1281)
                .setFrameRate(new BigFraction(24))
                .setInterlaced(true);
        assertEquals("hd720i23976", resolveStrategy.resolveContext(map).getName());

        resolveStrategy.setPackageType(ITunesPackageType.tv)
                .setWidth(800)
                .setHeight(600)
                .setFrameRate(new BigFraction(30))
                .setInterlaced(true);
        assertEquals("sdtvntsc480i2997", resolveStrategy.resolveContext(map).getName());
    }

    @Test(expected = ConversionException.class)
    public void testIncorrectWidth() {
        VideoDestContextResolveStrategy resolveStrategy = new VideoDestContextResolveStrategy();

        resolveStrategy.setPackageType(ITunesPackageType.film)
                .setWidth(639)
                .setHeight(2160)
                .setFrameRate(new BigFraction(60))
                .setInterlaced(true);
        resolveStrategy.resolveContext(map);
    }

    @Test(expected = ConversionException.class)
    public void testIncorrectHeight() {
        VideoDestContextResolveStrategy resolveStrategy = new VideoDestContextResolveStrategy();

        resolveStrategy.setPackageType(ITunesPackageType.film)
                .setWidth(4096)
                .setHeight(575)
                .setFrameRate(new BigFraction(60))
                .setInterlaced(false);
        resolveStrategy.resolveContext(map);
    }

    @Test(expected = ConversionException.class)
    public void testIncorrectFrameRate() {
        VideoDestContextResolveStrategy resolveStrategy = new VideoDestContextResolveStrategy();

        resolveStrategy.setPackageType(ITunesPackageType.film)
                .setWidth(4096)
                .setHeight(2160)
                .setFrameRate(new BigFraction(23))
                .setInterlaced(true);
        resolveStrategy.resolveContext(map);
    }

    @Test(expected = ConversionException.class)
    public void testIncorrectScanType() {
        VideoDestContextResolveStrategy resolveStrategy = new VideoDestContextResolveStrategy();

        resolveStrategy.setPackageType(ITunesPackageType.tv)
                .setWidth(800)
                .setHeight(600)
                .setFrameRate(new BigFraction(24000).divide(1001))
                .setInterlaced(true);
        // no interlaced format with fps < 24 defined in map
        resolveStrategy.resolveContext(map);
    }

    @Test(expected = ConversionException.class)
    public void testEmptyParameters() {
        VideoDestContextResolveStrategy resolveStrategy = new VideoDestContextResolveStrategy();
        // params not set
        resolveStrategy.resolveContext(map);
    }
}
