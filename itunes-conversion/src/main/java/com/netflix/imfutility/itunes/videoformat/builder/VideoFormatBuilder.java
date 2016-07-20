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
package com.netflix.imfutility.itunes.videoformat.builder;

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.itunes.videoformat.ScanType;
import com.netflix.imfutility.itunes.videoformat.VideoFormat;

/**
 * Build video format depends on characteristics.
 *
 * @param <T> destination class of video format
 */
public abstract class VideoFormatBuilder<T extends VideoFormat> {

    protected int frameWidth;
    protected int frameHeight;
    protected double fps;
    protected ScanType scanType;
    protected long duration;

    public VideoFormatBuilder<T> setFrameWidth(int frameWidth) {
        this.frameWidth = frameWidth;
        return this;
    }

    public VideoFormatBuilder<T> setFrameHeight(int frameHeight) {
        this.frameHeight = frameHeight;
        return this;
    }

    public VideoFormatBuilder<T> setFps(double fps) {
        this.fps = fps;
        return this;
    }

    public VideoFormatBuilder<T> setScanType(ScanType scanType) {
        this.scanType = scanType;
        return this;
    }

    public VideoFormatBuilder<T> setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public abstract T build() throws ConversionException;
}
