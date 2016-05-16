package com.netflix.imfutility.dpp;

import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import com.netflix.imfutility.xsd.dpp.metadata.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by Alexandr on 4/28/2016.
 * Provides functionality to generate empty metadata.xml for DPP format and transform it into BMXLib parameters.
 */
public class MetadataXml {

    private static final String METADATA_XML_SCHEME = "xsd/dpp/metadata.xsd";
    private static final String BMX_PARAMETERS_TRANSFORMATION = "xsd/dpp/bmx-parameters.xsl";
    private static final String XSLT2_TRANSFORMER_IMPLEMENTATION = "net.sf.saxon.TransformerFactoryImpl";
    private static final String METADATA_CONFIG_PACKAGE = "com.netflix.imfutility.xsd.dpp.metadata";

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
        } catch (JAXBException | DatatypeConfigurationException e) {
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

        Map<DMFramework, File> frameworkParameters = new HashMap<>();

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
            return loadMetadataXmlToDpp(metadataXmlFile);
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
        JAXBContext jaxbContext;
        try {
            Dpp dpp = loadMetadataXmlToDpp(metadataXmlFile);
            jaxbContext = JAXBContext.newInstance(METADATA_CONFIG_PACKAGE);
            return new JAXBSource(jaxbContext, dpp);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads and validates metadata.xml.
     *
     * @param metadataXmlFile the metadata.xml file
     * @return Dpp a Dpp instance with loaded metadata.xml
     * @throws XmlParsingException an exception in case of metadata.xml parsing error
     */
    private static Dpp loadMetadataXmlToDpp(File metadataXmlFile) throws XmlParsingException {
        return XmlParser.parse(
                metadataXmlFile, METADATA_XML_SCHEME, METADATA_CONFIG_PACKAGE, Dpp.class);
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
        } catch (TransformerException | IOException e) {
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

