package com.netflix.imfutility.util.conversion.executor;

import com.netflix.imfutility.conversion.ConversionEngine;

/**
 * A test conversion engine which has all the logic of a real conversion engine, but starts {@link FakeProcess} instead of real ones.
 */
public class TestConversionEngine extends ConversionEngine {

    private final TestExecutorLogger executorLogger = new TestExecutorLogger();

    @Override
    protected TestExecuteStrategyFactory getExecuteStrategyFactory() {
        return new TestExecuteStrategyFactory(executorLogger);
    }

    public TestExecutorLogger getExecutorLogger() {
        return executorLogger;
    }

}
