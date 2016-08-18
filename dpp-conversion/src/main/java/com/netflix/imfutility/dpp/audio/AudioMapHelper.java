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
package com.netflix.imfutility.dpp.audio;

import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.dpp.audiomap.AudioMapType;
import com.netflix.imfutility.generated.dpp.audiomap.EBUTrackType;
import com.netflix.imfutility.generated.dpp.audiomap.ObjectFactory;
import com.netflix.imfutility.util.FFmpegAudioChannels;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;

import static com.netflix.imfutility.util.FFmpegAudioChannels.FC;
import static com.netflix.imfutility.util.FFmpegAudioChannels.FL;
import static com.netflix.imfutility.util.FFmpegAudioChannels.FR;
import static com.netflix.imfutility.util.FFmpegAudioChannels.LFE;
import static com.netflix.imfutility.util.FFmpegAudioChannels.SL;
import static com.netflix.imfutility.util.FFmpegAudioChannels.SR;

/**
 * Helper methods related to creation of an Audio Map.
 */
public final class AudioMapHelper {

    public static void writeAudioMapToFile(File outputFile, AudioMapType audioMap) {
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(AudioMapType.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            JAXBElement<AudioMapType> audioMapJaxb = new ObjectFactory().createAudioMap(audioMap);
            jaxbMarshaller.marshal(audioMapJaxb, outputFile);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static FFmpegAudioChannels[] getStereoLayout() {
        return new FFmpegAudioChannels[]{FL, FR};
    }

    public static FFmpegAudioChannels[] get51Layout() {
        return new FFmpegAudioChannels[]{FL, FR, FC, LFE, SL, SR};
    }


    public static void addStereo(AudioMapType audioMapType, int num, SoundfieldGroupInfo stereo) {
        for (FFmpegAudioChannels channel : getStereoLayout()) {
            audioMapType.getEBUTrack().add(
                    createEbuTrack(num, stereo.getChannelsMap().get(channel)));
            num++;
        }
    }

    public static void addStereoSilence(AudioMapType audioMapType, int num) {
        for (FFmpegAudioChannels channel : getStereoLayout()) {
            audioMapType.getEBUTrack().add(createSilenceEbuTrack(num));
            num++;
        }
    }

    public static void add51(AudioMapType audioMapType, int num, SoundfieldGroupInfo fiveOne) {
        for (FFmpegAudioChannels channel : get51Layout()) {
            audioMapType.getEBUTrack().add(
                    createEbuTrack(num, fiveOne.getChannelsMap().get(channel)));
            num++;
        }
    }

    public static void add51Silence(AudioMapType audioMapType, int num) {
        for (FFmpegAudioChannels channel : get51Layout()) {
            audioMapType.getEBUTrack().add(createSilenceEbuTrack(num));
            num++;
        }
    }

    public static EBUTrackType createEbuTrack(int num, ImmutablePair<SequenceUUID, Integer> input) {
        EBUTrackType ebuTrack = new EBUTrackType();
        ebuTrack.setNumber(num);
        ebuTrack.setCPLVirtualTrackId(input.getLeft().getUuid());
        ebuTrack.setCPLVirtualTrackChannel(input.getRight());
        return ebuTrack;
    }

    public static EBUTrackType createSilenceEbuTrack(int num) {
        EBUTrackType ebuTrack = new EBUTrackType();
        ebuTrack.setNumber(num);
        return ebuTrack;
    }

    private AudioMapHelper() {
    }
}
