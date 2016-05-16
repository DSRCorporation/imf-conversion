package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.xsd.conversion.SequenceType;

/**
 * Unique identifier ofa resource within a sequence and a segment.
 */
public class ResourceKey {

    private final SegmentUUID segmentUuid;
    private final SequenceUUID sequenceUuid;
    private final SequenceType sequenceType;

    public static ResourceKey create(SegmentUUID segmentUuid, SequenceUUID sequenceUuid, SequenceType sequenceType) {
        return new ResourceKey(segmentUuid, sequenceUuid, sequenceType);
    }

    private ResourceKey(SegmentUUID segmentUuid, SequenceUUID sequenceUuid, SequenceType sequenceType) {
        this.segmentUuid = segmentUuid;
        this.sequenceUuid = sequenceUuid;
        this.sequenceType = sequenceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceKey that = (ResourceKey) o;

        if (segmentUuid != null ? !segmentUuid.equals(that.segmentUuid) : that.segmentUuid != null) return false;
        if (sequenceUuid != null ? !sequenceUuid.equals(that.sequenceUuid) : that.sequenceUuid != null) return false;
        return sequenceType == that.sequenceType;

    }

    @Override
    public int hashCode() {
        int result = segmentUuid != null ? segmentUuid.hashCode() : 0;
        result = 31 * result + (sequenceUuid != null ? sequenceUuid.hashCode() : 0);
        result = 31 * result + (sequenceType != null ? sequenceType.hashCode() : 0);
        return result;
    }

    public SegmentUUID getSegmentUuid() {
        return segmentUuid;
    }

    public SequenceUUID getSequenceUuid() {
        return sequenceUuid;
    }

    public SequenceType getSequenceType() {
        return sequenceType;
    }

    @Override
    public String toString() {
        return segmentUuid.toString() + "_" + sequenceUuid.toString() + "_" + sequenceType.value();
    }
}
