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

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.conversion.executor.strategy.ExecuteStrategyFactory;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterResolver;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.generated.conversion.DynamicParameterConcatType;
import com.netflix.imfutility.generated.conversion.ExecEachSegmentSequenceType;
import com.netflix.imfutility.generated.conversion.ExecEachSequenceSegmentType;
import com.netflix.imfutility.generated.conversion.ExecOnceType;
import com.netflix.imfutility.generated.conversion.ForType;
import com.netflix.imfutility.generated.conversion.IfType;
import com.netflix.imfutility.generated.conversion.PipeType;

import java.io.IOException;

/**
 * An executor for {@link IfType} conversion operation that described simple "if" condition functionality.
 */
public class ConversionExecutorIf extends AbstractConversionExecutor {

    private final IfType ifElem;
    private final TemplateParameterResolver parameterResolver;

    public ConversionExecutorIf(TemplateParameterContextProvider contextProvider,
                                ExecuteStrategyFactory executeStrategyFactory,
                                IfType ifElem) {
        super(contextProvider, executeStrategyFactory);
        this.parameterResolver = new TemplateParameterResolver(contextProvider);
        this.ifElem = ifElem;
    }

    @Override
    public void execute() throws IOException {
        String ifValue = parameterResolver.resolveTemplateParameter(ifElem.getTest(), ContextInfo.EMPTY);

        //  skip section on false condition
        if (!Boolean.valueOf(ifValue)) {
            return;
        }

        for (Object operation : ifElem.getExecOnceOrExecEachSegmentOrExecEachSequence()) {
            if (operation instanceof ExecOnceType) {
                new ConversionExecutorOnce(contextProvider, executeStrategyFactory, (ExecOnceType) operation).execute();
            } else if (operation instanceof ExecEachSegmentSequenceType) {
                new ConversionExecutorSegment(contextProvider, executeStrategyFactory,
                        (ExecEachSegmentSequenceType) operation).execute();
            } else if (operation instanceof ExecEachSequenceSegmentType) {
                new ConversionExecutorSequence(contextProvider, executeStrategyFactory,
                        (ExecEachSequenceSegmentType) operation).execute();
            } else if (operation instanceof PipeType) {
                new ConversionExecutorPipe(contextProvider, executeStrategyFactory, (PipeType) operation).execute();
            } else if (operation instanceof DynamicParameterConcatType) {
                new ConversionExecutorDynamicParameter(contextProvider, executeStrategyFactory,
                        (DynamicParameterConcatType) operation).execute();
            } else if (operation instanceof ForType) {
                new ConversionExecutorFor(contextProvider, executeStrategyFactory, (ForType) operation).execute();
            } else if (operation instanceof IfType) {
                new ConversionExecutorIf(contextProvider, executeStrategyFactory, (IfType) operation).execute();
            } else {
                throw new ConversionException(String.format("Unknown Conversion Operation type: %s", operation.toString()));
            }
        }
    }

}
