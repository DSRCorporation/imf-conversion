package com.netflix.imfutility.conversion;

import com.netflix.imfutility.cpl.uuid.SequenceUUID;

/**
 * The exception thrown when it's not allowed (in config.xml) to silently convert source parameters to destination ones if they don't match.
 * Example: source fps is 25, and the destination one (as defined by conversion.xml), is 50, and config.xml says that silent conversion of fps is not allowed.
 */
public class ConversionNotAllowedException extends Exception {

    public ConversionNotAllowedException(String paramName, String sourceValue, String destinationValue, SequenceUUID seqUuid) {
        super(String.format("Source %1$s (%2$s) in virtual track '%4$s' doesn't match destination %1$s (%3$s). Conversion to destination value is disabled in config.xml.",
                paramName, sourceValue, destinationValue, seqUuid.toString()));
    }
}
