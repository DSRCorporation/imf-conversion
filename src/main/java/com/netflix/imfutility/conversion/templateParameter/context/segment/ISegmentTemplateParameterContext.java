package com.netflix.imfutility.conversion.templateParameter.context.segment;

import com.netflix.imfutility.conversion.templateParameter.SegmentTemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.context.ITemplateParameterContext;

/**
 * Segment Template Parameter Context.
 * <ul>
 * <li>It's used to replace segment template parameters in conversion operations</li>
 * </ul>
 */
public interface ISegmentTemplateParameterContext extends ITemplateParameterContext {

    String resolveSegmentTemplateParameter(SegmentTemplateParameter templateParameter);

    int getSegmentsNum();

}
