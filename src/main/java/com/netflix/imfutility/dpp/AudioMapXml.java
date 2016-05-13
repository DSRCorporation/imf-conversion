package com.netflix.imfutility.dpp;

import com.netflix.imfutility.dpp.audiomap.AudioMap;
import com.netflix.imfutility.dpp.audiomap.EBUTrackType;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
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
