package com.netflix.imfutility.cpl._2013;

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.asset.AssetMap;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfoBuilder;
import com.netflix.imfutility.conversion.templateParameter.context.ResourceKey;
import com.netflix.imfutility.conversion.templateParameter.context.ResourceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters;
import com.netflix.imfutility.cpl.SequenceTypeCpl;
import com.netflix.imfutility.cpl.uuid.ResourceUUID;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.cpl.uuid.UUID;
import com.netflix.imfutility.imf._2013.*;
import com.netflix.imfutility.util.ConversionHelper;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import org.apache.commons.math3.fraction.BigFraction;

import javax.xml.bind.JAXBElement;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static com.netflix.imfutility.Constants.*;

/**
 * A CPL parser for 2013 namespace.
 * <ul>
 * <li>Parses the given CPL</li>
 * <li>Fills segment, sequence and resource contexts, so conversion can be started using context parameters
 * (see {@link com.netflix.imfutility.conversion.templateParameter.context.SequenceTemplateParameterContext},
 * {@link com.netflix.imfutility.conversion.templateParameter.context.SegmentTemplateParameterContext},
 * {@link com.netflix.imfutility.conversion.templateParameter.context.ResourceTemplateParameterContext}).</li>
 * </ul>
 */
public class Cpl2013ContextBuilder {

    private final TemplateParameterContextProvider contextProvider;
    private final AssetMap assetMap;

    private BigFraction compositionEditRate;
    private SegmentUUID currentSegmentUuid;
    private SequenceType currentSequence;
    private SequenceUUID currentSequenceUuid;
    private com.netflix.imfutility.conversion.SequenceType currentSequenceType;

    private final Map<String, BigFraction> videoEssences = new HashMap<>();

    private final Map<SequenceUUID, BigInteger> lastSegmentDuration = new HashMap<>();
    private BigInteger currentSegmentDuration;

    public Cpl2013ContextBuilder(TemplateParameterContextProvider contextProvider, AssetMap assetMap) {
        this.contextProvider = contextProvider;
        this.assetMap = assetMap;
    }

    /**
     * Parses the given CPL file and fills sequence, segment and resource contexts.
     *
     * @param cplFile an input CPL file.
     * @throws XmlParsingException, FileNotFoundException if the input is not a valid XML or it doesn't pass XSD validation
     */
    public void build(File cplFile) throws XmlParsingException, FileNotFoundException {
        // 1. parse CPL
        CompositionPlaylistType cpl2013 = XmlParser.parse(cplFile,
                new String[]{XMLDSIG_CORE_SCHEMA_XSD, DCML_TYPES_XSD, CPL_2013_XSD, CORE_CONSTRAINTS_2013_XSD},
                CPL_2013_PACKAGE, CompositionPlaylistType.class);

        // 2. get a composition edit rate (it's used if no specific edit rate is specified for a segment).
        this.compositionEditRate = ConversionHelper.parseEditRate(cpl2013.getEditRate());

        // 3. go through all segments and all sequences and build segment, sequence and resource contexts.
        for (SegmentType segment : cpl2013.getSegmentList().getSegment()) {
            this.currentSegmentUuid = SegmentUUID.create(segment.getId());

            contextProvider.getSegmentContext().initSegment(currentSegmentUuid);

            for (Object anySeqJaxb : segment.getSequenceList().getAny()) {
                if (!(anySeqJaxb instanceof JAXBElement)) {
                    throw new ConversionException(String.format("Could not understand a sequence '%s'", anySeqJaxb.toString()));
                }

                JAXBElement jaxbElement = (JAXBElement) (anySeqJaxb);
                Object anySeq = jaxbElement.getValue();

                SequenceTypeCpl currentSequenceTypeCpl = SequenceTypeCpl.fromName(jaxbElement.getName().getLocalPart());
                if ((currentSequenceTypeCpl != null) && (anySeq instanceof SequenceType)) {
                    this.currentSequence = (SequenceType) anySeq;
                    this.currentSequenceType = currentSequenceTypeCpl.toSequenceType();
                    this.currentSequenceUuid = SequenceUUID.create(currentSequence.getTrackId());
                    processSequence();
                }
            }
        }

        // 4. check for audio sequences which has essences containing both audio and video
        // (the values of DURATION_FRAME_EDIT_UNIT and START_TIME_FRAME_EDIT_UNIT  parameters must be calculated in video frames in this case)
        buildTimeAndDurationInFrames();
    }

