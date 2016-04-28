package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterResolver;
import com.netflix.imfutility.conversion.templateParameter.context.ITemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.segment.ISegmentTemplateParameterContext;
import com.netflix.imfutility.xsd.conversion.ExecEachSegmentType;
import com.netflix.imfutility.xsd.conversion.ExecOnceType;
import com.netflix.imfutility.xsd.conversion.SegmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.Query;
import javax.management.RuntimeErrorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander on 4/26/2016.
 */
public class ConvertionExecutorSegment extends AbstractConvertionExecutor {

    final Logger logger = LoggerFactory.getLogger(ConvertionExecutorSegment.class);

    public ConvertionExecutorSegment(TemplateParameterResolver parameterResolver) {
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
            startProcess(resolvedParams).waitFor();
        }
    }

}
