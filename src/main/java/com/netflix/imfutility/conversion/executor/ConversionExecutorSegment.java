package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.conversion.templateParameter.TemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterResolver;
import com.netflix.imfutility.conversion.templateParameter.context.ITemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.segment.ISegmentTemplateParameterContext;
import com.netflix.imfutility.xsd.conversion.ExecEachSegmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created by Alexander on 4/26/2016.
 */
public class ConversionExecutorSegment extends AbstractConversionExecutor {

    final Logger logger = LoggerFactory.getLogger(ConversionExecutorSegment.class);

    public ConversionExecutorSegment(TemplateParameterResolver parameterResolver) {
        super(parameterResolver);
    }

    public void execute(ExecEachSegmentType operation) throws IOException, InterruptedException {
        ITemplateParameterContext segmContext = parameterResolver.getContextProvider().getContext(TemplateParameterContext.SEGMENT);
        if (!(segmContext instanceof ISegmentTemplateParameterContext)) {
            throw new RuntimeException(String.format("'%s' context not found!", TemplateParameterContext.SEGMENT.getName()));
        }
        int segmentNum = ((ISegmentTemplateParameterContext) segmContext).getSegmentsNum();

        for (int segment = 0; segment < segmentNum; segment++) {
            List<String> resolvedParams = resolveSegmentParameters(operation.getValue(), segment, operation.getType());
            startProcess(resolvedParams, operation.getName(), operation.getClass()).waitFor();
        }
    }

}
