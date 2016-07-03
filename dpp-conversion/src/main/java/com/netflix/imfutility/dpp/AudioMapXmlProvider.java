package com.netflix.imfutility.dpp;

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.conversion.SequenceType;
import com.netflix.imfutility.conversion.templateParameter.ContextInfoBuilder;
import com.netflix.imfutility.conversion.templateParameter.context.SequenceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.dpp.audiomap.AudioMapType;
import com.netflix.imfutility.dpp.audiomap.EBUTrackType;
import com.netflix.imfutility.dpp.audiomap.ObjectFactory;
import com.netflix.imfutility.dpp.metadata.AudioTrackLayoutDmAs11Type;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static com.netflix.imfutility.dpp.DppConversionXsdConstants.*;

/**
 * Created by Alexandr on 5/12/2016.
 * <p>
 * Basic functionality for audiomap.xml handling.
 * </p>
 */
public class AudioMapXmlProvider {

    /**
     * Generates a sample audiomap.xml file.
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

            JAXBElement<AudioMapType> audioMapJaxb = new ObjectFactory().createAudioMap(audioMap);
            jaxbMarshaller.marshal(audioMapJaxb, file);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private final TemplateParameterContextProvider contextProvider;
    private final AudioTrackLayoutDmAs11Type audioLayout;
    private final File audioMapFile;

    private final AudioMapType audioMap;
    private final LinkedHashMap<String, Integer> channelsForTracks;

    /**
     * Creates a default audiomap.xml (see {@link #generateDefaultXml()}, loads it and validates.
     *
     * @param contextProvider context provider.
     * @throws XmlParsingException   an exception in case of audiomap.xml parsing error
     * @throws FileNotFoundException if the audioMapXml doesn't define an existing file.
     */
    public AudioMapXmlProvider(AudioTrackLayoutDmAs11Type audioLayout, TemplateParameterContextProvider contextProvider) throws FileNotFoundException, XmlParsingException {
        this(null, audioLayout, contextProvider);
    }

    /**
     * Loads and validates audiomap.xml. Creates a default audiomap.xml (see {@link #generateDefaultXml()} if audioMapXml is null.
     *
     * @param audioMapFile    a path to audiomap.xml file. May be null.
     * @param contextProvider context provider.
     * @throws XmlParsingException   an exception in case of audiomap.xml parsing error
     * @throws FileNotFoundException if the audioMapXml doesn't define an existing file.
     */
    public AudioMapXmlProvider(File audioMapFile, AudioTrackLayoutDmAs11Type audioLayout, TemplateParameterContextProvider contextProvider) throws FileNotFoundException, XmlParsingException {
        this.audioLayout = audioLayout;
        this.contextProvider = contextProvider;

        this.channelsForTracks = getChannelsForTracks();

        if (audioMapFile == null) {
            // if no audiomap.xml is provided - create a default one for the given input and audio layout specified in metadata.xml
            audioMapFile = generateDefaultXml();
            // add as dynamic parameter to delete at the end.
            contextProvider.getDynamicContext().addParameter(DppConversionConstants.DYNAMIC_AUDIO_MAP_XML, audioMapFile.getAbsolutePath(), true);
        }
        this.audioMapFile = audioMapFile;
        this.audioMap = loadAudioMapXml();
    }

    /**
     * Gets the corresponding audiomap.xml (either generated or provided)
     *
     * @return a path to audiomap xml.
     */
    public File getAudioMapFile() {
        return audioMapFile;
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
     * Loads and validates audiomap.xml.
     *
     * @return AudioMapType with loaded and mapped audiomap.xml
     * @throws XmlParsingException   an exception in case of audiomap.xml parsing error
     * @throws FileNotFoundException if the audioMapXml doesn't define an existing file.
     */
    private AudioMapType loadAudioMapXml() throws XmlParsingException, FileNotFoundException {
        if (!audioMapFile.isFile()) {
            throw new FileNotFoundException(String.format("Invalid audiomap.xml file: '%s' not found", audioMapFile.getAbsolutePath()));
        }
        return XmlParser.parse(audioMapFile, new String[] {AUDIOMAP_XML_SCHEME}, AUDIOMAP_PACKAGE, AudioMapType.class);
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
     * @return an audiomap.xml file in the provided working directory.
     */
    public File generateDefaultXml() {
        return generateDefaultXml(DppConversionConstants.DEFAULT_AUDIO_MAP);
    }

    /**
     * Generates a sample audiomap.xml file that maps all channels of all virtual tracks (sequencedTracks parameter) sequentially 1:1
     * for the number of audio tracks as defined by the audio layout specified in metadata.xml.
     * If the number of input channels is less than required number of audio tracks, the remaining audio tracks are filled with silence.
     *
     * @param audioMapName audiomap file name.
     * @return an audiomap.xml file in the provided working directory.
     */
    public File generateDefaultXml(String audioMapName) {
        File audiomapFile = new File(contextProvider.getWorkingDir(), audioMapName);

        Integer ebuAudioTracks = getEBUAudioTracks();

        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(AudioMapType.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            AudioMapType audioMap = new AudioMapType();

            final int[] currentAudioTrack = {1};
            channelsForTracks.forEach((String trackId, Integer trackChannelCount) -> {
                for (int i = 0; i < trackChannelCount; i++) {
                    if (currentAudioTrack[0] <= ebuAudioTracks) {
                        EBUTrackType ebuTrack = new EBUTrackType();
                        ebuTrack.setNumber(currentAudioTrack[0]);
                        ebuTrack.setCPLVirtualTrackId(trackId);
                        ebuTrack.setCPLVirtualTrackChannel(i + 1);
                        audioMap.getEBUTrack().add(ebuTrack);
                        currentAudioTrack[0]++;
                    }
                }
            });

            while (currentAudioTrack[0] <= ebuAudioTracks) {
                EBUTrackType ebuTrack = new EBUTrackType();
                ebuTrack.setNumber(currentAudioTrack[0]);
                audioMap.getEBUTrack().add(ebuTrack);
                currentAudioTrack[0]++;
            }

            JAXBElement<AudioMapType> audioMapJaxb = new ObjectFactory().createAudioMap(audioMap);
            jaxbMarshaller.marshal(audioMapJaxb, audiomapFile);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        return audiomapFile;
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
        // ffmpeg -i test_output.wav -i test_output2.wav -filter_complex "[0:a][1:a]amerge,pan=4c|c0=c0|c1=c1|c2=0*c0|c3=0*c0[aout]" -map "[aout]" -acodec pcm_s24le -ar 48000 output_map.wav
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
            case EBU_R_48_4_B:
            case EBU_R_48_4_C:
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
