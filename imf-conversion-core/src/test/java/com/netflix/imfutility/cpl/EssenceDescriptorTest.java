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
import com.netflix.imfutility.conversion.templateParameter.context.ResourceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters;
import com.netflix.imfutility.cpl.uuid.ResourceUUID;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.util.ImpUtils;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * <ul>
 * <li>Tests that resource context is filled correctly from CPL when CPL contains Essence Descriptors.</li>
 * </ul>
 */
public class EssenceDescriptorTest {

    private static ResourceTemplateParameterContext resourceContext;
    private static TemplateParameterContextProvider contextProvider;

    @BeforeClass
    public static void setUpAll() throws Exception {
        contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        AssetMap assetMap = new AssetMapParser().parse(ImpUtils.getImpFolderEssenceDesc(), ImpUtils.getCorrectAssetmapEssenceDesc());
        new CplContextBuilder(contextProvider, assetMap, ImpUtils.getCplEssenceDesc()).build();

        resourceContext = contextProvider.getResourceContext();
    }

    @Test
    public void testSeqmentAndSequenceCount() throws Exception {
        assertEquals(1, contextProvider.getSegmentContext().getSegmentsNum());
        assertEquals(1, contextProvider.getSequenceContext().getSequenceCount(SequenceType.VIDEO));
        assertEquals(2, contextProvider.getSequenceContext().getSequenceCount(SequenceType.AUDIO));
    }

