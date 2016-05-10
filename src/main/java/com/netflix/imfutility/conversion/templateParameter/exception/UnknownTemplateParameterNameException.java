package com.netflix.imfutility.conversion.templateParameter.exception;

/**
 * Unknown Template parameter name exception.
 * It's usually thrown if template parameter name can not be resolved (not one of the defined parameter name for the parameter's context).
 */
public class UnknownTemplateParameterNameException extends AbstractTemplateParameterException {

    public UnknownTemplateParameterNameException(String templateParameterStr, String message) {
        super(templateParameterStr, message);
    }

}
