package com.netflix.imfutility.conversion.templateParameter.exception;

/**
 * Base class for all exception related to Template Parameters.
 * <ul>
 * <li>Extends {@link RuntimeException}</li>
 * <li>Adds a default suffix to all exception messages</li>
 * </ul>
 */
public abstract class AbstractTemplateParameterException extends RuntimeException {

    public AbstractTemplateParameterException(String templateParameterStr, String message) {
        super(String.format("Can not resolve '%s' template parameter. %s.", templateParameterStr, message));
    }

}
