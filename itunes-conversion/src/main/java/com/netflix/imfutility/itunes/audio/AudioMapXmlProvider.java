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
import com.netflix.imfutility.conversion.templateParameter.ContextInfoBuilder;
import com.netflix.imfutility.conversion.templateParameter.context.SequenceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.generated.itunes.audiomap.AlternativeAudioType;
import com.netflix.imfutility.generated.itunes.audiomap.AudioMapType;
import com.netflix.imfutility.generated.itunes.audiomap.ChannelType;
import com.netflix.imfutility.generated.itunes.audiomap.MainAudioType;
import com.netflix.imfutility.generated.itunes.audiomap.Option1AType;
import com.netflix.imfutility.generated.itunes.audiomap.Option2Type;
import com.netflix.imfutility.generated.itunes.audiomap.Option3Type;
import com.netflix.imfutility.generated.itunes.audiomap.Option4Type;
import com.netflix.imfutility.generated.itunes.audiomap.Option5Type;
import com.netflix.imfutility.generated.itunes.audiomap.Option6Type;
import com.netflix.imfutility.itunes.audio.ChannelsMapper.LayoutType;
import com.netflix.imfutility.itunes.locale.LocaleHelper;
import com.netflix.imfutility.itunes.xmlprovider.LocalizedXmlProvider;
import com.netflix.imfutility.util.StreamUtil;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.netflix.imfutility.itunes.ITunesConversionConstants.DEFAULT_LOCALE;
import static com.netflix.imfutility.itunes.ITunesConversionXsdConstants.AUDIOMAP_PACKAGE;
import static com.netflix.imfutility.itunes.ITunesConversionXsdConstants.AUDIOMAP_XML_SCHEME;
import static com.netflix.imfutility.util.FFmpegAudioChannels.FC;
import static com.netflix.imfutility.util.FFmpegAudioChannels.FL;
import static com.netflix.imfutility.util.FFmpegAudioChannels.FR;
import static com.netflix.imfutility.util.FFmpegAudioChannels.LFE;
import static com.netflix.imfutility.util.FFmpegAudioChannels.SL;
import static com.netflix.imfutility.util.FFmpegAudioChannels.SR;

/**
 * Basic functionality for audiomap.xml handling.
 */
public class AudioMapXmlProvider implements LocalizedXmlProvider {

    /**
     * Internal. Describes AudioOption of iTunes audio asset configuration.
     */
    public static class AudioOption extends ArrayList<LinkedHashMap<String, ChannelType>> {
        private String fileName = null;
        private String locale = null;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getLocale() {
            return locale;
        }

        public void setLocale(String locale) {
            this.locale = locale;
        }
    }

    /**
     * Audio channel map: channelName -> CPL Virtual Track mapping.
     */
    private class Channel extends SimpleEntry<String, ChannelType> {
        public Channel(String k, ChannelType v) {
            super(k, v);
        }
    }

    private static final String INTERMEDIATE_KEY_SEPARATOR = ":";
    /**
     * Logger.
     */
    private final Logger logger = LoggerFactory.getLogger(AudioMapXmlProvider.class);
    private final boolean customized;
    /**
     * Context provider.
     */
    private final TemplateParameterContextProvider contextProvider;
    /**
     * Channel mapper used for default channel mapping (by order) or guessing channels layout by EssenceDescriptor.
     */
    private final ChannelsMapper channelMapper;
    /**
     * Parsed AudioMapType from XML.
     */
    private final AudioMapType audioMap;
    /**
     * CPL virtual tracks to channels number map.
     */
    private final LinkedHashMap<String, Integer> virtualTracksChannels;
    /**
     * Contains intermediate flat indexes map of sequence uuid and sequence channels.
     * <p></p>
     * For example: 2 sequences with uuid1 channels 4 and uuid2 channels 2 are converted to
     * ["uuid1:1", "uuid1:2", uuid1:3, uuid1:4, "uuid2:1", "uuid2:2"] and indexOf of this array will specify correct
     * channel number of "amerge" channel order.
     */
    private final ArrayList<String> sequencedTrackChannelNumbers;
    /**
     * Main audio configuration.
     */
    private AudioOption mainAudio;
    /**
     * Additional audio configurations if exist.
     */
    private ArrayList<AudioOption> alternativesAudio;