    private void processSequence() {
        // 1. check that the sequence type is known
        if (currentSequenceType == null) {
            throw new ConversionException(String.format("Sequence '%s': Unknown sequence type", currentSequence.getId()));
        }

        // 2. prepare the data for current segment duration calculation
        if (!lastSegmentDuration.containsKey(currentSequenceUuid)) {
            lastSegmentDuration.put(currentSequenceUuid, BigInteger.valueOf(0L));
        }
        currentSegmentDuration = BigInteger.valueOf(0L);

        // 3. init the sequence
        contextProvider.getSequenceContext().initSequence(currentSequenceType, currentSequenceUuid);

        // 4. process all resources within the sequence and segment and fill the Resource context
        currentSequence.getResourceList().getResource().forEach(this::processResource);

        // 5. save the duration of this segment for this sequence
        lastSegmentDuration.put(currentSequenceUuid, currentSegmentDuration);
    }

    private void processResource(BaseResourceType resource) {
        if (!(resource instanceof TrackFileResourceType)) {
            return;
        }
        TrackFileResourceType trackFileResource = (TrackFileResourceType) resource;

        // 1. init resource context
        ResourceUUID resourceId = ResourceUUID.create(trackFileResource.getId());
        ResourceKey resourceKey = ResourceKey.create(currentSegmentUuid, currentSequenceUuid, currentSequenceType);
        contextProvider.getResourceContext().initResource(resourceKey, resourceId);


        // 2. Init essence parameter. Check that we have a corresponding track file in assetmap
        // asset map already contains full absolute paths
        UUID trackId = UUID.create(trackFileResource.getTrackFileId());
        String assetPath = assetMap.getAsset(trackId);
        if (assetPath == null) {
            throw new ConversionException(String.format(
                    "Resource track file '%s' isn't present in assetmap.xml", trackId));
        }
        contextProvider.getResourceContext().addResourceParameter(resourceKey, resourceId,
                ResourceContextParameters.ESSENCE, assetPath);

        // 3. Init startTime parameter
        BigFraction editRate = trackFileResource.getEditRate() != null
                ? ConversionHelper.parseEditRate(trackFileResource.getEditRate()) : compositionEditRate;
        BigInteger startTimeEditUnit = trackFileResource.getEntryPoint() != null
                ? trackFileResource.getEntryPoint() : BigInteger.valueOf(0);
        String startTimeTimeCode = ConversionHelper.editUnitToTimecode(startTimeEditUnit, editRate);
        contextProvider.getResourceContext().addResourceParameter(resourceKey, resourceId,
                ResourceContextParameters.START_TIME_TIMECODE, startTimeTimeCode);
        contextProvider.getResourceContext().addResourceParameter(resourceKey, resourceId,
                ResourceContextParameters.START_TIME_EDIT_UNIT, startTimeEditUnit.toString());
        // assume essence has only one seq type (either video or audio), so start time in frames is equal to start time in edit units.
        contextProvider.getResourceContext().addResourceParameter(resourceKey, resourceId,
                ResourceContextParameters.START_TIME_FRAME_EDIT_UNIT, startTimeEditUnit.toString());

        // 4. init duration parameter
        BigInteger durationEditUnit;
        if (trackFileResource.getSourceDuration() != null) {
            durationEditUnit = trackFileResource.getSourceDuration();
        } else {
            durationEditUnit = trackFileResource.getIntrinsicDuration().subtract(startTimeEditUnit);
        }
        String durationTimecode = ConversionHelper.editUnitToTimecode(durationEditUnit, editRate);
        contextProvider.getResourceContext().addResourceParameter(resourceKey, resourceId,
                ResourceContextParameters.DURATION_TIMECODE, durationTimecode);
        contextProvider.getResourceContext().addResourceParameter(resourceKey, resourceId,
                ResourceContextParameters.DURATION_EDIT_UNIT, durationEditUnit.toString());
        // assume essence has only one seq type (either video or audio), so duration in frames is equal to duration in edit units.
        contextProvider.getResourceContext().addResourceParameter(resourceKey, resourceId,
                ResourceContextParameters.DURATION_FRAME_EDIT_UNIT, durationEditUnit.toString());

        // 5. init endTime parameter
        BigInteger endTimeEditUnit = startTimeEditUnit.add(durationEditUnit);
        contextProvider.getResourceContext().addResourceParameter(resourceKey, resourceId,
                ResourceContextParameters.END_TIME_EDIT_UNIT, endTimeEditUnit.toString());
        contextProvider.getResourceContext().addResourceParameter(resourceKey, resourceId,
                ResourceContextParameters.END_TIME_TIMECODE, ConversionHelper.editUnitToTimecode(endTimeEditUnit, editRate));

        // 6. init offset parameter
        BigInteger offsetEditUnit = lastSegmentDuration.get(currentSequenceUuid);
        contextProvider.getResourceContext().addResourceParameter(resourceKey, resourceId,
                ResourceContextParameters.OFFSET_EDIT_UNIT, offsetEditUnit.toString());
        contextProvider.getResourceContext().addResourceParameter(resourceKey, resourceId,
                ResourceContextParameters.OFFSET_TIMECODE, ConversionHelper.editUnitToTimecode(offsetEditUnit, editRate));

        // 7. init edit rate parameter
        contextProvider.getResourceContext().addResourceParameter(resourceKey, resourceId,
                ResourceContextParameters.EDIT_RATE, ConversionHelper.toEditRate(editRate));

        // 8. init repeat count parameter
        BigInteger repeatCount = trackFileResource.getRepeatCount() != null
                ? trackFileResource.getRepeatCount() : BigInteger.ONE;
        contextProvider.getResourceContext().addResourceParameter(resourceKey, resourceId,
                ResourceContextParameters.REPEAT_COUNT, repeatCount.toString());

        // 9. calculate the total duration of the current segment
        currentSegmentDuration = currentSegmentDuration.add(durationEditUnit);

        // 10. save all video essences to later re-check DURATION_FRAME_EDIT_UNIT and START_TIME_FRAME_EDIT_UNIT for
        // audio sequences which has essences containing both audio and video (the values must be calculated in video frames in this case)
        if (currentSequenceType == com.netflix.imfutility.conversion.SequenceType.VIDEO) {
            videoEssences.put(assetPath, editRate);
        }
    }

