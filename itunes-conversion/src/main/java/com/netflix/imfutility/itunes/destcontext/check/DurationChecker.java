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
package com.netflix.imfutility.itunes.destcontext.check;

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.itunes.destcontext.wrap.DestContextMapWrapper;
import com.netflix.imfutility.util.CplHelper;
import com.netflix.imfutility.xsd.conversion.DestContextTypeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters.DURATION;

/**
 * Check duration and show warning.
 */
public class DurationChecker {
    private final Logger logger = LoggerFactory.getLogger(DurationChecker.class);
    private final TemplateParameterContextProvider contextProvider;

    public DurationChecker(TemplateParameterContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    public void checkDuration(DestContextTypeMap destContextMap) {
        DestContextMapWrapper wrapper = new DestContextMapWrapper(destContextMap);

        long durationMs = CplHelper.getVirtualTrackDurationMS(contextProvider, SequenceType.VIDEO, getSequenceUUID());
        Long durationS = Duration.ofMillis(durationMs).getSeconds();

        if (wrapper.compareToLong(DURATION.getName(), durationS, true) < 0) {
            logger.warn("Duration of source in format {} supposed to be less than {} h.",
                    destContextMap.getName(),
                    Duration.ofMillis(wrapper.getValueAsLong(DURATION.getName())).toHours());
        }
    }

    private SequenceUUID getSequenceUUID() {
        return contextProvider.getSequenceContext().getUuids(SequenceType.VIDEO).stream()
                .findFirst()
                .orElseThrow(() -> new ConversionException("Source must have at least one video sequence"));
    }
}
