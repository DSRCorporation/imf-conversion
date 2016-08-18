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
package com.netflix.imfutility.cpl.essencedescriptor;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.context.ResourceKey;
import com.netflix.imfutility.conversion.templateParameter.context.ResourceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.SequenceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters;
import com.netflix.imfutility.util.FFmpegAudioChannels;
import org.smpte_ra.reg._2003._2012.ISO7;
import org.smpte_ra.reg._395._2014._13._1.aaf.AudioChannelLabelSubDescriptor;
import org.smpte_ra.reg._395._2014._13._1.aaf.SoundfieldGroupLabelSubDescriptor;
import org.smpte_ra.reg._395._2014._13._1.aaf.WAVEPCMDescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses Audio Essence Descriptors and adds corresponding values to Resource context.
 * Currently we get the following information:
 * <ul>
 *     <li>Audio Channels Layout.</li>
 * </ul>
 */
public class AudioEssenceDescriptorProcessor {

    private static final String AUDIO_CHANNEL_PREFIX = "ch";

    private final TemplateParameterContextProvider contextProvider;

    public AudioEssenceDescriptorProcessor(TemplateParameterContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    public void process(WAVEPCMDescriptor waveDesc, ContextInfo contextInfo) {
        if (waveDesc.getSubDescriptors() == null) {
            return;
        }

        // 1. parse wave pcm sub-descriptors
        List<FFmpegAudioChannels> ffmpegAudioChannels = new ArrayList<>();
        String soundfieldGroupId = null;
        ISO7 language = null;
        for (Object subDescriptor : waveDesc.getSubDescriptors()
                .getDCTimedTextResourceSubDescriptorOrStereoscopicPictureSubDescriptorOrJPEG2000SubDescriptor()) {

            // 1.1 audio channel
            if (subDescriptor instanceof AudioChannelLabelSubDescriptor) {
                AudioChannelLabelSubDescriptor channelSubDescriptor = (AudioChannelLabelSubDescriptor) subDescriptor;
                FFmpegAudioChannels channel = parseAudioChannel(channelSubDescriptor);
                if (channel != null) {
                    ffmpegAudioChannels.add(channel);
                }
                // all channels must be long to the same group. so, we just take the first.
                if (soundfieldGroupId == null) {
                    soundfieldGroupId = channelSubDescriptor.getSoundfieldGroupLinkID();
                }
            }

            // 1.2 soundfield group
            if (subDescriptor instanceof SoundfieldGroupLabelSubDescriptor) {
                SoundfieldGroupLabelSubDescriptor soundfieldGroupDesc = (SoundfieldGroupLabelSubDescriptor) subDescriptor;
                language = soundfieldGroupDesc.getRFC5646SpokenLanguage();
            }
        }

        // 2. fill resource context
        ResourceTemplateParameterContext resourceContext = contextProvider.getResourceContext();
        SequenceTemplateParameterContext seqContext = contextProvider.getSequenceContext();

        // 2.1. channels layout
        if (!ffmpegAudioChannels.isEmpty()) {
            String channelLayout = FFmpegAudioChannels.toChannelsLayoutString(ffmpegAudioChannels);
            resourceContext.addResourceParameter(
                    ResourceKey.create(contextInfo),
                    contextInfo.getResourceUuid(),
                    ResourceContextParameters.CHANNELS_LAYOUT,
                    channelLayout);
        }

        // 2.2. soundfield group ID
        if (soundfieldGroupId != null) {
            resourceContext.addResourceParameter(
                    ResourceKey.create(contextInfo),
                    contextInfo.getResourceUuid(),
                    ResourceContextParameters.SOUNDFIELD_GROUP_ID,
                    soundfieldGroupId);
        }

        // 2.3. language
        if (language != null && language.getValue() != null) {
            resourceContext.addResourceParameter(
                    ResourceKey.create(contextInfo),
                    contextInfo.getResourceUuid(),
                    ResourceContextParameters.LANG,
                    language.getValue());

            // sequence language: assume that the sequence language is the language of the first resource
            if (!seqContext.hasSequenceParameter(SequenceContextParameters.LANGUAGE, contextInfo)) {
                seqContext.addSequenceParameter(
                        contextInfo.getSequenceType(), contextInfo.getSequenceUuid(),
                        SequenceContextParameters.LANGUAGE, language.getValue());
            }
        }


    }

    private FFmpegAudioChannels parseAudioChannel(AudioChannelLabelSubDescriptor audioChannelLabelSubDesc) {
        if (audioChannelLabelSubDesc.getMCATagSymbol() == null) {
            return null;
        }
        String channelStr = audioChannelLabelSubDesc.getMCATagSymbol().getValue()
                .replace(AUDIO_CHANNEL_PREFIX, "");
        return toFfmpegChannel(channelStr);
    }

    private FFmpegAudioChannels toFfmpegChannel(String channelStr) {
        channelStr = channelStr.replace(AUDIO_CHANNEL_PREFIX, "").toUpperCase();
        switch (channelStr) {
            case "L":
                return FFmpegAudioChannels.FL;
            case "R":
                return FFmpegAudioChannels.FR;
            case "C":
                return FFmpegAudioChannels.FC;
            case "LS":
                return FFmpegAudioChannels.SL;
            case "RS":
                return FFmpegAudioChannels.SR;
            default:
                return FFmpegAudioChannels.valueOf(channelStr.toUpperCase());
        }
    }

}
