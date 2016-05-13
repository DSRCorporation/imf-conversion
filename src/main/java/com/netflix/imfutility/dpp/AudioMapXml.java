package com.netflix.imfutility.dpp;

import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import com.netflix.imfutility.xsd.dpp.audiomap.AudioMap;
import com.netflix.imfutility.xsd.dpp.audiomap.EBUTrackType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import java.io.File;

/**
 * Created by Alexandr on 5/12/2016.
 *
 * Basic functionality for audiomap.xml handling.
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
            AudioMap audioMap = new AudioMap();
            audioMap.getEBUTrack().add(ebuTrack1);
            audioMap.getEBUTrack().add(ebuTrack2);
            audioMap.getEBUTrack().add(ebuTrack3);
            audioMap.getEBUTrack().add(ebuTrack4);

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
