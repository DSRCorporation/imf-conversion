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

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;

/**
 * Template Parameter Context.
 * <ul>
 * <li>It's used to replace template parameters in conversion operations.</li>
 * </ul>
 */
public interface ITemplateParameterContext {

    /**
     * Resolves the given parameter.
     * The returned value is never null.
     * A runtime exception is thrown if parameter can not be resolved.
     *
     * @param templateParameter the template parameter to be resolved.
     * @param contextInfo       a context info helping to resolve the parameter.
     * @return resolved parameter value as a string. Never null.
     */
    String resolveTemplateParameter(TemplateParameter templateParameter, ContextInfo contextInfo);

}
