package com.netflix.imfutility.conversion.templateParameter;

import com.netflix.imfutility.xsd.conversion.SequenceType;

/**
 * Created by Alexander on 5/11/2016.
 */
public class ContextInfo {

    public static final int DEFAULT_SEGMENT = -1;
    public static final int DEFAULT_SEQUENCE = -1;
    public static final SequenceType DEFAULT_SEQUENCE_TYPE = null;
    public static final int DEFAULT_RESOURCE = -1;

    private static ContextInfo empty = new ContextInfo(
            DEFAULT_SEGMENT, DEFAULT_SEQUENCE, DEFAULT_SEQUENCE_TYPE, DEFAULT_RESOURCE);

    private final int segment;
    private final int sequence;
    private final SequenceType sequenceType;
    private final int resource;

    public ContextInfo(int segment, int sequence, SequenceType sequenceType, int resource) {
        this.segment = segment;
        this.sequence = sequence;
        this.sequenceType = sequenceType;
        this.resource = resource;
    }

    public static ContextInfo EMPTY() {
        return empty;
    }

    public int getSequence() {
        return sequence;
    }

    public int getSegment() {
        return segment;
    }

    public SequenceType getSequenceType() {
        return sequenceType;
    }

    public int getResource() {
        return resource;
    }

}
