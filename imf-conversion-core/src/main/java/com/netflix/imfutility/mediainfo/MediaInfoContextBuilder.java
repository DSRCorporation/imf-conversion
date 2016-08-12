/*
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
package com.netflix.imfutility.mediainfo;

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.CoreConstants;
import com.netflix.imfutility.conversion.executor.strategy.ExecuteStrategyFactory;
import com.netflix.imfutility.conversion.executor.strategy.OperationInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfoBuilder;
import com.netflix.imfutility.conversion.templateParameter.context.ResourceKey;
import com.netflix.imfutility.conversion.templateParameter.context.SequenceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.DynamicContextParameters;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters;
import com.netflix.imfutility.cpl.uuid.ResourceUUID;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.FormatType;
import com.netflix.imfutility.generated.conversion.MediaInfoCommandType;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.generated.mediainfo.FfprobeType;
import com.netflix.imfutility.generated.mediainfo.StreamType;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.netflix.imfutility.CoreConstants.MEDIAINFO_PACKAGE;

/**
 * Builds template parameters context related to Media Info.
 * <ul>
 * <li>For each resource, get resource parameters (media info) invoking an external analyzing tool from conversion.xml</li>
 * <li>We assume that some parameters of each resource within a sequence (virtual track) must be equal
 * (in particular, number of channels).</li>
 * <li>Fill Resource context with the obtained parameters.</li>
 * </ul>
 */
public class MediaInfoContextBuilder {

    private final TemplateParameterContextProvider contextProvider;
    private final ExecuteStrategyFactory executeStrategyFactory;
    private final FormatType format;

    // {type-asset-UUID} - AssetInfo map
    private final Map<ImmutablePair<SequenceType, String>, VirtualTrackInfo> processedMediaInfo = new HashMap<>();

    /**
     * Gets a media info XML file name created for the given sequence of the given type. The file is created in the working directory.
     *
     * @param seqType    a virtual track type (video, audio, subtitle)
     * @param essence    a full path to the essence file
     * @param workingDir a full path to the working directory.
     * @return media info XML file name for the given sequence of the given type
     */
    public static File getOutputFile(SequenceType seqType, String essence, File workingDir) {
        return new File(workingDir, getOutputFileName(seqType, essence));
    }

    /**
     * Gets a unique name of a dynamic parameter to store the output media info XML file created to the given essence of the given type
     * (see {@link #getOutputFileName(SequenceType, String)}).
     *
     * @param seqType a virtual track type (video, audio, subtitle)
     * @param essence a full path to the essence file
     * @return a unique dynamic parameter name.
     */
    public static String getOutputDynamicParamName(SequenceType seqType, String essence) {
        return String.format("%s_%s",
                DynamicContextParameters.MEDIA_INFO_OUTPUT.getName(),
                getOutputFileName(seqType, essence));
    }

    static String getOutputFileName(SequenceType seqType, String essence) {
        return String.format("%s_%s_%s.xml",
                CoreConstants.MEDIA_INFO_SUFFIX,
                seqType.value(),
                new File(essence).getName());
    }

    public MediaInfoContextBuilder(TemplateParameterContextProvider contextProvider, ExecuteStrategyFactory executeStrategyFactory) {
        this.contextProvider = contextProvider;
        this.executeStrategyFactory = executeStrategyFactory;
        this.format = contextProvider.getConversionProvider().getFormat();
    }

    /**
     * Invoke an external command from conversion.xml for each essence and each track type (video, audio, subtitle) participating in CPL
     * to create a media info XML ({@link #getOutputFile(SequenceType, String, File)}).
     * <ul>
     * <li>XSD validation is performed for the created media info xml file.</li>
     * <li>The created media info xml file is added to the dynamic context to be deleted on exit.</li>
     * <li>The media info XML file is parsed and Sequence context is filled with the values from media info XML
     * (such as fps, sample rate, size, etc..)</li>
     * <li>Each virtual track must have the same parameters (such as fps, sample rate, etc.)
     * Otherwise a {@link MediaInfoException} is thrown.</li>
     * </ul>
     *
     * @throws IOException         if creation of media info XML files or calling of external media info tools fail.
     * @throws XmlParsingException if created media info XML file is not a valid XML or it doesn't pass XSD validation
     * @throws MediaInfoException  if a virtual track contains mismatched parameters (such as fps, sample rate, etc.,)
     *                             for different segments.
     */
    public void build() throws IOException, XmlParsingException, MediaInfoException {
        SequenceTemplateParameterContext sequenceContext = contextProvider.getSequenceContext();
        for (SequenceType seqType : sequenceContext.getSequenceTypes()) {
            for (SequenceUUID seqUuid : sequenceContext.getUuids(seqType)) {
                VirtualTrackInfo prevVirtualTrack = null;
                for (SegmentUUID segmUuid : contextProvider.getSegmentContext().getUuids()) {
                    for (ResourceUUID resUuid : contextProvider.getResourceContext()
                            .getUuids(ResourceKey.create(segmUuid, seqUuid, seqType))) {
                        ContextInfo contextInfo = new ContextInfoBuilder()
                                .setResourceUuid(resUuid)
                                .setSegmentUuid(segmUuid)
                                .setSequenceUuid(seqUuid)
                                .setSequenceType(seqType).build();
                        prevVirtualTrack = doBuild(contextInfo, prevVirtualTrack);
                    }
                }
                // we assume all resources within an audio sequence have the same number of channels
                if (prevVirtualTrack.getParameters().containsKey(ResourceContextParameters.CHANNELS_NUM)) {
                    sequenceContext.addSequenceParameter(
                            seqType, seqUuid,
                            SequenceContextParameters.CHANNELS_NUM,
                            prevVirtualTrack.getParameters().get(ResourceContextParameters.CHANNELS_NUM));
                }
            }
        }
    }

