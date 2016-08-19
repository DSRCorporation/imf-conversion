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
package com.netflix.imfutility.dpp.metadata;

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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Provides functionality to generate empty metadata.xml for DPP format.
 */
public final class MetadataXmlCreator {

    private static final String LINE_UP_START = "09:59:30:00";
    private static final String IDENT_CLOCK_START = "09:59:50:00";
    private static final String FIRST_PART_SOM = "10:00:00:00";

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
            TimecodeType lineUpStart = new TimecodeType();
            lineUpStart.setValue(LINE_UP_START);
            TimecodeType identClockStart = new TimecodeType();
            identClockStart.setValue(IDENT_CLOCK_START);
            DurationType zeroDuration = new DurationType();
            zeroDuration.setValue("00:00:00:00");
            timecodes.setLineUpStart(lineUpStart);
            timecodes.setIdentClockStart(identClockStart);
            timecodes.setTotalNumberOfParts(1);
            timecodes.setTotalProgrammeDuration(zeroDuration);

            //Timecodes parts
            SegmentationType segmentation = new SegmentationType();
            SegmentType segment = new SegmentType();
            segment.setPartNumber(1);
            segment.setPartTotal(1);
            TimecodeType firstPartSOM = new TimecodeType();
            firstPartSOM.setValue(FIRST_PART_SOM);
            segment.setPartSOM(firstPartSOM);
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

    private MetadataXmlCreator() {
    }

}

