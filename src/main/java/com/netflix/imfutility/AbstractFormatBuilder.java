package com.netflix.imfutility;

import com.netflix.imfutility.asset.AssetMap;
import com.netflix.imfutility.asset.AssetMapParser;
import com.netflix.imfutility.config.ConfigProvider;
import com.netflix.imfutility.conversion.ConversionEngine;
import com.netflix.imfutility.conversion.ConversionNotAllowedException;
import com.netflix.imfutility.conversion.ConversionProvider;
import com.netflix.imfutility.conversion.SilentConversionChecker;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.cpl.CplContextBuilder;
import com.netflix.imfutility.mediainfo.MediaInfoContextBuilder;
import com.netflix.imfutility.mediainfo.MediaInfoException;
import com.netflix.imfutility.xml.XmlParsingException;
import com.netflix.imfutility.xsd.conversion.FormatConfigurationType;
import com.netflix.imfutility.xsd.conversion.ParamType;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * The base class responsible for conversion to a destination format.
 * <ul>
 * <li>Contains logic common for all formats</li>
 * <li>Designed for inheritance</li>
 * <li>Provides a common conversion workflow in a {@link #build(String, String)} method</li>
 * <li>Subclasses must provide logic related to context creation: {@link #buildDynamicContext()} and {@link }</li>
 * <li>Subclasses may customize the workflow using {@link #preConvert()} and {@link #postConvert()} methods</li>
 * <li>Common workflow ({@link #build(String, String)}):
 * <ul>
 * <li>Initializing config and conversion (reading, parsing and validating config.xml and conversion,xml)</li>
 * <li>Clearing the specified working dir</li>
 * <li>Creating logs dir in the working dir</li>
 * <li>Filling the context (segment and dynamic)</li>
 * <li>Perform conversion executing operation from conversion.xml (see {@link ConversionEngine}</li>
 * <li>Deleting tmp files created during conversion.</li>
 * </ul>
 * </li>
 * </ul>
 */
public abstract class AbstractFormatBuilder {

    private final Logger logger = LoggerFactory.getLogger(AbstractFormatBuilder.class);

    protected final Format format;
    protected final String configXml;
    protected final String conversionXml;

    protected ConfigProvider configProvider;
    protected ConversionProvider conversionProvider;
    protected FormatConfigurationType formatConfigurationType;
    protected TemplateParameterContextProvider contextProvider;
    protected String workingDir;
    protected AssetMap assetMap;

    public AbstractFormatBuilder(Format format, String configXml, String conversionXml) {
        this.format = format;
        this.configXml = configXml;
        this.conversionXml = conversionXml;
    }

    public final void build(String cplXml, String assetmapXml) {
        try {
            logger.info("Starting conversion to '{}' format\n", format.getName());

            // 1. init config and conversion.
            init(configXml, conversionXml);

            // 2. clear working dir
            cleanWorkingDir();

            // 3. create logs dir in the working fdir
            createLogsDir();

            // 4. build IMF CPL contexts
            buildCplContext(cplXml, assetmapXml);

            // 5. fill dynamic and output contexts
            buildDynamicContext();
            buildOutputContext();

            // 6. build Media Info contexts (get resource parameters such as channels_num, fps, sample_rate, etc.)
            buildMediaInfoContext();


            // 7. select a conversion config within format.
            selectConversionConfig();

            // 8. check whether we can silently convert to destination parameters
            checkForSilentConversion();

            // 9. convert
            preConvert();
            convert();
            postConvert();


            // 10. delete tmp files.
            //deleteTmpFiles();

            logger.info("Conversion to '{}' format: OK\n", format.getName());
        } catch (Exception e) {
            logger.error(String.format("Conversion to '%s' format aborted", format.getName()), e);
        }

    }

    protected void init(String configXml, String conversionXml) throws XmlParsingException {
        logger.info("Initializing...");

        logger.info("Reading config.xml: {}", configXml);
        this.configProvider = new ConfigProvider(configXml);
        logger.info("Config.xml is processed: OK");

        logger.info("Reading conversion.xml: {}", conversionXml);
        this.conversionProvider = new ConversionProvider(conversionXml, format);
        logger.info("Conversion.xml is processed: OK");

        this.workingDir = configProvider.getConfig().getWorkingDirectory();
        logger.info("Working directory: {}", this.workingDir);

        this.contextProvider =
                new TemplateParameterContextProvider(configProvider.getConfig(), conversionProvider.getFormat(), workingDir);

        logger.info("Initialized: OK\n");
    }

    private void cleanWorkingDir() throws IOException {
        logger.info("Cleaning working directory...");
        FileUtils.cleanDirectory(new File(workingDir));
        logger.info("Cleaned working directory: OK\n");
    }

    private void createLogsDir() {
        logger.info("Creating external tools logging directory...");

        File logsDir = new File(workingDir, Constants.LOGS_DIR);
        logger.info("External tools logging directory: {}", logsDir);
        if (!logsDir.mkdir()) {
            logger.warn("Couldn't create External tools logging directory!");
        }

        logger.info("Created external tools logging directory: OK\n");
    }

    private void buildCplContext(String cplXml, String assetmapXml) throws XmlParsingException {
        logger.info("Building CPL contexts...");

        logger.info("Parsing ASSETMAP.xml ('{}')...", assetmapXml);
        this.assetMap = new AssetMapParser().parse(assetmapXml);
        logger.info("Parsed ASSETMAP.xml: OK");

        logger.info("Parsing CPL ('{}')...", cplXml);
        new CplContextBuilder(contextProvider, assetMap).build(cplXml);
        logger.info("Parsed CPL: OK");

        logger.info("Built CPL contexts: OK");
    }

    private void buildDynamicContext() {
        logger.info("Building Dynamic context...");
        doBuildDynamicContext();
        logger.info("Built Dynamic context: OK");
    }

    protected abstract void doBuildDynamicContext();

    private void buildOutputContext() {
        logger.info("Building Output context...");
        doBuildOutputContext();
        logger.info("Built Output context: OK");
    }

    protected abstract void doBuildOutputContext();

    private void buildMediaInfoContext() throws XmlParsingException, IOException, MediaInfoException {
        logger.info("Building Metadata Info contexts...");

        new MediaInfoContextBuilder(
                contextProvider, new ConversionEngine().getExecuteStrategyFactory(), conversionProvider.getFormat()).build();

        logger.info("Built Metadata Info contexts: OK");
    }

    private void selectConversionConfig() {
        String conversionConfig = getConversionConfiguration();
        logger.info("Conversion config: {}\n", conversionConfig);
        this.formatConfigurationType = conversionProvider.getFormatConfigurationType(conversionConfig);
    }

    private void checkForSilentConversion() throws ConversionNotAllowedException {
        logger.info("Checking whether it's allowed by config.xml to silently convert to destination parameters if they don't macth...");
        new SilentConversionChecker(contextProvider, formatConfigurationType, configProvider).check();
        logger.info("Checked: silent conversion is either allowed or not needed.");
    }

    protected void preConvert() throws IOException, XmlParsingException {
    }

    protected void postConvert() throws IOException, XmlParsingException {
    }

    private void convert() throws IOException {
        logger.info("Starting conversion...");
        new ConversionEngine().convert(formatConfigurationType, contextProvider);
        logger.info("Converted: OK\n");
    }

    protected abstract String getConversionConfiguration();

    private void deleteTmpFiles() {
        logger.info("Deleting tmp files created during conversion...");

        boolean success = true;
        for (ParamType tmpParam : contextProvider.getTmpContext().getAllParameters()) {
            success &= doDeleteTmpFile(tmpParam.getValue());
        }
        for (String paramValue : contextProvider.getDynamicContext().getAllParameters()) {
            success &= doDeleteTmpFile(paramValue);
        }

        if (success) {
            logger.info("Deleted tmp files created during conversion: OK\n");
        }
    }

    private boolean doDeleteTmpFile(String paramValue) {
        boolean success = true;

        File tmpFile = new File(paramValue);
        if (!tmpFile.isAbsolute() || !tmpFile.isFile()) {
            tmpFile = new File(workingDir, paramValue);
        }

        if (!tmpFile.isAbsolute() || !tmpFile.isFile()) {
            tmpFile = null;
        }

        if (tmpFile != null) {
            if (!tmpFile.delete()) {
                success = false;
                logger.warn("Couldn't delete tmp file {}", tmpFile.getAbsolutePath());
            }
        }

        return success;
    }

}
