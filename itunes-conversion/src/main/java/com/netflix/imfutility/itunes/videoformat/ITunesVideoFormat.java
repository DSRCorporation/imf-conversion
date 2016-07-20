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
package com.netflix.imfutility.itunes.videoformat;

import com.netflix.imfutility.itunes.videoformat.profile.ITunesSourceProfile;
import com.netflix.imfutility.itunes.videoformat.profile.SourceProfile;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Video format specified for iTunes.
 */
public enum ITunesVideoFormat implements VideoFormat {
    hd1080p30("hd1080p30", ITunesSourceProfile.HD, 1920, 1080, 30,
            ScanType.PROGRESSIVE, null),
    hd1080i2997("hd1080i2997", ITunesSourceProfile.HD, 1920, 1080, 29.97,
            ScanType.INTERLACED, null),
    hd1080p2997("hd1080p2997", ITunesSourceProfile.HD, 1920, 1080, 29.97,
            ScanType.PROGRESSIVE, null),
    hd1080p25("hd1080p25", ITunesSourceProfile.HD, 1920, 1080, 25,
            ScanType.PROGRESSIVE, null),
    hd1080p24("hd1080p24", ITunesSourceProfile.HD, 1920, 1080, 24,
            ScanType.PROGRESSIVE, null),
    hd1080i23976("hd1080i23976", ITunesSourceProfile.HD, 1920, 1080, 23.976,
            ScanType.INTERLACED, null),
    hd1080p23976("hd1080p23976", ITunesSourceProfile.HD, 1920, 1080, 23.976,
            ScanType.PROGRESSIVE, null),

    hd720p30("hd720p30", ITunesSourceProfile.HD, 1280, 720, 30,
            ScanType.PROGRESSIVE, null),
    hd720i2997("hd720i2997", ITunesSourceProfile.HD, 1280, 720, 29.97,
            ScanType.INTERLACED, null),
    hd720p2997("hd720p2997", ITunesSourceProfile.HD, 1280, 720, 29.97,
            ScanType.PROGRESSIVE, null),
    hd720p25("hd720p25", ITunesSourceProfile.HD, 1280, 720, 25,
            ScanType.PROGRESSIVE, null),
    hd720p24("hd720p24", ITunesSourceProfile.HD, 1280, 720, 24,
            ScanType.PROGRESSIVE, null),
    hd720i23976("hd720i23976", ITunesSourceProfile.HD, 1280, 720, 23.976,
            ScanType.INTERLACED, null),
    hd720p23976("hd720p23976", ITunesSourceProfile.HD, 1280, 720, 23.976,
            ScanType.PROGRESSIVE, null),

    sdtvntsc480i2997("sdtvntsc480i2997", ITunesSourceProfile.SD_TV_NTSC, 720, 480, 29.97,
            ScanType.INTERLACED, Duration.ofHours(1).toMillis()),
    sdtvntsc480p2997("sdtvntsc480p2997", ITunesSourceProfile.SD_TV_NTSC, 720, 480, 29.97,
            ScanType.PROGRESSIVE, Duration.ofHours(1).toMillis()),
    sdtvntsc480p24("sdtvntsc480p24", ITunesSourceProfile.SD_TV_NTSC, 720, 480, 24,
            ScanType.PROGRESSIVE, Duration.ofHours(1).toMillis()),
    sdtvntsc480p23976("sdtvntsc480p23976", ITunesSourceProfile.SD_TV_NTSC, 720, 480, 23.976,
            ScanType.PROGRESSIVE, Duration.ofHours(1).toMillis()),

    sdtvpal576i25("sdtvpal576i25", ITunesSourceProfile.SD_TV_PAL, 720, 576, 25,
            ScanType.INTERLACED, Duration.ofHours(1).toMillis()),
    sdtvpal576p25("sdtvpal576p25", ITunesSourceProfile.SD_TV_PAL, 720, 576, 25,
            ScanType.PROGRESSIVE, Duration.ofHours(1).toMillis()),
    sdtvpal576p24("sdtvpal576p24", ITunesSourceProfile.SD_TV_PAL, 720, 576, 24,
            ScanType.PROGRESSIVE, Duration.ofHours(1).toMillis()),
    sdtvpal576p23976("sdtvpal576p23976", ITunesSourceProfile.SD_TV_PAL, 720, 576, 23.976,
            ScanType.PROGRESSIVE, Duration.ofHours(1).toMillis()),

    sdfilmntsc480i2997("sdfilmntsc480i2997", ITunesSourceProfile.SD_FILM_NTSC, 720, 480, 29.97,
            ScanType.INTERLACED, null),
    sdfilmntsc480p2997("sdfilmntsc480p2997", ITunesSourceProfile.SD_FILM_NTSC, 720, 480, 29.97,
            ScanType.PROGRESSIVE, null),
    sdfilmntsc480p24("sdfilmntsc480p24", ITunesSourceProfile.SD_FILM_NTSC, 720, 480, 24,
            ScanType.PROGRESSIVE, null),
    sdfilmntsc480p23976("sdfilmntsc480p23976", ITunesSourceProfile.SD_FILM_NTSC, 720, 480, 23.976,
            ScanType.PROGRESSIVE, null),

    sdfilmpal576p25("sdfilmpal576p25", ITunesSourceProfile.SD_FILM_PAL, 720, 576, 25,
            ScanType.PROGRESSIVE, null),
    sdfilmpal576p24("sdfilmpal576p24", ITunesSourceProfile.SD_FILM_PAL, 720, 576, 24,
            ScanType.PROGRESSIVE, null),
    sdfilmpal576p23976("sdfilmpal576p23976", ITunesSourceProfile.SD_FILM_PAL, 720, 576, 23.976,
            ScanType.PROGRESSIVE, null);

    private final String name;
    private final SourceProfile sourceProfile;
    private final int frameWidth;
    private final int frameHeight;
    private final double fps;
    private final ScanType scanType;
    private final Long maxDuration;

    ITunesVideoFormat(String name, SourceProfile sourceProfile, int frameWidth, int frameHeight,
                      double fps, ScanType scanType, Long maxDuration) {
        this.name = name;
        this.sourceProfile = sourceProfile;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.fps = fps;
        this.scanType = scanType;
        this.maxDuration = maxDuration;
    }

    public static String getSupportedFormats() {
        return Arrays.stream(ITunesVideoFormat.values())
                .map(ITunesVideoFormat::getName)
                .collect(Collectors.joining(" ", "[", "]"));
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public SourceProfile getSourceProfile() {
        return sourceProfile;
    }

    @Override
    public int getFrameWidth() {
        return frameWidth;
    }

    @Override
    public int getFrameHeight() {
        return frameHeight;
    }

    @Override
    public double getFps() {
        return fps;
    }

    @Override
    public ScanType getScanType() {
        return scanType;
    }

    @Override
    public Long getMaxDuration() {
        return maxDuration;
    }

    @Override
    public String toString() {
        return String.format(
                "%-20s [%-12s %4d X %-4d %.3f fps %s]", name, sourceProfile, frameWidth, frameHeight, fps, scanType);
    }
}
