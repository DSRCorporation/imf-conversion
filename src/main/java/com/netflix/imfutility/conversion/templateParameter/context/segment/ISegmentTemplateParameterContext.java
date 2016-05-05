package com.netflix.imfutility.conversion.templateParameter.context.segment;

import com.netflix.imfutility.conversion.templateParameter.context.ITemplateParameterContext;

/**
 * Segment Template Parameter Context.
 * <ul>
 * <li>It's used to replace segment template parameters in conversion operations</li>
 * </ul>
 */
public interface ISegmentTemplateParameterContext extends ITemplateParameterContext {

    /**
     * Resolves the given segment parameter.
     * The returned value is never null.
     * A runtime exception is thrown if parameter can not be resolved.
     *
     * @param templateParameter the template parameter to be resolved.
     * @return resolved parameter value as a string. Never null.
     */
    String resolveSegmentTemplateParameter(SegmentTemplateParameter templateParameter);

    /**
     * @return total number of segments the context is available for.
     */
    int getSegmentsNum();

}
