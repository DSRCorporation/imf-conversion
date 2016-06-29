package com.netflix.imfutility.conversion.executor.strategy;

import com.netflix.imfutility.conversion.executor.*;
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

    ExternalProcess startProcess(OperationInfo operationInfo, OutputRedirect defaultOutputRedirect) throws IOException {
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
