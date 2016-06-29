package com.netflix.imfutility.conversion.templateParameter.exception;

/**
 * Invalid template parameter format exception.
 */
public class InvalidTemplateParameterException extends AbstractTemplateParameterException {

    public InvalidTemplateParameterException(String templateParameterStr, String message) {
        super(templateParameterStr, message);
    }

}
