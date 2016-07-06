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
package com.netflix.imfutility.dpp;

import com.netflix.imfutility.ImfUtilityTest;
import com.netflix.imfutility.conversion.templateParameter.context.SequenceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.dpp.audiomap.AudioMapType;
import com.netflix.imfutility.generated.dpp.metadata.AudioTrackLayoutDmAs11Type;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.util.AudioMapUtils;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import com.netflix.imfutility.xml.XmlParsingException;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * <ul>
 * <li>Tests the audiomap.xml can be parsed and mapped to Java model successfully.</li>
 * <li>Tests the XSD validation is applied to the audiomap.xml and an exception is thrown is validation doesn't pass.</li>
 * <li>Tests that pan parameters are generated correctly according to the CPL virtual tracks and audio layout.</li>
 * <li>Tests that default audio map is generated correctly based on CPL virtual tracks and audio layout.</li>
 * </ul>
 */
public class AudioMapXmlProviderTest extends ImfUtilityTest {

    /**
     * Tests audiomap.xml loading and validation.
     *
     * @throws Exception
     */
    @Test
    public void parseCorrectAudiomapXml() throws Exception {
        //Try to load and validate
        AudioMapXmlProvider audioMapProvider = new AudioMapXmlProvider(
                AudioMapUtils.getCorrectAudiomapXml(), AudioTrackLayoutDmAs11Type.EBU_R_48_2_A,
                TemplateParameterContextCreator.createDefaultContextProvider());

        AudioMapType audioMap = audioMapProvider.getAudioMap();

        assertNotNull(audioMap.getEBUTrack());
        assertFalse(audioMap.getEBUTrack().isEmpty());
    }

    @Test(expected = XmlParsingException.class)
    public void testParseBrokenXml() throws Exception {
        new AudioMapXmlProvider(AudioMapUtils.getBrokenXmlAudiomapXml(), AudioTrackLayoutDmAs11Type.EBU_R_48_2_A,
                TemplateParameterContextCreator.createDefaultContextProvider());
    }

    @Test(expected = XmlParsingException.class)
    public void testParseInvalidXsd() throws Exception {
        new AudioMapXmlProvider(AudioMapUtils.getInvalidXsdAudiomapXml(), AudioTrackLayoutDmAs11Type.EBU_R_48_2_A,
                TemplateParameterContextCreator.createDefaultContextProvider());
    }

    @Test(expected = FileNotFoundException.class)
    public void testParseInvalidFilePath() throws Exception {
        new AudioMapXmlProvider(new File("invalid-path"), AudioTrackLayoutDmAs11Type.EBU_R_48_2_A,
                TemplateParameterContextCreator.createDefaultContextProvider());
    }

