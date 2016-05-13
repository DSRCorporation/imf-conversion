package com.netflix.imfutility.dpp;

import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import com.netflix.imfutility.xsd.dpp.audiomap.AudioMap;
import com.netflix.imfutility.xsd.dpp.audiomap.AudioVirtualTrackType;
import com.netflix.imfutility.xsd.dpp.audiomap.ChannelType;
import com.netflix.imfutility.xsd.dpp.audiomap.MapType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;

/**
 * Created by Alexandr on 5/12/2016.
 */
public class AudioMapXml {

    private static final String AUDIOMAP_XML_SCHEME = "xsd/dpp/audiomap.xsd";
    private static final String AUDIOMAP_CONFIG_PACKAGE = "com.netflix.imfutility.xsd.dpp.audiomap";

    /**
     * Generates sample audiomap.xml file.
     *
     * @param path a path to the output audiomap.xml file
     */
    public static void GenerateSampleXml(String path) {
        File file = new File(path);
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(AudioMap.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Channel Map
            // Sample with EBU R48: 2a  (2 stereo and 2 silence channels)
            MapType map = new MapType();
            ChannelType channel1 = new ChannelType();
            channel1.setValue(1);
            channel1.setSourceChannel(1);
            ChannelType channel2 = new ChannelType();
            channel2.setValue(2);
            channel2.setSourceChannel(2);
            ChannelType channel3 = new ChannelType();
            channel3.setValue(3);
            channel3.setSourceChannel(null);
            ChannelType channel4 = new ChannelType();
            channel4.setValue(4);
            channel4.setSourceChannel(null);

            map.getChannel().add(channel1);
            map.getChannel().add(channel2);
            map.getChannel().add(channel3);
            map.getChannel().add(channel4);

            // Audio Track
            AudioVirtualTrackType audioVirtualTrack = new AudioVirtualTrackType();
            // Virtual Track Id from CPL
            audioVirtualTrack.setTrackId("urn:uuid:38d52c00-68d3-4056-8858-28eeaf3238d3");
            audioVirtualTrack.setMap(map);

            // Audiomap XML structure
            AudioMap audioMap = new AudioMap();
            audioMap.getAudioVirtualTrack().add(audioVirtualTrack);

            jaxbMarshaller.marshal(audioMap, file);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads and validates audiomap.xml.
     *
     * @param audioMapXmlFile the audiomap.xml file
     * @return AudioMapType with loaded and mapped metadata.xml
     * @throws XmlParsingException an exception in case of metadata.xml parsing error
     */
    public static AudioMap loadAudioMapXml(File audioMapXmlFile) throws XmlParsingException {
        return new XmlParser().parse(
                audioMapXmlFile, AUDIOMAP_XML_SCHEME, AUDIOMAP_CONFIG_PACKAGE, AudioMap.class);
    }
}
