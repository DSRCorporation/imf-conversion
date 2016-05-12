package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;
import com.netflix.imfutility.xsd.config.ConfigType;
import com.netflix.imfutility.xsd.config.ToolType;
import org.apache.commons.lang3.StringUtils;

/**
 * Tool Template Parameter Context.
 * <ul>
 * <li>It's used to replace tool template parameters in conversion operations</li>
 * <li>Created from config.xml (see {@link com.netflix.imfutility.xsd.config.ExternalToolsType})</li>
 * </ul>
 */
public class ToolTemplateParameterContext implements ITemplateParameterContext {

    private final ConfigType config;

    public ToolTemplateParameterContext(ConfigType config) {
        this.config = config;
    }

    @Override
    public String resolveTemplateParameter(TemplateParameter templateParameter, ContextInfo contextInfo) {
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
