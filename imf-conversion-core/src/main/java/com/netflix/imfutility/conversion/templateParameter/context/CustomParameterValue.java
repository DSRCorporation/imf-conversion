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
package com.netflix.imfutility.conversion.templateParameter.context;

/**
 * An entity describing a custom parameter (such as Dynamic template parameter or Tmp template parameter).
 * In particular, it says whether a file defined by the parameter must be deleted on the program exit.
 */
public final class CustomParameterValue {

    private final String value;
    private final boolean deleteOnExit;

    public CustomParameterValue(String value, boolean deleteOnExit) {
        this.value = value;
        this.deleteOnExit = deleteOnExit;
    }

    public CustomParameterValue(String value) {
        this(value, false);
    }

    public String getValue() {
        return value;
    }

    public boolean isDeleteOnExit() {
        return deleteOnExit;
    }

}
