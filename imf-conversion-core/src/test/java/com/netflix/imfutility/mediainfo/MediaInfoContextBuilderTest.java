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
package com.netflix.imfutility.mediainfo;

import com.netflix.imfutility.conversion.executor.strategy.AbstractExecuteStrategy;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfoBuilder;
import com.netflix.imfutility.conversion.templateParameter.context.CustomParameterValue;
import com.netflix.imfutility.conversion.templateParameter.context.DynamicTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.ResourceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.SequenceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.DynamicContextParameters;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.generated.mediainfo.FfprobeType;
import com.netflix.imfutility.util.MediaInfoUtils;
import com.netflix.imfutility.util.conversion.executor.TestExecuteStrategyFactory;
import com.netflix.imfutility.util.conversion.executor.TestExecutorLogger;
import com.netflix.imfutility.xml.XmlParsingException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.EnumSet;

import static com.netflix.imfutility.util.TemplateParameterContextCreator.addResourceContextParameter;
import static com.netflix.imfutility.util.TemplateParameterContextCreator.createDefaultContextProviderWithCPLContext;
import static com.netflix.imfutility.util.TemplateParameterContextCreator.createResourceContextInfo;
import static com.netflix.imfutility.util.TemplateParameterContextCreator.getSequenceUuid;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * <ul>
 * <li>Tests the MediaInfo.xml can be parsed and mapped to Java model successfully.</li>
 * <li>Tests the XSD validation is applied to the MediaInfo.xml and an exception is thrown is validation doesn't pass.</li>
 * <li>Tests that media info external command execution is correct (each command is executed only once for each essence-type pair).</li>
 * <li>Tests that Sequence context is filled correctly according to mediaInfo.xml</li>
 * <li>Tests that Dynamic context parameters are filled correctly (input and output to delete on exit).</li>
 * <li>Tests that an exception is thrown if there are mismatched media info parameters for the same track.</li>
 * <li>Tests that output mediInfo.xml file names are correct (a separate file for each essence and each type).</li>
 * </ul>
 */
public class MediaInfoContextBuilderTest {

    @Before
    public void setUp() {
        AbstractExecuteStrategy.resetCount();
    }

    @Test
    public void testParseCorrectMediaInfo() throws Exception {
        new TestMediaInfoContextBuilder(MediaInfoUtils.getCorrectMediaInfoVideo(), MediaInfoUtils.getCorrectMediaInfoAudio())
                .build();
    }

    @Test(expected = XmlParsingException.class)
    public void testParseBrokenXmlVideo() throws Exception {
        new TestMediaInfoContextBuilder(MediaInfoUtils.getBrokenXmlMediaInfoVideo(), MediaInfoUtils.getCorrectMediaInfoAudio())
                .build();
    }

    @Test(expected = XmlParsingException.class)
    public void testParseBrokenXmlAudio() throws Exception {
        new TestMediaInfoContextBuilder(MediaInfoUtils.getCorrectMediaInfoVideo(), MediaInfoUtils.getBrokenXmlMediaInfoAudio())
                .build();
    }

    // currently we do not validate according to XSD as sometimes the output may contain not all required attributes
    @Test(expected = XmlParsingException.class)
    @Ignore
    public void testParseInvalidXsdVideo() throws Exception {
        new TestMediaInfoContextBuilder(MediaInfoUtils.getInvalidXsdMediaInfoVideo(), MediaInfoUtils.getCorrectMediaInfoAudio())
                .build();
    }

    // currently we do not validate according to XSD as sometimes the output may contain not all required attributes
    @Test(expected = XmlParsingException.class)
    @Ignore
    public void testParseInvalidXsdAudio() throws Exception {
        new TestMediaInfoContextBuilder(MediaInfoUtils.getCorrectMediaInfoVideo(), MediaInfoUtils.getInvalidXsdMediaInfoAudio())
                .build();
    }

    @Test(expected = FileNotFoundException.class)
    public void testParseInvalidFilePathVideo() throws Exception {
        new TestMediaInfoContextBuilder(new File("invalid-path"), MediaInfoUtils.getCorrectMediaInfoAudio())
                .build();
    }

    @Test(expected = FileNotFoundException.class)
    public void testParseInvalidFilePathAudio() throws Exception {
        new TestMediaInfoContextBuilder(MediaInfoUtils.getCorrectMediaInfoVideo(), new File("invalid-path"))
                .build();
    }

