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
package com.netflix.imfutility.validate;

import com.netflix.imfutility.ImfUtilityTest;
import com.netflix.imfutility.conversion.executor.strategy.ExecuteStrategyFactory;
import com.netflix.imfutility.conversion.templateParameter.context.DynamicTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.DynamicContextParameters;
import com.netflix.imfutility.util.ImpUtils;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

/**
 * The test to check that IMF validation works.
 */
public class ImfValidationTest extends ImfUtilityTest {

    @Test
    @Ignore
    public void testValidationPass() throws Exception {
        ImfValidator validator = createImfValidator(ImpUtils.getCorrectImpForValidation(), ImpUtils.getCorrectCplForValidation());
        validator.validate();
    }

    @Test(expected = ImfValidationException.class)
    @Ignore
    public void testValidationFailed() throws Exception {
        ImfValidator validator = createImfValidator(ImpUtils.getInvalidImpForValidation(), ImpUtils.getInvalidCplForValidation());
        validator.validate();
    }


    private ImfValidator createImfValidator(File imp, File cpl) throws Exception {
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        DynamicTemplateParameterContext dynamicContext = contextProvider.getDynamicContext();
        dynamicContext.addParameter(DynamicContextParameters.IMP, imp.getAbsolutePath());
        dynamicContext.addParameter(DynamicContextParameters.CPL, cpl.getAbsolutePath());
        return new ImfValidator(contextProvider, new ExecuteStrategyFactory());
    }

}
