package com.netflix.imfutility.conversion.templateParameter.context.segment;

import com.netflix.imfutility.conversion.templateParameter.SegmentTemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.xsd.conversion.SegmentType;

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
public class SegmentTemplateParameterContext implements ISegmentTemplateParameterContext {

    private final Map<Integer, SegmentData> segments = new HashMap<>();

    public void addSegmentParameter(int segment, SegmentType segmentType, SegmentContextParameters paramName, String paramValue) {
        SegmentData segmentData = segments.get(segment);
        if (segmentData == null) {
            segmentData = new SegmentData();
            segments.put(segment, segmentData);
        }
        segmentData.addParameter(segmentType, paramName, paramValue);
    }

    @Override
    public int getSegmentsNum() {
        return segments.size();
    }

    @Override
    public String resolveTemplateParameter(TemplateParameter templateParameter) {
        throw new RuntimeException("Segment context can be used with <execEachSegment> only.");
    }


    @Override
    public String resolveSegmentTemplateParameter(SegmentTemplateParameter templateParameter) {
        SegmentContextParameters segmentParameterName = SegmentContextParameters.fromName(templateParameter.getName());
        if (segmentParameterName == null) {
            throw new RuntimeException(
                    String.format("Unknown Segment Template Parameter Name '%s' in Template Parameter '%s'. Supported Segment Parameter Names: %s'",
                            templateParameter.getName(), templateParameter.toString(), getSupportedParameters()));

        }

        SegmentData segmentData = segments.get(templateParameter.getSegment());
        if (segmentData == null) {
            throw new RuntimeException(
                    String.format("Incorrect segment number '%d'. Context for '%s' segments only is available.",
                            templateParameter.getSegment(), segments.size()));
        }

        SegmentTypeData segmentTypeData = segmentData.getData(templateParameter.getSegmentType());
        if (segmentTypeData == null) {
            return null;
        }

        return segmentTypeData.getParameterValue(segmentParameterName);
    }

    private String getSupportedParameters() {
        StringBuilder supportedParamsBuilder = new StringBuilder();
        supportedParamsBuilder.append("[ ");
        for (SegmentContextParameters e : SegmentContextParameters.values()) {
            supportedParamsBuilder.append(e.getName());
            supportedParamsBuilder.append(" ");
        }
        supportedParamsBuilder.append("]");
        return supportedParamsBuilder.toString();
    }


    private static class SegmentData {

        private final Map<SegmentType, SegmentTypeData> segmentTypes = new HashMap<>();

        public SegmentTypeData getData(SegmentType segmentType) {
            return segmentTypes.get(segmentType);
        }

        public void addParameter(SegmentType segmentType, SegmentContextParameters paramName, String paramValue) {
            SegmentTypeData segmentTypeData = segmentTypes.get(segmentType);
            if (segmentTypeData == null) {
                segmentTypeData = new SegmentTypeData();
                segmentTypes.put(segmentType, segmentTypeData);
            }
            segmentTypeData.addParameter(paramName, paramValue);
        }
    }

    private static class SegmentTypeData {

        private final Map<SegmentContextParameters, String> params = new HashMap<>();

        public String getParameterValue(SegmentContextParameters param) {
            return params.get(param);
        }

        public void addParameter(SegmentContextParameters paramName, String paramValue) {
            params.put(paramName, paramValue);
        }
    }

}