    @Test
    public void testMediaInfoCommandsExecution() throws Exception {
        // fill essence context
        TemplateParameterContextProvider contextProvider = createDefaultContextProviderWithCPLContext(
                4, 1, 1, EnumSet.of(SequenceType.VIDEO, SequenceType.AUDIO));
        addResourceContextParameter(contextProvider, 0, 0, SequenceType.AUDIO, 0,         // essence1 for first audio segment
                ResourceContextParameters.ESSENCE, "essence1");
        addResourceContextParameter(contextProvider, 1, 0, SequenceType.AUDIO, 0,         // essence1 for second audio segment
                ResourceContextParameters.ESSENCE, "essence1");
        addResourceContextParameter(contextProvider, 2, 0, SequenceType.AUDIO, 0,        // essence2 for third audio segment
                ResourceContextParameters.ESSENCE, "essence2");
        addResourceContextParameter(contextProvider, 3, 0, SequenceType.AUDIO, 0,        // essence3 for fourth audio segment
                ResourceContextParameters.ESSENCE, "essence3");
        addResourceContextParameter(contextProvider, 0, 0, SequenceType.VIDEO, 0,        // essence1 for first video segment
                ResourceContextParameters.ESSENCE, "essence1");
        addResourceContextParameter(contextProvider, 1, 0, SequenceType.VIDEO, 0,        // essence2 for second video segment
                ResourceContextParameters.ESSENCE, "essence2");
        addResourceContextParameter(contextProvider, 2, 0, SequenceType.VIDEO, 0,        // essence2 for third video segment
                ResourceContextParameters.ESSENCE, "essence2");
        addResourceContextParameter(contextProvider, 3, 0, SequenceType.VIDEO, 0,        // essence4 for fourth video segment
                ResourceContextParameters.ESSENCE, "essence4");

        // build media info
        TestExecutorLogger testExecutorLogger = new TestExecutorLogger();
        new TestMediaInfoContextBuilder(contextProvider, testExecutorLogger).build();

        // media info command must be run once for each sequenceType-essence pair
        assertEquals(
                "START: External Process 1: MediaInfoCommandVideoType_essence1, ExecuteOnceStrategy, mediaInfoCommandVideo FILE",
                testExecutorLogger.getNext());
        assertEquals(
                "FINISH: External Process 1: MediaInfoCommandVideoType_essence1, ExecuteOnceStrategy, mediaInfoCommandVideo FILE",
                testExecutorLogger.getNext());

        assertEquals(
                "START: External Process 2: MediaInfoCommandVideoType_essence2, ExecuteOnceStrategy, mediaInfoCommandVideo FILE",
                testExecutorLogger.getNext());
        assertEquals(
                "FINISH: External Process 2: MediaInfoCommandVideoType_essence2, ExecuteOnceStrategy, mediaInfoCommandVideo FILE",
                testExecutorLogger.getNext());

        assertEquals(
                "START: External Process 3: MediaInfoCommandVideoType_essence4, ExecuteOnceStrategy, mediaInfoCommandVideo FILE",
                testExecutorLogger.getNext());
        assertEquals(
                "FINISH: External Process 3: MediaInfoCommandVideoType_essence4, ExecuteOnceStrategy, mediaInfoCommandVideo FILE",
                testExecutorLogger.getNext());

        assertEquals(
                "START: External Process 4: MediaInfoCommandAudioType_essence1, ExecuteOnceStrategy, mediaInfoCommandAudio FILE",
                testExecutorLogger.getNext());
        assertEquals(
                "FINISH: External Process 4: MediaInfoCommandAudioType_essence1, ExecuteOnceStrategy, mediaInfoCommandAudio FILE",
                testExecutorLogger.getNext());

        assertEquals(
                "START: External Process 5: MediaInfoCommandAudioType_essence2, ExecuteOnceStrategy, mediaInfoCommandAudio FILE",
                testExecutorLogger.getNext());
        assertEquals(
                "FINISH: External Process 5: MediaInfoCommandAudioType_essence2, ExecuteOnceStrategy, mediaInfoCommandAudio FILE",
                testExecutorLogger.getNext());

        assertEquals(
                "START: External Process 6: MediaInfoCommandAudioType_essence3, ExecuteOnceStrategy, mediaInfoCommandAudio FILE",
                testExecutorLogger.getNext());
        assertEquals(
                "FINISH: External Process 6: MediaInfoCommandAudioType_essence3, ExecuteOnceStrategy, mediaInfoCommandAudio FILE",
                testExecutorLogger.getNext());

        assertFalse("There are more executed processes than expected!", testExecutorLogger.hasNext());
    }

