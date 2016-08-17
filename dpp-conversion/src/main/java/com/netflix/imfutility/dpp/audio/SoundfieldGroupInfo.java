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

import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.util.FFmpegAudioChannels;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.HashMap;
import java.util.Map;

/**
 * A soundfield group as defined by an Essence Descriptor.
 * Maps each channel in a group to an appropriate channel in an audio sequence.
 */
final class SoundfieldGroupInfo {

    private final Map<FFmpegAudioChannels, ImmutablePair<SequenceUUID, Integer>> channelsMap = new HashMap<>();

    public void addChannels(SequenceUUID uuid, String channels) throws InvalidAudioChannelAssignmentException {
        addChannels(uuid, FFmpegAudioChannels.toFFmpegAudioChannels(channels));
    }

    public void addChannels(SequenceUUID uuid, FFmpegAudioChannels[] channels)
            throws InvalidAudioChannelAssignmentException {
        for (int i = 0; i < channels.length; i++) {
            FFmpegAudioChannels channel = channels[i];
            if (channelsMap.containsKey(channel)) {
                throw new InvalidAudioChannelAssignmentException("A soundfield group must contain different channels");
            }
            channelsMap.put(channel, ImmutablePair.of(uuid, i + 1));
        }
    }

    public Map<FFmpegAudioChannels, ImmutablePair<SequenceUUID, Integer>> getChannelsMap() {
        return channelsMap;
    }
}
