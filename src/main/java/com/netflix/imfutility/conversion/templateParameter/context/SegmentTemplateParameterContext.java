package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;
import com.netflix.imfutility.conversion.templateParameter.exception.UnknownTemplateParameterNameException;

import java.util.Collection;
import java.util.HashMap;
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

    private Map<String, SegmentParameterData> segments = new LinkedHashMap<>();

    public SegmentTemplateParameterContext addSegmentParameter(String uuid, SegmentContextParameters paramName, String paramValue) {
        initSegment(uuid);
        doAddParameter(uuid, paramName, paramValue);
        return this;
    }

    public SegmentTemplateParameterContext initSegment(String uuid) {
        if (!segments.containsKey(uuid)) {
            int segmNum = segments.size();
            doAddParameter(uuid, SegmentContextParameters.UUID, uuid);
            doAddParameter(uuid, SegmentContextParameters.NUM, String.valueOf(segmNum));
        }
        return this;
    }

    private void doAddParameter(String uuid, SegmentContextParameters paramName, String paramValue) {
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

    public Collection<String> getUuids() {
        return segments.keySet();
    }

    @Override
    public String resolveTemplateParameter(TemplateParameter templateParameter, ContextInfo contextInfo) {
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

        SegmentContextParameters segmentParameterName = SegmentContextParameters.fromName(templateParameter.getName());
        if (segmentParameterName == null) {
            throw new UnknownTemplateParameterNameException(
                    templateParameter.toString(),
                    String.format("Unknown Segment Template Parameter Name '%s'. Supported Segment Parameter Names: %s'",
                            templateParameter.getName(), SegmentContextParameters.getSupportedContextParameters()));
        }

        String parameterValue = segmentData.getParameterValue(segmentParameterName);
        if (parameterValue == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("'%s' parameter is not defined.", templateParameter.getName()));
        }
        return parameterValue;
    }

    private static class SegmentParameterData {

        private final Map<SegmentContextParameters, String> params = new HashMap<>();

        public String getParameterValue(SegmentContextParameters param) {
            return params.get(param);
        }

        public void addParameter(SegmentContextParameters paramName, String paramValue) {
            params.put(paramName, paramValue);
        }
    }

}
