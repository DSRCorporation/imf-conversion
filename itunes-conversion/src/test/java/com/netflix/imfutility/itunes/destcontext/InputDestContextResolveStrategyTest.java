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
import com.netflix.imfutility.cpl.uuid.ResourceUUID;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.itunes.ITunesPackageType;
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

    private static final int SEQ_COUNT = 1;
    private static final int SEGMENT_COUNT = 2;
    private static final int RESOURCE_COUNT = 2;
    private static final int REPEAT_COUNT = 2;

    private static final int[] widths = {4096, 4096, 1920, 1920, 1280, 1280, 720, 720};
    private static final int[] heights = {2160, 2160, 1080, 1080, 720, 720, 480, 480};
    private static final String[] frameRates = {"50", "50", "30", "30", "30000/1001", "30000/1001", "25", "25"};

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

        fillVideoResourceParameters();
    }

    private static void fillVideoResourceParameters() {
        ResourceTemplateParameterContext resourceContext = contextProvider.getResourceContext();

        SequenceType seqType = SequenceType.VIDEO;

        int i = 0;
        for (SequenceUUID seqUuid : contextProvider.getSequenceContext().getUuids(seqType)) {
            for (SegmentUUID segmUuid : contextProvider.getSegmentContext().getUuids()) {
                ResourceKey resKey = ResourceKey.create(segmUuid, seqUuid, seqType);
                for (ResourceUUID resUuid : resourceContext.getUuids(resKey)) {

                    resourceContext.addResourceParameter(resKey, resUuid,
                            ResourceContextParameters.DURATION_MS, String.valueOf(4000));
                    resourceContext.addResourceParameter(resKey, resUuid,
                            ResourceContextParameters.WIDTH, String.valueOf(widths[i]));
                    resourceContext.addResourceParameter(resKey, resUuid,
                            ResourceContextParameters.HEIGHT, String.valueOf(heights[i]));
                    resourceContext.addResourceParameter(resKey, resUuid,
                            ResourceContextParameters.FRAME_RATE, frameRates[i]);

                    i++;
                }
            }
        }
    }

    @Test
    public void testBuildCorrectVideoFormat() throws Exception {

        InputDestContextResolveStrategy strategy = new InputDestContextResolveStrategy(contextProvider, ITunesPackageType.film,
                new FakeVideoDestContextResolveStrategy());

        DestContextTypeMap map = strategy.resolveContext(new DestContextsTypeMap());

        assertEquals("720", map.getMap().get(DestContextParameters.WIDTH.getName()).getValue());
        assertEquals("480", map.getMap().get(DestContextParameters.HEIGHT.getName()).getValue());
        assertEquals("25 1", map.getMap().get(DestContextParameters.FRAME_RATE.getName()).getValue());
        // assume video scan type is progressive (according to IMF application #2E)
        assertEquals("false", map.getMap().get(DestContextParameters.INTERLACED.getName()).getValue());
        // max duration must be equals 2(segm)*2(res)*2(repeat)*(4000(millisDuration)/1000(millisInSec))
        assertEquals("32", map.getMap().get(DestContextParameters.DURATION.getName()).getValue());
    }
}
