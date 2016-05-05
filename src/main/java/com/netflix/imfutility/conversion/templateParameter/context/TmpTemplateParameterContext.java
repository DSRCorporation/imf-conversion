package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;
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
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(), "Conversion.xml doesn't contain any tmp context parameters.");
        }
        ParamType param = format.getTmpContext().getMap().get(templateParameter.getName());
        if (param == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(), "Conversion.xml doesn't contain '%s' tmp parameter.");
        }
        return param.getValue();
    }

    public Collection<ParamType> getAllParameters() {
        return format.getTmpContext().getMap().values();
    }

}
