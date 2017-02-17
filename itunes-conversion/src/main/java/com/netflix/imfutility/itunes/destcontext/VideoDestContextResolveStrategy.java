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
package com.netflix.imfutility.itunes.destcontext;

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.itunes.ITunesPackageType;
import com.netflix.imfutility.itunes.destcontext.filter.DestContextPackageTypeFilter;
import com.netflix.imfutility.itunes.destcontext.wrap.DestContextMapWrapper;
import com.netflix.imfutility.xsd.conversion.DestContextTypeMap;
import com.netflix.imfutility.xsd.conversion.DestContextsTypeMap;
import org.apache.commons.math3.fraction.BigFraction;

import java.util.Comparator;

import static com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters.FRAME_RATE;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters.HEIGHT;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters.INTERLACED;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters.WIDTH;

/**
 * Resolve dest context by video parameters.
 * Find closest context according to frame size, fps, duration and scan type.
 */
public class VideoDestContextResolveStrategy implements DestContextResolveStrategy {
    protected Integer width;
    protected Integer height;
    protected Boolean interlaced;
    protected BigFraction frameRate;
    protected ITunesPackageType packageType;

    public VideoDestContextResolveStrategy() {
    }

    public VideoDestContextResolveStrategy setWidth(Integer width) {
        this.width = width;
        return this;
    }

    public VideoDestContextResolveStrategy setHeight(Integer height) {
        this.height = height;
        return this;
    }

    public VideoDestContextResolveStrategy setInterlaced(Boolean interlaced) {
        this.interlaced = interlaced;
        return this;
    }

    public VideoDestContextResolveStrategy setFrameRate(BigFraction frameRate) {
        this.frameRate = frameRate;
        return this;
    }

    public VideoDestContextResolveStrategy setPackageType(ITunesPackageType packageType) {
        this.packageType = packageType;
        return this;
    }

    @Override
    public DestContextTypeMap resolveContext(DestContextsTypeMap destContexts) throws ConversionException {
        if (interlaced == null
                || width == null
                || height == null
                || frameRate == null) {
            throw new ConversionException(
                    "Format can't be defined. Source video characteristics must be set.");
        }

        return destContexts.getMap().values().stream()
                .filter(new DestContextPackageTypeFilter(packageType))
                .map(DestContextMapWrapper::new)
                .filter(this::matchScan)
                .filter(this::checkWidth)
                .filter(this::checkHeight)
                .filter(this::checkFrameRate)
                .max(comparator())
                .orElseThrow(() -> new ConversionException(String.format(
                        "Format can't be defined. Source video characteristics: [%4s X %-4s %s fps %s scan]",
                        width, height, frameRate, interlaced ? "interlaced" : "progressive")))
                .getMap();
    }

    private Comparator<DestContextMapWrapper> comparator() {
        return Comparator
                .comparingInt(this::getWidth)
                .thenComparingInt(this::getHeight)
                .thenComparing(this::getFrameRate);
    }

    private boolean checkWidth(DestContextMapWrapper wrapper) {
        return wrapper.compareToInteger(WIDTH.getName(), width, false) <= 0;
    }

    private boolean checkHeight(DestContextMapWrapper wrapper) {
        return wrapper.compareToInteger(HEIGHT.getName(), height, false) <= 0;
    }

    private boolean checkFrameRate(DestContextMapWrapper wrapper) {
        return wrapper.compareToFrameRate(FRAME_RATE.getName(), frameRate, false) <= 0;
    }

    private boolean matchScan(DestContextMapWrapper wrapper) {
        Boolean value = wrapper.getValueAsBoolean(INTERLACED.getName());
        return interlaced ? value : !value;
    }

    private Integer getWidth(DestContextMapWrapper wrapper) {
        return wrapper.getValueAsInteger(WIDTH.getName());
    }

    private Integer getHeight(DestContextMapWrapper wrapper) {
        return wrapper.getValueAsInteger(HEIGHT.getName());
    }

    private BigFraction getFrameRate(DestContextMapWrapper wrapper) {
        return wrapper.getValueAsFrameRate(FRAME_RATE.getName());
    }


}
