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
package com.netflix.imfutility.validation;

import com.netflix.imflibrary.utils.ErrorLogger;

import java.util.List;

/**
 * A generic interface to present (print) validation result.
 */
public interface IImfErrorPresenter {

    /**
     * Prints all validation findings in a proper format.
     *
     * @param errors     validation errors to be printed.
     * @param workingDir a working directory where a file with printed errors is created
     * @param fileName   a file name within working directory with printed errors
     */
    void printErrors(List<ErrorLogger.ErrorObject> errors, String workingDir, String fileName);

}
