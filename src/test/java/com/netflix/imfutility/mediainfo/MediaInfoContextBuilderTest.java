package com.netflix.imfutility.mediainfo;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.util.MediaInfoUtils;
import com.netflix.imfutility.util.TemplateParameterContextCreator;
import com.netflix.imfutility.util.conversion.executor.TestExecuteStrategyFactory;
import com.netflix.imfutility.util.conversion.executor.TestExecutorLogger;
import com.netflix.imfutility.xml.XmlParsingException;
import com.netflix.imfutility.xsd.conversion.SequenceType;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EnumSet;

/**
 * Created by Alexander on 6/2/2016.
 */
public class MediaInfoContextBuilderTest {

    @Test
    public void testParseCorrectMediaInfo() throws Exception {
        new TestMediaInfoContextBuilder(
                TemplateParameterContextCreator.createDefaultContextProviderWithCPLContext(
                        2, 2, 2, EnumSet.of(SequenceType.VIDEO, SequenceType.AUDIO)),
                new TestExecutorLogger(),
                MediaInfoUtils.getCorrectMediaInfoVideo(), MediaInfoUtils.getCorrectMediaInfoAudio())
                .build();
    }

    @Test(expected = XmlParsingException.class)
    public void testParseBrokenXmlVideo() throws Exception {
        new TestMediaInfoContextBuilder(
                TemplateParameterContextCreator.createDefaultContextProviderWithCPLContext(
                        2, 2, 2, EnumSet.of(SequenceType.VIDEO, SequenceType.AUDIO)),
                new TestExecutorLogger(),
                MediaInfoUtils.getBrokenXmlMediaInfoVideo(), MediaInfoUtils.getCorrectMediaInfoAudio())
                .build();
    }

    @Test(expected = XmlParsingException.class)
    public void testParseBrokenXmlAudio() throws Exception {
        new TestMediaInfoContextBuilder(
                TemplateParameterContextCreator.createDefaultContextProviderWithCPLContext(
                        2, 2, 2, EnumSet.of(SequenceType.VIDEO, SequenceType.AUDIO)),
                new TestExecutorLogger(),
                MediaInfoUtils.getCorrectMediaInfoVideo(), MediaInfoUtils.getBrokenXmlMediaInfoAudio())
                .build();
    }


    @Test(expected = XmlParsingException.class)
    public void testParseInvalidXsdVideo() throws Exception {
        new TestMediaInfoContextBuilder(
                TemplateParameterContextCreator.createDefaultContextProviderWithCPLContext(
                        2, 2, 2, EnumSet.of(SequenceType.VIDEO, SequenceType.AUDIO)),
                new TestExecutorLogger(),
                MediaInfoUtils.getInvalidXsdMediaInfoVideo(), MediaInfoUtils.getCorrectMediaInfoAudio())
                .build();
    }

    @Test(expected = XmlParsingException.class)
    public void testParseInvalidXsdAudio() throws Exception {
        new TestMediaInfoContextBuilder(
                TemplateParameterContextCreator.createDefaultContextProviderWithCPLContext(
                        2, 2, 2, EnumSet.of(SequenceType.VIDEO, SequenceType.AUDIO)),
                new TestExecutorLogger(),
                MediaInfoUtils.getCorrectMediaInfoVideo(), MediaInfoUtils.getInvalidXsdMediaInfoAudio())
                .build();
    }

    @Test(expected = FileNotFoundException.class)
    public void testParseInvalidFilePathVideo() throws Exception {
        new TestMediaInfoContextBuilder(
                TemplateParameterContextCreator.createDefaultContextProviderWithCPLContext(
                        2, 2, 2, EnumSet.of(SequenceType.VIDEO, SequenceType.AUDIO)),
                new TestExecutorLogger(),
                "C:/invalid-path", MediaInfoUtils.getCorrectMediaInfoAudio())
                .build();
    }

    @Test(expected = FileNotFoundException.class)
    public void testParseInvalidFilePathAudio() throws Exception {
        new TestMediaInfoContextBuilder(
                TemplateParameterContextCreator.createDefaultContextProviderWithCPLContext(
                        2, 2, 2, EnumSet.of(SequenceType.VIDEO, SequenceType.AUDIO)),
                new TestExecutorLogger(),
                MediaInfoUtils.getCorrectMediaInfoVideo(), "C:/invalid-path")
                .build();
    }

    @Test
    public void testMediaInfoCommandsExecution() throws Exception {
        TestExecutorLogger testExecutorLogger = new TestExecutorLogger();
        new TestMediaInfoContextBuilder(
                TemplateParameterContextCreator.createDefaultContextProviderWithCPLContext(
                        2, 2, 2, EnumSet.of(SequenceType.VIDEO, SequenceType.AUDIO)),
                new TestExecutorLogger(),
                MediaInfoUtils.getCorrectMediaInfoVideo(), MediaInfoUtils.getCorrectMediaInfoAudio())
                .build();


    }


    private static class TestMediaInfoContextBuilder extends MediaInfoContextBuilder {

        private final String videoMediaInfoXml;
        private final String audioMediaInfoXml;

        public TestMediaInfoContextBuilder(TemplateParameterContextProvider contextProvider, TestExecutorLogger testExecutorLogger,
                                           String videoMediaInfoXml, String audiooMediaInfoXml) {
            super(contextProvider, new TestExecuteStrategyFactory(testExecutorLogger));
            this.videoMediaInfoXml = videoMediaInfoXml;
            this.audioMediaInfoXml = audiooMediaInfoXml;
        }

        @Override
        File executeMediaInfoCommand(ContextInfo contextInfo) throws IOException {
            switch (contextInfo.getSequenceType()) {
                case AUDIO:
                    return new File(audioMediaInfoXml);
                case VIDEO:
                    return new File(videoMediaInfoXml);
                default:
                    throw new RuntimeException();
            }
        }
    }

}
