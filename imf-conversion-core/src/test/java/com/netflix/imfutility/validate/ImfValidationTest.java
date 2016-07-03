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
