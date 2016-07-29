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
package com.netflix.imfutility.xsd.config;

import com.netflix.imfutility.generated.config.ConversionParameterNameType;
import com.netflix.imfutility.generated.config.ConversionParameterType;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom map for generated {@link com.netflix.imfutility.generated.config.ConversionParametersType}.
 */
@XmlJavaTypeAdapter(ConversionParametersTypeMapAdapter.class)
public class ConversionParametersTypeMap {

    private final Map<ConversionParameterNameType, ConversionParameterType> map = new HashMap<>();

    public Map<ConversionParameterNameType, ConversionParameterType> getMap() {
        return map;
    }
}
