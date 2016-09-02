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
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfoBuilder;
import com.netflix.imfutility.conversion.templateParameter.context.DestTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.DynamicTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.SequenceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.generated.itunes.chapters.InputChapterItem;
import com.netflix.imfutility.generated.mediainfo.FfprobeType;
import com.netflix.imfutility.itunes.asset.AssetValidationException;
import com.netflix.imfutility.itunes.asset.AudioAssetProcessor;
import com.netflix.imfutility.itunes.asset.CaptionsAssetProcessor;
import com.netflix.imfutility.itunes.asset.ChapterAssetProcessor;
import com.netflix.imfutility.itunes.asset.PosterAssetProcessor;
import com.netflix.imfutility.itunes.asset.SourceAssetProcessor;
import com.netflix.imfutility.itunes.asset.SubtitlesAssetProcessor;
import com.netflix.imfutility.itunes.asset.TrailerAssetProcessor;
import com.netflix.imfutility.itunes.audiomap.AudioMapXmlProvider;
import com.netflix.imfutility.itunes.audiomap.AudioMapXmlProvider.AudioOption;
import com.netflix.imfutility.itunes.chapters.ChaptersXmlProvider;
import com.netflix.imfutility.itunes.destcontext.DestContextResolveStrategy;
import com.netflix.imfutility.itunes.destcontext.InputDestContextResolveStrategy;
import com.netflix.imfutility.itunes.destcontext.NameDestContextResolveStrategy;
import com.netflix.imfutility.itunes.inputparameters.ITunesInputParameters;
import com.netflix.imfutility.itunes.inputparameters.ITunesInputParametersValidator;
import com.netflix.imfutility.itunes.locale.LocaleHelper;
import com.netflix.imfutility.itunes.locale.LocaleValidator;
import com.netflix.imfutility.itunes.mediainfo.SimpleMediaInfoBuilder;
import com.netflix.imfutility.itunes.metadata.MetadataXmlProvider;
import com.netflix.imfutility.itunes.metadata.factory.MetadataXmlProviderFactory;
import com.netflix.imfutility.mediainfo.MediaInfoException;
import com.netflix.imfutility.util.ConversionHelper;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import com.netflix.imfutility.xsd.conversion.DestContextTypeMap;
import com.netflix.imfutility.xsd.conversion.DestContextsTypeMap;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.math3.fraction.BigFraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3.ns.ttml.TtEltype;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.IntStream;

import static com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters.ASPECT_RATIO;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters.DAR;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters.FRAME_RATE;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters.INTERLACED;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters.SAMPLE_RATE;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters.LANGUAGE;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DEFAULT_LOCALE;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DEST_PARAM_AUDIO_SAMPLES_PER_FRAME;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DEST_PARAM_VIDEO_END_BLACK_FRAME_COUNT;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DEST_PARAM_VIDEO_IFRAME_RATE;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DEST_PARAM_VIDEO_IS_DAR_SPECIFIED;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_ADDITIONAL_AUDIO_COUNT;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_ADDITIONAL_AUDIO_PREFIX;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_ADDITIONAL_AUDIO_TRACKS_PREFIX;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_MAIN_AUDIO;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_MAIN_AUDIO_TRACKS;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_PAN_PARAMETER_PREFIX;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_PARAM_AUDIO_SILENCE_EXPR_PREFIX;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_PARAM_DEST_SOURCE;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_PARAM_IS_OSX;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_PARAM_OUTPUT_ITMSP;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_PARAM_SUBTITLE_IS_CPL_SUB;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_PARAM_SUBTITLE_IS_INPUT_PARAM_SUB;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_PARAM_SUBTITLE_ITT_PREFIX;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_PARAM_SUBTITLE_TTML_COUNT;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_PARAM_SUBTITLE_TTML_PREFIX;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_PARAM_TRAILER_MEDIAINFO_COMMAND;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_PARAM_TRAILER_MEDIAINFO_INPUT;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_PARAM_TRAILER_MEDIAINFO_OUTPUT;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_PARAM_TTML_TO_ITT;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.DYNAMIC_PARAM_VENDOR_ID;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.TTML_PACKAGES;
import static com.netflix.imfutility.itunes.ITunesConversionConstants.TTML_SCHEMA;

