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
package com.netflix.imfutility.itunes.destcontext.wrap;

import com.netflix.imfutility.generated.conversion.DestContextParamType;
import com.netflix.imfutility.util.ConversionHelper;
import com.netflix.imfutility.xsd.conversion.DestContextTypeMap;
import org.apache.commons.math3.fraction.BigFraction;

/**
 * Wrapper for simplifying work {@link DestContextTypeMap}.
 */
public class DestContextMapWrapper {
    private final DestContextTypeMap map;

    public DestContextMapWrapper(DestContextTypeMap map) {
        this.map = map;
    }

    public DestContextTypeMap getMap() {
        return map;
    }

    public String getValue(String paramName) {
        DestContextParamType param = map.getMap().get(paramName);
        return param != null ? param.getValue() : null;
    }

    public Integer getValueAsInteger(String paramName) {
        String value = getValue(paramName);
        return value != null ? Integer.parseInt(value) : null;
    }

    public Long getValueAsLong(String paramName) {
        String value = getValue(paramName);
        return value != null ? Long.parseLong(value) : null;
    }

    public BigFraction getValueAsFrameRate(String paramName) {
        String value = getValue(paramName);
        return value != null ? ConversionHelper.parseEditRate(value) : null;
    }

    public Boolean getValueAsBoolean(String paramName) {
        String value = getValue(paramName);
        return value != null && Boolean.parseBoolean(value);
    }

    public <T extends Comparable<T>> int compare(T value, T comp, boolean nullsFirst) {
        if (value == null) {
            return nullsFirst ? 1 : -1;
        }
        return value.compareTo(comp);
    }

    public int compareToInteger(String paramName, Integer comp, boolean nullsFirst) {
        return compare(getValueAsInteger(paramName), comp, nullsFirst);
    }

    public int compareToLong(String paramName, Long comp, boolean nullsFirst) {
        return compare(getValueAsLong(paramName), comp, nullsFirst);
    }

    public int compareToFrameRate(String paramName, BigFraction comp, boolean nullsFirst) {
        return compare(getValueAsFrameRate(paramName), comp, nullsFirst);
    }
}