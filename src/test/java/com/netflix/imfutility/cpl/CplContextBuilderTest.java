package com.netflix.imfutility.cpl;

import com.netflix.imfutility.asset.AssetMap;
import com.netflix.imfutility.asset.AssetMapParser;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfoBuilder;
import com.netflix.imfutility.conversion.templateParameter.context.*;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SegmentContextParameters;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters;
import com.netflix.imfutility.cpl.uuid.ResourceUUID;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.util.AssetmapUtils;
import com.netflix.imfutility.util.CplUtils;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import com.netflix.imfutility.xml.XmlParsingException;
import com.netflix.imfutility.xsd.conversion.SequenceType;
import org.junit.Test;

import java.io.FileNotFoundException;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertArrayEquals;

/**
 * <ul>
 * <li>Tests that cpl.xml can be parsed and mapped to Java model successfully.</li>
 * <li>Tests the XSD validation is applied to the cpl.xml and an exception is thrown is validation doesn't pass.</li>
 * <li>Tests Sequence, segment and resource contexts are filled correctly.</li>
 * </ul>
 */
public class CplContextBuilderTest {

    @Test
    public void testParseCorrectConfigWithoutErrors() throws Exception {
        createCplContextBuilder().build(CplUtils.getCorrectCpl());
    }

    @Test(expected = XmlParsingException.class)
    public void testParseBrokenXml() throws Exception {
        createCplContextBuilder().build(CplUtils.getBrokenXmlCpl());
    }

    @Test(expected = XmlParsingException.class)
    public void testParseInvalidXsd() throws Exception {
        createCplContextBuilder().build(CplUtils.getInvalidXsdCpl());
    }

    @Test(expected = FileNotFoundException.class)
    public void testParseInvalidFilePath() throws Exception {
        createCplContextBuilder().build("C:/invalid-path");
    }

