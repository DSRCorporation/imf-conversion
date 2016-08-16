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

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.conversion.templateParameter.ContextInfoBuilder;
import com.netflix.imfutility.conversion.templateParameter.context.SequenceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.generated.dpp.audiomap.AudioMapType;
import com.netflix.imfutility.generated.dpp.audiomap.EBUTrackType;
import com.netflix.imfutility.generated.dpp.metadata.AudioTrackLayoutDmAs11Type;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.netflix.imfutility.dpp.DppConversionXsdConstants.AUDIOMAP_PACKAGE;
import static com.netflix.imfutility.dpp.DppConversionXsdConstants.AUDIOMAP_XML_SCHEME;

/**
 * <p>
 * Basic functionality for audiomap.xml handling.
 * </p>
 */
public final class AudioMapXmlProvider {

    private final Logger logger = LoggerFactory.getLogger(AudioMapXmlProvider.class);

    private final TemplateParameterContextProvider contextProvider;
    private final AudioTrackLayoutDmAs11Type audioLayout;

    private final AudioMapType audioMap;
    private final LinkedHashMap<String, Integer> channelsForTracks;

    /**
     * Creates a default audiomap.xml (see {@link #getDefaultAudioMap()}, loads it and validates.
     *
     * @param contextProvider context provider.
     * @throws XmlParsingException   an exception in case of audiomap.xml parsing error
     * @throws FileNotFoundException if the audioMapXml doesn't define an existing file.
     */
    public AudioMapXmlProvider(AudioTrackLayoutDmAs11Type audioLayout,
                               TemplateParameterContextProvider contextProvider) throws FileNotFoundException, XmlParsingException {
        this(null, audioLayout, contextProvider);
    }

    /**
     * Loads and validates audiomap.xml. Creates a default audiomap.xml (see {@link #getDefaultAudioMap()} if audioMapXml is null.
     *
     * @param audioMapFile    a path to audiomap.xml file. May be null.
     * @param contextProvider context provider.
     * @throws XmlParsingException   an exception in case of audiomap.xml parsing error
     * @throws FileNotFoundException if the audioMapXml doesn't define an existing file.
     */
    public AudioMapXmlProvider(File audioMapFile, AudioTrackLayoutDmAs11Type audioLayout,
                               TemplateParameterContextProvider contextProvider) throws FileNotFoundException, XmlParsingException {
        this.audioLayout = audioLayout;
        this.contextProvider = contextProvider;

        this.channelsForTracks = getChannelsForTracks();

        if (audioMapFile == null) {
            // if no audiomap.xml is provided - create a default one for the given input and audio layout specified in metadata.xml
            this.audioMap = getDefaultAudioMap();
        } else {
            this.audioMap = loadAudioMapXml(audioMapFile);
        }
    }

    private AudioMapType getDefaultAudioMap() {
        AudioMapType audioMap = null;

        logger.warn("No audiomap.xml specified as a command line argument. A default audiomap.xml will be generated.");
        try {
            audioMap = new AudioMapGuesser(contextProvider, audioLayout).guessAudioMap();
        } catch (InvalidAudioChannelAssignmentException e) {
            logger.warn("Could not generate an audiomap based on EssenceDescriptor: " + e.getMessage());
        }

        if (audioMap == null) {
            logger.info("Generating default audiomap in a natural order...");
            audioMap = generateDefaultXml();
            logger.info("Generated default audiomap in a natural order: OK");
        }
        return audioMap;
    }

    /**
     * Loads and validates audiomap.xml.
     *
     * @param  audioMapFile auidomap.xml location
     *
     * @return AudioMapType with loaded and mapped audiomap.xml
     * @throws XmlParsingException   an exception in case of audiomap.xml parsing error
     * @throws FileNotFoundException if the audioMapXml doesn't define an existing file.
     */
    private AudioMapType loadAudioMapXml(File audioMapFile) throws XmlParsingException, FileNotFoundException {
        if (!audioMapFile.isFile()) {
            throw new FileNotFoundException(String.format("Invalid audiomap.xml file: '%s' not found", audioMapFile.getAbsolutePath()));
        }
        return XmlParser.parse(audioMapFile, new String[]{AUDIOMAP_XML_SCHEME}, AUDIOMAP_PACKAGE, AudioMapType.class);
    }

    private LinkedHashMap<String, Integer> getChannelsForTracks() {
        LinkedHashMap<String, Integer> result = new LinkedHashMap<>();
        SequenceTemplateParameterContext sequenceContext = contextProvider.getSequenceContext();
        for (SequenceUUID uuid : sequenceContext.getUuids(SequenceType.AUDIO)) {
            String channelsNum = sequenceContext.getParameterValue(
                    SequenceContextParameters.CHANNELS_NUM,
                    new ContextInfoBuilder().setSequenceType(SequenceType.AUDIO).setSequenceUuid(uuid).build());
            result.put(uuid.getUuid(), Integer.valueOf(channelsNum));
        }
        return result;
    }

