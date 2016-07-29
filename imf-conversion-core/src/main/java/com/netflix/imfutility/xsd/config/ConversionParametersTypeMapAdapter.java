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

import com.netflix.imfutility.generated.config.ConversionParameterType;
import com.netflix.imfutility.generated.config.ConversionParametersType;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Maps generated {@link ConversionParametersType} to {@link ConversionParametersTypeMap}.
 */
public class ConversionParametersTypeMapAdapter extends
        XmlAdapter<ConversionParametersType, ConversionParametersTypeMap> {

    @Override
    public ConversionParametersTypeMap unmarshal(ConversionParametersType parameters)
            throws Exception {
        ConversionParametersTypeMap map = new ConversionParametersTypeMap();
        for (ConversionParameterType parameter : parameters.getParam()) {
            map.getMap().put(parameter.getName(), parameter);
        }
        return map;
    }

    @Override
    public ConversionParametersType marshal(ConversionParametersTypeMap map)
            throws Exception {
        ConversionParametersType parameters = new ConversionParametersType();
        for (ConversionParameterType value : map.getMap().values()) {
            parameters.getParam().add(value);
        }
        return parameters;
    }
}
