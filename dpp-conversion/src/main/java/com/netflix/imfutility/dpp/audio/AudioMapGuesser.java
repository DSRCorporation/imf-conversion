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
package com.netflix.imfutility.dpp.audio;

import com.netflix.imfutility.audio.InvalidAudioChannelAssignmentException;
import com.netflix.imfutility.audio.SoundfieldGroupHelper;
import com.netflix.imfutility.audio.SoundfieldGroupInfo;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.generated.dpp.audiomap.AudioMapType;
import com.netflix.imfutility.generated.dpp.metadata.AudioTrackLayoutDmAs11Type;
import com.netflix.imfutility.util.ImfLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.netflix.imfutility.audio.AudioConstants.STEREO_LAYOUT;
import static com.netflix.imfutility.audio.AudioConstants.SURROUND_5_1_LAYOUT;
import static com.netflix.imfutility.dpp.audio.AudioMapHelper.add51;
import static com.netflix.imfutility.dpp.audio.AudioMapHelper.add51Silence;
import static com.netflix.imfutility.dpp.audio.AudioMapHelper.addStereo;
import static com.netflix.imfutility.dpp.audio.AudioMapHelper.addStereoSilence;
import static com.netflix.imfutility.generated.dpp.metadata.AudioTrackLayoutDmAs11Type.EBU_R_123_16_C;
import static com.netflix.imfutility.generated.dpp.metadata.AudioTrackLayoutDmAs11Type.EBU_R_123_16_D;
import static com.netflix.imfutility.generated.dpp.metadata.AudioTrackLayoutDmAs11Type.EBU_R_123_16_F;
import static com.netflix.imfutility.generated.dpp.metadata.AudioTrackLayoutDmAs11Type.EBU_R_123_4_B;
import static com.netflix.imfutility.generated.dpp.metadata.AudioTrackLayoutDmAs11Type.EBU_R_123_4_C;
import static com.netflix.imfutility.generated.dpp.metadata.AudioTrackLayoutDmAs11Type.EBU_R_48_2_A;

/**
 * Generates an audiomap.xml based on Essence Descriptor and selected audio track layout if possible.
 */
public final class AudioMapGuesser extends SoundfieldGroupHelper {

    private final Logger logger = new ImfLogger(LoggerFactory.getLogger(AudioMapGuesser.class));

    private final AudioTrackLayoutDmAs11Type audioLayout;

    public AudioMapGuesser(TemplateParameterContextProvider contextProvider, AudioTrackLayoutDmAs11Type audioLayout) {
        super(contextProvider);
        this.audioLayout = audioLayout;
    }

    /**
     * Creates an audio map based on the channel layout information in the resource context
     * (this information is obtained from Essence Descriptors).
     * An audio map can be generated only if
     * <ul>
     * <li>All resources within a sequence has channel layout</li>
     * <li>All resources within a sequence has equal channel layout</li>
     * <li>For R48_2A: there are at least one stereo sequence</li>
     * <li>For R123_4B/C: there are one or two stereo sequences</li>
     * <li>For R123_16C: there are one or two 5.1 sequences</li>
     * <li>For R123_16D: there are exactly two 5.1 sequences with different languages (languages must be set)</li>
     * <li>For R123_16F: there are exactly three stereo sequences with different languages (languages must be set)</li>
     * </ul>
     *
     * @return an audio map instance or null if there is not audio.
     * @throws InvalidAudioChannelAssignmentException an exception with description why audio map can not ne generated
     *                                                for the selected track allocation
     */
    public AudioMapType guessAudioMap() throws InvalidAudioChannelAssignmentException {
        if (contextProvider.getSequenceContext().getSequenceCount(SequenceType.AUDIO) == 0) {
            return null;
        }

        logger.debug("Trying to generate an audiomap.xml based on the EssenceDescriptor...");

        prepareContext();

        // create the audio map
        AudioMapType audioMap = createAudioMap();
        logger.info("Generated an audiomap.xml based on the EssenceDescriptor: OK");
        return audioMap;
    }

    private AudioMapType createAudioMap() throws InvalidAudioChannelAssignmentException {
        switch (audioLayout) {
            case EBU_R_48_2_A:
                return processR482A();
            case EBU_R_123_4_B:
            case EBU_R_123_4_C:
                return processR1234B4C();
            case EBU_R_123_16_C:
                return processR12316C();
            case EBU_R_123_16_D:
                return processR12316D();
            case EBU_R_123_16_F:
                return processR12316F();
            default:
                throw new RuntimeException("Unknown audio track allocation: " + audioLayout.toString());
        }
    }

    private AudioMapType processR482A() throws InvalidAudioChannelAssignmentException {
        AudioMapType audioMap = new AudioMapType();

        List<SoundfieldGroupInfo> foundStereo = findInputForChannelGroup(STEREO_LAYOUT);

        // use the first found stereo pair
        if (foundStereo.isEmpty()) {
            throw new InvalidAudioChannelAssignmentException(
                    String.format("Expected at least one stereo for %s", EBU_R_48_2_A.value()));
        }

        addStereo(audioMap, 1, foundStereo.get(0));
        addStereoSilence(audioMap, 3);

        return audioMap;
    }

