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

import com.netflix.imfutility.conversion.executor.ProcessStarter;
import com.netflix.imfutility.conversion.executor.strategy.ExecutePipeStrategy;
import com.netflix.imfutility.conversion.executor.strategy.OperationInfo;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;

/**
 * Test execute strategy that logs skipped operations.
 */
public class TestExecutePipeStrategy extends ExecutePipeStrategy {

    private final TestExecutorLogger executorLogger;

    public TestExecutePipeStrategy(TemplateParameterContextProvider contextProvider, ProcessStarter processStarter,
                                   TestExecutorLogger executorLogger) {
        super(contextProvider, processStarter);
        this.executorLogger = executorLogger;
    }

    @Override
    protected void logSkipped(OperationInfo operationInfo) {
        super.logSkipped(operationInfo);
        executorLogger.skipOperation(operationInfo);
    }
}