    @Test
    public void testCommonVideoParameters() throws Exception {
        // UUIDs as defined in CPL.xml.
        // essence must be a full path!

        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:e9e18479-6116-40f4-a891-9ef999c80f2a"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:8546a723-ccb1-4d69-8834-b8ff96222d53"))
                .setSequenceType(SequenceType.VIDEO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:ec9f8003-655e-438a-b30a-d7700ec4cb6f", 0))
                .build();

        assertEquals("urn:uuid:ec9f8003-655e-438a-b30a-d7700ec4cb6f",
                resourceContext.getParameterValue(ResourceContextParameters.UUID, contextInfo));
        assertEquals("urn:uuid:ea05e7ab-5ee2-4ac7-ab41-6c3a2cf78a0b",
                resourceContext.getParameterValue(ResourceContextParameters.TRACK_FILE_ID, contextInfo));
        assertEquals("urn:uuid:3d3a369d-bce0-4347-8c45-ac527451a0f2",
                resourceContext.getParameterValue(ResourceContextParameters.ESSENCE_DESC_ID, contextInfo));
        assertEquals("0",
                resourceContext.getParameterValue(ResourceContextParameters.NUM, contextInfo));
        assertEquals(ImpUtils.getAbsolutePathEssenceDesc("Netflix_Plugfest_Oct2015.mxf"),
                resourceContext.getParameterValue(ResourceContextParameters.ESSENCE, contextInfo));

        assertEquals("0",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
        assertEquals("0",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_FRAME_EDIT_UNIT, contextInfo));
        assertEquals("00:00:00.000",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_TIMECODE, contextInfo));
        assertEquals("0",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_MS, contextInfo));

        assertEquals("7148",
                resourceContext.getParameterValue(ResourceContextParameters.END_TIME_EDIT_UNIT, contextInfo));
        assertEquals("00:04:58.131",
                resourceContext.getParameterValue(ResourceContextParameters.END_TIME_TIMECODE, contextInfo));
        assertEquals("298131",
                resourceContext.getParameterValue(ResourceContextParameters.END_TIME_MS, contextInfo));

        assertEquals("7148",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
        assertEquals("7148",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_FRAME_EDIT_UNIT, contextInfo));
        assertEquals("00:04:58.131",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_TIMECODE, contextInfo));
        assertEquals("298131",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_MS, contextInfo));

        assertEquals("1",
                resourceContext.getParameterValue(ResourceContextParameters.REPEAT_COUNT, contextInfo));
        assertEquals("0",
                resourceContext.getParameterValue(ResourceContextParameters.REPEAT, contextInfo));
        assertEquals("24000 1001",
                resourceContext.getParameterValue(ResourceContextParameters.EDIT_RATE, contextInfo));
    }

    @Test
    public void testCommonAudioParameters() throws Exception {
        // UUIDs as defined in CPL.xml.
        // essence must be a full path!

        // audio1
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:e9e18479-6116-40f4-a891-9ef999c80f2a"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:55b1a5a6-d62d-4903-aca5-f766f86696fc"))
                .setSequenceType(SequenceType.AUDIO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:7a9aec8a-ab50-4310-bb56-1f35411bd7ac", 0))
                .build();


        assertEquals("urn:uuid:7a9aec8a-ab50-4310-bb56-1f35411bd7ac",
                resourceContext.getParameterValue(ResourceContextParameters.UUID, contextInfo));
        assertEquals("urn:uuid:c808001c-da54-4295-a721-dcaa00659699",
                resourceContext.getParameterValue(ResourceContextParameters.TRACK_FILE_ID, contextInfo));
        assertEquals("urn:uuid:6086c285-d8b2-4c03-ba5b-b31f3b8cf059",
                resourceContext.getParameterValue(ResourceContextParameters.ESSENCE_DESC_ID, contextInfo));
        assertEquals("0",
                resourceContext.getParameterValue(ResourceContextParameters.NUM, contextInfo));
        assertEquals(ImpUtils.getAbsolutePathEssenceDesc("Netflix_Plugfest_Oct2015_ENG51.mxf"),
                resourceContext.getParameterValue(ResourceContextParameters.ESSENCE, contextInfo));

        assertEquals("1439992",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
        assertEquals("1439992",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_FRAME_EDIT_UNIT, contextInfo));
        assertEquals("00:00:29.999",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_TIMECODE, contextInfo));
        assertEquals("29999",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_MS, contextInfo));

        assertEquals("15750288",
                resourceContext.getParameterValue(ResourceContextParameters.END_TIME_EDIT_UNIT, contextInfo));
        assertEquals("00:05:28.131",
                resourceContext.getParameterValue(ResourceContextParameters.END_TIME_TIMECODE, contextInfo));
        assertEquals("328131",
                resourceContext.getParameterValue(ResourceContextParameters.END_TIME_MS, contextInfo));

        assertEquals("14310296",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
        assertEquals("14310296",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_FRAME_EDIT_UNIT, contextInfo));
        assertEquals("00:04:58.131",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_TIMECODE, contextInfo));
        assertEquals("298131",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_MS, contextInfo));

        assertEquals("1",
                resourceContext.getParameterValue(ResourceContextParameters.REPEAT_COUNT, contextInfo));
        assertEquals("0",
                resourceContext.getParameterValue(ResourceContextParameters.REPEAT, contextInfo));
        assertEquals("48000 1",
                resourceContext.getParameterValue(ResourceContextParameters.EDIT_RATE, contextInfo));

        // audio2
        contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:e9e18479-6116-40f4-a891-9ef999c80f2a"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:b8ee769c-b768-451c-bb5f-9c38fd3a5d18"))
                .setSequenceType(SequenceType.AUDIO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:8e910d09-a3e8-4f2e-88b4-37beb3e3c883", 0))
                .build();


        assertEquals("urn:uuid:8e910d09-a3e8-4f2e-88b4-37beb3e3c883",
                resourceContext.getParameterValue(ResourceContextParameters.UUID, contextInfo));
        assertEquals("urn:uuid:7be07495-1aaa-4a69-8b92-3ec162122b34",
                resourceContext.getParameterValue(ResourceContextParameters.TRACK_FILE_ID, contextInfo));
        assertEquals("urn:uuid:fe0ca4ce-2877-411b-9fbf-e8c64d561a6d",
                resourceContext.getParameterValue(ResourceContextParameters.ESSENCE_DESC_ID, contextInfo));
        assertEquals("0",
                resourceContext.getParameterValue(ResourceContextParameters.NUM, contextInfo));
        assertEquals(ImpUtils.getAbsolutePathEssenceDesc("Netflix_Plugfest_Oct2015_ENG20.mxf"),
                resourceContext.getParameterValue(ResourceContextParameters.ESSENCE, contextInfo));

        assertEquals("1439992",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
        assertEquals("1439992",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_FRAME_EDIT_UNIT, contextInfo));
        assertEquals("00:00:29.999",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_TIMECODE, contextInfo));
        assertEquals("29999",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_MS, contextInfo));

        assertEquals("15750288",
                resourceContext.getParameterValue(ResourceContextParameters.END_TIME_EDIT_UNIT, contextInfo));
        assertEquals("00:05:28.131",
                resourceContext.getParameterValue(ResourceContextParameters.END_TIME_TIMECODE, contextInfo));
        assertEquals("328131",
                resourceContext.getParameterValue(ResourceContextParameters.END_TIME_MS, contextInfo));

        assertEquals("14310296",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
        assertEquals("14310296",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_FRAME_EDIT_UNIT, contextInfo));
        assertEquals("00:04:58.131",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_TIMECODE, contextInfo));
        assertEquals("298131",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_MS, contextInfo));

        assertEquals("1",
                resourceContext.getParameterValue(ResourceContextParameters.REPEAT_COUNT, contextInfo));
        assertEquals("0",
                resourceContext.getParameterValue(ResourceContextParameters.REPEAT, contextInfo));
        assertEquals("48000 1",
                resourceContext.getParameterValue(ResourceContextParameters.EDIT_RATE, contextInfo));
    }

    @Test
    public void testEssenceDescriptorAudioParameters() throws Exception {
        // UUIDs as defined in CPL.xml.
        // essence must be a full path!

        // audio1
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:e9e18479-6116-40f4-a891-9ef999c80f2a"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:55b1a5a6-d62d-4903-aca5-f766f86696fc"))
                .setSequenceType(SequenceType.AUDIO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:7a9aec8a-ab50-4310-bb56-1f35411bd7ac", 0))
                .build();

        assertEquals("FL+FR+FC+LFE+SL+SR",
                resourceContext.getParameterValue(ResourceContextParameters.CHANNELS_LAYOUT, contextInfo));

        // audio2
        contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:e9e18479-6116-40f4-a891-9ef999c80f2a"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:b8ee769c-b768-451c-bb5f-9c38fd3a5d18"))
                .setSequenceType(SequenceType.AUDIO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:8e910d09-a3e8-4f2e-88b4-37beb3e3c883", 0))
                .build();

        assertEquals("FL+FR",
                resourceContext.getParameterValue(ResourceContextParameters.CHANNELS_LAYOUT, contextInfo));
    }
}
