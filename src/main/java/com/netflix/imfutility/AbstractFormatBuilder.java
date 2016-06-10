package com.netflix.imfutility;

import com.netflix.imfutility.asset.AssetMap;
import com.netflix.imfutility.asset.AssetMapParser;
import com.netflix.imfutility.config.ConfigXmlProvider;
import com.netflix.imfutility.conversion.ConversionEngine;
import com.netflix.imfutility.conversion.ConversionNotAllowedException;
import com.netflix.imfutility.conversion.ConversionXmlProvider;
import com.netflix.imfutility.conversion.SilentConversionChecker;
import com.netflix.imfutility.conversion.templateParameter.context.CustomParameterValue;
import com.netflix.imfutility.conversion.templateParameter.context.DynamicTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.DynamicContextParameters;
import com.netflix.imfutility.cpl.CplContextBuilder;
import com.netflix.imfutility.inputparameters.InputParameters;
import com.netflix.imfutility.mediainfo.MediaInfoContextBuilder;
import com.netflix.imfutility.mediainfo.MediaInfoException;
import com.netflix.imfutility.validate.ImfValidationException;
import com.netflix.imfutility.validate.ImfValidator;
import com.netflix.imfutility.xml.XmlParsingException;
import com.netflix.imfutility.xsd.conversion.FormatConfigurationType;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

