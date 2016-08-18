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
package com.netflix.imfutility.validate;

import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests parsing of IMF validation result.
 */
public class ImfValidatorTest {

    @Test
    public void testFailForFatalErrors() throws Exception {
        assertFalse(new TestImfValidator(
                TemplateParameterContextCreator.createDefaultContextProvider(),
                new File(ClassLoader.getSystemClassLoader().getResource("xml/errors/errors-fatal.xml").toURI()))
                .validate());
    }

    @Test
    public void testPassForNonFatalErrors() throws Exception {
        assertTrue(new TestImfValidator(
                TemplateParameterContextCreator.createDefaultContextProvider(),
                new File(ClassLoader.getSystemClassLoader().getResource("xml/errors/errors-non-fatal.xml").toURI()))
                .validate());
    }

    @Test
    public void testPassForWarnings() throws Exception {
        assertTrue(new TestImfValidator(
                TemplateParameterContextCreator.createDefaultContextProvider(),
                new File(ClassLoader.getSystemClassLoader().getResource("xml/errors/errors-warning.xml").toURI()))
                .validate());
    }

    @Test
    public void testValid() throws Exception {
        assertTrue(new TestImfValidator(
                TemplateParameterContextCreator.createDefaultContextProvider(),
                new File(ClassLoader.getSystemClassLoader().getResource("xml/errors/errors-valid.xml").toURI()))
                .validate());
    }


    private static class TestImfValidator extends ImfValidator {

        private File errorsFile;

        public TestImfValidator(TemplateParameterContextProvider contextProvider, File errorsFile) {
            super(contextProvider, null);
            this.errorsFile = errorsFile;
        }

        @Override
        void executeValidationCommand() throws IOException {
            // nothing
        }

        @Override
        File getErrorFile() {
            return errorsFile;
        }
    }

}
