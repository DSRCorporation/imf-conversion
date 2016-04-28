package com.netflix.imfutility.conversion.templateParameter;

import com.netflix.imfutility.xsd.conversion.SegmentType;

/**
 * Created by Alexander on 4/27/2016.
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
