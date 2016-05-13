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
 * Provides functionality to generate empty metadata.xml for DPP format and transform it into BMXLib parameters.
 */
public class MetadataXml {

    private static final String METADATA_XML_SCHEME = "xsd/dpp/metadata.xsd";
    private static final String BMX_PARAMETERS_TRANSFORMATION = "xsd/dpp/bmx-parameters.xsl";
    private static final String XSLT2_TRANSFORMER_IMPLEMENTATION = "net.sf.saxon.TransformerFactoryImpl";

    /**
     * MXF frameworks enumeration
     */
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

    /**
     * Generates empty metadata.xml file.
     *
     * @param path a path to the output metadata.xml file
     */
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
            video.setPictureRatio("16:9");
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

    /**
     * Transform metadata.xml into a set of parameter files for BMXLib tool.
     *
     * @param metadataXmlFile the metadata.xml file
     * @return a map with the parameter files for BMXLib
     * @throws XmlParsingException an exception in case of metadata.xml parsing error
     */
    public static Map<DMFramework, File> getBmxDppParameters(File metadataXmlFile) throws XmlParsingException {

        JAXBSource source = loadMetadataXml(metadataXmlFile);

        Map<DMFramework, File> frameworkParameters = new HashMap<DMFramework, File>();

        frameworkParameters.put(DMFramework.UKDPP, getBmxFrameworkParameters(source, DMFramework.UKDPP));
        frameworkParameters.put(DMFramework.AS11CORE, getBmxFrameworkParameters(source, DMFramework.AS11CORE));
        frameworkParameters.put(DMFramework.AS11Segmentation, getBmxFrameworkParameters(source, DMFramework.AS11Segmentation));

        return frameworkParameters;
    }

    /**
     * Loads and validates metadata.xml.
     *
     * @param metadataXmlFile the metadata.xml file
     * @return Dpp a Dpp instance with loaded metadata.xml
     * @throws XmlParsingException an exception in case of metadata.xml parsing error
     */
    public static Dpp getDpp(File metadataXmlFile) throws XmlParsingException {
        try {
            JAXBContext jc = JAXBContext.newInstance(Dpp.class);
            return loadMetadataXmlToDpp(jc, metadataXmlFile);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads and validates metadata.xml.
     *
     * @param metadataXmlFile the metadata.xml file
     * @return JAXBSource with loaded and mapped metadata.xml
     * @throws XmlParsingException an exception in case of metadata.xml parsing error
     */
    private static JAXBSource loadMetadataXml(File metadataXmlFile) throws XmlParsingException {
        try {
            JAXBContext jc = JAXBContext.newInstance(Dpp.class);
            Dpp dpp = loadMetadataXmlToDpp(jc, metadataXmlFile);
            return new JAXBSource(jc, dpp);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Loads and validates metadata.xml.
     *
     * @param jc JAXBContext initialized with Dpp.class
     * @param metadataXmlFile the metadata.xml file
     * @return Dpp a Dpp instance with loaded metadata.xml
     * @throws XmlParsingException an exception in case of metadata.xml parsing error
     */
    private static Dpp loadMetadataXmlToDpp(JAXBContext jc, File metadataXmlFile) throws XmlParsingException {
        XmlParsingHandler contentErrorHandler = null;
        try {

            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);

            //Get file from resources folder
            ClassLoader classLoader = MetadataXml.class.getClassLoader();
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(new File(classLoader.getResource(METADATA_XML_SCHEME).getFile()));
            spf.setSchema(schema);

            Unmarshaller jaxbUnmarshaller = jc.createUnmarshaller();
            UnmarshallerHandler unmarshallerHandler = jaxbUnmarshaller.getUnmarshallerHandler();

            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            contentErrorHandler = new XmlParsingHandler(unmarshallerHandler);
            xr.setErrorHandler(contentErrorHandler);
            xr.setContentHandler(contentErrorHandler);

            InputSource xml = new InputSource(new FileReader(metadataXmlFile));
            xr.parse(xml);

            if (contentErrorHandler.getParsingErrors().size() > 0) {
                throw new XmlParsingException(contentErrorHandler.getParsingErrors());
            }

            return (Dpp) unmarshallerHandler.getResult();
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

    /**
     * Transforms metadata.xml into a set of parameters for particular MXF framework.
     *
     * @param source    loaded and validated JAXBSource with metadata.xml
     * @param framework the framework for which the parameters must be transformed.
     * @return a temporary file to be used as BMXLib input parameter for particular framework.
     */
    private static File getBmxFrameworkParameters(JAXBSource source, DMFramework framework) {
        FileWriter writer = null;
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
            writer = new FileWriter(temp);
            StreamResult result = new StreamResult(writer);

            // Transform
            transformer.transform(source, result);
            writer.flush();

            return temp;
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

