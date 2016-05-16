package com.netflix.imfutility.conversion.templateParameter;

import com.netflix.imfutility.xsd.conversion.SequenceType;

/**
 * A builder class for {@link ContextInfo}.
 */
public class ContextInfoBuilder {


    private String segmentUuid = ContextInfo.DEFAULT_SEGMENT_UUID;
    private String sequenceUuid = ContextInfo.DEFAULT_SEQUENCE_UUID;
    private SequenceType sequenceType = ContextInfo.DEFAULT_SEQUENCE_TYPE;
    private String resourceUuid = ContextInfo.DEFAULT_RESOURCE_UUID;

    public ContextInfoBuilder setSegmentUuid(String segmentUuid) {
        this.segmentUuid = segmentUuid;
        return this;
    }

    public ContextInfoBuilder setSequenceUuid(String sequenceUuid) {
        this.sequenceUuid = sequenceUuid;
        return this;
    }

    public ContextInfoBuilder setSequenceType(SequenceType sequenceType) {
        this.sequenceType = sequenceType;
        return this;
    }

    public ContextInfoBuilder setResourceUuid(String resourceUuid) {
        this.resourceUuid = resourceUuid;
        return this;
    }

    public ContextInfo build() {
        return new ContextInfo(segmentUuid, sequenceUuid, sequenceType, resourceUuid);
    }
}
