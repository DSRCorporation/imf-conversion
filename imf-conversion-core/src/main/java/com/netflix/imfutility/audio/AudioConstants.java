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

import com.netflix.imfutility.util.FFmpegAudioChannels;

import static com.netflix.imfutility.util.FFmpegAudioChannels.FC;
import static com.netflix.imfutility.util.FFmpegAudioChannels.FL;
import static com.netflix.imfutility.util.FFmpegAudioChannels.FR;
import static com.netflix.imfutility.util.FFmpegAudioChannels.LFE;
import static com.netflix.imfutility.util.FFmpegAudioChannels.SL;
import static com.netflix.imfutility.util.FFmpegAudioChannels.SR;

/**
 * Common constants for audio managing.
 */
public final class AudioConstants {
    private AudioConstants() {
    }

    public static final FFmpegAudioChannels[] MONO_LAYOUT = new FFmpegAudioChannels[]{FC};

    public static final FFmpegAudioChannels[] STEREO_LAYOUT = new FFmpegAudioChannels[]{FL, FR};

    public static final FFmpegAudioChannels[] SURROUND_5_1_LAYOUT = new FFmpegAudioChannels[]{FL, FR, FC, LFE, SL, SR};
}