    @Test
    public void testSequenceContextCreatedCorrectly() throws Exception {
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        AssetMap assetMap = new AssetMapParser().parse(AssetmapUtils.getImpFolder(), AssetmapUtils.getCorrectAssetmap());
        new CplContextBuilder(contextProvider, assetMap).build(CplUtils.getCorrectCpl());

        SequenceTemplateParameterContext sequenceContext = contextProvider.getSequenceContext();
        assertEquals(2, sequenceContext.getSequenceCount(SequenceType.AUDIO));
        assertEquals(1, sequenceContext.getSequenceCount(SequenceType.VIDEO));

        // UUIDs as defined in CPL.xml
        assertArrayEquals(
                new SequenceType[]{SequenceType.VIDEO, SequenceType.AUDIO},
                sequenceContext.getSequenceTypes().toArray(new SequenceType[]{}));

        assertArrayEquals(
                new SequenceUUID[]{
                        SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711"),
                        SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712")},
                sequenceContext.getUuids(SequenceType.AUDIO).toArray(new SequenceUUID[]{}));
        assertArrayEquals(
                new SequenceUUID[]{SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d")},
                sequenceContext.getUuids(SequenceType.VIDEO).toArray(new SequenceUUID[]{}));

        ContextInfo contextInfo = new ContextInfoBuilder().setSequenceType(SequenceType.AUDIO)
                .setSequenceUuid(SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711")).build();
        assertEquals("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711",
                sequenceContext.getParameterValue(SequenceContextParameters.UUID, contextInfo));
        assertEquals("0",
                sequenceContext.getParameterValue(SequenceContextParameters.NUM, contextInfo));
        assertEquals("audio",
                sequenceContext.getParameterValue(SequenceContextParameters.TYPE, contextInfo));

        contextInfo = new ContextInfoBuilder().setSequenceType(SequenceType.AUDIO)
                .setSequenceUuid(SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712")).build();
        assertEquals("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712",
                sequenceContext.getParameterValue(SequenceContextParameters.UUID, contextInfo));
        assertEquals("1",
                sequenceContext.getParameterValue(SequenceContextParameters.NUM, contextInfo));
        assertEquals("audio",
                sequenceContext.getParameterValue(SequenceContextParameters.TYPE, contextInfo));

        contextInfo = new ContextInfoBuilder().setSequenceType(SequenceType.VIDEO)
                .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d")).build();
        assertEquals("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d",
                sequenceContext.getParameterValue(SequenceContextParameters.UUID, contextInfo));
        assertEquals("0",
                sequenceContext.getParameterValue(SequenceContextParameters.NUM, contextInfo));
        assertEquals("video",
                sequenceContext.getParameterValue(SequenceContextParameters.TYPE, contextInfo));
    }

    @Test
    public void testSegmentContextCreatedCorrectly() throws Exception {
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        AssetMap assetMap = new AssetMapParser().parse(AssetmapUtils.getImpFolder(), AssetmapUtils.getCorrectAssetmap());
        new CplContextBuilder(contextProvider, assetMap).build(CplUtils.getCorrectCpl());

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

    @Test
    public void testResourceContextUuidsAndCountCreatedCorrectly() throws Exception {
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        AssetMap assetMap = new AssetMapParser().parse(AssetmapUtils.getImpFolder(), AssetmapUtils.getCorrectAssetmap());
        new CplContextBuilder(contextProvider, assetMap).build(CplUtils.getCorrectCpl());

        ResourceTemplateParameterContext resourceContext = contextProvider.getResourceContext();

        // UUIDs as defined in CPL.xml

        // first audio segment of the fist audio track
        ResourceKey resKey = ResourceKey.create(
                SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"),
                SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711"),
                SequenceType.AUDIO);
        assertEquals(1, resourceContext.getResourceCount(resKey));
        assertArrayEquals(
                new ResourceUUID[]{ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9094")},
                resourceContext.getUuids(resKey).toArray(new ResourceUUID[]{}));

        // second audio segment of the fist audio track
        resKey = ResourceKey.create(
                SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17"),
                SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711"),
                SequenceType.AUDIO);
        assertEquals(1, resourceContext.getResourceCount(resKey));
        assertArrayEquals(
                new ResourceUUID[]{ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9094")},
                resourceContext.getUuids(resKey).toArray(new ResourceUUID[]{}));

        // first audio segment of the second audio track
        resKey = ResourceKey.create(
                SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"),
                SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712"),
                SequenceType.AUDIO);
        assertEquals(2, resourceContext.getResourceCount(resKey));
        assertArrayEquals(
                new ResourceUUID[]{
                        ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9095"),
                        ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9096")
                },
                resourceContext.getUuids(resKey).toArray(new ResourceUUID[]{}));

        // second audio segment of the second audio track
        resKey = ResourceKey.create(
                SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17"),
                SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712"),
                SequenceType.AUDIO);
        assertEquals(1, resourceContext.getResourceCount(resKey));
        assertArrayEquals(
                new ResourceUUID[]{ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9097")},
                resourceContext.getUuids(resKey).toArray(new ResourceUUID[]{}));

        // first video segment
        resKey = ResourceKey.create(
                SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"),
                SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"),
                SequenceType.VIDEO);
        assertEquals(2, resourceContext.getResourceCount(resKey));
        assertArrayEquals(
                new ResourceUUID[]{
                        ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ea"),
                        ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1eb")

                },
                resourceContext.getUuids(resKey).toArray(new ResourceUUID[]{}));

        // second video segment
        resKey = ResourceKey.create(
                SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17"),
                SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"),
                SequenceType.VIDEO);
        assertEquals(1, resourceContext.getResourceCount(resKey));
        assertArrayEquals(
                new ResourceUUID[]{ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ea")},
                resourceContext.getUuids(resKey).toArray(new ResourceUUID[]{}));
    }

    @Test
    public void testResourceContextAudioParametersCreatedCorrectly() throws Exception {
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        AssetMap assetMap = new AssetMapParser().parse(AssetmapUtils.getImpFolder(), AssetmapUtils.getCorrectAssetmap());
        new CplContextBuilder(contextProvider, assetMap).build(CplUtils.getCorrectCpl());

        ResourceTemplateParameterContext resourceContext = contextProvider.getResourceContext();

        // UUIDs as defined in CPL.xml.
        // essence must be a full path!

        // first segment of the first audio track
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711"))
                .setSequenceType(SequenceType.AUDIO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9094"))
                .build();
        assertEquals("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9094",
                resourceContext.getParameterValue(ResourceContextParameters.UUID, contextInfo));
        assertEquals("0",
                resourceContext.getParameterValue(ResourceContextParameters.NUM, contextInfo));
        assertEquals(AssetmapUtils.getAbsolutePath("Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV_Audio.mxf"),
                resourceContext.getParameterValue(ResourceContextParameters.ESSENCE, contextInfo));
        assertEquals("0",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
        assertEquals("00:00:00.000",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_TIMECODE, contextInfo));
        assertEquals("288000",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
        assertEquals("00:00:06.000",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_TIMECODE, contextInfo));

        // first segment of the second audio track: 1st resource
        contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712"))
                .setSequenceType(SequenceType.AUDIO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9095"))
                .build();
        assertEquals("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9095",
                resourceContext.getParameterValue(ResourceContextParameters.UUID, contextInfo));
        assertEquals("0",
                resourceContext.getParameterValue(ResourceContextParameters.NUM, contextInfo));
        assertEquals(AssetmapUtils.getAbsolutePath("Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV_Audio.mxf"),
                resourceContext.getParameterValue(ResourceContextParameters.ESSENCE, contextInfo));
        assertEquals("0",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
        assertEquals("00:00:00.000",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_TIMECODE, contextInfo));
        assertEquals("288000",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
        assertEquals("00:00:06.000",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_TIMECODE, contextInfo));

        // first segment of the second audio track: 2st resource
        contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712"))
                .setSequenceType(SequenceType.AUDIO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9096"))
                .build();
        assertEquals("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9096",
                resourceContext.getParameterValue(ResourceContextParameters.UUID, contextInfo));
        assertEquals("1",
                resourceContext.getParameterValue(ResourceContextParameters.NUM, contextInfo));
        assertEquals(AssetmapUtils.getAbsolutePath("Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV_Audio_2.mxf"),
                resourceContext.getParameterValue(ResourceContextParameters.ESSENCE, contextInfo));
        assertEquals("288000",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
        assertEquals("00:00:06.000",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_TIMECODE, contextInfo));
        assertEquals("288000",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
        assertEquals("00:00:06.000",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_TIMECODE, contextInfo));

        // second segment of the first audio track
        contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711"))
                .setSequenceType(SequenceType.AUDIO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9094"))
                .build();
        assertEquals("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9094",
                resourceContext.getParameterValue(ResourceContextParameters.UUID, contextInfo));
        assertEquals("0",
                resourceContext.getParameterValue(ResourceContextParameters.NUM, contextInfo));
        assertEquals(AssetmapUtils.getAbsolutePath("Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV_Audio.mxf"),
                resourceContext.getParameterValue(ResourceContextParameters.ESSENCE, contextInfo));
        assertEquals("576000",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
        assertEquals("00:00:12.000",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_TIMECODE, contextInfo));
        assertEquals("1053120",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
        assertEquals("00:00:21.940",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_TIMECODE, contextInfo));

        // second segment of the second audio track
        contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712"))
                .setSequenceType(SequenceType.AUDIO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9097"))
                .build();
        assertEquals("urn:uuid:895820ef-e379-4021-a69e-8a898b0a9097",
                resourceContext.getParameterValue(ResourceContextParameters.UUID, contextInfo));
        assertEquals("0",
                resourceContext.getParameterValue(ResourceContextParameters.NUM, contextInfo));
        assertEquals(AssetmapUtils.getAbsolutePath("Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV_Audio.mxf"),
                resourceContext.getParameterValue(ResourceContextParameters.ESSENCE, contextInfo));
        assertEquals("864000",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
        assertEquals("00:00:18.000",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_TIMECODE, contextInfo));
        assertEquals("765120",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
        assertEquals("00:00:15.940",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_TIMECODE, contextInfo));
    }

    @Test
    public void testResourceContextVideoParametersCreatedCorrectly() throws Exception {
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        AssetMap assetMap = new AssetMapParser().parse(AssetmapUtils.getImpFolder(), AssetmapUtils.getCorrectAssetmap());
        new CplContextBuilder(contextProvider, assetMap).build(CplUtils.getCorrectCpl());

        ResourceTemplateParameterContext resourceContext = contextProvider.getResourceContext();

        // UUIDs as defined in CPL.xml
        // essence must be a full path!

        // first segment of the video track: 1st resource
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                .setSequenceType(SequenceType.VIDEO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ea"))
                .build();
        assertEquals("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ea",
                resourceContext.getParameterValue(ResourceContextParameters.UUID, contextInfo));
        assertEquals("0",
                resourceContext.getParameterValue(ResourceContextParameters.NUM, contextInfo));
        assertEquals(AssetmapUtils.getAbsolutePath("Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV.mxf"),
                resourceContext.getParameterValue(ResourceContextParameters.ESSENCE, contextInfo));
        assertEquals("0",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
        assertEquals("00:00:00.000",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_TIMECODE, contextInfo));
        assertEquals("300",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
        assertEquals("00:00:06.000",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_TIMECODE, contextInfo));

        // first segment of the video track: 2st resource
        contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad16"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                .setSequenceType(SequenceType.VIDEO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1eb"))
                .build();
        assertEquals("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1eb",
                resourceContext.getParameterValue(ResourceContextParameters.UUID, contextInfo));
        assertEquals("1",
                resourceContext.getParameterValue(ResourceContextParameters.NUM, contextInfo));
        assertEquals(AssetmapUtils.getAbsolutePath("Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV_2.mxf"),
                resourceContext.getParameterValue(ResourceContextParameters.ESSENCE, contextInfo));
        assertEquals("300",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
        assertEquals("00:00:06.000",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_TIMECODE, contextInfo));
        assertEquals("150",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
        assertEquals("00:00:03.000",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_TIMECODE, contextInfo));

        // second segment of the video track
        contextInfo = new ContextInfoBuilder()
                .setSegmentUuid(SegmentUUID.create("urn:uuid:20544b5c-be3c-4274-8633-249ee8a5ad17"))
                .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d"))
                .setSequenceType(SequenceType.VIDEO)
                .setResourceUuid(ResourceUUID.create("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ea"))
                .build();
        assertEquals("urn:uuid:2404d06b-4d65-4511-9cac-42d41196a1ea",
                resourceContext.getParameterValue(ResourceContextParameters.UUID, contextInfo));
        assertEquals("0",
                resourceContext.getParameterValue(ResourceContextParameters.NUM, contextInfo));
        assertEquals(AssetmapUtils.getAbsolutePath("Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV.mxf"),
                resourceContext.getParameterValue(ResourceContextParameters.ESSENCE, contextInfo));
        assertEquals("600",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
        assertEquals("00:00:12.000",
                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_TIMECODE, contextInfo));
        assertEquals("1097",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
        assertEquals("00:00:21.940",
                resourceContext.getParameterValue(ResourceContextParameters.DURATION_TIMECODE, contextInfo));
    }

    private CplContextBuilder createCplContextBuilder() throws Exception {
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        AssetMap assetMap = new AssetMapParser().parse(AssetmapUtils.getImpFolder(), AssetmapUtils.getCorrectAssetmap());
        return new CplContextBuilder(contextProvider, assetMap);
    }

}
