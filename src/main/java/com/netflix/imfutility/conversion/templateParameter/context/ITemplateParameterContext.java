package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;

/**
 * Template Parameter Context.
 * <ul>
 * <li>It's used to replace template parameters in conversion operations</li>
 * </ul>
 */
public interface ITemplateParameterContext {

    String resolveTemplateParameter(TemplateParameter templateParameter);

}
