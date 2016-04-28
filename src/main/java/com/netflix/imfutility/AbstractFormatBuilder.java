package com.netflix.imfutility;

import com.netflix.imfutility.conversion.ConversionEngine;
import com.netflix.imfutility.conversion.ConversionProvider;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.xsd.conversion.ParamType;
import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.File;

/**
 * Created by Alexander on 4/28/2016.
 */
public abstract class AbstractFormatBuilder {

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
            // 1. init config and conversion.
            init(configXml, conversionXml);

            // 2. clear working dir
            FileUtils.cleanDirectory(new File(workingDir));

            // 3. create logs dir in the working dir
            createLogsDir();

            // 4. fill contexts
            fillDynamicContext();
            fillSegmentContext();

            // 5. convert
            preConvert();
            new ConversionEngine().convert(
                    conversionProvider.getFormat(), getConversionConfiguration(), contextProvider);
            postConvert();

            // 6. delete tmp files.
            deleteTmpFiles();
        } catch (Exception e) {
            // TODO
            e.printStackTrace();
        }

    }

    protected void init(String configXml, String conversionXml) throws JAXBException, SAXException {
        this.configProvider = new ConfigProvider(configXml);
        this.conversionProvider = new ConversionProvider(conversionXml, format);
        this.workingDir = configProvider.getConfig().getWorkingDirectory();
        this.contextProvider =
                new TemplateParameterContextProvider(configProvider.getConfig(), conversionProvider.getFormat(), workingDir);
    }

    protected void preConvert() {
    }

    protected void postConvert() {
    }

    protected abstract void fillDynamicContext();

    protected abstract void fillSegmentContext();

    protected abstract String getConversionConfiguration();

    private void createLogsDir() {
        File logsDir = new File(workingDir, Constants.LOGS_DIR);
        logsDir.mkdir();
    }

    private void deleteTmpFiles() {
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
    }

}
