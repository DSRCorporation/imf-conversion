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
package com.netflix.imfutility.validation;

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.HelpRequestedException;
import com.netflix.imflibrary.app.PhotonIMPAnalyzer;
import com.netflix.imflibrary.utils.ErrorLogger.ErrorObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Validates the provided IMP and CPL using Photon lib.
 * Found validation errors are printed in a specified file in XML format.
 */
public class ImfValidator {

    private final Logger logger = LoggerFactory.getLogger(ImfValidator.class);

    public static void main(String... args) {
        try {
            // 1. input parameters
            ImfValidationCmdLineArgs imfValidationCmdLineArgs = CliFactory.parseArguments(ImfValidationCmdLineArgs.class, args);
            String impFolder = imfValidationCmdLineArgs.getImpFolder();
            String workingDir = imfValidationCmdLineArgs.getOutputDirectory();
            String outputFile = imfValidationCmdLineArgs.getOutputFileName();

            // 2. do validate
            List<ErrorObject> result = new ImfValidator().validate(impFolder);

            // 3. print result in xml
            new ImfErrorXmlPresenter().printErrors(result, workingDir, outputFile);

            System.exit(0);
        } catch (HelpRequestedException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    List<ErrorObject> validate(String impFolder) throws IOException {
        return PhotonIMPAnalyzer.analyzePackage(new File(impFolder)).values().stream()
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }
}
