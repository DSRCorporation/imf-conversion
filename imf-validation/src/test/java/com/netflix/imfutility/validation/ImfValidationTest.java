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
package com.netflix.imfutility.validation;

import com.netflix.imflibrary.IMFErrorLogger;
import com.netflix.imflibrary.utils.ErrorLogger;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * The test to check that IMF validation works.
 */
public class ImfValidationTest {

    @Test
    public void testValidationPass() throws Exception {
        List<ErrorLogger.ErrorObject> result = new ImfValidator().validate(
                getResource("imp-validate-correct"),
                getResource("imp-validate-correct/CPL_a453b63a-cf4d-454a-8c34-141f560c0100.xml"));
        assertTrue(getFatalResults(result).isEmpty());
    }

    @Test
    public void testValidationFailed() throws Exception {
        List<ErrorLogger.ErrorObject> result = new ImfValidator().validate(
                getResource("imp-validate-invalid"),
                getResource("imp-validate-invalid/CPL_a453b63a-cf4d-454a-8c34-141f560c0100.xml"));
        assertFalse(getFatalResults(result).isEmpty());
    }

    private String getResource(String path) throws URISyntaxException {
        //noinspection ConstantConditions
        return new File(ClassLoader.getSystemClassLoader().getResource(path).toURI()).getAbsolutePath();
    }

    private List<ErrorLogger.ErrorObject> getFatalResults(List<ErrorLogger.ErrorObject> results) {
        List<ErrorLogger.ErrorObject> fatalResults = new ArrayList<>();
        for (ErrorLogger.ErrorObject result : results) {
            if (result.getErrorLevel() == IMFErrorLogger.IMFErrors.ErrorLevels.FATAL) {
                fatalResults.add(result);
            }
        }
        return fatalResults;
    }

}