/**
 * The base class responsible for conversion to a destination format.
 * <ul>
 * <li>Contains logic common for all formats</li>
 * <li>Designed for inheritance</li>
 * <li>Provides a common conversion workflow in a {@link #build()} method</li>
 * <li>Subclasses must provide logic related to context creation: {@link #buildDynamicContext()} and {@link }</li>
 * <li>Subclasses may customize the workflow using {@link #preConvert()} and {@link #postConvert()} methods</li>
 * <li>Common workflow ({@link #build()}):
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
    protected final InputParameters inputParameters;

    protected ConfigXmlProvider configProvider;
    protected ConversionXmlProvider conversionProvider;
    protected FormatConfigurationType formatConfigurationType;
    protected TemplateParameterContextProvider contextProvider;
    protected AssetMap assetMap;

    public AbstractFormatBuilder(Format format, InputParameters inputParameters) {
        this.format = format;
        this.inputParameters = inputParameters;
    }

    /**
     * Perform conversion.
     *
     * @return exit code
     */
    public final int build() {
        try {
            logger.info("Starting conversion to '{}' format\n", format.getName());

            // 1. init config and conversion.
            init();

            // 2. clear working dir
            cleanWorkingDir();

            // 3. create logs dir in the working dir
            createLogsDir();

            // 4. fill dynamic context
            buildDynamicContext();

            // 5. perform validation of the input IMP and CPL
            validateImpAndCpl();

            // 6. build IMF CPL contexts
            buildCplContext();

            // 7. build Media Info contexts (get resource parameters such as channels_num, fps, sample_rate, etc.)
            buildMediaInfoContext();

            // 8. select a conversion config within format.
            selectConversionConfig();

            // 9. check whether we can silently convert to destination parameters
            checkForSilentConversion();

            // 10. convert
            preConvert();
            convert();
            postConvert();

            // 11. delete tmp files.
            if (inputParameters.isDeleteTmpFilesOnExit()) {
                deleteTmpFiles();
            }

            logger.info("Conversion to '{}' format: OK\n", format.getName());

            return 0;
        } catch (Exception e) {
            logger.error(String.format("Conversion to '%s' format aborted", format.getName()), e);
            if (inputParameters.isDeleteTmpFilesOnFail()) {
                deleteTmpFiles();
            }
            return 1;
        }

    }

    protected void init() throws XmlParsingException, FileNotFoundException {
        logger.info("Initializing...");

        // 1. Reading and parsing config.xml
        logger.info("Reading config.xml: {}", inputParameters.getConfigXml());
        this.configProvider = new ConfigXmlProvider(inputParameters.getConfigXml());
        logger.info("Config.xml is processed: OK");

        // 2. check for alternative conversion.xml
        URL defaultConversionXmlUrl = ClassLoader.getSystemClassLoader().getResource(Constants.DEFAULT_CONVERSION_XML);
        String conversionXml = null;
        if (defaultConversionXmlUrl != null) {
            conversionXml = defaultConversionXmlUrl.getPath();
        }
        if (configProvider.getConfig().getConversionConfig() != null) {
            conversionXml = configProvider.getConfig().getConversionConfig();
            logger.info("Using alternative conversion.xml: {}", conversionXml);
        }
        if (conversionXml == null) {
            throw new ConversionException("Conversion.xml is not found in neither default location nor config.xml");
        }

        // 3. Reading and parsing conversion.xml
        logger.info("Reading conversion.xml: {}", conversionXml);
        this.conversionProvider = new ConversionXmlProvider(conversionXml, format);
        logger.info("Conversion.xml is processed: OK");

        // 4. setting working directory
        String workingDir = inputParameters.getDefaultWorkingDirectory();
        if (workingDir == null) {
            workingDir = configProvider.getConfig().getWorkingDirectory();
        }
        if (workingDir == null) {
            throw new ConversionException("Working directory must be specified either in config.xml or as an input parameter");
        }
        logger.info("Working directory: {}", workingDir);

        // 5. Init the context provider
        this.contextProvider =
                new TemplateParameterContextProvider(configProvider, conversionProvider, workingDir);

        logger.info("Initialized: OK\n");
    }

    private void cleanWorkingDir() throws IOException {
        logger.info("Cleaning working directory...");
        FileUtils.cleanDirectory(new File(contextProvider.getWorkingDir()));
        logger.info("Cleaned working directory: OK\n");
    }

    private void createLogsDir() {
        logger.info("Creating external tools logging directory...");

        File logsDir = new File(contextProvider.getWorkingDir(), Constants.LOGS_DIR);
        logger.info("External tools logging directory: {}", logsDir);
        if (!logsDir.mkdir()) {
            logger.warn("Couldn't create External tools logging directory!");
        }

        logger.info("Created external tools logging directory: OK\n");
    }

    private void buildDynamicContext() {
        logger.info("Building Dynamic context...");

        // build default dynamic parameters
        DynamicTemplateParameterContext dynamicContext = contextProvider.getDynamicContext();
        dynamicContext.addParameter(DynamicContextParameters.IMP, new File(inputParameters.getImpDirectory()).getAbsolutePath());
        dynamicContext.addParameter(DynamicContextParameters.CPL,
                new File(inputParameters.getImpDirectory(), inputParameters.getCplXml()).getAbsolutePath());
        dynamicContext.addParameter(DynamicContextParameters.WORKING_DIR, contextProvider.getWorkingDir());
        dynamicContext.addParameter(DynamicContextParameters.OUTPUT_VALIDATION_FILE, Constants.DEFAULT_OUTPUT_VALIDATION_FILE);
        dynamicContext.addParameter(DynamicContextParameters.VALIDATION_TOOL, ImfValidator.getValidationToolPath(configProvider));

        // build format-specific dynamic parameters
        doBuildDynamicContext();

        logger.info("Built Dynamic context: OK\n");
    }

    protected abstract void doBuildDynamicContext();

    private void validateImpAndCpl() throws XmlParsingException, IOException, ImfValidationException {
        logger.info("Validating input IMP and CPL...");

        new ImfValidator(contextProvider, new ConversionEngine().getExecuteStrategyFactory()).validate();

        logger.info("Validating input IMP and CPL: OK\n");
    }

    private void buildCplContext() throws XmlParsingException, FileNotFoundException {
        logger.info("Building CPL contexts...");

        File impDir = new File(inputParameters.getImpDirectory());
        if (!impDir.isDirectory()) {
            throw new FileNotFoundException(String.format("Invalid IMP directory: '%s' not found or not a directory", impDir.getAbsolutePath()));
        }

        File assetMapFile = new File(inputParameters.getImpDirectory(), Constants.ASSETMAP_FILE);
        logger.info("Parsing ASSETMAP.xml ('{}')...", assetMapFile.getAbsolutePath());
        this.assetMap = new AssetMapParser().parse(impDir, assetMapFile.getAbsolutePath());
        logger.info("Parsed ASSETMAP.xml: OK");

        File cplFile = new File(inputParameters.getImpDirectory(), inputParameters.getCplXml());
        logger.info("Parsing CPL ('{}')...", cplFile.getAbsolutePath());
        new CplContextBuilder(contextProvider, assetMap).build(cplFile.getAbsolutePath());
        logger.info("Parsed CPL: OK");

        logger.info("Built CPL contexts: OK\n");
    }

    private void buildMediaInfoContext() throws XmlParsingException, IOException, MediaInfoException {
        logger.info("Building Metadata Info contexts...");

        new MediaInfoContextBuilder(
                contextProvider, new ConversionEngine().getExecuteStrategyFactory()).build();

        logger.info("Built Metadata Info contexts: OK\n");
    }

    private void selectConversionConfig() {
        String conversionConfig = getConversionConfiguration();
        logger.info("Conversion config: {}\n", conversionConfig);
        this.formatConfigurationType = conversionProvider.getFormatConfigurationType(conversionConfig);
    }

    private void checkForSilentConversion() throws ConversionNotAllowedException {
        logger.info("Checking whether it's allowed by config.xml to silently convert to destination parameters if they don't match...");
        new SilentConversionChecker(contextProvider, formatConfigurationType, configProvider.getConfig()).check();
        logger.info("Checked: silent conversion is either allowed or not needed.\n");
    }

    protected abstract void preConvert() throws IOException, XmlParsingException;

    protected abstract void postConvert() throws IOException, XmlParsingException;

    private void convert() throws IOException {
        logger.info("Starting conversion...");
        new ConversionEngine().convert(formatConfigurationType, contextProvider);
        logger.info("Converted: OK\n");
    }

    protected abstract String getConversionConfiguration();

    private void deleteTmpFiles() {
        logger.info("Deleting tmp files created during conversion...");

        boolean success = true;
        for (CustomParameterValue tmpParam : contextProvider.getTmpContext().getAllParameters()) {
            if (tmpParam.isDeleteOnExit()) {
                success &= doDeleteTmpFile(tmpParam.getValue());
            }
        }
        for (CustomParameterValue paramValue : contextProvider.getDynamicContext().getAllParameters()) {
            if (paramValue.isDeleteOnExit()) {
                success &= doDeleteTmpFile(paramValue.getValue());
            }
        }

        if (success) {
            logger.info("Deleted tmp files created during conversion: OK\n");
        }
    }

    private boolean doDeleteTmpFile(String paramValue) {
        boolean success = true;

        File tmpFile = new File(paramValue);
        if (!tmpFile.isAbsolute() || !tmpFile.isFile()) {
            tmpFile = new File(contextProvider.getWorkingDir(), paramValue);
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
