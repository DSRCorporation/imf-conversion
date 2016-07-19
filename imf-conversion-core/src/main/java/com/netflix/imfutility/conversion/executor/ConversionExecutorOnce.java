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
package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.conversion.executor.strategy.ExecuteStrategyFactory;
import com.netflix.imfutility.conversion.executor.strategy.OperationInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.generated.conversion.ExecOnceType;
import com.netflix.imfutility.util.ExecTypeUtils;

import java.io.IOException;

/**
 * An executor for {@link ExecOnceType} conversion operation.
 * It simply starts the conversion operation and waits until it's finished
 * (see {@link com.netflix.imfutility.conversion.executor.strategy.ExecuteOnceStrategy}).
 */
public class ConversionExecutorOnce extends AbstractConversionExecutor {

    private final ExecOnceType operation;

    public ConversionExecutorOnce(TemplateParameterContextProvider contextProvider, ExecuteStrategyFactory strategyProvider,
                                  ExecOnceType operation) {
        super(contextProvider, strategyProvider);
        this.operation = operation;
    }

    @Override
    public void execute() throws IOException {
        OperationInfo operationInfo = new OperationInfo(operation.getValue(), operation.getName(), ContextInfo.EMPTY,
                ExecTypeUtils.isSkip(operation));
        executeStrategyFactory.createExecuteOnceStrategy(contextProvider).execute(operationInfo);
    }


}
