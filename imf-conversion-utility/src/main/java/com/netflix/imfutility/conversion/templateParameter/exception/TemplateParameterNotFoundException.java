package com.netflix.imfutility.conversion.templateParameter.exception;

/**
 * Template parameters can not be found exception.
 * It's usually thrown if there are no defined parameters (neither in code, nor config.xml, nor conversion.xml)
 * for the given template parameter (context and name).
 */
public class TemplateParameterNotFoundException extends AbstractTemplateParameterException {

    public TemplateParameterNotFoundException(String templateParameterStr, String message) {
        super(templateParameterStr, message);
    }
}
