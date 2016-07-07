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
package com.netflix.imfutility.conversion;

import com.netflix.imfutility.cpl.uuid.SequenceUUID;

/**
 * The exception thrown when it's not allowed (in config.xml) to silently convert source parameters to destination ones
 * if they don't match.
 * Example: source fps is 25, and the destination one (as defined by conversion.xml), is 50, and config.xml says that
 * silent conversion of fps is not allowed.
 */
public class ConversionNotAllowedException extends Exception {

    public ConversionNotAllowedException(String paramName, String sourceValue, String destinationValue, SequenceUUID seqUuid) {
        super(String.format(
                "Source %1$s (%2$s) in virtual track '%4$s' doesn't match destination %1$s (%3$s)."
                        + " Conversion to destination value is disabled in config.xml.",
                paramName, sourceValue, destinationValue, seqUuid.toString()));
    }
}
