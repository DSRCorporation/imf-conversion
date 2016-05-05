package com.netflix.imfutility.conversion;

import com.netflix.imfutility.conversion.executor.ConversionExecutorOnce;
import com.netflix.imfutility.conversion.executor.ConversionExecutorPipe;
import com.netflix.imfutility.conversion.executor.ConversionExecutorSegment;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.xsd.conversion.*;

import java.io.IOException;

/**
 * Performs conversion to a destination format as specified in conversion.xml
 * <ul>
 * <li>The context must be already prepared and provided to the engine</li>
 * <li>Each conversion operation from conversion.xml is executed using an appropriate executor depending on the operation type.</li>
 * </ul>
 */
public class ConversionEngine {

    private ConversionExecutorPipe pipeExecutor;
    private ConversionExecutorOnce onceExecutor;
    private ConversionExecutorSegment segmentExecutor;

    public void convert(FormatType formatType, String configuration, TemplateParameterContextProvider contextProvider) throws IOException {
        // 1. get configuration
        FormatConfigurationType formatConfigurationType = formatType.getFormatConfigurations().getMap().get(configuration);
        if (formatConfigurationType == null) {
            throw new RuntimeException(String.format("No configuration '%s' found for format '%s'.", configuration, formatType.getName()));
        }

        // 2. init executors
        pipeExecutor = new ConversionExecutorPipe(contextProvider);
        onceExecutor = new ConversionExecutorOnce(contextProvider);
        segmentExecutor = new ConversionExecutorSegment(contextProvider);

        // 3. run configuration
        run(formatConfigurationType);
    }

    private void run(FormatConfigurationType formatConfigurationType) throws IOException {
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


    private void execOnce(ExecOnceType operation) throws IOException {
        onceExecutor.execute(operation);
    }

    private void execPipe(PipeType operation) throws IOException {
        pipeExecutor.execute(operation);
    }

    private void execSegment(ExecEachSegmentType operation) throws IOException {
        segmentExecutor.execute(operation);
    }

}
