package com.netflix.imfutility.conversion.executor.strategy;

import com.netflix.imfutility.conversion.executor.ConversionOperationParser;
import com.netflix.imfutility.conversion.executor.ExecutionException;
import com.netflix.imfutility.conversion.executor.ExternalProcess;
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

    private static int count = 1;

    public static void resetCount() {
        count = 1;
    }

    public AbstractExecuteStrategy(TemplateParameterContextProvider contextProvider, ProcessStarter processStarter) {
        this.parameterResolver = new TemplateParameterResolver(contextProvider);
        this.conversionOperationParser = new ConversionOperationParser(parameterResolver);
        this.processStarter = processStarter;
    }

    ExternalProcess startProcess(OperationInfo operationInfo) throws IOException {
        List<String> execAndParams = conversionOperationParser.parseOperation(operationInfo.getOperation(), operationInfo.getContextInfo());
        if (execAndParams.isEmpty()) {
            throw new ExecutionException(String.format("No parameters for process '%s'", operationInfo.getOperationName()));
        }

        ExternalProcess.ExternalProcessInfo processInfo = createProcessInfo(operationInfo, execAndParams);
        Process process = processStarter.startProcess(
                processInfo, execAndParams, parameterResolver.getContextProvider().getWorkingDir(), operationInfo.getOutput());
        return new ExternalProcess(process, processInfo);
    }

    private ExternalProcess.ExternalProcessInfo createProcessInfo(OperationInfo operationInfo, List<String> execAndParams) {
        int processNum = count++;
        String operationType = getClass().getSimpleName();
        String programPath = execAndParams.get(0);
        String programName = new File(programPath.replaceAll("\"", "")).getName();
        return new ExternalProcess.ExternalProcessInfo(
                processNum, operationInfo.getOperationName(), operationType, programName, execAndParams);
    }


}
