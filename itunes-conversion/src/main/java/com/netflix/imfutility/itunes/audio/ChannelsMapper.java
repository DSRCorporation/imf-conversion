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
package com.netflix.imfutility.itunes.audio;

import com.netflix.imfutility.audio.InvalidAudioChannelAssignmentException;
import com.netflix.imfutility.audio.SoundfieldGroupHelper;
import com.netflix.imfutility.audio.SoundfieldGroupInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfoBuilder;
import com.netflix.imfutility.conversion.templateParameter.context.SequenceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.itunes.locale.LocaleHelper;
import com.netflix.imfutility.util.FFmpegAudioChannels;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.netflix.imfutility.audio.AudioConstants.STEREO_LAYOUT;
import static com.netflix.imfutility.audio.AudioConstants.SURROUND_5_1_LAYOUT;
import static com.netflix.imfutility.itunes.audio.ChannelsMapper.LayoutType.STEREO;
import static com.netflix.imfutility.itunes.audio.ChannelsMapper.LayoutType.SURROUND;

/**
 * Maps channels in accordance with EssenceDescriptor or by order (if there are no descriptors associated with essence).
 */
public final class ChannelsMapper extends SoundfieldGroupHelper {

    private final Logger logger = LoggerFactory.getLogger(ChannelsMapper.class);

    private final List<Pair<SequenceUUID, Integer>> channelsByOrder;
    Map<Pair<LayoutType, String>, List<Pair<SequenceUUID, Integer>>> resultChannels = new LinkedHashMap<>();
    private boolean essenceLayoutValid;

    public ChannelsMapper(TemplateParameterContextProvider contextProvider) {
        super(contextProvider);
        this.channelsByOrder = new ArrayList<>();

        prepareContext();
    }

    @Override
    public void prepareContext() {
        prepareChannelsByOrder();

        try {
            super.prepareContext();
            essenceLayoutValid = true;
        } catch (InvalidAudioChannelAssignmentException e) {
            logger.warn("Layout form Essence descriptors set in CPL cannot properly defined. {}", e.getLocalizedMessage());
            essenceLayoutValid = false;
        }

    }

    /**
     * Prepare channels layout by order.
     */
    private void prepareChannelsByOrder() {
        SequenceTemplateParameterContext sequenceContext = contextProvider.getSequenceContext();

        channelsByOrder.clear();

        for (SequenceUUID seqUuid : sequenceContext.getUuids(SequenceType.AUDIO)) {
            ContextInfo contextInfo = new ContextInfoBuilder()
                    .setSequenceType(SequenceType.AUDIO)
                    .setSequenceUuid(seqUuid)
                    .build();
            String channelsNum = sequenceContext.getParameterValue(SequenceContextParameters.CHANNELS_NUM, contextInfo);

            IntStream.rangeClosed(1, Integer.parseInt(channelsNum))
                    .mapToObj(i -> Pair.of(seqUuid, i))
                    .forEachOrdered(channelsByOrder::add);
        }
    }

    /**
     * Map passed options in accordance with EssenceDescriptor or by order.
     * If descriptor doesn't exist for definite option - it will be mapped by order.
     *
     * @param options options that contain definition of provided layout and language
     */
    public void mapChannels(List<Pair<LayoutType, String>> options) {
        List<Pair<LayoutType, String>> optionsByDescriptor = new ArrayList<>();
        List<Pair<LayoutType, String>> optionsByOrder = new ArrayList<>();

        if (essenceLayoutValid) {
            optionsByDescriptor.addAll(options);
        } else {
            optionsByOrder.addAll(options);
        }

        optionsByDescriptor.stream().forEachOrdered(option -> {
            List<Pair<SequenceUUID, Integer>> channels = guessChannelsByEssenceDescriptor(option.getKey(), option.getValue());

            if (channels.isEmpty()) {
                optionsByOrder.add(option);
                return;
            }

            channelsByOrder.removeAll(channels);
            resultChannels.put(option, channels);
        });

        optionsByOrder.stream().forEachOrdered(option -> {
            List<Pair<SequenceUUID, Integer>> channels = guessChannelsByOrder(option.getKey());

            if (channels.isEmpty()) {
                return;
            }

            channelsByOrder.removeAll(channels);
            resultChannels.put(option, channels);
        });
    }

    /**
     * Gets channels associated with definite layout and language.
     *
     * @param option option
     * @return list of channels
     */
    public List<Pair<SequenceUUID, Integer>> getChannels(Pair<LayoutType, String> option) {
        return resultChannels.containsKey(option) ? resultChannels.get(option) : new ArrayList<>();
    }

    /**
     * Gets channels from descriptor which can be associated for main audio with passed language.
     * At first scan descriptors to find out surround layout, if there are no channels - scan for stereo layout.
     * If channels can't be defined by descriptor return empty list.
     *
     * @param lang language
     * @return list of channels
     */
    public List<Pair<SequenceUUID, Integer>> guessMainAudio(String lang) {
        List<Pair<SequenceUUID, Integer>> surround = guessChannelsByEssenceDescriptor(SURROUND, lang);

        if (surround.isEmpty()) {
            return guessChannelsByEssenceDescriptor(STEREO, lang);
        }

        return surround;
    }

