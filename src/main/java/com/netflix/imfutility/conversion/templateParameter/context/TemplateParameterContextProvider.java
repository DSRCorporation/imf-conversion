package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.config.ConfigXmlProvider;
import com.netflix.imfutility.conversion.ConversionXmlProvider;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterContext;

import java.util.HashMap;
import java.util.Map;

/**
 * A holder of all template parameter contexts: tool, tmp, dynamic, segment, sequence  etc.
 */
public class TemplateParameterContextProvider {

    private final ConversionXmlProvider conversionProvider;
    private final ConfigXmlProvider configProvider;
    private final String workingDir;

    private final Map<TemplateParameterContext, ITemplateParameterContext> contexts = new HashMap<>();

    /**
     * @param configProvider     a config provider corresponding to config.xml
     * @param conversionProvider a conversion provider corresponding to conversion.xml.
     * @param workingDir         a working directory where the output file as well as all tmp files are created.
     */
    public TemplateParameterContextProvider(ConfigXmlProvider configProvider, ConversionXmlProvider conversionProvider, String workingDir) {
        this.configProvider = configProvider;
        this.conversionProvider = conversionProvider;
        this.workingDir = workingDir;
        initContexts();
    }

    public ITemplateParameterContext getContext(TemplateParameterContext context) {
        return contexts.get(context);
    }

    public ToolTemplateParameterContext getToolContext() {
        return (ToolTemplateParameterContext) contexts.get(TemplateParameterContext.TOOL);
    }

    public TmpTemplateParameterContext getTmpContext() {
        return (TmpTemplateParameterContext) contexts.get(TemplateParameterContext.TMP);
    }

    public DynamicTemplateParameterContext getDynamicContext() {
        return (DynamicTemplateParameterContext) contexts.get(TemplateParameterContext.DYNAMIC);
    }

    public SegmentTemplateParameterContext getSegmentContext() {
        return (SegmentTemplateParameterContext) contexts.get(TemplateParameterContext.SEGMENT);
    }

    public SequenceTemplateParameterContext getSequenceContext() {
        return (SequenceTemplateParameterContext) contexts.get(TemplateParameterContext.SEQUENCE);
    }

    public ResourceTemplateParameterContext getResourceContext() {
        return (ResourceTemplateParameterContext) contexts.get(TemplateParameterContext.RESOURCE);
    }

    /**
     * @return the working directory where the output file as well as all tmp files are created.
     */
    public String getWorkingDir() {
        return workingDir;
    }


    /**
     * @return a config provider corresponding to config.xml
     */
    public ConfigXmlProvider getConfigProvider() {
        return configProvider;
    }

    /**
     * @return a conversion provider corresponding to conversion.xml.
     */
    public ConversionXmlProvider getConversionProvider() {
        return conversionProvider;
    }

    private void initContexts() {
        for (TemplateParameterContext contextType : TemplateParameterContext.values()) {
            ITemplateParameterContext context = createContext(contextType);
            if (context != null) {
                contexts.put(contextType, context);
            }
        }
    }

    private ITemplateParameterContext createContext(TemplateParameterContext context) {
        switch (context) {
            case TMP:
                return new TmpTemplateParameterContext(conversionProvider.getFormat());
            case TOOL:
                return new ToolTemplateParameterContext(configProvider.getConfig());
            case DYNAMIC:
                return new DynamicTemplateParameterContext(this);
            case SEGMENT:
                return new SegmentTemplateParameterContext();
            case SEQUENCE:
                return new SequenceTemplateParameterContext();
            case RESOURCE:
                return new ResourceTemplateParameterContext();
        }
        return null;
    }

}