    /**
     * Generates a sample audiomap.xml file that maps all channels of all virtual tracks (sequencedTracks parameter) sequentially 1:1
     * for the number of audio tracks as defined by the audio layout specified in metadata.xml.
     * If the number of input channels is less than required number of audio tracks, the remaining audio tracks are filled with silence.
     *
     * @return an audio map
     */
    private AudioMapType generateDefaultXml() {
        AudioMapType newAudioMap = new AudioMapType();

        Integer ebuAudioTracks = getEBUAudioTracks();
        final int[] currentAudioTrack = {1};
        channelsForTracks.forEach((String trackId, Integer trackChannelCount) -> {
            for (int i = 0; i < trackChannelCount; i++) {
                if (currentAudioTrack[0] <= ebuAudioTracks) {
                    EBUTrackType ebuTrack = new EBUTrackType();
                    ebuTrack.setNumber(currentAudioTrack[0]);
                    ebuTrack.setCPLVirtualTrackId(trackId);
                    ebuTrack.setCPLVirtualTrackChannel(i + 1);
                    newAudioMap.getEBUTrack().add(ebuTrack);
                    currentAudioTrack[0]++;
                }
            }
        });

        while (currentAudioTrack[0] <= ebuAudioTracks) {
            EBUTrackType ebuTrack = new EBUTrackType();
            ebuTrack.setNumber(currentAudioTrack[0]);
            newAudioMap.getEBUTrack().add(ebuTrack);
            currentAudioTrack[0]++;
        }

        return newAudioMap;
    }

    /**
     * Gets the loaded AudioMap instances created from a provided audiomap.xml.
     *
     * @return a loaded AudioMap instances created from a provided audiomap.xml
     */
    public AudioMapType getAudioMap() {
        return this.audioMap;
    }

    /**
     * Return string pan parameter like: 4c|c0=c0|c1=c1|c2=0*c0|c3=0*c0.
     *
     * @return pan parameter like "4c|c0=c0|c1=c1|c2=0*c0|c3=0*c0"
     */
    public String getPanParameter() {
        // 2. How many channels we need to map
        Integer channelCountToMap = getChannelCountToMap();

        // 3. Build reverse map of mapped channels.
        Map<Integer, EBUTrackType> mappedChannels = new HashMap<>();
        for (EBUTrackType ebuTrackItem : audioMap.getEBUTrack()) {
            mappedChannels.put(ebuTrackItem.getNumber(), ebuTrackItem);
        }

        //We need build the following ffmpeg parameters
        // ffmpeg -i test_output.wav -i test_output2.wav -filter_complex "[0:a][1:a]amerge,pan=4c|c0=c0|c1=c1|c2=0*c0|
        // c3=0*c0[aout]" -map "[aout]" -acodec pcm_s24le -ar 48000 output_map.wav
        //"[0:a][1:a]" should be built with:
        //      <dynamicParameter name="amergeMap" concat="true">[%{seq.num}:a]</dynamicParameter>
        //"pan" part ("4c|c0=c0|c1=c1|c2=0*c0|c3=0*c0") should be build with this logic

        // 4. Get order of merged virtual track channels before pan filter
        List<String> sequencedTrackChannelNumbers = new ArrayList<>();
        channelsForTracks.forEach((String trackId, Integer trackChannelCount) -> {
            for (int i = 0; i < trackChannelCount; i++) {
                sequencedTrackChannelNumbers.add(getIntermediateKey(trackId, i));
            }
        });

        // 5. Create pan parameter
        StringBuilder panParameter = new StringBuilder();
        panParameter.append(channelCountToMap).append("c");
        for (Integer i = 0; i < channelCountToMap; i++) {
            panParameter.append("|c").append(i).append("=");

            //get sequenced channel number
            EBUTrackType ebuTrack = mappedChannels.get(i + 1);
            if (ebuTrack == null
                    || ebuTrack.getCPLVirtualTrackId() == null
                    || ebuTrack.getCPLVirtualTrackChannel() == null) {
                panParameter.append("0*c0");
                continue;
            }

            int sequencedChannel = sequencedTrackChannelNumbers.indexOf(
                    getIntermediateKey(ebuTrack.getCPLVirtualTrackId(), ebuTrack.getCPLVirtualTrackChannel() - 1));
            if (sequencedChannel == -1) {
                throw new ConversionException(
                        String.format(
                                "Audio Virtual TrackId \"%s\" with channel number \"%d\" was not found in CPL.",
                                ebuTrack.getCPLVirtualTrackId(), ebuTrack.getCPLVirtualTrackChannel()));
            }

            panParameter.append("c").append(sequencedChannel);
        }

        return panParameter.toString();
    }


    /**
     * Returns numbers of tracks required for pointed in metadata.xml Audio Layout.
     *
     * @return the number of audio tracks.
     */
    public int getEBUAudioTracks() {
        //How many channels we should provide for particular EBU option.
        switch (audioLayout) {
            case EBU_R_48_2_A:
            case EBU_R_123_4_B:
            case EBU_R_123_4_C:
                return 4;
            case EBU_R_123_16_C:
            case EBU_R_123_16_D:
            case EBU_R_123_16_F:
                return 16;
            default:
                // Unknown layout
                throw new ConversionException(
                        String.format("metadata.xml defined unknown audio layout as \"%s\".", audioLayout.value()));
        }
    }

    private String getIntermediateKey(String trackId, Integer channelNumber) {
        return trackId + ":" + channelNumber.toString();
    }

    private Integer getChannelCountToMap() {
        //Count mapped audio channels
        Integer mappedChannelCount = audioMap.getEBUTrack().size();

        //How many channels we need to map
        Integer channelCountToMap = getEBUAudioTracks();
        if (mappedChannelCount > channelCountToMap) {
            throw new ConversionException(
                    String.format(
                            "metadata.xml defined audio layout as \"%s\" that has %d tracks. Mapped channel count is greater than 4.",
                            audioLayout.value(),
                            channelCountToMap));
        }
        return channelCountToMap;
    }
}
