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
import com.netflix.imfutility.conversion.templateParameter.context.SequenceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.util.ImpUtils;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertArrayEquals;

/**
 * <ul>
 * <li>Tests that sequence context is filled correctly from CPL.</li>
 * </ul>

 */
public class CplContextBuilderSequenceContextTest {

    @Test
    public void testAllSequenceTypesPresent() throws Exception {
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        AssetMap assetMap = new AssetMapParser().parse(ImpUtils.getImpFolder(), ImpUtils.getCorrectAssetmap());
        new CplContextBuilder(contextProvider, assetMap).build(ImpUtils.getCplSequence());

        SequenceTemplateParameterContext sequenceContext = contextProvider.getSequenceContext();
        assertArrayEquals(
                new SequenceType[]{SequenceType.VIDEO, SequenceType.AUDIO, SequenceType.SUBTITLE},
                sequenceContext.getSequenceTypes().toArray(new SequenceType[]{}));
    }

    @Test
    public void testVideoSequenceContext() throws Exception {
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        AssetMap assetMap = new AssetMapParser().parse(ImpUtils.getImpFolder(), ImpUtils.getCorrectAssetmap());
        new CplContextBuilder(contextProvider, assetMap).build(ImpUtils.getCplSequence());

        SequenceTemplateParameterContext sequenceContext = contextProvider.getSequenceContext();
        assertEquals(1, sequenceContext.getSequenceCount(SequenceType.VIDEO));

        // UUIDs as defined in CPL.xml
        assertArrayEquals(
                new SequenceUUID[]{SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d")},
                sequenceContext.getUuids(SequenceType.VIDEO).toArray(new SequenceUUID[]{}));

        ContextInfo contextInfo = new ContextInfoBuilder().setSequenceType(SequenceType.VIDEO)
                .setSequenceUuid(SequenceUUID.create("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d")).build();
        assertEquals("urn:uuid:a4f226e7-adac-45a4-adbf-83335cf02d0d",
                sequenceContext.getParameterValue(SequenceContextParameters.UUID, contextInfo));
        assertEquals("0",
                sequenceContext.getParameterValue(SequenceContextParameters.NUM, contextInfo));
        assertEquals("video",
                sequenceContext.getParameterValue(SequenceContextParameters.TYPE, contextInfo));
    }

    @Test
    public void testAudioSequenceContext() throws Exception {
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        AssetMap assetMap = new AssetMapParser().parse(ImpUtils.getImpFolder(), ImpUtils.getCorrectAssetmap());
        new CplContextBuilder(contextProvider, assetMap).build(ImpUtils.getCplSequence());

        SequenceTemplateParameterContext sequenceContext = contextProvider.getSequenceContext();
        assertEquals(2, sequenceContext.getSequenceCount(SequenceType.AUDIO));

        // UUIDs as defined in CPL.xml
        assertArrayEquals(
                new SequenceUUID[]{
                        SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711"),
                        SequenceUUID.create("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712")},
                sequenceContext.getUuids(SequenceType.AUDIO).toArray(new SequenceUUID[]{}));

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
    }

    @Test
    public void testSubtitleSequenceContext() throws Exception {
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        AssetMap assetMap = new AssetMapParser().parse(ImpUtils.getImpFolder(), ImpUtils.getCorrectAssetmap());
        new CplContextBuilder(contextProvider, assetMap).build(ImpUtils.getCplSequence());

        SequenceTemplateParameterContext sequenceContext = contextProvider.getSequenceContext();
        assertEquals(6, sequenceContext.getSequenceCount(SequenceType.SUBTITLE));

        // UUIDs as defined in CPL.xml
        assertArrayEquals(
                new SequenceUUID[]{
                        SequenceUUID.create("urn:uuid:04f226e7-adac-45a4-adbf-83335cf02d0d"),
                        SequenceUUID.create("urn:uuid:14f226e7-adac-45a4-adbf-83335cf02d0d"),
                        SequenceUUID.create("urn:uuid:24f226e7-adac-45a4-adbf-83335cf02d0d"),
                        SequenceUUID.create("urn:uuid:34f226e7-adac-45a4-adbf-83335cf02d0d"),
                        SequenceUUID.create("urn:uuid:44f226e7-adac-45a4-adbf-83335cf02d0d"),
                        SequenceUUID.create("urn:uuid:54f226e7-adac-45a4-adbf-83335cf02d0d")},
                sequenceContext.getUuids(SequenceType.SUBTITLE).toArray(new SequenceUUID[]{}));

        ContextInfo contextInfo = new ContextInfoBuilder().setSequenceType(SequenceType.SUBTITLE)
                .setSequenceUuid(SequenceUUID.create("urn:uuid:04f226e7-adac-45a4-adbf-83335cf02d0d")).build();
        assertEquals("urn:uuid:04f226e7-adac-45a4-adbf-83335cf02d0d",
                sequenceContext.getParameterValue(SequenceContextParameters.UUID, contextInfo));
        assertEquals("0",
                sequenceContext.getParameterValue(SequenceContextParameters.NUM, contextInfo));
        assertEquals("subtitle",
                sequenceContext.getParameterValue(SequenceContextParameters.TYPE, contextInfo));

        contextInfo = new ContextInfoBuilder().setSequenceType(SequenceType.SUBTITLE)
                .setSequenceUuid(SequenceUUID.create("urn:uuid:14f226e7-adac-45a4-adbf-83335cf02d0d")).build();
        assertEquals("urn:uuid:14f226e7-adac-45a4-adbf-83335cf02d0d",
                sequenceContext.getParameterValue(SequenceContextParameters.UUID, contextInfo));
        assertEquals("1",
                sequenceContext.getParameterValue(SequenceContextParameters.NUM, contextInfo));
        assertEquals("subtitle",
                sequenceContext.getParameterValue(SequenceContextParameters.TYPE, contextInfo));

        contextInfo = new ContextInfoBuilder().setSequenceType(SequenceType.SUBTITLE)
                .setSequenceUuid(SequenceUUID.create("urn:uuid:24f226e7-adac-45a4-adbf-83335cf02d0d")).build();
        assertEquals("urn:uuid:24f226e7-adac-45a4-adbf-83335cf02d0d",
                sequenceContext.getParameterValue(SequenceContextParameters.UUID, contextInfo));
        assertEquals("2",
                sequenceContext.getParameterValue(SequenceContextParameters.NUM, contextInfo));
        assertEquals("subtitle",
                sequenceContext.getParameterValue(SequenceContextParameters.TYPE, contextInfo));

        contextInfo = new ContextInfoBuilder().setSequenceType(SequenceType.SUBTITLE)
                .setSequenceUuid(SequenceUUID.create("urn:uuid:34f226e7-adac-45a4-adbf-83335cf02d0d")).build();
        assertEquals("urn:uuid:34f226e7-adac-45a4-adbf-83335cf02d0d",
                sequenceContext.getParameterValue(SequenceContextParameters.UUID, contextInfo));
        assertEquals("3",
                sequenceContext.getParameterValue(SequenceContextParameters.NUM, contextInfo));
        assertEquals("subtitle",
                sequenceContext.getParameterValue(SequenceContextParameters.TYPE, contextInfo));

        contextInfo = new ContextInfoBuilder().setSequenceType(SequenceType.SUBTITLE)
                .setSequenceUuid(SequenceUUID.create("urn:uuid:44f226e7-adac-45a4-adbf-83335cf02d0d")).build();
        assertEquals("urn:uuid:44f226e7-adac-45a4-adbf-83335cf02d0d",
                sequenceContext.getParameterValue(SequenceContextParameters.UUID, contextInfo));
        assertEquals("4",
                sequenceContext.getParameterValue(SequenceContextParameters.NUM, contextInfo));
        assertEquals("subtitle",
                sequenceContext.getParameterValue(SequenceContextParameters.TYPE, contextInfo));

        contextInfo = new ContextInfoBuilder().setSequenceType(SequenceType.SUBTITLE)
                .setSequenceUuid(SequenceUUID.create("urn:uuid:54f226e7-adac-45a4-adbf-83335cf02d0d")).build();
        assertEquals("urn:uuid:54f226e7-adac-45a4-adbf-83335cf02d0d",
                sequenceContext.getParameterValue(SequenceContextParameters.UUID, contextInfo));
        assertEquals("5",
                sequenceContext.getParameterValue(SequenceContextParameters.NUM, contextInfo));
        assertEquals("subtitle",
                sequenceContext.getParameterValue(SequenceContextParameters.TYPE, contextInfo));
    }

    private CplContextBuilder createCplContextBuilder() throws Exception {
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        AssetMap assetMap = new AssetMapParser().parse(ImpUtils.getImpFolder(), ImpUtils.getCorrectAssetmap());
        return new CplContextBuilder(contextProvider, assetMap);
    }

}
