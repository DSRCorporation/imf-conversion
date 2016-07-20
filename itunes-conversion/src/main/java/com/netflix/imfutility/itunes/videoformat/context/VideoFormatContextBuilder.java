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
package com.netflix.imfutility.itunes.videoformat.context;

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
import com.netflix.imfutility.itunes.videoformat.ScanType;
import com.netflix.imfutility.itunes.videoformat.VideoFormat;
import com.netflix.imfutility.itunes.videoformat.builder.VideoFormatBuilder;
import com.netflix.imfutility.util.ConversionHelper;
import org.apache.commons.math3.fraction.BigFraction;

import java.math.BigInteger;
import java.util.Collection;

import static com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters.DURATION_EDIT_UNIT;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters.EDIT_RATE;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters.FRAME_RATE;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters.HEIGHT;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters.WIDTH;


/**
 * Build video format depends on input video characteristics.
 * width, height extracted from {@link SequenceTemplateParameterContext}
 * duration extracted from {@link ResourceTemplateParameterContext}
 */
public class VideoFormatContextBuilder {
    private final TemplateParameterContextProvider contextProvider;
    private final VideoFormatBuilder videoFormatBuilder;

    public VideoFormatContextBuilder(TemplateParameterContextProvider contextProvider, VideoFormatBuilder videoFormatBuilder) {
        this.contextProvider = contextProvider;
        this.videoFormatBuilder = videoFormatBuilder;
    }

    private String getSequenceParameterValue(SequenceContextParameters parameter) {
        SequenceTemplateParameterContext sequenceContext = contextProvider.getSequenceContext();
        return sequenceContext.getParameterValue(parameter, new ContextInfoBuilder()
                .setSequenceType(SequenceType.VIDEO)
                .setSequenceUuid(getSequenceUUID())
                .build());
    }

    public VideoFormat build() {
        int width = Integer.parseInt(getSequenceParameterValue(WIDTH));
        int height = Integer.parseInt(getSequenceParameterValue(HEIGHT));
        BigFraction fps = ConversionHelper.parseEditRate(getSequenceParameterValue(FRAME_RATE));
        Long duration = getSequenceDuration();

        return videoFormatBuilder
                .setFrameWidth(width)
                .setFrameHeight(height)
                .setFps(fps.doubleValue())
                //assume video scan type is progressive (according to IMF application #2E)
                .setScanType(ScanType.PROGRESSIVE)
                .setDuration(duration)
                .build();
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

        //  to get time in millis
        return ConversionHelper.toMilliSeconds(durationEU, editRate);
    }

}
