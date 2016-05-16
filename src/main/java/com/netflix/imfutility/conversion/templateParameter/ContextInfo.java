package com.netflix.imfutility.conversion.templateParameter;

import com.netflix.imfutility.xsd.conversion.SequenceType;

/**
 * A current state depending on the conversion operation type (sequence, segment, etc.).
 */
public class ContextInfo {

    public static final String DEFAULT_SEGMENT_UUID = null;
    public static final String DEFAULT_SEQUENCE_UUID = null;
    public static final SequenceType DEFAULT_SEQUENCE_TYPE = null;
    public static final String DEFAULT_RESOURCE_UUID = null;

    public static ContextInfo EMPTY = new ContextInfo(
            DEFAULT_SEGMENT_UUID, DEFAULT_SEQUENCE_UUID, DEFAULT_SEQUENCE_TYPE, DEFAULT_RESOURCE_UUID);

    private final String segmentUuid;
    private final String sequenceUuid;
    private final SequenceType sequenceType;
    private final String resourceUuid;

    public ContextInfo(String segmentUuid, String sequenceUuid, SequenceType sequenceType, String resourceUuid) {
        this.segmentUuid = segmentUuid;
        this.sequenceUuid = sequenceUuid;
        this.sequenceType = sequenceType;
        this.resourceUuid = resourceUuid;
    }

    public String getSegmentUuid() {
        return segmentUuid;
    }

    public String getSequenceUuid() {
        return sequenceUuid;
    }

    public SequenceType getSequenceType() {
        return sequenceType;
    }

    public String getResourceUuid() {
        return resourceUuid;
    }
}
