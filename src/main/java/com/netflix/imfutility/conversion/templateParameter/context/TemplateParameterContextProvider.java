package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.conversion.templateParameter.TemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.segment.SegmentTemplateParameterContext;
import com.netflix.imfutility.xsd.config.IMFUtilityConfigType;
import com.netflix.imfutility.xsd.conversion.FormatType;

import java.util.Map;

/**
 * Created by Alexander on 4/26/2016.
 */
public class TemplateParameterContextProvider implements ITemplateParameterContextProvider {

    private FormatType format;
    private IMFUtilityConfigType config;

    private Map<TemplateParameterContext, ITemplateParameterContext> contexts;

    public TemplateParameterContextProvider(IMFUtilityConfigType config, FormatType format) {
        this.config = config;
        this.format = format;
        initContexts();
    }

    @Override
    public ITemplateParameterContext getContext(TemplateParameterContext context) {
        return contexts.get(context);
    }

    protected void initContexts() {
        for (TemplateParameterContext contextType : TemplateParameterContext.values()) {
            ITemplateParameterContext context = createContext(contextType);
            if (context != null) {
                contexts.put(contextType, context);
            }
        }
    }

    protected ITemplateParameterContext createContext(TemplateParameterContext context) {
        switch (context) {
            case TMP:
                return new TmpTemplateParameterContext(format);
            case TOOL:
                return new ToolTemplateParameterContext(config);
            case DYNAMIC:
                return new DynamicTemplateParameterContext();
            case SEGMENT:
                return new SegmentTemplateParameterContext();
        }
        return null;
    }

}
