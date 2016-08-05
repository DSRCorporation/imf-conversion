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
import com.netflix.imfutility.conversion.templateParameter.context.ResourceKey;
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
import static org.junit.Assert.assertArrayEquals;

/**
 * <ul>
 * <li>Tests that resource context is filled correctly from CPL.</li>
 * </ul>
 */
public class CplContextBuilderResourceContextTest {

    private static ResourceTemplateParameterContext resourceContext;

    @BeforeClass
    public static void setUpAll() throws Exception {
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        AssetMap assetMap = new AssetMapParser().parse(ImpUtils.getImpFolder(), ImpUtils.getCorrectAssetmap());
        new CplContextBuilder(contextProvider, assetMap, ImpUtils.getCorrectCpl()).build();

        resourceContext = contextProvider.getResourceContext();
    }

    @Test
    public void testStartTime_EntryPoint_EditRate() throws Exception {
        // UUIDs as defined in CPL.xmlo
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                .setSequenceType(SequenceType.VIDEO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1eb", 0))
                .build();
        assertEquals("300",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
        assertEquals("00:00:06.000",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_TIMECODE, contextInfo));
        assertEquals("6000",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_MS, contextInfo));

    }

    @Test
    public void testStartTime_NoEntryPoint_EditRate() throws Exception {
        // UUIDs as defined in CPL.xml
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                .setSequenceType(SequenceType.VIDEO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ea", 0))
                .build();
        assertEquals("0",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
        assertEquals("00:00:00.000",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_TIMECODE, contextInfo));
        assertEquals("0",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_MS, contextInfo));

    }

    @Test
    public void testStartTime_EntryPoint_NoEditRate() throws Exception {
        // UUIDs as defined in CPL.xml
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                .setSequenceType(SequenceType.VIDEO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ea", 0))
                .build();
        assertEquals("600",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
        assertEquals("00:00:12.000",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_TIMECODE, contextInfo));
        assertEquals("12000",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_MS, contextInfo));

    }

    @Test
    public void testStartTime_NoEntryPoint_NoEditRate() throws Exception {
        // UUIDs as defined in CPL.xml
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                .setSequenceType(SequenceType.VIDEO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ea", 0))
                .build();
        assertEquals("0",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
        assertEquals("00:00:00.000",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_TIMECODE, contextInfo));
        assertEquals("0",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_MS, contextInfo));
    }

    @Test
    public void testDuration_SourceDuration() throws Exception {
        // UUIDs as defined in CPL.xml
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                .setSequenceType(SequenceType.VIDEO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1eb", 0))
                .build();
        assertEquals("150",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
        assertEquals("00:00:03.000",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_TIMECODE, contextInfo));
        assertEquals("3000",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_MS, contextInfo));
    }

    @Test
    public void testDuration_NoSourceDuration_EntryPoint() throws Exception {
        // UUIDs as defined in CPL.xml
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                .setSequenceType(SequenceType.VIDEO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ea", 0))
                .build();
        assertEquals("1097",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
        assertEquals("00:00:21.940",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_TIMECODE, contextInfo));
        assertEquals("21940",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_MS, contextInfo));
    }

    @Test
    public void testDuration_NoSourceDuration_NoEntryPoint() throws Exception {
        // UUIDs as defined in CPL.xml
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712"))
                .setSequenceType(SequenceType.AUDIO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9097", 0))
                .build();
        assertEquals("1629120",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
        assertEquals("00:00:33.940",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_TIMECODE, contextInfo));
        assertEquals("33940",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_MS, contextInfo));
    }

