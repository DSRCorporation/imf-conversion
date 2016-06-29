package com.netflix.imfutility.conversion.executor;

import java.io.IOException;

/**
 * A specific conversion executor is created for each conversion operation type.
 */
public interface IConversionExecutor {

    void execute() throws IOException;

}