    /**
     * Constructor with default generated AudioMap on default language.
     *
     * @param contextProvider context provider
     * @throws FileNotFoundException if audioMapFile not found
     * @throws XmlParsingException   if audiomap.xml is not validated by schema
     */
    public AudioMapXmlProvider(TemplateParameterContextProvider contextProvider)
            throws FileNotFoundException, XmlParsingException {
        this(contextProvider, DEFAULT_LOCALE);
    }

    /**
     * Constructor with default generated AudioMap.
     *
     * @param contextProvider context provider
     * @param locale          locale of generated main audio
     * @throws FileNotFoundException if audioMapFile not found
     * @throws XmlParsingException   if audiomap.xml is not validated by schema
     */
    public AudioMapXmlProvider(TemplateParameterContextProvider contextProvider, String locale)
            throws FileNotFoundException, XmlParsingException {
        this(null, contextProvider, locale);
    }

    /**
     * Constructor with default generated AudioMap.
     *
     * @param audiomapFile    audio map file
     * @param contextProvider context provider
     * @throws FileNotFoundException if audioMapFile not found
     * @throws XmlParsingException   if audiomap.xml is not validated by schema
     */
    public AudioMapXmlProvider(File audiomapFile, TemplateParameterContextProvider contextProvider)
            throws FileNotFoundException, XmlParsingException {
        this(audiomapFile, contextProvider, DEFAULT_LOCALE);
    }

    /**
     * Constructor.
     *
     * @param audiomapFile    audio map file
     * @param contextProvider context provider
     * @param locale          locale of generated main audio
     * @throws FileNotFoundException if audioMapFile not found
     * @throws XmlParsingException   if audiomap.xml is not validated by schema
     */
    public AudioMapXmlProvider(File audiomapFile, TemplateParameterContextProvider contextProvider, String locale)
            throws FileNotFoundException, XmlParsingException {

        this.customized = audiomapFile != null;
        this.contextProvider = contextProvider;

        this.virtualTracksChannels = getVirtualTrackChannels();
        this.sequencedTrackChannelNumbers = getSequencedTrackChannelNumbers(this.virtualTracksChannels);

        this.channelMapper = new ChannelsMapper(contextProvider);

        this.audioMap = customized ? loadAudioMapXml(audiomapFile) : generateDefaultAudioMap(locale);

        if (customized) {
            logger.info("AudioMap XML has been parsed successfully.");
        } else {
            logger.warn("No audiomap.xml specified as a command line argument. A default AudioMap was generated.");
        }
    }

    /**
     * Creates audio track.
     *
     * @param channels track channels
     * @return audio track
     */
    private static LinkedHashMap<String, ChannelType> createTrack(Channel... channels) {
        return StreamUtil.createLinkedMap(channels);
    }

    /**
     * Initialize main and alternative audios.
     */
    public void initAudio() {
        channelMapper.mapChannels(getMapperLayoutOptions());

        this.mainAudio = getMainAudio(this.audioMap);
        this.alternativesAudio = getAlternativeAudios(this.audioMap);
    }

    @Override
    public Locale getLocale() {
        return LocaleHelper.fromITunesLocale(audioMap.getMainAudio().getLocale());
    }

    @Override
    public void setLocale(Locale locale) {
        audioMap.getMainAudio().setLocale(LocaleHelper.toITunesLocale(locale));
    }

    public boolean isCustomized() {
        return customized;
    }

    /**
     * Gets main audio file name.
     *
     * @return main audio file name
     */
    public String getMainAudioFileName() {
        return mainAudio.getFileName();
    }

    /**
     * Gets count of main audio tracks/streams.
     *
     * @return count of main audio tracks/streams
     */
    public int getMainAudioTracks() {
        return mainAudio.size();
    }

    /**
     * Gets additional audio count.
     *
     * @return additional audio count
     */
    public int getAdditionalAudioCount() {
        return alternativesAudio.size();
    }

