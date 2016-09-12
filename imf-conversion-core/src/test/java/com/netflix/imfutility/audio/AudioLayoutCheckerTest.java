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

import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.util.AudioUtils;
import com.netflix.imfutility.util.FFmpegAudioChannels;
import org.junit.Test;

import static com.netflix.imfutility.util.FFmpegAudioChannels.FC;
import static com.netflix.imfutility.util.FFmpegAudioChannels.FL;
import static com.netflix.imfutility.util.FFmpegAudioChannels.FR;
import static com.netflix.imfutility.util.FFmpegAudioChannels.LFE;
import static com.netflix.imfutility.util.FFmpegAudioChannels.SL;
import static com.netflix.imfutility.util.FFmpegAudioChannels.SR;

/**
 * Tests that audio layout checks pass correctly.
 * (see {@link AudioLayoutChecker})
 */
public class AudioLayoutCheckerTest {

    @Test
    public void checkNoAudio() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(0,
                new FFmpegAudioChannels[][]{
                });

        new AudioLayoutChecker(contextProvider).checkCorrectChannelLayout();
    }

    @Test(expected = InvalidAudioChannelAssignmentException.class)
    public void checkNoChannelLayout() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(4,
                new FFmpegAudioChannels[][]{
                });

        new AudioLayoutChecker(contextProvider).checkCorrectChannelLayout();
    }

    @Test(expected = InvalidAudioChannelAssignmentException.class)
    public void check_noChannelLayout_forAll() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(4,
                new FFmpegAudioChannels[][]{
                        {FL, FR, FC, LFE, SL, SR}
                });

        new AudioLayoutChecker(contextProvider).checkCorrectChannelLayout();
    }

    @Test(expected = InvalidAudioChannelAssignmentException.class)
    public void checkChannelLayoutNotMatchChannelNum() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(4, 1, 1,
                new FFmpegAudioChannels[][]{
                        {FL, FR}, {FL, FR}, {FL, FR}, {FL, FR},
                },
                new String[]{},
                new int[]{1, 1, 1, 1},
                new String[]{}
        );

        new AudioLayoutChecker(contextProvider).checkCorrectChannelLayout();
    }


    @Test
    public void checkAllResourcesEqualChannelLayout() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(2, 2, 2,
                new FFmpegAudioChannels[][]{
                        {FL, FR}, {FL, FR}, {FL, FR}, {FL, FR},
                        {FL, FR, FC, LFE, SL, SR}, {FL, FR, FC, LFE, SL, SR}, {FL, FR, FC, LFE, SL, SR}, {FL, FR, FC, LFE, SL, SR}
                });

        new AudioLayoutChecker(contextProvider).checkCorrectChannelLayout();
    }

    @Test(expected = InvalidAudioChannelAssignmentException.class)
    public void checkAllResourcesSwappedChannelLayout() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(2, 2, 2,
                new FFmpegAudioChannels[][]{
                        {FL, FR}, {FR, FL}, {FL, FR}, {FL, FR},
                        {FL, FR, FC, LFE, SL, SR}, {SL, SR, FC, LFE, FL, FR}, {LFE, SL, SR, FL, FR, FC,}, {FC, FR, LFE, SL, SR}
                });

        new AudioLayoutChecker(contextProvider).checkCorrectChannelLayout();
    }

    @Test(expected = InvalidAudioChannelAssignmentException.class)
    public void checkAllResourcesDifferentChannelLayout() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(2, 2, 2,
                new FFmpegAudioChannels[][]{
                        {FL, FR}, {FL}, {FL, FR}, {FL, FR},
                        {FL, FR, FC, LFE, SL, SR}, {SL, SR, FC, LFE, FL, FR}, {LFE, SL, SR, FL, FR, FC,}, {FC}
                });

        new AudioLayoutChecker(contextProvider).checkCorrectChannelLayout();
    }
}
