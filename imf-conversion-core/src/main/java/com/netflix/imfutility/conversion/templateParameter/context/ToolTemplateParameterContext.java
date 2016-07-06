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
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;
import com.netflix.imfutility.generated.config.ConfigType;
import com.netflix.imfutility.generated.config.ToolType;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

/**
 * Tool Template Parameter Context.
 * <ul>
 * <li>It's used to replace tool template parameters in conversion operations</li>
 * <li>Created from config.xml (see {@link com.netflix.imfutility.generated.config.ExternalToolsType})</li>
 * </ul>
 */
public class ToolTemplateParameterContext implements ITemplateParameterContext {

    private final ConfigType config;

    public ToolTemplateParameterContext(ConfigType config) {
        this.config = config;
    }

    public String getParameterValue(String templateParameterName) {
        return getParameterValue(new TemplateParameter(TemplateParameterContext.TOOL, templateParameterName));
    }

    @Override
    public String resolveTemplateParameter(TemplateParameter templateParameter, ContextInfo contextInfo) {
        return getParameterValue(templateParameter);
    }

    public Collection<ToolType> getAllParameters() {
        return config.getExternalTools().getMap().values();
    }

    private String getParameterValue(TemplateParameter templateParameter) {
        if (config.getExternalTools() == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(), "Config doesn't contain any external tools.");
        }

        ToolType param = config.getExternalTools().getMap().get(templateParameter.getName());
        if (param == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(), "Config doesn't contain '%s' external tool.");
        }

        String paramValue = param.getValue();
        if (StringUtils.isEmpty(paramValue)) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(), "Config contains an empty '%s' external tool value.");
        }
        paramValue = paramValue.trim();
        if (StringUtils.isEmpty(paramValue)) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(), "Config contains an empty '%s' external tool value.");
        }

        return paramValue;
    }


}
