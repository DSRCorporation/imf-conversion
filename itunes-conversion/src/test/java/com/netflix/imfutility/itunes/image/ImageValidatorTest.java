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
package com.netflix.imfutility.itunes.image;

import com.netflix.imfutility.itunes.util.ImageUtils;
import com.netflix.imfutility.itunes.util.TestUtils;
import org.apache.commons.math3.fraction.BigFraction;
import org.junit.Test;

/**
 * Tests that image validation works correct.
 * (see {@link ImageValidator})
 */
public class ImageValidatorTest {

    @Test
    public void testCorrectValidation() throws Exception {
        ImageValidator validator;

        validator = new ImageValidator(ImageUtils.getTestImageJpgFile(), "Test");
        validator.validateSize(1920, 1080);
        validator.validateAspectRatio(new BigFraction(16).divide(9));
        validator.validateRGBColorSpace();
        validator.validateContentType("image/jpeg", "JPG");

        validator = new ImageValidator(ImageUtils.getTestImagePngFile(), "Test");
        validator.validateSize(1280, 720);
        validator.validateAspectRatio(new BigFraction(16).divide(9));
        validator.validateRGBColorSpace();
        validator.validateContentType("image/png", "PNG");
    }

    @Test(expected = ImageValidationException.class)
    public void testInvalidFile() throws Exception {
        new ImageValidator(TestUtils.getTestFile(), "Test");
    }

    @Test(expected = ImageValidationException.class)
    public void testInvalidSize() throws Exception {
        ImageValidator validator = new ImageValidator(ImageUtils.getTestImageJpgFile(), "Test");
        validator.validateSize(4096, 2160);
    }

    @Test(expected = ImageValidationException.class)
    public void testInvalidAspectRatio() throws Exception {
        ImageValidator validator = new ImageValidator(ImageUtils.getTestImageJpgFile(), "Test");
        validator.validateAspectRatio(new BigFraction(4).divide(3));
    }

    @Test(expected = ImageValidationException.class)
    public void testInvalidContentType() throws Exception {
        ImageValidator validator = new ImageValidator(ImageUtils.getTestImageJpgFile(), "Test");
        validator.validateContentType("image/png", "PNG");
    }

    @Test(expected = ImageValidationException.class)
    public void testNonRGBColorModel() throws Exception {
        ImageValidator validator = new ImageValidator(ImageUtils.getTestImageJpgCmykFile(), "Test");
        validator.validateRGBColorSpace();
    }
}
