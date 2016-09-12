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

import com.netflix.imfutility.generated.itunes.audiomap.AlternativeAudioType;
import com.netflix.imfutility.generated.itunes.audiomap.AudioMapType;
import com.netflix.imfutility.generated.itunes.audiomap.ChannelType;
import com.netflix.imfutility.generated.itunes.audiomap.MainAudioType;
import com.netflix.imfutility.generated.itunes.audiomap.ObjectFactory;
import com.netflix.imfutility.generated.itunes.audiomap.Option3Type;
import com.netflix.imfutility.generated.itunes.audiomap.Option6Type;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;

import static com.netflix.imfutility.itunes.ITunesConversionConstants.GEN_ADDITIONAL_SEQ_UUID;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.GEN_MAIN_SEQ_UUID;

/**
 * Provides functionality to generate a sample audiomap.xml for iTunes format.
 */
public final class AudioMapXmlCreator {

    /**
     * Generates a sample audiomap.xml file. Main audio Option3 and one additional track Option6.
     *
     * @param path a path to the output audiomap.xml file
     */
    public static void generateSampleXml(String path) {
        File file = new File(path);
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(AudioMapType.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Audiomap XML structure
            AudioMapType audioMap = new AudioMapType();
            audioMap.setMainAudio(new MainAudioType());
            audioMap.getAlternativeAudio().add(new AlternativeAudioType());

            MainAudioType mainAudio = audioMap.getMainAudio();
            mainAudio.setLocale("en-US");
            mainAudio.setName("main-audio.mov");
            mainAudio.setOption3(new Option3Type());

            Option3Type opt3 = mainAudio.getOption3();
            opt3.setTrack1(new Option3Type.Track1());
            opt3.setTrack2(new Option3Type.Track2());

            Option3Type.Track1 t1 = opt3.getTrack1();
            t1.setL(new ChannelType());
            t1.setR(new ChannelType());
            t1.setC(new ChannelType());
            t1.setLFE(new ChannelType());
            t1.setLs(new ChannelType());
            t1.setRs(new ChannelType());

            ChannelType l = t1.getL();
            l.setCPLVirtualTrackId(GEN_MAIN_SEQ_UUID);
            l.setCPLVirtualTrackChannel(1);
            ChannelType r = t1.getR();
            r.setCPLVirtualTrackId(GEN_MAIN_SEQ_UUID);
            r.setCPLVirtualTrackChannel(2);
            ChannelType c = t1.getC();
            c.setCPLVirtualTrackId(GEN_MAIN_SEQ_UUID);
            c.setCPLVirtualTrackChannel(3);
            ChannelType lfe = t1.getLFE();
            lfe.setCPLVirtualTrackId(GEN_MAIN_SEQ_UUID);
            lfe.setCPLVirtualTrackChannel(4);
            ChannelType ls = t1.getLs();
            ls.setCPLVirtualTrackId(GEN_MAIN_SEQ_UUID);
            ls.setCPLVirtualTrackChannel(5);
            ChannelType rs = t1.getRs();
            rs.setCPLVirtualTrackId(GEN_MAIN_SEQ_UUID);
            rs.setCPLVirtualTrackChannel(6);

            Option3Type.Track2 t2 = opt3.getTrack2();
            t2.setLt(new ChannelType());
            t2.setRt(new ChannelType());

            ChannelType lt = t2.getLt();
            lt.setCPLVirtualTrackId(GEN_MAIN_SEQ_UUID);
            lt.setCPLVirtualTrackChannel(1);
            ChannelType rt = t2.getRt();
            rt.setCPLVirtualTrackId(GEN_MAIN_SEQ_UUID);
            rt.setCPLVirtualTrackChannel(2);

            AlternativeAudioType alt = audioMap.getAlternativeAudio().get(0);
            alt.setLocale("de");
            alt.setName("audio_DE.mov");
            alt.setOption6(new Option6Type());

            Option6Type opt6 = alt.getOption6();
            opt6.setTrack1(new Option6Type.Track1());

            Option6Type.Track1 t61 = opt6.getTrack1();
            t61.setL(new ChannelType());
            t61.setR(new ChannelType());

            ChannelType l61 = t61.getL();
            l61.setCPLVirtualTrackId(GEN_ADDITIONAL_SEQ_UUID);
            l61.setCPLVirtualTrackChannel(1);
            ChannelType r61 = t61.getR();
            r61.setCPLVirtualTrackId(GEN_ADDITIONAL_SEQ_UUID);
            r61.setCPLVirtualTrackChannel(2);

            JAXBElement<AudioMapType> audioMapJaxb = new ObjectFactory().createAudiomap(audioMap);
            jaxbMarshaller.marshal(audioMapJaxb, file);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private AudioMapXmlCreator() {
    }
}
