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
 * A builder class for {@link ContextInfo}.
 */
public final class ContextInfoBuilder {

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
