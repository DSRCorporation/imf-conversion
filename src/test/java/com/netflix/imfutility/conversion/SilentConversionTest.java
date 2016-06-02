package com.netflix.imfutility.conversion;

import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import com.netflix.imfutility.xsd.config.*;
import com.netflix.imfutility.xsd.conversion.*;
import org.junit.Test;

/**
 * Tests whether silent conversion is allowed depending on values from config.xml (allow/disallow for each parameter)
 * and from conversion.xml (destination parameter values).
 */
public class SilentConversionTest {

    @Test
    public void okConversionNotSpecifiedInBoth() throws Exception {
        // 1. create format with no conversion parameters specified
        FormatConfigurationType formatConfiguration = new FormatConfigurationBuilder().build();

        // 2. create config with no conversion parameters specified
        ConfigType config = new ConfigBuilder().build();

        // 3. create context
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, formatConfiguration, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okConversionNotSpecifiedInConfigXml() throws Exception {
        // 1. create format with all parameters specified
        FormatConfigurationType formatConfiguration = new FormatConfigurationBuilder()
                .setBitsSample("24").setSampleRate("48000")
                .setBitDepth("10").setFrameRate("25").setHeight("1920").setWidth("1080").setPixelFmt("yuv422p10le")
                .build();

        // 2. create config with no conversion parameters specified
        ConfigType config = new ConfigBuilder().build();

        // 3. create context with mismatched parameters
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.AUDIO, SequenceUUID.create("111"),
                SequenceContextParameters.BITS_PER_SAMPLE, "16");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.AUDIO, SequenceUUID.create("111"),
                SequenceContextParameters.SAMPLE_RATE, "96000");

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, formatConfiguration, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okConversionNotSpecifiedInDestinationParams() throws Exception {
        // 1. create format with no conversion parameters specified
        FormatConfigurationType formatConfiguration = new FormatConfigurationBuilder().build();

        // 2. create config with disallow mismatched for all
        ConfigType config = new ConfigBuilder()
                .setBitsSample(false).setSampleRate(false)
                .setFrameRate(false).setBitDepth(false).setSize(false).setPixelFmt(false)
                .build();

        // 3. create context
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, formatConfiguration, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okContextEmpty() throws Exception {
        // 1. create format with all parameters specified
        FormatConfigurationType formatConfiguration = new FormatConfigurationBuilder()
                .setBitsSample("24").setSampleRate("48000")
                .setBitDepth("10").setFrameRate("25").setHeight("1920").setWidth("1080").setPixelFmt("yuv422p10le")
                .build();

        // 2. create config with disallow mismatched for all
        ConfigType config = new ConfigBuilder()
                .setBitsSample(false).setSampleRate(false)
                .setFrameRate(false).setBitDepth(false).setSize(false).setPixelFmt(false)
                .build();

        // 3. create empty context
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, formatConfiguration, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okAudioNotSpecifiedInConfigXml() throws Exception {
        // 1. create format with all parameters specified
        FormatConfigurationType formatConfiguration = new FormatConfigurationBuilder()
                .setBitsSample("24").setSampleRate("48000")
                .setBitDepth("10").setFrameRate("25").setHeight("1920").setWidth("1080").setPixelFmt("yuv422p10le")
                .build();

        // 2. create config with audio not specified
        ConfigType config = new ConfigBuilder()
                .setFrameRate(false).setBitDepth(false).setSize(false).setPixelFmt(false)
                .setNonNullAudio(false)
                .build();

        // 3. create context with audio specified and mismatched
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.AUDIO, SequenceUUID.create("111"),
                SequenceContextParameters.BITS_PER_SAMPLE, "20");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.AUDIO, SequenceUUID.create("111"),
                SequenceContextParameters.SAMPLE_RATE, "96000");

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, formatConfiguration, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okVideoNotSpecifiedInConfigXml() throws Exception {
        // 1. create format with all parameters specified
        FormatConfigurationType formatConfiguration = new FormatConfigurationBuilder()
                .setBitsSample("24").setSampleRate("48000")
                .setBitDepth("10").setFrameRate("25").setHeight("1920").setWidth("1080").setPixelFmt("yuv422p10le")
                .build();

        // 2. create config with video not specified
        ConfigType config = new ConfigBuilder()
                .setBitsSample(false).setSampleRate(false)
                .setNonNullVideo(false)
                .build();

        // 3. create context with video specified and mismatched
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("111"),
                SequenceContextParameters.FRAME_RATE, "30");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("111"),
                SequenceContextParameters.BIT_DEPTH, "8");

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, formatConfiguration, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okAudioNotSpecifiedInDestinationParams() throws Exception {
        // 1. create format with all parameters except audio specified
        FormatConfigurationType formatConfiguration = new FormatConfigurationBuilder()
                .setBitDepth("10").setFrameRate("25").setHeight("1920").setWidth("1080").setPixelFmt("yuv422p10le")
                .setNonNullAudio(false)
                .build();

        // 2. create config disallow mismatched for all
        ConfigType config = new ConfigBuilder()
                .setBitsSample(false).setSampleRate(true)
                .setFrameRate(false).setBitDepth(false).setSize(false).setPixelFmt(false)
                .build();

        // 3. create context with audio specified
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.AUDIO, SequenceUUID.create("111"),
                SequenceContextParameters.BITS_PER_SAMPLE, "16");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.AUDIO, SequenceUUID.create("111"),
                SequenceContextParameters.SAMPLE_RATE, "96000");

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, formatConfiguration, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okVideoNotSpecifiedInDestinationParams() throws Exception {
        // 1. create format with all parameters except video specified
        FormatConfigurationType formatConfiguration = new FormatConfigurationBuilder()
                .setBitsSample("24").setSampleRate("48000")
                .setNonNullVideo(false)
                .build();

        // 2. create config disallow mismatched
        ConfigType config = new ConfigBuilder()
                .setBitsSample(true).setSampleRate(true)
                .setFrameRate(true).setBitDepth(true).setSize(true).setPixelFmt(true)
                .build();

        // 3. create context with video specified
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("111"),
                SequenceContextParameters.BIT_DEPTH, "8");
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("111"),
                SequenceContextParameters.FRAME_RATE, "50");

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, formatConfiguration, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okAudioParameterNotSpecifiedInDestinationParams() throws Exception {
        // 1. create format with sample rate not specified
        FormatConfigurationType formatConfiguration = new FormatConfigurationBuilder()
                .setSampleRate(null)
                .setNonNullAudio(true)
                .setNonNullVideo(true)
                .build();

        // 2. create config disallow mismatched sample rate
        ConfigType config = new ConfigBuilder()
                .setSampleRate(false)
                .build();

        // 3. create context with sample rate specified
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.AUDIO, SequenceUUID.create("111"),
                SequenceContextParameters.SAMPLE_RATE, "96000");

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, formatConfiguration, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okAudioParameterEmptyInDestinationParams() throws Exception {
        // 1. create format with sample rate is empty
        FormatConfigurationType formatConfiguration = new FormatConfigurationBuilder()
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
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.AUDIO, SequenceUUID.create("111"),
                SequenceContextParameters.SAMPLE_RATE, "96000");

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, formatConfiguration, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okVideoParameterNotSpecifiedInDestinationParams() throws Exception {
        // 1. create format with bit depth not specified
        FormatConfigurationType formatConfiguration = new FormatConfigurationBuilder()
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
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("111"),
                SequenceContextParameters.BIT_DEPTH, "8");

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, formatConfiguration, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okVideoParameterEmptyInDestinationParams() throws Exception {
        // 1. create format with bit depth is empty
        FormatConfigurationType formatConfiguration = new FormatConfigurationBuilder()
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
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("111"),
                SequenceContextParameters.BIT_DEPTH, "8");

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, formatConfiguration, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okAudioParameterNotSpecifiedInConfigXml() throws Exception {
        // 1. create format with sample rate specified and mismatched
        FormatConfigurationType formatConfiguration = new FormatConfigurationBuilder()
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
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.AUDIO, SequenceUUID.create("111"),
                SequenceContextParameters.SAMPLE_RATE, "96000");

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, formatConfiguration, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okVideoParameterNotSpecifiedInConfigXml() throws Exception {
        // 1. create format with bit depth specified and mismatched
        FormatConfigurationType formatConfiguration = new FormatConfigurationBuilder()
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
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
        contextProvider.getSequenceContext().addSequenceParameter(SequenceType.VIDEO, SequenceUUID.create("111"),
                SequenceContextParameters.BIT_DEPTH, "8");

        // 4. init checker
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, formatConfiguration, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test(expected = ConversionNotAllowedException.class)
    public void exceptionDisallowMismatchedAndMismatch() throws Exception {
        // 1. create format with all parameters specified and mismatched
        FormatConfigurationType formatConfiguration = new FormatConfigurationBuilder()
                .setBitsSample("24").setSampleRate("48000")
                .setBitDepth("10").setFrameRate("25").setHeight("1920").setWidth("1080").setPixelFmt("yuv422p10le")
                .build();

        // 2. create config that doesn't allow mismatched parameters
        ConfigType config = new ConfigBuilder()
                .setBitsSample(false).setSampleRate(false)
                .setFrameRate(false).setBitDepth(true).setSize(false).setPixelFmt(false)
                .build();

        // 3. create context with mismacthed parameters
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
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
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, formatConfiguration, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okAllowMismatchedAndMismatch() throws Exception {
        // 1. create format with all parameters specified and mismatched
        FormatConfigurationType formatConfiguration = new FormatConfigurationBuilder()
                .setBitsSample("24").setSampleRate("48000")
                .setBitDepth("10").setFrameRate("25").setHeight("1920").setWidth("1080").setPixelFmt("yuv422p10le")
                .build();

        // 2. create config that allows mismatched parameters
        ConfigType config = new ConfigBuilder()
                .setBitsSample(true).setSampleRate(true)
                .setFrameRate(true).setBitDepth(true).setSize(true).setPixelFmt(true)
                .build();

        // 3. create context with mismatched parameters
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
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
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, formatConfiguration, config);

        // 5. check. no exception expected
        checker.check();
    }

    @Test
    public void okDisallowMismatchedAndMatch() throws Exception {
        // 1. create format with all parameters specified and matched
        FormatConfigurationType formatConfiguration = new FormatConfigurationBuilder()
                .setBitsSample("24").setSampleRate("48000")
                .setBitDepth("10").setFrameRate("25").setHeight("1920").setWidth("1080").setPixelFmt("yuv422p10le")
                .build();

        // 2. create config that doesn't allow mismatched parameters
        ConfigType config = new ConfigBuilder()
                .setBitsSample(false).setSampleRate(false)
                .setFrameRate(false).setBitDepth(false).setSize(false).setPixelFmt(false)
                .build();

        // 3. create context with matched parameters
        TemplateParameterContextProvider contextProvider = TemplateParameterContextCreator.createDefaultContextProvider();
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
        SilentConversionChecker checker = new SilentConversionChecker(contextProvider, formatConfiguration, config);

        // 5. check. no exception expected
        checker.check();
    }

    private static class FormatConfigurationBuilder {

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

        public FormatConfigurationBuilder setSampleRate(String sampleRate) {
            this.sampleRate = sampleRate;
            nonNullConversionParams = true;
            nonNullAudio = true;
            return this;
        }

        public FormatConfigurationBuilder setBitsSample(String bitsSample) {
            this.bitsSample = bitsSample;
            nonNullConversionParams = true;
            nonNullAudio = true;
            return this;
        }

        public FormatConfigurationBuilder setWidth(String width) {
            this.width = width;
            nonNullConversionParams = true;
            nonNullVideo = true;
            return this;
        }

        public FormatConfigurationBuilder setHeight(String height) {
            this.height = height;
            nonNullConversionParams = true;
            nonNullVideo = true;
            return this;
        }

        public FormatConfigurationBuilder setFrameRate(String frameRate) {
            this.frameRate = frameRate;
            nonNullConversionParams = true;
            nonNullVideo = true;
            return this;
        }

        public FormatConfigurationBuilder setPixelFmt(String pixelFmt) {
            this.pixelFmt = pixelFmt;
            nonNullConversionParams = true;
            nonNullVideo = true;
            return this;
        }

        public FormatConfigurationBuilder setBitDepth(String bitDepth) {
            this.bitDepth = bitDepth;
            nonNullConversionParams = true;
            nonNullVideo = true;
            return this;
        }

        public FormatConfigurationBuilder setNonNullConversionParams(Boolean nonNullConversionParams) {
            this.nonNullConversionParams = nonNullConversionParams;
            return this;
        }

        public FormatConfigurationBuilder setNonNullAudio(Boolean nonNullAudio) {
            this.nonNullAudio = nonNullAudio;
            nonNullConversionParams = true;
            return this;
        }

        public FormatConfigurationBuilder setNonNullVideo(Boolean nonNullVideo) {
            this.nonNullVideo = nonNullVideo;
            nonNullConversionParams = true;
            return this;
        }

        public FormatConfigurationType build() {
            FormatConfigurationType formatConfiguration = new FormatConfigurationType();
            if (!nonNullConversionParams) {
                return formatConfiguration;
            }
            DestinationConversionParametersType conversionParameters = new DestinationConversionParametersType();
            if (nonNullAudio) {
                DestinationAudioConversionParametersType audioConversionParameters = new DestinationAudioConversionParametersType();
                audioConversionParameters.setBitsSample(bitsSample);
                audioConversionParameters.setSampleRate(sampleRate);
                conversionParameters.setAudio(audioConversionParameters);
            }
            if (nonNullVideo) {
                DestinationVideoConversionParametersType videoConversionParameters = new DestinationVideoConversionParametersType();
                videoConversionParameters.setWidth(width);
                videoConversionParameters.setBitDepth(bitDepth);
                videoConversionParameters.setFrameRate(frameRate);
                videoConversionParameters.setPixelFormat(pixelFmt);
                conversionParameters.setVideo(videoConversionParameters);
            }
            formatConfiguration.setConversionParameters(conversionParameters);

            return formatConfiguration;
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
            ConversionParametersType conversionParameters = new ConversionParametersType();
            if (nonNullAudio) {
                AudioConversionParametersType audioConversionParameters = new AudioConversionParametersType();
                audioConversionParameters.setBitsSample(fromBoolean(bitsSample));
                audioConversionParameters.setSampleRate(fromBoolean(sampleRate));
                conversionParameters.setAudio(audioConversionParameters);
            }
            if (nonNullVideo) {
                VideoConversionParametersType videoConversionParameters = new VideoConversionParametersType();
                videoConversionParameters.setSize(fromBoolean(size));
                videoConversionParameters.setBitDepth(fromBoolean(bitDepth));
                videoConversionParameters.setFrameRate(fromBoolean(frameRate));
                videoConversionParameters.setPixelFormat(fromBoolean(pixelFmt));
                conversionParameters.setVideo(videoConversionParameters);
            }
            config.setConversionParameters(conversionParameters);

            return config;
        }
    }

}
