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
package com.netflix.imfutility.conversion;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfoBuilder;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterResolver;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;
import com.netflix.imfutility.conversion.templateParameter.exception.UnknownTemplateParameterContextException;
import com.netflix.imfutility.cpl.uuid.ResourceUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.ExecComplexType;
import com.netflix.imfutility.generated.conversion.ExecEachSegmentSequenceType;
import com.netflix.imfutility.generated.conversion.ExecEachSequenceType;
import com.netflix.imfutility.generated.conversion.ExecOnceType;
import com.netflix.imfutility.generated.conversion.ExecSimpleType;
import com.netflix.imfutility.util.SkipOperationResolver;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link SkipOperationResolver}
 */
public class SkipOperationResolverTest {

    private static TemplateParameterContextProvider contextProvider;

    @BeforeClass
    public static void setUpAll() throws Exception {
        contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        contextProvider.getDynamicContext().addParameter("trueParam", "true");
        contextProvider.getDynamicContext().addParameter("falseParam", "false");
        contextProvider.getDynamicContext().addParameter("damagedParam", "123");
    }

    @Test
    public void testOperationSkipped() {
        SkipOperationResolver resolver = new SkipOperationResolver(new TemplateParameterResolver(contextProvider));

        ExecSimpleType falseExec = new ExecOnceType();
        falseExec.setIf("false");

        ExecSimpleType ifParamExec = new ExecOnceType();
        ifParamExec.setIf("%{dynamic.falseParam}");

        ExecComplexType unlessParamExec = new ExecEachSequenceType();
        unlessParamExec.setUnless("%{dynamic.trueParam}");

        ExecComplexType ifUnlessParamExec = new ExecEachSegmentSequenceType();
        ifUnlessParamExec.setIf("%{dynamic.trueParam}");        // not skipped
        ifUnlessParamExec.setUnless("%{dynamic.trueParam}");    // skipped

        assertTrue(resolver.setContextInfo(ContextInfo.EMPTY).isSkip(falseExec));
        assertTrue(resolver.setContextInfo(ContextInfo.EMPTY).isSkip(ifParamExec));
        assertTrue(resolver.setContextInfo(ContextInfo.EMPTY).isSkip(unlessParamExec));
        assertTrue(resolver.setContextInfo(ContextInfo.EMPTY).isSkip(ifUnlessParamExec));
    }

    @Test
    public void testOperationNotSkipped() {
        SkipOperationResolver resolver = new SkipOperationResolver(new TemplateParameterResolver(contextProvider));

        ExecSimpleType trueExec = new ExecOnceType();
        trueExec.setUnless("false");

        ExecSimpleType ifParamExec = new ExecOnceType();
        ifParamExec.setIf("%{dynamic.trueParam}");

        ExecComplexType unlessParamExec = new ExecEachSequenceType();
        unlessParamExec.setUnless("%{dynamic.falseParam}");

        ExecComplexType ifUnlessParamExec = new ExecEachSegmentSequenceType();
        ifUnlessParamExec.setIf("%{dynamic.trueParam}");
        ifUnlessParamExec.setUnless("%{dynamic.falseParam}");

        assertFalse(resolver.setContextInfo(ContextInfo.EMPTY).isSkip(trueExec));
        assertFalse(resolver.setContextInfo(ContextInfo.EMPTY).isSkip(ifParamExec));
        assertFalse(resolver.setContextInfo(ContextInfo.EMPTY).isSkip(unlessParamExec));
        assertFalse(resolver.setContextInfo(ContextInfo.EMPTY).isSkip(ifUnlessParamExec));
    }

    @Test
    public void testFewOperationsSkipped() {
        SkipOperationResolver resolver = new SkipOperationResolver(new TemplateParameterResolver(contextProvider));

        ExecSimpleType trueExec1 = new ExecOnceType();
        trueExec1.setUnless("false");

        ExecComplexType trueExec2 = new ExecComplexType();
        trueExec2.setIf("true");

        ExecComplexType falseExec = new ExecComplexType();
        falseExec.setIf("false");

        assertTrue(resolver.setContextInfo(ContextInfo.EMPTY).isSkip(trueExec1, trueExec2, falseExec));
    }

    @Test
    public void testInvalidBoolean() {
        SkipOperationResolver resolver = new SkipOperationResolver(new TemplateParameterResolver(contextProvider));

        ExecOnceType ifInvalidExec = new ExecOnceType();
        ifInvalidExec.setIf("true");
        ifInvalidExec.setUnless("123");     // convert to false in result

        assertFalse(resolver.setContextInfo(ContextInfo.EMPTY).isSkip(ifInvalidExec));
    }

    @Test(expected = TemplateParameterNotFoundException.class)
    public void testInvalidContextParameter() {
        SkipOperationResolver resolver = new SkipOperationResolver(new TemplateParameterResolver(contextProvider));

        ExecOnceType exec = new ExecOnceType();
        exec.setIf("%{dynamic.null}");

        resolver.setContextInfo(ContextInfo.EMPTY).isSkip(exec);
    }

    @Test(expected = UnknownTemplateParameterContextException.class)
    public void testInvalidContext() {
        SkipOperationResolver resolver = new SkipOperationResolver(new TemplateParameterResolver(contextProvider));

        ExecOnceType exec = new ExecOnceType();
        exec.setIf("%{null.true}");

        resolver.setContextInfo(ContextInfo.EMPTY).isSkip(exec);
    }

    @Test
    public void testDamagedContextParameter() {
        SkipOperationResolver resolver = new SkipOperationResolver(new TemplateParameterResolver(contextProvider));

        ExecOnceType exec = new ExecOnceType();
        exec.setIf("%{dynamic.damagedParam}"); // convert to false in result

        assertTrue(resolver.setContextInfo(ContextInfo.EMPTY).isSkip(exec));
    }

    @Test
    public void testEmptyParameter() {
        SkipOperationResolver resolver = new SkipOperationResolver(new TemplateParameterResolver(contextProvider));

        ExecOnceType exec = new ExecOnceType();
        exec.setIf("");                 // convert to false in result

        assertTrue(resolver.setContextInfo(ContextInfo.EMPTY).isSkip(exec));
    }

    @Test
    public void testContextInfo() {
        SkipOperationResolver resolver = new SkipOperationResolver(new TemplateParameterResolver(contextProvider));

        ExecOnceType exec = new ExecOnceType();
        exec.setIf("true");

        assertFalse(resolver
                .setContextInfo(new ContextInfoBuilder()
                        .setSequenceUuid(SequenceUUID.create("seq:urn"))
                        .setResourceUuid(ResourceUUID.create("res:urn", 0))
                        .build())
                .isSkip(exec));
    }
}
