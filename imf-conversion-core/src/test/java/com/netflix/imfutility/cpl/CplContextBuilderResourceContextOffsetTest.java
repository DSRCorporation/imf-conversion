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
import com.netflix.imfutility.conversion.templateParameter.context.DestTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.ResourceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters;
import com.netflix.imfutility.cpl.uuid.ResourceUUID;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.util.ImpUtils;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import com.netflix.imfutility.xsd.conversion.DestContextTypeMap;
import org.junit.Test;

import static com.netflix.imfutility.util.TemplateParameterContextCreator.putDestContextValue;
import static junit.framework.TestCase.assertEquals;

/**
 * <ul>
 * <li>Tests that resource context is filled correctly from CPL.</li>
 * </ul>
 */
public class CplContextBuilderResourceContextOffsetTest {

    @Test
    public void testStartTimeFromCplZeroStart() throws Exception {
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        AssetMap assetMap = new AssetMapParser().parse(ImpUtils.getImpFolder(), ImpUtils.getCorrectAssetmap());
        new CplContextBuilder(contextProvider, assetMap).build(ImpUtils.getCorrectCpl());

        doTestOffset(contextProvider, 0);
    }

    @Test
    public void testStartTimeFromCplNonZeroZeroStart() throws Exception {
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        AssetMap assetMap = new AssetMapParser().parse(ImpUtils.getImpFolder(), ImpUtils.getCorrectAssetmap());
        new CplContextBuilder(contextProvider, assetMap).build(ImpUtils.getCorrectCplNonZeroStart());

        doTestOffset(contextProvider, 3670500);
    }

    @Test
    public void testStartTimeFromDestContext() throws Exception {
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        DestContextTypeMap map = new DestContextTypeMap();
        map.setName("test");
        putDestContextValue(DestContextParameters.FRAME_RATE.getName(), "25 1", map);
        putDestContextValue(DestContextParameters.START_TIME.getName(), "09:50:30:05", map);
        DestTemplateParameterContext dest = contextProvider.getDestContext();
        dest.setDestContextMap(map);

        AssetMap assetMap = new AssetMapParser().parse(ImpUtils.getImpFolder(), ImpUtils.getCorrectAssetmap());
        new CplContextBuilder(contextProvider, assetMap).build(ImpUtils.getCorrectCpl());

        doTestOffset(contextProvider, 35430200);
    }

    private void doTestOffset(TemplateParameterContextProvider contextProvider, long startTime) {
        ResourceTemplateParameterContext resourceContext = contextProvider.getResourceContext();

        // UUIDs as defined in CPL.xml
        // essence must be a full path!

        // first segment

        // 1st resource
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                .setSequenceType(SequenceType.VIDEO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ea", 0))
                .build();
        assertEquals(String.valueOf(startTime + 0),
                resourceContext.getParameterValue(ResourceContextParameters.OFFSET_MS, contextInfo));

        // 2d resource
        contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                .setSequenceType(SequenceType.VIDEO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1eb", 0))
                .build();
        assertEquals(String.valueOf(startTime + 6000),
                resourceContext.getParameterValue(ResourceContextParameters.OFFSET_MS, contextInfo));


        // 3d resource: repeat=0
        contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                .setSequenceType(SequenceType.VIDEO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ec", 0))
                .build();
        assertEquals(String.valueOf(startTime + 9000),
                resourceContext.getParameterValue(ResourceContextParameters.OFFSET_MS, contextInfo));

        // 3d resource: repeat=1
        contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                .setSequenceType(SequenceType.VIDEO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ec", 1))
                .build();
        assertEquals(String.valueOf(startTime + 12000),
                resourceContext.getParameterValue(ResourceContextParameters.OFFSET_MS, contextInfo));

        // 3d resource: repeat=1
        contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                .setSequenceType(SequenceType.VIDEO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ec", 2))
                .build();
        assertEquals(String.valueOf(startTime + 15000),
                resourceContext.getParameterValue(ResourceContextParameters.OFFSET_MS, contextInfo));


        // second segment

        // 1st resource
        contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                .setSequenceType(SequenceType.VIDEO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ea", 0))
                .build();
        assertEquals(String.valueOf(startTime + 18000),
                resourceContext.getParameterValue(ResourceContextParameters.OFFSET_MS, contextInfo));

        // 2d resource: repeat = 0
        contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                .setSequenceType(SequenceType.VIDEO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1eb", 0))
                .build();
        assertEquals(String.valueOf(startTime + 39940),
                resourceContext.getParameterValue(ResourceContextParameters.OFFSET_MS, contextInfo));

        // 2d resource: repeat = 1
        contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                .setSequenceType(SequenceType.VIDEO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1eb", 1))
                .build();
        assertEquals(String.valueOf(startTime + 61880),
                resourceContext.getParameterValue(ResourceContextParameters.OFFSET_MS, contextInfo));
    }

}
