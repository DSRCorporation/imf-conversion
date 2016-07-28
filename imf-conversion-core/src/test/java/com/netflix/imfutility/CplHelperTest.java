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
package com.netflix.imfutility;

import com.netflix.imfutility.asset.AssetMap;
import com.netflix.imfutility.asset.AssetMapParser;
import com.netflix.imfutility.conversion.templateParameter.ContextInfoBuilder;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.cpl.CplContextBuilder;
import com.netflix.imfutility.cpl.uuid.ResourceUUID;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.util.CplHelper;
import com.netflix.imfutility.util.ImpUtils;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigInteger;

import static junit.framework.TestCase.assertEquals;

/**
 * Tests that CPL helper utility methods work properly.
 */
public class CplHelperTest {

    private static TemplateParameterContextProvider contextProvider;

    @BeforeClass
    public static void setUpAll() throws Exception {
        contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        AssetMap assetMap = new AssetMapParser().parse(ImpUtils.getImpFolder(), ImpUtils.getCorrectAssetmap());
        new CplContextBuilder(contextProvider, assetMap).build(ImpUtils.getCorrectCpl());
    }


    @Test
    public void resourceDurationEU() {
        assertEquals(BigInteger.valueOf(144000), CplHelper.getResourceDurationEU(contextProvider,
                // the values as specified in CPL.xml
                new ContextInfoBuilder()
                        .setSequenceType(SequenceType.AUDIO)
                        .setSequenceUuid(SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712"))
                        .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                        .setResourceUuid(ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9096", 0))
                        .build()));
        assertEquals(BigInteger.valueOf(150), CplHelper.getResourceDurationEU(contextProvider,
                // the values as specified in CPL.xml
                new ContextInfoBuilder()
                        .setSequenceType(SequenceType.VIDEO)
                        .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                        .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                        .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ec", 2))
                        .build()));
    }

    @Test
    public void resourceDurationMS() {
        assertEquals(3000L, CplHelper.getResourceDurationMS(contextProvider,
                // the values as specified in CPL.xml
                new ContextInfoBuilder()
                        .setSequenceType(SequenceType.AUDIO)
                        .setSequenceUuid(SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712"))
                        .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                        .setResourceUuid(ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9096", 0))
                        .build()));
        assertEquals(3000L, CplHelper.getResourceDurationMS(contextProvider,
                // the values as specified in CPL.xml
                new ContextInfoBuilder()
                        .setSequenceType(SequenceType.VIDEO)
                        .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                        .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                        .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ec", 2))
                        .build()));
    }

    @Test
    public void virtualTrackDurationEU() {
        // the values as specified in CPL.xml
        assertEquals(BigInteger.valueOf(1917120),
                CplHelper.getVirtualTrackDurationEU(
                        contextProvider,
                        SequenceType.AUDIO,
                        SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711")));

        assertEquals(BigInteger.valueOf(4191),
                CplHelper.getVirtualTrackDurationEU(
                        contextProvider,
                        SequenceType.VIDEO,
                        SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d")));

    }

    @Test
    public void virtualTrackDurationMS() {
        // the values as specified in CPL.xml
        assertEquals(39940L,
                CplHelper.getVirtualTrackDurationMS(
                        contextProvider,
                        SequenceType.AUDIO,
                        SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711")));

        assertEquals(83820L,
                CplHelper.getVirtualTrackDurationMS(
                        contextProvider,
                        SequenceType.VIDEO,
                        SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d")));
    }

}
