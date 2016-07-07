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
package com.netflix.imfutility.mediainfo;

import com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.generated.mediainfo.StreamType;
import com.netflix.imfutility.util.ConversionHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * A helper class to wrap required parameters (media info).
 * It's also used to check whether a virtual track has equal parameters (such as fps, sample rate, etc.) for each segment.
 */
public class VirtualTrackInfo {

    private final SequenceType seqType;

    private final Map<SequenceContextParameters, String> parameters = new HashMap<>();

    public VirtualTrackInfo(SequenceType seqType, StreamType stream) {
        this.seqType = seqType;

        // audio
        addParameter(SequenceContextParameters.CHANNELS_NUM, stream.getChannels());
        addParameter(SequenceContextParameters.SAMPLE_RATE, stream.getSampleRate());
        addParameter(SequenceContextParameters.BITS_PER_SAMPLE, stream.getBitsPerSample());

        // video
        addParameter(SequenceContextParameters.WIDTH, stream.getWidth());
        addParameter(SequenceContextParameters.HEIGHT, stream.getHeight());
        addParameter(SequenceContextParameters.BIT_DEPTH, stream.getBitsPerRawSample());
        addParameter(SequenceContextParameters.PIXEL_FORMAT, stream.getPixFmt());
        String rFrameRate = stream.getRFrameRate();
        if (rFrameRate != null) {
            addParameter(SequenceContextParameters.FRAME_RATE, ConversionHelper.rFrameRateToEditRate(rFrameRate));
        }
    }

    private void addParameter(SequenceContextParameters paramName, Object paramValue) {
        if (paramValue != null) {
            parameters.put(paramName, paramValue.toString());
        }
    }

    public SequenceType getSeqType() {
        return seqType;
    }

    public Map<SequenceContextParameters, String> getParameters() {
        return parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        VirtualTrackInfo that = (VirtualTrackInfo) o;

        if (seqType != that.seqType) {
            return false;
        }
        return parameters != null ? parameters.equals(that.parameters) : that.parameters == null;

    }

    @Override
    public int hashCode() {
        int result = seqType != null ? seqType.hashCode() : 0;
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        return result;
    }

}
