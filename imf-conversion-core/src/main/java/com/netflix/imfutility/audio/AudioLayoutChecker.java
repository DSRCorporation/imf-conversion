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
package com.netflix.imfutility.audio;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfoBuilder;
import com.netflix.imfutility.conversion.templateParameter.context.ResourceKey;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters;
import com.netflix.imfutility.cpl.uuid.ResourceUUID;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.util.FFmpegAudioChannels;
import org.apache.commons.lang3.StringUtils;

/**
 * Checks consistency of CPL provided audio essences and descriptor info.
 */
public final class AudioLayoutChecker {

    private final TemplateParameterContextProvider contextProvider;

    public AudioLayoutChecker(TemplateParameterContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    /**
     * Check that all of resources which relate to audio sequence have the same channels layout values.
     *
     * @throws InvalidAudioChannelAssignmentException exception should be raised when definite resources parameters aren't the same
     */
    public void checkCorrectChannelLayout() throws InvalidAudioChannelAssignmentException {
        for (SequenceUUID seqUuid : contextProvider.getSequenceContext().getUuids(SequenceType.AUDIO)) {
            String channelLayout = null;
            for (SegmentUUID segmUuid : contextProvider.getSegmentContext().getUuids()) {
                for (ResourceUUID resUuid : contextProvider.getResourceContext()
                        .getUuids(ResourceKey.create(segmUuid, seqUuid, SequenceType.AUDIO))) {
                    ContextInfo contextInfo = new ContextInfoBuilder()
                            .setResourceUuid(resUuid)
                            .setSegmentUuid(segmUuid)
                            .setSequenceUuid(seqUuid)
                            .setSequenceType(SequenceType.AUDIO).build();

                    if (!contextProvider.getResourceContext().hasResourceParameter(
                            ResourceContextParameters.CHANNELS_LAYOUT, contextInfo)) {
                        // all resources must have a channel layout!
                        throw new InvalidAudioChannelAssignmentException(
                                "All resources within a sequence must have a channel layout set in the Essence Descriptor.");
                    }

                    String nextChannelLayout = contextProvider.getResourceContext().getParameterValue(
                            ResourceContextParameters.CHANNELS_LAYOUT, contextInfo);
                    if (StringUtils.isEmpty(nextChannelLayout)) {
                        // all resources must have a channel layout!
                        throw new InvalidAudioChannelAssignmentException(
                                "All resources within a sequence must have a channel layout set in the Essence Descriptor.");
                    }

                    if (channelLayout != null && !channelLayout.equals(nextChannelLayout)) {
                        // all resources within a sequence must have equal channel layouts
                        throw new InvalidAudioChannelAssignmentException(
                                "All resources within a sequence must have the same channel layout.");
                    }

                    if (!contextProvider.getResourceContext().hasResourceParameter(
                            ResourceContextParameters.CHANNELS_NUM, contextInfo)) {
                        // all resources must have a channels num!
                        throw new InvalidAudioChannelAssignmentException(
                                "All resources within a sequence must have a channels number set.");
                    }

                    Integer channelsCount = Integer.parseInt(contextProvider.getResourceContext().getParameterValue(
                            ResourceContextParameters.CHANNELS_NUM, contextInfo));
                    if (FFmpegAudioChannels.toFFmpegAudioChannels(nextChannelLayout).length != channelsCount) {
                        // the number of channels as defined in the channels layout must match the number of channels
                        // as get from media info tools
                        throw new InvalidAudioChannelAssignmentException(
                                String.format(
                                        "A number of channels in channel layout (%s) must match real number of channels (%d)",
                                        nextChannelLayout, channelsCount));
                    }

                    channelLayout = nextChannelLayout;
                }
            }
        }
    }
}
