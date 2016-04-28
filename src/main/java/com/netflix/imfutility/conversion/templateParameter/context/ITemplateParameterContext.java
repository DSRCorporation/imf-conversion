package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;

/**
 * Created by Alexander on 4/25/2016.
 */
public interface ITemplateParameterContext {

    String resolveTemplateParameter(TemplateParameter templateParameter);

}
