package com.netflix.imfutility.dpp;

import com.netflix.imfutility.AbstractFormatBuilder;
import com.netflix.imfutility.Format;
import com.netflix.imfutility.conversion.templateParameter.context.DynamicTemplateParameterContext;
import com.netflix.imfutility.dpp.MetadataXmlProvider.DMFramework;
import com.netflix.imfutility.dpp.inputparameters.DppInputParameters;
import com.netflix.imfutility.xml.XmlParsingException;
import com.netflix.imfutility.xsd.dpp.metadata.AudioTrackLayoutDmAs11Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.netflix.imfutility.dpp.DppConversionConstants.*;

/**
 * DPP format builder (see {@link AbstractFormatBuilder}). It's used for conversion to DPP format.
 */
public class DppFormatBuilder extends AbstractFormatBuilder {

    private final Logger logger = LoggerFactory.getLogger(DppFormatBuilder.class);

    private final DppInputParameters dppInputParameters;

    public DppFormatBuilder(DppInputParameters dppInputParameters) {
        super(Format.DPP, dppInputParameters);
        this.dppInputParameters = dppInputParameters;
    }

    @Override
    protected void doBuildDynamicContext() {
        DynamicTemplateParameterContext dynamicContext = contextProvider.getDynamicContext();
        // fill output.mxf parameter
        dynamicContext.addParameter(DYNAMIC_PARAM_OUTPUT_MXF, DYNAMIC_PARAM_VALUE_OUTPUT_MXF, false);
    }

    @Override
    protected String getConversionConfiguration() {
        return conversionProvider.getConvertConfiguration().get(0);
    }

    @Override
    protected void preConvert() throws IOException, XmlParsingException {
        DynamicTemplateParameterContext dynamicContext = contextProvider.getDynamicContext();

        // 1. load metadata.xml
        MetadataXmlProvider metadataXmlProvider = new MetadataXmlProvider(dppInputParameters.getMetadataXml(), contextProvider.getWorkingDir());

        // 2. load audiomap.xml
        AudioTrackLayoutDmAs11Type audioTrackLayout = metadataXmlProvider.getDpp().getTechnical().getAudio().getAudioTrackLayout();
        AudioMapXmlProvider audioMapXmlProvider = new AudioMapXmlProvider(dppInputParameters.getAudiomapXml(), audioTrackLayout, contextProvider);

        // 3. fill audio map parameters
        dynamicContext.addParameter(DYNAMIC_PARAM_PAN, audioMapXmlProvider.getPanParameter());

        // 4. fill ebuAudioTracks parameter
        Integer audioTracksNum = audioMapXmlProvider.getEBUAudioTracks();
        dynamicContext.addParameter(DYNAMIC_PARAM_EBU_AUDIO_TRACKS, String.valueOf(audioTracksNum));

        // 5. fill bmx metadata files parameters
        metadataXmlProvider.createBmxDppParameterFiles();
        dynamicContext.addParameter(DYNAMIC_PARAM_UK_DPP_FILE,
                metadataXmlProvider.getBmxDppParameterFile(DMFramework.UKDPP).getAbsolutePath(), true);
        dynamicContext.addParameter(DYNAMIC_PARAM_AS11_CORE_FILE,
                metadataXmlProvider.getBmxDppParameterFile(DMFramework.AS11CORE).getAbsolutePath(), true);
        dynamicContext.addParameter(DYNAMIC_PARAM_AS11_SEGM_FILE,
                metadataXmlProvider.getBmxDppParameterFile(DMFramework.AS11Segmentation).getAbsolutePath(), true);
    }

    @Override
    protected void postConvert() throws IOException, XmlParsingException {
        // nothing to do
    }
}