    private VirtualTrackInfo doBuild(ContextInfo contextInfo, VirtualTrackInfo prevVirtualTrack)
            throws IOException, XmlParsingException, MediaInfoException {
        // 1. get the corresponding essence
        String essence = contextProvider.getResourceContext().getParameterValue(
                ResourceContextParameters.ESSENCE, contextInfo);
        String trackFileId = contextProvider.getResourceContext().getParameterValue(
                ResourceContextParameters.TRACK_FILE_ID, contextInfo);

        // 2. check if we already have media info loaded for this track. If no - load it by executing an external program.
        ImmutablePair<SequenceType, String> key = ImmutablePair.of(contextInfo.getSequenceType(), trackFileId);
        VirtualTrackInfo processedInfo = processedMediaInfo.get(key);
        if (processedInfo == null) {
            File outputFile = getMediaInfo(contextInfo.getSequenceType(), essence);
            processedInfo = getTrackInfo(outputFile, contextInfo, essence);
            processedMediaInfo.put(key, processedInfo);
        }

        // 3. we assume that some parameters within a sequence must be equal (for example, channels number).
        validateSequenceHomogeneous(prevVirtualTrack, processedInfo);

        // 4. add to resource context
        buildResourceContext(processedInfo, contextInfo);

        return processedInfo;
    }

    private File getMediaInfo(SequenceType seqType, String essence) throws IOException {
        // 1. fill dynamic context's mediaInfoInput
        contextProvider.getDynamicContext().addParameter(DynamicContextParameters.MEDIA_INFO_INPUT, essence, false);

        // 2. prepare the output file
        File outputFile = getOutputFile(seqType, essence, contextProvider.getWorkingDir());

        // 2. execute media info command. the output will be the provided file.
        executeMediaInfoCommand(seqType, essence, outputFile);

        // 3. add output as a dynamic parameter to delete on exit
        contextProvider.getDynamicContext().addParameter(
                getOutputDynamicParamName(seqType, essence), outputFile.getAbsolutePath(), true);

        return outputFile;
    }

    private void executeMediaInfoCommand(SequenceType seqType, String essence, File outputFile) throws IOException {
        MediaInfoCommandType mediaInfoCommand;
        switch (seqType) {
            case VIDEO:
                mediaInfoCommand = format.getMediaInfoCommandVideo();
                break;
            case AUDIO:
                mediaInfoCommand = format.getMediaInfoCommandAudio();
                break;
            case SUBTITLE:
                mediaInfoCommand = format.getMediaInfoCommandSubtitle();
                break;
            default:
                throw new ConversionException(String.format("Can not get media info. Unknown sequence type '%s'", seqType.toString()));
        }

        String operationName = String.format("%s_%s", mediaInfoCommand.getClass().getSimpleName(), new File(essence).getName());
        OperationInfo operationInfo = new OperationInfo(
                mediaInfoCommand.getValue(), operationName, ContextInfo.EMPTY, false, outputFile);
        executeStrategyFactory.createExecuteOnceStrategy(contextProvider).execute(operationInfo);
    }

    private VirtualTrackInfo getTrackInfo(File outputFile, ContextInfo contextInfo, String essence)
            throws XmlParsingException, MediaInfoException, FileNotFoundException {
        // 1. parse output xml
        FfprobeType mediaInfo = parseOutputFile(outputFile, contextInfo);

        // 2. check that info is available
        if (mediaInfo.getStreams() == null || mediaInfo.getStreams().getStream().isEmpty()) {
            throw new MediaInfoException("No streams output", essence);
        }
        StreamType stream = mediaInfo.getStreams().getStream().get(0);

        // 3. fill info
        return new VirtualTrackInfo(contextInfo.getSequenceType(), stream);
    }

    FfprobeType parseOutputFile(File outputFile, ContextInfo contextInfo) throws XmlParsingException, FileNotFoundException {
        if (!outputFile.isFile()) {
            throw new FileNotFoundException(String.format("Invalid media info output file: '%s' not found", outputFile.getAbsolutePath()));
        }

        // do not validate according to XSD as sometimes the output may contain not all required attributes
        return XmlParser.parse(outputFile, null, MEDIAINFO_PACKAGE, FfprobeType.class);
    }

    private void buildResourceContext(VirtualTrackInfo virtualTrackInfo, ContextInfo contextInfo) {
        virtualTrackInfo.getParameters().forEach(
                (paramName, paramValue) ->
                        contextProvider.getResourceContext().addResourceParameter(
                                ResourceKey.create(contextInfo),
                                contextInfo.getResourceUuid(),
                                paramName,
                                paramValue));
    }

    private void validateSequenceHomogeneous(VirtualTrackInfo prevVirtualTrackInfo, VirtualTrackInfo nextVirtualTrackInfo) {
        if (prevVirtualTrackInfo == null) {
            return;
        }
        if (nextVirtualTrackInfo == null) {
            return;
        }

        if (!Objects.equals(
                prevVirtualTrackInfo.getParameters().get(ResourceContextParameters.CHANNELS_NUM),
                nextVirtualTrackInfo.getParameters().get(ResourceContextParameters.CHANNELS_NUM))) {
            throw new ConversionException(
                    "All audio resource tracks within an audio sequence (virtual track) must have the same number of channels!");
        }
    }

}