    @Test
    public void testFillSequenceContext() throws Exception {
        TemplateParameterContextProvider contextProvider = createDefaultContextProviderWithCPLContext(
                2, 2, 2, EnumSet.of(SequenceType.VIDEO, SequenceType.AUDIO));
        new TestMediaInfoContextBuilder(contextProvider).build();

        SequenceTemplateParameterContext sequenceContext = contextProvider.getSequenceContext();

        // the values as defined in mediaInfoAudio.xml and mediaInfoVideo.xml

        // first audio track
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSequenceType(SequenceType.AUDIO)
                .setSequenceUuid(getSequenceUuid(0, SequenceType.AUDIO))
                .build();
        assertEquals("48000", sequenceContext.getParameterValue(SequenceContextParameters.SAMPLE_RATE, contextInfo));
        assertEquals("24", sequenceContext.getParameterValue(SequenceContextParameters.BITS_PER_SAMPLE, contextInfo));

        // second audio track
        contextInfo = new ContextInfoBuilder()
                .setSequenceType(SequenceType.AUDIO)
                .setSequenceUuid(getSequenceUuid(1, SequenceType.AUDIO))
                .build();
        assertEquals("48000", sequenceContext.getParameterValue(SequenceContextParameters.SAMPLE_RATE, contextInfo));
        assertEquals("24", sequenceContext.getParameterValue(SequenceContextParameters.BITS_PER_SAMPLE, contextInfo));

        // first video track
        contextInfo = new ContextInfoBuilder()
                .setSequenceType(SequenceType.VIDEO)
                .setSequenceUuid(getSequenceUuid(0, SequenceType.VIDEO))
                .build();
        assertEquals("50 1", sequenceContext.getParameterValue(SequenceContextParameters.FRAME_RATE, contextInfo));
        assertEquals("yuv422p10le", sequenceContext.getParameterValue(SequenceContextParameters.PIXEL_FORMAT, contextInfo));
        assertEquals("10", sequenceContext.getParameterValue(SequenceContextParameters.BIT_DEPTH, contextInfo));
        assertEquals("4096", sequenceContext.getParameterValue(SequenceContextParameters.WIDTH, contextInfo));
        assertEquals("2160", sequenceContext.getParameterValue(SequenceContextParameters.HEIGHT, contextInfo));

        // second video track
        contextInfo = new ContextInfoBuilder()
                .setSequenceType(SequenceType.VIDEO)
                .setSequenceUuid(getSequenceUuid(1, SequenceType.VIDEO))
                .build();
        assertEquals("50 1", sequenceContext.getParameterValue(SequenceContextParameters.FRAME_RATE, contextInfo));
        assertEquals("yuv422p10le", sequenceContext.getParameterValue(SequenceContextParameters.PIXEL_FORMAT, contextInfo));
        assertEquals("10", sequenceContext.getParameterValue(SequenceContextParameters.BIT_DEPTH, contextInfo));
        assertEquals("4096", sequenceContext.getParameterValue(SequenceContextParameters.WIDTH, contextInfo));
        assertEquals("2160", sequenceContext.getParameterValue(SequenceContextParameters.HEIGHT, contextInfo));
    }

    @Test
    public void testDynamicContextMediaInfoInputNotForDelete() throws Exception {
        TemplateParameterContextProvider contextProvider = createDefaultContextProviderWithCPLContext(
                2, 2, 2, EnumSet.of(SequenceType.VIDEO, SequenceType.AUDIO));
        new TestMediaInfoContextBuilder(contextProvider).build();

        DynamicTemplateParameterContext dynamicContext = contextProvider.getDynamicContext();
        CustomParameterValue input = dynamicContext.getParameterValue(DynamicContextParameters.MEDIA_INFO_INPUT);

        // input not in context for delete on exit!
        assertNotNull(input);
        assertFalse(input.isDeleteOnExit());
    }

