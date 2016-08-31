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
package com.netflix.imfutility.itunes.locale;

import org.junit.Test;

import java.util.Locale;

import static junit.framework.TestCase.assertEquals;

/**
 * Tests that locale helper works correct.
 * (see {@link LocaleHelper})
 */
public class LocaleHelperTest {

    @Test
    public void testFromITunesLocale() throws Exception {
        Locale locale;

        locale = LocaleHelper.fromITunesLocale("en");
        assertEquals(Locale.ENGLISH, locale);

        locale = LocaleHelper.fromITunesLocale("en-GB");
        assertEquals(Locale.UK, locale);

        // helper must create locales from xx_XX formatted strings too
        locale = LocaleHelper.fromITunesLocale("fr_CA");
        assertEquals(Locale.CANADA_FRENCH, locale);
    }

    @Test
    public void testToITunesLocale() throws Exception {
        String locale;

        locale = LocaleHelper.toITunesLocale(Locale.ENGLISH);
        assertEquals("en", locale);

        locale = LocaleHelper.toITunesLocale(Locale.UK);
        assertEquals("en-GB", locale);
    }

}
