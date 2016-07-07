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
package com.netflix.imfutility.conversion.executor.strategy;

import com.netflix.imfutility.conversion.executor.ConversionOperationParser;
import com.netflix.imfutility.conversion.executor.ExecutionException;
import com.netflix.imfutility.conversion.executor.ExternalProcess;
import com.netflix.imfutility.conversion.executor.OutputRedirect;
import com.netflix.imfutility.conversion.executor.ProcessStarter;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterResolver;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * A base executor strategy. Invokes parsing of the operation and starting an external process.
 */
public class AbstractExecuteStrategy {

    private final Logger logger = LoggerFactory.getLogger(AbstractExecuteStrategy.class);

    protected final ConversionOperationParser conversionOperationParser;
    protected final TemplateParameterResolver parameterResolver;
    protected final ProcessStarter processStarter;

    private static int count = 1; // the current number of executed conversion operations.

    /**
     * Resets the current number of executed conversion operations.
     */
    public static void resetCount() {
        count = 1;
    }

    public AbstractExecuteStrategy(TemplateParameterContextProvider contextProvider, ProcessStarter processStarter) {
        this.parameterResolver = new TemplateParameterResolver(contextProvider);
        this.conversionOperationParser = new ConversionOperationParser(parameterResolver);
        this.processStarter = processStarter;
    }

    final ExternalProcess startProcess(OperationInfo operationInfo, OutputRedirect defaultOutputRedirect) throws IOException {
        List<String> execAndParams = conversionOperationParser.parseOperation(operationInfo.getOperation(), operationInfo.getContextInfo());
        if (execAndParams.isEmpty()) {
            throw new ExecutionException(String.format("No parameters for process '%s'", operationInfo.getOperationName()));
        }

        ExternalProcess.ExternalProcessInfo processInfo = createProcessInfo(operationInfo, execAndParams, defaultOutputRedirect);
        Process process = processStarter.startProcess(processInfo, execAndParams);
        return new ExternalProcess(process, processInfo);
    }

    private ExternalProcess.ExternalProcessInfo createProcessInfo(OperationInfo operationInfo, List<String> execAndParams,
                                                                  OutputRedirect defaultOutputRedirect) {
        int processNum = count++;
        String operationType = getClass().getSimpleName();
        String programPath = execAndParams.get(0);
        String programName = new File(programPath.replaceAll("\"", "")).getName();
        OutputRedirect outputRedirect = operationInfo.getOutput() != null ? OutputRedirect.FILE : defaultOutputRedirect;
        return new ExternalProcess.ExternalProcessInfo(
                processNum, operationInfo.getOperationName(), operationType, programName, execAndParams,
                parameterResolver.getContextProvider().getWorkingDir(), outputRedirect, operationInfo.getOutput());
    }


}
