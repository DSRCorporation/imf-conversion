/*
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
package com.netflix.imfutility.itunes.destcontext;

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfoBuilder;
import com.netflix.imfutility.conversion.templateParameter.context.ResourceKey;
import com.netflix.imfutility.conversion.templateParameter.context.ResourceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.SequenceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters;
import com.netflix.imfutility.cpl.uuid.ResourceUUID;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.util.ConversionHelper;
import com.netflix.imfutility.xsd.conversion.DestContextTypeMap;
import com.netflix.imfutility.xsd.conversion.DestContextsTypeMap;
import org.apache.commons.math3.fraction.BigFraction;

import java.math.BigInteger;
import java.util.Collection;

import static com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters.DURATION_EDIT_UNIT;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters.EDIT_RATE;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters.FRAME_RATE;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters.HEIGHT;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters.WIDTH;


/**
 * Resolve dest context by input video parameters defined in contexts.
 * Width, height, fps extracted from {@link SequenceTemplateParameterContext}.
 * Duration extracted from {@link ResourceTemplateParameterContext}.
 */
public class InputDestContextResolveStrategy implements DestContextResolveStrategy {
    private final TemplateParameterContextProvider contextProvider;
    private final VideoDestContextResolveStrategy resolveStrategy;

    public InputDestContextResolveStrategy(TemplateParameterContextProvider contextProvider) {
        this(contextProvider, new VideoDestContextResolveStrategy());
    }

    public InputDestContextResolveStrategy(TemplateParameterContextProvider contextProvider,
                                           VideoDestContextResolveStrategy resolveStrategy) {
        this.contextProvider = contextProvider;
        this.resolveStrategy = resolveStrategy;
    }

    @Override
    public DestContextTypeMap resolveContext(
            DestContextsTypeMap destContexts)
            throws ConversionException {
        Integer width = Integer.parseInt(getSequenceParameterValue(WIDTH));
        Integer height = Integer.parseInt(getSequenceParameterValue(HEIGHT));
        BigFraction frameRate = ConversionHelper.safeParseEditRate(getSequenceParameterValue(FRAME_RATE));
        Long duration = getSequenceDuration();

        return resolveStrategy
                .setWidth(width)
                .setHeight(height)
                .setFrameRate(frameRate)
                // assume video scan type is progressive (according to IMF application #2E)
                .setInterlaced(false)
                .setDuration(duration)
                .resolveContext(destContexts);
    }

    private String getSequenceParameterValue(SequenceContextParameters parameter) {
        SequenceTemplateParameterContext sequenceContext = contextProvider.getSequenceContext();
        return sequenceContext.getParameterValue(parameter, new ContextInfoBuilder()
                .setSequenceType(SequenceType.VIDEO)
                .setSequenceUuid(getSequenceUUID())
                .build());
    }

    private SequenceUUID getSequenceUUID() {
        Collection<SequenceUUID> uuids = contextProvider.getSequenceContext().getUuids(SequenceType.VIDEO);
        if (!uuids.isEmpty()) {
            return uuids.iterator().next();
        }
        return null;
    }

    private Long getSequenceDuration() {
        Long sequenceDuration = 0L;
        for (SegmentUUID segmUuid : contextProvider.getSegmentContext().getUuids()) {
            for (ResourceUUID resUuid : contextProvider.getResourceContext()
                    .getUuids(ResourceKey.create(segmUuid, getSequenceUUID(), SequenceType.VIDEO))) {
                ContextInfo contextInfo = new ContextInfoBuilder()
                        .setResourceUuid(resUuid)
                        .setSegmentUuid(segmUuid)
                        .setSequenceUuid(getSequenceUUID())
                        .setSequenceType(SequenceType.VIDEO)
                        .build();

                sequenceDuration += getResourceDuration(contextInfo);
            }
        }
        return sequenceDuration;
    }

    private Long getResourceDuration(ContextInfo contextInfo) {
        ResourceTemplateParameterContext resourceContext = contextProvider.getResourceContext();

        BigFraction editRate = ConversionHelper.parseEditRate(resourceContext
                .getParameterValue(EDIT_RATE, contextInfo));
        BigInteger durationEU = new BigInteger(resourceContext
                .getParameterValue(DURATION_EDIT_UNIT, contextInfo));

        //  to get time in seconds
        return ConversionHelper.toSeconds(durationEU, editRate);
    }

}
