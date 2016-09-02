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
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfoBuilder;
import com.netflix.imfutility.conversion.templateParameter.context.ResourceKey;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.itunes.ITunesPackageType;
import com.netflix.imfutility.util.ConversionHelper;
import com.netflix.imfutility.xsd.conversion.DestContextTypeMap;
import com.netflix.imfutility.xsd.conversion.DestContextsTypeMap;
import org.apache.commons.math3.fraction.BigFraction;

import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters.FRAME_RATE;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters.HEIGHT;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters.WIDTH;


/**
 * Resolve dest context by input video parameters defined for resources.
 * Values extracted from {@link com.netflix.imfutility.conversion.templateParameter.context.ResourceTemplateParameterContext}.
 */
public class InputDestContextResolveStrategy implements DestContextResolveStrategy {
    private final TemplateParameterContextProvider contextProvider;
    private final ITunesPackageType packageType;
    private final VideoDestContextResolveStrategy resolveStrategy;

    public InputDestContextResolveStrategy(TemplateParameterContextProvider contextProvider, ITunesPackageType packageType) {
        this(contextProvider, packageType, new VideoDestContextResolveStrategy());
    }

    public InputDestContextResolveStrategy(TemplateParameterContextProvider contextProvider,
                                           ITunesPackageType packageType,
                                           VideoDestContextResolveStrategy resolveStrategy) {
        this.contextProvider = contextProvider;
        this.packageType = packageType;
        this.resolveStrategy = resolveStrategy;
    }

    @Override
    public DestContextTypeMap resolveContext(DestContextsTypeMap destContexts) throws ConversionException {
        ContextInfo seqContextInfo = new ContextInfoBuilder()
                .setSequenceType(SequenceType.VIDEO)
                .setSequenceUuid(getSequenceUUID())
                .build();

        Integer width = getMinResourceParameterValue(seqContextInfo, WIDTH, Integer::parseInt);
        Integer height = getMinResourceParameterValue(seqContextInfo, HEIGHT, Integer::parseInt);

        BigFraction frameRate = getMinResourceParameterValue(seqContextInfo, FRAME_RATE, ConversionHelper::parseEditRate);

        return resolveStrategy
                .setPackageType(packageType)
                .setWidth(width)
                .setHeight(height)
                .setFrameRate(frameRate)
                // assume video scan type is progressive (according to IMF application #2E)
                .setInterlaced(false)
                .resolveContext(destContexts);
    }

    private SequenceUUID getSequenceUUID() {
        return contextProvider.getSequenceContext().getUuids(SequenceType.VIDEO).stream()
                .findFirst()
                .orElseThrow(() -> new ConversionException("Source must have at least one video sequence"));
    }

    private <T extends Comparable<T>> T getMinResourceParameterValue(ContextInfo contextInfo,
                                                                     ResourceContextParameters parameter,
                                                                     Function<String, T> parser) {
        return segmentStream(contextInfo)
                .flatMap(this::resourceStream)
                .map(info -> getResourceParameterValue(info, parameter, parser))
                .min(Comparator.naturalOrder())
                .get();
    }

    private <T> T getResourceParameterValue(ContextInfo contextInfo, ResourceContextParameters parameter, Function<String, T> parser) {
        String value = contextProvider.getResourceContext().getParameterValue(parameter, contextInfo);
        return parser.apply(value);
    }

    private Stream<ContextInfo> segmentStream(ContextInfo contextInfo) {
        return contextProvider.getSegmentContext().getUuids().stream()
                .map(segUuid -> new ContextInfoBuilder()
                        .setSequenceType(contextInfo.getSequenceType())
                        .setSequenceUuid(contextInfo.getSequenceUuid())
                        .setSegmentUuid(segUuid)
                        .build());
    }

    private Stream<ContextInfo> resourceStream(ContextInfo contextInfo) {
        return contextProvider.getResourceContext().getUuids(ResourceKey.create(contextInfo)).stream()
                .map(resUuid -> new ContextInfoBuilder()
                        .setSequenceType(contextInfo.getSequenceType())
                        .setSequenceUuid(contextInfo.getSequenceUuid())
                        .setSegmentUuid(contextInfo.getSegmentUuid())
                        .setResourceUuid(resUuid)
                        .build());
    }
}
