package com.netflix.imfutility.dpp;

import com.netflix.imfutility.dpp.audiomap.AudioMap;
import com.netflix.imfutility.dpp.audiomap.AudioVirtualTrackType;
import com.netflix.imfutility.dpp.audiomap.ChannelType;
import com.netflix.imfutility.dpp.audiomap.MapType;
import com.netflix.imfutility.dpp.metadata.Dpp;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.bind.util.JAXBSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Alexandr on 5/12/2016.
 */
public class AudioMapXml {

    private static final String AUDIOMAP_XML_SCHEME = "xsd/dpp/audiomap.xsd";

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
        XmlParsingHandler contentErrorHandler = null;
        try {

            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);

            //Get file from resources folder
            ClassLoader classLoader = MetadataXml.class.getClassLoader();
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(new File(classLoader.getResource(AUDIOMAP_XML_SCHEME).getFile()));
            spf.setSchema(schema);

            JAXBContext jc = JAXBContext.newInstance(AudioMap.class);
            Unmarshaller jaxbUnmarshaller = jc.createUnmarshaller();
            UnmarshallerHandler unmarshallerHandler = jaxbUnmarshaller.getUnmarshallerHandler();

            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            contentErrorHandler = new XmlParsingHandler(unmarshallerHandler);
            xr.setErrorHandler(contentErrorHandler);
            xr.setContentHandler(contentErrorHandler);

            InputSource xml = new InputSource(new FileReader(audioMapXmlFile));
            xr.parse(xml);

            if (contentErrorHandler.getParsingErrors().size() > 0) {
                throw new XmlParsingException(contentErrorHandler.getParsingErrors());
            }

            return (AudioMap) unmarshallerHandler.getResult();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            if (contentErrorHandler != null && contentErrorHandler.getParsingErrors().size() > 0) {
                throw new XmlParsingException(e, contentErrorHandler.getParsingErrors());
            } else {
                throw new RuntimeException(e);
            }
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
