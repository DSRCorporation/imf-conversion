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
package com.netflix.imfutility.itunes;

import com.netflix.imfutility.AbstractFormatBuilder;
import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.conversion.templateParameter.context.DynamicTemplateParameterContext;
import com.netflix.imfutility.itunes.inputparameters.ITunesInputParameters;
import com.netflix.imfutility.itunes.inputparameters.ITunesInputParametersValidator;
import com.netflix.imfutility.itunes.videoformat.VideoFormat;
import com.netflix.imfutility.itunes.videoformat.builder.ITunesVideoFormatBuilder;
import com.netflix.imfutility.itunes.videoformat.context.VideoFormatContextBuilder;
import com.netflix.imfutility.itunes.videoformat.context.VideoFormatContextHelper;
import com.netflix.imfutility.itunes.xmlprovider.MetadataXmlProvider;
import com.netflix.imfutility.xml.XmlParsingException;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_PARAM_OUTPUT_ITMSP;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_PARAM_VENDOR_ID;

/**
 * iTunes format builder (see {@link AbstractFormatBuilder}). It's used for conversion to iTunes format ('convert' iTunes mode).
 */
public class ITunesFormatBuilder extends AbstractFormatBuilder {

    private final Logger logger = LoggerFactory.getLogger(ITunesFormatBuilder.class);

    private final ITunesInputParameters iTunesInputParameters;
    private MetadataXmlProvider metadataXmlProvider;

    public ITunesFormatBuilder(ITunesInputParameters inputParameters) {
        super(new ITunesFormat(), inputParameters);
        this.iTunesInputParameters = inputParameters;
    }

    @Override
    protected void doValidateCmdLineArguments() {
        ITunesInputParametersValidator.validateCmdLineArguments(iTunesInputParameters);
    }

    @Override
    protected void doBuildDynamicContext() {
        DynamicTemplateParameterContext dynamicContext = contextProvider.getDynamicContext();
        // fill vendorId parameter
        String vendorId = iTunesInputParameters.getCmdLineArgs().getVendorId();
        dynamicContext.addParameter(DYNAMIC_PARAM_VENDOR_ID, vendorId);
        // fill output parameter to [vendor-id].itmsp
        String itmspName = vendorId + ".itmsp";
        dynamicContext.addParameter(DYNAMIC_PARAM_OUTPUT_ITMSP, itmspName, false);
    }

    @Override
    protected void preConvert() throws IOException, XmlParsingException {
        // 1. creating [vendor-id].itmsp output directory
        createItmspDir();

        // 2. set video format
        setVideoFormatDynamicContext();

        //  3. set OS parameters
        setOSParameters();

        // 4. load, parse and validate metadata.xml
        loadMetadata();
    }

    @Override
    protected void postConvert() throws IOException, XmlParsingException {

    }

    @Override
    protected String getConversionConfiguration() {
        return conversionProvider.getConvertConfiguration().get(0);
    }

    private void createItmspDir() {
        DynamicTemplateParameterContext dynamicContext = contextProvider.getDynamicContext();
        String itmspName = dynamicContext.getParameterValueAsString(DYNAMIC_PARAM_OUTPUT_ITMSP);

        logger.info("Creating {} output directory...", itmspName);

        File itmspDir = new File(contextProvider.getWorkingDir(), itmspName);
        logger.info("Itmsp output directory: {}", itmspDir);
        if (!itmspDir.mkdir()) {
            throw new ConversionException(String.format(
                    "Couldn't create %s output directory!", itmspName));
        }

        logger.info("Created {} output directory: OK\n", itmspName);
    }

    private void setVideoFormatDynamicContext() {
        DynamicTemplateParameterContext dynamicContext = contextProvider.getDynamicContext();

        VideoFormat videoFormat = iTunesInputParameters.getCmdLineArgs().getVideoFormat();
        if (videoFormat == null) {
            videoFormat = new VideoFormatContextBuilder(contextProvider, new ITunesVideoFormatBuilder()).build();
        }

        new VideoFormatContextHelper(dynamicContext).setVideoFormat(videoFormat);
    }

    private void setOSParameters() {
        DynamicTemplateParameterContext dynamicContext = contextProvider.getDynamicContext();
        dynamicContext.addParameter("isOSX", Boolean.toString(SystemUtils.IS_OS_MAC_OSX));
    }

    private void loadMetadata() throws IOException, XmlParsingException {
        File metadataFile = iTunesInputParameters.getMetadataFile();

        metadataXmlProvider = metadataFile == null
                ? new MetadataXmlProvider(inputParameters.getWorkingDirFile(), MetadataXmlProvider.generateSampleMetadata())
                : new MetadataXmlProvider(inputParameters.getWorkingDirFile(), metadataFile);
    }
}
