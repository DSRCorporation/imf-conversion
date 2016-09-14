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
package com.netflix.imfutility.itunes.audio;

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.ImfUtilityTest;
import com.netflix.imfutility.conversion.templateParameter.context.SequenceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.generated.itunes.audiomap.AlternativeAudioType;
import com.netflix.imfutility.generated.itunes.audiomap.AudioMapType;
import com.netflix.imfutility.generated.itunes.audiomap.ChannelType;
import com.netflix.imfutility.generated.itunes.audiomap.MainAudioType;
import com.netflix.imfutility.generated.itunes.audiomap.Option3Type;
import com.netflix.imfutility.generated.itunes.audiomap.Option6Type;
import com.netflix.imfutility.itunes.audio.AudioMapXmlProvider.AudioOption;
import com.netflix.imfutility.util.AudioUtils;
import com.netflix.imfutility.util.FFmpegAudioChannels;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import com.netflix.imfutility.xml.XmlParsingException;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Stream;

import static com.netflix.imfutility.util.FFmpegAudioChannels.FC;
import static com.netflix.imfutility.util.FFmpegAudioChannels.FL;
import static com.netflix.imfutility.util.FFmpegAudioChannels.FR;
import static com.netflix.imfutility.util.FFmpegAudioChannels.LFE;
import static com.netflix.imfutility.util.FFmpegAudioChannels.SL;
import static com.netflix.imfutility.util.FFmpegAudioChannels.SR;
import static com.netflix.imfutility.util.TemplateParameterContextCreator.getSequenceUuid;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

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
    public void parseCorrectAudiomapXmlSuccessful() throws Exception {
        /* PREPARATION */
        String trackUuid = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711";

        /* EXECUTION */
        AudioMapXmlProvider audioMapProvider = createAndInitProvider(
                getAudiomapXml("xml/audiomap/valid-audiomap.xml"),
                TemplateParameterContextCreator.createDefaultContextProvider());

        /* VALIDATION */
        AudioMapType audioMap = audioMapProvider.getAudioMap();
        Option3Type opt3 = audioMap.getMainAudio().getOption3();
        // o3 t1
        assertEquals(trackUuid, opt3.getTrack1().getL().getCPLVirtualTrackId());
        assertEquals(1, opt3.getTrack1().getL().getCPLVirtualTrackChannel());
        assertEquals(trackUuid, opt3.getTrack1().getR().getCPLVirtualTrackId());
        assertEquals(2, opt3.getTrack1().getR().getCPLVirtualTrackChannel());
        assertEquals(trackUuid, opt3.getTrack1().getC().getCPLVirtualTrackId());
        assertEquals(3, opt3.getTrack1().getC().getCPLVirtualTrackChannel());
        assertEquals(trackUuid, opt3.getTrack1().getLFE().getCPLVirtualTrackId());
        assertEquals(4, opt3.getTrack1().getLFE().getCPLVirtualTrackChannel());
        assertEquals(trackUuid, opt3.getTrack1().getLs().getCPLVirtualTrackId());
        assertEquals(5, opt3.getTrack1().getLs().getCPLVirtualTrackChannel());
        assertEquals(trackUuid, opt3.getTrack1().getRs().getCPLVirtualTrackId());
        assertEquals(6, opt3.getTrack1().getRs().getCPLVirtualTrackChannel());
        // o3 t2
        assertEquals(trackUuid, opt3.getTrack2().getLt().getCPLVirtualTrackId());
        assertEquals(1, opt3.getTrack2().getLt().getCPLVirtualTrackChannel());
        assertEquals(trackUuid, opt3.getTrack2().getRt().getCPLVirtualTrackId());
        assertEquals(2, opt3.getTrack2().getRt().getCPLVirtualTrackChannel());

        Option6Type opt6 = audioMap.getAlternativeAudio().get(0).getOption6();
        // o6 t1
        assertEquals(trackUuid, opt6.getTrack1().getL().getCPLVirtualTrackId());
        assertEquals(1, opt6.getTrack1().getL().getCPLVirtualTrackChannel());
        assertEquals(trackUuid, opt6.getTrack1().getR().getCPLVirtualTrackId());
        assertEquals(2, opt6.getTrack1().getR().getCPLVirtualTrackChannel());
    }

    /**
     * Invalid XML throws schema validation exception.
     *
     * @throws Exception unexpected exception
     */
    @Test(expected = XmlParsingException.class)
    public void invalidXmlAgainstXsdThrowException() throws Exception {
        createAndInitProvider(getAudiomapXml("xml/audiomap/invalid-audiomap.xml"),
                TemplateParameterContextCreator.createDefaultContextProvider());
    }

    /**
     * Throw exception if audiomap file does not exist.
     *
     * @throws Exception unexpected exception
     */
    @Test(expected = FileNotFoundException.class)
    public void parseInvalidFilePathThrowsException() throws Exception {
        createAndInitProvider(new File("invalid-path"),
                TemplateParameterContextCreator.createDefaultContextProvider());
    }

    /**
     * Throw exception if audiomap.xml Option1A includes not all necessary tracks.
     *
     * @throws Exception unexpected exception
     */
    @Test(expected = ConversionException.class)
    public void option1AWithIncompleteTracksThrowConversionException() throws Exception {
        /* PREPARATION */

        /* EXECUTION */
        createAndInitProvider(
                getAudiomapXml("xml/audiomap/1a-incomplete-audiomap.xml"),
                TemplateParameterContextCreator.createDefaultContextProvider());

        /* VALIDATION */
    }

    /**
     * Throw exception if audiomap.xml Option2 includes not all necessary tracks.
     *
     * @throws Exception unexpected exception
     */
    @Test(expected = ConversionException.class)
    public void option2WithIncompleteTracksThrowConversionException() throws Exception {
        /* PREPARATION */

        /* EXECUTION */
        createAndInitProvider(
                getAudiomapXml("xml/audiomap/2-incomplete-audiomap.xml"),
                TemplateParameterContextCreator.createDefaultContextProvider());

        /* VALIDATION */
    }

    /**
     * Throw exception if audiomap.xml Option3 includes not all necessary tracks.
     *
     * @throws Exception unexpected exception
     */
    @Test(expected = ConversionException.class)
    public void option3WithIncompleteTracksThrowConversionException() throws Exception {
        /* PREPARATION */

        /* EXECUTION */
        createAndInitProvider(
                getAudiomapXml("xml/audiomap/3-incomplete-audiomap.xml"),
                TemplateParameterContextCreator.createDefaultContextProvider());

        /* VALIDATION */
    }

    /**
     * Throw exception if audiomap.xml Option4 includes not all necessary tracks.
     *
     * @throws Exception unexpected exception
     */
    @Test(expected = ConversionException.class)
    public void option4WithIncompleteTracksThrowConversionException() throws Exception {
        /* PREPARATION */

        /* EXECUTION */
        createAndInitProvider(
                getAudiomapXml("xml/audiomap/4-incomplete-audiomap.xml"),
                TemplateParameterContextCreator.createDefaultContextProvider());

        /* VALIDATION */
    }

    /**
     * Throw exception if audiomap.xml Option5 includes not all necessary tracks.
     *
     * @throws Exception unexpected exception
     */
    @Test(expected = ConversionException.class)
    public void option5WithIncompleteTracksThrowConversionException() throws Exception {
        /* PREPARATION */

        /* EXECUTION */
        createAndInitProvider(
                getAudiomapXml("xml/audiomap/5-incomplete-audiomap.xml"),
                TemplateParameterContextCreator.createDefaultContextProvider());

        /* VALIDATION */
    }

    @Test
    public void emptyOption1AWithEnoughSequencesChanneslCanBeGenerated() throws Exception {
        String seq1 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711";
        String seq2 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712";
        String seq3 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd713";
        String seq4 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd714";
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(
                contextProvider,
                trackChannels(seq1, 4),
                trackChannels(seq2, 2),
                trackChannels(seq3, 2),
                trackChannels(seq4, 2));

        /* EXECUTION */
        AudioOption mainAudio = createAndInitProvider(
                getAudiomapXml("xml/audiomap/1a-empty-audiomap.xml"), contextProvider)
                .getMainAudio();

        /* VALIDATION */
        assertEquals("main-audio.mov", mainAudio.getFileName());
        assertEquals("en-US", mainAudio.getLocale());

        assertEquals("Track count", 3, mainAudio.size());

        assertChannelEquals(seq1, 1, mainAudio.get(0).get(FL.name()));
        assertChannelEquals(seq1, 2, mainAudio.get(0).get(FR.name()));
        assertChannelEquals(seq1, 3, mainAudio.get(0).get(FC.name()));
        assertChannelEquals(seq1, 4, mainAudio.get(0).get(LFE.name()));
        assertChannelEquals(seq2, 1, mainAudio.get(0).get(SL.name()));
        assertChannelEquals(seq2, 2, mainAudio.get(0).get(SR.name()));

        assertChannelEquals(seq3, 1, mainAudio.get(1).get(FL.name()));

        assertChannelEquals(seq3, 2, mainAudio.get(2).get(FR.name()));
    }

    @Test
    public void emptyOption2WithEnoughSequencesChanneslCanBeGenerated() throws Exception {
        String seq1 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711";
        String seq2 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712";
        String seq3 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd713";
        String seq4 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd714";
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(
                contextProvider,
                trackChannels(seq1, 4),
                trackChannels(seq2, 2),
                trackChannels(seq3, 2),
                trackChannels(seq4, 2));

        /* EXECUTION */
        AudioOption mainAudio = createAndInitProvider(
                getAudiomapXml("xml/audiomap/2-empty-audiomap.xml"), contextProvider)
                .getMainAudio();

        /* VALIDATION */
        assertEquals("main-audio.mov", mainAudio.getFileName());
        assertEquals("en-US", mainAudio.getLocale());

        assertEquals("Track count", 8, mainAudio.size());

        assertChannelEquals(seq1, 1, mainAudio.get(0).get(FL.name()));
        assertChannelEquals(seq1, 2, mainAudio.get(1).get(FR.name()));
        assertChannelEquals(seq1, 3, mainAudio.get(2).get(FC.name()));
        assertChannelEquals(seq1, 4, mainAudio.get(3).get(LFE.name()));
        assertChannelEquals(seq2, 1, mainAudio.get(4).get(SL.name()));
        assertChannelEquals(seq2, 2, mainAudio.get(5).get(SR.name()));

        assertChannelEquals(seq3, 1, mainAudio.get(6).get(FL.name()));

        assertChannelEquals(seq3, 2, mainAudio.get(7).get(FR.name()));
    }

    @Test
    public void emptyOption3WithEnoughSequencesChanneslCanBeGenerated() throws Exception {
        String seq1 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711";
        String seq2 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712";
        String seq3 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd713";
        String seq4 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd714";
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(
                contextProvider,
                trackChannels(seq1, 4),
                trackChannels(seq2, 2),
                trackChannels(seq3, 2),
                trackChannels(seq4, 2));

        /* EXECUTION */
        AudioOption mainAudio = createAndInitProvider(
                getAudiomapXml("xml/audiomap/3-empty-audiomap.xml"), contextProvider)
                .getMainAudio();

        /* VALIDATION */
        assertEquals("main-audio.mov", mainAudio.getFileName());
        assertEquals("en-US", mainAudio.getLocale());

        assertEquals("Track count", 2, mainAudio.size());

        assertChannelEquals(seq1, 1, mainAudio.get(0).get(FL.name()));
        assertChannelEquals(seq1, 2, mainAudio.get(0).get(FR.name()));
        assertChannelEquals(seq1, 3, mainAudio.get(0).get(FC.name()));
        assertChannelEquals(seq1, 4, mainAudio.get(0).get(LFE.name()));
        assertChannelEquals(seq2, 1, mainAudio.get(0).get(SL.name()));
        assertChannelEquals(seq2, 2, mainAudio.get(0).get(SR.name()));

        assertChannelEquals(seq3, 1, mainAudio.get(1).get(FL.name()));

        assertChannelEquals(seq3, 2, mainAudio.get(1).get(FR.name()));
    }

    @Test
    public void emptyOption4WithEnoughSequencesChanneslCanBeGenerated() throws Exception {
        String seq1 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711";
        String seq2 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712";
        String seq3 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd713";
        String seq4 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd714";
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(
                contextProvider,
                trackChannels(seq1, 4),
                trackChannels(seq2, 2),
                trackChannels(seq3, 2),
                trackChannels(seq4, 2));

        /* EXECUTION */
        AudioOption mainAudio = createAndInitProvider(
                getAudiomapXml("xml/audiomap/4-empty-audiomap.xml"), contextProvider)
                .getMainAudio();

        /* VALIDATION */
        assertEquals("main-audio.mov", mainAudio.getFileName());
        assertEquals("en-US", mainAudio.getLocale());

        assertEquals("Track count", 7, mainAudio.size());

        assertChannelEquals(seq1, 1, mainAudio.get(0).get(FL.name()));
        assertChannelEquals(seq1, 2, mainAudio.get(1).get(FR.name()));
        assertChannelEquals(seq1, 3, mainAudio.get(2).get(FC.name()));
        assertChannelEquals(seq1, 4, mainAudio.get(3).get(LFE.name()));
        assertChannelEquals(seq2, 1, mainAudio.get(4).get(SL.name()));
        assertChannelEquals(seq2, 2, mainAudio.get(5).get(SR.name()));

        assertChannelEquals(seq3, 1, mainAudio.get(6).get(FL.name()));

        assertChannelEquals(seq3, 2, mainAudio.get(6).get(FR.name()));
    }

    @Test
    public void emptyOption5WithEnoughSequencesChanneslCanBeGenerated() throws Exception {
        String seq1 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711";
        String seq2 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712";
        String seq3 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd713";
        String seq4 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd714";
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(
                contextProvider,
                trackChannels(seq1, 4),
                trackChannels(seq2, 2),
                trackChannels(seq3, 2),
                trackChannels(seq4, 2));

        /* EXECUTION */
        AudioOption mainAudio = createAndInitProvider(
                getAudiomapXml("xml/audiomap/5-empty-audiomap.xml"), contextProvider)
                .getMainAudio();

        /* VALIDATION */
        assertEquals("main-audio.mov", mainAudio.getFileName());
        assertEquals("en-US", mainAudio.getLocale());

        assertEquals("Track count", 2, mainAudio.size());

        assertChannelEquals(seq1, 1, mainAudio.get(0).get(FL.name()));

        assertChannelEquals(seq1, 2, mainAudio.get(1).get(FR.name()));
    }

    @Test
    public void emptyOption6WithEnoughSequencesChanneslCanBeGenerated() throws Exception {
        String seq1 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711";
        String seq2 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712";
        String seq3 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd713";
        String seq4 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd714";
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(
                contextProvider,
                trackChannels(seq1, 4),
                trackChannels(seq2, 2),
                trackChannels(seq3, 2),
                trackChannels(seq4, 2));

        /* EXECUTION */
        AudioOption mainAudio = createAndInitProvider(
                getAudiomapXml("xml/audiomap/6-empty-audiomap.xml"), contextProvider)
                .getMainAudio();

        /* VALIDATION */
        assertEquals("main-audio.mov", mainAudio.getFileName());
        assertEquals("en-US", mainAudio.getLocale());

        assertEquals("Track count", 1, mainAudio.size());

        assertChannelEquals(seq1, 1, mainAudio.get(0).get(FL.name()));
        assertChannelEquals(seq1, 2, mainAudio.get(0).get(FR.name()));
    }

    @Test
    public void emptyOption1AWithSixSequencesChanneslCanBeGenerated() throws Exception {
        String seq1 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711";
        String seq2 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712";
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(
                contextProvider,
                trackChannels(seq1, 4),
                trackChannels(seq2, 2));

        /* EXECUTION */
        AudioOption mainAudio = createAndInitProvider(
                getAudiomapXml("xml/audiomap/1a-empty-audiomap.xml"), contextProvider)
                .getMainAudio();

        /* VALIDATION */
        assertEquals("main-audio.mov", mainAudio.getFileName());
        assertEquals("en-US", mainAudio.getLocale());

        assertEquals("Track count", 3, mainAudio.size());

        assertChannelEquals(seq1, 1, mainAudio.get(0).get(FL.name()));
        assertChannelEquals(seq1, 2, mainAudio.get(0).get(FR.name()));
        assertChannelEquals(seq1, 3, mainAudio.get(0).get(FC.name()));
        assertChannelEquals(seq1, 4, mainAudio.get(0).get(LFE.name()));
        assertChannelEquals(seq2, 1, mainAudio.get(0).get(SL.name()));
        assertChannelEquals(seq2, 2, mainAudio.get(0).get(SR.name()));

        assertChannelEquals(seq1, 1, mainAudio.get(1).get(FL.name()));

        assertChannelEquals(seq1, 2, mainAudio.get(2).get(FR.name()));
    }

    @Test
    public void emptyOption2WithSixSequencesChanneslCanBeGenerated() throws Exception {
        String seq1 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711";
        String seq2 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712";
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(
                contextProvider,
                trackChannels(seq1, 4),
                trackChannels(seq2, 2));

        /* EXECUTION */
        AudioOption mainAudio = createAndInitProvider(
                getAudiomapXml("xml/audiomap/2-empty-audiomap.xml"), contextProvider)
                .getMainAudio();

        /* VALIDATION */
        assertEquals("main-audio.mov", mainAudio.getFileName());
        assertEquals("en-US", mainAudio.getLocale());

        assertEquals("Track count", 8, mainAudio.size());

        assertChannelEquals(seq1, 1, mainAudio.get(0).get(FL.name()));
        assertChannelEquals(seq1, 2, mainAudio.get(1).get(FR.name()));
        assertChannelEquals(seq1, 3, mainAudio.get(2).get(FC.name()));
        assertChannelEquals(seq1, 4, mainAudio.get(3).get(LFE.name()));
        assertChannelEquals(seq2, 1, mainAudio.get(4).get(SL.name()));
        assertChannelEquals(seq2, 2, mainAudio.get(5).get(SR.name()));

        assertChannelEquals(seq1, 1, mainAudio.get(6).get(FL.name()));

        assertChannelEquals(seq1, 2, mainAudio.get(7).get(FR.name()));
    }

    @Test
    public void emptyOption3SixSequencesChanneslCanBeGenerated() throws Exception {
        String seq1 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711";
        String seq2 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712";
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(
                contextProvider,
                trackChannels(seq1, 4),
                trackChannels(seq2, 2));

        /* EXECUTION */
        AudioOption mainAudio = createAndInitProvider(
                getAudiomapXml("xml/audiomap/3-empty-audiomap.xml"), contextProvider)
                .getMainAudio();

        /* VALIDATION */
        assertEquals("main-audio.mov", mainAudio.getFileName());
        assertEquals("en-US", mainAudio.getLocale());

        assertEquals("Track count", 2, mainAudio.size());

        assertChannelEquals(seq1, 1, mainAudio.get(0).get(FL.name()));
        assertChannelEquals(seq1, 2, mainAudio.get(0).get(FR.name()));
        assertChannelEquals(seq1, 3, mainAudio.get(0).get(FC.name()));
        assertChannelEquals(seq1, 4, mainAudio.get(0).get(LFE.name()));
        assertChannelEquals(seq2, 1, mainAudio.get(0).get(SL.name()));
        assertChannelEquals(seq2, 2, mainAudio.get(0).get(SR.name()));

        assertChannelEquals(seq1, 1, mainAudio.get(1).get(FL.name()));

        assertChannelEquals(seq1, 2, mainAudio.get(1).get(FR.name()));
    }

    @Test
    public void emptyOption4WithSixSequencesChanneslCanBeGenerated() throws Exception {
        String seq1 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711";
        String seq2 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712";
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(
                contextProvider,
                trackChannels(seq1, 4),
                trackChannels(seq2, 2));

        /* EXECUTION */
        AudioOption mainAudio = createAndInitProvider(
                getAudiomapXml("xml/audiomap/4-empty-audiomap.xml"), contextProvider)
                .getMainAudio();

        /* VALIDATION */
        assertEquals("main-audio.mov", mainAudio.getFileName());
        assertEquals("en-US", mainAudio.getLocale());

        assertEquals("Track count", 7, mainAudio.size());

        assertChannelEquals(seq1, 1, mainAudio.get(0).get(FL.name()));
        assertChannelEquals(seq1, 2, mainAudio.get(1).get(FR.name()));
        assertChannelEquals(seq1, 3, mainAudio.get(2).get(FC.name()));
        assertChannelEquals(seq1, 4, mainAudio.get(3).get(LFE.name()));
        assertChannelEquals(seq2, 1, mainAudio.get(4).get(SL.name()));
        assertChannelEquals(seq2, 2, mainAudio.get(5).get(SR.name()));

        assertChannelEquals(seq1, 1, mainAudio.get(6).get(FL.name()));

        assertChannelEquals(seq1, 2, mainAudio.get(6).get(FR.name()));
    }

    @Test(expected = ConversionException.class)
    public void emptyOption1AWithoutEnoughSequencesChanneslThrowsException() throws Exception {
        String seq1 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711";
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(
                contextProvider,
                trackChannels(seq1, 1));

        /* EXECUTION */
        createAndInitProvider(
                getAudiomapXml("xml/audiomap/1a-empty-audiomap.xml"), contextProvider)
                .getMainAudio();

        /* VALIDATION */
    }

    @Test(expected = ConversionException.class)
    public void emptyOption2WithoutEnoughSequencesChanneslThrowsException() throws Exception {
        String seq1 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711";
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(
                contextProvider,
                trackChannels(seq1, 1));

        /* EXECUTION */
        createAndInitProvider(
                getAudiomapXml("xml/audiomap/2-empty-audiomap.xml"), contextProvider)
                .getMainAudio();

        /* VALIDATION */
    }

    @Test(expected = ConversionException.class)
    public void emptyOption3WithoutEnoughSequencesChanneslThrowsException() throws Exception {
        String seq1 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711";
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(
                contextProvider,
                trackChannels(seq1, 4));

        /* EXECUTION */
        createAndInitProvider(
                getAudiomapXml("xml/audiomap/3-empty-audiomap.xml"), contextProvider)
                .getMainAudio();

        /* VALIDATION */
    }

    @Test(expected = ConversionException.class)
    public void emptyOption4WithoutEnoughSequencesChanneslThrowsException() throws Exception {
        String seq1 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711";
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(
                contextProvider,
                trackChannels(seq1, 4));

        /* EXECUTION */
        createAndInitProvider(
                getAudiomapXml("xml/audiomap/4-empty-audiomap.xml"), contextProvider)
                .getMainAudio();

        /* VALIDATION */
    }

    @Test
    public void emptyOption5WithMonoSequenceCanBeGenerated() throws Exception {
        String seq1 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711";
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(
                contextProvider,
                trackChannels(seq1, 1));

        /* EXECUTION */
        AudioOption mainAudio = createAndInitProvider(
                getAudiomapXml("xml/audiomap/5-empty-audiomap.xml"), contextProvider)
                .getMainAudio();

        /* VALIDATION */
        assertEquals("main-audio.mov", mainAudio.getFileName());
        assertEquals("en-US", mainAudio.getLocale());

        assertEquals("Track count", 2, mainAudio.size());

        assertChannelEquals(seq1, 1, mainAudio.get(0).get(FL.name()));

        assertChannelEquals(seq1, 1, mainAudio.get(1).get(FR.name()));
    }

    @Test
    public void emptyOption6WithMonoSequenceCanBeGenerated() throws Exception {
        String seq1 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711";
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(
                contextProvider,
                trackChannels(seq1, 1));

        /* EXECUTION */
        AudioOption mainAudio = createAndInitProvider(
                getAudiomapXml("xml/audiomap/6-empty-audiomap.xml"), contextProvider)
                .getMainAudio();

        /* VALIDATION */
        assertEquals("main-audio.mov", mainAudio.getFileName());
        assertEquals("en-US", mainAudio.getLocale());

        assertEquals("Track count", 1, mainAudio.size());

        assertChannelEquals(seq1, 1, mainAudio.get(0).get(FL.name()));
        assertChannelEquals(seq1, 1, mainAudio.get(0).get(FR.name()));
    }

    /**
     * Verify mainAudio parameter for 4 sequences with channels configuration 4 - 2 - 2 - 2.
     *
     * @throws Exception unexpected exception
     */
    @Test
    public void mainAudioForFourSequencesWithOneQuadOthersTwoChannelsCreatedCorrectly() throws Exception {
        /* PREPARATION */
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(
                contextProvider,
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 4),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712", 2),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd713", 2),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd714", 2));

        /* EXECUTION */
        String mainAudio = createAndInitProvider(
                getAudiomapXml("xml/audiomap/4s-4-2-2-2-m2-a6-audiomap.xml"), contextProvider)
                .getMainAudioFileName();

        /* VALIDATION */
        assertEquals("main audio file name", "main-audio.mov", mainAudio);
    }

    /**
     * Verify mainAudioTracks parameter for 4 sequences with channels configuration 4 - 2 - 2 - 2.
     *
     * @throws Exception unexpected exception
     */
    @Test
    public void mainAudioTracksForFourSequencesWithOneQuadOthersTwoChannelsCreatedCorrectly() throws Exception {
        /* PREPARATION */
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(
                contextProvider,
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 4),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712", 2),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd713", 2),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd714", 2));

        /* EXECUTION */
        int mainAudioTracks = createAndInitProvider(
                getAudiomapXml("xml/audiomap/4s-4-2-2-2-m2-a6-audiomap.xml"), contextProvider)
                .getMainAudioTracks();

        /* VALIDATION */
        assertEquals("main audio tracks count", 8, mainAudioTracks);
    }

    /**
     * Verify additionalAudioCount parameter for 4 sequences with channels configuration 4 - 2 - 2 - 2.
     *
     * @throws Exception unexpected exception
     */
    @Test
    public void additionalAudioCountForFourSequencesWithOneQuadOthersTwoChannelsCreatedCorrectly() throws Exception {
        /* PREPARATION */
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(
                contextProvider,
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 4),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712", 2),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd713", 2),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd714", 2));

        /* EXECUTION */
        int additionalAudioCount = createAndInitProvider(
                getAudiomapXml("xml/audiomap/4s-4-2-2-2-m2-a6-audiomap.xml"), contextProvider)
                .getAdditionalAudioCount();

        /* VALIDATION */
        assertEquals("additional audio count", 1, additionalAudioCount);
    }

    /**
     * Verify additionalAudioTracks parameters for 4 sequences with channels configuration 4 - 2 - 2 - 2.
     *
     * @throws Exception unexpected exception
     */
    @Test
    public void additionalAudioTracksForFourSequencesWithOneQuadOthersTwoChannelsCreatedCorrectly() throws Exception {
        /* PREPARATION */
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(
                contextProvider,
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 4),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712", 2),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd713", 2),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd714", 2));

        /* EXECUTION */
        ArrayList<Integer> additionalAudioTracks = createAndInitProvider(
                getAudiomapXml("xml/audiomap/4s-4-2-2-2-m2-a6-audiomap.xml"), contextProvider)
                .getAdditionalAudioTracks();

        /* VALIDATION */
        assertEquals(1, additionalAudioTracks.size());
        assertEquals(1, additionalAudioTracks.get(0).intValue());
    }

    /**
     * Verify pan parameters for 4 sequences with channels configuration 4 - 2 - 2 - 2.
     *
     * @throws Exception unexpected exception
     */
    @Test
    public void panParametersForFourSequencesWithOneQuadOthersTwoChannelsCreatedCorrectly() throws Exception {
        /* PREPARATION */
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(
                contextProvider,
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 4),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712", 2),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd713", 2),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd714", 2));

        /* EXECUTION */
        ArrayList<String> panParameters = createAndInitProvider(
                getAudiomapXml("xml/audiomap/4s-4-2-2-2-m2-a6-audiomap.xml"), contextProvider)
                .getPanParameters();

        /* VALIDATION */
        assertEquals(9, panParameters.size());
        assertEquals("Main Track1", "pan=1c|c0=c6,aformat=channel_layouts=FL", panParameters.get(0));
        assertEquals("Main Track2", "pan=1c|c0=c7,aformat=channel_layouts=FR", panParameters.get(1));
        assertEquals("Main Track3", "pan=1c|c0=c2,aformat=channel_layouts=FC", panParameters.get(2));
        assertEquals("Main Track4", "pan=1c|c0=c3,aformat=channel_layouts=LFE", panParameters.get(3));
        assertEquals("Main Track5", "pan=1c|c0=c8,aformat=channel_layouts=SL", panParameters.get(4));
        assertEquals("Main Track6", "pan=1c|c0=c5,aformat=channel_layouts=SR", panParameters.get(5));
        assertEquals("Main Track7", "pan=1c|c0=c4,aformat=channel_layouts=FL", panParameters.get(6));
        assertEquals("Main Track8", "pan=1c|c0=c9,aformat=channel_layouts=FR", panParameters.get(7));

        assertEquals("Alt1 Track1", "pan=2c|c0=c0|c1=c1,aformat=channel_layouts=FL+FR", panParameters.get(8));
    }

    /**
     * Verify additionalAudio parameters for 4 sequences with channels configuration 4 - 2 - 2 - 2.
     *
     * @throws Exception unexpected exception
     */
    @Test
    public void additionalAudioForFourSequencesWithOneQuadOthersTwoChannelsCreatedCorrectly() throws Exception {
        /* PREPARATION */
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(
                contextProvider,
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 4),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712", 2),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd713", 2),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd714", 2));

        /* EXECUTION */
        ArrayList<String> additionalAudio = createAndInitProvider(
                getAudiomapXml("xml/audiomap/4s-4-2-2-2-m2-a6-audiomap.xml"), contextProvider)
                .getAdditionalAudioFileNames();

        /* VALIDATION */
        assertEquals(1, additionalAudio.size());
        assertEquals("audio_JA.mov", additionalAudio.get(0));
    }

    /**
     * Verify mainAudio parameter for 3 sequences with channels configuration 4 - 6 - 4.
     *
     * @throws Exception unexpected exception
     */
    @Test
    public void mainAudioForThreeSequencesWithFourSixFourChannelsCreatedCorrectly() throws Exception {
        /* PREPARATION */
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(
                contextProvider,
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 4),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712", 6),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd713", 4));

        /* EXECUTION */
        String mainAudio = createAndInitProvider(
                getAudiomapXml("xml/audiomap/3s-4-6-4-m3-a5-a6-a6-audiomap.xml"), contextProvider)
                .getMainAudioFileName();

        /* VALIDATION */
        assertEquals("main audio file name", "main-audio.mov", mainAudio);
    }

    /**
     * Verify mainAudioTracks parameter for 3 sequences with channels configuration 4 - 6 - 4.
     *
     * @throws Exception unexpected exception
     */
    @Test
    public void mainAudioTracksForThreeSequencesWithFourSixFourChannelsCreatedCorrectly() throws Exception {
        /* PREPARATION */
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(
                contextProvider,
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 4),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712", 6),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd713", 4));

        /* EXECUTION */
        int mainAudioTracks = createAndInitProvider(
                getAudiomapXml("xml/audiomap/3s-4-6-4-m3-a5-a6-a6-audiomap.xml"), contextProvider)
                .getMainAudioTracks();

        /* VALIDATION */
        assertEquals("main audio tracks count", 2, mainAudioTracks);
    }

    /**
     * Verify additionalAudioCount parameter for 3 sequences with channels configuration 4 - 6 - 4.
     *
     * @throws Exception unexpected exception
     */
    @Test
    public void additionalAudioCountForThreeSequencesWithFourSixFourChannelsCreatedCorrectly() throws Exception {
        /* PREPARATION */
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(
                contextProvider,
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 4),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712", 6),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd713", 4));

        /* EXECUTION */
        int additionalAudioCount = createAndInitProvider(
                getAudiomapXml("xml/audiomap/3s-4-6-4-m3-a5-a6-a6-audiomap.xml"), contextProvider)
                .getAdditionalAudioCount();

        /* VALIDATION */
        assertEquals("additional audio count", 3, additionalAudioCount);
    }

    /**
     * Verify additionalAudioTracks parameter for 3 sequences with channels configuration 4 - 6 - 4.
     *
     * @throws Exception unexpected exception
     */
    @Test
    public void additionalAudioTracksForThreeSequencesWithFourSixFourChannelsCreatedCorrectly() throws Exception {
        /* PREPARATION */
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(
                contextProvider,
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 4),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712", 6),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd713", 4));

        /* EXECUTION */
        ArrayList<Integer> additionalAudioTracks = createAndInitProvider(
                getAudiomapXml("xml/audiomap/3s-4-6-4-m3-a5-a6-a6-audiomap.xml"), contextProvider)
                .getAdditionalAudioTracks();

        /* VALIDATION */
        assertEquals(3, additionalAudioTracks.size());
        assertEquals(2, additionalAudioTracks.get(0).intValue());
        assertEquals(1, additionalAudioTracks.get(1).intValue());
        assertEquals(1, additionalAudioTracks.get(2).intValue());
    }

    /**
     * Verify panParameter parameters for 3 sequences with channels configuration 4 - 6 - 4.
     *
     * @throws Exception unexpected exception
     */
    @Test
    public void panParameterForThreeSequencesWithFourSixFourChannelsCreatedCorrectly() throws Exception {
        /* PREPARATION */
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(
                contextProvider,
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711", 4),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712", 6),
                trackChannels("urn:uuid:63b41d86-c5df-4169-b036-3a25024bd713", 4));

        /* EXECUTION */
        ArrayList<String> panParameters = createAndInitProvider(
                getAudiomapXml("xml/audiomap/3s-4-6-4-m3-a5-a6-a6-audiomap.xml"), contextProvider)
                .getPanParameters();

        /* VALIDATION */
        assertEquals(6, panParameters.size());
        assertEquals("Main Track1",
                "pan=6c|c0=c0|c1=c1|c2=c6|c3=c7|c4=c5|c5=c4,aformat=channel_layouts=FL+FR+FC+LFE+SL+SR",
                panParameters.get(0));
        assertEquals("Main Track2", "pan=2c|c0=c2|c1=c3,aformat=channel_layouts=FL+FR", panParameters.get(1));

        assertEquals("Alt1 Track1", "pan=1c|c0=c12,aformat=channel_layouts=FL", panParameters.get(2));
        assertEquals("Alt1 Track2", "pan=1c|c0=c13,aformat=channel_layouts=FR", panParameters.get(3));

        assertEquals("Alt2 Track1", "pan=2c|c0=c8|c1=c9,aformat=channel_layouts=FL+FR", panParameters.get(4));

        assertEquals("Alt3 Track1", "pan=2c|c0=c11|c1=c10,aformat=channel_layouts=FL+FR", panParameters.get(5));
    }

    /**
     * Checks audiomap.xml generation with 3 mono sequences.
     *
     * @throws Exception
     */
    @Test
    public void generateAudioMapXmlWhenThreeMonoSequences() throws Exception {
        /* PREPARATION */
        String seq1 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711";
        String seq2 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712";
        String seq3 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd713";
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(contextProvider,
                trackChannels(seq1, 1),
                trackChannels(seq2, 1),
                trackChannels(seq3, 1));

        /* EXECUTION */
        AudioMapXmlProvider provider = createAndInitProvider(contextProvider);
        AudioOption mainAudio = provider.getMainAudio();

        /* VALIDATION */
        assertEquals("en-US", mainAudio.getLocale());
        assertEquals("main-audio.mov", mainAudio.getFileName());

        assertChannelEquals(seq1, 1, mainAudio.get(0).get(FL.name()));
        assertChannelEquals(seq2, 1, mainAudio.get(0).get(FR.name()));

        assertEquals(0, provider.getAlternativesAudio().size());
    }

    /**
     * Checks audiomap.xml generation with 1 mono sequence.
     *
     * @throws Exception
     */
    @Test
    public void generateAudioMapXmlWhenOneMonoSequence() throws Exception {
        /* PREPARATION */
        String seq1 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711";
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(contextProvider, trackChannels(seq1, 1));

        /* EXECUTION */
        AudioMapXmlProvider provider = createAndInitProvider(contextProvider);
        AudioOption mainAudio = provider.getMainAudio();

        /* VALIDATION */
        assertEquals("en-US", mainAudio.getLocale());
        assertEquals("main-audio.mov", mainAudio.getFileName());

        assertChannelEquals(seq1, 1, mainAudio.get(0).get(FL.name()));
        assertChannelEquals(seq1, 1, mainAudio.get(0).get(FR.name()));

        assertEquals(0, provider.getAlternativesAudio().size());
    }

    /**
     * Checks audiomap.xml generation with 4 stereo sequences.
     *
     * @throws Exception
     */
    @Test
    public void generateAudioMapXmlWhenFourStereoSequences() throws Exception {
        /* PREPARATION */
        String seq1 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711";
        String seq2 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712";
        String seq3 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd713";
        String seq4 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd714";
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(contextProvider,
                trackChannels(seq1, 2),
                trackChannels(seq2, 2),
                trackChannels(seq3, 2),
                trackChannels(seq4, 2));

        /* EXECUTION */
        AudioMapXmlProvider provider = createAndInitProvider(contextProvider);
        AudioOption mainAudio = provider.getMainAudio();

        /* VALIDATION */
        assertEquals("en-US", mainAudio.getLocale());
        assertEquals("main-audio.mov", mainAudio.getFileName());

        assertChannelEquals(seq1, 1, mainAudio.get(0).get(FL.name()));
        assertChannelEquals(seq1, 2, mainAudio.get(0).get(FR.name()));
        assertChannelEquals(seq2, 1, mainAudio.get(0).get(FC.name()));
        assertChannelEquals(seq2, 2, mainAudio.get(0).get(LFE.name()));
        assertChannelEquals(seq3, 1, mainAudio.get(0).get(SL.name()));
        assertChannelEquals(seq3, 2, mainAudio.get(0).get(SR.name()));
        assertChannelEquals(seq4, 1, mainAudio.get(1).get(FL.name()));
        assertChannelEquals(seq4, 2, mainAudio.get(1).get(FR.name()));

        assertEquals(0, provider.getAlternativesAudio().size());
    }

    /**
     * Checks audiomap.xml generation with 3 stereo and one mono sequences.
     *
     * @throws Exception
     */
    @Test
    public void generateAudioMapXmlWhen3StereoAndOneMonoSequences() throws Exception {
        /* PREPARATION */
        String seq1 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711";
        String seq2 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd712";
        String seq3 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd713";
        String seq4 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd714";
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(contextProvider,
                trackChannels(seq1, 2),
                trackChannels(seq2, 2),
                trackChannels(seq3, 2),
                trackChannels(seq4, 1));

        /* EXECUTION */
        AudioMapXmlProvider provider = createAndInitProvider(contextProvider);
        AudioOption mainAudio = provider.getMainAudio();

        /* VALIDATION */
        assertEquals("en-US", mainAudio.getLocale());
        assertEquals("main-audio.mov", mainAudio.getFileName());

        assertChannelEquals(seq1, 1, mainAudio.get(0).get(FL.name()));
        assertChannelEquals(seq1, 2, mainAudio.get(0).get(FR.name()));
        assertChannelEquals(seq2, 1, mainAudio.get(0).get(FC.name()));
        assertChannelEquals(seq2, 2, mainAudio.get(0).get(LFE.name()));
        assertChannelEquals(seq3, 1, mainAudio.get(0).get(SL.name()));
        assertChannelEquals(seq3, 2, mainAudio.get(0).get(SR.name()));
        assertChannelEquals(seq1, 1, mainAudio.get(1).get(FL.name()));
        assertChannelEquals(seq1, 2, mainAudio.get(1).get(FR.name()));

        assertEquals(0, provider.getAlternativesAudio().size());
    }

    @Test
    public void emptySurroundOptionFromDescriptor() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR},
                        {FC},
                        {FC},
                        {FL, FR},
                        {FL, FR, FC, LFE, SL, SR}
                },
                new String[]{"fr-CA", "en-US", "en-US", "fr-CA", "en-US"});

        /* EXECUTION */
        AudioOption mainAudio = createAndInitProvider(
                getAudiomapXml("xml/audiomap/1a-empty-audiomap.xml"), contextProvider)
                .getMainAudio();

        /* VALIDATION */
        assertEquals("main-audio.mov", mainAudio.getFileName());
        assertEquals("en-US", mainAudio.getLocale());

        assertEquals("Track count", 3, mainAudio.size());

        assertChannelEquals(uuid(4), 1, mainAudio.get(0).get(FL.name()));
        assertChannelEquals(uuid(4), 2, mainAudio.get(0).get(FR.name()));
        assertChannelEquals(uuid(4), 3, mainAudio.get(0).get(FC.name()));
        assertChannelEquals(uuid(4), 4, mainAudio.get(0).get(LFE.name()));
        assertChannelEquals(uuid(4), 5, mainAudio.get(0).get(SL.name()));
        assertChannelEquals(uuid(4), 6, mainAudio.get(0).get(SR.name()));

        assertChannelEquals(uuid(4), 1, mainAudio.get(1).get(FL.name()));
        assertChannelEquals(uuid(4), 2, mainAudio.get(2).get(FR.name()));
    }

    @Test
    public void emptySurroundOptionWithStereoFromDescriptor() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR},
                        {FC},
                        {FC},
                        {FL, FR},
                        {FL, FR, FC, LFE, SL, SR}
                },
                new String[]{"en-US", "en-US", "en-US", "fr-CA", "en-US"});

        /* EXECUTION */
        AudioOption mainAudio = createAndInitProvider(
                getAudiomapXml("xml/audiomap/1a-empty-audiomap.xml"), contextProvider)
                .getMainAudio();

        /* VALIDATION */
        assertEquals("main-audio.mov", mainAudio.getFileName());
        assertEquals("en-US", mainAudio.getLocale());

        assertEquals("Track count", 3, mainAudio.size());

        assertChannelEquals(uuid(4), 1, mainAudio.get(0).get(FL.name()));
        assertChannelEquals(uuid(4), 2, mainAudio.get(0).get(FR.name()));
        assertChannelEquals(uuid(4), 3, mainAudio.get(0).get(FC.name()));
        assertChannelEquals(uuid(4), 4, mainAudio.get(0).get(LFE.name()));
        assertChannelEquals(uuid(4), 5, mainAudio.get(0).get(SL.name()));
        assertChannelEquals(uuid(4), 6, mainAudio.get(0).get(SR.name()));

        assertChannelEquals(uuid(0), 1, mainAudio.get(1).get(FL.name()));
        assertChannelEquals(uuid(0), 2, mainAudio.get(2).get(FR.name()));
    }

    @Test
    public void emptySurroundOptionByOrderFromDescriptor() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR},
                        {FC},
                        {FC},
                        {FL, FR},
                        {FL, FR, FC, LFE, SL, SR}
                },
                new String[]{"en-US", "", "en-US", "", "fr-CA"});

        /* EXECUTION */
        AudioOption mainAudio = createAndInitProvider(
                getAudiomapXml("xml/audiomap/1a-empty-audiomap.xml"), contextProvider)
                .getMainAudio();

        /* VALIDATION */
        assertEquals("main-audio.mov", mainAudio.getFileName());
        assertEquals("en-US", mainAudio.getLocale());

        assertEquals("Track count", 3, mainAudio.size());

        assertChannelEquals(uuid(0), 1, mainAudio.get(0).get(FL.name()));
        assertChannelEquals(uuid(0), 2, mainAudio.get(0).get(FR.name()));
        assertChannelEquals(uuid(1), 1, mainAudio.get(0).get(FC.name()));
        assertChannelEquals(uuid(2), 1, mainAudio.get(0).get(LFE.name()));
        assertChannelEquals(uuid(3), 1, mainAudio.get(0).get(SL.name()));
        assertChannelEquals(uuid(3), 2, mainAudio.get(0).get(SR.name()));

        assertChannelEquals(uuid(4), 1, mainAudio.get(1).get(FL.name()));
        assertChannelEquals(uuid(4), 2, mainAudio.get(2).get(FR.name()));
    }

    @Test
    public void emptyStereoOptionFromDescriptor() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR},
                        {FC},
                        {FC},
                        {FL, FR},
                        {FL, FR}
                },
                new String[]{"fr-CA", "en-US", "", "en", "en-US"});

        /* EXECUTION */
        AudioOption mainAudio = createAndInitProvider(
                getAudiomapXml("xml/audiomap/5-empty-audiomap.xml"), contextProvider)
                .getMainAudio();

        /* VALIDATION */
        assertEquals("main-audio.mov", mainAudio.getFileName());
        assertEquals("en-US", mainAudio.getLocale());

        assertEquals("Track count", 2, mainAudio.size());

        assertChannelEquals(uuid(3), 1, mainAudio.get(0).get(FL.name()));
        assertChannelEquals(uuid(3), 2, mainAudio.get(1).get(FR.name()));
    }

    @Test
    public void emptyStereoOptionByOrderFromDescriptor() throws Exception {
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR},
                        {FC},
                        {FC},
                        {FL, FR},
                        {FL, FR, FC, LFE, SL, SR}
                },
                new String[]{"fr-CA", "en-US", "en-US", "fr-CA", "en-US"});

        /* EXECUTION */
        AudioOption mainAudio = createAndInitProvider(
                getAudiomapXml("xml/audiomap/5-empty-audiomap.xml"), contextProvider)
                .getMainAudio();

        /* VALIDATION */
        assertEquals("main-audio.mov", mainAudio.getFileName());
        assertEquals("en-US", mainAudio.getLocale());

        assertEquals("Track count", 2, mainAudio.size());

        // generated by order
        assertChannelEquals(uuid(0), 1, mainAudio.get(0).get(FL.name()));
        assertChannelEquals(uuid(0), 2, mainAudio.get(1).get(FR.name()));
    }

    @Test
    public void emptySurroundOptionWithTwoAlternativesFromDescriptor() throws Exception {
        /* PREPARATION */
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR},
                        {FC},
                        {FC},
                        {FL, FR},
                        {FL, FR, FC, LFE, SL, SR}
                },
                new String[]{"ja", "en-US", "en-US", "de", "en-GB"});

        /* EXECUTION */
        AudioMapXmlProvider provider = new AudioMapXmlProvider(
                getAudiomapXml("xml/audiomap/3-6-5-audiomap.xml"), contextProvider);

        // set for de option custom values to exclude this option from default channel mapping routine
        Option6Type deOption = provider.getAudioMap().getAlternativeAudio().get(0).getOption6();
        deOption.setTrack1(new Option6Type.Track1());
        ChannelType deLeft = new ChannelType();
        deLeft.setCPLVirtualTrackChannel(1);
        deLeft.setCPLVirtualTrackId("urn:uuid:seq:audio:4");
        ChannelType deRight = new ChannelType();
        deRight.setCPLVirtualTrackChannel(2);
        deRight.setCPLVirtualTrackId("urn:uuid:seq:audio:4");
        deOption.getTrack1().setL(deLeft);
        deOption.getTrack1().setR(deRight);

        provider.initAudio();

        AudioOption mainAudio = provider.getMainAudio();
        AudioOption deAudio = provider.getAlternativesAudio().get(0);
        AudioOption jaAudio = provider.getAlternativesAudio().get(1);

        /* VALIDATION */

        // main audio
        assertEquals("main-audio.mov", mainAudio.getFileName());
        assertEquals("en-US", mainAudio.getLocale());

        assertEquals("Track count", 2, mainAudio.size());

        // main audio defined by order (started from 2 sequence)
        assertChannelEquals(uuid(1), 1, mainAudio.get(0).get(FL.name()));
        assertChannelEquals(uuid(2), 1, mainAudio.get(0).get(FR.name()));
        assertChannelEquals(uuid(3), 1, mainAudio.get(0).get(FC.name()));
        assertChannelEquals(uuid(3), 2, mainAudio.get(0).get(LFE.name()));
        assertChannelEquals(uuid(4), 1, mainAudio.get(0).get(SL.name()));
        assertChannelEquals(uuid(4), 2, mainAudio.get(0).get(SR.name()));

        assertChannelEquals(uuid(4), 3, mainAudio.get(1).get(FL.name()));
        assertChannelEquals(uuid(4), 4, mainAudio.get(1).get(FR.name()));

        // de audio
        assertEquals("audio_DE.mov", deAudio.getFileName());
        assertEquals("de", deAudio.getLocale());

        assertEquals("Track count", 1, deAudio.size());

        // de audio defined by audiomap
        assertChannelEquals(uuid(4), 1, deAudio.get(0).get(FL.name()));
        assertChannelEquals(uuid(4), 2, deAudio.get(0).get(FR.name()));

        // ja audio
        assertEquals("audio_JA.mov", jaAudio.getFileName());
        assertEquals("ja", jaAudio.getLocale());

        assertEquals("Track count", 2, jaAudio.size());

        // ja audio defined by essence descriptor (1st sequence)
        assertChannelEquals(uuid(0), 1, jaAudio.get(0).get(FL.name()));
        assertChannelEquals(uuid(0), 2, jaAudio.get(1).get(FR.name()));
    }

    @Test(expected = ConversionException.class)
    public void emptySurroundOptionWithTwoAlternativesFromDescriptorConversionException() throws Exception {
        /* PREPARATION */
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR},
                        {FC},
                        {FC},
                        {FL, FR},
                        {FL, FR}
                },
                new String[]{"ja", "en-US", "en-US", "de", "en-GB"});

        /* EXECUTION */
        AudioMapXmlProvider provider = new AudioMapXmlProvider(
                getAudiomapXml("xml/audiomap/3-6-5-audiomap.xml"), contextProvider);

        // can not pass initialization: main audio can't be defined by natural order of unused tracks (not enough channels)
        provider.initAudio();
    }

    @Test
    public void emptyStereoOptionWithTwoAlternativesFromDescriptor() throws Exception {
        /* PREPARATION */
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR},
                        {FC},
                        {FC},
                        {FL, FR},
                        {FL, FR, FC, LFE, SL, SR}
                },
                new String[]{"ja", "en-US", "de", "en-US", "en-US"});

        /* EXECUTION */
        AudioMapXmlProvider provider = new AudioMapXmlProvider(
                getAudiomapXml("xml/audiomap/5-6-5-audiomap.xml"), contextProvider);

        provider.initAudio();

        AudioOption mainAudio = provider.getMainAudio();
        AudioOption deAudio = provider.getAlternativesAudio().get(0);
        AudioOption jaAudio = provider.getAlternativesAudio().get(1);

        /* VALIDATION */

        // main audio
        assertEquals("main-audio.mov", mainAudio.getFileName());
        assertEquals("en-US", mainAudio.getLocale());

        assertEquals("Track count", 2, mainAudio.size());

        // main audio defined by essence descriptor
        assertChannelEquals(uuid(3), 1, mainAudio.get(0).get(FL.name()));
        assertChannelEquals(uuid(3), 2, mainAudio.get(1).get(FR.name()));

        // de audio
        assertEquals("audio_DE.mov", deAudio.getFileName());
        assertEquals("de", deAudio.getLocale());

        assertEquals("Track count", 1, deAudio.size());

        // de audio defined by order
        assertChannelEquals(uuid(1), 1, deAudio.get(0).get(FL.name()));
        assertChannelEquals(uuid(2), 1, deAudio.get(0).get(FR.name()));

        // ja audio
        assertEquals("audio_JA.mov", jaAudio.getFileName());
        assertEquals("ja", jaAudio.getLocale());

        assertEquals("Track count", 2, jaAudio.size());

        // de audio defined by essence descriptor (1st sequence)
        assertChannelEquals(uuid(0), 1, jaAudio.get(0).get(FL.name()));
        assertChannelEquals(uuid(0), 2, jaAudio.get(1).get(FR.name()));
    }

    // Default audiomap generation in accordance with essence descriptor

    @Test
    public void generateDefaultAudioMapWithSurroundFromDescriptor() throws Exception {
        /* PREPARATION */
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR},
                        {FC},
                        {FC},
                        {FL, FR},
                        {FL, FR, FC, LFE, SL, SR}
                },
                new String[]{"en-US", "en-US", "ja", "de", "en-US"});

        /* EXECUTION */
        AudioMapXmlProvider provider = new AudioMapXmlProvider(contextProvider);

        AudioMapType audioMap = provider.getAudioMap();

        /* VALIDATION */

        // main audio from audioMap
        MainAudioType mainAudio = audioMap.getMainAudio();
        assertEquals("main-audio.mov", mainAudio.getName());
        assertEquals("en-US", mainAudio.getLocale());

        Option3Type option3 = mainAudio.getOption3();

        assertChannelEquals(uuid(4), 1, option3.getTrack1().getL());
        assertChannelEquals(uuid(4), 2, option3.getTrack1().getR());
        assertChannelEquals(uuid(4), 3, option3.getTrack1().getC());
        assertChannelEquals(uuid(4), 4, option3.getTrack1().getLFE());
        assertChannelEquals(uuid(4), 5, option3.getTrack1().getLs());
        assertChannelEquals(uuid(4), 6, option3.getTrack1().getRs());

        assertChannelEquals(uuid(0), 1, option3.getTrack2().getLt());
        assertChannelEquals(uuid(0), 2, option3.getTrack2().getRt());

        // alternative from audioMap

        assertEquals(1, audioMap.getAlternativeAudio().size());

        AlternativeAudioType deAudio = audioMap.getAlternativeAudio().get(0);
        assertEquals("audio_DE.mov", deAudio.getName());
        assertEquals("de", deAudio.getLocale());

        Option6Type option6 = deAudio.getOption6();

        assertChannelEquals(uuid(3), 1, option6.getTrack1().getL());
        assertChannelEquals(uuid(3), 2, option6.getTrack1().getR());

        // try to create init provider based on generated audioMap
        provider.initAudio();

        // lightweight assertions
        assertEquals(8, provider.getMainAudio().stream().flatMap(i -> i.values().stream()).count());
        assertEquals(1, provider.getAdditionalAudioCount());
    }

    @Test
    public void generateDefaultAudioMapWithStereoFromDescriptor() throws Exception {
        /* PREPARATION */
        TemplateParameterContextProvider contextProvider = AudioUtils.createContext(
                new FFmpegAudioChannels[][]{
                        {FL, FR},
                        {FC},
                        {FC},
                        {FL, FR},
                        {FL, FR, FC, LFE, SL, SR}
                },
                new String[]{"en-US", "en-US", "ja", "en", "en-GB"});

        /* EXECUTION */
        AudioMapXmlProvider provider = new AudioMapXmlProvider(contextProvider);

        AudioMapType audioMap = provider.getAudioMap();

        /* VALIDATION */

        // main audio from audioMap
        MainAudioType mainAudio = audioMap.getMainAudio();
        assertEquals("main-audio.mov", mainAudio.getName());
        assertEquals("en-US", mainAudio.getLocale());

        Option6Type option6;
        option6 = mainAudio.getOption6();

        assertChannelEquals(uuid(0), 1, option6.getTrack1().getL());
        assertChannelEquals(uuid(0), 2, option6.getTrack1().getR());

        // no alternative can't be defined
        assertTrue(audioMap.getAlternativeAudio().isEmpty());

        // try to create init provider based on generated audioMap
        provider.initAudio();

        // lightweight assertions
        assertEquals(2, provider.getMainAudio().stream().flatMap(i -> i.values().stream()).count());
        assertEquals(0, provider.getAdditionalAudioCount());
    }

    @Test
    public void testUpdateLocale() throws Exception {
        /* PREPARATION */
        String seq1 = "urn:uuid:63b41d86-c5df-4169-b036-3a25024bd711";
        TemplateParameterContextProvider contextProvider =
                TemplateParameterContextCreator.createDefaultContextProvider();
        prepareCplVirtualTracksWithChannels(contextProvider, trackChannels(seq1, 1));

        /* EXECUTION */
        AudioMapXmlProvider provider = createAndInitProvider(contextProvider);
        provider.setLocale(Locale.CANADA_FRENCH);

        /* VALIDATION */
        assertEquals("fr-CA", provider.getAudioMap().getMainAudio().getLocale());
    }

    private static File getAudiomapXml(String path) throws URISyntaxException {
        return new File(ClassLoader.getSystemClassLoader().getResource(path).toURI());
    }

    private static SimpleEntry<String, Integer> trackChannels(String uuid, Integer channels) {
        return new SimpleEntry<>(uuid, channels);
    }

    private static void assertChannelEquals(String uuid, int chNumber, ChannelType actualCh) {
        assertEquals(uuid, actualCh.getCPLVirtualTrackId());
        assertEquals(chNumber, actualCh.getCPLVirtualTrackChannel());
    }

    private static AudioMapXmlProvider createAndInitProvider(File audiomapFile, TemplateParameterContextProvider contextProvider)
            throws Exception {
        AudioMapXmlProvider provider = new AudioMapXmlProvider(audiomapFile, contextProvider);
        provider.initAudio();
        return provider;
    }

    private static AudioMapXmlProvider createAndInitProvider(TemplateParameterContextProvider contextProvider)
            throws Exception {
        return createAndInitProvider(null, contextProvider);
    }

    private static String uuid(int seqNum) {
        return getSequenceUuid(seqNum, SequenceType.AUDIO).getUuid();
    }

    @SafeVarargs
    private final void prepareCplVirtualTracksWithChannels(TemplateParameterContextProvider contextProvider,
                                                           SimpleEntry<String, Integer>... entries) {
        SequenceTemplateParameterContext sequenceContext = contextProvider.getSequenceContext();
        Stream.of(entries).forEach((e) -> {
                    sequenceContext.initSequence(SequenceType.AUDIO, SequenceUUID.create(e.getKey())); // as in test-audiomap.xml
                    contextProvider.getSequenceContext().addSequenceParameter(
                            SequenceType.AUDIO,
                            SequenceUUID.create(e.getKey()),
                            SequenceContextParameters.CHANNELS_NUM,
                            String.valueOf(e.getValue()));
                }

        );
    }
}
