package com.netflix.imfutility.dpp;

import com.netflix.imfutility.dpp.metadata.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.bind.util.JAXBSource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.util.*;

/**
 * Created by Alexandr on 4/28/2016.
 */
public class MetadataXml {

    private static final String METADATA_XML_SCHEME = "xsd/dpp/metadata.xsd";
    private static final String BMX_PARAMETERS_TRANSFORMATION = "xsd/dpp/bmx-parameters.xsl";
    private static final String XSLT2_TRANSFORMER_IMPLEMENTATION = "net.sf.saxon.TransformerFactoryImpl";

    public enum DMFramework {
        UKDPP("UKDPP"),
        AS11CORE("AS11Core"),
        AS11Segmentation("AS11Segmentation");

        private final String value;

        DMFramework(String v) {
            value = v;
        }

        private String value() {
            return value;
        }
    }

    public static void GenerateEmptyXml(String path) {
        File file = new File(path);
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(Dpp.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            //Editorial section
            EditorialType editorial = new EditorialType();
            editorial.setSeriesTitle("");
            editorial.setEpisodeTitleNumber("");
            editorial.setProgrammeTitle("");
            editorial.setProductionNumber("");
            editorial.setSynopsis("");
            editorial.setOriginator("");
            editorial.setCopyrightYear(Calendar.getInstance().get(Calendar.YEAR));
            editorial.setOtherIdentifier("");
            editorial.setOtherIdentifierType("");
            editorial.setGenre("");
            editorial.setDistributor("");

            //Technical section
            TechnicalType technical = new TechnicalType();
            technical.setShimName(ShimNameType.UK_DPP_HD);
            technical.setShimVersion("1.1");

            //Video
            VideoType video = new VideoType();
            video.setPictureRatio("");
            video.setThreeD(false);
            video.setThreeDType(ThreeDTypeType.SIDE_BY_SIDE);
            video.setProductPlacement(false);
            video.setPSEPass(PSEPassType.NOT_TESTED);
            video.setPSEManufacturer("");
            video.setPSEVersion("");
            video.setVideoComments("");

            //Audio
            AudioType audio = new AudioType();
            audio.setAudioTrackLayout(AudioTrackLayoutDmAs11Type.EBU_R_48_2_A);
            audio.setPrimaryAudioLanguage(Iso6392CodeType.ZXX);
            audio.setSecondaryAudioLanguage(Iso6392CodeType.ZXX);
            audio.setTertiaryAudioLanguage(Iso6392CodeType.ZXX);
            audio.setAudioLoudnessStandard(AudioLoudnessStandardType.NONE);
            audio.setAudioComments("");

            //Timecodes
            TimecodesType timecodes = new TimecodesType();
            TimecodeType zeroTimecode = new TimecodeType();
            zeroTimecode.setValue("00:00:00:00");
            DurationType zeroDuration = new DurationType();
            zeroDuration.setValue("00:00:00:00");
            timecodes.setLineUpStart(zeroTimecode);
            timecodes.setIdentClockStart(zeroTimecode);
            timecodes.setTotalNumberOfParts(1);
            timecodes.setTotalProgrammeDuration(zeroDuration);

            //Timecodes parts
            SegmentationType segmentation = new SegmentationType();
            SegmentType segment = new SegmentType();
            segment.setPartNumber(1);
            segment.setPartTotal(1);
            segment.setPartSOM(zeroTimecode);
            segment.setPartDuration(zeroDuration);
            segmentation.getPart().add(segment);
            timecodes.setParts(segmentation);

            //AccessService
            AccessServicesType accessServicesType = new AccessServicesType();
            accessServicesType.setAudioDescriptionPresent(false);
            accessServicesType.setAudioDescriptionType(AudioDescriptionTypeType.CONTROL_DATA_NARRATION);
            accessServicesType.setClosedCaptionsPresent(false);
            accessServicesType.setClosedCaptionsType(CaptionsTypeType.TRANSLATION);
            accessServicesType.setClosedCaptionsLanguage(Iso6392CodeType.ZXX);
            accessServicesType.setOpenCaptionsPresent(false);
            accessServicesType.setOpenCaptionsType(CaptionsTypeType.HARD_OF_HEARING);
            accessServicesType.setOpenCaptionsLanguage(Iso6392CodeType.ZXX);
            accessServicesType.setSigningPresent(SigningPresentType.NO);
            accessServicesType.setSignLanguage(SignLanguageType.BSL_BRITISH_SIGN_LANGUAGE);

            //Additional Section
            AdditionalType additional = new AdditionalType();
            additional.setCompletionDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
            additional.setTextlessElementsExist(false);
            additional.setProgrammeHasText(false);
            additional.setProgrammeTextLanguage(Iso6392CodeType.ZXX);

            //Contact Information
            ContactInformationType contactInformation = new ContactInformationType();
            contactInformation.setContactEmail("account@myemail.com");
            contactInformation.setContactTelephoneNumber("+1 000 000 0000");

            // Metadata XML empty structure
            Dpp dpp = new Dpp();
            dpp.setEditorial(editorial);
            dpp.setTechnical(technical);
            technical.setVideo(video);
            technical.setAudio(audio);
            technical.setTimecodes(timecodes);
            technical.setAccessServices(accessServicesType);
            technical.setAdditional(additional);
            technical.setContactInformation(contactInformation);

            jaxbMarshaller.marshal(dpp, file);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<DMFramework, File> getBmxDppParameters(File metadataXmlFile) {

        // Source
        JAXBContext jc = null;
        JAXBSource source = null;
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);

            //Get file from resources folder
            ClassLoader classLoader = MetadataXml.class.getClassLoader();
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(new File(classLoader.getResource(METADATA_XML_SCHEME).getFile()));
            spf.setSchema(schema);

            jc = JAXBContext.newInstance(Dpp.class);
            Unmarshaller jaxbUnmarshaller = jc.createUnmarshaller();
            UnmarshallerHandler unmarshallerHandler = jaxbUnmarshaller.getUnmarshallerHandler();

            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            MetadataXmlParsingHandler contentErrorHandler = new MetadataXmlParsingHandler(unmarshallerHandler);
            xr.setErrorHandler(contentErrorHandler);
            xr.setContentHandler(contentErrorHandler);

            InputSource xml = new InputSource(new FileReader(metadataXmlFile));
            xr.parse(xml);

            Dpp dpp = (Dpp) unmarshallerHandler.getResult();
            source = new JAXBSource(jc, dpp);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<DMFramework, File> frameworkParameters = new HashMap<DMFramework, File>();

        frameworkParameters.put(DMFramework.UKDPP, getBmxFrameworkParameters(source, DMFramework.UKDPP));
        frameworkParameters.put(DMFramework.AS11CORE, getBmxFrameworkParameters(source, DMFramework.AS11CORE));
        frameworkParameters.put(DMFramework.AS11Segmentation, getBmxFrameworkParameters(source, DMFramework.AS11Segmentation));

        return frameworkParameters;
    }

    public static void validateMetadataXml(File metadataXmlFile) {

    }

    private static File getBmxFrameworkParameters(JAXBSource source, DMFramework framework) {
        try {
            //Get file from resources folder
            ClassLoader classLoader = MetadataXml.class.getClassLoader();

            // Create Transformer
            TransformerFactory tf = TransformerFactory.newInstance(XSLT2_TRANSFORMER_IMPLEMENTATION, null);
            StreamSource xslt = new StreamSource(classLoader.getResource(BMX_PARAMETERS_TRANSFORMATION).getFile());
            Transformer transformer = tf.newTransformer(xslt);

            //Set framework
            transformer.setParameter("framework", framework.value());

            //Prepare empty temporary file
            File temp = File.createTempFile(UUID.randomUUID().toString(), ".txt");
            if (!temp.delete()) {
                throw new RuntimeException(String.format("Could not delete temporary file: %s", temp.getAbsolutePath()));
            }
            temp.deleteOnExit();

            // Result
            FileWriter writer = new FileWriter(temp);
            StreamResult result = new StreamResult(writer);

            // Transform
            transformer.transform(source, result);

            writer.flush();
            writer.close();

            return temp;
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}

