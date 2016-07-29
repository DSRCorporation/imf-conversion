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
import com.netflix.imfutility.generated.conversion.ForType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * An executor for {@link ForType} conversion operation that described simple "for" loop functionality.
 */
public class ConversionExecutorFor extends AbstractConversionExecutor {

    private final ForType forElem;
    private final TemplateParameterResolver parameterResolver;
    private final Map<String, Integer> iterators;

    public ConversionExecutorFor(TemplateParameterContextProvider contextProvider,
            ExecuteStrategyFactory executeStrategyFactory, ForType forElem) {

        this(contextProvider, executeStrategyFactory, forElem, new HashMap<>());
    }

    public ConversionExecutorFor(TemplateParameterContextProvider contextProvider,
            ExecuteStrategyFactory executeStrategyFactory, ForType forElem, Map<String, Integer> iterators) {

        super(contextProvider, executeStrategyFactory);

        this.parameterResolver = new TemplateParameterResolver(contextProvider);
        this.forElem = forElem;
        this.iterators = iterators;
    }

    @Override
    public void execute() throws IOException {

        String iterator = forElem.getIterator(); // current iterator
        String resolvedFrom =
                parameterResolver.resolveTemplateAndIteratorParameter(forElem.getFrom(), iterators, ContextInfo.EMPTY);
        String resolvedTo =
                parameterResolver.resolveTemplateAndIteratorParameter(forElem.getTo(), iterators, ContextInfo.EMPTY);
        String resolvedCount =
                parameterResolver.resolveTemplateAndIteratorParameter(forElem.getCount(), iterators, ContextInfo.EMPTY);
        int from = toInt("from", resolvedFrom);
        int to = toInt("to", resolvedTo);
        int count = toInt("count", resolvedCount);
        int internalCounter = (count != 0) ? count : to - from;

        if (internalCounter <= 0) {
            return;
        }

        iterators.put(iterator, from);
        for (int i = 0; i < internalCounter; i++) {

            for (Object operation : forElem.getDynamicParameterOrFor()) {
                if (operation instanceof ForType) {
                    new ConversionExecutorFor(contextProvider,
                            executeStrategyFactory, (ForType) operation, iterators).execute();
                } else if (operation instanceof DynamicParameterConcatType) {
                    addDynamicParameter((DynamicParameterConcatType) operation, iterators);
                } else {
                    throw new ConversionException(
                            String.format("Unknown Conversion Operation type: %s", operation.toString()));
                }
            }

            iterators.put(iterator, iterators.get(iterator) + 1);
        }

        iterators.remove(iterator);
    }

    /**
     * Resolves dynamic parameter name and value and add to dynamic context.
     *
     * @param dynParam dynamic parameter to add
     */
    private void addDynamicParameter(DynamicParameterConcatType dynParam, Map<String, Integer> iterators) {
        DynamicParameterConcatType resolvedParameter = contextProvider.getDynamicContext().cloneParameter(dynParam);

        resolvedParameter.setName(parameterResolver.resolveIteratorParameter(resolvedParameter.getName(), iterators));
        resolvedParameter.setValue(parameterResolver.resolveIteratorParameter(resolvedParameter.getValue(), iterators));
        resolvedParameter.setAdd(parameterResolver.resolveIteratorParameter(resolvedParameter.getAdd(), iterators));
        contextProvider.getDynamicContext().addParameter(resolvedParameter, ContextInfo.EMPTY,
                skipOperationResolver.isSkip(dynParam));
    }

    private int toInt(String parameterName, String parameterStr) {
        try {
            return Integer.parseInt(parameterStr);
        } catch (NumberFormatException e) {
            throw new ConversionException(
                    String.format("'for' attribute '%s' (%s) should be an integer.", parameterName, parameterStr), e);
        }
    }
}
