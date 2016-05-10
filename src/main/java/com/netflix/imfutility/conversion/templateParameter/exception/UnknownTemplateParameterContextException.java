package com.netflix.imfutility.conversion.templateParameter.exception;

/**
 * Unknown Template parameter context exception.
 * It's usually thrown if template parameter context can not be resolved (not one of the defined contexts).
 */
public class UnknownTemplateParameterContextException extends AbstractTemplateParameterException {

    public UnknownTemplateParameterContextException(String templateParameterStr, String message) {
        super(templateParameterStr, message);
    }
}
