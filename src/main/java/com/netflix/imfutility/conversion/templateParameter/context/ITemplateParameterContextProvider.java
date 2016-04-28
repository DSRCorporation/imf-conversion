package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.conversion.templateParameter.TemplateParameterContext;
import com.netflix.imfutility.xsd.config.IMFUtilityConfigType;
import com.netflix.imfutility.xsd.conversion.FormatType;

/**
 * Created by Alexander on 4/26/2016.
 */
public interface ITemplateParameterContextProvider {

    ITemplateParameterContext getContext(TemplateParameterContext context);

}
