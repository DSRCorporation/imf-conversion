package com.netflix.imfutility.mediainfo;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import com.netflix.imfutility.xsd.mediainfo.FfprobeType;
import com.netflix.imfutility.xsd.mediainfo.StreamType;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import static com.netflix.imfutility.Constants.MEDIAINFO_PACKAGE;
import static com.netflix.imfutility.Constants.MEDIAINFO_XSD;

/**
 * A helper class to build virtual track parameters (media info) for each sequence (virtual track).
 */
class VirtualTrackInfoMapBuilder {

    private final Map<SequenceUUID, VirtualTrackInfo> virtualTrackInfoMap = new HashMap<>();

    void addResourceInfo(File inputFile, File outputFile, ContextInfo contextInfo) throws XmlParsingException, MediaInfoException, FileNotFoundException {
        // 1. parse output xml
        FfprobeType mediaInfo = parseOutputFile(outputFile);

        // 2. check that info is available
        if (mediaInfo.getStreams() == null || mediaInfo.getStreams().getStream().isEmpty()) {
            throw new MediaInfoException("No streams output", inputFile.getAbsolutePath());
        }
        StreamType stream = mediaInfo.getStreams().getStream().get(0);

        // 3. fill info
        VirtualTrackInfo virtualTrackInfo = new VirtualTrackInfo(contextInfo.getSequenceType(), stream);

        // 4. check that virtual info for all resources within a virtual tracks are the same, that is have the same parameters.
        VirtualTrackInfo existingInfoForSeq = virtualTrackInfoMap.get(contextInfo.getSequenceUuid());
        if (existingInfoForSeq != null && !existingInfoForSeq.equals(virtualTrackInfo)) {
            throw new MediaInfoException("All resource tracks within a sequence (virtual track) must have the same parameters!", inputFile.getAbsolutePath());
        }

        // 5. add to map
        if (existingInfoForSeq == null) {
            virtualTrackInfoMap.put(contextInfo.getSequenceUuid(), virtualTrackInfo);
        }

    }

    Map<SequenceUUID, VirtualTrackInfo> getVirtualTrackInfoMap() {
        return virtualTrackInfoMap;
    }

    private FfprobeType parseOutputFile(File outputFile) throws XmlParsingException, FileNotFoundException {
        if (!outputFile.isFile()) {
            throw new FileNotFoundException(String.format("Invalid media info output file: '%s' not found", outputFile.getAbsolutePath()));
        }

        return XmlParser.parse(outputFile, MEDIAINFO_XSD, MEDIAINFO_PACKAGE, FfprobeType.class);
    }

}
