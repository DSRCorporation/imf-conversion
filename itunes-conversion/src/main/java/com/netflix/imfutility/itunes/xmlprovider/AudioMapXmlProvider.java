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
package com.netflix.imfutility.itunes.xmlprovider;

import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.generated.itunes.audiomap.AudioMapType;
import com.netflix.imfutility.generated.itunes.audiomap.ChannelType;
import com.netflix.imfutility.generated.itunes.audiomap.MainAudioType;
import com.netflix.imfutility.generated.itunes.audiomap.Option1AType;
import static com.netflix.imfutility.util.FFmpegAudioChannels.FC;
import static com.netflix.imfutility.util.FFmpegAudioChannels.FL;
import static com.netflix.imfutility.util.FFmpegAudioChannels.FR;
import static com.netflix.imfutility.util.FFmpegAudioChannels.LFE;
import static com.netflix.imfutility.util.FFmpegAudioChannels.SL;
import static com.netflix.imfutility.util.FFmpegAudioChannels.SR;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic functionality for audiomap.xml handling.
 */
public final class AudioMapXmlProvider {

    private void verifyOption1A(Option1AType opt1A) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private class AudioOption extends ArrayList<LinkedHashMap<String, ChannelType>> {
    }

    private static final String DYNAMIC_PAN_PARAMETER_PREFIX = "panParameter";
    private static final String DYNAMIC_AUDIOMAP_FILE = "itunesAudioMap";

    private static final String AUDIOMAP_DEFAUL_FILE = "audiomap.xml";
    private static final String AUDIOMAP_XML_SCHEME = "xsd/audiomap/audiomap.xsd";
    private static final String AUDIOMAP_PACKAGE = "com.netflix.imfutility.generated.itunes.audiomap";




    private final Logger logger = LoggerFactory.getLogger(AudioMapXmlProvider.class);

    private final TemplateParameterContextProvider contextProvider;
    private final File audioMapFile;

    private final AudioOption mainAudio;
    private final ArrayList<AudioOption> alternativeAudios;

    /**
     * Generates a sample audiomap.xml file.
     *
     * @param path a path to the output audiomap.xml file
     */
    public static void generateSampleXml(String path) {
    }

    public AudioMapXmlProvider(File audioMapFile, TemplateParameterContextProvider contextProvider)
            throws FileNotFoundException, XmlParsingException {

        AudioMapType audioMap;

        if (audioMapFile == null) {
            logger.warn(
                    "No audiomap.xml specified as a command line argument. A default audiomap.xml will be generated.");

            audioMapFile = generateDerfaultAudioMapXml();

            // add as dynamic parameter to delete at the end.
            contextProvider.getDynamicContext().addParameter(DYNAMIC_AUDIOMAP_FILE,
                    audioMapFile.getAbsolutePath(), true);
        }

        this.contextProvider = contextProvider;
        this.audioMapFile = audioMapFile;

        audioMap = loadAudioMapXml();
        mainAudio = getMainAudio(audioMap);
        alternativeAudios = getAlternativeAudios(audioMap);
    }

    public Map<String, String> getPanParameters() {
        Map<String, String> panParams = new HashMap<>();

        // TODO: add implementation
        return panParams;
    }

    /**
     * Loads and validates audiomap.xml.
     *
     * @return AudioMapType with loaded and mapped audiomap.xml
     * @throws XmlParsingException   an exception in case of audiomap.xml parsing error
     * @throws FileNotFoundException if the audioMapXml doesn't define an existing file.
     */
    private AudioMapType loadAudioMapXml() throws XmlParsingException, FileNotFoundException {

        if (!audioMapFile.isFile()) {
            throw new FileNotFoundException(
                    String.format("Invalid audiomap.xml file: '%s' not found", audioMapFile.getAbsolutePath()));
        }

        return XmlParser.parse(audioMapFile, new String[]{AUDIOMAP_XML_SCHEME}, AUDIOMAP_PACKAGE, AudioMapType.class);
    }

    private File generateDerfaultAudioMapXml() {
        //  TODO: implement audiomap generation
        return null;
    }

    private AudioOption getMainAudio(AudioMapType audioMap) {
        AudioOption option = new AudioOption();
        MainAudioType mainAudioType = audioMap.getMainAudio();

        if (mainAudioType.getOption1A() != null) {
            Option1AType opt1A = mainAudioType.getOption1A();
            verifyOption1A(opt1A);

            option.add(Stream.of(
                    new SimpleEntry<>(FL.name(), opt1A.getTrack1().getL()),
                    new SimpleEntry<>(FR.name(), opt1A.getTrack1().getR()),
                    new SimpleEntry<>(FC.name(), opt1A.getTrack1().getC()),
                    new SimpleEntry<>(LFE.name(), opt1A.getTrack1().getLFE()),
                    new SimpleEntry<>(SL.name(), opt1A.getTrack1().getLs()),
                    new SimpleEntry<>(SR.name(), opt1A.getTrack1().getRs())
            ).collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue(),
                    (u, v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); },
                    LinkedHashMap::new)));

        } else if (mainAudioType.getOption1B() != null) {
        } else if (mainAudioType.getOption1C() != null) {
        } else if (mainAudioType.getOption1D() != null) {
        } else if (mainAudioType.getOption2() != null) {
        } else if (mainAudioType.getOption3() != null) {
        } else if (mainAudioType.getOption4() != null) {
        } else if (mainAudioType.getOption5() != null) {
        } else if (mainAudioType.getOption6() != null) {
        }

        return option;
    }

    private ArrayList<AudioOption> getAlternativeAudios(AudioMapType audioMap) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
