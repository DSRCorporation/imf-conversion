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
package com.netflix.imfutility.ttmltostl.stl;

import com.netflix.imfutility.dpp.DppConversionXsdConstants;
import com.netflix.imfutility.generated.dpp.metadata.DppType;
import com.netflix.imfutility.generated.dpp.metadata.TimecodeType;
import com.netflix.imfutility.ttmltostl.ttml.Caption;
import com.netflix.imfutility.ttmltostl.ttml.Time;
import com.netflix.imfutility.ttmltostl.ttml.TimedTextObject;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import static com.netflix.imfutility.dpp.DppConversionXsdConstants.ISO_639_2_CODES_XML_SCHEME;
import static com.netflix.imfutility.dpp.DppConversionXsdConstants.TYPES_XML_SCHEME;
import static com.netflix.imfutility.ttmltostl.stl.GsiAttribute.CD;
import static com.netflix.imfutility.ttmltostl.stl.GsiAttribute.ECD;
import static com.netflix.imfutility.ttmltostl.stl.GsiAttribute.EN;
import static com.netflix.imfutility.ttmltostl.stl.GsiAttribute.OET;
import static com.netflix.imfutility.ttmltostl.stl.GsiAttribute.OPT;
import static com.netflix.imfutility.ttmltostl.stl.GsiAttribute.PUB;
import static com.netflix.imfutility.ttmltostl.stl.GsiAttribute.RD;
import static com.netflix.imfutility.ttmltostl.stl.GsiAttribute.TCF;
import static com.netflix.imfutility.ttmltostl.stl.GsiAttribute.TCP;

/**
 * BBC-specific implementation of EBU STL GSI block building. Some fields are build based on the input metadata.xml.
 */
public final class BbcGsiStrategy extends DefaultGsiStrategy {

    private static final String METADATA_XML_SCHEME = DppConversionXsdConstants.METADATA_XML_SCHEME;
    private static final String METADATA_PACKAGE = DppConversionXsdConstants.METADATA_PACKAGE;

    public static final int ZERO_SUBTITLE_DURATION_MS = 1000;

    private final DppType metadata;

    public BbcGsiStrategy(String metadataXml) throws XmlParsingException, FileNotFoundException {
        this.metadata = XmlParser.parse(new File(metadataXml),
                new String[]{TYPES_XML_SCHEME, ISO_639_2_CODES_XML_SCHEME, METADATA_XML_SCHEME},
                METADATA_PACKAGE, DppType.class);
    }

    @Override
    public void fillAttributes(TimedTextObject tto) {
        String programName = metadata.getEditorial().getProgrammeTitle() != null
                ? metadata.getEditorial().getProgrammeTitle() : "";

        // subtitle zero
        addSubtitleZero(tto, programName);

        super.fillAttributes(tto);

        // OPT
        if (!programName.isEmpty()) {
            OPT.setValue(programName);
        } else {
            OPT.fillEmptyValue();
        }

        // OET
        if (metadata.getEditorial().getEpisodeTitleNumber() != null) {
            OET.setValue(metadata.getEditorial().getEpisodeTitleNumber());
        } else {
            OET.fillEmptyValue();
        }

        // TCP
        fillTcp();

        // TCF
        fillTcf(tto);

        // PUB
        if (metadata.getEditorial().getOriginator() != null) {
            PUB.setValue(metadata.getEditorial().getOriginator());
        } else {
            PUB.fillEmptyValue();
        }

        // EN
        if (metadata.getEditorial().getDistributor() != null) {
            EN.setValue(metadata.getEditorial().getDistributor());
        } else if (metadata.getEditorial().getOriginator() != null) {
            EN.setValue(metadata.getEditorial().getOriginator());
        } else {
            EN.fillEmptyValue();
        }

        // ECD
        if (metadata.getTechnical().getContactInformation() != null
                && metadata.getTechnical().getContactInformation().getContactEmail() != null) {
            ECD.setValue(metadata.getTechnical().getContactInformation().getContactEmail());
        } else {
            ECD.fillEmptyValue();
        }

        // CD and RD from metadata.xml
        if (metadata.getTechnical().getAdditional() != null
                && metadata.getTechnical().getAdditional().getCompletionDate() != null) {
            GregorianCalendar gregorianCalendar = metadata.getTechnical().getAdditional().getCompletionDate().toGregorianCalendar();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
            simpleDateFormat.setCalendar(gregorianCalendar);
            String completionDate = simpleDateFormat.format(gregorianCalendar.getTime());
            CD.setValue(completionDate);
            RD.setValue(completionDate);
        }
    }

    private void addSubtitleZero(TimedTextObject tto, String programName) {
        // add subtitle zero
        int key = tto.getCaptions().isEmpty() ? 0 : tto.getCaptions().firstKey() - 1;
        Caption caption = new Caption();
        caption.setStart(new Time(0));
        caption.setEnd(new Time(ZERO_SUBTITLE_DURATION_MS));
        caption.setContent(programName);
        tto.getCaptions().put(key, caption);
    }

    private void fillTcp() {
        TCP.fillEmptyValue();

        if (metadata.getTechnical().getTimecodes() == null) {
            return;
        }
        if (metadata.getTechnical().getTimecodes().getParts() == null) {
            return;
        }
        if (metadata.getTechnical().getTimecodes().getParts().getPart() == null) {
            return;
        }
        if (metadata.getTechnical().getTimecodes().getParts().getPart().isEmpty()) {
            return;
        }

        TimecodeType tc = metadata.getTechnical().getTimecodes().getParts().getPart().get(0).getPartSOM();

        String[] parts = tc.getValue().split("[:;]");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.length() == 1) {
                sb.append("0");
                sb.append(part);
            } else if (part.length() > 2) {
                sb.append(part.substring(part.length() - 2, part.length()));
            } else {
                sb.append(part);
            }
        }

        TCP.setValue(sb.toString());
    }

    private void fillTcf(TimedTextObject tto) {
        String firstCaptionTime;
        if (tto.getCaptions().size() > 1) {
            // ignore subtitle zero
            firstCaptionTime = tto.getCaptions().values().toArray(new Caption[]{})[1]
                    .getStart().getTime("hhmmssff/" + getFrameRate());
        } else if (tto.getCaptions().size() == 1) {
            firstCaptionTime = tto.getCaptions().values().toArray(new Caption[]{})[0]
                    .getStart().getTime("hhmmssff/" + getFrameRate());
        } else {
            firstCaptionTime = "000000";
        }
        TCF.setValue(firstCaptionTime);
    }

}
