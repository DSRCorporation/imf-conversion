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

import com.netflix.imfutility.generated.itunes.audiomap.AudioMapType;
import com.netflix.imfutility.generated.itunes.audiomap.Option3Type;
import com.netflix.imfutility.generated.itunes.audiomap.Option6Type;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import org.junit.Test;

import java.io.File;

import static com.netflix.imfutility.itunes.ITunesConversionConstants.GEN_ADDITIONAL_SEQ_UUID;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.GEN_MAIN_SEQ_UUID;
import static junit.framework.TestCase.assertEquals;

/**
 * Unit tests for generation of audiomap.xml.
 */
public class AudioMapXmlCreatorTest {


    /**
     * Checks that generated sample XML file is a correct audiomap.xml file.
     *
     * @throws Exception unexpected exception
     */
    @Test
    public void sampleAudioMapCanBeGenerated() throws Exception {
        /* PREPARATION */
        File sampleFile = File.createTempFile("sample-audiomap", ".xml");
        sampleFile.deleteOnExit();

        AudioMapXmlCreator.generateSampleXml(sampleFile.getAbsolutePath());

        /* EXECUTION */
        AudioMapXmlProvider audioMapProvider = new AudioMapXmlProvider(
                sampleFile,
                TemplateParameterContextCreator.createDefaultContextProvider());

        /* VALIDATION */
        AudioMapType audioMap = audioMapProvider.getAudioMap();
        Option3Type opt3 = audioMap.getMainAudio().getOption3();
        // o3 t1
        assertEquals(GEN_MAIN_SEQ_UUID, opt3.getTrack1().getL().getCPLVirtualTrackId());
        assertEquals(1, opt3.getTrack1().getL().getCPLVirtualTrackChannel());
        assertEquals(GEN_MAIN_SEQ_UUID, opt3.getTrack1().getR().getCPLVirtualTrackId());
        assertEquals(2, opt3.getTrack1().getR().getCPLVirtualTrackChannel());
        assertEquals(GEN_MAIN_SEQ_UUID, opt3.getTrack1().getC().getCPLVirtualTrackId());
        assertEquals(3, opt3.getTrack1().getC().getCPLVirtualTrackChannel());
        assertEquals(GEN_MAIN_SEQ_UUID, opt3.getTrack1().getLFE().getCPLVirtualTrackId());
        assertEquals(4, opt3.getTrack1().getLFE().getCPLVirtualTrackChannel());
        assertEquals(GEN_MAIN_SEQ_UUID, opt3.getTrack1().getLs().getCPLVirtualTrackId());
        assertEquals(5, opt3.getTrack1().getLs().getCPLVirtualTrackChannel());
        assertEquals(GEN_MAIN_SEQ_UUID, opt3.getTrack1().getRs().getCPLVirtualTrackId());
        assertEquals(6, opt3.getTrack1().getRs().getCPLVirtualTrackChannel());
        // o3 t2
        assertEquals(GEN_MAIN_SEQ_UUID, opt3.getTrack2().getLt().getCPLVirtualTrackId());
        assertEquals(1, opt3.getTrack2().getLt().getCPLVirtualTrackChannel());
        assertEquals(GEN_MAIN_SEQ_UUID, opt3.getTrack2().getRt().getCPLVirtualTrackId());
        assertEquals(2, opt3.getTrack2().getRt().getCPLVirtualTrackChannel());

        Option6Type opt6 = audioMap.getAlternativeAudio().get(0).getOption6();
        // o6 t1
        assertEquals(GEN_ADDITIONAL_SEQ_UUID, opt6.getTrack1().getL().getCPLVirtualTrackId());
        assertEquals(1, opt6.getTrack1().getL().getCPLVirtualTrackChannel());
        assertEquals(GEN_ADDITIONAL_SEQ_UUID, opt6.getTrack1().getR().getCPLVirtualTrackId());
        assertEquals(2, opt6.getTrack1().getR().getCPLVirtualTrackChannel());
    }

}