    /**
     * Gets array of additional audio tracks numbers by order.
     *
     * @return array of additional audio tracks numbers by order
     */
    public ArrayList<Integer> getAdditionalAudioTracks() {
        return alternativesAudio.stream()
                .map(AudioOption::size)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Gets array of additional audio file names by order.
     *
     * @return array of additional audio file names by order
     */
    public ArrayList<String> getAdditionalAudioFileNames() {
        return alternativesAudio.stream()
                .map(AudioOption::getFileName)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Gets array of conversion pan parameters by order.
     *
     * @return array of conversion pan parameters by order
     */
    public ArrayList<String> getPanParameters() {
        ArrayList<String> panParams = new ArrayList<>();

        // create main audio pan parameters
        mainAudio.forEach((t) -> {
            panParams.add(getPanParameter(t));
        });

        alternativesAudio.stream().flatMap(AudioOption::stream).forEach((t) -> {
            panParams.add(getPanParameter(t));
        });

        return panParams;
    }

    /**
     * Gets parsed audio map for test purposes.
     *
     * @return parsed audio map for test purposes
     */
    AudioMapType getAudioMap() {
        return audioMap;
    }

    /**
     * Gets main audio for test purposes.
     *
     * @return main audio for test purposes
     */
    AudioOption getMainAudio() {
        return mainAudio;
    }

    /**
     * Gets alternatives audio for test purposes.
     *
     * @return alternatives audio for test purposes
     */
    public ArrayList<AudioOption> getAlternativesAudio() {
        return alternativesAudio;
    }

    /**
     * Gets pan parameter for track.
     * <p></p>
     * Example: pan=4c|c0=c0|c1=c1|c2=c2|c3=c3,aformat=channel_layouts=FL+FR+FC+LFE
     *
     * @param track audio track
     * @return pan parameter for track
     */
    private String getPanParameter(LinkedHashMap<String, ChannelType> track) {
        int[] i = {0};
        StringJoiner aformat = new StringJoiner("+", ",aformat=channel_layouts=", ""); // ,aformat=channel_layouts=
        StringBuilder panParameter = new StringBuilder("pan="); // pan=
        panParameter.append(track.size()).append("c"); // pan=4c
        track.forEach((chName, channel) -> {
            panParameter.append("|c").append(i[0]).append("="); // pan=4c|c0=

            // gets channel number in amerge channels sequence
            int sequencedChannel = sequencedTrackChannelNumbers.indexOf(
                    getIntermediateKey(channel.getCPLVirtualTrackId(), channel.getCPLVirtualTrackChannel() - 1));
            if (sequencedChannel == -1) {
                throw new ConversionException(
                        String.format(
                                "Audio Virtual TrackId \"%s\" with channel number \"%d\" was not found in CPL.",
                                channel.getCPLVirtualTrackId(), channel.getCPLVirtualTrackChannel()));
            }
            panParameter.append("c").append(sequencedChannel); // pan=4c|c0=c0

            aformat.add(chName); // ,aformat=channel_layouts=FL

            i[0]++;
        });
        panParameter.append(aformat); // pan=4c|c0=c0|c1=c1|c2=c2|c3=c3,aformat=channel_layouts=FL+FR+FC+LFE

        return panParameter.toString();
    }

    /**
     * Gets map of CPL virtual track UUID to virtual track channels number by order.
     *
     * @return map of CPL virtual track UUID to virtual track channels number by order
     */
    private LinkedHashMap<String, Integer> getVirtualTrackChannels() {
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
     * Gets flat map of uuid + channel of track for each tracks and uuids: [uuid:cn1, uuid:cn2, ....].
     *
     * @param virtualTracksChannels uuid to channels map
     * @return flat map of uuid + channel of track for each tracks and uuids: [uuid:cn1, uuid:cn2, ....]
     */
    private ArrayList<String> getSequencedTrackChannelNumbers(LinkedHashMap<String, Integer> virtualTracksChannels) {
        ArrayList<String> res = new ArrayList<>();
        virtualTracksChannels.forEach((String trackId, Integer trackChannelCount) -> {
            for (int i = 0; i < trackChannelCount; i++) {
                res.add(getIntermediateKey(trackId, i));
            }
        });

        return res;
    }

    /**
     * Gets intermediate key from track id and channel number -> trackId:channelNumber.
     *
     * @param trackId       track id
     * @param channelNumber channel number
     * @return intermediate key from track id and channel number -> trackId:channelNumber
     */
    private String getIntermediateKey(String trackId, Integer channelNumber) {
        return trackId + INTERMEDIATE_KEY_SEPARATOR + channelNumber.toString();
    }

    private SimpleEntry<String, Integer> splitIntermediateKey(String key) {
        String uuid = key.substring(0, key.lastIndexOf(INTERMEDIATE_KEY_SEPARATOR));
        String channelNumber = key.substring(key.lastIndexOf(INTERMEDIATE_KEY_SEPARATOR) + 1);

        return new SimpleEntry<>(uuid, Integer.valueOf(channelNumber));
    }

    /**
     * Loads and validates audiomap.xml.
     *
     * @return AudioMapType with loaded and mapped audiomap.xml
     * @throws XmlParsingException   an exception in case of audiomap.xml parsing error
     * @throws FileNotFoundException if the audioMapXml doesn't define an existing file.
     */
    private AudioMapType loadAudioMapXml(File audioMapFile) throws XmlParsingException, FileNotFoundException {

        if (!audioMapFile.isFile()) {
            throw new FileNotFoundException(
                    String.format("Invalid audiomap.xml file: '%s' not found", audioMapFile.getAbsolutePath()));
        }

        return XmlParser.parse(audioMapFile, new String[]{AUDIOMAP_XML_SCHEME}, AUDIOMAP_PACKAGE, AudioMapType.class);
    }

    /**
     * Generates a default audiomap basis on information of CPL virtual tracks.
     *
     * @return audiomap.
     */
    private AudioMapType generateDefaultAudioMap(String locale) {
        AudioMapType audioMap = new AudioMapType();
        audioMap.setMainAudio(new MainAudioType());
        audioMap.getMainAudio().setLocale(locale);
        audioMap.getMainAudio().setName("main-audio.mov");
        if (!fillDefaultAudioMapFromDescriptor(audioMap)) {
            setAudioMapDefaultOptions(audioMap);
        }
        return audioMap;
    }

    private boolean fillDefaultAudioMapFromDescriptor(AudioMapType audioMap) {
        String mainLang = audioMap.getMainAudio().getLocale();
        List<Pair<SequenceUUID, Integer>> mainChannels = channelMapper.guessMainAudio(mainLang);

        if (mainChannels.isEmpty()) {
            return false;
        }

        if (mainChannels.size() == 8) {
            audioMap.getMainAudio().setOption3(createXmlOption3(mainChannels));
        } else if (mainChannels.size() == 2) {
            audioMap.getMainAudio().setOption6(createXmlOption6(mainChannels));
        } else {
            return false;
        }

        channelMapper.guessAlternatives(mainLang).entrySet().forEach(entry -> {
            AlternativeAudioType audio = new AlternativeAudioType();
            audio.setLocale(entry.getKey());
            audio.setName(String.format("audio_%s.mov", entry.getKey().replace("-", "_").toUpperCase()));

            audio.setOption6(createXmlOption6(entry.getValue()));

            audioMap.getAlternativeAudio().add(audio);
        });

        return true;
    }

    private void setAudioMapDefaultOptions(AudioMapType audioMap) {
        if (sequencedTrackChannelNumbers.size() < 6) {
            audioMap.getMainAudio().setOption6(new Option6Type());
        } else {
            audioMap.getMainAudio().setOption3(new Option3Type());
        }
    }

    /**
     * Gets mainAudio as AudioOption from parsed XML type.
     *
     * @param audioMap parsed audio map
     * @return mainAudio as AudioOption from parsed XML type
     */
    private AudioOption getMainAudio(AudioMapType audioMap) {
        AudioOption option;
        Object xmlOpt = getXmlMainAudioOption(audioMap.getMainAudio());

        verifyOption(xmlOpt);

        option = audioOptionFromXmlOption(xmlOpt, audioMap.getMainAudio().getLocale(), true);
        option.setFileName(audioMap.getMainAudio().getName());
        option.setLocale(audioMap.getMainAudio().getLocale());

        return option;
    }

    /**
     * Gets array of additional audio from parsed XML type.
     *
     * @param audioMap parsed audio map
     * @return array of additional audio from parsed XML type
     */
    private ArrayList<AudioOption> getAlternativeAudios(AudioMapType audioMap) {
        ArrayList<AudioOption> options = new ArrayList<>();

        if (audioMap.getAlternativeAudio() == null) {
            return options;
        }

        audioMap.getAlternativeAudio().forEach((altAudio) -> {
            AudioOption option;
            Object xmlOpt = getXmlAlternativeAudioOption(altAudio);

            verifyOption(xmlOpt);

            option = audioOptionFromXmlOption(xmlOpt, altAudio.getLocale(), false);
            if (option != null) {
                option.setFileName(altAudio.getName());
                option.setLocale(altAudio.getLocale());

                options.add(option);
            }
        });

        return options;
    }

    /**
     * Gets mapper options that contains expected layout for all audiomap opts, which aren't linked to related track explicitly.
     *
     * @return list of all options that aren't linked to related tracks
     */
    private List<Pair<LayoutType, String>> getMapperLayoutOptions() {
        List<Pair<LayoutType, String>> options = new ArrayList<>();

        MainAudioType mainAudio = audioMap.getMainAudio();
        Object mainOption = getXmlMainAudioOption(mainAudio);

        verifyOption(mainOption);

        if (isOptionEmpty(mainOption)) {
            options.add(getMapperLayoutOption(mainOption, mainAudio.getLocale()));
        }

        audioMap.getAlternativeAudio().forEach(altAudio -> {
            Object altOption = getXmlAlternativeAudioOption(altAudio);

            verifyOption(altOption);

            if (isOptionEmpty(altOption)) {
                options.add(getMapperLayoutOption(altOption, altAudio.getLocale()));
            }
        });
        return options;
    }

    /**
     * Gets option that defines expected audio layout and language.
     *
     * @param xmlOpt xml option type
     * @param lang   expected language of audio
     * @return {@link Pair} of layout type and language
     */
    private Pair<LayoutType, String> getMapperLayoutOption(Object xmlOpt, String lang) {
        LayoutType layout = null;
        if (xmlOpt instanceof Option1AType
                || xmlOpt instanceof Option2Type
                || xmlOpt instanceof Option3Type
                || xmlOpt instanceof Option4Type) {
            layout = LayoutType.SURROUND;
        } else if (xmlOpt instanceof Option5Type
                || xmlOpt instanceof Option6Type) {
            layout = LayoutType.STEREO;
        }

        return Pair.of(layout, lang);
    }

    /**
     * Gets existing main audio XML option as object.
     *
     * @param mainAudio main audio
     * @return existing main audio XML option as object
     */
    private Object getXmlMainAudioOption(MainAudioType mainAudio) {
        Object opt = null;

        if (mainAudio.getOption1A() != null) {
            opt = mainAudio.getOption1A();
        } else if (mainAudio.getOption2() != null) {
            opt = mainAudio.getOption2();
        } else if (mainAudio.getOption3() != null) {
            opt = mainAudio.getOption3();
        } else if (mainAudio.getOption4() != null) {
            opt = mainAudio.getOption4();
        } else if (mainAudio.getOption5() != null) {
            opt = mainAudio.getOption5();
        } else if (mainAudio.getOption6() != null) {
            opt = mainAudio.getOption6();
        } else {
            // nothing
        }

        return opt;
    }

    /**
     * Gets existing alternative audio XML option as object.
     *
     * @param altAudio alternative audio
     * @return existing alternative audio XML option as object
     */
    private Object getXmlAlternativeAudioOption(AlternativeAudioType altAudio) {
        Object opt = null;

        if (altAudio.getOption5() != null) {
            opt = altAudio.getOption5();
        } else if (altAudio.getOption6() != null) {
            opt = altAudio.getOption6();
        } else {
            // nothing
        }

        return opt;
    }

    /**
     * Create AudioOption from XML option type.
     *
     * @param xmlOpt xml option type
     * @return AudioOption
     */
    private AudioOption audioOptionFromXmlOption(Object xmlOpt, String lang, boolean isMainAudio) {
        return (isOptionEmpty(xmlOpt)) ? generateAudioOption(xmlOpt, lang, isMainAudio) : createAudioOption(xmlOpt);
    }

    /**
     * Verify OptionXXX and throw ConversionException on error.
     *
     * @param opt parsed Option
     */
    private void verifyOption(Object opt) {
        if (!isOptionEmpty(opt) && !hasOptionAllTracks(opt)) {
            throw new ConversionException("Audio Option should contain all tracks or be empty");
        }
    }

    /**
     * Generates AudioOption when XML option type is empty.
     *
     * @param xmlOpt      xml option
     * @param lang        expected language of audio
     * @param isMainAudio defines whether option relates to main audio or alternative
     * @return AudioOption
     */
    private AudioOption generateAudioOption(Object xmlOpt, String lang, boolean isMainAudio) {
        AudioOption option = null;

        ArrayList<ChannelType> channels = channelMapper.getChannels(getMapperLayoutOption(xmlOpt, lang)).stream()
                .map(pair -> {
                    ChannelType channel = new ChannelType();
                    channel.setCPLVirtualTrackId(pair.getLeft().getUuid());
                    channel.setCPLVirtualTrackChannel(pair.getRight());
                    return channel;
                })
                .collect(Collectors.toCollection(ArrayList::new));

        if (channels.isEmpty()) {
            if (isMainAudio) {
                throw new ConversionException("Main audio can't be processed. "
                        + "Please check CPL or audiomap.xml, it may contain inappropriate layout or essence descriptors.");
            } else {
                logger.warn("Alternative audio for {} locale can't be processed.", lang);
            }
            return null;
        }

        if (xmlOpt instanceof Option1AType) {
            option = createOption1A(channels.toArray(new ChannelType[0]));
        } else if (xmlOpt instanceof Option2Type) {
            option = createOption2(channels.toArray(new ChannelType[0]));
        } else if (xmlOpt instanceof Option3Type) {
            option = createOption3(channels.toArray(new ChannelType[0]));
        } else if (xmlOpt instanceof Option4Type) {
            option = createOption4(channels.toArray(new ChannelType[0]));
        } else if (xmlOpt instanceof Option5Type) {
            option = createOption5(channels.toArray(new ChannelType[0]));
        } else if (xmlOpt instanceof Option6Type) {
            option = createOption6(channels.toArray(new ChannelType[0]));
        } else {
            // nothing
        }

        return option;
    }


    private Option3Type createXmlOption3(List<Pair<SequenceUUID, Integer>> channels) {
        Option3Type option3 = new Option3Type();
        option3.setTrack1(new Option3Type.Track1());
        option3.setTrack2(new Option3Type.Track2());

        option3.getTrack1().setL(createXmlChannel(channels.get(0)));
        option3.getTrack1().setR(createXmlChannel(channels.get(1)));
        option3.getTrack1().setC(createXmlChannel(channels.get(2)));
        option3.getTrack1().setLFE(createXmlChannel(channels.get(3)));
        option3.getTrack1().setLs(createXmlChannel(channels.get(4)));
        option3.getTrack1().setRs(createXmlChannel(channels.get(5)));

        option3.getTrack2().setLt(createXmlChannel(channels.get(6)));
        option3.getTrack2().setRt(createXmlChannel(channels.get(7)));

        return option3;
    }

    private Option6Type createXmlOption6(List<Pair<SequenceUUID, Integer>> channels) {
        Option6Type option6 = new Option6Type();
        option6.setTrack1(new Option6Type.Track1());

        option6.getTrack1().setL(createXmlChannel(channels.get(0)));
        option6.getTrack1().setR(createXmlChannel(channels.get(1)));

        return option6;
    }

    /**
     * Create xml channel info form input pair.
     *
     * @param pair
     * @return xml ChannelType object
     */
    private ChannelType createXmlChannel(Pair<SequenceUUID, Integer> pair) {
        ChannelType channel = new ChannelType();
        channel.setCPLVirtualTrackId(pair.getLeft().getUuid());
        channel.setCPLVirtualTrackChannel(pair.getRight());
        return channel;
    }

    /**
     * Creates AudioOption from XML option type.
     *
     * @param xmlOpt xml option type
     * @return AudioOption
     */
    private AudioOption createAudioOption(Object xmlOpt) {
        AudioOption option = null;

        if (xmlOpt instanceof Option1AType) {
            Option1AType opt1A = (Option1AType) xmlOpt;

            option = createOption1A(
                    opt1A.getTrack1().getL(),
                    opt1A.getTrack1().getR(),
                    opt1A.getTrack1().getC(),
                    opt1A.getTrack1().getLFE(),
                    opt1A.getTrack1().getLs(),
                    opt1A.getTrack1().getRs(),
                    opt1A.getTrack2().getLt(),
                    opt1A.getTrack3().getRt());
        } else if (xmlOpt instanceof Option2Type) {
            Option2Type opt2 = (Option2Type) xmlOpt;

            option = createOption2(
                    opt2.getTrack1().getL(),
                    opt2.getTrack2().getR(),
                    opt2.getTrack3().getC(),
                    opt2.getTrack4().getLFE(),
                    opt2.getTrack5().getLs(),
                    opt2.getTrack6().getRs(),
                    opt2.getTrack7().getLt(),
                    opt2.getTrack8().getRt());
        } else if (xmlOpt instanceof Option3Type) {
            Option3Type opt3 = (Option3Type) xmlOpt;

            option = createOption3(
                    opt3.getTrack1().getL(),
                    opt3.getTrack1().getR(),
                    opt3.getTrack1().getC(),
                    opt3.getTrack1().getLFE(),
                    opt3.getTrack1().getLs(),
                    opt3.getTrack1().getRs(),
                    opt3.getTrack2().getLt(),
                    opt3.getTrack2().getRt());
        } else if (xmlOpt instanceof Option4Type) {
            Option4Type opt4 = (Option4Type) xmlOpt;

            option = createOption4(
                    opt4.getTrack1().getL(),
                    opt4.getTrack2().getR(),
                    opt4.getTrack3().getC(),
                    opt4.getTrack4().getLFE(),
                    opt4.getTrack5().getLs(),
                    opt4.getTrack6().getRs(),
                    opt4.getTrack7().getLt(),
                    opt4.getTrack7().getRt());
        } else if (xmlOpt instanceof Option5Type) {
            Option5Type opt5 = (Option5Type) xmlOpt;

            option = createOption5(opt5.getTrack1().getL(), opt5.getTrack2().getR());
        } else if (xmlOpt instanceof Option6Type) {
            Option6Type opt6 = (Option6Type) xmlOpt;

            option = createOption6(opt6.getTrack1().getL(), opt6.getTrack1().getR());
        } else {
            // nothing
        }

        return option;
    }

    /**
     * Creates Option1A option.
     *
     * @param channels channels array to create by order
     * @return Option1A
     */
    private AudioOption createOption1A(ChannelType... channels) {
        AudioOption option = new AudioOption();

        option.add(createTrack(
                new Channel(FL.name(), channels[0]),
                new Channel(FR.name(), channels[1]),
                new Channel(FC.name(), channels[2]),
                new Channel(LFE.name(), channels[3]),
                new Channel(SL.name(), channels[4]),
                new Channel(SR.name(), channels[5])
        ));

        Stream.of(
                createTrack(new Channel(FL.name(), channels[6])),
                createTrack(new Channel(FR.name(), channels[7]))
        ).forEach((m) -> {
            option.add(m);
        });

        return option;
    }

    /**
     * Creates Option2 option.
     *
     * @param channels channels array to create by order
     * @return Option2
     */
    private AudioOption createOption2(ChannelType... channels) {
        AudioOption option = new AudioOption();

        Stream.of(
                createTrack(new Channel(FL.name(), channels[0])),
                createTrack(new Channel(FR.name(), channels[1])),
                createTrack(new Channel(FC.name(), channels[2])),
                createTrack(new Channel(LFE.name(), channels[3])),
                createTrack(new Channel(SL.name(), channels[4])),
                createTrack(new Channel(SR.name(), channels[5])),
                createTrack(new Channel(FL.name(), channels[6])),
                createTrack(new Channel(FR.name(), channels[7]))
        ).forEach((m) -> {
            option.add(m);
        });

        return option;
    }

    /**
     * Creates Option3 option.
     *
     * @param channels channels array to create by order
     * @return Option3
     */
    private AudioOption createOption3(ChannelType... channels) {
        AudioOption option = new AudioOption();

        option.add(createTrack(
                new Channel(FL.name(), channels[0]),
                new Channel(FR.name(), channels[1]),
                new Channel(FC.name(), channels[2]),
                new Channel(LFE.name(), channels[3]),
                new Channel(SL.name(), channels[4]),
                new Channel(SR.name(), channels[5])
        ));
        option.add(createTrack(
                new Channel(FL.name(), channels[6]),
                new Channel(FR.name(), channels[7])
        ));

        return option;
    }

    /**
     * Creates Option4 option.
     *
     * @param channels channels array to create by order
     * @return Option4
     */
    private AudioOption createOption4(ChannelType... channels) {
        AudioOption option = new AudioOption();

        Stream.of(
                createTrack(new Channel(FL.name(), channels[0])),
                createTrack(new Channel(FR.name(), channels[1])),
                createTrack(new Channel(FC.name(), channels[2])),
                createTrack(new Channel(LFE.name(), channels[3])),
                createTrack(new Channel(SL.name(), channels[4])),
                createTrack(new Channel(SR.name(), channels[5]))
        ).forEach((m) -> {
            option.add(m);
        });
        option.add(createTrack(
                new Channel(FL.name(), channels[6]),
                new Channel(FR.name(), channels[7])
        ));

        return option;
    }

    /**
     * Creates Option5 option.
     *
     * @param channels channels array to create by order
     * @return Option5
     */
    private AudioOption createOption5(ChannelType... channels) {
        AudioOption option = new AudioOption();

        Stream.of(
                createTrack(new Channel(FL.name(), channels[0])),
                createTrack(new Channel(FR.name(), channels[1]))
        ).forEach((m) -> {
            option.add(m);
        });

        return option;
    }

    /**
     * Creates Option6 option.
     *
     * @param channels channels array to create by order
     * @return Option6
     */
    private AudioOption createOption6(ChannelType... channels) {
        AudioOption option = new AudioOption();

        option.add(createTrack(
                new Channel(FL.name(), channels[0]),
                new Channel(FR.name(), channels[1])
        ));

        return option;
    }

    /**
     * Checks that Option does not contain any tracks.
     *
     * @param opt option
     * @return true if empty
     */
    private boolean isOptionEmpty(Object opt) {
        boolean empty = false;

        if (opt instanceof Option1AType) {
            Option1AType opt1A = (Option1AType) opt;
            empty = opt1A.getTrack1() == null
                    && opt1A.getTrack2() == null
                    && opt1A.getTrack3() == null;
        } else if (opt instanceof Option2Type) {
            Option2Type opt2 = (Option2Type) opt;
            empty = opt2.getTrack1() == null
                    && opt2.getTrack2() == null
                    && opt2.getTrack3() == null
                    && opt2.getTrack4() == null
                    && opt2.getTrack5() == null
                    && opt2.getTrack6() == null
                    && opt2.getTrack7() == null
                    && opt2.getTrack8() == null;
        } else if (opt instanceof Option3Type) {
            Option3Type opt3 = (Option3Type) opt;
            empty = opt3.getTrack1() == null
                    && opt3.getTrack2() == null;
        } else if (opt instanceof Option4Type) {
            Option4Type opt4 = (Option4Type) opt;
            empty = opt4.getTrack1() == null
                    && opt4.getTrack2() == null
                    && opt4.getTrack3() == null
                    && opt4.getTrack4() == null
                    && opt4.getTrack5() == null
                    && opt4.getTrack6() == null
                    && opt4.getTrack7() == null;
        } else if (opt instanceof Option5Type) {
            Option5Type opt5 = (Option5Type) opt;
            empty = opt5.getTrack1() == null
                    && opt5.getTrack2() == null;
        } else if (opt instanceof Option6Type) {
            Option6Type opt6 = (Option6Type) opt;
            empty = opt6.getTrack1() == null;
        } else {
            // nothing
        }

        return empty;
    }

    /**
     * Checks that Option contains all tracks.
     *
     * @param opt option
     * @return true if contains all tracks
     */
    private boolean hasOptionAllTracks(Object opt) {
        boolean allTracks = false;

        if (opt instanceof Option1AType) {
            Option1AType opt1A = (Option1AType) opt;
            allTracks = opt1A.getTrack1() != null
                    && opt1A.getTrack2() != null
                    && opt1A.getTrack3() != null;
        } else if (opt instanceof Option2Type) {
            Option2Type opt2 = (Option2Type) opt;
            allTracks = opt2.getTrack1() != null
                    && opt2.getTrack2() != null
                    && opt2.getTrack3() != null
                    && opt2.getTrack4() != null
                    && opt2.getTrack5() != null
                    && opt2.getTrack6() != null
                    && opt2.getTrack7() != null
                    && opt2.getTrack8() != null;
        } else if (opt instanceof Option3Type) {
            Option3Type opt3 = (Option3Type) opt;
            allTracks = opt3.getTrack1() != null
                    && opt3.getTrack2() != null;
        } else if (opt instanceof Option4Type) {
            Option4Type opt4 = (Option4Type) opt;
            allTracks = opt4.getTrack1() != null
                    && opt4.getTrack2() != null
                    && opt4.getTrack3() != null
                    && opt4.getTrack4() != null
                    && opt4.getTrack5() != null
                    && opt4.getTrack6() != null
                    && opt4.getTrack7() != null;
        } else if (opt instanceof Option5Type) {
            Option5Type opt5 = (Option5Type) opt;
            allTracks = opt5.getTrack1() != null
                    && opt5.getTrack2() != null;
        } else if (opt instanceof Option6Type) {
            Option6Type opt6 = (Option6Type) opt;
            allTracks = opt6.getTrack1() != null;
        } else {
            // nothing
        }

        return allTracks;
    }
}
