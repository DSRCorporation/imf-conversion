package com.netflix.imfutility.conversion.templateParameter;

import com.netflix.imfutility.xsd.conversion.SequenceType;

/**
 * Created by Alexander on 5/12/2016.
 */
public class ContextInfoBuilder {


    private int segment = ContextInfo.DEFAULT_SEGMENT;
    private int sequence = ContextInfo.DEFAULT_SEQUENCE;
    private SequenceType sequenceType = ContextInfo.DEFAULT_SEQUENCE_TYPE;
    private int resource = ContextInfo.DEFAULT_RESOURCE;

    public ContextInfoBuilder setSegment(int segment) {
        this.segment = segment;
        return this;
    }

    public ContextInfoBuilder setSequence(int sequence) {
        this.sequence = sequence;
        return this;
    }

    public ContextInfoBuilder setSequenceType(SequenceType sequenceType) {
        this.sequenceType = sequenceType;
        return this;
    }

    public ContextInfoBuilder setResource(int resource) {
        this.resource = resource;
        return this;
    }

    public ContextInfo build() {
        return new ContextInfo(segment, sequence, sequenceType, resource);
    }
}