    private void buildTimeAndDurationInFrames() {
        ResourceTemplateParameterContext resourceContext = contextProvider.getResourceContext();

        // process only audio
        com.netflix.imfutility.conversion.SequenceType seqType = com.netflix.imfutility.conversion.SequenceType.AUDIO;
        for (SequenceUUID seqUuid : contextProvider.getSequenceContext().getUuids(seqType)) {
            for (SegmentUUID segmUuid : contextProvider.getSegmentContext().getUuids()) {
                ResourceKey resourceKey = ResourceKey.create(segmUuid, seqUuid, seqType);
                for (ResourceUUID resUuid : resourceContext.getUuids(resourceKey)) {
                    ContextInfo contextInfo = new ContextInfoBuilder()
                            .setResourceUuid(resUuid)
                            .setSegmentUuid(segmUuid)
                            .setSequenceUuid(seqUuid)
                            .setSequenceType(seqType).build();

                    String essence = resourceContext.getParameterValue(ResourceContextParameters.ESSENCE, contextInfo);
                    BigFraction videoEditRate = videoEssences.get(essence); // frame rate
                    // the essence containing the audio has also a video
                    if (videoEditRate != null) {
                        // start time and duration in audio edit units (samples)
                        BigInteger startTimeEU = new BigInteger(
                                resourceContext.getParameterValue(ResourceContextParameters.START_TIME_EDIT_UNIT, contextInfo));
                        BigInteger durationEU = new BigInteger(
                                resourceContext.getParameterValue(ResourceContextParameters.DURATION_EDIT_UNIT, contextInfo));
                        // audio edit rate (sample rate)
                        BigFraction editRate = ConversionHelper.parseEditRate(
                                resourceContext.getParameterValue(ResourceContextParameters.EDIT_RATE, contextInfo));

                        // convert start time and duration from samples to video frames
                        String startTimeInFrames = String.valueOf(ConversionHelper.toNewEditRate(startTimeEU, editRate, videoEditRate));
                        String durationInFrames = String.valueOf(ConversionHelper.toNewEditRate(durationEU, editRate, videoEditRate));

                        // save in context
                        resourceContext.addResourceParameter(resourceKey, resUuid, ResourceContextParameters.START_TIME_FRAME_EDIT_UNIT, startTimeInFrames);
                        resourceContext.addResourceParameter(resourceKey, resUuid, ResourceContextParameters.DURATION_FRAME_EDIT_UNIT, durationInFrames);
                    }
                }
            }
        }

    }

}