    /**
     * Gets map of languages and its channels from descriptor which can be associated with alternative audios.
     *
     * @param mainLang language of main audio (excluded from scan)
     * @return list of channels
     */
    public Map<String, List<Pair<SequenceUUID, Integer>>> guessAlternatives(String mainLang) {
        List<SoundfieldGroupInfo> stereo = findInputForChannelGroup(STEREO_LAYOUT);

        return stereo.stream()
                .filter(g -> getLanguage(g) != null)
                .filter(g -> !LocaleHelper.equalsByDefaultRegion(mainLang, getLanguage(g)))
                .collect(Collectors.toMap(this::getLanguage, g -> getChannelsByLayout(STEREO_LAYOUT, g)));
    }

    private List<Pair<SequenceUUID, Integer>> guessChannelsByEssenceDescriptor(LayoutType layoutType, String lang) {
        switch (layoutType) {
            case SURROUND:
                return guessSurroundChannels(lang);
            case STEREO:
                return guessStereoChannels(lang);
            default:
                throw new RuntimeException("Unknown layout type:" + String.valueOf(layoutType));
        }
    }

    private List<Pair<SequenceUUID, Integer>> guessSurroundChannels(String lang) {
        List<Pair<SequenceUUID, Integer>> channels = new ArrayList<>();

        List<Pair<SequenceUUID, Integer>> surround = guessChannelsByChannelsGroup(SURROUND_5_1_LAYOUT, lang);
        if (!surround.isEmpty()) {
            channels.addAll(surround);

            List<Pair<SequenceUUID, Integer>> stereo = guessChannelsByChannelsGroup(STEREO_LAYOUT, lang);
            if (stereo.isEmpty()) {
                // create additional Stereo use L and R from Surround
                channels.add(surround.get(0));
                channels.add(surround.get(1));
            } else {
                channels.addAll(stereo);
            }
        }

        return channels;
    }

    private List<Pair<SequenceUUID, Integer>> guessStereoChannels(String lang) {
        List<Pair<SequenceUUID, Integer>> channels = new ArrayList<>();

        List<Pair<SequenceUUID, Integer>> stereo = guessChannelsByChannelsGroup(STEREO_LAYOUT, lang);
        if (!stereo.isEmpty()) {
            channels.addAll(stereo);
        }

        return channels;
    }

    private List<Pair<SequenceUUID, Integer>> guessChannelsByChannelsGroup(FFmpegAudioChannels[] channelsGroup, String lang) {
        return findInputForChannelGroup(channelsGroup).stream()
                .filter(g -> !isGroupUsed(g))
                .filter(g -> LocaleHelper.equalsByDefaultRegion(lang, getLanguage(g)))
                .findFirst()
                .map(g -> getChannelsByLayout(channelsGroup, g))
                .orElseGet(ArrayList::new);
    }

    private List<Pair<SequenceUUID, Integer>> guessChannelsByOrder(LayoutType layoutType) {
        List<Pair<SequenceUUID, Integer>> channels = channelsByOrder.stream()
                .limit(channelsByOrder.size() >= layoutType.maxSize ? layoutType.maxSize : layoutType.minSize)
                .collect(Collectors.toList());

        // not enough channels for layout
        if (channels.size() < layoutType.minSize) {
            return new ArrayList<>();
        }

        // add used channels for correct layout definition
        // for Surround -> Lt=FL, Rt=FR
        // for Stereo -> L=FC, R=FC (stereo from two monos)
        IntStream.range(0, layoutType.maxSize - channels.size())
                .mapToObj(channels::get)
                .forEach(channels::add);
        return channels;
    }

    private boolean isGroupUsed(SoundfieldGroupInfo soundfieldGroup) {
        return soundfieldGroup.getChannelsMap().values().stream()
                .noneMatch(this::isChannelUsed);
    }

    private boolean isChannelUsed(Pair<SequenceUUID, Integer> channel) {
        return resultChannels.values().stream()
                .flatMap(Collection::stream)
                .noneMatch(channel::equals);
    }

    private List<Pair<SequenceUUID, Integer>> getChannelsByLayout(FFmpegAudioChannels[] channelsGroup,
                                                                  SoundfieldGroupInfo soundfieldGroup) {
        return Stream.of(channelsGroup)
                .map(soundfieldGroup.getChannelsMap()::get)
                .collect(Collectors.toList());
    }

    /**
     * Layout types with appropriate channel count definition.
     */
    public enum LayoutType {
        SURROUND(8, 6), STEREO(2, 1);

        protected final Integer maxSize;
        protected final Integer minSize;

        LayoutType(Integer maxSize, Integer minSize) {
            this.maxSize = maxSize;
            this.minSize = minSize;
        }
    }

}
