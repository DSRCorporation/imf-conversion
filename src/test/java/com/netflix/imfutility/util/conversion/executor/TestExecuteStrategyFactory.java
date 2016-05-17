package com.netflix.imfutility.util.conversion.executor;

import com.netflix.imfutility.conversion.executor.strategy.ExecuteStrategyFactory;

/**
 * A test execute strategy facytory to create execute straegies that start {@link FakeProcess} instead of real ones.
 */
public class TestExecuteStrategyFactory extends ExecuteStrategyFactory {

    private final TestExecutorLogger executorLogger;

    public TestExecuteStrategyFactory(TestExecutorLogger executorLogger) {
        this.executorLogger = executorLogger;
    }

    @Override
    protected FakeProcessStarter getProcessStarter() {
        return new FakeProcessStarter(executorLogger);
    }

}
