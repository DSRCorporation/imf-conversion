package com.netflix.imfutility.conversion.templateParameter;

import com.netflix.imfutility.cpl.uuid.ResourceUUID;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.SequenceType;

/**
 * A builder class for {@link ContextInfo}.
 */
public class ContextInfoBuilder {

    private SegmentUUID segmentUuid = ContextInfo.DEFAULT_SEGMENT_UUID;
    private SequenceUUID sequenceUuid = ContextInfo.DEFAULT_SEQUENCE_UUID;
    private SequenceType sequenceType = ContextInfo.DEFAULT_SEQUENCE_TYPE;
    private ResourceUUID resourceUuid = ContextInfo.DEFAULT_RESOURCE_UUID;

    public ContextInfoBuilder setSegmentUuid(SegmentUUID segmentUuid) {
        this.segmentUuid = segmentUuid;
        return this;
    }

    public ContextInfoBuilder setSequenceUuid(SequenceUUID sequenceUuid) {
        this.sequenceUuid = sequenceUuid;
        return this;
    }

    public ContextInfoBuilder setSequenceType(SequenceType sequenceType) {
        this.sequenceType = sequenceType;
        return this;
    }

    public ContextInfoBuilder setResourceUuid(ResourceUUID resourceUuid) {
        this.resourceUuid = resourceUuid;
        return this;
    }

    public ContextInfo build() {
        return new ContextInfo(segmentUuid, sequenceUuid, sequenceType, resourceUuid);
    }
}
