package com.netflix.imfutility.validate;

import com.netflix.imfutility.Constants;
import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.config.ConfigXmlProvider;
import com.netflix.imfutility.conversion.executor.strategy.ExecuteStrategyFactory;
import com.netflix.imfutility.conversion.executor.strategy.OperationInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.DynamicContextParameters;
import com.netflix.imfutility.xsd.config.PreDefinedExternalToolsType;
import com.netflix.imfutility.xsd.conversion.ImfValidationType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Performs validation of the input IMF package (IMP) and CPL.
 * <ul>
 * <li>Validation is performed by a separate external tool.</li>
 * <li>The validation eternall tool command is specified in conversion.xml</li>
 * <li>By default, validation is done by a wrapper on Netflix Photon library.</li>
 * <li>It's possible to set a custom validation tool using config.xml.</li>
 * <li>The validation tool expect IMP, CPL, working dir and output XML file name parameters.</li>
 * <li>The result of the validation is stored in the specified output XML file.</li>
 * <li>The current class executes the validation commands and parses the output XML.</li>
 * <li>If there are validation exceptions - it throws {@link ImfValidationException} containing all found errors.</li>
 * </ul>
 */
public class ImfValidator {

    private final TemplateParameterContextProvider contextProvider;
    private final ExecuteStrategyFactory executeStrategyFactory;

    /**
     * Gets a validation tool path to be used as a dynamic parameter for a validation command from conversion.xml.
     * It's either the default one or a custom one from config.xml.
     *
     * @param configXmlProvider config provider that may contain a path to a custom validation tool.
     * @return validation tool path to be used as a dynamic parameter for a validation command from conversion.xml.
     */
    public static String getValidationToolPath(ConfigXmlProvider configXmlProvider) {
        // do we have a custom one in config.xml?
        PreDefinedExternalToolsType preDefinedExternalTools = configXmlProvider.getConfig().getPreDefinedExternalTools();
        if (preDefinedExternalTools != null && preDefinedExternalTools.getValidation() != null) {
            return preDefinedExternalTools.getValidation().getValue();
        }

        // return the default one
        URL defaultJarResourceUrl = ClassLoader.getSystemClassLoader().getResource("test/imf-validation-1.0.jar");
        if (defaultJarResourceUrl == null) {
            throw new ConversionException("Default IMF validation .jar not found");
        }
        try {
            URI defaultJarResourceUri = defaultJarResourceUrl.toURI();
            return String.format("java -jar %s", new File(defaultJarResourceUri).getAbsolutePath());
        } catch (URISyntaxException e) {
            throw new ConversionException("Default IMF validation .jar not found");
        }
    }

    public ImfValidator(TemplateParameterContextProvider contextProvider, ExecuteStrategyFactory executeStrategyFactory) {
        this.contextProvider = contextProvider;
        this.executeStrategyFactory = executeStrategyFactory;
    }

    /**
     * Performs validation of the given IMP and CPL.
     *
     * @throws ImfValidationException an exception thrown if validation errors are found
     * @throws IOException
     */
    public void validate() throws ImfValidationException, IOException {
        executeValidationCommand();
        analyzeResult();
    }

    private void executeValidationCommand() throws IOException {
        ImfValidationType imfValidationCommand = contextProvider.getConversionProvider().getConversion().getImfValidation();
        OperationInfo operationInfo = new OperationInfo(
                imfValidationCommand.getValue(), imfValidationCommand.getClass().getSimpleName(), ContextInfo.EMPTY);
        executeStrategyFactory.createExecuteOnceStrategy(contextProvider).execute(operationInfo);
    }

    private void analyzeResult() throws ImfValidationException, IOException {
        String errorFileName = contextProvider.getDynamicContext().getParameterValueAsString(DynamicContextParameters.OUTPUT_VALIDATION_FILE);
        File errorFile = new File(contextProvider.getWorkingDir(), errorFileName);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(errorFile);
            NodeList errorNodes = doc.getElementsByTagName(Constants.VALIDATION_OUTPUT_XML_ERROR_TAG);

            if (errorNodes == null || errorNodes.getLength() == 0) {
                // OK! no errors
                return;
            }

            List<String> errors = new ArrayList<>();
            for (int i = 0; i < errorNodes.getLength(); i++) {
                Element errorNode = (Element) errorNodes.item(i);
                errors.add(errorNode.getNodeValue());
            }
            throw new ImfValidationException(errors);
        } catch (ParserConfigurationException | SAXException e) {
            throw new ConversionException("Can not read result of IMF validation", e);
        }
    }

}
