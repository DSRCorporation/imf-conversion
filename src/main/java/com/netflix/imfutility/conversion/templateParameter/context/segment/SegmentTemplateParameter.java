package com.netflix.imfutility.conversion.templateParameter.context.segment;

import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.xsd.conversion.SegmentType;

/**
 * Represents a segment template parameter from conversion.xml in the following form: %{segment.paramName}.
 * Contains segment number and segment type in addition to a common template parameter.
 */
public class SegmentTemplateParameter extends TemplateParameter {

    private final int segment;
    private final SegmentType segmentType;

    public SegmentTemplateParameter(String parameterString, int segment, SegmentType segmentType) {
        super(parameterString);
        this.segment = segment;
        this.segmentType = segmentType;
    }

    public int getSegment() {
        return segment;
    }

    public SegmentType getSegmentType() {
        return segmentType;
    }
}