    private AudioMapType processR1234B4C() throws InvalidAudioChannelAssignmentException {
        AudioMapType audioMap = new AudioMapType();

        List<SoundfieldGroupInfo> foundStereo = findInputForChannelGroup(STEREO_LAYOUT);

        // we expect one or two stereo
        if (foundStereo.isEmpty() || foundStereo.size() > 2) {
            throw new InvalidAudioChannelAssignmentException(
                    String.format("Expected one or two stereo for %s and %s", EBU_R_123_4_B.value(), EBU_R_123_4_C.value()));
        }

        addStereo(audioMap, 1, foundStereo.get(0)); // final stereo
        if (foundStereo.size() == 2) {
            addStereo(audioMap, 3, foundStereo.get(1)); // M&E stereo
        } else {
            addStereoSilence(audioMap, 3);
        }

        return audioMap;
    }

    private AudioMapType processR12316C() throws InvalidAudioChannelAssignmentException {
        AudioMapType audioMap = new AudioMapType();

        List<SoundfieldGroupInfo> foundStereo = findInputForChannelGroup(STEREO_LAYOUT);
        List<SoundfieldGroupInfo> found51 = findInputForChannelGroup(SURROUND_5_1_LAYOUT);

        // we expect one or two stereo and one 5.1
        if (foundStereo.isEmpty() || foundStereo.size() > 2) {
            throw new InvalidAudioChannelAssignmentException(
                    String.format("Expected one or two stereo for %s", EBU_R_123_16_C.value()));
        }
        if (found51.isEmpty() || found51.size() > 2) {
            throw new InvalidAudioChannelAssignmentException(
                    String.format("Expected one or two 5.1 for %s", EBU_R_123_16_C.value()));
        }

        addStereo(audioMap, 1, foundStereo.get(0)); // final stereo
        if (foundStereo.size() == 2 && found51.size() == 2) {
            addStereo(audioMap, 3, foundStereo.get(1)); // M&E stereo
        } else {
            addStereoSilence(audioMap, 3);
        }

        add51(audioMap, 5, found51.get(0)); // final 51
        if (foundStereo.size() == 2 && found51.size() == 2) {
            add51(audioMap, 11, found51.get(1)); // M&E 5.1
        } else {
            add51Silence(audioMap, 11);
        }

        return audioMap;
    }

    private AudioMapType processR12316D() throws InvalidAudioChannelAssignmentException {
        AudioMapType audioMap = new AudioMapType();

        List<SoundfieldGroupInfo> found51 = findInputForChannelGroup(SURROUND_5_1_LAYOUT);

        // we expect exactly two 5.1 with different languages
        if (found51.size() != 2) {
            throw new InvalidAudioChannelAssignmentException(
                    String.format("Expected exactly two 5.1 for %s", EBU_R_123_16_D.value()));
        }
        // get languages (all sequences from a soundfield group must have the same language):
        SoundfieldGroupInfo first51 = found51.get(0);
        SoundfieldGroupInfo second51 = found51.get(1);
        String firstLanguage = getLanguage(first51);
        String secondLanguage = getLanguage(second51);
        // languages must be different
        if (firstLanguage == null || secondLanguage == null || firstLanguage.equals(secondLanguage)) {
            throw new InvalidAudioChannelAssignmentException(
                    String.format("Expected two 5.1 with different languages for %s", EBU_R_123_16_D.value()));
        }

        add51(audioMap, 1, first51); // 5.1 first language
        addStereoSilence(audioMap, 7);
        add51(audioMap, 9, second51); // 5.1 second language
        addStereoSilence(audioMap, 15);

        return audioMap;
    }

    private AudioMapType processR12316F() throws InvalidAudioChannelAssignmentException {
        AudioMapType audioMap = new AudioMapType();

        List<SoundfieldGroupInfo> foundStereo = findInputForChannelGroup(STEREO_LAYOUT);

        // we expect exactly three stereo with different languages
        if (foundStereo.size() != 3) {
            throw new InvalidAudioChannelAssignmentException(
                    String.format("Expected exactly three stereo for %s", EBU_R_123_16_F.value()));
        }
        // get languages (all sequences from a soundfield group must have the same language):
        SoundfieldGroupInfo stereo1 = foundStereo.get(0);
        SoundfieldGroupInfo stereo2 = foundStereo.get(1);
        SoundfieldGroupInfo stereo3 = foundStereo.get(2);
        String lang1 = getLanguage(stereo1);
        String lang2 = getLanguage(stereo2);
        String lang3 = getLanguage(stereo3);
        // languages must be different
        if (lang1 == null || lang2 == null || lang3 == null
                || lang1.equals(lang2) || lang2.equals(lang3) || lang1.equals(lang3)) {
            throw new InvalidAudioChannelAssignmentException(
                    String.format("Expected three stereo with different languages for %s", EBU_R_123_16_F.value()));
        }

        addStereo(audioMap, 1, stereo1); // stereo first language
        addStereoSilence(audioMap, 3);
        addStereo(audioMap, 5, stereo2); // stereo second language
        addStereoSilence(audioMap, 7);
        addStereo(audioMap, 9, stereo3); // stereo third language
        addStereoSilence(audioMap, 11);
        addStereoSilence(audioMap, 13);
        addStereoSilence(audioMap, 15);

        return audioMap;
    }

}
