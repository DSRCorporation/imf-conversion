package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.xsd.config.IMFUtilityConfigType;

/**
 * Created by Alexander on 4/25/2016.
 */
public class ToolTemplateParameterContext implements ITemplateParameterContext {

    private IMFUtilityConfigType config;

    public ToolTemplateParameterContext(IMFUtilityConfigType config) {
        this.config = config;
    }

    @Override
    public String resolveTemplateParameter(TemplateParameter templateParameter) {
        if (config.getExternalTools() == null) {
            return null;
        }
        return "";//config.getExternalTools().get(templateParameter.getName()).getValue();
    }

}
