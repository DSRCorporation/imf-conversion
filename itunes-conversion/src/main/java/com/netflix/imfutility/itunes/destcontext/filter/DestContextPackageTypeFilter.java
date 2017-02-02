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
package com.netflix.imfutility.itunes.destcontext.filter;

import com.netflix.imfutility.itunes.ITunesPackageType;
import com.netflix.imfutility.itunes.destcontext.wrap.DestContextMapWrapper;
import com.netflix.imfutility.xsd.conversion.DestContextTypeMap;

import java.util.Objects;
import java.util.function.Predicate;

import static com.netflix.imfutility.itunes.ITunesConversionConstants.DEST_PARAM_VIDEO_SPECIFIED_FOR;

/**
 * Class to filter dest context depends on package type (film or tv).
 */
public class DestContextPackageTypeFilter implements Predicate<DestContextTypeMap> {
    private final ITunesPackageType packageType;

    public DestContextPackageTypeFilter(ITunesPackageType packageType) {
        this.packageType = packageType;
    }

    @Override
    public boolean test(DestContextTypeMap destContextTypeMap) {
        if (packageType == null) {
            return true;
        }

        DestContextMapWrapper wrapper = new DestContextMapWrapper(destContextTypeMap);

        ITunesPackageType packageType = ITunesPackageType.fromName(wrapper.getValue(DEST_PARAM_VIDEO_SPECIFIED_FOR));

        return packageType == null || Objects.equals(packageType, this.packageType);
    }
}
