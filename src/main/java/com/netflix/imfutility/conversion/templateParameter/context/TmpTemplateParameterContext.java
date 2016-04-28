package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.xsd.conversion.FormatType;

/**
 * Created by Alexander on 4/25/2016.
 */
public class TmpTemplateParameterContext implements ITemplateParameterContext {

    private final FormatType format;

    public TmpTemplateParameterContext(FormatType format) {
        this.format = format;
    }

    @Override
    public String resolveTemplateParameter(TemplateParameter templateParameter) {
        if (format.getTmpContext() == null) {
            return null;
        }
        return format.getTmpContext().getMap().get(templateParameter.getName()).getValue();
    }

}
