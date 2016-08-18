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

import com.netflix.imfutility.CommonConstants;
import com.netflix.imfutility.conversion.executor.strategy.ExecuteStrategyFactory;
import com.netflix.imfutility.conversion.executor.strategy.OperationInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.DynamicContextParameters;
import com.netflix.imfutility.generated.conversion.ImfValidationType;
import com.netflix.imfutility.generated.validation.ErrorType;
import com.netflix.imfutility.generated.validation.Errors;
import com.netflix.imfutility.util.ImfLogger;
import com.netflix.imfutility.util.LogHelper;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Performs validation of the input IMF package (IMP) and CPL.
 * <ul>
 * <li>Validation is performed by a separate external tool.</li>
 * <li>The validation external tool command is specified in conversion.xml</li>
 * <li>By default, validation is done by a wrapper on Netflix Photon library.</li>
 * <li>It's possible to set a custom validation tool using config.xml.</li>
 * <li>The validation tool expect IMP, CPL, working dir and output XML file name parameters.</li>
 * <li>The result of the validation is stored in the specified output XML file.</li>
 * <li>The current class executes the validation commands and parses the output XML.</li>
 * <li>All errors are logged.</li>
 * <li>If there are fatal errors - conversion will be aborted.</li>
 * <li>Conversion is allowed for non-fatal errors and warnings.</li>
 * </ul>
 */
public class ImfValidator {

    private final Logger logger = new ImfLogger(new ImfLogger(LoggerFactory.getLogger(ImfValidator.class)));

    private final TemplateParameterContextProvider contextProvider;
    private final ExecuteStrategyFactory executeStrategyFactory;

    public ImfValidator(TemplateParameterContextProvider contextProvider, ExecuteStrategyFactory executeStrategyFactory) {
        this.contextProvider = contextProvider;
        this.executeStrategyFactory = executeStrategyFactory;
    }

    /**
     * Performs validation of the given IMP and CPL. All errors and warning are logged.
     *
     * @return true if validation passed anf false otherwise.
     *
     * @throws IOException, XmlParsingException
     */
    public boolean validate() throws IOException, XmlParsingException {
        executeValidationCommand();
        return analyzeResult();
    }

    void executeValidationCommand() throws IOException {
        ImfValidationType imfValidationCommand = contextProvider.getConversionProvider().getConversion().getImfValidation();
        OperationInfo operationInfo = new OperationInfo(
                imfValidationCommand.getValue(), imfValidationCommand.getClass().getSimpleName(), ContextInfo.EMPTY, false);
        executeStrategyFactory.createExecuteOnceStrategy(contextProvider).execute(operationInfo);
    }

    File getErrorFile() {
        String errorFileName = contextProvider.getDynamicContext().getParameterValueAsString(
                DynamicContextParameters.OUTPUT_VALIDATION_FILE);
        return new File(contextProvider.getWorkingDir(), errorFileName);
    }

    private boolean analyzeResult() throws IOException, XmlParsingException {

        Errors errors = XmlParser.parse(
                getErrorFile(),
                new String[]{CommonConstants.ERRORS_XML_SCHEME}, CommonConstants.ERRORS_PACKAGE,
                Errors.class);

        if (errors.getError() == null || errors.getError().isEmpty()) {
            // OK! no errors
            return true;
        }

        boolean fatalErrors = false;
        logger.warn("{}IMF Validation result:", LogHelper.TAB);
        for (ErrorType error : errors.getError()) {
            switch (error.getLevel()) {
                case FATAL:
                    fatalErrors = true;
                    logger.error("{}{}{}", LogHelper.TAB, LogHelper.TAB, error.getValue());
                    break;
                case NON_FATAL:
                    logger.warn("{}{}{}", LogHelper.TAB, LogHelper.TAB, error.getValue());
                    break;
                case WARNING:
                    logger.warn("{}{}{}", LogHelper.TAB, LogHelper.TAB, error.getValue());
                    break;
                default:
                    //nothing
            }
        }

        return !fatalErrors;
    }

}
