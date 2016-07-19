/*
 * Copyright (C) 2016 Netflix, Inc.
 *
 *     This file is part of IMF Conversion Utility.
 *
 *     IMF Conversion Utility is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     IMF Conversion Utility is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with IMF Conversion Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.netflix.imfutility.util.conversion.executor;

import com.netflix.imfutility.conversion.executor.strategy.ExecuteOnceStrategy;
import com.netflix.imfutility.conversion.executor.strategy.ExecutePipeStrategy;
import com.netflix.imfutility.conversion.executor.strategy.ExecuteStrategyFactory;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;

/**
 * A test execute strategy facytory to create execute straegies that start {@link FakeProcess} instead of real ones.
 */
public class TestExecuteStrategyFactory extends ExecuteStrategyFactory {

    private final TestExecutorLogger executorLogger;

    public TestExecuteStrategyFactory() {
        this(new TestExecutorLogger());
    }

    public TestExecuteStrategyFactory(TestExecutorLogger executorLogger) {
        this.executorLogger = executorLogger;
    }

    @Override
    public ExecuteOnceStrategy createExecuteOnceStrategy(TemplateParameterContextProvider contextProvider) {
        return new TestExecuteOnceStrategy(contextProvider, getProcessStarter(), executorLogger);
    }

    @Override
    public ExecutePipeStrategy createExecutePipeStrategy(TemplateParameterContextProvider contextProvider) {
        return new TestExecutePipeStrategy(contextProvider, getProcessStarter(), executorLogger);
    }

    @Override
    protected FakeProcessStarter getProcessStarter() {
        return new FakeProcessStarter(executorLogger);
    }

}
