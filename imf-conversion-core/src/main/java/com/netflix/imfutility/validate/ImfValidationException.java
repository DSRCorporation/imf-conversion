package com.netflix.imfutility.validate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An Exception class to wrap IMF validation errors.
 */
public class ImfValidationException extends Exception {

    private List<String> errors;

    public ImfValidationException(List<String> errors) {
        super();
        this.errors = errors;
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
    }

    @Override
    public String getMessage() {
        return errors.stream().collect(Collectors.joining("\n", "\n[", "]"));
    }

}
