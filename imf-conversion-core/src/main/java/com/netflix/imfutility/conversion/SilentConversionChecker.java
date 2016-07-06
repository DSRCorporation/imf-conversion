package com.netflix.imfutility.conversion;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfoBuilder;
import com.netflix.imfutility.conversion.templateParameter.context.SequenceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.config.*;
import com.netflix.imfutility.generated.conversion.*;

import java.util.Objects;

/**
 * Checks whether it's allowed (in config.xml) to silently convert source parameters to destination ones if they don't match.
 * Example: input video essence has 50 fps; destination fps (as defined in conversion.xml) is 25 fps.
 * If config.xml says that silent conversion is not allowed => {@link ConversionNotAllowedException} is thrown.
 * If config.xml says that silent conversion is allowed, then no exception is thrown, and the fps will be silently converted (for example, by FFMPEG)
 * as defined in conversion.xml.
 */
public class SilentConversionChecker {

    private final SequenceTemplateParameterContext sequenceContext;
    private final DestinationConversionParametersType destConversionParams;
    private final ConversionParametersType configConversionParams;

    public SilentConversionChecker(TemplateParameterContextProvider contextProvider, FormatConfigurationType formatConfiguration, ConfigType config) {
        this.sequenceContext = contextProvider.getSequenceContext();
        this.destConversionParams = formatConfiguration.getConversionParameters();
        this.configConversionParams = config.getConversionParameters();
    }

    /**
     * Whether it's allowed to silently convert source parameters to destination ones if they don't match.
     * {@link ConversionNotAllowedException} is thrown if it's not allowed.
     *
     * @throws ConversionNotAllowedException if there are mismatched parameters,
     *                                       For example, source fps is 25, and the destination one (as defined by conversion.xml), is 50,
     *                                       and config.xml says that silent conversion of fps is not allowed.
     */
    public void check() throws ConversionNotAllowedException {
        if (destConversionParams == null) {
            return;
        }
        if (configConversionParams == null) {
            return;
        }

        for (SequenceType seqType : sequenceContext.getSequenceTypes()) {
            for (SequenceUUID seqUuid : sequenceContext.getUuids(seqType)) {
                switch (seqType) {
                    case AUDIO:
                        checkForSilentAudioConversion(destConversionParams.getAudio(), seqUuid);
                        break;
                    case VIDEO:
                        checkForSilentVideoConversion(destConversionParams.getVideo(), seqUuid);
                        break;
                    default:
                        // nothing for subtitle so far
                }
            }
        }

    }

    private void checkForSilentAudioConversion(DestinationAudioConversionParametersType audioConversionParams, SequenceUUID seqUuid) throws ConversionNotAllowedException {
        if (audioConversionParams == null) {
            return;
        }
        AudioConversionParametersType audio = configConversionParams.getAudio();
        if (audio == null) {
            return;
        }

        boolean allowBitSample = isAllow(audio.getBitsSample());
        boolean allowSampleRate = isAllow(audio.getSampleRate());

        ContextInfo contextInfo = new ContextInfoBuilder().setSequenceUuid(seqUuid).setSequenceType(SequenceType.AUDIO).build();
        if (!allowBitSample) {
            checkParameter(SequenceContextParameters.BITS_PER_SAMPLE, audioConversionParams.getBitsSample(), contextInfo);
        }
        if (!allowSampleRate) {
            checkParameter(SequenceContextParameters.SAMPLE_RATE, audioConversionParams.getSampleRate(), contextInfo);
        }
    }

    private void checkForSilentVideoConversion(DestinationVideoConversionParametersType videoConversionParams, SequenceUUID seqUuid) throws ConversionNotAllowedException {
        if (videoConversionParams == null) {
            return;
        }
        VideoConversionParametersType video = configConversionParams.getVideo();
        if (video == null) {
            return;
        }

        boolean allowFrameRate = isAllow(video.getFrameRate());
        boolean allowSize = isAllow(video.getSize());
        boolean allowPixelFmt = isAllow(video.getPixelFormat());
        boolean allowBitDepth = isAllow(video.getBitDepth());

        ContextInfo contextInfo = new ContextInfoBuilder().setSequenceUuid(seqUuid).setSequenceType(SequenceType.VIDEO).build();
        if (!allowFrameRate) {
            checkParameter(SequenceContextParameters.FRAME_RATE, videoConversionParams.getFrameRate(), contextInfo);
        }
        if (!allowBitDepth) {
            checkParameter(SequenceContextParameters.BIT_DEPTH, videoConversionParams.getBitDepth(), contextInfo);
        }
        if (!allowPixelFmt) {
            checkParameter(SequenceContextParameters.PIXEL_FORMAT, videoConversionParams.getPixelFormat(), contextInfo);
        }
        if (!allowSize) {
            checkParameter(SequenceContextParameters.WIDTH, videoConversionParams.getWidth(), contextInfo);
            checkParameter(SequenceContextParameters.HEIGHT, videoConversionParams.getHeight(), contextInfo);
        }

    }

    private void checkParameter(SequenceContextParameters param, String destinationParamValue, ContextInfo contextInfo) throws ConversionNotAllowedException {
        if (destinationParamValue == null || destinationParamValue.isEmpty()) {
            return;
        }

        String paramValue = sequenceContext.getParameterValue(param, contextInfo);
        if (!Objects.equals(paramValue, destinationParamValue)) {
            throw new ConversionNotAllowedException(param.getName(), paramValue, destinationParamValue, contextInfo.getSequenceUuid());
        }

    }

    private boolean isAllow(AllowDisallow allowDisallow) {
        return (allowDisallow == null) || (allowDisallow == AllowDisallow.YES);
    }

}
