package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.Constants;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterResolver;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.xsd.conversion.SegmentType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base Conversion Operation Executor.
 * <ul>
 * <li>Resolves template parameters using Template Parameter Context</li>
 * <li>Creates a new external process</li>
 * </ul>
 */
public abstract class AbstractConversionExecutor {

    private final Logger logger = LoggerFactory.getLogger(ConversionExecutorOnce.class);

    protected final TemplateParameterResolver parameterResolver;

    private static int count = 1;

    public AbstractConversionExecutor(TemplateParameterContextProvider contextProvider) {
        this.parameterResolver = new TemplateParameterResolver(contextProvider);
    }

    protected List<String> parseOperation(String conversionOperation) {
        return parseOperation(conversionOperation, TemplateParameter.DEFAULT_SEGMENT, TemplateParameter.DEFAULT_SEGMENT_TYPE);
    }

    protected List<String> parseOperation(String conversionOperation, int segment, SegmentType segmentType) {
        // split parameters
        String[] params = splitParameters(conversionOperation);

        List<String> execAndParams = new ArrayList<>();
        for (String param : params) {
            String resolvedParam = param;
            // resolve each template parameter the param contains
            Matcher m = Pattern.compile(TemplateParameter.TEMPLATE_PARAM).matcher(param);
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                String templateParam = m.group();
                String resolvedTemplateParam = parameterResolver.resolveTemplateParameter(templateParam, segment, segmentType);
                resolvedParam = resolvedParam.replace(templateParam, resolvedTemplateParam);
            }
            // add quotes if needed
            resolvedParam = addQuotes(resolvedParam);
            execAndParams.add(resolvedParam);
        }

        return execAndParams;
    }

    private String addQuotes(String param) {
        if (!param.contains(" ")) {
            return param;
        }
        if (!param.contains("=")) {
            return addQuotesIfNeeded(param);
        }
        String subParam = StringUtils.substringAfter(param, "=");
        String quotedSubParam = addQuotesIfNeeded(subParam);
        return StringUtils.substringBefore(param, "=") + "=" + quotedSubParam;
    }

    private String addQuotesIfNeeded(String param) {
        String trimmedParam = param.trim();
        if (!(trimmedParam.startsWith("\"") && trimmedParam.endsWith("\""))) {
            trimmedParam = String.format("\"%s\"", trimmedParam);
        }
        return trimmedParam;
    }

    protected ExternalProcess startProcess(List<String> execAndParams, String operationName, Class<?> operationClass) throws IOException {
        if (execAndParams.isEmpty()) {
            throw new RuntimeException(String.format("No parameters for process '%s'", operationName));
        }

        int processNum = count++;
        String operationType = operationClass.getSimpleName();
        String programPath = execAndParams.get(0);
        String programName = new File(programPath.replaceAll("\"", "")).getName();
        ExternalProcess.ExternalProcessInfo processInfo = new ExternalProcess.ExternalProcessInfo(
                processNum, operationName, operationType, programName, execAndParams);

        logger.info("Starting {}", processInfo.toString());
        logger.info("\t{}", processInfo.getProcessString());

        ProcessBuilder pb = new ProcessBuilder(execAndParams);
        pb.directory(new File(parameterResolver.getContextProvider().getWorkingDir()));

        File logFile = createLogFile(processInfo);
        if (logFile != null) {
            logger.info("\tRedirecting stderr to {}", logFile.getAbsolutePath());
            pb.redirectError(ProcessBuilder.Redirect.to(logFile));
        }

        Process process = pb.start();
        return new ExternalProcess(process, processInfo);
    }

    private File createLogFile(ExternalProcess.ExternalProcessInfo processInfo) {
        File logsDir = new File(parameterResolver.getContextProvider().getWorkingDir(), Constants.LOGS_DIR);
        String logFileName = String.format(
                Constants.LOG_TEMPLATE,
                processInfo.getProcessNum(), processInfo.getOperationName(), processInfo.getOperationType(), processInfo.getProgramName());
        File logFile = new File(logsDir, logFileName);

        String errorDesc = String.format("Couldn't create log file for %s", toString());
        try {
            boolean created = logFile.createNewFile();
            if (!created) {
                logger.warn(errorDesc);
                return null;
            }
        } catch (IOException e) {
            logger.warn(errorDesc, e);
            return null;
        }
        return logFile;
    }

    private String[] splitParameters(String conversionOperation) {
        return conversionOperation.trim().split("\\s+");
    }

}