    @Test
    public void testMediaInfoOutputFileName() throws Exception {
        assertEquals("mediaInfo_audio_filename.mxf.xml",
                TestMediaInfoContextBuilder.getOutputFileName(SequenceType.AUDIO, "C:/parent/filename.mxf"));
        assertEquals("mediaInfo_video_filename.mxf.xml",
                TestMediaInfoContextBuilder.getOutputFileName(SequenceType.VIDEO, "C:/parent/filename.mxf"));

        assertEquals("mediaInfo_audio_filename.mxf.xml",
                TestMediaInfoContextBuilder.getOutputFileName(SequenceType.AUDIO, "filename.mxf"));
        assertEquals("mediaInfo_video_filename.mxf.xml",
                TestMediaInfoContextBuilder.getOutputFileName(SequenceType.VIDEO, "filename.mxf"));

        assertEquals("mediaInfo_audio_filename.mxf.xml",
                TestMediaInfoContextBuilder.getOutputFileName(SequenceType.AUDIO, "/tmp/parent/filename.mxf"));
        assertEquals("mediaInfo_video_filename.mxf.xml",
                TestMediaInfoContextBuilder.getOutputFileName(SequenceType.VIDEO, "/tmp/parent/filename.mxf"));
    }

    @Test
    public void testDynamicContextMediaInfoOutput() throws Exception {
        TemplateParameterContextProvider contextProvider = createDefaultContextProviderWithCPLContext(
                2, 2, 2, EnumSet.of(SequenceType.VIDEO, SequenceType.AUDIO));
        new TestMediaInfoContextBuilder(contextProvider).build();


        // essence
        ResourceTemplateParameterContext resourceContext = contextProvider.getResourceContext();
        String essenceAudio1 = resourceContext.getParameterValue(ResourceContextParameters.ESSENCE,
                createResourceContextInfo(0, 0, SequenceType.AUDIO, 0, 0));
        String essenceAudio2 = resourceContext.getParameterValue(ResourceContextParameters.ESSENCE,
                createResourceContextInfo(1, 1, SequenceType.AUDIO, 1, 0));
        String essenceVideo1 = resourceContext.getParameterValue(ResourceContextParameters.ESSENCE,
                createResourceContextInfo(0, 0, SequenceType.VIDEO, 0, 0));
        String essenceVideo2 = resourceContext.getParameterValue(ResourceContextParameters.ESSENCE,
                createResourceContextInfo(1, 1, SequenceType.VIDEO, 1, 0));

        // dynamic parameter for each mediaInfo.xml created for each essence
        DynamicTemplateParameterContext dynamicContext = contextProvider.getDynamicContext();
        CustomParameterValue outputVideo1 = dynamicContext.getParameterValue(
                MediaInfoContextBuilder.getOutputDynamicParamName(SequenceType.VIDEO, essenceVideo1));
        CustomParameterValue outputVideo2 = dynamicContext.getParameterValue(
                MediaInfoContextBuilder.getOutputDynamicParamName(SequenceType.VIDEO, essenceVideo2));
        CustomParameterValue outputAudio1 = dynamicContext.getParameterValue(
                MediaInfoContextBuilder.getOutputDynamicParamName(SequenceType.AUDIO, essenceAudio1));
        CustomParameterValue outputAudio2 = dynamicContext.getParameterValue(
                MediaInfoContextBuilder.getOutputDynamicParamName(SequenceType.AUDIO, essenceAudio2));

        // output must be deleted on exit!
        // the value of the parameter must be equal to the  full path to mediaInfo.xml for each essence
        assertNotNull(outputVideo1);
        assertTrue(outputVideo1.isDeleteOnExit());
        assertEquals(
                MediaInfoContextBuilder.getOutputFile(SequenceType.VIDEO, essenceVideo1, contextProvider.getWorkingDir()).getAbsolutePath(),
                outputVideo1.getValue());

        assertNotNull(outputVideo2);
        assertTrue(outputVideo2.isDeleteOnExit());
        assertEquals(
                MediaInfoContextBuilder.getOutputFile(SequenceType.VIDEO, essenceVideo2, contextProvider.getWorkingDir()).getAbsolutePath(),
                outputVideo2.getValue());

        assertNotNull(outputAudio1);
        assertTrue(outputAudio1.isDeleteOnExit());
        assertEquals(
                MediaInfoContextBuilder.getOutputFile(SequenceType.AUDIO, essenceAudio1, contextProvider.getWorkingDir()).getAbsolutePath(),
                outputAudio1.getValue());

        assertNotNull(outputAudio2);
        assertTrue(outputAudio2.isDeleteOnExit());
        assertEquals(
                MediaInfoContextBuilder.getOutputFile(SequenceType.AUDIO, essenceAudio2, contextProvider.getWorkingDir()).getAbsolutePath(),
                outputAudio2.getValue());
    }

