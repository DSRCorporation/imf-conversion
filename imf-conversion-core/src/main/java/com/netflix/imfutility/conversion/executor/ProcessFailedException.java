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
package com.netflix.imfutility.conversion.executor;

/**
 * An exception thrown when an external process execution fails (either due to IOException, or exit code is non-zero).
 */
public class ProcessFailedException extends RuntimeException {

    public ProcessFailedException(ExternalProcess process, int errorCode) {
        super(String.format("Execution of '%s' process failed: exit code '%d'. See log folder for details.", process.toString(), errorCode));
    }

    public ProcessFailedException(String message, Throwable e) {
        super(message, e);
    }
}
