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
import org.junit.BeforeClass;
import org.junit.Test;

import static com.netflix.imfutility.util.TemplateParameterContextCreator.createDestContextMap;
import static junit.framework.TestCase.assertEquals;

/**
 * Tests that dest context resolves by name correctly.
 */
public class NameDestContextResolveStrategyTest {
    private static DestContextsTypeMap map;

    @BeforeClass
    public static void setUpAll() throws Exception {
        map = new DestContextsTypeMap();

        map.getMap().put("hd1080p30", createDestContextMap(
                "hd1080p30", "1920", "1080", "30/1", "false", null));
        map.getMap().put("hd1080i2997", createDestContextMap(
                "hd1080i2997", "1920", "1080", "30000/1001", "true", null));
        map.getMap().put("hd720i23976", createDestContextMap(
                "hd720i23976", "1280", "720", "24000/1001", "true", null));
        map.getMap().put("sdfilmntsc480i2997", createDestContextMap(
                "sdfilmntsc480i2997", "640", "480", "30000/1001", "true", null));
        map.getMap().put("sdtvntsc480i2997", createDestContextMap(
                "sdtvntsc480i2997", "640", "480", "30000/1001", "true", "3600"));
        map.getMap().put("sdfilmpal576p24", createDestContextMap(
                "sdfilmpal576p24", "720", "576", "24/1", "", null));
    }

    @Test
    public void testCorrectName() {
        NameDestContextResolveStrategy resolveStrategy;

        resolveStrategy = new NameDestContextResolveStrategy("sdfilmntsc480i2997", ITunesPackageType.film);
        assertEquals("sdfilmntsc480i2997", resolveStrategy.resolveContext(map).getName());

        resolveStrategy = new NameDestContextResolveStrategy("hd720i23976", ITunesPackageType.film);
        assertEquals("hd720i23976", resolveStrategy.resolveContext(map).getName());
    }

    @Test(expected = ConversionException.class)
    public void testIncorrectName() {
        NameDestContextResolveStrategy resolveStrategy = new NameDestContextResolveStrategy("xxxx", ITunesPackageType.film);

        resolveStrategy.resolveContext(map);
    }
}
