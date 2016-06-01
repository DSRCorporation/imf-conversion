package com.netflix.imfutility.conversion.templateParameter.context;

/**
 * An entity describing a custom parameter (such as Dynamic template parameter or Tmp template parameter).
 * In particular, it says whether a file defined by the parameter must be deleted on the program exit.
 */
public final class CustomParameterValue {

    private final String value;
    private final boolean deleteOnExit;

    public CustomParameterValue(String value, boolean deleteOnExit) {
        this.value = value;
        this.deleteOnExit = deleteOnExit;
    }

    public CustomParameterValue(String value) {
        this(value, false);
    }

    public String getValue() {
        return value;
    }

    public boolean isDeleteOnExit() {
        return deleteOnExit;
    }

}
