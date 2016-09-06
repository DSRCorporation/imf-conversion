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
package com.netflix.imfutility.itunes.asset.type;

import java.util.stream.Stream;

/**
 * All possible asset roles for all possible metadata specifications.
 */
public enum AssetRole {
    SOURCE("source"),
    SOURCE_HDR("source.hdr"),
    MAPPING_HDR("mapping.hdr"),
    CAPTIONS("captions"),
    CHAPTERS("chapters"),
    AUDIO("audio"),
    AUDIO_7_1("audio.7_1"),
    AUDIO_OBJECT_BASED("audio.object_based"),
    NOTES("notes"),
    SUBTITLES("subtitles"),
    FORCED_SUBTITLES("forced_subtitles"),
    VIDEO_END_DUB_CREDITS("video.end.dub_credits"),
    SUBTITLES_HEARING_IMPAIRED("subtitles.hearing_impaired"),
    AUDIO_VISUALLY_IMPAIRED("audio.visually_impaired"),
    AUDIO_COMMENTARY("audio.commentary"),
    SUBTITLES_COMMENTARY("subtitles.commentary"),
    FORCED_SUBTITLES_COMMENTARY("forced_subtitles.commentary"),
    SUBTITLES_HEARING_IMPAIRED_COMMENTARY("subtitles.hearing_impaired.commentary");

    private final String name;

    AssetRole(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static AssetRole fromName(String name) {
        return Stream.of(values())
                .filter(e -> e.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
