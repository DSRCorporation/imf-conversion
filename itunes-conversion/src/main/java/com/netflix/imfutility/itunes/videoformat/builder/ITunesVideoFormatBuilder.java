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
import com.netflix.imfutility.itunes.videoformat.ITunesVideoFormat;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Build video format specified for iTunes depends on characteristics.
 */
public class ITunesVideoFormatBuilder extends VideoFormatBuilder<ITunesVideoFormat> {

    public ITunesVideoFormatBuilder() {
    }

    /**
     * Build video format specified for iTunes.
     * If there are no allowed format matched frameWidth, frameHeight, fps throws {@link ConversionException}
     *
     * @return closest to characteristics iTunes video format
     * @throws ConversionException
     */
    @Override
    public ITunesVideoFormat build() throws ConversionException {
        return Arrays.stream(ITunesVideoFormat.values())
                .filter(format -> format.getScanType() == scanType)
                .filter(format -> format.getMaxDuration() == null || format.getMaxDuration() > duration)
                .filter(format -> format.getFrameWidth() <= frameWidth)
                .filter(format -> format.getFrameHeight() <= frameHeight)
                .filter(format -> format.getFps() <= fps)
                .max(comparator())
                .orElseThrow(() -> new ConversionException(String.format(
                        "Format can't be defined. Source video characteristics: [%4d X %-4d %.3f fps %s]",
                        frameWidth, frameHeight, fps, scanType)));
    }

    private static Comparator<ITunesVideoFormat> comparator() {
        return Comparator
                .comparingDouble(ITunesVideoFormat::getFps)
                .thenComparingInt(ITunesVideoFormat::getFrameWidth)
                .thenComparingInt(ITunesVideoFormat::getFrameHeight);
    }

}
