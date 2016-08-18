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
package com.netflix.imfutility.cpl.essencedescriptor;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfoBuilder;
import com.netflix.imfutility.conversion.templateParameter.context.ResourceKey;
import com.netflix.imfutility.conversion.templateParameter.context.SequenceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters;
import com.netflix.imfutility.cpl.uuid.ResourceUUID;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.SequenceType;
import org.smpte_ra.reg._395._2014._13._1.aaf.WAVEPCMDescriptor;

import java.util.List;
import java.util.Map;

/**
 * Parses Essence Descriptors and adds corresponding values to Resource context.
 * Currently we get the following information:
 * <ul>
 *     <li>Audio Channels Layout.</li>
 * </ul>
 */
public class EssenceDescriptorProcessor {

    private final Map<String, List<Object>> essenceDescriptors;
    private final TemplateParameterContextProvider contextProvider;

    private final AudioEssenceDescriptorProcessor audioEssenceDescriptorProcessor;

    public EssenceDescriptorProcessor(Map<String, List<Object>> essenceDescriptors,
                                      TemplateParameterContextProvider contextProvider) {
        this.essenceDescriptors = essenceDescriptors;
        this.contextProvider = contextProvider;
        this.audioEssenceDescriptorProcessor = new AudioEssenceDescriptorProcessor(contextProvider);
    }

    public void build() {
        SequenceTemplateParameterContext sequenceContext = contextProvider.getSequenceContext();
        for (SequenceType seqType : sequenceContext.getSequenceTypes()) {
            for (SequenceUUID seqUuid : sequenceContext.getUuids(seqType)) {
                for (SegmentUUID segmUuid : contextProvider.getSegmentContext().getUuids()) {
                    for (ResourceUUID resUuid : contextProvider.getResourceContext()
                            .getUuids(ResourceKey.create(segmUuid, seqUuid, seqType))) {
                        ContextInfo contextInfo = new ContextInfoBuilder()
                                .setResourceUuid(resUuid)
                                .setSegmentUuid(segmUuid)
                                .setSequenceUuid(seqUuid)
                                .setSequenceType(seqType).build();
                        doBuild(contextInfo);
                    }
                }
            }
        }
    }

    private void doBuild(ContextInfo contextInfo) {
        String essenceDescId = contextProvider.getResourceContext().getParameterValue(
                ResourceContextParameters.ESSENCE_DESC_ID, contextInfo);

        List<? extends Object> subDescriptors = essenceDescriptors.get(essenceDescId);
        if (subDescriptors == null) {
            return;
        }

        for (Object subDescriptor : subDescriptors) {
            if (subDescriptor instanceof WAVEPCMDescriptor) {
                audioEssenceDescriptorProcessor.process((WAVEPCMDescriptor) subDescriptor, contextInfo);
            }
        }
    }


}
