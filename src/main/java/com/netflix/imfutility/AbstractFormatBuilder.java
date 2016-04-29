package com.netflix.imfutility;

import com.netflix.imfutility.conversion.ConversionEngine;
import com.netflix.imfutility.conversion.ConversionProvider;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.xsd.conversion.ParamType;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;

/**
 * Created by Alexander on 4/28/2016.
 */
public abstract class AbstractFormatBuilder {

    final Logger logger = LoggerFactory.getLogger(AbstractFormatBuilder.class);

    protected Format format;
    protected ConfigProvider configProvider;
    protected ConversionProvider conversionProvider;
    protected TemplateParameterContextProvider contextProvider;
    protected String workingDir;

    public AbstractFormatBuilder(Format format) {
        this.format = format;
    }

    public final void build(String configXml, String conversionXml) {
        try {
            logger.info("Starting conversion to '{}' format\n", format.getName());

            // 1. init config and conversion.
            init(configXml, conversionXml);

            // 2. clear working dir
            cleanWorkingDir();

            // 3. create logs dir in the working dir
            createLogsDir();

            // 4. fill contexts
            fillDynamicContext();
            fillSegmentContext();

            // 5. convert
            preConvert();
            convert();
            postConvert();

            // 6. delete tmp files.
            deleteTmpFiles();
        } catch (Exception e) {
            logger.error("Conversion aborted", e);
        }

    }

    protected void init(String configXml, String conversionXml) throws JAXBException, SAXException {
        logger.info("Initializing...");

        logger.info("Reading config.xml: {}", configXml);
        this.configProvider = new ConfigProvider(configXml);
        logger.info("Config.xml is processed successfully");

        logger.info("Reading conversion.xml: {}", conversionXml);
        this.conversionProvider = new ConversionProvider(conversionXml, format);
        logger.info("Conversion.xml is processed successfully");

        this.workingDir = configProvider.getConfig().getWorkingDirectory();
        logger.info("Working directory: {}", this.workingDir);

        this.contextProvider =
                new TemplateParameterContextProvider(configProvider.getConfig(), conversionProvider.getFormat(), workingDir);

        logger.info("Initialized successfully\n");
    }

    protected void preConvert() {
    }

    protected void postConvert() {
    }

    protected void convert() throws IOException, InterruptedException {
        logger.info("Starting conversion...");

        String conversionConfig = getConversionConfiguration();
        logger.info("Conversion config: {}", conversionConfig);
        new ConversionEngine().convert(
                conversionProvider.getFormat(), conversionConfig, contextProvider);

        logger.info("Successfully converted\n");
    }

    protected abstract void fillDynamicContext();

    protected abstract void fillSegmentContext();

    protected abstract String getConversionConfiguration();

    private void cleanWorkingDir() throws IOException {
        logger.info("Cleaning working directory...");
        FileUtils.cleanDirectory(new File(workingDir));
        logger.info("Cleaned working directory successfully\n");
    }

    private void createLogsDir() {
        logger.info("Creating external tools logging directory...");

        File logsDir = new File(workingDir, Constants.LOGS_DIR);
        logger.info("External tools logging directory: {}", logsDir);
        if (!logsDir.mkdir()) {
            logger.warn("Couldn't create External tools logging directory!");
        }

        logger.info("Created external tools logging directory successfully\n");
    }

    private void deleteTmpFiles() {
        logger.info("Deleting tmp files created during conversion...");

        for (ParamType tmpParam : contextProvider.getTmpContext().getAllParameters()) {
            File tmpFile = new File(tmpParam.getValue());
            if (!tmpFile.isAbsolute() || !tmpFile.isFile()) {
                tmpFile = new File(workingDir, tmpParam.getValue());
            }
            if (!tmpFile.isAbsolute() || !tmpFile.isFile()) {
                tmpFile = null;
            }
            if (tmpFile != null) {
                tmpFile.delete();
            }
        }

        logger.info("Deleted tmp files created during conversion successfully\n");
    }

}
