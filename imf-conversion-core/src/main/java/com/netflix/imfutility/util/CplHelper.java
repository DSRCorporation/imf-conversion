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
package com.netflix.imfutility.util;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfoBuilder;
import com.netflix.imfutility.conversion.templateParameter.context.ResourceKey;
import com.netflix.imfutility.conversion.templateParameter.context.ResourceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.SegmentTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters;
import com.netflix.imfutility.cpl.uuid.ResourceUUID;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.SequenceType;
import org.apache.commons.math3.fraction.BigFraction;

import java.math.BigInteger;

/**
 * Helper class to calculate values based on CPL (that is sequence, segment and resource contexts).
 */
public final class CplHelper {

    /**
     * Gets a total duration of the given virtual track in milliseconds.
     *
     * @param contextProvider a context provider
     * @param seqType a sequence (virtual track) type
     * @param seqUuid a sequence (virtual track) UUID
     * @return a total duration of a virtual track in milliseconds
     */
    public static long getVirtualTrackDurationMS(TemplateParameterContextProvider contextProvider,
                                                 SequenceType seqType, SequenceUUID seqUuid) {
        SegmentTemplateParameterContext segmentContext = contextProvider.getSegmentContext();
        ResourceTemplateParameterContext resourceContext = contextProvider.getResourceContext();

        long sequenceDuration = 0L;
        for (SegmentUUID segmUuid : segmentContext.getUuids()) {
            for (ResourceUUID resUuid : resourceContext.getUuids(ResourceKey.create(segmUuid, seqUuid, seqType))) {
                ContextInfo contextInfo = new ContextInfoBuilder()
                        .setResourceUuid(resUuid)
                        .setSegmentUuid(segmUuid)
                        .setSequenceUuid(seqUuid)
                        .setSequenceType(seqType)
                        .build();
                sequenceDuration += getResourceDurationMS(contextProvider, contextInfo);
            }
        }

        return sequenceDuration;
    }

    /**
     * Gets a total duration of the given virtual track in edit units (frames or samples).
     *
     * @param contextProvider a context provider
     * @param seqType a sequence (virtual track) type
     * @param seqUuid a sequence (virtual track) UUID
     * @return a total duration of a virtual track in edit units (frames or samples).
     */
    public static BigInteger getVirtualTrackDurationEU(TemplateParameterContextProvider contextProvider,
                                                       SequenceType seqType, SequenceUUID seqUuid) {
        SegmentTemplateParameterContext segmentContext = contextProvider.getSegmentContext();
        ResourceTemplateParameterContext resourceContext = contextProvider.getResourceContext();

        BigInteger sequenceDuration = BigInteger.ZERO;
        for (SegmentUUID segmUuid : segmentContext.getUuids()) {
            for (ResourceUUID resUuid : resourceContext.getUuids(ResourceKey.create(segmUuid, seqUuid, seqType))) {
                ContextInfo contextInfo = new ContextInfoBuilder()
                        .setResourceUuid(resUuid)
                        .setSegmentUuid(segmUuid)
                        .setSequenceUuid(seqUuid)
                        .setSequenceType(seqType)
                        .build();
                sequenceDuration = sequenceDuration.add(getResourceDurationEU(contextProvider, contextInfo));
            }
        }

        return sequenceDuration;
    }

    /**
     * Gets a duration of the given resource within a segment and sequence in milliseconds.
     *
     * @param contextProvider a context provider
     * @param contextInfo a context info defining a resource (all fields must be set).
     * @return a duration of the given resource within a segment and sequence in milliseconds.
     */
    public static long getResourceDurationMS(TemplateParameterContextProvider contextProvider, ContextInfo contextInfo) {
        BigInteger durationEU = getResourceDurationEU(contextProvider, contextInfo);
        BigFraction editRate = ConversionHelper.parseEditRate(contextProvider.getResourceContext()
                .getParameterValue(ResourceContextParameters.EDIT_RATE, contextInfo));
        return ConversionHelper.editUnitToMilliSeconds(durationEU, editRate);
    }

    /**
     * Gets a duration of the given resource within a segment and sequence in edit units (frames or samples).
     *
     * @param contextProvider a context provider
     * @param contextInfo a context info defining a resource (all fields must be set).
     * @return a duration of the given resource within a segment and sequence in edit units (frames or samples)
     */
    public static BigInteger getResourceDurationEU(TemplateParameterContextProvider contextProvider, ContextInfo contextInfo) {
        return new BigInteger(contextProvider.getResourceContext()
                .getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
    }

    private CplHelper() {
    }
}
