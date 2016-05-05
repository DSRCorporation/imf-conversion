package com.netflix.imfutility.conversion.executor;

import com.netflix.imfutility.ConfigProvider;
import com.netflix.imfutility.Format;
import com.netflix.imfutility.conversion.ConversionProvider;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.util.ConfigUtils;
import com.netflix.imfutility.util.ConversionUtils;
import com.netflix.imfutility.xsd.conversion.ExecOnceType;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Alexander on 5/5/2016.
 */
public class ParseConversionOperationTest {

    private static TemplateParameterContextProvider contextProvider;
    private static ConversionProvider conversionProvider;

    @BeforeClass
    public static void setUpAll() throws Exception {
        ConfigProvider configProvider = new ConfigProvider(ConfigUtils.getCorrectConfigXml());
        conversionProvider = new ConversionProvider(ConversionUtils.getCorrectConversionXml(), Format.DPP);
        contextProvider = new TemplateParameterContextProvider(
                configProvider.getConfig(), conversionProvider.getFormat(), ".");
    }

    @Test
    public void testSplitAndResolve() {
        ConversionExecutorOnce executor = new ConversionExecutorOnce(contextProvider);
        List<String> actual = executor.parseOperation(getOperation(0));
        List<String> expected = Arrays.asList(
                "root\\tool1",
                "--input-resolution", "1920x1080",
                "-a",
                "name=xxx",
                "tmpParam1Value");

        assertThat(actual, is(expected));
    }

    @Test
    public void testSplitWithNewlines() {
        ConversionExecutorOnce executor = new ConversionExecutorOnce(contextProvider);
        List<String> actual = executor.parseOperation(getOperation(1));
        List<String> expected = Arrays.asList(
                "root\\tool1",
                "--input-resolution",
                "1920x1080",
                "-a",
                "name=xxx",
                "tmpParam1Value",
                "-u");

        assertThat(actual, is(expected));
    }

    @Test
    public void testAddQuotesWhenNeeded() {
        ConversionExecutorOnce executor = new ConversionExecutorOnce(contextProvider);
        List<String> actual = executor.parseOperation(getOperation(2));
        List<String> expected = Arrays.asList(
                "\"root\\tool   3\"",
                "\"tmpParam2 Value\"",
                "\"tmpParam3   Value\"",
                "\"tmpParam6 Value\"");

        assertThat(actual, is(expected));
    }

    @Test
    public void testResolveComplexParameters() {
        ConversionExecutorOnce executor = new ConversionExecutorOnce(contextProvider);
        List<String> actual = executor.parseOperation(getOperation(3));
        List<String> expected = Arrays.asList(
                "\"root\\tool   3\"",
                "tmpParam1Value=tmpParam1Value",
                "tmpParam1Value=tmpParam1Value/tmpParam1Value");

        assertThat(actual, is(expected));
    }

    @Test
    public void testAddQuotesWhenNeededForComplexParameters() {
        ConversionExecutorOnce executor = new ConversionExecutorOnce(contextProvider);
        List<String> actual = executor.parseOperation(getOperation(4));
        List<String> expected = Arrays.asList(
                "\"root\\tool   3\"",
                "tmpParam1Value=\"tmpParam3   Value\"",
                "tmpParam1Value=\"tmpParam6 Value\"",
                "aaa=\"tmpParam3   Value/tmpParam2 Value\"",
                "bbb=\"tmpParam3   Value/tmpParam2 Value\"");

        assertThat(actual, is(expected));
    }

    private String getOperation(int operationNum) {
        return ((ExecOnceType) conversionProvider.getFormat().getFormatConfigurations().getMap()
                .get("main").getPipeOrExecOnceOrExecEachSegment().get(operationNum)).getValue();
    }

}
