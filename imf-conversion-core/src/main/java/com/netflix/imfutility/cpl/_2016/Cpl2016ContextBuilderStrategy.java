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
package com.netflix.imfutility.cpl._2016;

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.asset.AssetMap;
import com.netflix.imfutility.conversion.templateParameter.context.ResourceKey;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters;
import com.netflix.imfutility.cpl.AbstractCplContextBuilderStrategy;
import com.netflix.imfutility.cpl.SequenceTypeCpl;
import com.netflix.imfutility.cpl.uuid.ResourceUUID;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.cpl.uuid.UUID;
import com.netflix.imfutility.generated.imf._2016.BaseResourceType;
import com.netflix.imfutility.generated.imf._2016.CompositionPlaylistType;
import com.netflix.imfutility.generated.imf._2016.SegmentType;
import com.netflix.imfutility.generated.imf._2016.SequenceType;
import com.netflix.imfutility.generated.imf._2016.TrackFileResourceType;
import com.netflix.imfutility.util.ConversionHelper;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.fraction.BigFraction;

import javax.xml.bind.JAXBElement;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static com.netflix.imfutility.CoreConstants.CORE_CONSTRAINTS_2016_XSD;
import static com.netflix.imfutility.CoreConstants.CPL_2016_PACKAGE;
import static com.netflix.imfutility.CoreConstants.CPL_2016_XSD;
import static com.netflix.imfutility.CoreConstants.DCML_TYPES_XSD;
import static com.netflix.imfutility.CoreConstants.XMLDSIG_CORE_SCHEMA_XSD;

/**
 * A CPL parser for 2016 namespace.
 * <ul>
 * <li>Parses the given CPL</li>
 * <li>Inits segment, sequence and resource contexts.</li>
 * <li>Fills edit-unit-based resource parameters.</li>
 * </ul>
 */
public class Cpl2016ContextBuilderStrategy extends AbstractCplContextBuilderStrategy {

    private CompositionPlaylistType cpl2016;

    private BigFraction compositionEditRate;
    private SegmentUUID currentSegmentUuid;
    private SequenceType currentSequence;
    private SequenceUUID currentSequenceUuid;
    private com.netflix.imfutility.generated.conversion.SequenceType currentSequenceType;

    private final Map<SequenceUUID, BigInteger> lastSegmentDuration = new HashMap<>();
    private BigInteger currentSegmentDuration;

    public Cpl2016ContextBuilderStrategy(TemplateParameterContextProvider contextProvider, AssetMap assetMap) {
        super(contextProvider, assetMap);
    }

    @Override
    public void parse(File cplFile) throws XmlParsingException, FileNotFoundException {
        cpl2016 = XmlParser.parse(cplFile,
                new String[]{XMLDSIG_CORE_SCHEMA_XSD, DCML_TYPES_XSD, CPL_2016_XSD, CORE_CONSTRAINTS_2016_XSD},
                CPL_2016_PACKAGE, CompositionPlaylistType.class);
    }

    @Override
    public String getCompositionTimecodeStart() {
        if (cpl2016.getCompositionTimecode() == null) {
            return null;
        }
        String timecode = cpl2016.getCompositionTimecode().getTimecodeStartAddress();
        if (StringUtils.isEmpty(timecode)) {
            return null;
        }
        return timecode;
    }

    @Override
    public BigFraction getCompositionTimecodeRate() {
        if (cpl2016.getCompositionTimecode() == null) {
            return null;
        }
        BigInteger rate = cpl2016.getCompositionTimecode().getTimecodeRate();
        boolean isDropFrame = cpl2016.getCompositionTimecode().isTimecodeDropFrame();
        if (rate == null || BigInteger.ZERO.equals(rate)) {
            return null;
        }

        // return as-is if non-drop
        if (!isDropFrame) {
            return new BigFraction(rate);
        }

        // return as 30000/1001 for 30 if drop frame
        return new BigFraction(
                rate.multiply(BigInteger.valueOf(1000)),
                BigInteger.valueOf(1001));
    }

    @Override
    protected void buildFromCpl() {
        // 1. get a composition edit rate (it's used if no specific edit rate is specified for a segment).
        this.compositionEditRate = ConversionHelper.parseEditRate(cpl2016.getEditRate());

        // 2. go through all segments and all sequences and build segment, sequence and resource contexts.
        for (SegmentType segment : cpl2016.getSegmentList().getSegment()) {
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

        BigInteger repeatCount = trackFileResource.getRepeatCount() != null
                ? trackFileResource.getRepeatCount() : BigInteger.ONE;

        for (long i = 0; i < repeatCount.longValue(); i++) {
            processResourceRepeat(trackFileResource, i);
        }
    }

    private void processResourceRepeat(TrackFileResourceType trackFileResource, long repeat) {
        // 1. init resource context
        ResourceUUID resourceId = ResourceUUID.create(trackFileResource.getId(), repeat);
        ResourceKey resourceKey = ResourceKey.create(currentSegmentUuid, currentSequenceUuid, currentSequenceType);
        contextProvider.getResourceContext().initResource(resourceKey, resourceId);

        // 2. Init essence parameter in Edit Units (as defined in CPL)
        // Check that we have a corresponding track file in assetmap
        // asset map already contains full absolute paths
        UUID trackId = UUID.create(trackFileResource.getTrackFileId());
        String assetPath = assetMap.getAsset(trackId);
        if (assetPath == null) {
            throw new ConversionException(String.format(
                    "Resource track file '%s' isn't present in assetmap.xml", trackId));
        }
        contextProvider.getResourceContext().addResourceParameter(resourceKey, resourceId,
                ResourceContextParameters.ESSENCE, assetPath);

        // 3. init edit rate parameter
        BigFraction editRate = ((trackFileResource.getEditRate() != null) && !trackFileResource.getEditRate().isEmpty())
                ? ConversionHelper.parseEditRate(trackFileResource.getEditRate()) : compositionEditRate;
        contextProvider.getResourceContext().addResourceParameter(resourceKey, resourceId,
                ResourceContextParameters.EDIT_RATE, ConversionHelper.toEditRate(editRate));

        // 4. Init startTime parameter
        BigInteger startTimeEditUnit = trackFileResource.getEntryPoint() != null
                ? trackFileResource.getEntryPoint() : BigInteger.valueOf(0);
        contextProvider.getResourceContext().addResourceParameter(resourceKey, resourceId,
                ResourceContextParameters.START_TIME_EDIT_UNIT, startTimeEditUnit.toString());

        // 5. init duration parameter
        BigInteger durationEditUnit;
        if (trackFileResource.getSourceDuration() != null) {
            durationEditUnit = trackFileResource.getSourceDuration();
        } else {
            durationEditUnit = trackFileResource.getIntrinsicDuration().subtract(startTimeEditUnit);
        }
        contextProvider.getResourceContext().addResourceParameter(resourceKey, resourceId,
                ResourceContextParameters.DURATION_EDIT_UNIT, durationEditUnit.toString());

        // 6. init endTime parameter
        BigInteger endTimeEditUnit = startTimeEditUnit.add(durationEditUnit);
        contextProvider.getResourceContext().addResourceParameter(resourceKey, resourceId,
                ResourceContextParameters.END_TIME_EDIT_UNIT, endTimeEditUnit.toString());

        // 7. init total repeat count parameter
        BigInteger repeatCount = trackFileResource.getRepeatCount() != null
                ? trackFileResource.getRepeatCount() : BigInteger.ONE;
        contextProvider.getResourceContext().addResourceParameter(resourceKey, resourceId,
                ResourceContextParameters.REPEAT_COUNT, repeatCount.toString());
    }

}
