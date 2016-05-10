package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.conversion.templateParameter.TemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterResolver;
import com.netflix.imfutility.conversion.templateParameter.context.segment.SegmentTemplateParameterContext;
import com.netflix.imfutility.xsd.conversion.ExecEachSegmentType;

import java.io.IOException;
import java.util.List;

/**
 * Executor of {@link ExecEachSegmentType} conversion operation.
 * <ul>
 * <li>Execute the operation for each segment (Segment Template Parameter Context is used)</li>
 * </ul>
 */
public class ConversionExecutorSegment extends AbstractConversionExecutor {

    public ConversionExecutorSegment(TemplateParameterResolver parameterResolver) {
        super(parameterResolver);
    }

    public void execute(ExecEachSegmentType operation) throws IOException {
        SegmentTemplateParameterContext segmContext = parameterResolver.getContextProvider().getSegmentContext();
        if (segmContext == null) {
            throw new RuntimeException(String.format("'%s' context not found!", TemplateParameterContext.SEGMENT.getName()));
        }
        int segmentNum = segmContext.getSegmentsNum();

        for (int segment = 0; segment < segmentNum; segment++) {
            List<String> resolvedParams = resolveSegmentParameters(operation.getValue(), segment, operation.getType());
            ExternalProcess process = startProcess(resolvedParams, operation.getName(), operation.getClass());
            process.finishWaitFor();
        }
    }

}