/**
 * iTunes format builder (see {@link AbstractFormatBuilder}). It's used for conversion to iTunes format ('convert' iTunes mode).
 */
public class ITunesFormatBuilder extends AbstractFormatBuilder {

    private final Logger logger = LoggerFactory.getLogger(ITunesFormatBuilder.class);

    private final ITunesInputParameters iTunesInputParameters;
    private File itmspDir;
    private MetadataXmlProvider metadataXmlProvider;
    private AudioMapXmlProvider audioMapXmlProvider;

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

        //  fill ttml-to-itt tool parameter
        dynamicContext.addParameter(DYNAMIC_PARAM_TTML_TO_ITT, iTunesInputParameters.getTtmlToIttTool());

        // fill vendorId parameter
        String vendorId = iTunesInputParameters.getCmdLineArgs().getVendorId();
        dynamicContext.addParameter(DYNAMIC_PARAM_VENDOR_ID, vendorId);

        // fill output package parameter to [vendor-id].itmsp
        String itmspName = vendorId + ".itmsp";
        dynamicContext.addParameter(DYNAMIC_PARAM_OUTPUT_ITMSP, itmspName, false);

        // fill destination main source path
        String destSource = itmspName + File.separator + vendorId + "-source.mov";
        dynamicContext.addParameter(DYNAMIC_PARAM_DEST_SOURCE, destSource, false);

        // fill subtitle input source
        boolean isCplSub = iTunesInputParameters.getSubFiles() == null;
        dynamicContext.addParameter(DYNAMIC_PARAM_SUBTITLE_IS_CPL_SUB, Boolean.toString(isCplSub));
        dynamicContext.addParameter(DYNAMIC_PARAM_SUBTITLE_IS_INPUT_PARAM_SUB, Boolean.toString(!isCplSub));

