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
package com.netflix.imfutility.dpp.audio;

import com.netflix.imfutility.ImfUtilityTest;
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
import com.netflix.imfutility.generated.dpp.audiomap.AudioMapType;
import com.netflix.imfutility.util.FFmpegAudioChannels;
import org.junit.Assert;
import org.junit.Test;

import java.util.EnumSet;

import static com.netflix.imfutility.generated.dpp.metadata.AudioTrackLayoutDmAs11Type.EBU_R_123_16_C;
import static com.netflix.imfutility.generated.dpp.metadata.AudioTrackLayoutDmAs11Type.EBU_R_123_16_D;
import static com.netflix.imfutility.generated.dpp.metadata.AudioTrackLayoutDmAs11Type.EBU_R_123_16_F;
import static com.netflix.imfutility.generated.dpp.metadata.AudioTrackLayoutDmAs11Type.EBU_R_123_4_B;
import static com.netflix.imfutility.generated.dpp.metadata.AudioTrackLayoutDmAs11Type.EBU_R_123_4_C;
import static com.netflix.imfutility.generated.dpp.metadata.AudioTrackLayoutDmAs11Type.EBU_R_48_2_A;
import static com.netflix.imfutility.util.FFmpegAudioChannels.FC;
import static com.netflix.imfutility.util.FFmpegAudioChannels.FL;
import static com.netflix.imfutility.util.FFmpegAudioChannels.FR;
import static com.netflix.imfutility.util.FFmpegAudioChannels.LFE;
import static com.netflix.imfutility.util.FFmpegAudioChannels.SL;
import static com.netflix.imfutility.util.FFmpegAudioChannels.SR;
import static com.netflix.imfutility.util.TemplateParameterContextCreator.createDefaultContextProviderWithCPLContext;
import static com.netflix.imfutility.util.TemplateParameterContextCreator.getSequenceUuid;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;


/**
 * <ul>
 * <li>Tests the audiomap.xml can be guessed from EssenceDescriptors correctly.</li>
 * </ul>
 */
public class AudioMapGuesserTest extends ImfUtilityTest {

    @Test
    public void check_noAudio() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(0,
                new FFmpegAudioChannels[][]{
                });

