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
package com.netflix.imfutility.cpl;

import com.netflix.imfutility.asset.AssetMap;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfoBuilder;
import com.netflix.imfutility.conversion.templateParameter.context.DestTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.ResourceKey;
import com.netflix.imfutility.conversion.templateParameter.context.ResourceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.SequenceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters;
import com.netflix.imfutility.cpl.uuid.ResourceUUID;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.util.ConversionHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.fraction.BigFraction;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * The base implementation of CPL context builder strategy common for all namespaces.
 * <ul>
 * <li>A concrete strategy must implement {@link #buildFromCpl()} method to
 * init sequence, segment and resource contexts. The method must also fill all resource parameters based on CPL
 * (such as EditUnit-based parameters and Repeat).</li>
 * <li>A concrete strategy must also implement {@link #getCompositionTimecodeRate()} and {@link #getCompositionTimecodeStart()}
 * methods to get information about composition timecode as defined in CPL.</li>
 * <li>The abstract strategy contains logic (common for all namespaces) to calculate other Resource parameters
 * (such as millisecond-based parameters and timecode-based parameters) using edit-unit-based parameters filled
 * by a concrete strategy.</li>
 * <li>The abstract strategy contains common logic to calculate Offset and edit-unit-frame parameters.
 * Start timecode is taken into account when calculating offset parameter (the start code specified in conversion.xml's
 * Destination context has higher priority than the start timecode from CPL's composition).</li>
 * </ul>
 */
public abstract class AbstractCplContextBuilderStrategy implements ICplContextBuilderStrategy {

    protected final TemplateParameterContextProvider contextProvider;
    protected final AssetMap assetMap;

    public AbstractCplContextBuilderStrategy(TemplateParameterContextProvider contextProvider, AssetMap assetMap) {
        this.contextProvider = contextProvider;
        this.assetMap = assetMap;
    }

    @Override
    public final void build() {
        // 1. Init sequence, segment and resource contexts. Fill all resource parameters based on CPL
        // (such as EditUnit-based parameters and Repeat)
        buildFromCpl();

        // 2. calculate other Resource parameters
        calculateMsAndTcParameters();

        // 3. re-check DURATION_FRAME_EDIT_UNIT and START_TIME_FRAME_EDIT_UNIT for
        // audio sequences which has essences containing both audio and video
        // (the values must be calculated in video frames in this case)
        buildTimeAndDurationInFrames();

        // 4. define languages for all sequences
        buildSequenceLanguages();
    }

    @Override
    public void buildPostDest() {
        calculateOffsetMs();
    }

    /**
     * Init sequence, segment and resource contexts. Fill all resource parameters based on CPL
     * (such as EditUnit-based parameters and Repeat)
     */
    protected abstract void buildFromCpl();

    /**
     * <ul>
     * <li>Calculate other Resource parameters (such as millisecond-based parameters and timecode-based parameters)
     * using edit-unit-based parameters filled by a concrete strategy.</li>
     * </ul>
     */
    private void calculateMsAndTcParameters() {
        ResourceTemplateParameterContext resourceContext = contextProvider.getResourceContext();
        for (SequenceType seqType : contextProvider.getSequenceContext().getSequenceTypes()) {
            for (SequenceUUID seqUuid : contextProvider.getSequenceContext().getUuids(seqType)) {
                for (SegmentUUID segmUuid : contextProvider.getSegmentContext().getUuids()) {
                    ResourceKey resourceKey = ResourceKey.create(segmUuid, seqUuid, seqType);
                    for (ResourceUUID resUuid : resourceContext.getUuids(resourceKey)) {
                        ContextInfo contextInfo = new ContextInfoBuilder()
                                .setResourceUuid(resUuid)
                                .setSegmentUuid(segmUuid)
                                .setSequenceUuid(seqUuid)
                                .setSequenceType(seqType).build();

                        // 1. already filled from CPL: startTime/endTime/duration in EU and edit rate
                        BigFraction editRate = ConversionHelper.parseEditRate(contextProvider.getResourceContext()
                                .getParameterValue(ResourceContextParameters.EDIT_RATE, contextInfo));
                        BigInteger durationEU = new BigInteger(contextProvider.getResourceContext()
                                .getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
                        BigInteger startTimeEU = new BigInteger(contextProvider.getResourceContext()
                                .getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
                        BigInteger endTimeEU = new BigInteger(contextProvider.getResourceContext()
                                .getParameterValue(ResourceContextParameters.END_TIME_EDIT_UNIT, contextInfo));

                        // 2. startTime, endTime and duration in MS
                        long durationMs = ConversionHelper.editUnitToMilliSeconds(durationEU, editRate);
                        long startTimeMs = ConversionHelper.editUnitToMilliSeconds(startTimeEU, editRate);
                        long endTimeMs = ConversionHelper.editUnitToMilliSeconds(endTimeEU, editRate);
                        resourceContext.addResourceParameter(resourceKey, resUuid,
                                ResourceContextParameters.DURATION_MS, String.valueOf(durationMs));
                        resourceContext.addResourceParameter(resourceKey, resUuid,
                                ResourceContextParameters.START_TIME_MS, String.valueOf(startTimeMs));
                        resourceContext.addResourceParameter(resourceKey, resUuid,
                                ResourceContextParameters.END_TIME_MS, String.valueOf(endTimeMs));

                        // 3. startTime, endTime and Duration in TC
                        String durationTimecode = ConversionHelper.editUnitToTimecode(durationEU, editRate);
                        String startTimeTimeCode = ConversionHelper.editUnitToTimecode(startTimeEU, editRate);
                        String endTimeTimeCode = ConversionHelper.editUnitToTimecode(endTimeEU, editRate);
                        resourceContext.addResourceParameter(resourceKey, resUuid,
                                ResourceContextParameters.DURATION_TIMECODE, String.valueOf(durationTimecode));
                        resourceContext.addResourceParameter(resourceKey, resUuid,
                                ResourceContextParameters.START_TIME_TIMECODE, String.valueOf(startTimeTimeCode));
                        resourceContext.addResourceParameter(resourceKey, resUuid,
                                ResourceContextParameters.END_TIME_TIMECODE, String.valueOf(endTimeTimeCode));

                        // 4. startTime and duration in frameEditUnits (will be re-calculated later)
                        // assume essence has only one seq type (either video or audio),
                        // so start time in frames is equal to start time in edit units.
                        contextProvider.getResourceContext().addResourceParameter(resourceKey, resUuid,
                                ResourceContextParameters.DURATION_FRAME_EDIT_UNIT, durationEU.toString());
                        contextProvider.getResourceContext().addResourceParameter(resourceKey, resUuid,
                                ResourceContextParameters.START_TIME_FRAME_EDIT_UNIT, startTimeEU.toString());
                    }
                }
            }
        }
    }

    /**
     * <ul>
     * <li>It contains common logic to calculate Offset in milliseconds.</li>
     * <li>Start timecode is taken into account when calculating offset parameter (the start code specified in conversion.xml's
     * Destination context has higher priority than the start timecode from CPL's composition)</li>
     * </ul>
     */
    private void calculateOffsetMs() {
        ResourceTemplateParameterContext resourceContext = contextProvider.getResourceContext();
        long destStartTimeMs = getDestStartTime();

        for (SequenceType seqType : contextProvider.getSequenceContext().getSequenceTypes()) {
            for (SequenceUUID seqUuid : contextProvider.getSequenceContext().getUuids(seqType)) {
                long offsetMs = destStartTimeMs;
                for (SegmentUUID segmUuid : contextProvider.getSegmentContext().getUuids()) {
                    ResourceKey resourceKey = ResourceKey.create(segmUuid, seqUuid, seqType);
                    for (ResourceUUID resUuid : resourceContext.getUuids(resourceKey)) {
                        ContextInfo contextInfo = new ContextInfoBuilder()
                                .setResourceUuid(resUuid)
                                .setSegmentUuid(segmUuid)
                                .setSequenceUuid(seqUuid)
                                .setSequenceType(seqType).build();

                        resourceContext.addResourceParameter(resourceKey, resUuid,
                                ResourceContextParameters.OFFSET_MS, String.valueOf(offsetMs));
                        long durationMs = new BigInteger(contextProvider.getResourceContext()
                                .getParameterValue(ResourceContextParameters.DURATION_MS, contextInfo)).longValue();
                        offsetMs += durationMs;
                    }
                }
            }
        }
    }

    private long getDestStartTime() {
        // composition timecode as specified in CPL
        String cplStartTime = getCompositionTimecodeStart();
        BigFraction cplRate = getCompositionTimecodeRate();

        // start time as specified in conversion.xml (destination parameter).
        DestTemplateParameterContext destContext = contextProvider.getDestContext();
        String destStartTime = destContext.getParameterValue(DestContextParameters.START_TIME);
        String destRateStr = destContext.getParameterValue(DestContextParameters.FRAME_RATE);
        BigFraction destRate = StringUtils.isEmpty(destRateStr)
                ? null : ConversionHelper.parseEditRate(destRateStr);

        // values from conversion.xml has higher priority
        String startTime = StringUtils.isEmpty(destStartTime) ? cplStartTime : destStartTime;
        BigFraction rate = destRate == null ? cplRate : destRate;

        // convert to milliseconds
        if (!StringUtils.isEmpty(startTime) && (rate != null)) {
            return ConversionHelper.smpteTimecodeToMilliSeconds(startTime, rate);
        }

        // default fallback 00:00:00:00
        return 0L;
    }

    /**
     * Re-check DURATION_FRAME_EDIT_UNIT and START_TIME_FRAME_EDIT_UNIT for audio sequences which has essences containing
     * both audio and video the values must be calculated in video frames in this case).
     */
    private void buildTimeAndDurationInFrames() {
        ResourceTemplateParameterContext resourceContext = contextProvider.getResourceContext();

        // get all video essences to re-check DURATION_FRAME_EDIT_UNIT and START_TIME_FRAME_EDIT_UNIT for
        // audio sequences which has essences containing both audio and video (the values must be calculated in video frames in this case)
        Map<String, BigFraction> videoEssences = new HashMap<>();
        SequenceType seqType = SequenceType.VIDEO;
        for (SequenceUUID seqUuid : contextProvider.getSequenceContext().getUuids(SequenceType.VIDEO)) {
            for (SegmentUUID segmUuid : contextProvider.getSegmentContext().getUuids()) {
                ResourceKey resourceKey = ResourceKey.create(segmUuid, seqUuid, seqType);
                for (ResourceUUID resUuid : resourceContext.getUuids(resourceKey)) {
                    ContextInfo contextInfo = new ContextInfoBuilder()
                            .setResourceUuid(resUuid)
                            .setSegmentUuid(segmUuid)
                            .setSequenceUuid(seqUuid)
                            .setSequenceType(seqType).build();
                    String essence = resourceContext.getParameterValue(ResourceContextParameters.ESSENCE, contextInfo);
                    String editRate = resourceContext.getParameterValue(ResourceContextParameters.EDIT_RATE, contextInfo);
                    BigFraction editRateFraction = ConversionHelper.parseEditRate(editRate);
                    videoEssences.put(essence, editRateFraction);
                }
            }
        }

        // process only audio
        seqType = SequenceType.AUDIO;
        for (SequenceUUID seqUuid : contextProvider.getSequenceContext().getUuids(seqType)) {
            for (SegmentUUID segmUuid : contextProvider.getSegmentContext().getUuids()) {
                ResourceKey resourceKey = ResourceKey.create(segmUuid, seqUuid, seqType);
                for (ResourceUUID resUuid : resourceContext.getUuids(resourceKey)) {
                    ContextInfo contextInfo = new ContextInfoBuilder()
                            .setResourceUuid(resUuid)
                            .setSegmentUuid(segmUuid)
                            .setSequenceUuid(seqUuid)
                            .setSequenceType(seqType).build();

                    String essence = resourceContext.getParameterValue(ResourceContextParameters.ESSENCE, contextInfo);
                    BigFraction videoEditRate = videoEssences.get(essence); // frame rate
                    // the essence containing the audio has also a video
                    if (videoEditRate != null) {
                        // start time and duration in audio edit units (samples)
                        BigInteger startTimeEU = new BigInteger(
                                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
                        BigInteger durationEU = new BigInteger(
                                resourceContext.getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
                        // audio edit rate (sample rate)
                        BigFraction editRate = ConversionHelper.parseEditRate(
                                resourceContext.getParameterValue(ResourceContextParameters.EDIT_RATE, contextInfo));

                        // convert start time and duration from samples to video frames
                        String startTimeInFrames = String.valueOf(ConversionHelper.toNewEditRate(startTimeEU, editRate, videoEditRate));
                        String durationInFrames = String.valueOf(ConversionHelper.toNewEditRate(durationEU, editRate, videoEditRate));

                        // save in context
                        resourceContext.addResourceParameter(resourceKey, resUuid, ResourceContextParameters.START_TIME_FRAME_EDIT_UNIT,
                                startTimeInFrames);
                        resourceContext.addResourceParameter(resourceKey, resUuid, ResourceContextParameters.DURATION_FRAME_EDIT_UNIT,
                                durationInFrames);
                    }
                }
            }
        }
    }

    private void buildSequenceLanguages() {
        SequenceTemplateParameterContext sequenceContext = contextProvider.getSequenceContext();
        for (SequenceType seqType : sequenceContext.getSequenceTypes()) {
            for (SequenceUUID seqUuid : sequenceContext.getUuids(seqType)) {
                String language = getSequenceLanguage(seqUuid);

                if (language != null) {
                    sequenceContext.addSequenceParameter(seqType, seqUuid, SequenceContextParameters.LANGUAGE, language);
                }
            }
        }
    }

    protected abstract String getSequenceLanguage(SequenceUUID seqUuid);

}
