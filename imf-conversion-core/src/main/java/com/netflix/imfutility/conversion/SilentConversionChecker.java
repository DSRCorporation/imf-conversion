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
package com.netflix.imfutility.conversion;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfoBuilder;
import com.netflix.imfutility.conversion.templateParameter.context.DestTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.SequenceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.config.AllowDisallow;
import com.netflix.imfutility.generated.config.ConfigType;
import com.netflix.imfutility.generated.config.ConversionParameterNameType;
import com.netflix.imfutility.generated.config.ConversionParameterType;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.xsd.config.ConversionParametersTypeMap;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Checks whether it's allowed (in config.xml) to silently convert source parameters to destination ones if they don't match.
 * Example: input video essence has 50 fps; destination fps (as defined in conversion.xml) is 25 fps.
 * If config.xml says that silent conversion is not allowed => {@link ConversionNotAllowedException} is thrown.
 * If config.xml says that silent conversion is allowed, then no exception is thrown, and the fps will be silently
 * converted (for example, by FFMPEG)
 * as defined in conversion.xml.
 */
public class SilentConversionChecker {
    private final SequenceTemplateParameterContext sequenceContext;
    private final DestTemplateParameterContext destContext;
    private final ConversionParametersTypeMap configConversionParams;

    public SilentConversionChecker(TemplateParameterContextProvider contextProvider, ConfigType config) {
        this.sequenceContext = contextProvider.getSequenceContext();
        this.destContext = contextProvider.getDestContext();
        this.configConversionParams = config.getConversionParameters();
    }

    /**
     * Whether it's allowed to silently convert source parameters to destination ones if they don't match.
     * {@link ConversionNotAllowedException} is thrown if it's not allowed.
     *
     * @throws ConversionNotAllowedException if there are mismatched parameters,
     *                                       For example, source fps is 25, and the destination one (as defined by conversion.xml), is 50,
     *                                       and config.xml says that silent conversion of fps is not allowed.
     */
    public void check() throws ConversionNotAllowedException {
        if (destContext == null) {
            return;
        }
        if (configConversionParams == null) {
            return;
        }

        for (SequenceType seqType : sequenceContext.getSequenceTypes()) {
            for (SequenceUUID seqUuid : sequenceContext.getUuids(seqType)) {
                switch (seqType) {
                    case AUDIO:
                        checkForSilentAudioConversion(seqUuid);
                        break;
                    case VIDEO:
                        checkForSilentVideoConversion(seqUuid);
                        break;
                    default:
                        // nothing for subtitle so far
                }
            }
        }

    }

    private void checkForSilentAudioConversion(SequenceUUID seqUuid) throws ConversionNotAllowedException {
        boolean allowBitSample = isAllow(ConversionParameterNameType.BITS_SAMPLE);
        boolean allowSampleRate = isAllow(ConversionParameterNameType.SAMPLE_RATE);

        ContextInfo contextInfo = new ContextInfoBuilder().setSequenceUuid(seqUuid).setSequenceType(SequenceType.AUDIO).build();
        if (!allowBitSample) {
            checkParameter(SequenceContextParameters.BITS_PER_SAMPLE, ConversionParameterNameType.BITS_SAMPLE, contextInfo);
        }
        if (!allowSampleRate) {
            checkParameter(SequenceContextParameters.SAMPLE_RATE, ConversionParameterNameType.SAMPLE_RATE, contextInfo);
        }
    }

    private void checkForSilentVideoConversion(SequenceUUID seqUuid) throws ConversionNotAllowedException {
        boolean allowFrameRate = isAllow(ConversionParameterNameType.FRAME_RATE);
        boolean allowSize = isAllow(ConversionParameterNameType.SIZE);
        boolean allowPixelFmt = isAllow(ConversionParameterNameType.PIXEL_FORMAT);
        boolean allowBitDepth = isAllow(ConversionParameterNameType.BIT_DEPTH);

        ContextInfo contextInfo = new ContextInfoBuilder().setSequenceUuid(seqUuid).setSequenceType(SequenceType.VIDEO).build();
        if (!allowFrameRate) {
            checkParameter(SequenceContextParameters.FRAME_RATE, ConversionParameterNameType.FRAME_RATE, contextInfo);
        }
        if (!allowBitDepth) {
            checkParameter(SequenceContextParameters.BIT_DEPTH, ConversionParameterNameType.BIT_DEPTH, contextInfo);
        }
        if (!allowPixelFmt) {
            checkParameter(SequenceContextParameters.PIXEL_FORMAT, ConversionParameterNameType.PIXEL_FORMAT, contextInfo);
        }
        if (!allowSize) {
            checkParameter(SequenceContextParameters.WIDTH, null, contextInfo);
            checkParameter(SequenceContextParameters.HEIGHT, null, contextInfo);
        }

    }

    private void checkParameter(SequenceContextParameters param, ConversionParameterNameType conversionParam, ContextInfo contextInfo)
            throws ConversionNotAllowedException {
        String destinationParamValue = getDestinationValue(conversionParam, param);
        if (destinationParamValue == null || destinationParamValue.isEmpty()) {
            return;
        }

        String paramValue = sequenceContext.getParameterValue(param, contextInfo);
        if (!Objects.equals(paramValue, destinationParamValue)) {
            throw new ConversionNotAllowedException(param.getName(), paramValue, destinationParamValue, contextInfo.getSequenceUuid());
        }
    }

    private String getDestinationValue(ConversionParameterNameType conversionParam, SequenceContextParameters sequenceParam) {
        String value = null;
        if (conversionParam != null) {
            value = destContext.getParameterValue(conversionParam.value());
        }
        if (sequenceParam != null && StringUtils.isBlank(value)) {
            value = destContext.getParameterValue(sequenceParam.getName());
        }
        return value;
    }

    private boolean isAllow(ConversionParameterNameType parameterName) {
        return isAllow(configConversionParams.getMap().get(parameterName));
    }

    private boolean isAllow(ConversionParameterType parameter) {
        return parameter == null || parameter.getValue() == null || parameter.getValue() == AllowDisallow.YES;
    }

}
