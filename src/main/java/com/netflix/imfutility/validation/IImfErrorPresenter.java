package com.netflix.imfutility.validation;

import com.netflix.imflibrary.utils.ErrorLogger;

import java.util.List;

/**
 * A generic interface to present (print) validation result.
 */
public interface IImfErrorPresenter {

    /**
     * Prints all validation findings in a proper format.
     *
     * @param errors     validation errors to be printed.
     * @param workingDir a working directory where a file with printed errors is created
     * @param fileName   a file name within working directory with printed errors
     */
    void printErrors(List<ErrorLogger.ErrorObject> errors, String workingDir, String fileName);

}