    @Test(expected = MediaInfoException.class)
    public void testExceptionOnMismatchedSequenceParameters() throws Exception {
        TemplateParameterContextProvider contextProvider = createDefaultContextProviderWithCPLContext(
                2, 2, 2, EnumSet.of(SequenceType.VIDEO, SequenceType.AUDIO));
        // create a test builder that uses different mediaInfo files with different parameters for the same sequence
        MediaInfoContextBuilder mediaInfoContextBuilder = new MediaInfoContextBuilder(contextProvider, new TestExecuteStrategyFactory()) {

            @Override
            FfprobeType parseOutputFile(File outputFile, ContextInfo contextInfo) throws XmlParsingException, FileNotFoundException {
                try {
                    switch (contextInfo.getSequenceType()) {
                        case AUDIO:
                            outputFile = contextInfo.getResourceUuid().getUuid().contains("-0-")
                                    ? MediaInfoUtils.getCorrectMediaInfoAudio()
                                    : MediaInfoUtils.getCorrectMediaInfoAudio2();
                            break;
                        case VIDEO:
                            outputFile = contextInfo.getResourceUuid().getUuid().contains("-0-")
                                    ? MediaInfoUtils.getCorrectMediaInfoVideo()
                                    : MediaInfoUtils.getCorrectMediaInfoVideo2();
                            break;
                        default:
                            throw new RuntimeException();
                    }
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                return super.parseOutputFile(outputFile, contextInfo);
            }
        };

        mediaInfoContextBuilder.build();
    }

    private static class TestMediaInfoContextBuilder extends MediaInfoContextBuilder {

        private final File videoMediaInfoXml;
        private final File audioMediaInfoXml;

        public TestMediaInfoContextBuilder(File videoMediaInfoXml, File audioMediaInfoXml) throws Exception {
            this(createDefaultContextProviderWithCPLContext(
                    2, 2, 2, EnumSet.of(SequenceType.VIDEO, SequenceType.AUDIO)),
                    new TestExecutorLogger(), videoMediaInfoXml, audioMediaInfoXml);
        }

        public TestMediaInfoContextBuilder(TemplateParameterContextProvider contextProvider, TestExecutorLogger testExecutorLogger) throws URISyntaxException {
            this(contextProvider, testExecutorLogger, MediaInfoUtils.getCorrectMediaInfoVideo(), MediaInfoUtils.getCorrectMediaInfoAudio());
        }

        public TestMediaInfoContextBuilder(TemplateParameterContextProvider contextProvider) throws URISyntaxException {
            this(contextProvider, new TestExecutorLogger(),
                    MediaInfoUtils.getCorrectMediaInfoVideo(), MediaInfoUtils.getCorrectMediaInfoAudio());
        }

        public TestMediaInfoContextBuilder(TemplateParameterContextProvider contextProvider, TestExecutorLogger testExecutorLogger,
                                           File videoMediaInfoXml, File audioMediaInfoXml) {
            super(contextProvider, new TestExecuteStrategyFactory(testExecutorLogger));
            this.videoMediaInfoXml = videoMediaInfoXml;
            this.audioMediaInfoXml = audioMediaInfoXml;
        }

        @Override
        FfprobeType parseOutputFile(File outputFile, ContextInfo contextInfo) throws XmlParsingException, FileNotFoundException {
            switch (contextInfo.getSequenceType()) {
                case AUDIO:
                    outputFile = audioMediaInfoXml;
                    break;
                case VIDEO:
                    outputFile = videoMediaInfoXml;
                    break;
                default:
                    throw new RuntimeException();
            }
            return super.parseOutputFile(outputFile, contextInfo);
        }

    }

}
