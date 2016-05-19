package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SegmentContextParameters;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;
import com.netflix.imfutility.conversion.templateParameter.exception.UnknownTemplateParameterNameException;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Segment Template Parameter Context.
 * <ul>
 * <li>It's used to replace segment template parameters in conversion operations</li>
 * <li>May contain any only supported segment parameters (see {@link SegmentContextParameters}</li>
 * <li>Created dynamically in the code when analyzing CPL.</li>
 * </ul>
 */
public class SegmentTemplateParameterContext implements ITemplateParameterContext {

    private static class SegmentParameterData extends ContextParameterData<SegmentContextParameters> {
    }

    private final Map<SegmentUUID, SegmentParameterData> segments = new LinkedHashMap<>();

    public SegmentTemplateParameterContext addSegmentParameter(SegmentUUID uuid, SegmentContextParameters paramName, String paramValue) {
        initSegment(uuid);
        doAddParameter(uuid, paramName, paramValue);
        return this;
    }

    public SegmentTemplateParameterContext initSegment(SegmentUUID uuid) {
        if (!segments.containsKey(uuid)) {
            int segmNum = segments.size();
            doAddParameter(uuid, SegmentContextParameters.UUID, uuid.getUuid());
            doAddParameter(uuid, SegmentContextParameters.NUM, String.valueOf(segmNum));
        }
        return this;
    }

    private void doAddParameter(SegmentUUID uuid, SegmentContextParameters paramName, String paramValue) {
        SegmentParameterData segmentData = segments.get(uuid);
        if (segmentData == null) {
            segmentData = new SegmentParameterData();
            segments.put(uuid, segmentData);
        }
        segmentData.addParameter(paramName, paramValue);
    }

    public int getSegmentsNum() {
        return segments.size();
    }

    public Collection<SegmentUUID> getUuids() {
        return segments.keySet();
    }

    public String getParameterValue(SegmentContextParameters segmParameter, ContextInfo contextInfo) {
        return getParameterValue(
                new TemplateParameter(TemplateParameterContext.SEGMENT, segmParameter.getName()),
                segmParameter,
                contextInfo);
    }

    @Override
    public String resolveTemplateParameter(TemplateParameter templateParameter, ContextInfo contextInfo) {
        SegmentContextParameters segmentParameter = SegmentContextParameters.fromName(templateParameter.getName());
        if (segmentParameter == null) {
            throw new UnknownTemplateParameterNameException(
                    templateParameter.toString(),
                    String.format("Unknown Segment Template Parameter Name '%s'. Supported Segment Parameter Names: %s'",
                            templateParameter.getName(), SegmentContextParameters.getSupportedContextParameters()));
        }

        return getParameterValue(templateParameter, segmentParameter, contextInfo);
    }

    private String getParameterValue(TemplateParameter templateParameter, SegmentContextParameters segmParameter, ContextInfo contextInfo) {
        if (contextInfo.getSegmentUuid() == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(), "Segment UUID is not specified. Segment UUID is required for a segment template parameter.");
        }

        SegmentParameterData segmentData = segments.get(contextInfo.getSegmentUuid());
        if (segmentData == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("Segment Context for '%s' segment is not defined. Context for '%d' segments only is defined.",
                            contextInfo.getSegmentUuid(), segments.size()));
        }

        String parameterValue = segmentData.getParameterValue(segmParameter);
        if (parameterValue == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("'%s' parameter is not defined.", templateParameter.getName()));
        }
        return parameterValue;
    }


}
