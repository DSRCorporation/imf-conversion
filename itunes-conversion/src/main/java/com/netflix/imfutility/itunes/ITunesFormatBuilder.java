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
import com.netflix.imfutility.conversion.ConversionEngine;
import com.netflix.imfutility.conversion.templateParameter.context.DestTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.DynamicTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters.ASPECT_RATIO;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.generated.itunes.metadata.ChapterInputType;
import com.netflix.imfutility.generated.itunes.metadata.LocaleType;
import com.netflix.imfutility.generated.mediainfo.FfprobeType;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_ADDITIONAL_AUDIO_COUNT;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_ADDITIONAL_AUDIO_PREFIX;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_ADDITIONAL_AUDIO_TRACKS_PREFIX;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_MAIN_AUDIO;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_MAIN_AUDIO_TRACKS;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_PAN_PARAMETER_PREFIX;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_PARAM_OUTPUT_ITMSP;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_PARAM_TRAILER_MEDIAINFO_INPUT;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_PARAM_TRAILER_MEDIAINFO_OUTPUT;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_PARAM_VENDOR_ID;
import com.netflix.imfutility.itunes.asset.ChapterAssetProcessor;
import com.netflix.imfutility.itunes.asset.PosterAssetProcessor;
import com.netflix.imfutility.itunes.asset.TrailerAssetProcessor;
import com.netflix.imfutility.itunes.destcontext.DestContextResolveStrategy;
import com.netflix.imfutility.itunes.destcontext.InputDestContextResolveStrategy;
import com.netflix.imfutility.itunes.destcontext.NameDestContextResolveStrategy;
import com.netflix.imfutility.itunes.inputparameters.ITunesInputParameters;
import com.netflix.imfutility.itunes.inputparameters.ITunesInputParametersValidator;
import com.netflix.imfutility.itunes.mediainfo.SimpleMediaInfoBuilder;
import com.netflix.imfutility.itunes.xmlprovider.AudioMapXmlProvider;
import com.netflix.imfutility.itunes.xmlprovider.ChaptersXmlProvider;
import com.netflix.imfutility.itunes.xmlprovider.MetadataXmlProvider;
import com.netflix.imfutility.mediainfo.MediaInfoException;
import com.netflix.imfutility.util.ConversionHelper;
import com.netflix.imfutility.xml.XmlParsingException;
import com.netflix.imfutility.xsd.conversion.DestContextTypeMap;
import com.netflix.imfutility.xsd.conversion.DestContextsTypeMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.math3.fraction.BigFraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * iTunes format builder (see {@link AbstractFormatBuilder}). It's used for conversion to iTunes format ('convert' iTunes mode).
 */
public class ITunesFormatBuilder extends AbstractFormatBuilder {

    private final Logger logger = LoggerFactory.getLogger(ITunesFormatBuilder.class);

    private final ITunesInputParameters iTunesInputParameters;
    private File itmspDir;
    private MetadataXmlProvider metadataXmlProvider;
    private ChaptersXmlProvider chaptersXmlProvider;

    public ITunesFormatBuilder(ITunesInputParameters inputParameters) {
        super(new ITunesFormat(), inputParameters);
        this.iTunesInputParameters = inputParameters;
    }

    @Override
    protected void doValidateCmdLineArguments() {
        ITunesInputParametersValidator.validateCmdLineArguments(iTunesInputParameters);
    }

    @Override
    protected void doBuildDynamicContextPreCpl() {
        DynamicTemplateParameterContext dynamicContext = contextProvider.getDynamicContext();
        // fill vendorId parameter
        String vendorId = iTunesInputParameters.getCmdLineArgs().getVendorId();
        dynamicContext.addParameter(DYNAMIC_PARAM_VENDOR_ID, vendorId);
        // fill output parameter to [vendor-id].itmsp
        String itmspName = vendorId + ".itmsp";
        dynamicContext.addParameter(DYNAMIC_PARAM_OUTPUT_ITMSP, itmspName, false);
        setOSParameters();
    }

    @Override
    protected void doBuildDynamicContextPostCpl() throws IOException, XmlParsingException {
        // parse audiomap and add audio parameters if audio exist
        if (contextProvider.getSequenceContext().getSequenceCount(SequenceType.AUDIO) > 0) {
            parseAudioMapAndAddParameters(iTunesInputParameters.getAudiomapFile(), contextProvider);
            logger.info("AudioMap XML has been parsed sucessfully.");
        }
    }

    @Override
    protected void preConvert() throws IOException, XmlParsingException {
        // 1. creating [vendor-id].itmsp output directory
        createItmspDir();

        // 2. load, parse and validate metadata.xml
        loadMetadata();

        // 3. process additional assets (poster, chapters, trailer)
        processAdditionalAssets();
    }

    @Override
    protected void postConvert() throws IOException, XmlParsingException {
    }

    @Override
    protected String getConversionConfiguration() {
        return conversionProvider.getConvertConfiguration().get(0);
    }

    @Override
    protected DestContextTypeMap getDestContextMap(DestContextsTypeMap destContexts) {
        String format = iTunesInputParameters.getCmdLineArgs().getFormat();

        DestContextResolveStrategy resolveStrategy = format != null
                ? new NameDestContextResolveStrategy(format)
                : new InputDestContextResolveStrategy(contextProvider);
        return resolveStrategy.resolveContext(destContexts);
    }

