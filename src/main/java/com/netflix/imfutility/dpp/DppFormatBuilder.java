package com.netflix.imfutility.dpp;

import com.netflix.imfutility.AbstractFormatBuilder;
import com.netflix.imfutility.Format;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.context.DynamicTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.OutputTemplateParameterContext;
import com.netflix.imfutility.xml.XmlParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

/**
 * DPP format builder (see {@link AbstractFormatBuilder}). It's used for conversion to DPP format.
 */
public class DppFormatBuilder extends AbstractFormatBuilder {

    private final Logger logger = LoggerFactory.getLogger(AbstractFormatBuilder.class);

    public DppFormatBuilder(String configXml, String conversionXml) {
        super(Format.DPP, configXml, conversionXml);
    }

    @Override
    protected void doBuildOutputContext() {
        // FIXME
        OutputTemplateParameterContext outputContext = contextProvider.getOutputContext();
        outputContext.addParameter("mxf", "output.mxf");
    }

    @Override
    protected void doBuildDynamicContext() {
        // FIXME
        DynamicTemplateParameterContext dynamicContext = contextProvider.getDynamicContext();
        dynamicContext.addParameter("audioChannels", "2", ContextInfo.EMPTY);
    }

    @Override
    protected String getConversionConfiguration() {
        return conversionProvider.getConvertConfiguration().get(0);
    }

    @Override
    protected void preConvert() {
        DynamicTemplateParameterContext dynamicContext = contextProvider.getDynamicContext();
        dynamicContext.addParameter("panParameter", "2c|c0=c0|c1=c1", ContextInfo.EMPTY);

        dynamicContext.addParameter("ebuAudioTracks", "2", ContextInfo.EMPTY);


        //create a temp file
        File temp = null;
        try {
            temp = File.createTempFile(UUID.randomUUID().toString(), ".xml");

            assertTrue("Temporary file cannot be deleted.", temp.delete());

            //try to generate Dpp metadata.xml
            MetadataXml.GenerateEmptyXml(temp.getAbsolutePath());

            //check it is not empty
            assertTrue("Generated metadata.xml is zero size.", temp.length() > 0);

            //get generated temporary files
            Map<MetadataXml.DMFramework, File> dppParameters = MetadataXml.getBmxDppParameters(temp);

            dynamicContext.addParameter("ukDppFramework", dppParameters.get(MetadataXml.DMFramework.UKDPP).getPath(), ContextInfo.EMPTY);
            dynamicContext.addParameter("as11CoreFramework", dppParameters.get(MetadataXml.DMFramework.AS11CORE).getPath(), ContextInfo.EMPTY);
            dynamicContext.addParameter("as11SegmentationFramework", dppParameters.get(MetadataXml.DMFramework.AS11Segmentation).getPath(), ContextInfo.EMPTY);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlParsingException e) {
            e.printStackTrace();
        }

    }


}
