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
import com.netflix.imfutility.conversion.templateParameter.context.SequenceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters;
import com.netflix.imfutility.cpl.uuid.ResourceUUID;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.util.FFmpegAudioChannels;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Provides basic methods for finding and processing audio layouts defined by EssenceDescriptor.
 * Data related to grouped channels layout aggregates into SoundfieldGroup.
 * (see {@link SoundfieldGroupInfo}).
 */
public class SoundfieldGroupHelper {

    protected final TemplateParameterContextProvider contextProvider;
    private final Map<String, SoundfieldGroupInfo> inputSoundfieldGroups;

    public SoundfieldGroupHelper(TemplateParameterContextProvider contextProvider) {
        this.contextProvider = contextProvider;
        this.inputSoundfieldGroups = new LinkedHashMap<>();
    }

    public void prepareContext() throws InvalidAudioChannelAssignmentException {
        new AudioLayoutChecker(contextProvider).checkCorrectChannelLayout();

        prepareSoundfieldGroups();
    }

    private void prepareSoundfieldGroups() throws InvalidAudioChannelAssignmentException {
        for (SequenceUUID seqUuid : contextProvider.getSequenceContext().getUuids(SequenceType.AUDIO)) {
            // several sequences may belong to the same soundfield group
            StringBuilder soundfieldGroupIdKey = new StringBuilder();
            // find a channel layout for a sequence
            String channelLayout = null;
            for (SegmentUUID segmUuid : contextProvider.getSegmentContext().getUuids()) {
                for (ResourceUUID resUuid : contextProvider.getResourceContext()
                        .getUuids(ResourceKey.create(segmUuid, seqUuid, SequenceType.AUDIO))) {
                    ContextInfo contextInfo = new ContextInfoBuilder()
                            .setResourceUuid(resUuid)
                            .setSegmentUuid(segmUuid)
                            .setSequenceUuid(seqUuid)
                            .setSequenceType(SequenceType.AUDIO)
                            .build();

                    if (!contextProvider.getResourceContext().hasResourceParameter(
                            ResourceContextParameters.SOUNDFIELD_GROUP_ID, contextInfo)) {
                        soundfieldGroupIdKey.append("");
                    } else {
                        String soundFieldGroupId = contextProvider.getResourceContext().getParameterValue(
                                ResourceContextParameters.SOUNDFIELD_GROUP_ID, contextInfo);
                        soundfieldGroupIdKey.append(soundFieldGroupId);
                    }

                    if (channelLayout == null) {
                        // we've already checked that all resources within a sequence have the same channel layout
                        channelLayout = contextProvider.getResourceContext().getParameterValue(
                                ResourceContextParameters.CHANNELS_LAYOUT, contextInfo);
                    }
                }
            }

            String soundfieldKey = soundfieldGroupIdKey.toString();
            // if no soundfield groups specified
            if (soundfieldKey.isEmpty()) {
                soundfieldKey = UUID.randomUUID().toString();
            }

            if (channelLayout != null) {
                SoundfieldGroupInfo info = inputSoundfieldGroups.get(soundfieldKey);
                if (info == null) {
                    info = new SoundfieldGroupInfo();
                    inputSoundfieldGroups.put(soundfieldKey, info);
                }
                info.addChannels(seqUuid, channelLayout);
            }
        }
    }

    protected List<SoundfieldGroupInfo> findInputForChannelGroup(FFmpegAudioChannels[] channelsGroup) {
        List<SoundfieldGroupInfo> result = new ArrayList<>();
        for (SoundfieldGroupInfo soundfieldGroupInfo : inputSoundfieldGroups.values()) {
            Set<FFmpegAudioChannels> inputChannels = soundfieldGroupInfo.getChannelsMap().keySet();
            Set<FFmpegAudioChannels> requiredChannels = new HashSet<>(Arrays.asList(channelsGroup));
            if (!inputChannels.equals(requiredChannels)) {
                continue;
            }
            result.add(soundfieldGroupInfo);
        }
        return result;
    }

    protected String getLanguage(SoundfieldGroupInfo info) {
        String lang = null;
        for (ImmutablePair<SequenceUUID, Integer> seqInfos : info.getChannelsMap().values()) {
            SequenceTemplateParameterContext seqContext = contextProvider.getSequenceContext();
            ContextInfo contextInfo = new ContextInfoBuilder()
                    .setSequenceType(SequenceType.AUDIO)
                    .setSequenceUuid(seqInfos.getLeft())
                    .build();
            if (!seqContext.hasSequenceParameter(SequenceContextParameters.LANGUAGE, contextInfo)) {
                return null;
            }
            String nextLang = seqContext.getParameterValue(SequenceContextParameters.LANGUAGE, contextInfo);
            // all sequences from a soundfield group must have the same language!
            if (lang != null && !lang.equals(nextLang)) {
                return null;
            }
            lang = nextLang;
        }
        return lang;
    }

}
