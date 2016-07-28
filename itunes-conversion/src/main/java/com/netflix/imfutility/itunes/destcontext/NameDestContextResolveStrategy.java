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
import com.netflix.imfutility.xsd.conversion.DestContextTypeMap;
import com.netflix.imfutility.xsd.conversion.DestContextsTypeMap;

/**
 * Resolve dest context by name.
 */
public class NameDestContextResolveStrategy implements DestContextResolveStrategy {
    private final String name;

    public NameDestContextResolveStrategy(String name) {
        this.name = name;
    }

    @Override
    public DestContextTypeMap resolveContext(DestContextsTypeMap destContexts) throws ConversionException {
        if (!destContexts.getMap().containsKey(name)) {
            throw new ConversionException(String.format("Format %s can't be defined.", name));
        }
        return destContexts.getMap().get(name);
    }
}
