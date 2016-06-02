package com.netflix.imfutility.mediainfo;

import com.netflix.imfutility.Constants;
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
import com.netflix.imfutility.xml.XmlParsingException;
import com.netflix.imfutility.xsd.conversion.FormatType;
import com.netflix.imfutility.xsd.conversion.MediaInfoCommandType;
import com.netflix.imfutility.xsd.conversion.SequenceType;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Builds template parameters context related to Media Info.
 * <ul>
 * <li>For each resource, get resource parameters (media info) invoking an external analyzing tool from conversion.xml</li>
 * <li>Parameters of each resource within a sequence (virtual track) must be equal.</li>
 * <li>Fill Sequence context with the obtained parameters.</li>
 * </ul>
 */
public class MediaInfoContextBuilder {

    private final TemplateParameterContextProvider contextProvider;
    private final ExecuteStrategyFactory executeStrategyFactory;
    private final FormatType format;

    private final VirtualTrackInfoMapBuilder virtualTrackInfoMapBuilder;

    public MediaInfoContextBuilder(TemplateParameterContextProvider contextProvider, ExecuteStrategyFactory executeStrategyFactory) {
        this.contextProvider = contextProvider;
        this.executeStrategyFactory = executeStrategyFactory;
        this.format = contextProvider.getFormat();
        this.virtualTrackInfoMapBuilder = new VirtualTrackInfoMapBuilder();
    }

    public void build() throws IOException, XmlParsingException, MediaInfoException {
        SequenceTemplateParameterContext sequenceContext = contextProvider.getSequenceContext();
        for (SequenceType seqType : sequenceContext.getSequenceTypes()) {
            for (SequenceUUID seqUuid : sequenceContext.getUuids(seqType)) {
                for (SegmentUUID segmUuid : contextProvider.getSegmentContext().getUuids()) {
                    for (ResourceUUID resUuid : contextProvider.getResourceContext()
                            .getUuids(ResourceKey.create(segmUuid, seqUuid, seqType))) {
                        ContextInfo contextInfo = new ContextInfoBuilder()
                                .setResourceUuid(resUuid)
                                .setSegmentUuid(segmUuid)
                                .setSequenceUuid(seqUuid)
                                .setSequenceType(seqType).build();
                        doBuild(contextInfo);
                    }
                }
            }
        }

        buildSequenceContext();
    }

    private void doBuild(ContextInfo contextInfo) throws IOException, XmlParsingException, MediaInfoException {
        // 1. the next essence get media info for.
        String essence = contextProvider.getResourceContext().getParameterValue(
                ResourceContextParameters.ESSENCE, contextInfo);

        // 2. fill dynamic context's mediaInfoInput
        contextProvider.getDynamicContext().addParameter(DynamicContextParameters.MEDIA_INFO_INPUT, essence, false);

        // 3. execute media info command. the output will be in %{tmp.mediaInfoOutput}
        File outputFile = executeMediaInfoCommand(contextInfo);

        // 4. add output as a dynamic parameter to delete on exit
        contextProvider.getDynamicContext().addParameter(
                DynamicContextParameters.MEDIA_INFO_OUTPUT.getName() + contextInfo.getSequenceType().value(),
                outputFile.getAbsolutePath(), true);

        // 5. build sequence map
        virtualTrackInfoMapBuilder.addResourceInfo(new File(essence), outputFile, contextInfo);
    }

    File executeMediaInfoCommand(ContextInfo contextInfo) throws IOException {
        File outputFile = new File(contextProvider.getWorkingDir(), Constants.MEDIA_INFO_SUFFIX + contextInfo.getSequenceType().value() + ".xml");
        MediaInfoCommandType mediaInfoCommand = null;
        switch (contextInfo.getSequenceType()) {
            case VIDEO:
                mediaInfoCommand = format.getMediaInfoCommandVideo();
                break;
            case AUDIO:
                mediaInfoCommand = format.getMediaInfoCommandAudio();
                break;
            case SUBTITLE:
                mediaInfoCommand = format.getMediaInfoCommandSubtitle();
                break;
        }

        OperationInfo operationInfo = new OperationInfo(
                mediaInfoCommand.getValue(), mediaInfoCommand.getClass().getSimpleName(), mediaInfoCommand.getClass(), contextInfo, outputFile);
        executeStrategyFactory.createExecuteOnceStrategy(contextProvider).execute(operationInfo);

        return outputFile;
    }

    private void buildSequenceContext() {
        for (Map.Entry<SequenceUUID, VirtualTrackInfo> entry : virtualTrackInfoMapBuilder.getVirtualTrackInfoMap().entrySet()) {
            SequenceUUID seqUuid = entry.getKey();
            VirtualTrackInfo virtualTrackInfo = entry.getValue();
            for (Map.Entry<SequenceContextParameters, String> paramEntry : virtualTrackInfo.getParameters().entrySet()) {
                contextProvider.getSequenceContext().addSequenceParameter(
                        virtualTrackInfo.getSeqType(),
                        seqUuid,
                        paramEntry.getKey(),
                        paramEntry.getValue());
            }
        }
    }

}
