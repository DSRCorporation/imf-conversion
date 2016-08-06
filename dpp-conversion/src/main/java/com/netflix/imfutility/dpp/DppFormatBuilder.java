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
package com.netflix.imfutility.dpp;

import com.netflix.imfutility.AbstractFormatBuilder;
import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.conversion.templateParameter.context.DynamicTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.SequenceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.dpp.MetadataXmlProvider.DMFramework;
import com.netflix.imfutility.dpp.inputparameters.DppInputParameters;
import com.netflix.imfutility.dpp.inputparameters.DppInputParametersValidator;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.generated.dpp.metadata.AudioTrackLayoutDmAs11Type;
import com.netflix.imfutility.util.ConversionHelper;
import com.netflix.imfutility.util.CplHelper;
import com.netflix.imfutility.xml.XmlParsingException;
import com.netflix.imfutility.xsd.conversion.DestContextTypeMap;
import com.netflix.imfutility.xsd.conversion.DestContextsTypeMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.fraction.BigFraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static com.netflix.imfutility.dpp.DppConversionConstants.DYNAMIC_PARAM_AS11_CORE_FILE;
import static com.netflix.imfutility.dpp.DppConversionConstants.DYNAMIC_PARAM_AS11_SEGM_FILE;
import static com.netflix.imfutility.dpp.DppConversionConstants.DYNAMIC_PARAM_EBU_AUDIO_TRACKS;
import static com.netflix.imfutility.dpp.DppConversionConstants.DYNAMIC_PARAM_METADATA_XML;
import static com.netflix.imfutility.dpp.DppConversionConstants.DYNAMIC_PARAM_OUTPUT_MXF;
import static com.netflix.imfutility.dpp.DppConversionConstants.DYNAMIC_PARAM_PAN;
import static com.netflix.imfutility.dpp.DppConversionConstants.DYNAMIC_PARAM_TTML_TO_STL;
import static com.netflix.imfutility.dpp.DppConversionConstants.DYNAMIC_PARAM_UK_DPP_FILE;
import static com.netflix.imfutility.dpp.DppConversionConstants.DYNAMIC_PARAM_VALUE_OUTPUT_MXF;

/**
 * DPP format builder (see {@link AbstractFormatBuilder}). It's used for conversion to DPP format ('convert' DPP mode).
 */
public class DppFormatBuilder extends AbstractFormatBuilder {

    private final Logger logger = LoggerFactory.getLogger(DppFormatBuilder.class);

    private final DppInputParameters dppInputParameters;
    private MetadataXmlProvider metadataXmlProvider;

    public DppFormatBuilder(DppInputParameters dppInputParameters) {
        super(new DppFormat(), dppInputParameters);
        this.dppInputParameters = dppInputParameters;
    }

    @Override
    protected void doValidateCmdLineArguments() {
        DppInputParametersValidator.validateCmdLineArguments(dppInputParameters);
    }

    @Override
    protected void doBuildDynamicContextPreCpl() {
        DynamicTemplateParameterContext dynamicContext = contextProvider.getDynamicContext();
        logger.info("Output file name: '{}.mxf'.", getOutputName());
        dynamicContext.addParameter(DYNAMIC_PARAM_OUTPUT_MXF, getOutputName(), false);
        dynamicContext.addParameter(DYNAMIC_PARAM_TTML_TO_STL, dppInputParameters.getTtmlToStlTool());
        dynamicContext.addParameter(DYNAMIC_PARAM_METADATA_XML, dppInputParameters.getMetadataFile().getAbsolutePath());
    }

    private String getOutputName() {
        if (dppInputParameters.getOutputName() != null) {
            return dppInputParameters.getOutputName();
        }
        return DYNAMIC_PARAM_VALUE_OUTPUT_MXF;
    }

    @Override
    protected String getConversionConfiguration() {
        return conversionProvider.getConvertConfiguration().get(0);
    }

    @Override
    protected DestContextTypeMap getDestContextMap(DestContextsTypeMap destContexts) {
        // no multiple dest contexts allowed
        return null;
    }

