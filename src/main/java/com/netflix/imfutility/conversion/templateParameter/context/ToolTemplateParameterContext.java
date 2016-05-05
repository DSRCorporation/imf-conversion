package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;
import com.netflix.imfutility.xsd.config.ConfigType;
import com.netflix.imfutility.xsd.config.ToolType;

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
    public String resolveTemplateParameter(TemplateParameter templateParameter) {
        if (config.getExternalTools() == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(), "Config doesn't contain any external tools.");
        }
        ToolType param = config.getExternalTools().getMap().get(templateParameter.getName());
        if (param == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(), "Config doesn't contain '%s' external tool.");
        }
        return param.getValue();
    }

}