    private void createItmspDir() {
        DynamicTemplateParameterContext dynamicContext = contextProvider.getDynamicContext();
        String itmspName = dynamicContext.getParameterValueAsString(DYNAMIC_PARAM_OUTPUT_ITMSP);

        logger.info("Creating {} output directory...", itmspName);

        itmspDir = new File(contextProvider.getWorkingDir(), itmspName);
        logger.info("Itmsp output directory: {}", itmspDir);
        if (!itmspDir.mkdir()) {
            throw new ConversionException(String.format(
                    "Couldn't create %s output directory!", itmspName));
        }

        logger.info("Created {} output directory: OK\n", itmspName);
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

    private void parseAudioMapAndAddParameters(File audiomapFile, TemplateParameterContextProvider contextProvider)
            throws XmlParsingException, FileNotFoundException {

        int[] i = {0};
        AudioMapXmlProvider audioMapXmlProvider = new AudioMapXmlProvider(audiomapFile, contextProvider);

        // add dynamic parameters
        // mainAudio
        contextProvider.getDynamicContext().addParameter(DYNAMIC_MAIN_AUDIO,
                audioMapXmlProvider.getMainAudioFileName());

        // mainAudioTracks
        contextProvider.getDynamicContext().addParameter(DYNAMIC_MAIN_AUDIO_TRACKS,
                String.valueOf(audioMapXmlProvider.getMainAudioTracks()));

        // additionalAudioCount
        contextProvider.getDynamicContext().addParameter(DYNAMIC_ADDITIONAL_AUDIO_COUNT,
                String.valueOf(audioMapXmlProvider.getAdditionalAudioCount()));

        // additionalAudioTracks%{i}
        i[0] = 0;
        audioMapXmlProvider.getAdditionalAudioTracks().stream().forEach((p) -> {
            contextProvider.getDynamicContext().addParameter(DYNAMIC_ADDITIONAL_AUDIO_TRACKS_PREFIX + i[0],
                    p.toString());
            i[0]++;
        });

        // panParameter%{i}
        i[0] = 0;
        audioMapXmlProvider.getPanParameters().stream().forEach((p) -> {
            contextProvider.getDynamicContext().addParameter(DYNAMIC_PAN_PARAMETER_PREFIX + i[0], p);
            i[0]++;
        });

        // additionalAudio%{i}
        i[0] = 0;
        audioMapXmlProvider.getAdditionalAudioFileNames().stream().forEach((p) -> {
            contextProvider.getDynamicContext().addParameter(DYNAMIC_ADDITIONAL_AUDIO_PREFIX + i[0], p);
            i[0]++;
        });
    }

    private void processAdditionalAssets() throws XmlParsingException, IOException {
        processPoster();
        processTrailer();
        processChapters();
    }

    private void processPoster() throws IOException {
        File poster = iTunesInputParameters.getPosterFile();
        if (poster == null) {
            return;
        }

        new PosterAssetProcessor(metadataXmlProvider, itmspDir)
                .setVendorId(iTunesInputParameters.getCmdLineArgs().getVendorId())
                .process(poster);
    }

    private void processChapters() throws XmlParsingException, IOException {
        File chaptersFile = iTunesInputParameters.getChaptersFile();
        if (chaptersFile == null) {
            return;
        }

        chaptersXmlProvider = new ChaptersXmlProvider(chaptersFile);

        metadataXmlProvider.appendChaptersTimeCode(chaptersXmlProvider.getChapters().getTimecodeFormat());

        ChapterAssetProcessor processor = new ChapterAssetProcessor(metadataXmlProvider, itmspDir)
                .setAspectRatio(getDestAspectRatio());

        int i = 1;
        for (ChapterInputType chapter : chaptersXmlProvider.getChapters().getChapter()) {
            processor.setInputChapter(chapter)
                    .setChapterIndex(i)
                    .process(chaptersXmlProvider.getChapterFile(chapter));
            i++;
        }
    }

    private void processTrailer() throws XmlParsingException, IOException {
        File trailer = iTunesInputParameters.getTrailerFile();
        if (trailer == null) {
            return;
        }

        new TrailerAssetProcessor(metadataXmlProvider, itmspDir)
                .setVendorId(iTunesInputParameters.getCmdLineArgs().getVendorId())
                .setFormat(getTrailerMediaInfo(trailer).getFormat())
                .setLocale(getDefaultLocale())
                .process(trailer);
    }

    private FfprobeType getTrailerMediaInfo(File trailer) throws XmlParsingException, IOException {
        try {
            return new SimpleMediaInfoBuilder(contextProvider, new ConversionEngine().getExecuteStrategyFactory())
                    .setCommandName("trailer")
                    .setInputDynamicParam(DYNAMIC_PARAM_TRAILER_MEDIAINFO_INPUT)
                    .setOutputDynamicParam(DYNAMIC_PARAM_TRAILER_MEDIAINFO_OUTPUT)
                    .build(trailer);
        } catch (MediaInfoException e) {
            throw new ConversionException("Conversion aborted cause of MediaInfo failures", e);
        }
    }

    private BigFraction getDestAspectRatio() {
        DestTemplateParameterContext destContext = contextProvider.getDestContext();
        return ConversionHelper.parseAspectRatio(destContext.getParameterValue(ASPECT_RATIO));
    }

    private LocaleType getDefaultLocale() {
        LocaleType locale = new LocaleType();
        locale.setName(iTunesInputParameters.getCmdLineArgs().getFallbackLocale() != null
                ? iTunesInputParameters.getCmdLineArgs().getFallbackLocale()
                : metadataXmlProvider.getLanguage());
        return locale;
    }
}
