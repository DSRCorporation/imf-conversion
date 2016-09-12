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

import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.itunes.audio.ChannelsMapper.LayoutType;
import com.netflix.imfutility.util.AudioUtils;
import com.netflix.imfutility.util.FFmpegAudioChannels;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.netflix.imfutility.itunes.audio.ChannelsMapper.LayoutType.STEREO;
import static com.netflix.imfutility.itunes.audio.ChannelsMapper.LayoutType.SURROUND;
import static com.netflix.imfutility.util.FFmpegAudioChannels.FC;
import static com.netflix.imfutility.util.FFmpegAudioChannels.FL;
import static com.netflix.imfutility.util.FFmpegAudioChannels.FR;
import static com.netflix.imfutility.util.FFmpegAudioChannels.LFE;
import static com.netflix.imfutility.util.FFmpegAudioChannels.SL;
import static com.netflix.imfutility.util.FFmpegAudioChannels.SR;
import static com.netflix.imfutility.util.TemplateParameterContextCreator.getSequenceUuid;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Tests the channels properly mapped on provided layout either in accordance with EssecnceDescriptor or by order.
 * (see {@link ChannelsMapper}).
 */
public class ChannelsMapperTest {

    @Test
    public void testNoAudio() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                });

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{SURROUND}, new String[]{"en"}));

        assertTrue(mapper.resultChannels.isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void testUnknownLayout() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                });

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{null}, new String[]{"en"}));
    }

    // Surround

    @Test
    public void testSurroundWithStereo() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR, FC, LFE, SL, SR},
                        {FL, FR}
                },
                new String[]{"en", "en"});

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{SURROUND}, new String[]{"en"}));

        List<Pair<SequenceUUID, Integer>> channels = mapper.getChannels(Pair.of(SURROUND, "en"));

        assertChannelEquals(channels.get(0), 0, 1);
        assertChannelEquals(channels.get(1), 0, 2);
        assertChannelEquals(channels.get(2), 0, 3);
        assertChannelEquals(channels.get(3), 0, 4);
        assertChannelEquals(channels.get(4), 0, 5);
        assertChannelEquals(channels.get(5), 0, 6);

        assertChannelEquals(channels.get(6), 1, 1);
        assertChannelEquals(channels.get(7), 1, 2);

        assertEquals(8, channels.size());
    }

    @Test
    public void testSurroundWithTwoStereo() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR},
                        {FC},
                        {FL, FR, FC, LFE, SL, SR},
                        {FL, FR}
                }, new String[]{"fr", "en", "en", "en"});

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{SURROUND}, new String[]{"en"}));

        List<Pair<SequenceUUID, Integer>> channels = mapper.getChannels(Pair.of(SURROUND, "en"));

        assertChannelEquals(channels.get(0), 2, 1);
        assertChannelEquals(channels.get(1), 2, 2);
        assertChannelEquals(channels.get(2), 2, 3);
        assertChannelEquals(channels.get(3), 2, 4);
        assertChannelEquals(channels.get(4), 2, 5);
        assertChannelEquals(channels.get(5), 2, 6);

        assertChannelEquals(channels.get(6), 3, 1);
        assertChannelEquals(channels.get(7), 3, 2);

        assertEquals(8, channels.size());
    }

    @Test
    public void testSurroundWithNoStereo() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR, FC, LFE, SL, SR},
                        {FC}
                },
                new String[]{"en", "en"});

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{SURROUND}, new String[]{"en"}));

        List<Pair<SequenceUUID, Integer>> channels = mapper.getChannels(Pair.of(SURROUND, "en"));

        assertChannelEquals(channels.get(0), 0, 1);
        assertChannelEquals(channels.get(1), 0, 2);
        assertChannelEquals(channels.get(2), 0, 3);
        assertChannelEquals(channels.get(3), 0, 4);
        assertChannelEquals(channels.get(4), 0, 5);
        assertChannelEquals(channels.get(5), 0, 6);

        assertChannelEquals(channels.get(6), 0, 1);
        assertChannelEquals(channels.get(7), 0, 2);

        assertEquals(8, channels.size());
    }

    @Test
    public void testTwoSurrounds() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR, FC, LFE, SL, SR},
                        {FL, FR, FC, LFE, SL, SR}
                },
                new String[]{"fr", "en"});

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{SURROUND}, new String[]{"en"}));

        List<Pair<SequenceUUID, Integer>> channels = mapper.getChannels(Pair.of(SURROUND, "en"));

        assertChannelEquals(channels.get(0), 1, 1);
        assertChannelEquals(channels.get(1), 1, 2);
        assertChannelEquals(channels.get(2), 1, 3);
        assertChannelEquals(channels.get(3), 1, 4);
        assertChannelEquals(channels.get(4), 1, 5);
        assertChannelEquals(channels.get(5), 1, 6);

        assertChannelEquals(channels.get(6), 1, 1);
        assertChannelEquals(channels.get(7), 1, 2);

        assertEquals(8, channels.size());
    }

    @Test
    public void testSurroundWithStereoDiffLang() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR, FC, LFE, SL, SR},
                        {FL, FR}
                },
                new String[]{"en", "fr"});

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{SURROUND}, new String[]{"en"}));

        List<Pair<SequenceUUID, Integer>> channels = mapper.getChannels(Pair.of(SURROUND, "en"));

        assertChannelEquals(channels.get(0), 0, 1);
        assertChannelEquals(channels.get(1), 0, 2);
        assertChannelEquals(channels.get(2), 0, 3);
        assertChannelEquals(channels.get(3), 0, 4);
        assertChannelEquals(channels.get(4), 0, 5);
        assertChannelEquals(channels.get(5), 0, 6);

        assertChannelEquals(channels.get(6), 0, 1);
        assertChannelEquals(channels.get(7), 0, 2);

        assertEquals(8, channels.size());
    }

    @Test
    public void testSurroundDiffLang() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FC},
                        {FL, FR, FC, LFE, SL, SR}
                },
                new String[]{"en", "fr"});

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{SURROUND}, new String[]{"en"}));

        // defined by order
        List<Pair<SequenceUUID, Integer>> channels = mapper.getChannels(Pair.of(SURROUND, "en"));

        assertChannelEquals(channels.get(0), 0, 1);
        assertChannelEquals(channels.get(1), 1, 1);
        assertChannelEquals(channels.get(2), 1, 2);
        assertChannelEquals(channels.get(3), 1, 3);
        assertChannelEquals(channels.get(4), 1, 4);
        assertChannelEquals(channels.get(5), 1, 5);

        assertChannelEquals(channels.get(6), 0, 1);
        assertChannelEquals(channels.get(7), 1, 1);

        assertEquals(8, channels.size());
    }

    @Test
    public void testSurroundInsufficientChannels() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FC},
                        {FL, FR}
                },
                new String[]{"en", "fr"});

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{SURROUND}, new String[]{"en"}));

        // not enough channels to define by order
        assertTrue(mapper.getChannels(Pair.of(SURROUND, "en")).isEmpty());
    }

    @Test
    public void testSurroundWithStereoByOrder() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR, FC, LFE, SL, SR},
                        {FL, FR}
                },
                new String[]{"fr", "fr"});

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{SURROUND}, new String[]{"en"}));

        // defined by order
        List<Pair<SequenceUUID, Integer>> channels = mapper.getChannels(Pair.of(SURROUND, "en"));

        assertChannelEquals(channels.get(0), 0, 1);
        assertChannelEquals(channels.get(1), 0, 2);
        assertChannelEquals(channels.get(2), 0, 3);
        assertChannelEquals(channels.get(3), 0, 4);
        assertChannelEquals(channels.get(4), 0, 5);
        assertChannelEquals(channels.get(5), 0, 6);

        assertChannelEquals(channels.get(6), 1, 1);
        assertChannelEquals(channels.get(7), 1, 2);

        assertEquals(8, channels.size());
    }

    @Test
    public void testSurroundByOrder() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR, FC, LFE, SL, SR}
                },
                new String[]{"fr"});

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{SURROUND}, new String[]{"en"}));

        // defined by order
        List<Pair<SequenceUUID, Integer>> channels = mapper.getChannels(Pair.of(SURROUND, "en"));

        assertChannelEquals(channels.get(0), 0, 1);
        assertChannelEquals(channels.get(1), 0, 2);
        assertChannelEquals(channels.get(2), 0, 3);
        assertChannelEquals(channels.get(3), 0, 4);
        assertChannelEquals(channels.get(4), 0, 5);
        assertChannelEquals(channels.get(5), 0, 6);

        assertChannelEquals(channels.get(6), 0, 1);
        assertChannelEquals(channels.get(7), 0, 2);

        assertEquals(8, channels.size());
    }

    @Test
    public void testSurroundNoDescriptor() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(3, 1, 1,
                new FFmpegAudioChannels[][]{
                }
                , null,
                new int[]{2, 2, 4},
                null);

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{SURROUND}, new String[]{"en"}));

        // defined by order
        List<Pair<SequenceUUID, Integer>> channels = mapper.getChannels(Pair.of(SURROUND, "en"));

        assertChannelEquals(channels.get(0), 0, 1);
        assertChannelEquals(channels.get(1), 0, 2);
        assertChannelEquals(channels.get(2), 1, 1);
        assertChannelEquals(channels.get(3), 1, 2);
        assertChannelEquals(channels.get(4), 2, 1);
        assertChannelEquals(channels.get(5), 2, 2);

        assertChannelEquals(channels.get(6), 2, 3);
        assertChannelEquals(channels.get(7), 2, 4);

        assertEquals(8, channels.size());
    }

    @Test
    public void testSurroundSwapChannels() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {SR, FL, FC, SL, FR, LFE},
                        {FR, FL}
                }
                , new String[]{"en", "en"});

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{SURROUND}, new String[]{"en"}));

        List<Pair<SequenceUUID, Integer>> channels = mapper.getChannels(Pair.of(SURROUND, "en"));

        assertChannelEquals(channels.get(0), 0, 2);
        assertChannelEquals(channels.get(1), 0, 5);
        assertChannelEquals(channels.get(2), 0, 3);
        assertChannelEquals(channels.get(3), 0, 6);
        assertChannelEquals(channels.get(4), 0, 4);
        assertChannelEquals(channels.get(5), 0, 1);

        assertChannelEquals(channels.get(6), 1, 2);
        assertChannelEquals(channels.get(7), 1, 1);

        assertEquals(8, channels.size());
    }

    // Stereo

    @Test
    public void testStereo() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR}
                },
                new String[]{"en"});

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{STEREO}, new String[]{"en"}));

        List<Pair<SequenceUUID, Integer>> channels = mapper.getChannels(Pair.of(STEREO, "en"));

        assertChannelEquals(channels.get(0), 0, 1);
        assertChannelEquals(channels.get(1), 0, 2);

        assertEquals(2, channels.size());
    }

    @Test
    public void testTwoStereo() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR},
                        {FL, FR}
                },
                new String[]{"fr", "en"});

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{STEREO}, new String[]{"en"}));

        List<Pair<SequenceUUID, Integer>> channels = mapper.getChannels(Pair.of(STEREO, "en"));

        assertChannelEquals(channels.get(0), 1, 1);
        assertChannelEquals(channels.get(1), 1, 2);

        assertEquals(2, channels.size());
    }

    @Test
    public void testStereoDiffLang() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR, FC, LFE, SL, SR},
                        {FL, FR}
                },
                new String[]{"en", "fr"});

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{STEREO}, new String[]{"en"}));

        // defined by order
        List<Pair<SequenceUUID, Integer>> channels = mapper.getChannels(Pair.of(STEREO, "en"));

        assertChannelEquals(channels.get(0), 0, 1);
        assertChannelEquals(channels.get(1), 0, 2);

        assertEquals(2, channels.size());
    }

    @Test
    public void testStereoByOrder() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR}
                },
                new String[]{"fr"});

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{STEREO}, new String[]{"en"}));

        // defined by order
        List<Pair<SequenceUUID, Integer>> channels = mapper.getChannels(Pair.of(STEREO, "en"));

        assertChannelEquals(channels.get(0), 0, 1);
        assertChannelEquals(channels.get(1), 0, 2);

        assertEquals(2, channels.size());
    }

    @Test
    public void testStereoMonoByOrder() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FC}
                },
                new String[]{"fr"});

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{STEREO}, new String[]{"en"}));

        // defined by order
        List<Pair<SequenceUUID, Integer>> channels = mapper.getChannels(Pair.of(STEREO, "en"));

        assertChannelEquals(channels.get(0), 0, 1);
        assertChannelEquals(channels.get(1), 0, 1);

        assertEquals(2, channels.size());
    }

    @Test
    public void testStereoNoDescriptor() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(2, 1, 1,
                new FFmpegAudioChannels[][]{
                }
                , null,
                new int[]{1, 3},
                null);

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{STEREO}, new String[]{"en"}));

        // defined by order
        List<Pair<SequenceUUID, Integer>> channels = mapper.getChannels(Pair.of(STEREO, "en"));

        assertChannelEquals(channels.get(0), 0, 1);
        assertChannelEquals(channels.get(1), 1, 1);

        assertEquals(2, channels.size());
    }

    @Test
    public void testStereoSwapChannels() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FR, FL}
                }
                , new String[]{"en"});

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{STEREO}, new String[]{"en"}));

        List<Pair<SequenceUUID, Integer>> channels = mapper.getChannels(Pair.of(STEREO, "en"));

        assertChannelEquals(channels.get(0), 0, 2);
        assertChannelEquals(channels.get(1), 0, 1);

        assertEquals(2, channels.size());
    }

    // Complex layout tests

    @Test
    public void testSurroundAndStereoFromDescriptor() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR, FC, LFE, SL, SR},
                        {FL, FR},
                        {FC},
                        {FL, FR},
                        {FL, FR, FC, LFE, SL, SR}
                },
                new String[]{"en", "en", "fr", "en", "en"});

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{SURROUND, STEREO}, new String[]{"en", "en"}));

        List<Pair<SequenceUUID, Integer>> channels;
        channels = mapper.getChannels(Pair.of(SURROUND, "en"));

        assertChannelEquals(channels.get(0), 0, 1);
        assertChannelEquals(channels.get(1), 0, 2);
        assertChannelEquals(channels.get(2), 0, 3);
        assertChannelEquals(channels.get(3), 0, 4);
        assertChannelEquals(channels.get(4), 0, 5);
        assertChannelEquals(channels.get(5), 0, 6);

        assertChannelEquals(channels.get(6), 1, 1);
        assertChannelEquals(channels.get(7), 1, 2);

        assertEquals(8, channels.size());

        channels = mapper.getChannels(Pair.of(STEREO, "en"));

        assertChannelEquals(channels.get(0), 3, 1);
        assertChannelEquals(channels.get(1), 3, 2);

        assertEquals(2, channels.size());
    }

    @Test
    public void testSurroundWithoutStereoAndStereoFromDescriptor() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR, FC, LFE, SL, SR},
                        {FC},
                        {FL, FR},
                        {FL, FR, FC, LFE, SL, SR}
                },
                new String[]{"en", "en", "fr", "en"});

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{SURROUND, STEREO}, new String[]{"en", "fr"}));

        List<Pair<SequenceUUID, Integer>> channels;
        channels = mapper.getChannels(Pair.of(SURROUND, "en"));

        assertChannelEquals(channels.get(0), 0, 1);
        assertChannelEquals(channels.get(1), 0, 2);
        assertChannelEquals(channels.get(2), 0, 3);
        assertChannelEquals(channels.get(3), 0, 4);
        assertChannelEquals(channels.get(4), 0, 5);
        assertChannelEquals(channels.get(5), 0, 6);

        assertChannelEquals(channels.get(6), 0, 1);
        assertChannelEquals(channels.get(7), 0, 2);

        assertEquals(8, channels.size());

        channels = mapper.getChannels(Pair.of(STEREO, "fr"));

        assertChannelEquals(channels.get(0), 2, 1);
        assertChannelEquals(channels.get(1), 2, 2);

        assertEquals(2, channels.size());
    }

    @Test
    public void testSurroundFromDescriptorAndStereoByOrder() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR, FC, LFE, SL, SR},
                        {FC},
                        {FC},
                        {FL, FR, FC, LFE, SL, SR}
                },
                new String[]{"en", "en", "en", "en"});

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{SURROUND, STEREO}, new String[]{"en", "fr"}));

        List<Pair<SequenceUUID, Integer>> channels;
        channels = mapper.getChannels(Pair.of(SURROUND, "en"));

        assertChannelEquals(channels.get(0), 0, 1);
        assertChannelEquals(channels.get(1), 0, 2);
        assertChannelEquals(channels.get(2), 0, 3);
        assertChannelEquals(channels.get(3), 0, 4);
        assertChannelEquals(channels.get(4), 0, 5);
        assertChannelEquals(channels.get(5), 0, 6);

        assertChannelEquals(channels.get(6), 0, 1);
        assertChannelEquals(channels.get(7), 0, 2);

        assertEquals(8, channels.size());

        channels = mapper.getChannels(Pair.of(STEREO, "fr"));

        assertChannelEquals(channels.get(0), 1, 1);
        assertChannelEquals(channels.get(1), 2, 1);

        assertEquals(2, channels.size());
    }

    @Test
    public void testSurroundByOrderAndStereoFromDescriptor() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR},
                        {FC},
                        {FC},
                        {FL, FR},
                        {FL, FR, FC, LFE, SL, SR}
                },
                new String[]{"fr", "en", "en", "en", "fr"});

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{SURROUND, STEREO}, new String[]{"en", "en"}));

        List<Pair<SequenceUUID, Integer>> channels;
        channels = mapper.getChannels(Pair.of(SURROUND, "en"));

        assertChannelEquals(channels.get(0), 0, 1);
        assertChannelEquals(channels.get(1), 0, 2);
        assertChannelEquals(channels.get(2), 1, 1);
        assertChannelEquals(channels.get(3), 2, 1);
        assertChannelEquals(channels.get(4), 4, 1);
        assertChannelEquals(channels.get(5), 4, 2);

        assertChannelEquals(channels.get(6), 4, 3);
        assertChannelEquals(channels.get(7), 4, 4);

        assertEquals(8, channels.size());

        channels = mapper.getChannels(Pair.of(STEREO, "en"));

        assertChannelEquals(channels.get(0), 3, 1);
        assertChannelEquals(channels.get(1), 3, 2);

        assertEquals(2, channels.size());
    }

    @Test
    public void testSurroundAndStereoByOrder() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR},
                        {FC},
                        {FC},
                        {FL, FR},
                        {FL, FR, FC, LFE, SL, SR}
                },
                new String[]{"fr", "fr", "fr", "fr", "fr"});

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{SURROUND, STEREO}, new String[]{"en", "en"}));

        List<Pair<SequenceUUID, Integer>> channels;
        channels = mapper.getChannels(Pair.of(SURROUND, "en"));

        assertChannelEquals(channels.get(0), 0, 1);
        assertChannelEquals(channels.get(1), 0, 2);
        assertChannelEquals(channels.get(2), 1, 1);
        assertChannelEquals(channels.get(3), 2, 1);
        assertChannelEquals(channels.get(4), 3, 1);
        assertChannelEquals(channels.get(5), 3, 2);

        assertChannelEquals(channels.get(6), 4, 1);
        assertChannelEquals(channels.get(7), 4, 2);

        assertEquals(8, channels.size());

        channels = mapper.getChannels(Pair.of(STEREO, "en"));

        assertChannelEquals(channels.get(0), 4, 3);
        assertChannelEquals(channels.get(1), 4, 4);

        assertEquals(2, channels.size());
    }

    @Test
    public void testSurroundAndStereoNoDescriptor() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(4, 1, 1,
                new FFmpegAudioChannels[][]{
                }
                , null,
                new int[]{6, 1, 1, 4},
                null);

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{SURROUND, STEREO}, new String[]{"en", "en"}));

        List<Pair<SequenceUUID, Integer>> channels;
        channels = mapper.getChannels(Pair.of(SURROUND, "en"));

        assertChannelEquals(channels.get(0), 0, 1);
        assertChannelEquals(channels.get(1), 0, 2);
        assertChannelEquals(channels.get(2), 0, 3);
        assertChannelEquals(channels.get(3), 0, 4);
        assertChannelEquals(channels.get(4), 0, 5);
        assertChannelEquals(channels.get(5), 0, 6);

        assertChannelEquals(channels.get(6), 1, 1);
        assertChannelEquals(channels.get(7), 2, 1);

        assertEquals(8, channels.size());

        channels = mapper.getChannels(Pair.of(STEREO, "en"));

        assertChannelEquals(channels.get(0), 3, 1);
        assertChannelEquals(channels.get(1), 3, 2);

        assertEquals(2, channels.size());
    }

    @Test
    public void testSurroundAndStereoNoDescriptorInsufficientChannels() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(2, 1, 1,
                new FFmpegAudioChannels[][]{
                }
                , null,
                new int[]{4, 2},
                null);

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{SURROUND, STEREO}, new String[]{"en", "en"}));

        List<Pair<SequenceUUID, Integer>> channels;
        channels = mapper.getChannels(Pair.of(SURROUND, "en"));

        assertChannelEquals(channels.get(0), 0, 1);
        assertChannelEquals(channels.get(1), 0, 2);
        assertChannelEquals(channels.get(2), 0, 3);
        assertChannelEquals(channels.get(3), 0, 4);
        assertChannelEquals(channels.get(4), 1, 1);
        assertChannelEquals(channels.get(5), 1, 2);

        assertChannelEquals(channels.get(6), 0, 1);
        assertChannelEquals(channels.get(7), 0, 2);

        assertEquals(8, channels.size());

        assertTrue(mapper.getChannels(Pair.of(STEREO, "en")).isEmpty());
    }

    @Test
    public void testGuessSurroundMainAudio() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR, FC, LFE, SL, SR},
                        {FC},
                        {FC},
                        {FL, FR, FC, LFE, SL, SR}
                },
                new String[]{"fr", "en", "en", "en"});

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);

        List<Pair<SequenceUUID, Integer>> channels = mapper.guessMainAudio("en");

        assertChannelEquals(channels.get(0), 3, 1);
        assertChannelEquals(channels.get(1), 3, 2);
        assertChannelEquals(channels.get(2), 3, 3);
        assertChannelEquals(channels.get(3), 3, 4);
        assertChannelEquals(channels.get(4), 3, 5);
        assertChannelEquals(channels.get(5), 3, 6);

        assertChannelEquals(channels.get(6), 3, 1);
        assertChannelEquals(channels.get(7), 3, 2);

        assertEquals(8, channels.size());
    }

    @Test
    public void testGuessStereoMainAudio() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR},
                        {FC},
                        {FC},
                        {FL, FR}
                },
                new String[]{"fr", "en", "en", "en"});

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);

        List<Pair<SequenceUUID, Integer>> channels = mapper.guessMainAudio("fr");

        assertChannelEquals(channels.get(0), 0, 1);
        assertChannelEquals(channels.get(1), 0, 2);

        assertEquals(2, channels.size());
    }

    @Test
    public void testGuessNoMainAudio() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR},
                        {FC},
                        {FL, FR, FC, LFE, SL, SR},
                        {FC}
                },
                new String[]{"fr", "en", "en", "en"});

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);

        assertTrue(mapper.guessMainAudio("es").isEmpty());
    }

    @Test
    public void testGuessAlternativeAudios() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR},
                        {FL, FR},
                        {FC},
                        {FL, FR}
                },
                new String[]{"fr", "en", "en", "de"});

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);

        Map<String, List<Pair<SequenceUUID, Integer>>> channelsMap = mapper.guessAlternatives("fr");


        assertChannelEquals(channelsMap.get("en").get(0), 1, 1);
        assertChannelEquals(channelsMap.get("en").get(1), 1, 2);

        assertChannelEquals(channelsMap.get("de").get(0), 3, 1);
        assertChannelEquals(channelsMap.get("de").get(1), 3, 2);

        assertEquals(2, channelsMap.keySet().size());
    }

    @Test
    public void testGuessNoAlternativeAudios() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FC},
                        {FL, FR},
                        {FC},
                        {FL, FR, FC, LFE, SL, SR}
                },
                new String[]{"fr", "en", "en", "de"});

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);

        assertTrue(mapper.guessAlternatives("en").isEmpty());
    }

    @Test
    public void testLanguageFoundByDefaultRegion() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR, FC, LFE, SL, SR},
                        {FL, FR},
                        {FC},
                        {FL, FR},
                        {FL, FR, FC, LFE, SL, SR}
                },
                new String[]{"", "fr-CA", "en", "fr", "en-US"});

        ChannelsMapper mapper = new ChannelsMapper(contextProvider);
        mapper.mapChannels(createLayoutOptions(new LayoutType[]{SURROUND, STEREO}, new String[]{"en", "fr-FR"}));

        List<Pair<SequenceUUID, Integer>> channels;
        channels = mapper.getChannels(Pair.of(SURROUND, "en"));

        assertChannelEquals(channels.get(0), 4, 1);
        assertChannelEquals(channels.get(1), 4, 2);
        assertChannelEquals(channels.get(2), 4, 3);
        assertChannelEquals(channels.get(3), 4, 4);
        assertChannelEquals(channels.get(4), 4, 5);
        assertChannelEquals(channels.get(5), 4, 6);

        assertChannelEquals(channels.get(6), 4, 1);
        assertChannelEquals(channels.get(7), 4, 2);

        assertEquals(8, channels.size());

        channels = mapper.getChannels(Pair.of(STEREO, "fr-FR"));

        assertChannelEquals(channels.get(0), 3, 1);
        assertChannelEquals(channels.get(1), 3, 2);

        assertEquals(2, channels.size());
    }

    private static void assertChannelEquals(Pair<SequenceUUID, Integer> channel, Integer audioSeqNum, Integer channelsNum) {
        SequenceUUID seqUuid = getSequenceUuid(audioSeqNum, SequenceType.AUDIO);

        assertEquals(seqUuid, channel.getLeft());
        assertEquals(channelsNum, channel.getRight());
    }

    private static List<Pair<LayoutType, String>> createLayoutOptions(LayoutType[] layoutTypes, String[] langs) {
        List<Pair<LayoutType, String>> options = new ArrayList<>();
        if (layoutTypes == null) {
            return options;
        }
        for (int i = 0; i < layoutTypes.length; i++) {
            options.add(Pair.of(layoutTypes[i], langs[i]));
        }
        return options;
    }
}
