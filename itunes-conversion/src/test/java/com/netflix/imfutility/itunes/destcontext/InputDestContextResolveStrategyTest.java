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

import com.netflix.imfutility.conversion.templateParameter.context.ResourceKey;
import com.netflix.imfutility.conversion.templateParameter.context.ResourceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters;
import com.netflix.imfutility.cpl.uuid.ResourceUUID;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.itunes.util.FakeVideoDestContextResolveStrategy;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import com.netflix.imfutility.xsd.conversion.DestContextTypeMap;
import com.netflix.imfutility.xsd.conversion.DestContextsTypeMap;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.EnumSet;

import static junit.framework.TestCase.assertEquals;

/**
 * Tests that dest context for input contexts resolves correctly.
 */
public class InputDestContextResolveStrategyTest {

    private static final int SEGMENT_COUNT = 2;
    private static final int SEQ_COUNT = 2;
    private static final int RESOURCE_COUNT = 2;
    private static final int REPEAT_COUNT = 2;

    private static TemplateParameterContextProvider contextProvider;

    @BeforeClass
    public static void setUpAll() throws Exception {
        contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        TemplateParameterContextCreator.fillCPLContext(contextProvider,
                SEGMENT_COUNT,
                SEQ_COUNT,
                RESOURCE_COUNT,
                REPEAT_COUNT,
                EnumSet.of(SequenceType.VIDEO, SequenceType.AUDIO));

        fillMediaInfoVideoParameters();
        fillVideoResourcesParameters();
    }

    private static void fillVideoResourcesParameters() {
        ResourceTemplateParameterContext resourceContext = contextProvider.getResourceContext();

        for (SequenceUUID seqUuid : contextProvider.getSequenceContext().getUuids(SequenceType.VIDEO)) {
            for (SegmentUUID segmUuid : contextProvider.getSegmentContext().getUuids()) {
                for (ResourceUUID resUuid : resourceContext.getUuids(ResourceKey.create(segmUuid, seqUuid, SequenceType.VIDEO))) {

                    ResourceKey resKey = ResourceKey.create(segmUuid, seqUuid, SequenceType.VIDEO);

                    resourceContext.addResourceParameter(resKey, resUuid,
                            ResourceContextParameters.DURATION_EDIT_UNIT, String.valueOf(100));
                    resourceContext.addResourceParameter(resKey, resUuid,
                            ResourceContextParameters.EDIT_RATE, "100 2");
                    resourceContext.addResourceParameter(resKey, resUuid,
                            ResourceContextParameters.REPEAT_COUNT, String.valueOf(REPEAT_COUNT));
                }
            }
        }
    }

    private static void fillMediaInfoVideoParameters() {
        for (SequenceUUID seqUuid : contextProvider.getSequenceContext().getUuids(SequenceType.VIDEO)) {
            contextProvider.getSequenceContext().addSequenceParameter(
                    SequenceType.VIDEO,
                    seqUuid,
                    SequenceContextParameters.WIDTH,
                    String.valueOf(4096));
            contextProvider.getSequenceContext().addSequenceParameter(
                    SequenceType.VIDEO,
                    seqUuid,
                    SequenceContextParameters.HEIGHT,
                    String.valueOf(2160));
            contextProvider.getSequenceContext().addSequenceParameter(
                    SequenceType.VIDEO,
                    seqUuid,
                    SequenceContextParameters.FRAME_RATE,
                    "50 1");
        }
    }

    @Test
    public void testBuildCorrectVideoFormat() throws Exception {

        InputDestContextResolveStrategy strategy = new InputDestContextResolveStrategy(contextProvider,
                new FakeVideoDestContextResolveStrategy());

        DestContextTypeMap map = strategy.resolveContext(new DestContextsTypeMap());

        assertEquals("4096", map.getMap().get(DestContextParameters.WIDTH.getName()).getValue());
        assertEquals("2160", map.getMap().get(DestContextParameters.HEIGHT.getName()).getValue());
        assertEquals("50 1", map.getMap().get(DestContextParameters.FRAME_RATE.getName()).getValue());
        // assume video scan type is progressive (according to IMF application #2E)
        assertEquals("false", map.getMap().get(DestContextParameters.INTERLACED.getName()).getValue());
        // max duration must be equals 2(segm)*2(res)*2(repeat)*(100(durationEU)/50(unitsInSec))
        assertEquals("16", map.getMap().get(DestContextParameters.DURATION.getName()).getValue());
    }
}
