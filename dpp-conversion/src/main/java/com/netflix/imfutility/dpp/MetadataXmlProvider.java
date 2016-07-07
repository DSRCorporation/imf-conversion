/**
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
package com.netflix.imfutility.dpp;

import com.netflix.imfutility.generated.dpp.metadata.AccessServicesType;
import com.netflix.imfutility.generated.dpp.metadata.AdditionalType;
import com.netflix.imfutility.generated.dpp.metadata.AudioDescriptionTypeType;
import com.netflix.imfutility.generated.dpp.metadata.AudioLoudnessStandardType;
import com.netflix.imfutility.generated.dpp.metadata.AudioTrackLayoutDmAs11Type;
import com.netflix.imfutility.generated.dpp.metadata.AudioType;
import com.netflix.imfutility.generated.dpp.metadata.CaptionsTypeType;
import com.netflix.imfutility.generated.dpp.metadata.ContactInformationType;
import com.netflix.imfutility.generated.dpp.metadata.DppType;
import com.netflix.imfutility.generated.dpp.metadata.DurationType;
import com.netflix.imfutility.generated.dpp.metadata.EditorialType;
import com.netflix.imfutility.generated.dpp.metadata.Iso6392CodeType;
import com.netflix.imfutility.generated.dpp.metadata.ObjectFactory;
import com.netflix.imfutility.generated.dpp.metadata.PSEPassType;
import com.netflix.imfutility.generated.dpp.metadata.SegmentType;
import com.netflix.imfutility.generated.dpp.metadata.SegmentationType;
import com.netflix.imfutility.generated.dpp.metadata.ShimNameType;
import com.netflix.imfutility.generated.dpp.metadata.SignLanguageType;
import com.netflix.imfutility.generated.dpp.metadata.SigningPresentType;
import com.netflix.imfutility.generated.dpp.metadata.TechnicalType;
import com.netflix.imfutility.generated.dpp.metadata.ThreeDTypeType;
import com.netflix.imfutility.generated.dpp.metadata.TimecodeType;
import com.netflix.imfutility.generated.dpp.metadata.TimecodesType;
import com.netflix.imfutility.generated.dpp.metadata.VideoType;
import com.netflix.imfutility.resources.ResourceHelper;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import static com.netflix.imfutility.dpp.DppConversionConstants.BMX_FRAMEWORK_PARAM;
import static com.netflix.imfutility.dpp.DppConversionConstants.BMX_PARAMETERS_TRANSFORMATION;
import static com.netflix.imfutility.dpp.DppConversionConstants.XSLT2_TRANSFORMER_IMPLEMENTATION;
import static com.netflix.imfutility.dpp.DppConversionXsdConstants.ISO_639_2_CODES_XML_SCHEME;
import static com.netflix.imfutility.dpp.DppConversionXsdConstants.METADATA_PACKAGE;
import static com.netflix.imfutility.dpp.DppConversionXsdConstants.METADATA_XML_SCHEME;
import static com.netflix.imfutility.dpp.DppConversionXsdConstants.TYPES_XML_SCHEME;

/**
 * Created by Alexandr on 4/28/2016.
 * Provides functionality to generate empty metadata.xml for DPP format and transform it into BMXLib parameters.
 */
public class MetadataXmlProvider {

    /**
     * MXF frameworks enumeration.
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
    public static void generateEmptyXml(String path) {
        File file = new File(path);
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(DppType.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            //Editorial section
            EditorialType editorial = new EditorialType();
            editorial.setSeriesTitle("Required Title");
            editorial.setEpisodeTitleNumber("Required Number");
            editorial.setProgrammeTitle("Required Programme Title");
            editorial.setProductionNumber("Required Production Number");
            editorial.setSynopsis("Required Synopsis");
            editorial.setOriginator("Required Originator");
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
            DppType dpp = new DppType();
            dpp.setEditorial(editorial);
            dpp.setTechnical(technical);
            technical.setVideo(video);
            technical.setAudio(audio);
            technical.setTimecodes(timecodes);
            technical.setAccessServices(accessServicesType);
            technical.setAdditional(additional);
            technical.setContactInformation(contactInformation);

            JAXBElement<DppType> dppJaxb = new ObjectFactory().createDpp(dpp);
            jaxbMarshaller.marshal(dppJaxb, file);
        } catch (JAXBException | DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private final DppType dpp;
    private final File workingDir;
    private Map<DMFramework, File> bmxDppParameters = new HashMap<>();

    /**
     * Loads and validates metadata.xml.
     * Transforms metadata.xml into a set of parameter files for BMXLib tool.
     * The parameter files are created within the provided working directory.
     *
     * @param metadataFile a path to the metadata.xml file
     * @param workingDir   current working directory where parameter files are created.
     * @throws XmlParsingException   an exception in case of metadata.xml parsing error
     * @throws FileNotFoundException if the metadataXml doesn't define an existing file.
     */
    public MetadataXmlProvider(File metadataFile, File workingDir) throws XmlParsingException, FileNotFoundException {
        this.workingDir = workingDir;
        this.dpp = loadDpp(metadataFile);
    }

