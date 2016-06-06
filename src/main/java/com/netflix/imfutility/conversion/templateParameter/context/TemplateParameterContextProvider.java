package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.conversion.templateParameter.TemplateParameterContext;
import com.netflix.imfutility.xsd.config.ConfigType;
import com.netflix.imfutility.xsd.conversion.FormatType;

import java.util.HashMap;
import java.util.Map;

/**
 * A holder of all template parameter contexts: tool, tmp, dynamic, segment, etc.
 */
public class TemplateParameterContextProvider {

    private final FormatType format;
    private final ConfigType config;
    private final String workingDir;

    private final Map<TemplateParameterContext, ITemplateParameterContext> contexts = new HashMap<>();

    public TemplateParameterContextProvider(ConfigType config, FormatType format, String workingDir) {
        this.config = config;
        this.format = format;
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

    public String getWorkingDir() {
        return workingDir;
    }

    public ConfigType getConfig() {
        return config;
    }

    public FormatType getFormat() {
        return format;
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
                return new TmpTemplateParameterContext(format);
            case TOOL:
                return new ToolTemplateParameterContext(config);
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
