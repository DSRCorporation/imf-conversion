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

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

/**
 * Validator to check existing locales.
 */
public final class LocaleValidator {

    private LocaleValidator() {
    }

    public static void validateLocale(String str) throws LocaleValidationException {
        if (StringUtils.isBlank(str)) {
            throw new LocaleValidationException("Locale must be set.");
        }

        try {
            Locale locale = LocaleUtils.toLocale(str.replace("-", "_"));

            if (!LocaleUtils.availableLocaleList().contains(locale)) {
                throw new LocaleValidationException(String.format(
                        "Locale %s is unavailable.", str));
            }
        } catch (IllegalArgumentException e) {
            throw new LocaleValidationException("Locale validation failed.", e);
        }
    }

}