    /**
     * Tests that audio map parameter for pan filter is generated properly for 2A audio layout.
     *
     * @throws Exception
     */
    @Test
    public void getAudioMapParameter2A() throws Exception {
        // fil CPL context with the number of channels for each virtual track
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(contextProvider,
                new LinkedHashMap<String, Integer>() {{
                    put("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 2);
                    put("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712", 2);
                }}
        );

        String panParameter2A = new AudioMapXmlProvider(
                AudioMapUtils.getCorrectAudiomapXml(), AudioTrackLayoutDmAs11Type.EBU_R_48_2_A, contextProvider)
                .getPanParameter();

        assertEquals("4c|c0=c1|c1=c0|c2=c3|c3=0*c0", panParameter2A);
    }

    /**
     * Tests that audio map parameter for pan filter is generated properly for 4B audio layout.
     *
     * @throws Exception
     */
    @Test
    public void getAudioMapParameter4B() throws Exception {
        // fil CPL context with the number of channels for each virtual track
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(contextProvider,
                new LinkedHashMap<String, Integer>() {{
                    put("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 2);
                    put("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712", 2);
                }}
        );

        String panParameter4B = new AudioMapXmlProvider(
                AudioMapUtils.getCorrectAudiomapXml(), AudioTrackLayoutDmAs11Type.EBU_R_48_4_B, contextProvider)
                .getPanParameter();

        assertEquals("4c|c0=c1|c1=c0|c2=c3|c3=0*c0", panParameter4B);
    }

    /**
     * Tests that audio map parameter for pan filter is generated properly for 16C audio layout.
     *
     * @throws Exception
     */
    @Test
    public void getAudioMapParameter16C() throws Exception {
        // fil CPL context with the number of channels for each virtual track
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(contextProvider,
                new LinkedHashMap<String, Integer>() {{
                    put("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 2);
                    put("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712", 2);
                }}
        );

        String panParameter16C = new AudioMapXmlProvider(
                AudioMapUtils.getCorrectAudiomapXml(), AudioTrackLayoutDmAs11Type.EBU_R_123_16_C, contextProvider)
                .getPanParameter();

        assertEquals("16c|c0=c1|c1=c0|c2=c3|c3=0*c0|c4=0*c0|c5=0*c0|c6=0*c0|c7=0*c0|c8=0*c0|c9=0*c0|c10=0*c0|c11=0*c0|c12=0*c0|c13=0*c0|c14=0*c0|c15=0*c0",
                panParameter16C);
    }

    /**
     * Tests that default audio map is generated correctly based on CPL virtual tracks and audio layout for 2A layout
     * when the total number of virtual tracks and channels is equal to the required track count.
     *
     * @throws Exception
     */
    @Test
    public void generateDefaultAudiomap2AEqualChannels() throws Exception {
        // fil CPL context with the number of channels for each virtual track
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(contextProvider,
                new LinkedHashMap<String, Integer>() {{
                    put("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 2);
                    put("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712", 2);
                }}
        );

        // create and read default audio map files
        AudioMapXmlProvider audioMapXmlProvider = new AudioMapXmlProvider(AudioTrackLayoutDmAs11Type.EBU_R_48_2_A, contextProvider);
        audioMapXmlProvider.getAudioMapFile().deleteOnExit();
        AudioMapType audioMap2A = audioMapXmlProvider.getAudioMap();


        // check that audio map is correct
        assertNotNull(audioMap2A);
        assertNotNull(audioMap2A.getEBUTrack());
        assertEquals(4, audioMap2A.getEBUTrack().size());
        checkEBUTrack(audioMap2A, 0, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 1);
        checkEBUTrack(audioMap2A, 1, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 2);
        checkEBUTrack(audioMap2A, 2, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712", 1);
        checkEBUTrack(audioMap2A, 3, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712", 2);
    }

    /**
     * Tests that default audio map is generated correctly based on CPL virtual tracks and audio layout for 2A layout
     * when there is one input track with less channels than required.
     *
     * @throws Exception
     */
    @Test
    public void generateDefaultAudiomap2ALessChannelsOneTrack() throws Exception {
        // fil CPL context with the number of channels for each virtual track
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(contextProvider,
                new LinkedHashMap<String, Integer>() {{
                    put("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 2);
                }}
        );

        // create and read default audio map files
        AudioMapXmlProvider audioMapXmlProvider = new AudioMapXmlProvider(AudioTrackLayoutDmAs11Type.EBU_R_48_2_A, contextProvider);
        audioMapXmlProvider.getAudioMapFile().deleteOnExit();
        AudioMapType audioMap2A = audioMapXmlProvider.getAudioMap();


        // check that audio map is correct
        assertNotNull(audioMap2A);
        assertNotNull(audioMap2A.getEBUTrack());
        assertEquals(4, audioMap2A.getEBUTrack().size());
        checkEBUTrack(audioMap2A, 0, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 1);
        checkEBUTrack(audioMap2A, 1, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 2);
        checkEBUTrack(audioMap2A, 2, null, null); // silence
        checkEBUTrack(audioMap2A, 3, null, null); // silence
    }

    /**
     * Tests that default audio map is generated correctly based on CPL virtual tracks and audio layout for 2A layout
     * when there are two input tracks with less channels than required.
     *
     * @throws Exception
     */
    @Test
    public void generateDefaultAudiomap2ALessTwoTracks() throws Exception {
        // fil CPL context with the number of channels for each virtual track
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(contextProvider,
                new LinkedHashMap<String, Integer>() {{
                    put("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 1);
                    put("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712", 1);
                }}
        );

        // create and read default audio map files
        AudioMapXmlProvider audioMapXmlProvider = new AudioMapXmlProvider(AudioTrackLayoutDmAs11Type.EBU_R_48_2_A, contextProvider);
        audioMapXmlProvider.getAudioMapFile().deleteOnExit();
        AudioMapType audioMap2A = audioMapXmlProvider.getAudioMap();

        // check that audio map is correct
        assertNotNull(audioMap2A);
        assertNotNull(audioMap2A.getEBUTrack());
        assertEquals(4, audioMap2A.getEBUTrack().size());
        checkEBUTrack(audioMap2A, 0, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 1);
        checkEBUTrack(audioMap2A, 1, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712", 1);
        checkEBUTrack(audioMap2A, 2, null, null); // silence
        checkEBUTrack(audioMap2A, 3, null, null); // silence
    }

    /**
     * Tests that default audio map is generated correctly based on CPL virtual tracks and audio layout for 2A layout
     * when there is one input track with more channels than required.
     *
     * @throws Exception
     */
    @Test
    public void generateDefaultAudiomap2AMoreOneTrack() throws Exception {
        // fil CPL context with the number of channels for each virtual track
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(contextProvider,
                new LinkedHashMap<String, Integer>() {{
                    put("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 10);
                }}
        );

        // create and read default audio map files
        AudioMapXmlProvider audioMapXmlProvider = new AudioMapXmlProvider(AudioTrackLayoutDmAs11Type.EBU_R_48_2_A, contextProvider);
        audioMapXmlProvider.getAudioMapFile().deleteOnExit();
        AudioMapType audioMap2A = audioMapXmlProvider.getAudioMap();

        // check that audio map is correct
        assertNotNull(audioMap2A);
        assertNotNull(audioMap2A.getEBUTrack());
        assertEquals(4, audioMap2A.getEBUTrack().size());
        checkEBUTrack(audioMap2A, 0, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 1);
        checkEBUTrack(audioMap2A, 1, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 2);
        checkEBUTrack(audioMap2A, 2, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 3);
        checkEBUTrack(audioMap2A, 3, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 4);
    }

    /**
     * Tests that default audio map is generated correctly based on CPL virtual tracks and audio layout for 2A layout
     * when there are two input track with more channels than required.
     *
     * @throws Exception
     */
    @Test
    public void generateDefaultAudiomap2AMoreTwoTracks() throws Exception {
        // fil CPL context with the number of channels for each virtual track
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(contextProvider,
                new LinkedHashMap<String, Integer>() {{
                    put("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 3);
                    put("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712", 3);
                }}
        );

        // create and read default audio map files
        AudioMapXmlProvider audioMapXmlProvider = new AudioMapXmlProvider(AudioTrackLayoutDmAs11Type.EBU_R_48_2_A, contextProvider);
        audioMapXmlProvider.getAudioMapFile().deleteOnExit();
        AudioMapType audioMap2A = audioMapXmlProvider.getAudioMap();

        // check that audio map is correct
        assertNotNull(audioMap2A);
        assertNotNull(audioMap2A.getEBUTrack());
        assertEquals(4, audioMap2A.getEBUTrack().size());
        checkEBUTrack(audioMap2A, 0, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 1);
        checkEBUTrack(audioMap2A, 1, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 2);
        checkEBUTrack(audioMap2A, 2, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 3);
        checkEBUTrack(audioMap2A, 3, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712", 1);
    }

    /**
     * Tests that default audio map is generated correctly based on CPL virtual tracks and audio layout for 16C layout.
     *
     * @throws Exception
     */
    @Test
    public void generateDefaultAudiomap16C() throws Exception {
        // fil CPL context with the number of channels for each virtual track
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(contextProvider,
                new LinkedHashMap<String, Integer>() {{
                    put("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 2);
                    put("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712", 4);
                    put("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd713", 1);
                    put("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd714", 3);
                    put("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd715", 5);
                    put("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd716", 4);
                }}
        );

        // create and read default audio map files
        AudioMapXmlProvider audioMapXmlProvider = new AudioMapXmlProvider(AudioTrackLayoutDmAs11Type.EBU_R_123_16_C, contextProvider);
        audioMapXmlProvider.getAudioMapFile().deleteOnExit();
        AudioMapType audioMap16C = audioMapXmlProvider.getAudioMap();

        // check that audio map is correct
        assertNotNull(audioMap16C);
        assertNotNull(audioMap16C.getEBUTrack());
        assertEquals(16, audioMap16C.getEBUTrack().size());
        checkEBUTrack(audioMap16C, 0, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 1);
        checkEBUTrack(audioMap16C, 1, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 2);
        checkEBUTrack(audioMap16C, 2, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712", 1);
        checkEBUTrack(audioMap16C, 3, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712", 2);
        checkEBUTrack(audioMap16C, 4, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712", 3);
        checkEBUTrack(audioMap16C, 5, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712", 4);
        checkEBUTrack(audioMap16C, 6, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd713", 1);
        checkEBUTrack(audioMap16C, 7, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd714", 1);
        checkEBUTrack(audioMap16C, 8, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd714", 2);
        checkEBUTrack(audioMap16C, 9, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd714", 3);
        checkEBUTrack(audioMap16C, 10, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd715", 1);
        checkEBUTrack(audioMap16C, 11, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd715", 2);
        checkEBUTrack(audioMap16C, 12, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd715", 3);
        checkEBUTrack(audioMap16C, 13, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd715", 4);
        checkEBUTrack(audioMap16C, 14, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd715", 5);
        checkEBUTrack(audioMap16C, 15, "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd716", 1);
    }

    private void prepareCplVirtualTracksWithChannels(TemplateParameterContextProvider contextProvider, Map<String, Integer> channelsForTrack) {
        SequenceTemplateParameterContext sequenceContext = contextProvider.getSequenceContext();
        channelsForTrack.forEach(
                (uuid, channels) -> {
                    sequenceContext.initSequence(SequenceType.AUDIO, SequenceUUID.create(uuid)); // as in test-audiomap.xml
                    contextProvider.getSequenceContext().addSequenceParameter(
                            SequenceType.AUDIO,
                            SequenceUUID.create(uuid),
                            SequenceContextParameters.CHANNELS_NUM,
                            String.valueOf(channels));
                }

        );
    }

    private void checkEBUTrack(AudioMapType audioMap, int trackNum, String uuid, Integer channelsNum) {
        assertEquals(trackNum + 1, audioMap.getEBUTrack().get(trackNum).getNumber());
        assertEquals(uuid, audioMap.getEBUTrack().get(trackNum).getCPLVirtualTrackId());
        assertEquals(channelsNum, audioMap.getEBUTrack().get(trackNum).getCPLVirtualTrackChannel());
    }

}