    @Override
    protected void doBuildDynamicContextPostCpl() throws IOException, XmlParsingException {
        DynamicTemplateParameterContext dynamicContext = contextProvider.getDynamicContext();

        // 1. load metadata.xml
        metadataXmlProvider = new MetadataXmlProvider(dppInputParameters.getMetadataFile(),
                contextProvider.getWorkingDir());

        // 2. load audiomap.xml
        if (dppInputParameters.getAudiomapFile() == null) {
            logger.warn("No audiomap.xml specified as a command line argument. A default audiomap.xml will be generated.");
        }
        AudioTrackLayoutDmAs11Type audioTrackLayout = metadataXmlProvider.getDpp().getTechnical().getAudio().getAudioTrackLayout();
        AudioMapXmlProvider audioMapXmlProvider = new AudioMapXmlProvider(dppInputParameters.getAudiomapFile(),
                audioTrackLayout, contextProvider);

        // 3. fill audio map parameters
        dynamicContext.addParameter(DYNAMIC_PARAM_PAN, audioMapXmlProvider.getPanParameter());

        // 4. fill ebuAudioTracks parameter
        Integer audioTracksNum = audioMapXmlProvider.getEBUAudioTracks();
        dynamicContext.addParameter(DYNAMIC_PARAM_EBU_AUDIO_TRACKS, String.valueOf(audioTracksNum));

        // 5. fill bmx metadata files parameters
        metadataXmlProvider.createBmxDppParameterFiles();
        dynamicContext.addParameter(DYNAMIC_PARAM_UK_DPP_FILE,
                metadataXmlProvider.getBmxDppParameterFile(DMFramework.UKDPP).getAbsolutePath(), true);
        dynamicContext.addParameter(DYNAMIC_PARAM_AS11_CORE_FILE,
                metadataXmlProvider.getBmxDppParameterFile(DMFramework.AS11CORE).getAbsolutePath(), true);
        dynamicContext.addParameter(DYNAMIC_PARAM_AS11_SEGM_FILE,
                metadataXmlProvider.getBmxDppParameterFile(DMFramework.AS11Segmentation).getAbsolutePath(), true);

    }

    @Override
    protected void preConvert() throws IOException, XmlParsingException {
        // check that total duration specified in Metadata.xml matches total duration of the output file!
        // otherwise conversion will abort at the very last step (BMX). It may make the user unhappy after a long conversion.
        checkTotalDuration();
    }

    /**
     * Check that total duration specified in Metadata.xml is less than total duration of the output file.
     * Otherwise conversion will abort at the very last step (BMX). It may make the user unhappy after a long conversion.
     */
    private void checkTotalDuration() {
        String metadataTotalDurationTc = metadataXmlProvider.getDpp().getTechnical().getTimecodes().getTotalProgrammeDuration().getValue();
        if (StringUtils.isEmpty(metadataTotalDurationTc)) {
            return;
        }

        String destFps = contextProvider.getDestContext().getParameterValue(DestContextParameters.FRAME_RATE);
        if (destFps == null) {
            destFps = MetadataXmlProvider.DEST_FRAME_RATE;
        }
        BigFraction fps = ConversionHelper.parseEditRate(destFps);

        long metadataTotalDurationMs = ConversionHelper.smpteTimecodeToMilliSeconds(
                metadataTotalDurationTc, fps);
        long cplTotalDurationMs = getCplTotalDurationMs();

        // BMX accepts any total duration if zero timecode is specified in metadata.xml
        if (metadataTotalDurationMs == 0) {
            return;
        }

        if (metadataTotalDurationMs > cplTotalDurationMs) {
            throw new ConversionException(
                    String.format("A total programme duration as specified in metadata.xml (%s, %s ms) exceeds a "
                                    + "total duration of the output as defined by the CPL (%s, %s ms) ",
                            metadataTotalDurationTc, String.valueOf(metadataTotalDurationMs),
                            ConversionHelper.msToSmpteTimecode(cplTotalDurationMs, fps), String.valueOf(cplTotalDurationMs)));
        }
    }

    private long getCplTotalDurationMs() {
        // bmx uses the smallest video/audio duration as a total duration when it's not equal
        long result = Long.MAX_VALUE;
        SequenceTemplateParameterContext sequenceContext = contextProvider.getSequenceContext();
        for (SequenceType seqType : sequenceContext.getSequenceTypes()) {
            for (SequenceUUID seqUuid : sequenceContext.getUuids(seqType)) {
                long trackDuration = CplHelper.getVirtualTrackDurationMS(contextProvider, seqType, seqUuid);
                result = Math.min(result, trackDuration);
            }
        }
        return result;
    }

    @Override
    protected void postConvert() throws IOException, XmlParsingException {
        logger.info("Conversion output:");
        String fileName = getOutputName() + ".mxf";
        logger.info("   {}", new File(inputParameters.getWorkingDirFile(), fileName).getAbsoluteFile());
        int subtitleCount = contextProvider.getSequenceContext().getSequenceCount(SequenceType.SUBTITLE);
        if (subtitleCount == 1) {
            fileName = getOutputName() + ".stl";
            logger.info("   {}", new File(inputParameters.getWorkingDirFile(), fileName).getAbsoluteFile());
        } else {
            for (int i = 0; i < subtitleCount; i++) {
                fileName = getOutputName() + "-" + i + ".stl";
                logger.info("   {}", new File(inputParameters.getWorkingDirFile(), fileName).getAbsoluteFile());
            }
        }
    }
}
