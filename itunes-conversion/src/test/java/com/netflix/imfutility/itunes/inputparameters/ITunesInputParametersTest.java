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
package com.netflix.imfutility.itunes.inputparameters;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;
import com.netflix.imfutility.itunes.util.TestUtils;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.function.Function;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

/**
 * Tests that input parameters specified for iTunes format are parsed and validated correctly.
 */
public class ITunesInputParametersTest {

    @BeforeClass
    public static void setupAll() throws IOException {
        // create both working directory and logs folder.
        FileUtils.deleteDirectory(TemplateParameterContextCreator.getWorkingDir());
        File workingDir = TemplateParameterContextCreator.getWorkingDir();
        if (!workingDir.mkdir()) {
            throw new RuntimeException("Could not create a working dir within tmp folder");
        }
        new File(workingDir, "config.xml");
    }

    @AfterClass
    public static void teardownAll() throws IOException {
        FileUtils.deleteDirectory(TemplateParameterContextCreator.getWorkingDir());
    }

    private void testCmdLineFile(String option, String value, Function<ITunesInputParameters, File> paramFunction) {
        ITunesInputParameters inputParametersExist = createInputParameters(new String[]{option, value});
        ITunesInputParameters inputParametersNotExist = createInputParameters(new String[]{});

        assertEquals(new File(value), paramFunction.apply(inputParametersExist));
        assertNull(paramFunction.apply(inputParametersNotExist));
    }

    private ITunesInputParameters createInputParameters(String[] args) {
        return new ITunesInputParameters(CliFactory.parseArguments(ITunesCmdLineArgs.class, args), new ITunesFakeDefaultTools());
    }

    @Test
    public void testMetadataFile() throws URISyntaxException {
        testCmdLineFile("--metadata", TestUtils.getTestFile().getAbsolutePath(), parameters -> parameters.getMetadataFile());
    }

    private void validate(String[] args) {
        ITunesInputParameters inputParameters = createInputParameters(args);

        ITunesInputParametersValidator.validateCmdLineArguments(inputParameters);
    }

    @Test
    public void testValidateConvertCorrect() throws Exception {
        validate(new String[]{"--vendor-id", "abc12_"});
        validate(new String[]{"-m", "convert",
                "--vendor-id", "abc12_"});

        validate(new String[]{"-m", "convert",
                "--vendor-id", "abc12_",
                "--metadata", TestUtils.getTestFile().getAbsolutePath()});
    }

    @Test(expected = ArgumentValidationException.class)
    public void testValidateMetadataIncorrect() throws Exception {
        validate(new String[]{"-m", "convert", "--vendor-id", "abc12_", "--metadata", "someFile"});
    }

    @Test
    public void testValidateOutputCorrect() throws Exception {
        validate(new String[]{"-m", "metadata", "-o", TemplateParameterContextCreator.getWorkingDir().getAbsolutePath()});
        validate(new String[]{"-m", "audiomap", "-o", TemplateParameterContextCreator.getWorkingDir().getAbsolutePath()});
        validate(new String[]{"-m", "chapters", "-o", TemplateParameterContextCreator.getWorkingDir().getAbsolutePath()});
    }

    @Test(expected = ArgumentValidationException.class)
    public void testValidateMetadataOutputNull() throws Exception {
        validate(new String[]{"-m", "metadata"});
    }

    @Test(expected = ArgumentValidationException.class)
    public void testValidateAudiomapOutputNull() throws Exception {
        validate(new String[]{"-m", "audiomap"});
    }

    @Test(expected = ArgumentValidationException.class)
    public void testValidateChaptersOutputNull() throws Exception {
        validate(new String[]{"-m", "chapters"});
    }

    @Test
    public void testValidateVendorIdCorrect() throws Exception {
        validate(new String[]{"-m", "convert", "--vendor-id", "abc12_"});
    }

    @Test(expected = ArgumentValidationException.class)
    public void testValidateVendorIdNotExist() throws Exception {
        validate(new String[]{"-m", "convert"});
    }

    @Test(expected = ArgumentValidationException.class)
    public void testValidateVendorIdInvalidSymbol() throws Exception {
        validate(new String[]{"-m", "convert", "--vendor-id", "abc12$"});
    }

    @Test(expected = ArgumentValidationException.class)
    public void testValidateVendorIdInvalidSize() throws Exception {
        validate(new String[]{"-m", "convert", "--vendor-id", "abc12"});
    }

    @Test
    public void testValidateFallbackLocaleCorrect() throws Exception {
        validate(new String[]{"-m", "convert", "--vendor-id", "abc12_", "--fallback-locale", "fr_FR"});
    }

    @Test(expected = ArgumentValidationException.class)
    public void testValidateFallbackLocaleNotExist() throws Exception {
        validate(new String[]{"-m", "convert", "--vendor-id", "abc12_", "--fallback-locale", "fr_XX"});
    }
}