        setOSParameters();
    }

    @Override
    protected void doBuildDynamicContextPostCpl() throws IOException, XmlParsingException {
        // load, parse and validate metadata.xml (or generate default)
        initMetadata();

        // load, parse and validate audiomap.xml (or generate default) and add audio parameters if audio exist
        if (contextProvider.getSequenceContext().getSequenceCount(SequenceType.AUDIO) > 0) {
            initAudioMap();

            buildAudiomapParameters();
            buildSilenceExprParameters();
        }

        buildSubtitleInputParameters();

        resolveLocales();
    }

    @Override
    protected void preConvert() throws IOException, XmlParsingException {
        // creating [vendor-id].itmsp output directory
        createItmspDir();

        // process additional assets (poster, chapters, trailer)
        processAdditionalAssets();
    }

    @Override
    protected void postConvert() throws IOException, XmlParsingException {
        // process main source
        processMainSource();

        // process additional audios (if exists)
        processAdditionalAudios();

        // process subtitles (if exists)
        processSubtitles();


        metadataXmlProvider.saveMetadata(itmspDir);
    }

    @Override
    protected String getConversionConfiguration() {
        return conversionProvider.getConvertConfiguration().get(0);
    }

    @Override
    protected DestContextTypeMap getDestContextMap(DestContextsTypeMap destContexts) {
        logger.info("Resolving destination format...");

        String format = iTunesInputParameters.getCmdLineArgs().getFormat();
        ITunesPackageType packageType = metadataXmlProvider.getDescriptor().getPackageType();
        DestContextResolveStrategy resolveStrategy = format != null
                ? new NameDestContextResolveStrategy(format, packageType)
                : new InputDestContextResolveStrategy(contextProvider, packageType);
        DestContextTypeMap destContextMap = resolveStrategy.resolveContext(destContexts);

        logger.info("Destination format defined by {}", format != null
                ? "cmdline arg"
                : "source video");
        logger.info("Destination format: {}", destContextMap.getName());
        logger.info("Resolved destination format: OK\n");

        return destContextMap;
    }

    @Override
    protected void doBuildDestContext() {
        DestTemplateParameterContext destContext = contextProvider.getDestContext();

        // set interlaced to false if not specified
        String interlaced = destContext.getParameterValue(INTERLACED);
        destContext.addParameter(INTERLACED, interlaced == null ? Boolean.FALSE.toString() : interlaced);

        // define is dar provided
        destContext.addParameter(DEST_PARAM_VIDEO_IS_DAR_SPECIFIED, Boolean.toString(destContext.getParameterValue(DAR) != null));

        // set frame rate for interlaced scan
        // for ffmpeg iFrameRate=frameRate*2
        // for prenc iFrameRate=frameRate
        BigFraction iFrameRate = ConversionHelper.parseEditRate(destContext.getParameterValue(FRAME_RATE));
        if (!SystemUtils.IS_OS_MAC_OSX) {
            iFrameRate = iFrameRate.multiply(2);
        }
        destContext.addParameter(DEST_PARAM_VIDEO_IFRAME_RATE, ConversionHelper.toREditRate(iFrameRate));

        // set end black frame count
        // for ffmpeg count = 1 (if progressive), count = 2 (if interlace)
        // for prenc count = 1 always
        int count = 1;
        if (!SystemUtils.IS_OS_MAC_OSX) {
            count = Boolean.valueOf(interlaced) ? 2 : 1;
        }
        destContext.addParameter(DEST_PARAM_VIDEO_END_BLACK_FRAME_COUNT, String.valueOf(count));

        // set samplesPerFrame (for silence generation)
        BigFraction sampleRate = ConversionHelper.parseEditRate(destContext.getParameterValue(SAMPLE_RATE));
        BigFraction frameRate = ConversionHelper.parseEditRate(destContext.getParameterValue(FRAME_RATE));
        Long samplesPerFrame = sampleRate.divide(frameRate).longValue();
        destContext.addParameter(DEST_PARAM_AUDIO_SAMPLES_PER_FRAME, String.valueOf(samplesPerFrame));
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
        dynamicContext.addParameter(DYNAMIC_PARAM_IS_OSX, Boolean.toString(SystemUtils.IS_OS_MAC_OSX));
    }

    // Locales resolving

    private void resolveLocales() {
        boolean hasAudio = contextProvider.getSequenceContext().getSequenceCount(SequenceType.AUDIO) > 0;

        //  get locale from audiomap or metadata
        //  print warning (if both exist and differ)
        if (hasAudio) {

            if (metadataXmlProvider.isCustomized() && audioMapXmlProvider.isCustomized()) {
                if (!Objects.equals(metadataXmlProvider.getLocale(), audioMapXmlProvider.getLocale())) {
                    logger.warn("Locale set in metadata.xml doesn't match locale in audiomap.xml.");
                }
                return;
            }

            if (metadataXmlProvider.isCustomized()) {
                audioMapXmlProvider.setLocale(metadataXmlProvider.getLocale());
                return;
            }

            if (audioMapXmlProvider.isCustomized()) {
                metadataXmlProvider.setLocale(audioMapXmlProvider.getLocale());
                return;
            }
        }

        if (!hasAudio && metadataXmlProvider.isCustomized()) {
            return;
        }

        //  get locale from context or use fallback-locale, if can't get from context
        String locale = resolveContextLocale();
        if (locale == null) {
            locale = resolveFallbackLocale();
        }

        LocaleValidator.validateLocale(locale);

        metadataXmlProvider.setLocale(LocaleHelper.fromITunesLocale(locale));
        if (hasAudio) {
            audioMapXmlProvider.setLocale(LocaleHelper.fromITunesLocale(locale));
        }
    }

    private String resolveContextLocale() {
        SequenceTemplateParameterContext sequenceContext = contextProvider.getSequenceContext();
        return sequenceContext.getUuids(SequenceType.AUDIO).stream()
                .map(uuid -> new ContextInfoBuilder()
                        .setSequenceType(SequenceType.AUDIO)
                        .setSequenceUuid(uuid)
                        .build())
                .findFirst()
                .filter(contextInfo -> sequenceContext.hasSequenceParameter(LANGUAGE, contextInfo))
                .map(contextInfo -> sequenceContext.getParameterValue(LANGUAGE, contextInfo))
                .orElse(null);
    }

    private String resolveFallbackLocale() {
        String fallbackLocale = iTunesInputParameters.getCmdLineArgs().getFallbackLocale();
        return StringUtils.isBlank(fallbackLocale) ? DEFAULT_LOCALE : fallbackLocale;
    }

    private void initMetadata() throws IOException, XmlParsingException {
        File metadataFile = iTunesInputParameters.getMetadataFile();
        String vendorId = iTunesInputParameters.getCmdLineArgs().getVendorId();
        ITunesPackageType packageType = iTunesInputParameters.getCmdLineArgs().getPackageType();

        metadataXmlProvider = MetadataXmlProviderFactory.createProvider(metadataFile, packageType);
        metadataXmlProvider.updateVendorId(vendorId);
    }

    private void initAudioMap() throws IOException, XmlParsingException {
        File audiomapFile = iTunesInputParameters.getAudiomapFile();

        audioMapXmlProvider = new AudioMapXmlProvider(audiomapFile, contextProvider);
    }

    //  Audio processing

    private void buildAudiomapParameters() throws XmlParsingException, FileNotFoundException {
        int[] i = {0};

        // add dynamic parameters
        // mainAudio
        contextProvider.getDynamicContext().addParameter(DYNAMIC_MAIN_AUDIO,
                audioMapXmlProvider.getMainAudioFileName(), true);

        // mainAudioTracks
        contextProvider.getDynamicContext().addParameter(DYNAMIC_MAIN_AUDIO_TRACKS,
                String.valueOf(audioMapXmlProvider.getMainAudioTracks()));

        // additionalAudioCount
        contextProvider.getDynamicContext().addParameter(DYNAMIC_ADDITIONAL_AUDIO_COUNT,
                String.valueOf(audioMapXmlProvider.getAdditionalAudioCount()));

        // additionalAudioTracks%{i}
        i[0] = 0;
        audioMapXmlProvider.getAdditionalAudioTracks().forEach((p) -> {
            contextProvider.getDynamicContext().addParameter(DYNAMIC_ADDITIONAL_AUDIO_TRACKS_PREFIX + i[0],
                    p.toString());
            i[0]++;
        });

        // panParameter%{i}
        i[0] = 0;
        audioMapXmlProvider.getPanParameters().forEach((p) -> {
            contextProvider.getDynamicContext().addParameter(DYNAMIC_PAN_PARAMETER_PREFIX + i[0], p);
            i[0]++;
        });

        // additionalAudio%{i}
        i[0] = 0;
        audioMapXmlProvider.getAdditionalAudioFileNames().forEach((p) -> {
            contextProvider.getDynamicContext().addParameter(DYNAMIC_ADDITIONAL_AUDIO_PREFIX + i[0], p);
            i[0]++;
        });
    }

    private void buildSilenceExprParameters() {
        contextProvider.getSequenceContext().getUuids(SequenceType.AUDIO).forEach(this::buildSilenceExprParameter);
    }

    private void buildSilenceExprParameter(SequenceUUID seqUuid) {
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSequenceType(SequenceType.AUDIO)
                .setSequenceUuid(seqUuid)
                .build();

        SequenceTemplateParameterContext sequenceContext = contextProvider.getSequenceContext();
        String seqNum = sequenceContext.getParameterValue(SequenceContextParameters.NUM, contextInfo);
        String channelsNum = sequenceContext.getParameterValue(SequenceContextParameters.CHANNELS_NUM, contextInfo);

        contextProvider.getDynamicContext().addParameter(DYNAMIC_PARAM_AUDIO_SILENCE_EXPR_PREFIX + seqNum,
                computeSilenceExprParameter(channelsNum));
    }

    private String computeSilenceExprParameter(String channelsNum) {
        return StringUtils.repeat("0:", Integer.parseInt(channelsNum));
    }

    //  Subtitle processing

    private void buildSubtitleInputParameters() {
        if (iTunesInputParameters.getSubFiles() == null) {
            return;
        }

        iTunesInputParameters.getSubFiles().forEach(this::buildSubtitleInputParameter);

        int count = iTunesInputParameters.getSubFiles().size();
        contextProvider.getDynamicContext().addParameter(DYNAMIC_PARAM_SUBTITLE_TTML_COUNT, String.valueOf(count));
    }

    private void buildSubtitleInputParameter(File subtitles) {
        int num = iTunesInputParameters.getSubFiles().indexOf(subtitles);
        contextProvider.getDynamicContext().addParameter(DYNAMIC_PARAM_SUBTITLE_TTML_PREFIX + num, subtitles.getAbsolutePath());
    }

    //  Asset processing

    //  Additional assets (poster, trailer, chapters)

    private void processAdditionalAssets() throws XmlParsingException, IOException {
        processTrailer();
        processPoster();
        processChapters();
        processCaptions();
    }

    private void processPoster() throws IOException {
        File poster = iTunesInputParameters.getPosterFile();
        if (poster == null) {
            return;
        }

        new PosterAssetProcessor(metadataXmlProvider, itmspDir)
                .setVendorId(iTunesInputParameters.getCmdLineArgs().getVendorId())
                .setLocale(metadataXmlProvider.getLocale())
                .process(poster);
    }

    private void processChapters() throws XmlParsingException, IOException {
        File chaptersFile = iTunesInputParameters.getChaptersFile();
        if (chaptersFile == null) {
            return;
        }

        ChaptersXmlProvider chaptersXmlProvider = new ChaptersXmlProvider(chaptersFile);

        metadataXmlProvider.appendChaptersTimeCode(chaptersXmlProvider.getTimecodeFormat());

        ChapterAssetProcessor processor = new ChapterAssetProcessor(metadataXmlProvider, itmspDir)
                .setAspectRatio(getDestAspectRatio());

        int i = 1;
        for (InputChapterItem chapter : chaptersXmlProvider.getChapters()) {
            processor.setInputChapterItem(chapter)
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
                .setLocale(metadataXmlProvider.getLocale())
                .process(trailer);
    }

    private void processCaptions() throws IOException {
        File captions = iTunesInputParameters.getCcFile();
        if (captions == null) {
            if (Objects.equals(metadataXmlProvider.getLocale(), Locale.US)) {
                logger.warn("Closed captions are required for US deliveries.");
            }
            return;
        }

        new CaptionsAssetProcessor(metadataXmlProvider, itmspDir)
                .setVendorId(iTunesInputParameters.getCmdLineArgs().getVendorId())
                // only US english captions currently allowed
                .setLocale(Locale.US)
                .process(captions);
    }

    //  Main source asset

    private void processMainSource() throws IOException {
        DynamicTemplateParameterContext dynamicContext = contextProvider.getDynamicContext();

        File destSource = new File(contextProvider.getWorkingDir(), dynamicContext.getParameterValueAsString(DYNAMIC_PARAM_DEST_SOURCE));

        new SourceAssetProcessor(metadataXmlProvider, itmspDir)
                .setLocale(audioMapXmlProvider.getLocale())
                .process(destSource);
    }

    //  Additional audio assets

    private void processAdditionalAudios() {
        if (audioMapXmlProvider == null) {
            return;
        }

        audioMapXmlProvider.getAlternativesAudio().forEach(this::safeProcessAdditionalAudio);
    }

    private void safeProcessAdditionalAudio(AudioOption audioOption) {
        try {
            processAdditionalAudio(audioOption);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processAdditionalAudio(AudioOption audioOption) throws IOException {
        File audio = new File(inputParameters.getWorkingDirFile(), audioOption.getFileName());

        new AudioAssetProcessor(metadataXmlProvider, itmspDir)
                .setLocale(LocaleUtils.toLocale(audioOption.getLocale()))
                .process(audio);
    }

    // Subtitles processing

    private void processSubtitles() {
        int count = iTunesInputParameters.getSubFiles() == null
                ? contextProvider.getSequenceContext().getSequenceCount(SequenceType.SUBTITLE)
                : iTunesInputParameters.getSubFiles().size();

        if (count == 0) {
            return;
        }

        IntStream.range(0, count)
                .mapToObj(i -> contextProvider.getDynamicContext().getParameterValueAsString(DYNAMIC_PARAM_SUBTITLE_ITT_PREFIX + i))
                .map(fileName -> new File(inputParameters.getWorkingDirFile(), fileName))
                .forEach(this::safeProcessSubtitles);
    }

    private void safeProcessSubtitles(File subtitles) {
        try {
            processSubtitles(subtitles);
        } catch (AssetValidationException e) {
            logger.warn("Subtitles processing failed: {}", e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processSubtitles(File subtitles) throws IOException {
        new SubtitlesAssetProcessor(metadataXmlProvider, itmspDir)
                .setLocale(getLocaleFromTtml(subtitles))
                .process(subtitles);
    }

    /**
     * Get iTunes locale based on lang extracted from subtitles itt ot ttml file.
     *
     * @param subtitles - a subtitles file (itt or ttml).
     * @return iTunes locale associated with input subtitles.
     * @throws IOException
     */
    private Locale getLocaleFromTtml(File subtitles) throws IOException {
        try {
            TtEltype ttEl = XmlParser.parse(subtitles, new String[]{TTML_SCHEMA}, TTML_PACKAGES, TtEltype.class);
            return LocaleHelper.fromITunesLocale(ttEl.getLang());
        } catch (XmlParsingException e) {
            throw new ConversionException("Can't parse subtitles");
        }
    }

    /**
     * Get ffprobe media info for trailer.
     * For correct work &lt;mediaInfoCommandOthers&gt; must contain &lt;mediaInfoCommand&gt; with name 'trailer' in conversion.xml
     *
     * @param trailer - a trailer file.
     * @return media info for input trailer.
     * @throws XmlParsingException
     * @throws IOException
     */
    private FfprobeType getTrailerMediaInfo(File trailer) throws XmlParsingException, IOException {
        try {
            return new SimpleMediaInfoBuilder(contextProvider, new ConversionEngine().getExecuteStrategyFactory())
                    .setCommandName(DYNAMIC_PARAM_TRAILER_MEDIAINFO_COMMAND)
                    .setInputDynamicParam(DYNAMIC_PARAM_TRAILER_MEDIAINFO_INPUT)
                    .setOutputDynamicParam(DYNAMIC_PARAM_TRAILER_MEDIAINFO_OUTPUT)
                    .build(trailer);
        } catch (MediaInfoException e) {
            throw new ConversionException("Conversion aborted cause of MediaInfo failures", e);
        }
    }

    /**
     * Get dest context aspect ratio and convert to fraction.
     *
     * @return fraction corresponds to destination aspect ratio
     */
    private BigFraction getDestAspectRatio() {
        DestTemplateParameterContext destContext = contextProvider.getDestContext();
        return ConversionHelper.parseAspectRatio(destContext.getParameterValue(ASPECT_RATIO));
    }
}
