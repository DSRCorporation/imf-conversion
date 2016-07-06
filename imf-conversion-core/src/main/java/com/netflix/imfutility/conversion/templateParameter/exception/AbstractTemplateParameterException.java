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
package com.netflix.imfutility.conversion.templateParameter.exception;

/**
 * Base class for all exception related to Template Parameters.
 * <ul>
 * <li>Extends {@link RuntimeException}</li>
 * <li>Adds a default suffix to all exception messages</li>
 * </ul>
 */
public abstract class AbstractTemplateParameterException extends RuntimeException {

    public AbstractTemplateParameterException(String templateParameterStr, String message) {
        super(String.format("Can not resolve '%s' template parameter. %s", templateParameterStr, message));
    }

}
