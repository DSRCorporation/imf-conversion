package com.netflix.imfutility.cpl._2013;

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.asset.AssetMap;
import com.netflix.imfutility.conversion.templateParameter.context.ResourceKey;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters;
import com.netflix.imfutility.cpl.SequenceTypeCpl;
import com.netflix.imfutility.cpl.uuid.ResourceUUID;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.cpl.uuid.UUID;
import com.netflix.imfutility.util.ConversionHelper;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import com.netflix.imfutility.xsd.imf._2013.cpl.*;
import org.apache.commons.math3.fraction.BigFraction;

import javax.xml.bind.JAXBElement;
import java.io.File;
import java.math.BigInteger;

import static com.netflix.imfutility.Constants.CPL_2013_PACKAGE;
import static com.netflix.imfutility.Constants.XSD_CPL_2013_XSD;

/**
 * A CPL parser for 2013 namespace.
 * <ul>
 * <li>Parses the given CPL</li>
 * <li>Fills segment, sequence and resource contexts, so conversion can be started using context parameters.</li>
 * </ul>
 */
public class Cpl2013ContextBuilder {

    private final TemplateParameterContextProvider contextProvider;
    private final AssetMap assetMap;

    private BigFraction compositionEditRate;
    private SegmentUUID currentSegmentUuid;
    private SequenceType currentSequence;
    private SequenceUUID currentSequenceUuid;
    private com.netflix.imfutility.xsd.conversion.SequenceType currentSequenceType;

    public Cpl2013ContextBuilder(TemplateParameterContextProvider contextProvider, AssetMap assetMap) {
        this.contextProvider = contextProvider;
        this.assetMap = assetMap;
    }

    public void build(File cplFile) throws XmlParsingException {
        CompositionPlaylistType cpl2013 = XmlParser.parse(cplFile, XSD_CPL_2013_XSD, CPL_2013_PACKAGE, CompositionPlaylistType.class);

        this.compositionEditRate = ConversionHelper.parseEditRate(cpl2013.getEditRate());

        for (SegmentType segment : cpl2013.getSegmentList().getSegment()) {
            this.currentSegmentUuid = SegmentUUID.create(segment.getId());

            contextProvider.getSegmentContext().initSegment(currentSegmentUuid);

            for (Object anySeqJaxb : segment.getSequenceList().getAny()) {
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

    }

    private void processSequence() {
        if (currentSequenceType == null) {
            throw new ConversionException(String.format("Sequence '%s': Unknown sequence type", currentSequence.getId()));
        }
        contextProvider.getSequenceContext().initSequence(currentSequenceType, currentSequenceUuid);

        currentSequence.getResourceList().getResource().forEach(this::processResource);
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

    }


}
