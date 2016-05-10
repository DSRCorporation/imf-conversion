package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.xsd.conversion.FormatType;
import com.netflix.imfutility.xsd.conversion.ParamType;

import java.util.Collection;

/**
 * Tmp Template Parameter Context.
 * <ul>
 * <li>It's used to replace tmp template parameters in conversion operations</li>
 * <li>Created from conversion.xml (see {@link com.netflix.imfutility.xsd.conversion.TmpContextType})</li>
 * </ul>
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

    public Collection<ParamType> getAllParameters() {
        return format.getTmpContext().getMap().values();
    }

}
