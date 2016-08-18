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

import com.netflix.imfutility.generated.dpp.audiomap.AudioMapType;
import com.netflix.imfutility.generated.dpp.audiomap.EBUTrackType;
import com.netflix.imfutility.util.ImfLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Provides functionality to generate a sample audiomap.xml for DPP format.
 */
public final class AudioMapXmlCreator {

    private final Logger logger = new ImfLogger(LoggerFactory.getLogger(AudioMapXmlCreator.class));

    /**
     * Generates a sample audiomap.xml file.
     *
     * @param path a path to the output audiomap.xml file
     */
    public static void generateSampleXml(String path) {
        String cplTrackId = "urn:uuid:38d52c00-68d3-4056-8858-28eeaf3238d3";
        // Channel Map
        // Sample with EBU R48: 2a  (2 stereo and 2 silence channels)
        EBUTrackType ebuTrack1 = new EBUTrackType();
        ebuTrack1.setNumber(1);
        ebuTrack1.setCPLVirtualTrackId(cplTrackId);
        ebuTrack1.setCPLVirtualTrackChannel(1);

        EBUTrackType ebuTrack2 = new EBUTrackType();
        ebuTrack2.setNumber(2);
        ebuTrack2.setCPLVirtualTrackId(cplTrackId);
        ebuTrack2.setCPLVirtualTrackChannel(2);

        EBUTrackType ebuTrack3 = new EBUTrackType();
        ebuTrack3.setNumber(3);
        ebuTrack3.setCPLVirtualTrackId(null);
        ebuTrack3.setCPLVirtualTrackChannel(null);

        EBUTrackType ebuTrack4 = new EBUTrackType();
        ebuTrack4.setNumber(4);
        ebuTrack4.setCPLVirtualTrackId(null);
        ebuTrack4.setCPLVirtualTrackChannel(null);

        // Audiomap XML structure
        AudioMapType audioMap = new AudioMapType();
        audioMap.getEBUTrack().add(ebuTrack1);
        audioMap.getEBUTrack().add(ebuTrack2);
        audioMap.getEBUTrack().add(ebuTrack3);
        audioMap.getEBUTrack().add(ebuTrack4);

        AudioMapHelper.writeAudioMapToFile(new File(path), audioMap);
    }

    private AudioMapXmlCreator() {
    }
}
