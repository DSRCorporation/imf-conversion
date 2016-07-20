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
package com.netflix.imfutility.itunes.videoformat.context;

import com.netflix.imfutility.conversion.templateParameter.context.DynamicTemplateParameterContext;
import com.netflix.imfutility.itunes.videoformat.VideoFormat;

/**
 * Helper to set video format parameters into passed {@link DynamicTemplateParameterContext}.
 */
public class VideoFormatContextHelper {
    private final DynamicTemplateParameterContext dynamicContext;

    public VideoFormatContextHelper(DynamicTemplateParameterContext dynamicContext) {
        this.dynamicContext = dynamicContext;
    }

    public void setVideoFormat(VideoFormat format) {
        doAddParameter(VideoFormatContextParameters.FW, format.getFrameWidth());
        doAddParameter(VideoFormatContextParameters.FH, format.getFrameHeight());
        doAddParameter(VideoFormatContextParameters.FPS, format.getFps());
        doAddParameter(VideoFormatContextParameters.SCAN, format.getScanType());
    }

    private <T> void doAddParameter(VideoFormatContextParameters parameter, T value) {
        dynamicContext.addParameter(parameter.getName(), String.valueOf(value));
    }

    public DynamicTemplateParameterContext getDynamicContext() {
        return dynamicContext;
    }

}