    @Test
    public void testEndTime_NoEntryPoint_SourceDuration() throws Exception {
        // UUIDs as defined in CPL.xml
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                .setSequenceType(SequenceType.VIDEO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ea", 0))
                .build();
        assertEquals("300",
                resourceContext.getParameterValue(ResourceContextParameters.END_TIME_EDIT_UNIT, contextInfo));
        assertEquals("00:00:06.000",
                resourceContext.getParameterValue(ResourceContextParameters.END_TIME_TIMECODE, contextInfo));
        assertEquals("6000",
                resourceContext.getParameterValue(ResourceContextParameters.END_TIME_MS, contextInfo));
    }

    @Test
    public void testEndTime_NoEntryPoint_NoSourceDuration() throws Exception {
        // UUIDs as defined in CPL.xml
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712"))
                .setSequenceType(SequenceType.AUDIO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9097", 0))
                .build();
        assertEquals("1629120",
                resourceContext.getParameterValue(ResourceContextParameters.END_TIME_EDIT_UNIT, contextInfo));
        assertEquals("00:00:33.940",
                resourceContext.getParameterValue(ResourceContextParameters.END_TIME_TIMECODE, contextInfo));
        assertEquals("33940",
                resourceContext.getParameterValue(ResourceContextParameters.END_TIME_MS, contextInfo));
    }

    @Test
    public void testEndTime_EntryPoint_NoSourceDuration() throws Exception {
        // UUIDs as defined in CPL.xml
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711"))
                .setSequenceType(SequenceType.AUDIO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9094", 0))
                .build();
        assertEquals("1629120",
                resourceContext.getParameterValue(ResourceContextParameters.END_TIME_EDIT_UNIT, contextInfo));
        assertEquals("00:00:33.940",
                resourceContext.getParameterValue(ResourceContextParameters.END_TIME_TIMECODE, contextInfo));
        assertEquals("33940",
                resourceContext.getParameterValue(ResourceContextParameters.END_TIME_MS, contextInfo));
    }

    @Test
    public void testEndTime_EntryPoint_SourceDuration() throws Exception {
        // UUIDs as defined in CPL.xml
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                .setSequenceType(SequenceType.VIDEO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1eb", 0))
                .build();
        assertEquals("450",
                resourceContext.getParameterValue(ResourceContextParameters.END_TIME_EDIT_UNIT, contextInfo));
        assertEquals("00:00:09.000",
                resourceContext.getParameterValue(ResourceContextParameters.END_TIME_TIMECODE, contextInfo));
        assertEquals("9000",
                resourceContext.getParameterValue(ResourceContextParameters.END_TIME_MS, contextInfo));
    }

    @Test
    public void testEditRate() throws Exception {
        // UUIDs as defined in CPL.xml
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712"))
                .setSequenceType(SequenceType.AUDIO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9097", 0))
                .build();
        assertEquals("48000 1",
                resourceContext.getParameterValue(ResourceContextParameters.EDIT_RATE, contextInfo));
    }

    @Test
    public void testEditRate_NoEditRate() throws Exception {
        // UUIDs as defined in CPL.xml
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                .setSequenceType(SequenceType.VIDEO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ea", 0))
                .build();
        assertEquals("50 1",
                resourceContext.getParameterValue(ResourceContextParameters.EDIT_RATE, contextInfo));
    }

    @Test
    public void testUuidsAndCountWithRepeat() throws Exception {
        // UUIDs as defined in CPL.xml

        // first segment

        // video
        ResourceKey resKey = ResourceKey.create(
                SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"),
                SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"),
                SequenceType.VIDEO);
        assertEquals(5, resourceContext.getResourceCount(resKey));
        assertArrayEquals(
                new ResourceUUID[]{
                        ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ea", 0),
                        ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1eb", 0),
                        ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ec", 0),
                        ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ec", 1),
                        ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ec", 2)
                },
                resourceContext.getUuids(resKey).toArray(new ResourceUUID[]{})
        );

        // 1st audio
        resKey = ResourceKey.create(
                SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"),
                SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711"),
                SequenceType.AUDIO);
        assertEquals(2, resourceContext.getResourceCount(resKey));
        assertArrayEquals(
                new ResourceUUID[]{
                        ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9094", 0),
                        ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9094", 1)
                },
                resourceContext.getUuids(resKey).toArray(new ResourceUUID[]{})
        );

        // 2d audio
        resKey = ResourceKey.create(
                SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"),
                SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712"),
                SequenceType.AUDIO);
        assertEquals(2, resourceContext.getResourceCount(resKey));
        assertArrayEquals(
                new ResourceUUID[]{
                        ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9095", 0),
                        ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9096", 0)
                },
                resourceContext.getUuids(resKey).toArray(new ResourceUUID[]{})
        );

        // second segment

        // video
        resKey = ResourceKey.create(
                SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17"),
                SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"),
                SequenceType.VIDEO);
        assertEquals(3, resourceContext.getResourceCount(resKey));
        assertArrayEquals(
                new ResourceUUID[]{
                        ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ea", 0),
                        ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1eb", 0),
                        ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1eb", 1)
                },
                resourceContext.getUuids(resKey).toArray(new ResourceUUID[]{})
        );

        // 1st audio
        resKey = ResourceKey.create(
                SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17"),
                SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711"),
                SequenceType.AUDIO);
        assertEquals(1, resourceContext.getResourceCount(resKey));
        assertArrayEquals(
                new ResourceUUID[]{
                        ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9094", 0)
                },
                resourceContext.getUuids(resKey).toArray(new ResourceUUID[]{})
        );

        // 2d audio
        resKey = ResourceKey.create(
                SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17"),
                SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712"),
                SequenceType.AUDIO);
        assertEquals(1, resourceContext.getResourceCount(resKey));
        assertArrayEquals(
                new ResourceUUID[]{
                        ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9097", 0)
                },
                resourceContext.getUuids(resKey).toArray(new ResourceUUID[]{})
        );
    }

    @Test
    //CHECKSTYLE:OFF
    public void testAudioParameters() throws Exception {
        // UUIDs as defined in CPL.xml.
        // essence must be a full path!

        // first segment

        // 1st audio:
        for (int repeat = 0; repeat < 2; repeat++) {
            ContextInfo contextInfo = new ContextInfoBuilder()
                    .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                    .setSequenceUuid(SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711"))
                    .setSequenceType(SequenceType.AUDIO)
                    .setResourceUuid(ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9094", repeat))
                    .build();

            assertEquals("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9094",
                    resourceContext.getParameterValue(ResourceContextParameters.UUID, contextInfo));
            assertEquals(String.valueOf(0 + repeat),
                    resourceContext.getParameterValue(ResourceContextParameters.NUM, contextInfo));
            assertEquals(ImpUtils.getAbsolutePath("Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV_Audio.mxf"),
                    resourceContext.getParameterValue(ResourceContextParameters.ESSENCE, contextInfo));

            assertEquals("0",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
            assertEquals("0",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_FRAME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:00.000",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_TIMECODE, contextInfo));
            assertEquals("0",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_MS, contextInfo));

            assertEquals("432000",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:09.000",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_TIMECODE, contextInfo));
            assertEquals("9000",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_MS, contextInfo));

            assertEquals("432000",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
            assertEquals("432000",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_FRAME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:09.000",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_TIMECODE, contextInfo));
            assertEquals("9000",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_MS, contextInfo));

            assertEquals("2",
                    resourceContext.getParameterValue(ResourceContextParameters.REPEAT_COUNT, contextInfo));
            assertEquals(String.valueOf(repeat),
                    resourceContext.getParameterValue(ResourceContextParameters.REPEAT, contextInfo));
            assertEquals("48000 1",
                    resourceContext.getParameterValue(ResourceContextParameters.EDIT_RATE, contextInfo));
        }

        // 2d audio: 1st resource
        for (int repeat = 0; repeat < 1; repeat++) {
            ContextInfo contextInfo = new ContextInfoBuilder()
                    .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                    .setSequenceUuid(SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712"))
                    .setSequenceType(SequenceType.AUDIO)
                    .setResourceUuid(ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9095", repeat))
                    .build();

            assertEquals("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9095",
                    resourceContext.getParameterValue(ResourceContextParameters.UUID, contextInfo));
            assertEquals(String.valueOf(0 + repeat),
                    resourceContext.getParameterValue(ResourceContextParameters.NUM, contextInfo));
            assertEquals(ImpUtils.getAbsolutePath("Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV_Audio.mxf"),
                    resourceContext.getParameterValue(ResourceContextParameters.ESSENCE, contextInfo));

            assertEquals("0",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
            assertEquals("0",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_FRAME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:00.000",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_TIMECODE, contextInfo));
            assertEquals("0",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_MS, contextInfo));

            assertEquals("288000",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:06.000",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_TIMECODE, contextInfo));
            assertEquals("6000",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_MS, contextInfo));

            assertEquals("288000",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
            assertEquals("288000",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_FRAME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:06.000",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_TIMECODE, contextInfo));
            assertEquals("6000",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_MS, contextInfo));

            assertEquals("1",
                    resourceContext.getParameterValue(ResourceContextParameters.REPEAT_COUNT, contextInfo));
            assertEquals(String.valueOf(repeat),
                    resourceContext.getParameterValue(ResourceContextParameters.REPEAT, contextInfo));
            assertEquals("48000 1",
                    resourceContext.getParameterValue(ResourceContextParameters.EDIT_RATE, contextInfo));
        }

        // 2d audio: 2d resource
        for (int repeat = 0; repeat < 1; repeat++) {
            ContextInfo contextInfo = new ContextInfoBuilder()
                    .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                    .setSequenceUuid(SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712"))
                    .setSequenceType(SequenceType.AUDIO)
                    .setResourceUuid(ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9096", repeat))
                    .build();

            assertEquals("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9096",
                    resourceContext.getParameterValue(ResourceContextParameters.UUID, contextInfo));
            assertEquals(String.valueOf(1 + repeat),
                    resourceContext.getParameterValue(ResourceContextParameters.NUM, contextInfo));
            assertEquals(ImpUtils.getAbsolutePath("Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV_Audio_2.mxf"),
                    resourceContext.getParameterValue(ResourceContextParameters.ESSENCE, contextInfo));

            assertEquals("288000",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
            assertEquals("288000",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_FRAME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:06.000",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_TIMECODE, contextInfo));
            assertEquals("6000",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_MS, contextInfo));

            assertEquals("432000",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:09.000",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_TIMECODE, contextInfo));
            assertEquals("9000",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_MS, contextInfo));

            assertEquals("144000",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
            assertEquals("144000",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_FRAME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:03.000",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_TIMECODE, contextInfo));
            assertEquals("3000",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_MS, contextInfo));

            assertEquals("1",
                    resourceContext.getParameterValue(ResourceContextParameters.REPEAT_COUNT, contextInfo));
            assertEquals(String.valueOf(repeat),
                    resourceContext.getParameterValue(ResourceContextParameters.REPEAT, contextInfo));
            assertEquals("48000 1",
                    resourceContext.getParameterValue(ResourceContextParameters.EDIT_RATE, contextInfo));
        }

        // second segment

        // 1st audio:
        for (int repeat = 0; repeat < 1; repeat++) {
            ContextInfo contextInfo = new ContextInfoBuilder()
                    .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17"))
                    .setSequenceUuid(SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711"))
                    .setSequenceType(SequenceType.AUDIO)
                    .setResourceUuid(ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9094", repeat))
                    .build();

            assertEquals("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9094",
                    resourceContext.getParameterValue(ResourceContextParameters.UUID, contextInfo));
            assertEquals(String.valueOf(0 + repeat),
                    resourceContext.getParameterValue(ResourceContextParameters.NUM, contextInfo));
            assertEquals(ImpUtils.getAbsolutePath("Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV_Audio.mxf"),
                    resourceContext.getParameterValue(ResourceContextParameters.ESSENCE, contextInfo));

            assertEquals("576000",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
            assertEquals("576000",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_FRAME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:12.000",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_TIMECODE, contextInfo));
            assertEquals("12000",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_MS, contextInfo));

            assertEquals("1629120",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:33.940",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_TIMECODE, contextInfo));
            assertEquals("33940",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_MS, contextInfo));

            assertEquals("1053120",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
            assertEquals("1053120",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_FRAME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:21.940",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_TIMECODE, contextInfo));
            assertEquals("21940",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_MS, contextInfo));

            assertEquals("1",
                    resourceContext.getParameterValue(ResourceContextParameters.REPEAT_COUNT, contextInfo));
            assertEquals(String.valueOf(repeat),
                    resourceContext.getParameterValue(ResourceContextParameters.REPEAT, contextInfo));
            assertEquals("48000 1",
                    resourceContext.getParameterValue(ResourceContextParameters.EDIT_RATE, contextInfo));
        }

        // 2d audio:
        for (int repeat = 0; repeat < 1; repeat++) {
            ContextInfo contextInfo = new ContextInfoBuilder()
                    .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17"))
                    .setSequenceUuid(SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712"))
                    .setSequenceType(SequenceType.AUDIO)
                    .setResourceUuid(ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9097", repeat))
                    .build();

            assertEquals("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9097",
                    resourceContext.getParameterValue(ResourceContextParameters.UUID, contextInfo));
            assertEquals(String.valueOf(0 + repeat),
                    resourceContext.getParameterValue(ResourceContextParameters.NUM, contextInfo));
            assertEquals(ImpUtils.getAbsolutePath("Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV_Audio_2.mxf"),
                    resourceContext.getParameterValue(ResourceContextParameters.ESSENCE, contextInfo));

            assertEquals("0",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
            assertEquals("0",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_FRAME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:00.000",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_TIMECODE, contextInfo));
            assertEquals("0",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_MS, contextInfo));

            assertEquals("1629120",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:33.940",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_TIMECODE, contextInfo));
            assertEquals("33940",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_MS, contextInfo));

            assertEquals("1629120",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
            assertEquals("1629120",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_FRAME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:33.940",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_TIMECODE, contextInfo));
            assertEquals("33940",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_MS, contextInfo));

            assertEquals("1",
                    resourceContext.getParameterValue(ResourceContextParameters.REPEAT_COUNT, contextInfo));
            assertEquals(String.valueOf(repeat),
                    resourceContext.getParameterValue(ResourceContextParameters.REPEAT, contextInfo));
            assertEquals("48000 1",
                    resourceContext.getParameterValue(ResourceContextParameters.EDIT_RATE, contextInfo));
        }
    }
    //CHECKSTYLE:ON

    @Test
    public void testVideoParameters() throws Exception {
        // UUIDs as defined in CPL.xml
        // essence must be a full path!

        // first segment

        // 1st resource
        for (int repeat = 0; repeat < 1; repeat++) {
            ContextInfo contextInfo = new ContextInfoBuilder()
                    .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                    .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                    .setSequenceType(SequenceType.VIDEO)
                    .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ea", repeat))
                    .build();

            assertEquals("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ea",
                    resourceContext.getParameterValue(ResourceContextParameters.UUID, contextInfo));
            assertEquals(String.valueOf(0 + repeat),
                    resourceContext.getParameterValue(ResourceContextParameters.NUM, contextInfo));
            assertEquals(ImpUtils.getAbsolutePath("Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV.mxf"),
                    resourceContext.getParameterValue(ResourceContextParameters.ESSENCE, contextInfo));

            assertEquals("0",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
            assertEquals("0",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_FRAME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:00.000",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_TIMECODE, contextInfo));
            assertEquals("0",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_MS, contextInfo));

            assertEquals("300",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:06.000",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_TIMECODE, contextInfo));
            assertEquals("6000",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_MS, contextInfo));

            assertEquals("300",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
            assertEquals("300",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_FRAME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:06.000",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_TIMECODE, contextInfo));
            assertEquals("6000",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_MS, contextInfo));

            assertEquals("1",
                    resourceContext.getParameterValue(ResourceContextParameters.REPEAT_COUNT, contextInfo));
            assertEquals(String.valueOf(repeat),
                    resourceContext.getParameterValue(ResourceContextParameters.REPEAT, contextInfo));
            assertEquals("50 1",
                    resourceContext.getParameterValue(ResourceContextParameters.EDIT_RATE, contextInfo));
        }

        // 2d resource
        for (int repeat = 0; repeat < 1; repeat++) {
            ContextInfo contextInfo = new ContextInfoBuilder()
                    .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                    .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                    .setSequenceType(SequenceType.VIDEO)
                    .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1eb", repeat))
                    .build();

            assertEquals("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1eb",
                    resourceContext.getParameterValue(ResourceContextParameters.UUID, contextInfo));
            assertEquals(String.valueOf(1 + repeat),
                    resourceContext.getParameterValue(ResourceContextParameters.NUM, contextInfo));
            assertEquals(ImpUtils.getAbsolutePath("Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV_2.mxf"),
                    resourceContext.getParameterValue(ResourceContextParameters.ESSENCE, contextInfo));

            assertEquals("300",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
            assertEquals("300",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_FRAME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:06.000",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_TIMECODE, contextInfo));
            assertEquals("6000",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_MS, contextInfo));

            assertEquals("450",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:09.000",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_TIMECODE, contextInfo));
            assertEquals("9000",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_MS, contextInfo));

            assertEquals("150",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
            assertEquals("150",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_FRAME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:03.000",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_TIMECODE, contextInfo));
            assertEquals("3000",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_MS, contextInfo));

            assertEquals("1",
                    resourceContext.getParameterValue(ResourceContextParameters.REPEAT_COUNT, contextInfo));
            assertEquals(String.valueOf(repeat),
                    resourceContext.getParameterValue(ResourceContextParameters.REPEAT, contextInfo));
            assertEquals("50 1",
                    resourceContext.getParameterValue(ResourceContextParameters.EDIT_RATE, contextInfo));
        }

        // 3d resource
        for (int repeat = 0; repeat < 3; repeat++) {
            ContextInfo contextInfo = new ContextInfoBuilder()
                    .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                    .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                    .setSequenceType(SequenceType.VIDEO)
                    .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ec", repeat))
                    .build();

            assertEquals("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ec",
                    resourceContext.getParameterValue(ResourceContextParameters.UUID, contextInfo));
            assertEquals(String.valueOf(2 + repeat),
                    resourceContext.getParameterValue(ResourceContextParameters.NUM, contextInfo));
            assertEquals(ImpUtils.getAbsolutePath("Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV_2.mxf"),
                    resourceContext.getParameterValue(ResourceContextParameters.ESSENCE, contextInfo));

            assertEquals("300",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
            assertEquals("300",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_FRAME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:06.000",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_TIMECODE, contextInfo));
            assertEquals("6000",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_MS, contextInfo));

            assertEquals("450",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:09.000",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_TIMECODE, contextInfo));
            assertEquals("9000",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_MS, contextInfo));

            assertEquals("150",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
            assertEquals("150",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_FRAME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:03.000",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_TIMECODE, contextInfo));
            assertEquals("3000",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_MS, contextInfo));

            assertEquals("3",
                    resourceContext.getParameterValue(ResourceContextParameters.REPEAT_COUNT, contextInfo));
            assertEquals(String.valueOf(repeat),
                    resourceContext.getParameterValue(ResourceContextParameters.REPEAT, contextInfo));
            assertEquals("50 1",
                    resourceContext.getParameterValue(ResourceContextParameters.EDIT_RATE, contextInfo));
        }

        // second segment

        // 1st resource
        for (int repeat = 0; repeat < 1; repeat++) {
            ContextInfo contextInfo = new ContextInfoBuilder()
                    .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17"))
                    .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                    .setSequenceType(SequenceType.VIDEO)
                    .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ea", repeat))
                    .build();

            assertEquals("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ea",
                    resourceContext.getParameterValue(ResourceContextParameters.UUID, contextInfo));
            assertEquals(String.valueOf(0 + repeat),
                    resourceContext.getParameterValue(ResourceContextParameters.NUM, contextInfo));
            assertEquals(ImpUtils.getAbsolutePath("Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV.mxf"),
                    resourceContext.getParameterValue(ResourceContextParameters.ESSENCE, contextInfo));

            assertEquals("600",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
            assertEquals("600",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_FRAME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:12.000",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_TIMECODE, contextInfo));
            assertEquals("12000",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_MS, contextInfo));

            assertEquals("1697",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:33.940",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_TIMECODE, contextInfo));
            assertEquals("33940",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_MS, contextInfo));

            assertEquals("1097",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
            assertEquals("1097",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_FRAME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:21.940",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_TIMECODE, contextInfo));
            assertEquals("21940",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_MS, contextInfo));

            assertEquals("1",
                    resourceContext.getParameterValue(ResourceContextParameters.REPEAT_COUNT, contextInfo));
            assertEquals(String.valueOf(repeat),
                    resourceContext.getParameterValue(ResourceContextParameters.REPEAT, contextInfo));
            assertEquals("50 1",
                    resourceContext.getParameterValue(ResourceContextParameters.EDIT_RATE, contextInfo));
        }

        // 2d resource
        for (int repeat = 0; repeat < 2; repeat++) {
            ContextInfo contextInfo = new ContextInfoBuilder()
                    .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17"))
                    .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                    .setSequenceType(SequenceType.VIDEO)
                    .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1eb", repeat))
                    .build();

            assertEquals("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1eb",
                    resourceContext.getParameterValue(ResourceContextParameters.UUID, contextInfo));
            assertEquals(String.valueOf(1 + repeat),
                    resourceContext.getParameterValue(ResourceContextParameters.NUM, contextInfo));
            assertEquals(ImpUtils.getAbsolutePath("Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV_2.mxf"),
                    resourceContext.getParameterValue(ResourceContextParameters.ESSENCE, contextInfo));

            assertEquals("600",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
            assertEquals("600",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_FRAME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:12.000",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_TIMECODE, contextInfo));
            assertEquals("12000",
                    resourceContext.getParameterValue(ResourceContextParameters.START_TIME_MS, contextInfo));

            assertEquals("1697",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:33.940",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_TIMECODE, contextInfo));
            assertEquals("33940",
                    resourceContext.getParameterValue(ResourceContextParameters.END_TIME_MS, contextInfo));

            assertEquals("1097",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
            assertEquals("1097",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_FRAME_EDIT_UNIT, contextInfo));
            assertEquals("00:00:21.940",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_TIMECODE, contextInfo));
            assertEquals("21940",
                    resourceContext.getParameterValue(ResourceContextParameters.DURATION_MS, contextInfo));

            assertEquals("2",
                    resourceContext.getParameterValue(ResourceContextParameters.REPEAT_COUNT, contextInfo));
            assertEquals(String.valueOf(repeat),
                    resourceContext.getParameterValue(ResourceContextParameters.REPEAT, contextInfo));
            assertEquals("50 1",
                    resourceContext.getParameterValue(ResourceContextParameters.EDIT_RATE, contextInfo));
        }
    }

    @Test
    public void testFrameStartTimeAndDuration() throws Exception {
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        AssetMap assetMap = new AssetMapParser().parse(ImpUtils.getImpFolder(), ImpUtils.getCorrectAssetmap());
        // get a CPL that uses one essence for both audio and video
        new CplContextBuilder(contextProvider, assetMap, ImpUtils.getCorrectCplOneEssence()).build();

        ResourceTemplateParameterContext resourceContext = contextProvider.getResourceContext();

        // UUIDs as defined in CPL.xml.

        // first segment

        // video
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                .setSequenceType(SequenceType.VIDEO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ea", 0))
                .build();
        assertEquals("0",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_FRAME_EDIT_UNIT, contextInfo));
        assertEquals("300",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_FRAME_EDIT_UNIT, contextInfo));

        // audio (the values are the same as for video!)
        contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711"))
                .setSequenceType(SequenceType.AUDIO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9094", 0))
                .build();
        assertEquals("0",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_FRAME_EDIT_UNIT, contextInfo));
        assertEquals("300",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_FRAME_EDIT_UNIT, contextInfo));

        // second segment

        // video
        contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                .setSequenceType(SequenceType.VIDEO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ea", 0))
                .build();
        assertEquals("600",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_FRAME_EDIT_UNIT, contextInfo));
        assertEquals("1097",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_FRAME_EDIT_UNIT, contextInfo));

        // audio (the values are the same as for video!)
        contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711"))
                .setSequenceType(SequenceType.AUDIO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9094", 0))
                .build();
        assertEquals("600",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_FRAME_EDIT_UNIT, contextInfo));
        assertEquals("1097",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_FRAME_EDIT_UNIT, contextInfo));
    }

}
