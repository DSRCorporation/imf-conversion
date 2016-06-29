package com.netflix.imfutility;

/**
 * A general runtime exception thrown during conversion. Throwing this exception aborts conversion process.
 * <p>
 * Created by Alexander on 5/30/2016.
 */
public class ConversionException extends RuntimeException {

    public ConversionException(String message) {
        super(message);
    }

    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }

}
