package com.netflix.imfutility.conversion;

import com.netflix.imfutility.conversion.executor.ConvertionExecutorOnce;
import com.netflix.imfutility.conversion.executor.ConvertionExecutorPipe;
import com.netflix.imfutility.conversion.executor.ConvertionExecutorSegment;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterResolver;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.xsd.conversion.*;

import java.io.IOException;

/**
 * Created by Alexander on 4/22/2016.
 */
public class ConversionEngine {

    private ConvertionExecutorPipe pipeExecutor;
    private ConvertionExecutorOnce onceExecutor;
    private ConvertionExecutorSegment segmentExecutor;

    public void convert(FormatType formatType, String configuration, TemplateParameterContextProvider contextProvider) throws IOException, InterruptedException {
        // 1. get configuration
        FormatConfigurationType formatConfigurationType = formatType.getFormatConfigurations().getMap().get(configuration);
        if (formatConfigurationType == null) {
            throw new RuntimeException(String.format("No configuration '%s' found for format '%s'.", configuration, formatType.getName()));
        }

        // 3. init template parameter resolver
        TemplateParameterResolver parameterResolver = new TemplateParameterResolver(contextProvider);

        // 4. init executors
        pipeExecutor = new ConvertionExecutorPipe(parameterResolver);
        onceExecutor = new ConvertionExecutorOnce(parameterResolver);
        segmentExecutor = new ConvertionExecutorSegment(parameterResolver);

        // 5. run configuration
        run(formatConfigurationType);
    }

    private void run(FormatConfigurationType formatConfigurationType) throws IOException, InterruptedException {
        for (Object operation : formatConfigurationType.getPipeOrExecOnceOrExecEachSegment()) {
            if (operation instanceof ExecOnceType) {
                execOnce((ExecOnceType) operation);
            } else if (operation instanceof PipeType) {
                execPipe((PipeType) operation);
            } else if (operation instanceof ExecEachSegmentType) {
                execSegment((ExecEachSegmentType) operation);
            } else {
                throw new RuntimeException(String.format("Unknown Conversion Operation type: %s", operation.toString()));
            }
        }
    }


    private void execOnce(ExecOnceType operation) throws IOException, InterruptedException {
        onceExecutor.execute(operation);
    }

    private void execPipe(PipeType operation) throws IOException, InterruptedException {
        pipeExecutor.execute(operation);
    }

    private void execSegment(ExecEachSegmentType operation) throws IOException, InterruptedException {
        segmentExecutor.execute(operation);
    }

}
