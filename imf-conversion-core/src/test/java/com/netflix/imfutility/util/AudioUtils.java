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
package com.netflix.imfutility.util;

import com.netflix.imfutility.conversion.templateParameter.context.ResourceKey;
import com.netflix.imfutility.conversion.templateParameter.context.ResourceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.SequenceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters;
import com.netflix.imfutility.cpl.uuid.ResourceUUID;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.SequenceType;

import java.util.EnumSet;

import static com.netflix.imfutility.util.TemplateParameterContextCreator.createDefaultContextProviderWithCPLContext;

/**
 * Utility class for audio testing purposes.
 */
public final class AudioUtils {

    private AudioUtils() {
    }

    public static TemplateParameterContextProvider createContext(FFmpegAudioChannels[][] channelsForSeq)
            throws Exception {
        return createContext(channelsForSeq, null);
    }

    public static TemplateParameterContextProvider createContext(FFmpegAudioChannels[][] channelsForSeq,
                                                                 String[] langs) throws Exception {
        return createContext(channelsForSeq.length, channelsForSeq, langs);
    }

    public static TemplateParameterContextProvider createContext(int audioSeqCount,
                                                                 FFmpegAudioChannels[][] channelsForSeq) throws Exception {
        return createContext(audioSeqCount, channelsForSeq, null);
    }

    public static TemplateParameterContextProvider createContext(int audioSeqCount, FFmpegAudioChannels[][] channelsForSeq,
                                                                 String[] langs) throws Exception {
        return createContext(audioSeqCount, 1, 1, channelsForSeq, langs);
    }

    public static TemplateParameterContextProvider createContext(int audioSeqCount, int segmCount, int resourceCount,
                                                                 FFmpegAudioChannels[][] channelsForSeq) throws Exception {
        return createContext(audioSeqCount, segmCount, resourceCount, channelsForSeq, null);
    }

    public static TemplateParameterContextProvider createContext(int audioSeqCount, int segmCount, int resourceCount,
                                                                 FFmpegAudioChannels[][] channelsForSeq,
                                                                 String[] langs) throws Exception {
        return createContext(audioSeqCount, segmCount, resourceCount, channelsForSeq, null, langs);
    }

    public static TemplateParameterContextProvider createContext(int audioSeqCount, int segmCount, int resourceCount,
                                                                 FFmpegAudioChannels[][] channelsForSeq,
                                                                 String[] soundfieldGroups,
                                                                 String[] langs) throws Exception {
        return createContext(audioSeqCount, segmCount, resourceCount, channelsForSeq, soundfieldGroups, null, langs);
    }

    public static TemplateParameterContextProvider createContext(int audioSeqCount, int segmCount, int resourceCount,
                                                                 FFmpegAudioChannels[][] channelsForSeq,
                                                                 String[] soundfieldGroups,
                                                                 int[] channelsNum,
                                                                 String[] langs) throws Exception {
        TemplateParameterContextProvider contextProvider =
                createDefaultContextProviderWithCPLContext(segmCount, audioSeqCount, resourceCount, EnumSet.of(SequenceType.AUDIO));
        SequenceTemplateParameterContext sequenceContext = contextProvider.getSequenceContext();
        ResourceTemplateParameterContext resourceContext = contextProvider.getResourceContext();

        int i = 0;
        int j = 0;
        for (SequenceUUID seqUuid : sequenceContext.getUuids(SequenceType.AUDIO)) {
            if (langs != null && i < langs.length) {
                sequenceContext.addSequenceParameter(
                        SequenceType.AUDIO, seqUuid,
                        SequenceContextParameters.LANGUAGE, langs[i]);
            }

            for (SegmentUUID segmUuid : contextProvider.getSegmentContext().getUuids()) {
                for (ResourceUUID resUuid : contextProvider.getResourceContext()
                        .getUuids(ResourceKey.create(segmUuid, seqUuid, SequenceType.AUDIO))) {
                    if (channelsForSeq != null && j < channelsForSeq.length) {
                        resourceContext.addResourceParameter(
                                ResourceKey.create(segmUuid, seqUuid, SequenceType.AUDIO), resUuid,
                                ResourceContextParameters.CHANNELS_LAYOUT,
                                FFmpegAudioChannels.toChannelsLayoutString(channelsForSeq[j])
                        );
                        resourceContext.addResourceParameter(
                                ResourceKey.create(segmUuid, seqUuid, SequenceType.AUDIO), resUuid,
                                ResourceContextParameters.CHANNELS_NUM,
                                String.valueOf(channelsForSeq[j].length)
                        );
                        sequenceContext.addSequenceParameter(
                                SequenceType.AUDIO, seqUuid,
                                SequenceContextParameters.CHANNELS_NUM,
                                String.valueOf(channelsForSeq[j].length)
                        );
                    }

                    if (soundfieldGroups != null && j < soundfieldGroups.length) {
                        resourceContext.addResourceParameter(
                                ResourceKey.create(segmUuid, seqUuid, SequenceType.AUDIO), resUuid,
                                ResourceContextParameters.SOUNDFIELD_GROUP_ID,
                                soundfieldGroups[j]
                        );
                    }

                    if (langs != null && i < langs.length) {
                        resourceContext.addResourceParameter(
                                ResourceKey.create(segmUuid, seqUuid, SequenceType.AUDIO), resUuid,
                                ResourceContextParameters.LANG,
                                langs[i]
                        );
                    }

                    if (channelsNum != null && i < channelsNum.length) {
                        resourceContext.addResourceParameter(
                                ResourceKey.create(segmUuid, seqUuid, SequenceType.AUDIO), resUuid,
                                ResourceContextParameters.CHANNELS_NUM,
                                String.valueOf(channelsNum[i])
                        );
                        sequenceContext.addSequenceParameter(
                                SequenceType.AUDIO, seqUuid,
                                SequenceContextParameters.CHANNELS_NUM,
                                String.valueOf(channelsNum[i])
                        );
                    }
                    j++;
                }
                i++;
            }
        }

        return contextProvider;
    }

}
