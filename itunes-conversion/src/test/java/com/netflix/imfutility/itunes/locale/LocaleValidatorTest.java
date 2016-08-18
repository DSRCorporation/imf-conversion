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

/**
 * Tests that locale validation works correct.
 * (see {@link LocaleValidator})
 */
public class LocaleValidatorTest {

    @Test
    public void testCorrectValidation() throws Exception {
        LocaleValidator.validateLocale("en");
        LocaleValidator.validateLocale("en_US");
        LocaleValidator.validateLocale("fr-CA");
    }

    @Test(expected = LocaleValidationException.class)
    public void testNullLocale() throws Exception {
        LocaleValidator.validateLocale(null);
    }

    @Test(expected = LocaleValidationException.class)
    public void testEmptyLocale() throws Exception {
        LocaleValidator.validateLocale("");
    }

    @Test(expected = LocaleValidationException.class)
    public void testIncorrectFormatLocale() throws Exception {
        LocaleValidator.validateLocale("fr_CA-");
    }

    @Test(expected = LocaleValidationException.class)
    public void testUnavailableLocale() throws Exception {
        LocaleValidator.validateLocale("xx_XX");
    }
}