    /**
     * Gets the loaded DPP instances created from a provided metadata.xml.
     *
     * @return a loaded DPP instances created from a provided metadata.xml
     */
    public DppType getDpp() {
        return dpp;
    }

    /**
     * Transform metadata.xml into a set of parameter files for BMXLib tool.
     * The parameter files are created within the provided working directory.
     */
    public void createBmxDppParameterFiles() {
        JAXBSource source = dppToJaxbSource(dpp);

        bmxDppParameters = new HashMap<>();
        bmxDppParameters.put(DMFramework.UKDPP, createBmxFrameworkParameterFile(source, DMFramework.UKDPP, workingDir));
        bmxDppParameters.put(DMFramework.AS11CORE, createBmxFrameworkParameterFile(source, DMFramework.AS11CORE, workingDir));
        bmxDppParameters.put(DMFramework.AS11Segmentation, createBmxFrameworkParameterFile(
                source, DMFramework.AS11Segmentation, workingDir));
    }

    /**
     * Gets a parameter files for BMXLib tool for the given framework.
     * The parameter files are created within the provided working directory.
     *
     * @param framework a framework get a parameter file for.
     * @return a parameter file withing the current working directory.
     */
    public File getBmxDppParameterFile(DMFramework framework) {
        return bmxDppParameters.get(framework);
    }

    /**
     * Gets all parameter files for BMXLib tool for all frameworks (see {@link DMFramework}).
     * The parameter files are created within the provided working directory.
     *
     * @return a collection of all parameter files for BMXLib within the working directory.
     */
    public Collection<File> getBmxDppParameterFiles() {
        return bmxDppParameters.values();
    }

    private DppType loadDpp(File metadataFile) throws XmlParsingException, FileNotFoundException {
        if (!metadataFile.isFile()) {
            throw new FileNotFoundException(String.format("Invalid metadata.xml file: '%s' not found", metadataFile.getAbsolutePath()));
        }

        return XmlParser.parse(metadataFile,
                new String[]{TYPES_XML_SCHEME, ISO_639_2_CODES_XML_SCHEME, METADATA_XML_SCHEME},
                METADATA_PACKAGE, DppType.class);
    }

    /**
     * Transforms metadata.xml into a set of parameters for particular MXF framework.
     *
     * @param source    loaded and validated JAXBSource with metadata.xml
     * @param framework the framework for which the parameters must be transformed.
     * @return a temporary file to be used as BMXLib input parameter for particular framework.
     */
    private File createBmxFrameworkParameterFile(JAXBSource source, DMFramework framework, File workingDir) {
        // Create Transformer
        Transformer transformer;
        try {
            // Create Transformer
            TransformerFactory tf = TransformerFactory.newInstance(XSLT2_TRANSFORMER_IMPLEMENTATION, null);
            InputStream transformationStream = ResourceHelper.getResourceInputStream(BMX_PARAMETERS_TRANSFORMATION);
            if (transformationStream == null) {
                throw new FileNotFoundException(String.format(
                        "Metadata.xml to BMX transformation file is absent: %s", BMX_PARAMETERS_TRANSFORMATION));
            }
            StreamSource xslt = new StreamSource(transformationStream);
            transformer = tf.newTransformer(xslt);

            //Set framework
            transformer.setParameter(BMX_FRAMEWORK_PARAM, framework.value());

        } catch (TransformerException | IOException e) {
            throw new RuntimeException(e);
        }

        //Prepare a parameter file
        File result = new File(workingDir, framework.value + ".txt");

        // Transform
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(result), "UTF-8"))) {
            StreamResult streamResult = new StreamResult(writer);
            transformer.transform(source, streamResult);
            writer.flush();
            return result;
        } catch (TransformerException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JAXBSource dppToJaxbSource(DppType dpp) {
        try {
            JAXBElement<DppType> dppJaxb = new ObjectFactory().createDpp(dpp);
            return new JAXBSource(JAXBContext.newInstance(METADATA_PACKAGE), dppJaxb);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}

