package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;
import com.netflix.imfutility.conversion.templateParameter.exception.UnknownTemplateParameterNameException;

import java.util.HashMap;
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

    private Map<Integer, SegmentParameterData> segments = new HashMap<>();

    public void initDefaultSegmentParameters(int segmentCount) {
        for (int segm = 0; segm < segmentCount; segm++) {
            doAddParameter(segm, SegmentContextParameters.NUM, String.valueOf(segm));
        }
    }

    public void addSegmentParameter(int segmentCount, SegmentContextParameters paramName, String paramValue) {
        doAddParameter(segmentCount, paramName, paramValue);
    }

    private void doAddParameter(int segment, SegmentContextParameters paramName, String paramValue) {
        SegmentParameterData segmentData = segments.get(segment);
        if (segmentData == null) {
            segmentData = new SegmentParameterData();
            segments.put(segment, segmentData);
        }
        segmentData.addParameter(paramName, paramValue);
    }

    public int getSegmentsNum() {
        return segments.size();
    }

    @Override
    public String resolveTemplateParameter(TemplateParameter templateParameter, ContextInfo contextInfo) {
        if (contextInfo.getSegment() < 0) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("Incorrect segment number '%d'. Segment number must be specified for a segment template parameter.",
                            contextInfo.getSegment()));
        }

        SegmentParameterData segmentData = segments.get(contextInfo.getSegment());
        if (segmentData == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("Segment Context for %d segment is not defined. Context for '%s' segments only is defined.",
                            contextInfo.getSegment(), segments.size()));
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
