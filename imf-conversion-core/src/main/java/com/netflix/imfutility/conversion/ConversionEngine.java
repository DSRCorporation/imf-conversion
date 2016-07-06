/**
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
package com.netflix.imfutility.conversion;

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.conversion.executor.ConversionExecutorOnce;
import com.netflix.imfutility.conversion.executor.ConversionExecutorPipe;
import com.netflix.imfutility.conversion.executor.ConversionExecutorSegment;
import com.netflix.imfutility.conversion.executor.ConversionExecutorSequence;
import com.netflix.imfutility.conversion.executor.strategy.ExecuteStrategyFactory;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.generated.conversion.*;

import java.io.IOException;

/**
 * Performs conversion to a destination format as specified in conversion.xml
 * <ul>
 * <li>The context must be already prepared and provided to the engine</li>
 * <li>Each conversion operation from conversion.xml is executed using an appropriate executor depending on the operation type.</li>
 * </ul>
 */
public class ConversionEngine {

    public void convert(FormatConfigurationType formatConfigurationType, TemplateParameterContextProvider contextProvider) throws IOException {
        for (Object operation : formatConfigurationType.getExecOnceOrExecEachSegmentOrExecEachSequence()) {
            if (operation instanceof ExecOnceType) {
                new ConversionExecutorOnce(contextProvider, getExecuteStrategyFactory(), (ExecOnceType) operation).execute();
            } else if (operation instanceof ExecEachSegmentSequenceType) {
                new ConversionExecutorSegment(contextProvider, getExecuteStrategyFactory(), (ExecEachSegmentSequenceType) operation).execute();
            } else if (operation instanceof ExecEachSequenceSegmentType) {
                new ConversionExecutorSequence(contextProvider, getExecuteStrategyFactory(), (ExecEachSequenceSegmentType) operation).execute();
            } else if (operation instanceof PipeType) {
                new ConversionExecutorPipe(contextProvider, getExecuteStrategyFactory(), (PipeType) operation).execute();
            } else if (operation instanceof DynamicParameterType) {
                contextProvider.getDynamicContext().addParameter((DynamicParameterType) operation, ContextInfo.EMPTY);
            } else {
                throw new ConversionException(String.format("Unknown Conversion Operation type: %s", operation.toString()));
            }
        }
    }

    /**
     * @return a factory to create execute strategies to execute external conversion operations.
     */
    public ExecuteStrategyFactory getExecuteStrategyFactory() {
        return new ExecuteStrategyFactory();
    }

}
