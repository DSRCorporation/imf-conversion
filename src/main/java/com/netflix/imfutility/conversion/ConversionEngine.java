package com.netflix.imfutility.conversion;

import com.netflix.imfutility.conversion.executor.ConversionExecutorOnce;
import com.netflix.imfutility.conversion.executor.ConversionExecutorSegment;
import com.netflix.imfutility.conversion.executor.ConversionExecutorSequence;
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

    private ConversionExecutorOnce onceExecutor;
    private ConversionExecutorSegment segmentExecutor;
    private ConversionExecutorSequence sequenceExecutor;

    public void convert(FormatType formatType, String configuration, TemplateParameterContextProvider contextProvider) throws IOException {
        // 1. get configuration
        FormatConfigurationType formatConfigurationType = formatType.getFormatConfigurations().getMap().get(configuration);
        if (formatConfigurationType == null) {
            throw new RuntimeException(String.format("No configuration '%s' found for format '%s'.", configuration, formatType.getName()));
        }

        for (Object operation : formatConfigurationType.getExecOnceOrExecEachSegmentOrExecEachSequence()) {
            if (operation instanceof ExecOnceType) {
                new ConversionExecutorOnce(contextProvider, (ExecOnceType) operation).execute();
            } else if (operation instanceof ExecEachSegmentSequenceType) {
                new ConversionExecutorSegment(contextProvider, (ExecEachSegmentSequenceType) operation).execute();
            } else if (operation instanceof ExecEachSegmentType) {
                new ConversionExecutorSequence(contextProvider, (ExecEachSequenceSegmentType) operation).execute();
            } else {
                throw new RuntimeException(String.format("Unknown Conversion Operation type: %s", operation.toString()));
            }
        }
    }


}
