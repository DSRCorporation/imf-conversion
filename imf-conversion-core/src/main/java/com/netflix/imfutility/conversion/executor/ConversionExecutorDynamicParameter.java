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
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.generated.conversion.DynamicParameterConcatType;

import java.io.IOException;

/**
 * An executor for {@link DynamicParameterConcatType}.
 * It adds the dynamic parameter to the dynamic parameter context if it's not skipped.
 */
public class ConversionExecutorDynamicParameter extends AbstractConversionExecutor {

    private final DynamicParameterConcatType dynamicParameter;

    public ConversionExecutorDynamicParameter(TemplateParameterContextProvider contextProvider,
                                              ExecuteStrategyFactory executeStrategyFactory,
                                              DynamicParameterConcatType dynamicParameter) {
        super(contextProvider, executeStrategyFactory);
        this.dynamicParameter = dynamicParameter;
    }

    @Override
    public void execute() throws IOException {
        contextProvider.getDynamicContext()
                .addParameter(dynamicParameter, ContextInfo.EMPTY,
                        skipOperationResolver.isSkip(dynamicParameter));
    }

}
