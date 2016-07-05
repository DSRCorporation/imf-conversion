package com.netflix.imfutility;

import com.netflix.imfutility.asset.AssetMap;
import com.netflix.imfutility.asset.AssetMapParser;
import com.netflix.imfutility.config.ConfigXmlProvider;
import com.netflix.imfutility.conversion.*;
import com.netflix.imfutility.conversion.templateParameter.context.CustomParameterValue;
import com.netflix.imfutility.conversion.templateParameter.context.DynamicTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.DynamicContextParameters;
import com.netflix.imfutility.cpl.CplContextBuilder;
import com.netflix.imfutility.inputparameters.ImfUtilityInputParameters;
import com.netflix.imfutility.inputparameters.ImfUtilityInputParametersValidator;
import com.netflix.imfutility.mediainfo.MediaInfoContextBuilder;
import com.netflix.imfutility.mediainfo.MediaInfoException;
import com.netflix.imfutility.validate.ImfValidationException;
import com.netflix.imfutility.validate.ImfValidator;
import com.netflix.imfutility.xml.XmlParsingException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * The base class responsible for conversion to a destination format.
 * <ul>
 * <li>Contains logic common for all formats</li>
 * <li>Designed for inheritance</li>
 * <li>Provides a common conversion workflow in a {@link #build()}  method</li>
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

    protected final IFormat format;
    protected final ImfUtilityInputParameters inputParameters;

    protected ConfigXmlProvider configProvider;
    protected ConversionXmlProvider conversionProvider;
    protected FormatConfigurationType formatConfigurationType;
    protected TemplateParameterContextProvider contextProvider;
    protected AssetMap assetMap;

    public AbstractFormatBuilder(IFormat format, ImfUtilityInputParameters inputParameters) {
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

            // 1. validate required command line arguments (including path to config.xml)
            validateCmdLineArguments();

            // 2. init config (from config.xml)
            initConfigXml();

            // 3. init conversion.xml
            initConversionXml();

            // 4. init input parameters (such as IMP folder, CPL file, etc.) either from cmd line, or from config.xml.
            initInputParameters();

            // 5. validate input parameters
            validateInputParameters();

            // 6. clear working dir
            if (isCleanWorkingDir()) {
                cleanWorkingDir();
            }

            // 7. create logs dir in the working dir
            createLogsDir();

            // 8. init template parameter contexts
            initContexts();

            // 9. fill dynamic context
            buildDynamicContext();

            // 10. perform validation of the input IMP and CPL
            validateImpAndCpl();

            // 11. build IMF CPL contexts
            buildCplContext();

            // 12. build Media Info contexts (get resource parameters such as channels_num, fps, sample_rate, etc.)
            buildMediaInfoContext();

            // 13. select a conversion config within format.
            selectConversionConfig();

            // 14. check whether we can silently convert to destination parameters
            checkForSilentConversion();

            // 15. convert
            doConvert();

            // 16. delete tmp files.
            if (isDeleteTmpFilesOnExit()) {
                deleteTmpFiles();
            }

            logger.info("Conversion to '{}' format: OK\n", format.getName());

            return 0;
        } catch (Exception e) {
            logger.error(String.format("Conversion to '%s' format aborted", format.getName()), e);
            if (isDeleteTmpFilesOnFail()) {
                deleteTmpFiles();
            }
            return 1;
        }

    }

    private boolean isCleanWorkingDir() {
        if (configProvider == null) {
            return CoreConstants.DEFAULT_CLEAN_WORKING_DIR;
        }
        if (configProvider.getConfig().isCleanWorkingDir() == null) {
            return CoreConstants.DEFAULT_CLEAN_WORKING_DIR;
        }
        return configProvider.getConfig().isCleanWorkingDir();
    }

    private boolean isDeleteTmpFilesOnExit() {
        if (configProvider == null) {
            return CoreConstants.DEFAULT_DELETE_TMP_FILES_ON_EXIT;
        }
        if (configProvider.getConfig().isDeleteTmpFilesOnExit() == null) {
            return CoreConstants.DEFAULT_DELETE_TMP_FILES_ON_EXIT;
        }
        return configProvider.getConfig().isDeleteTmpFilesOnExit();
    }

    private boolean isDeleteTmpFilesOnFail() {
        if (configProvider == null) {
            return CoreConstants.DEFAULT_DELETE_TMP_FILES_ON_FAIL;
        }
        if (configProvider.getConfig().isDeleteTmpFilesOnFail() == null) {
            return CoreConstants.DEFAULT_DELETE_TMP_FILES_ON_FAIL;
        }
        return configProvider.getConfig().isDeleteTmpFilesOnFail();
    }

    private void validateCmdLineArguments() {
        logger.info("Checking required command line arguments for conversion...");
        ImfUtilityInputParametersValidator.validateCmdLineArguments(inputParameters);
        doValidateCmdLineArguments();
        logger.info("Checked required command line arguments for conversion: OK\n");
    }

    protected abstract void doValidateCmdLineArguments();


    private void initConfigXml() throws XmlParsingException, FileNotFoundException {
        logger.info("Reading config.xml: {}", inputParameters.getConfigFile().getAbsolutePath());
        this.configProvider = new ConfigXmlProvider(inputParameters.getConfigFile());
        logger.info("Config.xml is processed: OK\n");
    }

    private void initConversionXml() throws XmlParsingException, FileNotFoundException {
        logger.info("Initializing conversion.xml");

        if (configProvider.getConfig().getConversionConfig() != null) {
            // 1. check for alternative conversion.xml
            String conversionXml = configProvider.getConfig().getConversionConfig();
            logger.info("Using alternative conversion.xml: {}", conversionXml);
            this.conversionProvider = new ConversionXmlProvider(conversionXml, format);
        } else {
            // 2. use default one from resources
            InputStream defaultConversionXml = getClass().getResourceAsStream(CoreConstants.DEFAULT_CONVERSION_XML);
            if (defaultConversionXml == null) {
                throw new ConversionException("Conversion.xml is not found in neither default location nor config.xml");
            }
            this.conversionProvider = new ConversionXmlProvider(defaultConversionXml, CoreConstants.DEFAULT_CONVERSION_XML, format);
        }

        logger.info("Conversion.xml is processed: OK\n");
    }

    private void initInputParameters() {
        logger.info("Initializing input parameters...");

        // 1. setting working directory (if it's in config.xml)
        inputParameters.setDefaultWorkingDir(configProvider.getConfig().getWorkingDirectory());

        // 2. setting IMP (if it's in config.xml)
        inputParameters.setDefaultImp(configProvider.getConfig().getImp());

        // 3. setting CPL (if it's in config.xml)
        inputParameters.setDefaultCpl(configProvider.getConfig().getCpl());

        // 4. custom IMF validation
        if (configProvider.getConfig().getExternalTools().getMap().containsKey(CoreConstants.IMF_VALIDATION_TOOL)) {
            String customImfValidation = configProvider.getConfig().getExternalTools().getMap().get(CoreConstants.IMF_VALIDATION_TOOL).getValue();
            if (!StringUtils.isEmpty(customImfValidation)) {
                inputParameters.setCustomImfValidationTool(customImfValidation);
            }
        }

        logger.info("Initialized input parameters: OK\n");
    }

    private void validateInputParameters() {
        logger.info("Checking required input parameters for conversion...");
        ImfUtilityInputParametersValidator.validateInputParameters(inputParameters);
        logger.info("Checked required input parameters for conversion: OK\n");
    }

    private void cleanWorkingDir() throws IOException {
        logger.info("Cleaning working directory...");
        FileUtils.cleanDirectory(inputParameters.getWorkingDirFile());
        logger.info("Cleaned working directory: OK\n");
    }

    private void createLogsDir() {
        logger.info("Creating external tools logging directory...");

        File logsDir = new File(inputParameters.getWorkingDirFile(), CoreConstants.LOGS_DIR);
        logger.info("External tools logging directory: {}", logsDir);
        if (!logsDir.mkdir()) {
            logger.warn("Couldn't create External tools logging directory!");
        }

        logger.info("Created external tools logging directory: OK\n");
    }

    private void initContexts() {
        logger.info("Initializing template parameter contexts...");
        this.contextProvider =
                new TemplateParameterContextProvider(configProvider, conversionProvider, inputParameters.getWorkingDirFile());
        logger.info("Initialized template parameter contexts: OK\n");
    }

    private void buildDynamicContext() {
        logger.info("Building Dynamic context...");

        // build default dynamic parameters
        DynamicTemplateParameterContext dynamicContext = contextProvider.getDynamicContext();
        dynamicContext.addParameter(DynamicContextParameters.IMP, inputParameters.getImpDirectoryFile().getAbsolutePath());
        dynamicContext.addParameter(DynamicContextParameters.CPL, inputParameters.getCplFile().getAbsolutePath());
        dynamicContext.addParameter(DynamicContextParameters.VALIDATION_TOOL, inputParameters.getImfValidationTool());

        // build format-specific dynamic parameters
        doBuildDynamicContext();

        logger.info("Built Dynamic context: OK\n");
    }

    protected abstract void doBuildDynamicContext();

    private void validateImpAndCpl() throws IOException, ImfValidationException {
        logger.info("Validating input IMP and CPL...");
        new ImfValidator(contextProvider, new ConversionEngine().getExecuteStrategyFactory()).validate();
        logger.info("Validated input IMP and CPL: OK\n");
    }

    private void buildCplContext() throws XmlParsingException, FileNotFoundException {
        logger.info("Building CPL contexts...");

        File assetMapFile = new File(inputParameters.getImpDirectoryFile(), CoreConstants.ASSETMAP_FILE);
        logger.info("Parsing ASSETMAP.xml ('{}')...", assetMapFile.getAbsolutePath());
        this.assetMap = new AssetMapParser().parse(inputParameters.getImpDirectoryFile(), assetMapFile);
        logger.info("Parsed ASSETMAP.xml: OK");

        logger.info("Parsing CPL ('{}')...", inputParameters.getCplFile().getAbsolutePath());
        new CplContextBuilder(contextProvider, assetMap).build(inputParameters.getCplFile());
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

    private void doConvert() throws IOException, XmlParsingException {
        logger.info("Starting conversion...");
        preConvert();
        new ConversionEngine().convert(formatConfigurationType, contextProvider);
        postConvert();
        logger.info("Converted: OK\n");
    }

    protected abstract void preConvert() throws IOException, XmlParsingException;

    protected abstract void postConvert() throws IOException, XmlParsingException;


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
