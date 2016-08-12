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
import com.netflix.imfutility.conversion.templateParameter.context.ResourceKey;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters;
import com.netflix.imfutility.cpl.uuid.ResourceUUID;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.config.AllowDisallow;
import com.netflix.imfutility.generated.config.ConfigType;
import com.netflix.imfutility.generated.config.ConversionParameterNameType;
import com.netflix.imfutility.generated.config.ConversionParameterType;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.util.ConversionHelper;
import com.netflix.imfutility.xsd.config.ConversionParametersTypeMap;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * Checks whether it's allowed (in config.xml) to silently convert source parameters to destination ones if they don't match.
 * Example: input video essence has 50 fps; destination fps (as defined in conversion.xml) is 25 fps.
 * If config.xml says that silent conversion is not allowed => {@link ConversionNotAllowedException} is thrown.
 * If config.xml says that silent conversion is allowed, then no exception is thrown, and the fps will be silently
 * converted (for example, by FFMPEG)
 * as defined in conversion.xml.
 */
public class SilentConversionChecker {

    private final TemplateParameterContextProvider contextProvider;
    private final ConversionParametersTypeMap configConversionParams;

    public SilentConversionChecker(TemplateParameterContextProvider contextProvider, ConfigType config) {
        this.contextProvider = contextProvider;
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
        if (contextProvider.getDestContext() == null) {
            return;
        }
        if (configConversionParams == null) {
            return;
        }

        for (SequenceType seqType : contextProvider.getSequenceContext().getSequenceTypes()) {
            for (SequenceUUID seqUuid : contextProvider.getSequenceContext().getUuids(seqType)) {
                for (SegmentUUID segmUuid : contextProvider.getSegmentContext().getUuids()) {
                    for (ResourceUUID resUuid : contextProvider.getResourceContext()
                            .getUuids(ResourceKey.create(segmUuid, seqUuid, seqType))) {
                        ContextInfo contextInfo = new ContextInfoBuilder()
                                .setResourceUuid(resUuid)
                                .setSegmentUuid(segmUuid)
                                .setSequenceUuid(seqUuid)
                                .setSequenceType(seqType).build();

                        switch (seqType) {
                            case AUDIO:
                                checkForSilentAudioConversion(contextInfo);
                                break;
                            case VIDEO:
                                checkForSilentVideoConversion(contextInfo);
                                break;
                            default:
                                // nothing for subtitle so far
                        }
                    }
                }
            }
        }

    }

    private void checkForSilentAudioConversion(ContextInfo contextInfo) throws ConversionNotAllowedException {
        boolean allowBitSample = isAllow(ConversionParameterNameType.BITS_SAMPLE);
        boolean allowSampleRate = isAllow(ConversionParameterNameType.SAMPLE_RATE);

        if (!allowBitSample) {
            checkParameter(ResourceContextParameters.BITS_PER_SAMPLE, ConversionParameterNameType.BITS_SAMPLE, contextInfo);
        }
        if (!allowSampleRate) {
            checkParameter(ResourceContextParameters.SAMPLE_RATE, ConversionParameterNameType.SAMPLE_RATE, contextInfo);
        }
    }

    private void checkForSilentVideoConversion(ContextInfo contextInfo) throws ConversionNotAllowedException {
        boolean allowFrameRate = isAllow(ConversionParameterNameType.FRAME_RATE);
        boolean allowSize = isAllow(ConversionParameterNameType.SIZE);
        boolean allowPixelFmt = isAllow(ConversionParameterNameType.PIXEL_FORMAT);
        boolean allowBitDepth = isAllow(ConversionParameterNameType.BIT_DEPTH);

        if (!allowFrameRate) {
            //  make check regardless to frame rate format (50 1 and 50/1 both allowed)
            checkFrameRate(contextInfo);
        }
        if (!allowBitDepth) {
            checkParameter(ResourceContextParameters.BIT_DEPTH, ConversionParameterNameType.BIT_DEPTH, contextInfo);
        }
        if (!allowPixelFmt) {
            checkParameter(ResourceContextParameters.PIXEL_FORMAT, ConversionParameterNameType.PIXEL_FORMAT, contextInfo);
        }
        if (!allowSize) {
            checkParameter(ResourceContextParameters.WIDTH, null, contextInfo);
            checkParameter(ResourceContextParameters.HEIGHT, null, contextInfo);
        }

    }

    private void checkParameter(ResourceContextParameters param, ConversionParameterNameType conversionParam, ContextInfo contextInfo)
            throws ConversionNotAllowedException {
        checkParameter(param, conversionParam, contextInfo, Objects::equals);
    }

    private void checkFrameRate(ContextInfo contextInfo)
            throws ConversionNotAllowedException {
        checkParameter(ResourceContextParameters.FRAME_RATE, ConversionParameterNameType.FRAME_RATE, contextInfo,
                (f1, f2) -> Objects.equals(ConversionHelper.parseEditRate(f1), ConversionHelper.parseEditRate(f2)));
    }

    private void checkParameter(ResourceContextParameters param, ConversionParameterNameType conversionParam, ContextInfo contextInfo,
                                BiPredicate<String, String> equality)
            throws ConversionNotAllowedException {
        String destinationParamValue = getDestinationValue(conversionParam, param);
        if (destinationParamValue == null || destinationParamValue.isEmpty()) {
            return;
        }

        if (contextProvider.getResourceContext().hasResourceParameter(param, contextInfo)) {
            String paramValue = contextProvider.getResourceContext().getParameterValue(param, contextInfo);
            if (!equality.test(paramValue, destinationParamValue)) {
                throw new ConversionNotAllowedException(param.getName(), paramValue, destinationParamValue, contextInfo.getSequenceUuid());
            }
        }
    }

    private String getDestinationValue(ConversionParameterNameType conversionParam, ResourceContextParameters
            resourceParam) {
        String value = null;
        if (conversionParam != null) {
            value = contextProvider.getDestContext().getParameterValue(conversionParam.value());
        }
        if (resourceParam != null && StringUtils.isBlank(value)) {
            value = contextProvider.getDestContext().getParameterValue(resourceParam.getName());
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
