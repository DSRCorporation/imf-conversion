package com.netflix.imfutility.dpp;

import com.netflix.imfutility.AbstractFormatBuilder;
import com.netflix.imfutility.Format;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.context.DynamicTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.OutputTemplateParameterContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DPP format builder (see {@link AbstractFormatBuilder}). It's used for conversion to DPP format.
 */
public class DppFormatBuilder extends AbstractFormatBuilder {

    private final Logger logger = LoggerFactory.getLogger(AbstractFormatBuilder.class);

    public DppFormatBuilder(String configXml, String conversionXml) {
        super(Format.DPP, configXml, conversionXml);
    }

    @Override
    protected void doFillOutputContext() {
        // FIXME
        OutputTemplateParameterContext outputContext = contextProvider.getOutputContext();
        outputContext.addParameter("mxf", "output.mxf");
    }

    @Override
    protected void doFillDynamicContext() {
        // FIXME
        DynamicTemplateParameterContext dynamicContext = contextProvider.getDynamicContext();
        dynamicContext.addParameter("audioChannels", "2", ContextInfo.EMPTY);
    }

    @Override
    protected String getConversionConfiguration() {
        return conversionProvider.getConvertConfiguration().get(0);
    }

}
