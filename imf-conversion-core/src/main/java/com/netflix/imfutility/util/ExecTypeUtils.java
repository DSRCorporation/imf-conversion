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
package com.netflix.imfutility.util;

import com.netflix.imfutility.generated.conversion.ExecComplexType;
import com.netflix.imfutility.generated.conversion.ExecSimpleType;

import java.util.Arrays;

/**
 * Utils for ExecType.
 * (see {@link ExecSimpleType} {@link ExecComplexType}).
 */
public final class ExecTypeUtils {
    private ExecTypeUtils() {
    }

    public static boolean isSkip(ExecSimpleType execType, ExecComplexType... parentExecTypes) {
        if (parentExecTypes == null || parentExecTypes.length == 0) {
            return isSkip(execType);
        }

        return Arrays.asList(parentExecTypes).stream().anyMatch(ExecTypeUtils::isSkip) || isSkip(execType);
    }

    public static boolean isSkip(ExecSimpleType execType) {
        return isSkip(execType.getIf(), execType.getUnless());
    }

    public static boolean isSkip(ExecComplexType execType) {
        return isSkip(execType.getIf(), execType.getUnless());
    }

    private static boolean isSkip(String ifValue, String unlessValue) {
        return !Boolean.parseBoolean(ifValue) || Boolean.parseBoolean(unlessValue);
    }

}
