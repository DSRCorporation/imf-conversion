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
package com.netflix.imfutility.cpl;

import com.netflix.imfutility.asset.AssetMap;
import com.netflix.imfutility.asset.AssetMapParser;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfoBuilder;
import com.netflix.imfutility.conversion.templateParameter.context.SegmentTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SegmentContextParameters;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.util.ImpUtils;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertArrayEquals;

/**
 * <ul>
 * <li>Tests that segment context is filled correctly from CPL.</li>
 * </ul>

 */
public class CplContextBuilderSegmentContextTest {

    @Test
    public void testSegmentContextCreatedCorrectly() throws Exception {
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        AssetMap assetMap = new AssetMapParser().parse(ImpUtils.getImpFolder(), ImpUtils.getCorrectAssetmap());
        new CplContextBuilder(contextProvider, assetMap).build(ImpUtils.getCorrectCpl());

        SegmentTemplateParameterContext segmentContext = contextProvider.getSegmentContext();
        assertEquals(2, segmentContext.getSegmentsNum());

        // UUIDs as defined in CPL.xml
        assertArrayEquals(
                new SegmentUUID[]{
                        SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"),
                        SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17")
                },
                segmentContext.getUuids().toArray(new SegmentUUID[]{}));

        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                .build();
        assertEquals("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16",
                segmentContext.getParameterValue(SegmentContextParameters.UUID, contextInfo));
        assertEquals("0",
                segmentContext.getParameterValue(SegmentContextParameters.NUM, contextInfo));

        contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17"))
                .build();
        assertEquals("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17",
                segmentContext.getParameterValue(SegmentContextParameters.UUID, contextInfo));
        assertEquals("1",
                segmentContext.getParameterValue(SegmentContextParameters.NUM, contextInfo));
    }

    private CplContextBuilder createCplContextBuilder() throws Exception {
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        AssetMap assetMap = new AssetMapParser().parse(ImpUtils.getImpFolder(), ImpUtils.getCorrectAssetmap());
        return new CplContextBuilder(contextProvider, assetMap);
    }

}
