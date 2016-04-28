package com.netflix.imfutility.conversion.templateParameter.context.segment;

import com.netflix.imfutility.conversion.templateParameter.SegmentTemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.context.ITemplateParameterContext;

/**
 * Created by Alexander on 4/27/2016.
 */
public interface ISegmentTemplateParameterContext extends ITemplateParameterContext {

    String resolveSegmentTemplateParameter(SegmentTemplateParameter templateParameter);

    int getSegmentsNum();

}
