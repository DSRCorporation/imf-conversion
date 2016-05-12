package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.xsd.conversion.SequenceType;

/**
 * Created by Alexander on 5/12/2016.
 */
public class ResourceKey {

    private final int segmentNum;
    private final int sequenceNum;
    private final SequenceType sequenceType;

    public ResourceKey(int segmentNum, int sequenceNum, SequenceType sequenceType) {
        this.segmentNum = segmentNum;
        this.sequenceNum = sequenceNum;
        this.sequenceType = sequenceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceKey that = (ResourceKey) o;

        if (segmentNum != that.segmentNum) return false;
        if (sequenceNum != that.sequenceNum) return false;
        return sequenceType == that.sequenceType;

    }

    @Override
    public int hashCode() {
        int result = segmentNum;
        result = 31 * result + sequenceNum;
        result = 31 * result + sequenceType.hashCode();
        return result;
    }
}
