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
package com.netflix.imfutility.conversion;

import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.config.AllowDisallow;
import com.netflix.imfutility.generated.config.ConfigType;
import com.netflix.imfutility.generated.config.ConversionParameterNameType;
import com.netflix.imfutility.generated.config.ConversionParameterType;
import com.netflix.imfutility.generated.conversion.DestContextParamType;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import com.netflix.imfutility.xsd.config.ConversionParametersTypeMap;
import com.netflix.imfutility.xsd.conversion.DestContextTypeMap;
import org.junit.Test;

/**
 * Tests whether silent conversion is allowed depending on values from config.xml (allow/disallow for each parameter)
 * and from conversion.xml (destination parameter values).
 */
public class SilentConversionTest {

    @Test
    public void okConversionNotSpecifiedInBoth() throws Exception {
        // 1. create context with no conversion parameters specified
        DestContextTypeMap destContextMap = new DestContextMapBuilder().build();

        // 2. create config with no conversion parameters specified
        ConfigType config = new ConfigBuilder().build();

        // 3. create context
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator
                .createDefaultContextProviderWithDestContext(destContextMap);

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okConversionNotSpecifiedInConfigXml() throws Exception {
        // 1. create context with all parameters specified
        DestContextTypeMap destContextMap = new DestContextMapBuilder()
                .setBitsSample("24").setSampleRate("48000")
                .setBitDepth("10").setFrameRate("25").setHeight("1920").setWidth("1080").setPixelFmt("yuv422p10le")
                .build();

        // 2. create config with no conversion parameters specified
        ConfigType config = new ConfigBuilder().build();

        // 3. create context with mismatched parameters
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator
                .createDefaultContextProviderWithDestContext(destContextMap);
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.AUDIO, SequenceUUID.create("111"),
                SequenceContextParameters.BITS_PER_SAMPLE, "16");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.AUDIO, SequenceUUID.create("111"),
                SequenceContextParameters.SAMPLE_RATE, "96000");

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okConversionNotSpecifiedInDestinationParams() throws Exception {
        // 1. create context with no conversion parameters specified
        DestContextTypeMap destContextMap = new DestContextMapBuilder().build();

        // 2. create config with disallow mismatched for all
        ConfigType config = new ConfigBuilder()
                .setBitsSample(false).setSampleRate(false)
                .setFrameRate(false).setBitDepth(false).setSize(false).setPixelFmt(false)
                .build();

        // 3. create context
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator
                .createDefaultContextProviderWithDestContext(destContextMap);

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okContextEmpty() throws Exception {
        // 1. create context with all parameters specified
        DestContextTypeMap destContextMap = new DestContextMapBuilder()
                .setBitsSample("24").setSampleRate("48000")
                .setBitDepth("10").setFrameRate("25").setHeight("1920").setWidth("1080").setPixelFmt("yuv422p10le")
                .build();

        // 2. create config with disallow mismatched for all
        ConfigType config = new ConfigBuilder()
                .setBitsSample(false).setSampleRate(false)
                .setFrameRate(false).setBitDepth(false).setSize(false).setPixelFmt(false)
                .build();

        // 3. create empty context
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProviderWithDestContext(destContextMap);

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okAudioNotSpecifiedInConfigXml() throws Exception {
        // 1. create context with all parameters specified
        DestContextTypeMap destContextMap = new DestContextMapBuilder()
                .setBitsSample("24").setSampleRate("48000")
                .setBitDepth("10").setFrameRate("25").setHeight("1920").setWidth("1080").setPixelFmt("yuv422p10le")
                .build();

        // 2. create config with audio not specified
        ConfigType config = new ConfigBuilder()
                .setFrameRate(false).setBitDepth(false).setSize(false).setPixelFmt(false)
                .setNonNullAudio(false)
                .build();

        // 3. create context with audio specified and mismatched
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProviderWithDestContext(destContextMap);
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.AUDIO, SequenceUUID.create("111"),
                SequenceContextParameters.BITS_PER_SAMPLE, "20");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.AUDIO, SequenceUUID.create("111"),
                SequenceContextParameters.SAMPLE_RATE, "96000");

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okVideoNotSpecifiedInConfigXml() throws Exception {
        // 1. create context with all parameters specified
        DestContextTypeMap destContextMap = new DestContextMapBuilder()
                .setBitsSample("24").setSampleRate("48000")
                .setBitDepth("10").setFrameRate("25").setHeight("1920").setWidth("1080").setPixelFmt("yuv422p10le")
                .build();

        // 2. create config with video not specified
        ConfigType config = new ConfigBuilder()
                .setBitsSample(false).setSampleRate(false)
                .setNonNullVideo(false)
                .build();

        // 3. create context with video specified and mismatched
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProviderWithDestContext(destContextMap);
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("111"),
                SequenceContextParameters.FRAME_RATE, "30");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("111"),
                SequenceContextParameters.BIT_DEPTH, "8");

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okAudioNotSpecifiedInDestinationParams() throws Exception {
        // 1. create context with all parameters except audio specified
        DestContextTypeMap destContextMap = new DestContextMapBuilder()
                .setBitDepth("10").setFrameRate("25").setHeight("1920").setWidth("1080").setPixelFmt("yuv422p10le")
                .setNonNullAudio(false)
                .build();

        // 2. create config disallow mismatched for all
        ConfigType config = new ConfigBuilder()
                .setBitsSample(false).setSampleRate(true)
                .setFrameRate(false).setBitDepth(false).setSize(false).setPixelFmt(false)
                .build();

        // 3. create context with audio specified
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProviderWithDestContext(destContextMap);
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.AUDIO, SequenceUUID.create("111"),
                SequenceContextParameters.BITS_PER_SAMPLE, "16");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.AUDIO, SequenceUUID.create("111"),
                SequenceContextParameters.SAMPLE_RATE, "96000");

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okVideoNotSpecifiedInDestinationParams() throws Exception {
        // 1. create context with all parameters except video specified
        DestContextTypeMap destContextMap = new DestContextMapBuilder()
                .setBitsSample("24").setSampleRate("48000")
                .setNonNullVideo(false)
                .build();

        // 2. create config disallow mismatched
        ConfigType config = new ConfigBuilder()
                .setBitsSample(true).setSampleRate(true)
                .setFrameRate(true).setBitDepth(true).setSize(true).setPixelFmt(true)
                .build();

        // 3. create context with video specified
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProviderWithDestContext(destContextMap);
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("111"),
                SequenceContextParameters.BIT_DEPTH, "8");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("111"),
                SequenceContextParameters.FRAME_RATE, "50");

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okAudioParameterNotSpecifiedInDestinationParams() throws Exception {
        // 1. create context with sample rate not specified
        DestContextTypeMap destContextMap = new DestContextMapBuilder()
                .setSampleRate(null)
                .setNonNullAudio(true)
                .setNonNullVideo(true)
                .build();

        // 2. create config disallow mismatched sample rate
        ConfigType config = new ConfigBuilder()
                .setSampleRate(false)
                .build();

        // 3. create context with sample rate specified
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProviderWithDestContext(destContextMap);
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.AUDIO, SequenceUUID.create("111"),
                SequenceContextParameters.SAMPLE_RATE, "96000");

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okAudioParameterEmptyInDestinationParams() throws Exception {
        // 1. create context with sample rate is empty
        DestContextTypeMap destContextMap = new DestContextMapBuilder()
                .setSampleRate("")
                .setNonNullAudio(true)
                .setNonNullVideo(true)
                .build();

        // 2. create config disallow mismatched sample rate
        ConfigType config = new ConfigBuilder()
                .setSampleRate(false)
                .setNonNullAudio(true)
                .setNonNullVideo(true)
                .build();

        // 3. create context with sample rate specified
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProviderWithDestContext(destContextMap);
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.AUDIO, SequenceUUID.create("111"),
                SequenceContextParameters.SAMPLE_RATE, "96000");

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okVideoParameterNotSpecifiedInDestinationParams() throws Exception {
        // 1. create context with bit depth not specified
        DestContextTypeMap destContextMap = new DestContextMapBuilder()
                .setBitDepth(null)
                .setNonNullAudio(true)
                .setNonNullVideo(true)
                .build();

        // 2. create config disallow mismatched bit depth
        ConfigType config = new ConfigBuilder()
                .setBitDepth(false)
                .setNonNullAudio(true)
                .setNonNullVideo(true)
                .build();

        // 3. create context with bit depth specified
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProviderWithDestContext(destContextMap);
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("111"),
                SequenceContextParameters.BIT_DEPTH, "8");

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okVideoParameterEmptyInDestinationParams() throws Exception {
        // 1. create context with bit depth is empty
        DestContextTypeMap destContextMap = new DestContextMapBuilder()
                .setBitDepth("")
                .setNonNullAudio(true)
                .setNonNullVideo(true)
                .build();

        // 2. create config disallow mismatched bit depth
        ConfigType config = new ConfigBuilder()
                .setBitDepth(false)
                .setNonNullAudio(true)
                .setNonNullVideo(true)
                .build();

        // 3. create context with bit depth specified
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProviderWithDestContext(destContextMap);
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("111"),
                SequenceContextParameters.BIT_DEPTH, "8");

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okAudioParameterNotSpecifiedInConfigXml() throws Exception {
        // 1. create context with sample rate specified and mismatched
        DestContextTypeMap destContextMap = new DestContextMapBuilder()
                .setSampleRate("48000")
                .setNonNullAudio(true)
                .setNonNullVideo(true)
                .build();

        // 2. create config with sample rate not specified
        ConfigType config = new ConfigBuilder()
                .setSampleRate(null)
                .setNonNullAudio(true)
                .setNonNullVideo(true)
                .build();

        // 3. create context with mismatched sample rate
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProviderWithDestContext(destContextMap);
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.AUDIO, SequenceUUID.create("111"),
                SequenceContextParameters.SAMPLE_RATE, "96000");

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okVideoParameterNotSpecifiedInConfigXml() throws Exception {
        // 1. create context with bit depth specified and mismatched
        DestContextTypeMap destContextMap = new DestContextMapBuilder()
                .setBitDepth("10")
                .setNonNullAudio(true)
                .setNonNullVideo(true)
                .build();

        // 2. create config with bit depth specified
        ConfigType config = new ConfigBuilder()
                .setBitDepth(null)
                .setNonNullAudio(true)
                .setNonNullVideo(true)
                .build();

        // 3. create context with mismatched bit depth
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProviderWithDestContext(destContextMap);
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("111"),
                SequenceContextParameters.BIT_DEPTH, "8");

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test(expected = ConversionNotAllowedException.class)
    public void exceptionDisallowMismatchedAndMismatch() throws Exception {
        // 1. create context with all parameters specified and mismatched
        DestContextTypeMap destContextMap = new DestContextMapBuilder()
                .setBitsSample("24").setSampleRate("48000")
                .setBitDepth("10").setFrameRate("25").setHeight("1920").setWidth("1080").setPixelFmt("yuv422p10le")
                .build();

        // 2. create config that doesn't allow mismatched parameters
        ConfigType config = new ConfigBuilder()
                .setBitsSample(false).setSampleRate(false)
                .setFrameRate(false).setBitDepth(true).setSize(false).setPixelFmt(false)
                .build();

        // 3. create context with mismacthed parameters
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProviderWithDestContext(destContextMap);
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.AUDIO, SequenceUUID.create("111"),
                SequenceContextParameters.BITS_PER_SAMPLE, "32");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.AUDIO, SequenceUUID.create("111"),
                SequenceContextParameters.SAMPLE_RATE, "96000");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("112"),
                SequenceContextParameters.BIT_DEPTH, "8");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("112"),
                SequenceContextParameters.FRAME_RATE, "30");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("112"),
                SequenceContextParameters.HEIGHT, "720");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("112"),
                SequenceContextParameters.WIDTH, "540");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("112"),
                SequenceContextParameters.PIXEL_FORMAT, "yuv420p10le");

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okAllowMismatchedAndMismatch() throws Exception {
        // 1. create context with all parameters specified and mismatched
        DestContextTypeMap destContextMap = new DestContextMapBuilder()
                .setBitsSample("24").setSampleRate("48000")
                .setBitDepth("10").setFrameRate("25").setHeight("1920").setWidth("1080").setPixelFmt("yuv422p10le")
                .build();

        // 2. create config that allows mismatched parameters
        ConfigType config = new ConfigBuilder()
                .setBitsSample(true).setSampleRate(true)
                .setFrameRate(true).setBitDepth(true).setSize(true).setPixelFmt(true)
                .build();

        // 3. create context with mismatched parameters
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProviderWithDestContext(destContextMap);
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.AUDIO, SequenceUUID.create("111"),
                SequenceContextParameters.BITS_PER_SAMPLE, "32");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.AUDIO, SequenceUUID.create("111"),
                SequenceContextParameters.SAMPLE_RATE, "96000");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("112"),
                SequenceContextParameters.BIT_DEPTH, "8");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("112"),
                SequenceContextParameters.FRAME_RATE, "30");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("112"),
                SequenceContextParameters.HEIGHT, "720");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("112"),
                SequenceContextParameters.WIDTH, "540");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("112"),
                SequenceContextParameters.PIXEL_FORMAT, "yuv420p10le");

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okDisallowMismatchedAndMatch() throws Exception {
        // 1. create context with all parameters specified and matched
        DestContextTypeMap destContextMap = new DestContextMapBuilder()
                .setBitsSample("24").setSampleRate("48000")
                .setBitDepth("10").setFrameRate("25").setHeight("1920").setWidth("1080").setPixelFmt("yuv422p10le")
                .build();

        // 2. create config that doesn't allow mismatched parameters
        ConfigType config = new ConfigBuilder()
                .setBitsSample(false).setSampleRate(false)
                .setFrameRate(false).setBitDepth(false).setSize(false).setPixelFmt(false)
                .build();

        // 3. create context with matched parameters
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator
                .createDefaultContextProviderWithDestContext(destContextMap);
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.AUDIO, SequenceUUID.create("111"),
                SequenceContextParameters.BITS_PER_SAMPLE, "24");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.AUDIO, SequenceUUID.create("111"),
                SequenceContextParameters.SAMPLE_RATE, "48000");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("112"),
                SequenceContextParameters.BIT_DEPTH, "10");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("112"),
                SequenceContextParameters.FRAME_RATE, "25");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("112"),
                SequenceContextParameters.HEIGHT, "1920");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("112"),
                SequenceContextParameters.WIDTH, "1080");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("112"),
                SequenceContextParameters.PIXEL_FORMAT, "yuv422p10le");

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, config);

        // 5. check. no exception expected
        checker.check();
    }

    private static class DestContextMapBuilder {

        private String sampleRate;
        private String bitsSample;

        private String width;
        private String height;
        private String frameRate;
        private String pixelFmt;
        private String bitDepth;

        private boolean nonNullConversionParams = false;
        private boolean nonNullAudio = false;
        private boolean nonNullVideo = false;

        public DestContextMapBuilder setSampleRate(String sampleRate) {
            this.sampleRate = sampleRate;
            nonNullConversionParams = true;
            nonNullAudio = true;
            return this;
        }

        public DestContextMapBuilder setBitsSample(String bitsSample) {
            this.bitsSample = bitsSample;
            nonNullConversionParams = true;
            nonNullAudio = true;
            return this;
        }

        public DestContextMapBuilder setWidth(String width) {
            this.width = width;
            nonNullConversionParams = true;
            nonNullVideo = true;
            return this;
        }

        public DestContextMapBuilder setHeight(String height) {
            this.height = height;
            nonNullConversionParams = true;
            nonNullVideo = true;
            return this;
        }

        public DestContextMapBuilder setFrameRate(String frameRate) {
            this.frameRate = frameRate;
            nonNullConversionParams = true;
            nonNullVideo = true;
            return this;
        }

        public DestContextMapBuilder setPixelFmt(String pixelFmt) {
            this.pixelFmt = pixelFmt;
            nonNullConversionParams = true;
            nonNullVideo = true;
            return this;
        }

        public DestContextMapBuilder setBitDepth(String bitDepth) {
            this.bitDepth = bitDepth;
            nonNullConversionParams = true;
            nonNullVideo = true;
            return this;
        }

        public DestContextMapBuilder setNonNullConversionParams(Boolean nonNullConversionParams) {
            this.nonNullConversionParams = nonNullConversionParams;
            return this;
        }

        public DestContextMapBuilder setNonNullAudio(Boolean nonNullAudio) {
            this.nonNullAudio = nonNullAudio;
            nonNullConversionParams = true;
            return this;
        }

        public DestContextMapBuilder setNonNullVideo(Boolean nonNullVideo) {
            this.nonNullVideo = nonNullVideo;
            nonNullConversionParams = true;
            return this;
        }

        public DestContextTypeMap build() {
            DestContextTypeMap map = new DestContextTypeMap();

            if (!nonNullConversionParams) {
                return map;
            }
            if (nonNullAudio) {
                setContextValue(ConversionParameterNameType.BITS_SAMPLE, bitsSample, map);
                setContextValue(ConversionParameterNameType.SAMPLE_RATE, sampleRate, map);
            }
            if (nonNullVideo) {
                setContextValue(SequenceContextParameters.WIDTH, width, map);
                setContextValue(SequenceContextParameters.HEIGHT, height, map);
                setContextValue(ConversionParameterNameType.BIT_DEPTH, bitDepth, map);
                setContextValue(ConversionParameterNameType.FRAME_RATE, frameRate, map);
                setContextValue(ConversionParameterNameType.PIXEL_FORMAT, pixelFmt, map);
            }

            return map;
        }

        private void setContextValue(SequenceContextParameters seqParam, String value, DestContextTypeMap map) {
            setContextValue(seqParam.getName(), value, map);
        }

        private void setContextValue(ConversionParameterNameType convParam, String value, DestContextTypeMap map) {
            setContextValue(convParam.value(), value, map);
        }

        private void setContextValue(String paramName, String value, DestContextTypeMap map) {
            DestContextParamType param = new DestContextParamType();
            param.setName(paramName);
            param.setValue(value);

            map.getMap().put(paramName, param);
        }
    }

    private static class ConfigBuilder {

        private Boolean sampleRate;
        private Boolean bitsSample;

        private Boolean size;
        private Boolean frameRate;
        private Boolean pixelFmt;
        private Boolean bitDepth;

        private boolean nonNullConversionParams = false;
        private boolean nonNullAudio = false;
        private boolean nonNullVideo = false;

        public ConfigBuilder setSampleRate(Boolean sampleRate) {
            this.sampleRate = sampleRate;
            nonNullConversionParams = true;
            nonNullAudio = true;
            return this;
        }

        public ConfigBuilder setBitsSample(Boolean bitsSample) {
            this.bitsSample = bitsSample;
            nonNullConversionParams = true;
            nonNullAudio = true;
            return this;
        }

        public ConfigBuilder setSize(Boolean size) {
            this.size = size;
            nonNullConversionParams = true;
            nonNullVideo = true;
            return this;
        }

        public ConfigBuilder setFrameRate(Boolean frameRate) {
            this.frameRate = frameRate;
            nonNullConversionParams = true;
            nonNullVideo = true;
            return this;
        }

        public ConfigBuilder setPixelFmt(Boolean pixelFmt) {
            this.pixelFmt = pixelFmt;
            nonNullConversionParams = true;
            nonNullVideo = true;
            return this;
        }

        public ConfigBuilder setBitDepth(Boolean bitDepth) {
            this.bitDepth = bitDepth;
            nonNullConversionParams = true;
            nonNullVideo = true;
            return this;
        }

        public ConfigBuilder setNonNullConversionParams(Boolean nonNullConversionParams) {
            this.nonNullConversionParams = nonNullConversionParams;
            return this;
        }

        public ConfigBuilder setNonNullAudio(Boolean nonNullAudio) {
            this.nonNullAudio = nonNullAudio;
            nonNullConversionParams = true;
            return this;
        }

        public ConfigBuilder setNonNullVideo(Boolean nonNullVideo) {
            this.nonNullVideo = nonNullVideo;
            nonNullConversionParams = true;
            return this;
        }

        private AllowDisallow fromBoolean(Boolean value) {
            if (value == null) {
                return null;
            }
            if (value) {
                return AllowDisallow.YES;
            }
            return AllowDisallow.NO;
        }

        public ConfigType build() {
            ConfigType config = new ConfigType();
            if (!nonNullConversionParams) {
                return config;
            }
            ConversionParametersTypeMap conversionParameters = new ConversionParametersTypeMap();
            if (nonNullAudio) {
                setParameterValue(ConversionParameterNameType.BITS_SAMPLE, bitsSample, conversionParameters);
                setParameterValue(ConversionParameterNameType.SAMPLE_RATE, sampleRate, conversionParameters);
            }
            if (nonNullVideo) {
                setParameterValue(ConversionParameterNameType.SIZE, size, conversionParameters);
                setParameterValue(ConversionParameterNameType.BIT_DEPTH, bitDepth, conversionParameters);
                setParameterValue(ConversionParameterNameType.FRAME_RATE, frameRate, conversionParameters);
                setParameterValue(ConversionParameterNameType.PIXEL_FORMAT, pixelFmt, conversionParameters);
            }

            config.setConversionParameters(conversionParameters);

            return config;
        }

        private void setParameterValue(ConversionParameterNameType paramName, Boolean value, ConversionParametersTypeMap map) {
            ConversionParameterType param = new ConversionParameterType();
            param.setName(paramName);
            param.setValue(fromBoolean(value));

            map.getMap().put(paramName, param);
        }
    }

}
