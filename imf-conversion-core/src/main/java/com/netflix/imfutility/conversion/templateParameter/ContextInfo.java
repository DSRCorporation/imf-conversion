/**
 * Copyright (C) 2016 Netflix, Inc.
 *
 *     This file is part of IMF Conversion Utility.
 *
 *     IMF Conversion Utility is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     IMF Conversion Utility is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with IMF Conversion Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.netflix.imfutility.conversion.templateParameter;

import com.netflix.imfutility.cpl.uuid.ResourceUUID;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.SequenceType;

/**
 * A current state that helps to resolve a parameter by {@link com.netflix.imfutility.conversion.templateParameter.context.ITemplateParameterContext}.
 * All fields are optional there.  It depends on the conversion operation type (sequence, segment, etc.) what fields must be filled to be able to resolve a template parameter.
 */
public class ContextInfo {

    public static final SegmentUUID DEFAULT_SEGMENT_UUID = null;
    public static final SequenceUUID DEFAULT_SEQUENCE_UUID = null;
    public static final SequenceType DEFAULT_SEQUENCE_TYPE = null;
    public static final ResourceUUID DEFAULT_RESOURCE_UUID = null;

    public static final ContextInfo EMPTY = new ContextInfo(
            DEFAULT_SEGMENT_UUID, DEFAULT_SEQUENCE_UUID, DEFAULT_SEQUENCE_TYPE, DEFAULT_RESOURCE_UUID);

    private final SegmentUUID segmentUuid;
    private final SequenceUUID sequenceUuid;
    private final SequenceType sequenceType;
    private final ResourceUUID resourceUuid;

    public ContextInfo(SegmentUUID segmentUuid, SequenceUUID sequenceUuid, SequenceType sequenceType, ResourceUUID resourceUuid) {
        this.segmentUuid = segmentUuid;
        this.sequenceUuid = sequenceUuid;
        this.sequenceType = sequenceType;
        this.resourceUuid = resourceUuid;
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

    public ResourceUUID getResourceUuid() {
        return resourceUuid;
    }
}
