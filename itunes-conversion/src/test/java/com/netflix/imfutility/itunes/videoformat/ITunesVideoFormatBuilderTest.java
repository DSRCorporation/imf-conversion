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

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.itunes.videoformat.builder.ITunesVideoFormatBuilder;
import com.netflix.imfutility.itunes.videoformat.builder.VideoFormatBuilder;
import org.junit.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;

import static org.junit.Assert.assertEquals;

/**
 * Tests that iTunes video format builds correctly.
 */
public class ITunesVideoFormatBuilderTest {

    @Test
    public void testBuildCorrectVideoFormat() {
        VideoFormatBuilder<ITunesVideoFormat> builder = new ITunesVideoFormatBuilder();

        builder.setFrameWidth(4096)
                .setFrameHeight(2160)
                .setFps(60)
                .setScanType(ScanType.INTERLACED)
                .setDuration(Duration.ofHours(1).toMillis());
        assertEquals(ITunesVideoFormat.hd1080i2997, builder.build());

        builder.setFrameWidth(800)
                .setFrameHeight(600)
                .setFps(30)
                .setScanType(ScanType.INTERLACED)
                .setDuration(Duration.ofHours(1).toMillis() - 1);
        //  duration < 1 hour. Expected sd tv format
        assertEquals(ITunesVideoFormat.sdtvntsc480i2997, builder.build());

        builder.setFrameWidth(1919)
                .setFrameHeight(1281)
                .setFps(24)
                .setScanType(ScanType.INTERLACED)
                .setDuration(Duration.ofHours(1).toMillis());
        assertEquals(ITunesVideoFormat.hd720i23976, builder.build());

        builder.setFrameWidth(800)
                .setFrameHeight(600)
                .setFps(24)
                .setScanType(ScanType.PROGRESSIVE)
                .setDuration(Duration.ofHours(1).toMillis());
        assertEquals(ITunesVideoFormat.sdfilmpal576p24, builder.build());
    }

    @Test(expected = ConversionException.class)
    public void testBuildIncorrectVideoFormatWidth() {
        ITunesVideoFormatBuilder builder = new ITunesVideoFormatBuilder();

        int incorrectWidth = Arrays.asList(ITunesVideoFormat.values()).stream()
                .map(format -> format.getFrameWidth())
                .min(Comparator.naturalOrder())
                .orElse(0) - 1;

        builder.setFrameWidth(incorrectWidth)
                .setFrameHeight(2160)
                .setFps(60)
                .setScanType(ScanType.INTERLACED)
                .setDuration(1900);
        builder.build();
    }

    @Test(expected = ConversionException.class)
    public void testBuildIncorrectVideoFormatHeight() {
        ITunesVideoFormatBuilder builder = new ITunesVideoFormatBuilder();

        int incorrectHeight = Arrays.asList(ITunesVideoFormat.values()).stream()
                .map(format -> format.getFrameHeight())
                .min(Comparator.naturalOrder())
                .orElse(0) - 1;

        builder.setFrameWidth(4096)
                .setFrameHeight(incorrectHeight)
                .setFps(60)
                .setScanType(ScanType.PROGRESSIVE)
                .setDuration(1900);
        builder.build();
    }

    @Test(expected = ConversionException.class)
    public void testBuildIncorrectVideoFormatFps() {
        ITunesVideoFormatBuilder builder = new ITunesVideoFormatBuilder();

        double incorrectFps = Arrays.asList(ITunesVideoFormat.values()).stream()
                .map(format -> format.getFps())
                .min(Comparator.naturalOrder())
                .orElse(0.) - 1.;
        builder.setFrameWidth(4096)
                .setFrameHeight(2160)
                .setFps(incorrectFps)
                .setScanType(ScanType.INTERLACED)
                .setDuration(1900);
        builder.build();
    }

    @Test(expected = ConversionException.class)
    public void testBuildIncorrectVideoFormatScanning() {
        ITunesVideoFormatBuilder builder = new ITunesVideoFormatBuilder();

        builder.setFrameWidth(800)
                .setFrameHeight(600)
                .setFps(24)
                .setScanType(ScanType.INTERLACED)
                .setDuration(0);
        // no interlaced sd format with fps < 24 defined
        builder.build();
    }
}