        AudioMapType audioMap = new AudioMapGuesser(contextProvider, EBU_R_123_16_C).guessAudioMap();
        assertNull(audioMap);
    }

    @Test(expected = InvalidAudioChannelAssignmentException.class)
    public void check_noChannelLayout() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(4,
                new FFmpegAudioChannels[][]{
                });

        new AudioMapGuesser(contextProvider, EBU_R_123_16_C).checkCorrectChannelLayout();
    }

    @Test(expected = InvalidAudioChannelAssignmentException.class)
    public void check_noChannelLayout_forAll() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(4,
                new FFmpegAudioChannels[][]{
                        {FL, FR, FC, LFE, SL, SR}
                });

        new AudioMapGuesser(contextProvider, EBU_R_123_16_C).checkCorrectChannelLayout();
    }

    @Test
    public void check_allResourcesEqualChannelLayout() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(2, 2, 2,
                new FFmpegAudioChannels[][]{
                        {FL, FR}, {FL, FR}, {FL, FR}, {FL, FR},
                        {FL, FR, FC, LFE, SL, SR}, {FL, FR, FC, LFE, SL, SR}, {FL, FR, FC, LFE, SL, SR}, {FL, FR, FC, LFE, SL, SR}
                });

        new AudioMapGuesser(contextProvider, EBU_R_123_16_C).checkCorrectChannelLayout();
    }

    @Test(expected = InvalidAudioChannelAssignmentException.class)
    public void check_allResourcesSwappedChannelLayout() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(2, 2, 2,
                new FFmpegAudioChannels[][]{
                        {FL, FR}, {FR, FL}, {FL, FR}, {FL, FR},
                        {FL, FR, FC, LFE, SL, SR}, {SL, SR, FC, LFE, FL, FR}, {LFE, SL, SR, FL, FR, FC,}, {FC, FR, LFE, SL, SR}
                });

        new AudioMapGuesser(contextProvider, EBU_R_123_16_C).checkCorrectChannelLayout();
    }

    @Test(expected = InvalidAudioChannelAssignmentException.class)
    public void check_allResourcesDifferentChannelLayout() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(2, 2, 2,
                new FFmpegAudioChannels[][]{
                        {FL, FR}, {FL}, {FL, FR}, {FL, FR},
                        {FL, FR, FC, LFE, SL, SR}, {SL, SR, FC, LFE, FL, FR}, {LFE, SL, SR, FL, FR, FC,}, {FC}
                });

        new AudioMapGuesser(contextProvider, EBU_R_123_16_C).checkCorrectChannelLayout();
    }

    @Test
    public void guess2A_oneStereo() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR}
                });

        AudioMapType audioMap = new AudioMapGuesser(contextProvider, EBU_R_48_2_A).guessAudioMap();

        checkAudioMapSize(audioMap, 4);
        checkEBUTrack(audioMap, 0, 0, 1);
        checkEBUTrack(audioMap, 1, 0, 2);
        checkEBUTrackSilence(audioMap, 2);
        checkEBUTrackSilence(audioMap, 3);
    }

    @Test
    public void guess2A_oneStereo_and_other() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FC}, {FL, FR, FC, LFE, SL, SR}, {FL, FR}
                });

        AudioMapType audioMap = new AudioMapGuesser(contextProvider, EBU_R_48_2_A).guessAudioMap();

        checkAudioMapSize(audioMap, 4);
        checkEBUTrack(audioMap, 0, 2, 1);
        checkEBUTrack(audioMap, 1, 2, 2);
        checkEBUTrackSilence(audioMap, 2);
        checkEBUTrackSilence(audioMap, 3);
    }

    @Test
    public void guess2A_swapChannel() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FR, FL}
                });

        AudioMapType audioMap = new AudioMapGuesser(contextProvider, EBU_R_48_2_A).guessAudioMap();

        checkAudioMapSize(audioMap, 4);
        checkEBUTrack(audioMap, 0, 0, 2);
        checkEBUTrack(audioMap, 1, 0, 1);
        checkEBUTrackSilence(audioMap, 2);
        checkEBUTrackSilence(audioMap, 3);
    }

    @Test
    public void guess2A_twoStereo() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR}, {FR, FL}
                });

        AudioMapType audioMap = new AudioMapGuesser(contextProvider, EBU_R_48_2_A).guessAudioMap();

        checkAudioMapSize(audioMap, 4);
        checkEBUTrack(audioMap, 0, 0, 1);
        checkEBUTrack(audioMap, 1, 0, 2);
        checkEBUTrackSilence(audioMap, 2);
        checkEBUTrackSilence(audioMap, 3);
    }

    @Test(expected = InvalidAudioChannelAssignmentException.class)
    public void guess2A_noStereo() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR, FC, LFE, SL, SR}
                });

        new AudioMapGuesser(contextProvider, EBU_R_48_2_A).guessAudioMap();
    }


    @Test
    public void guess4B_twoStereo() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR}, {FR, FL}
                });

        AudioMapType audioMap = new AudioMapGuesser(contextProvider, EBU_R_123_4_B).guessAudioMap();

        checkAudioMapSize(audioMap, 4);
        checkEBUTrack(audioMap, 0, 0, 1);
        checkEBUTrack(audioMap, 1, 0, 2);
        checkEBUTrack(audioMap, 2, 1, 2);
        checkEBUTrack(audioMap, 3, 1, 1);
    }

    @Test
    public void guess4C_twoStereo() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR}, {FR, FL}
                });

        AudioMapType audioMap = new AudioMapGuesser(contextProvider, EBU_R_123_4_C).guessAudioMap();

        checkAudioMapSize(audioMap, 4);
        checkEBUTrack(audioMap, 0, 0, 1);
        checkEBUTrack(audioMap, 1, 0, 2);
        checkEBUTrack(audioMap, 2, 1, 2);
        checkEBUTrack(audioMap, 3, 1, 1);
    }


    @Test
    public void guess4B_twoStereo_and_other() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FC}, {FL, FR, FC, LFE, SL, SR}, {FL, FR}, {FR, FL}
                });

        AudioMapType audioMap = new AudioMapGuesser(contextProvider, EBU_R_123_4_B).guessAudioMap();

        checkAudioMapSize(audioMap, 4);
        checkEBUTrack(audioMap, 0, 2, 1);
        checkEBUTrack(audioMap, 1, 2, 2);
        checkEBUTrack(audioMap, 2, 3, 2);
        checkEBUTrack(audioMap, 3, 3, 1);
    }

    @Test
    public void guess4B_oneStereo() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FR, FL}
                });

        AudioMapType audioMap = new AudioMapGuesser(contextProvider, EBU_R_123_4_B).guessAudioMap();

        checkAudioMapSize(audioMap, 4);
        checkEBUTrack(audioMap, 0, 0, 2);
        checkEBUTrack(audioMap, 1, 0, 1);
        checkEBUTrackSilence(audioMap, 2);
        checkEBUTrackSilence(audioMap, 3);
    }

    @Test(expected = InvalidAudioChannelAssignmentException.class)
    public void guess4BC_noStereo() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR, FC, LFE, SL, SR}
                });

        new AudioMapGuesser(contextProvider, EBU_R_123_4_B).guessAudioMap();
    }

    @Test(expected = InvalidAudioChannelAssignmentException.class)
    public void guess4BC_threeStereo() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR}, {FL, FR}, {FL, FR}
                });

        new AudioMapGuesser(contextProvider, EBU_R_123_4_B).guessAudioMap();
    }

    @Test
    public void guess16C_twoStereo_two51() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR}, {FL, FR}, {FL, FR, FC, LFE, SL, SR}, {FL, FR, FC, LFE, SL, SR}
                });

        AudioMapType audioMap = new AudioMapGuesser(contextProvider, EBU_R_123_16_C).guessAudioMap();

        checkAudioMapSize(audioMap, 16);
        checkEBUTrack(audioMap, 0, 0, 1);
        checkEBUTrack(audioMap, 1, 0, 2);


        checkEBUTrack(audioMap, 2, 1, 1);
        checkEBUTrack(audioMap, 3, 1, 2);

        checkEBUTrack(audioMap, 4, 2, 1);
        checkEBUTrack(audioMap, 5, 2, 2);
        checkEBUTrack(audioMap, 6, 2, 3);
        checkEBUTrack(audioMap, 7, 2, 4);
        checkEBUTrack(audioMap, 8, 2, 5);
        checkEBUTrack(audioMap, 9, 2, 6);

        checkEBUTrack(audioMap, 10, 3, 1);
        checkEBUTrack(audioMap, 11, 3, 2);
        checkEBUTrack(audioMap, 12, 3, 3);
        checkEBUTrack(audioMap, 13, 3, 4);
        checkEBUTrack(audioMap, 14, 3, 5);
        checkEBUTrack(audioMap, 15, 3, 6);
    }

    @Test
    public void guess16C_twoStereo_two51_swapChannels() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {SL, SR, LFE, FC, FL, FR}, {FR, FL, SR, SL, FC, LFE}, {FL, FR}, {FR, FL}
                });

        AudioMapType audioMap = new AudioMapGuesser(contextProvider, EBU_R_123_16_C).guessAudioMap();

        checkAudioMapSize(audioMap, 16);
        checkEBUTrack(audioMap, 0, 2, 1);
        checkEBUTrack(audioMap, 1, 2, 2);


        checkEBUTrack(audioMap, 2, 3, 2);
        checkEBUTrack(audioMap, 3, 3, 1);

        checkEBUTrack(audioMap, 4, 0, 5);
        checkEBUTrack(audioMap, 5, 0, 6);
        checkEBUTrack(audioMap, 6, 0, 4);
        checkEBUTrack(audioMap, 7, 0, 3);
        checkEBUTrack(audioMap, 8, 0, 1);
        checkEBUTrack(audioMap, 9, 0, 2);

        checkEBUTrack(audioMap, 10, 1, 2);
        checkEBUTrack(audioMap, 11, 1, 1);
        checkEBUTrack(audioMap, 12, 1, 5);
        checkEBUTrack(audioMap, 13, 1, 6);
        checkEBUTrack(audioMap, 14, 1, 4);
        checkEBUTrack(audioMap, 15, 1, 3);
    }

    @Test
    public void guess16C_oneStereo_one51() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR}, {FL, FR, FC, LFE, SL, SR}
                });

        AudioMapType audioMap = new AudioMapGuesser(contextProvider, EBU_R_123_16_C).guessAudioMap();

        checkAudioMapSize(audioMap, 16);
        checkEBUTrack(audioMap, 0, 0, 1);
        checkEBUTrack(audioMap, 1, 0, 2);

        checkEBUTrackSilence(audioMap, 2);
        checkEBUTrackSilence(audioMap, 3);

        checkEBUTrack(audioMap, 4, 1, 1);
        checkEBUTrack(audioMap, 5, 1, 2);
        checkEBUTrack(audioMap, 6, 1, 3);
        checkEBUTrack(audioMap, 7, 1, 4);
        checkEBUTrack(audioMap, 8, 1, 5);
        checkEBUTrack(audioMap, 9, 1, 6);

        checkEBUTrackSilence(audioMap, 10);
        checkEBUTrackSilence(audioMap, 11);
        checkEBUTrackSilence(audioMap, 12);
        checkEBUTrackSilence(audioMap, 13);
        checkEBUTrackSilence(audioMap, 14);
        checkEBUTrackSilence(audioMap, 15);
    }

    @Test
    public void guess16C_twoStereo_one51() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR}, {FL, FR}, {FL, FR, FC, LFE, SL, SR}
                });

        AudioMapType audioMap = new AudioMapGuesser(contextProvider, EBU_R_123_16_C).guessAudioMap();

        checkAudioMapSize(audioMap, 16);
        checkEBUTrack(audioMap, 0, 0, 1);
        checkEBUTrack(audioMap, 1, 0, 2);

        checkEBUTrackSilence(audioMap, 2);
        checkEBUTrackSilence(audioMap, 3);

        checkEBUTrack(audioMap, 4, 2, 1);
        checkEBUTrack(audioMap, 5, 2, 2);
        checkEBUTrack(audioMap, 6, 2, 3);
        checkEBUTrack(audioMap, 7, 2, 4);
        checkEBUTrack(audioMap, 8, 2, 5);
        checkEBUTrack(audioMap, 9, 2, 6);

        checkEBUTrackSilence(audioMap, 10);
        checkEBUTrackSilence(audioMap, 11);
        checkEBUTrackSilence(audioMap, 12);
        checkEBUTrackSilence(audioMap, 13);
        checkEBUTrackSilence(audioMap, 14);
        checkEBUTrackSilence(audioMap, 15);
    }

    @Test
    public void guess16C_oneStereo_two51() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR}, {FL, FR, FC, LFE, SL, SR}, {FL, FR, FC, LFE, SL, SR}
                });

        AudioMapType audioMap = new AudioMapGuesser(contextProvider, EBU_R_123_16_C).guessAudioMap();

        checkAudioMapSize(audioMap, 16);
        checkEBUTrack(audioMap, 0, 0, 1);
        checkEBUTrack(audioMap, 1, 0, 2);

        checkEBUTrackSilence(audioMap, 2);
        checkEBUTrackSilence(audioMap, 3);

        checkEBUTrack(audioMap, 4, 1, 1);
        checkEBUTrack(audioMap, 5, 1, 2);
        checkEBUTrack(audioMap, 6, 1, 3);
        checkEBUTrack(audioMap, 7, 1, 4);
        checkEBUTrack(audioMap, 8, 1, 5);
        checkEBUTrack(audioMap, 9, 1, 6);

        checkEBUTrackSilence(audioMap, 10);
        checkEBUTrackSilence(audioMap, 11);
        checkEBUTrackSilence(audioMap, 12);
        checkEBUTrackSilence(audioMap, 13);
        checkEBUTrackSilence(audioMap, 14);
        checkEBUTrackSilence(audioMap, 15);
    }

    @Test
    public void guess16C_twoStereo_two51_and_other() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FC}, {FC, FL, FR},
                        {FL, FR}, {SL, SR}, {FL, FR},
                        {FL, FR, FC, LFE, SL, SR}, {SL, SR}, {FL, FR, FC, LFE, SL, SR}
                });

        AudioMapType audioMap = new AudioMapGuesser(contextProvider, EBU_R_123_16_C).guessAudioMap();

        checkAudioMapSize(audioMap, 16);
        checkEBUTrack(audioMap, 0, 2, 1);
        checkEBUTrack(audioMap, 1, 2, 2);

        checkEBUTrack(audioMap, 2, 4, 1);
        checkEBUTrack(audioMap, 3, 4, 2);

        checkEBUTrack(audioMap, 4, 5, 1);
        checkEBUTrack(audioMap, 5, 5, 2);
        checkEBUTrack(audioMap, 6, 5, 3);
        checkEBUTrack(audioMap, 7, 5, 4);
        checkEBUTrack(audioMap, 8, 5, 5);
        checkEBUTrack(audioMap, 9, 5, 6);

        checkEBUTrack(audioMap, 10, 7, 1);
        checkEBUTrack(audioMap, 11, 7, 2);
        checkEBUTrack(audioMap, 12, 7, 3);
        checkEBUTrack(audioMap, 13, 7, 4);
        checkEBUTrack(audioMap, 14, 7, 5);
        checkEBUTrack(audioMap, 15, 7, 6);
    }


    @Test(expected = InvalidAudioChannelAssignmentException.class)
    public void guess16C_noStereo() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR, FC, LFE, SL, SR}, {FL, FR, FC, LFE, SL, SR}
                });

        new AudioMapGuesser(contextProvider, EBU_R_123_16_C).guessAudioMap();
    }

    @Test(expected = InvalidAudioChannelAssignmentException.class)
    public void guess16C_no51() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR}, {FL, FR}
                });

        new AudioMapGuesser(contextProvider, EBU_R_123_16_C).guessAudioMap();
    }

    @Test
    public void guess16D_two51_diffLang() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR, FC, LFE, SL, SR}, {FL, FR, FC, LFE, SL, SR}
                },
                new String[]{"en", "de"}
        );

        AudioMapType audioMap = new AudioMapGuesser(contextProvider, EBU_R_123_16_D).guessAudioMap();

        checkAudioMapSize(audioMap, 16);

        checkEBUTrack(audioMap, 0, 0, 1);
        checkEBUTrack(audioMap, 1, 0, 2);
        checkEBUTrack(audioMap, 2, 0, 3);
        checkEBUTrack(audioMap, 3, 0, 4);
        checkEBUTrack(audioMap, 4, 0, 5);
        checkEBUTrack(audioMap, 5, 0, 6);

        checkEBUTrackSilence(audioMap, 6);
        checkEBUTrackSilence(audioMap, 7);

        checkEBUTrack(audioMap, 8, 1, 1);
        checkEBUTrack(audioMap, 9, 1, 2);
        checkEBUTrack(audioMap, 10, 1, 3);
        checkEBUTrack(audioMap, 11, 1, 4);
        checkEBUTrack(audioMap, 12, 1, 5);
        checkEBUTrack(audioMap, 13, 1, 6);

        checkEBUTrackSilence(audioMap, 14);
        checkEBUTrackSilence(audioMap, 15);
    }

    @Test(expected = InvalidAudioChannelAssignmentException.class)
    public void guess16D_two51_sameLang() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR, FC, LFE, SL, SR}, {FL, FR, FC, LFE, SL, SR}
                },
                new String[]{"en", "en"}
        );

        new AudioMapGuesser(contextProvider, EBU_R_123_16_D).guessAudioMap();
    }

    @Test(expected = InvalidAudioChannelAssignmentException.class)
    public void guess16D_two51_noLang() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR, FC, LFE, SL, SR}, {FL, FR, FC, LFE, SL, SR}
                }
        );

        new AudioMapGuesser(contextProvider, EBU_R_123_16_D).guessAudioMap();
    }

    @Test(expected = InvalidAudioChannelAssignmentException.class)
    public void guess16D_two51_oneLang() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR, FC, LFE, SL, SR}, {FL, FR, FC, LFE, SL, SR}
                },
                new String[]{"en"}
        );

        new AudioMapGuesser(contextProvider, EBU_R_123_16_D).guessAudioMap();
    }

    @Test(expected = InvalidAudioChannelAssignmentException.class)
    public void guess16D_one51() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR, FC, LFE, SL, SR}
                },
                new String[]{"en"}
        );

        new AudioMapGuesser(contextProvider, EBU_R_123_16_D).guessAudioMap();
    }

    @Test(expected = InvalidAudioChannelAssignmentException.class)
    public void guess16D_three51() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR, FC, LFE, SL, SR}, {FL, FR, FC, LFE, SL, SR}, {FL, FR, FC, LFE, SL, SR}
                },
                new String[]{"en", "de", "ru"}
        );

        new AudioMapGuesser(contextProvider, EBU_R_123_16_D).guessAudioMap();
    }

    @Test
    public void guess16F_threeStereo_diffLang() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR}, {FL, FR}, {FL, FR}
                },
                new String[]{"en", "de", "ru"}
        );

        AudioMapType audioMap = new AudioMapGuesser(contextProvider, EBU_R_123_16_F).guessAudioMap();

        checkAudioMapSize(audioMap, 16);

        checkEBUTrack(audioMap, 0, 0, 1);
        checkEBUTrack(audioMap, 1, 0, 2);
        checkEBUTrackSilence(audioMap, 2);
        checkEBUTrackSilence(audioMap, 3);

        checkEBUTrack(audioMap, 4, 1, 1);
        checkEBUTrack(audioMap, 5, 1, 2);
        checkEBUTrackSilence(audioMap, 6);
        checkEBUTrackSilence(audioMap, 7);

        checkEBUTrack(audioMap, 8, 2, 1);
        checkEBUTrack(audioMap, 9, 2, 2);
        checkEBUTrackSilence(audioMap, 10);
        checkEBUTrackSilence(audioMap, 11);

        checkEBUTrackSilence(audioMap, 12);
        checkEBUTrackSilence(audioMap, 13);
        checkEBUTrackSilence(audioMap, 14);
        checkEBUTrackSilence(audioMap, 15);
    }

    @Test(expected = InvalidAudioChannelAssignmentException.class)
    public void guess16F_threeStereo_sameLang_12() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR}, {FL, FR}, {FL, FR}
                },
                new String[]{"en", "en", "ru"}
        );

        new AudioMapGuesser(contextProvider, EBU_R_123_16_F).guessAudioMap();
    }

    @Test(expected = InvalidAudioChannelAssignmentException.class)
    public void guess16F_threeStereo_sameLang_23() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR}, {FL, FR}, {FL, FR}
                },
                new String[]{"en", "ru", "ru"}
        );

        new AudioMapGuesser(contextProvider, EBU_R_123_16_F).guessAudioMap();
    }

    @Test(expected = InvalidAudioChannelAssignmentException.class)
    public void guess16F_threeStereo_sameLang_13() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR}, {FL, FR}, {FL, FR}
                },
                new String[]{"en", "ru", "en"}
        );

        new AudioMapGuesser(contextProvider, EBU_R_123_16_F).guessAudioMap();
    }

    @Test(expected = InvalidAudioChannelAssignmentException.class)
    public void guess16F_twoStereo_diffLang() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR}, {FL, FR}
                },
                new String[]{"en", "ru"}
        );

        new AudioMapGuesser(contextProvider, EBU_R_123_16_F).guessAudioMap();
    }

    @Test(expected = InvalidAudioChannelAssignmentException.class)
    public void guess16F_fourStereo_diffLang() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR}, {FL, FR}, {FL, FR}, {FL, FR}
                },
                new String[]{"en", "ru", "de", "it"}
        );

        new AudioMapGuesser(contextProvider, EBU_R_123_16_F).guessAudioMap();
    }

    @Test
    public void guess16C_twoStereo_two51_multipleResources() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(4, 2, 2,
                new FFmpegAudioChannels[][]{
                        {FL, FR}, {FL, FR}, {FL, FR}, {FL, FR},
                        {FL, FR}, {FL, FR}, {FL, FR}, {FL, FR},
                        {FL, FR, FC, LFE, SL, SR}, {FL, FR, FC, LFE, SL, SR}, {FL, FR, FC, LFE, SL, SR}, {FL, FR, FC, LFE, SL, SR},
                        {FL, FR, FC, LFE, SL, SR}, {FL, FR, FC, LFE, SL, SR}, {FL, FR, FC, LFE, SL, SR}, {FL, FR, FC, LFE, SL, SR}
                });

        AudioMapType audioMap = new AudioMapGuesser(contextProvider, EBU_R_123_16_C).guessAudioMap();

        checkAudioMapSize(audioMap, 16);
        checkEBUTrack(audioMap, 0, 0, 1);
        checkEBUTrack(audioMap, 1, 0, 2);


        checkEBUTrack(audioMap, 2, 1, 1);
        checkEBUTrack(audioMap, 3, 1, 2);

        checkEBUTrack(audioMap, 4, 2, 1);
        checkEBUTrack(audioMap, 5, 2, 2);
        checkEBUTrack(audioMap, 6, 2, 3);
        checkEBUTrack(audioMap, 7, 2, 4);
        checkEBUTrack(audioMap, 8, 2, 5);
        checkEBUTrack(audioMap, 9, 2, 6);

        checkEBUTrack(audioMap, 10, 3, 1);
        checkEBUTrack(audioMap, 11, 3, 2);
        checkEBUTrack(audioMap, 12, 3, 3);
        checkEBUTrack(audioMap, 13, 3, 4);
        checkEBUTrack(audioMap, 14, 3, 5);
        checkEBUTrack(audioMap, 15, 3, 6);
    }

    @Test
    public void guess16C_twoStereo_two51_soundfieldGroupsSingleChannel() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(16, 1, 1,
                new FFmpegAudioChannels[][]{
                        {FR}, {FL},
                        {FR}, {FL},
                        {FC}, {LFE}, {SL}, {SR}, {FL}, {FR},
                        {FC}, {LFE}, {SL}, {SR}, {FL}, {FR}
                },
                new String[]{
                        "1", "1",
                        "2", "2",
                        "3", "3", "3", "3", "3", "3",
                        "4", "4", "4", "4", "4", "4"
                },
                new String[]{}
        );

        AudioMapType audioMap = new AudioMapGuesser(contextProvider, EBU_R_123_16_C).guessAudioMap();

        checkAudioMapSize(audioMap, 16);
        checkEBUTrack(audioMap, 0, 1, 1);
        checkEBUTrack(audioMap, 1, 0, 1);


        checkEBUTrack(audioMap, 2, 3, 1);
        checkEBUTrack(audioMap, 3, 2, 1);

        checkEBUTrack(audioMap, 4, 8, 1);
        checkEBUTrack(audioMap, 5, 9, 1);
        checkEBUTrack(audioMap, 6, 4, 1);
        checkEBUTrack(audioMap, 7, 5, 1);
        checkEBUTrack(audioMap, 8, 6, 1);
        checkEBUTrack(audioMap, 9, 7, 1);

        checkEBUTrack(audioMap, 10, 14, 1);
        checkEBUTrack(audioMap, 11, 15, 1);
        checkEBUTrack(audioMap, 12, 10, 1);
        checkEBUTrack(audioMap, 13, 11, 1);
        checkEBUTrack(audioMap, 14, 12, 1);
        checkEBUTrack(audioMap, 15, 13, 1);
    }

    @Test
    public void guess16C_twoStereo_two51_soundfieldGroupsMultipleChannel() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(8, 1, 1,
                new FFmpegAudioChannels[][]{
                        {FR}, {FL},
                        {FR}, {FL},
                        {FC, LFE, SL, SR}, {FL, FR},
                        {FC, LFE, SL, SR}, {FL, FR}
                },
                new String[]{
                        "1", "1",
                        "2", "2",
                        "3", "3",
                        "4", "4",
                },
                new String[]{}
        );

        AudioMapType audioMap = new AudioMapGuesser(contextProvider, EBU_R_123_16_C).guessAudioMap();

        checkAudioMapSize(audioMap, 16);
        checkEBUTrack(audioMap, 0, 1, 1);
        checkEBUTrack(audioMap, 1, 0, 1);


        checkEBUTrack(audioMap, 2, 3, 1);
        checkEBUTrack(audioMap, 3, 2, 1);

        checkEBUTrack(audioMap, 4, 5, 1);
        checkEBUTrack(audioMap, 5, 5, 2);
        checkEBUTrack(audioMap, 6, 4, 1);
        checkEBUTrack(audioMap, 7, 4, 2);
        checkEBUTrack(audioMap, 8, 4, 3);
        checkEBUTrack(audioMap, 9, 4, 4);

        checkEBUTrack(audioMap, 10, 7, 1);
        checkEBUTrack(audioMap, 11, 7, 2);
        checkEBUTrack(audioMap, 12, 6, 1);
        checkEBUTrack(audioMap, 13, 6, 2);
        checkEBUTrack(audioMap, 14, 6, 3);
        checkEBUTrack(audioMap, 15, 6, 4);
    }

    @Test
    public void guess16C_twoStereo_two51_soundfieldGroupsWithEmpty() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(8, 1, 2,
                new FFmpegAudioChannels[][]{
                        {FR}, {FR}, {FL}, {FL},
                        {FR}, {FR}, {FL}, {FL},
                        {FC, LFE, SL, SR}, {FC, LFE, SL, SR}, {FL, FR}, {FL, FR},
                        {FC, LFE, SL, SR}, {FC, LFE, SL, SR}, {FL, FR}, {FL, FR}
                },
                new String[]{
                        "1", null, "1", null,
                        "2", null, "2", null,
                        "3", null, "3", null,
                        "4", "4", "4", "4"
                },
                new String[]{}
        );

        AudioMapType audioMap = new AudioMapGuesser(contextProvider, EBU_R_123_16_C).guessAudioMap();

        checkAudioMapSize(audioMap, 16);
        checkEBUTrack(audioMap, 0, 1, 1);
        checkEBUTrack(audioMap, 1, 0, 1);


        checkEBUTrack(audioMap, 2, 3, 1);
        checkEBUTrack(audioMap, 3, 2, 1);

        checkEBUTrack(audioMap, 4, 5, 1);
        checkEBUTrack(audioMap, 5, 5, 2);
        checkEBUTrack(audioMap, 6, 4, 1);
        checkEBUTrack(audioMap, 7, 4, 2);
        checkEBUTrack(audioMap, 8, 4, 3);
        checkEBUTrack(audioMap, 9, 4, 4);

        checkEBUTrack(audioMap, 10, 7, 1);
        checkEBUTrack(audioMap, 11, 7, 2);
        checkEBUTrack(audioMap, 12, 6, 1);
        checkEBUTrack(audioMap, 13, 6, 2);
        checkEBUTrack(audioMap, 14, 6, 3);
        checkEBUTrack(audioMap, 15, 6, 4);
    }

    @Test(expected = InvalidAudioChannelAssignmentException.class)
    public void guess16C_twoStereo_two51_noSoundfieldGroups() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(16, 1, 1,
                new FFmpegAudioChannels[][]{
                        {FR}, {FL},
                        {FR}, {FL},
                        {FC}, {LFE}, {SL}, {SR}, {FL}, {FR},
                        {FC}, {LFE}, {SL}, {SR}, {FL}, {FR}
                },
                new String[]{
                },
                new String[]{}
        );

        new AudioMapGuesser(contextProvider, EBU_R_123_16_C).guessAudioMap();
    }

    @Test(expected = InvalidAudioChannelAssignmentException.class)
    public void guess16C_oneStereo_one51_invalidSoundfieldGroups() throws Exception {
        TemplateParameterContextProvider contextProvider = createContext(8, 1, 1,
                new FFmpegAudioChannels[][]{
                        {FR}, {FL},
                        {FC}, {LFE}, {SL}, {SR}, {FL}, {FR}
                },
                new String[]{
                        "1", "11",
                        "3", "3", "3", "3", "3", "3"
                },
                new String[]{}
        );

        new AudioMapGuesser(contextProvider, EBU_R_123_16_C).guessAudioMap();
    }

    private TemplateParameterContextProvider createContext(FFmpegAudioChannels[][] channelsForSeq)
            throws Exception {
        return createContext(channelsForSeq, null);
    }

    private TemplateParameterContextProvider createContext(FFmpegAudioChannels[][] channelsForSeq,
                                                           String[] langs) throws Exception {
        return createContext(channelsForSeq.length, channelsForSeq, langs);
    }

    private TemplateParameterContextProvider createContext(int audioSeqCount,
                                                           FFmpegAudioChannels[][] channelsForSeq) throws Exception {
        return createContext(audioSeqCount, channelsForSeq, null);
    }

    private TemplateParameterContextProvider createContext(int audioSeqCount, FFmpegAudioChannels[][] channelsForSeq,
                                                           String[] langs) throws Exception {
        return createContext(audioSeqCount, 1, 1, channelsForSeq, langs);
    }

    private TemplateParameterContextProvider createContext(int audioSeqCount, int segmCount, int resourceCount,
                                                           FFmpegAudioChannels[][] channelsForSeq) throws Exception {
        return createContext(audioSeqCount, segmCount, resourceCount, channelsForSeq, null);
    }

    private TemplateParameterContextProvider createContext(int audioSeqCount, int segmCount, int resourceCount,
                                                           FFmpegAudioChannels[][] channelsForSeq,
                                                           String[] langs) throws Exception {
        return createContext(audioSeqCount, segmCount, resourceCount, channelsForSeq, null, langs);
    }

    private TemplateParameterContextProvider createContext(int audioSeqCount, int segmCount, int resourceCount,
                                                           FFmpegAudioChannels[][] channelsForSeq,
                                                           String[] soundfieldGroups,
                                                           String[] langs) throws Exception {
        TemplateParameterContextProvider contextProvider =
                createDefaultContextProviderWithCPLContext(segmCount, audioSeqCount, resourceCount, EnumSet.of(SequenceType.AUDIO));
        SequenceTemplateParameterContext sequenceContext = contextProvider.getSequenceContext();
        ResourceTemplateParameterContext resourceContext = contextProvider.getResourceContext();

        int i = 0;
        int j = 0;
        for (SequenceUUID seqUuid : sequenceContext.getUuids(SequenceType.AUDIO)) {
            sequenceContext.addSequenceParameter(
                    SequenceType.AUDIO, seqUuid,
                    SequenceContextParameters.CHANNELS_NUM, String.valueOf(audioSeqCount));
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
                    j++;
                }
                i++;
            }
        }

        return contextProvider;
    }

    private void checkAudioMapSize(AudioMapType audioMap, int trackCount) {
        assertNotNull(audioMap);
        Assert.assertNotNull(audioMap.getEBUTrack());
        assertEquals(trackCount, audioMap.getEBUTrack().size());
    }

    private void checkEBUTrack(AudioMapType audioMap, int trackNum, int audioSeqNum, Integer channelsNum) {
        assertEquals(trackNum + 1, audioMap.getEBUTrack().get(trackNum).getNumber());
        assertEquals(getSequenceUuid(audioSeqNum, SequenceType.AUDIO).getUuid(),
                audioMap.getEBUTrack().get(trackNum).getCPLVirtualTrackId());
        assertEquals(channelsNum, audioMap.getEBUTrack().get(trackNum).getCPLVirtualTrackChannel());
    }

    private void checkEBUTrackSilence(AudioMapType audioMap, int trackNum) {
        assertEquals(trackNum + 1, audioMap.getEBUTrack().get(trackNum).getNumber());
        assertNull(audioMap.getEBUTrack().get(trackNum).getCPLVirtualTrackId());
        assertNull(audioMap.getEBUTrack().get(trackNum).getCPLVirtualTrackChannel());
    }


}
