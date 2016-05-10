package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;
import com.netflix.imfutility.conversion.templateParameter.exception.UnknownTemplateParameterNameException;
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
public class SegmentTemplateParameterContext implements ITemplateParameterContext {

    private final Map<Integer, SegmentData> segments = new HashMap<>();

    public int getSegmentsNum() {
        return segments.size();
    }

    public void addSegmentParameter(int segment, SegmentType segmentType, SegmentContextParameters paramName, String paramValue) {
        SegmentData segmentData = segments.get(segment);
        if (segmentData == null) {
            segmentData = new SegmentData();
            segments.put(segment, segmentData);
        }
        segmentData.addParameter(segmentType, paramName, paramValue);
    }

    @Override
    public String resolveTemplateParameter(TemplateParameter templateParameter) {
        if (templateParameter.getSegment() < 0) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("Incorrect segment number '%d'. Segment number must be specified for a segment template parameter.",
                            templateParameter.getSegment()));
        }
        if (templateParameter.getSegmentType() == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(), "Segment type must be specified for a segment template parameter.");
        }

        SegmentData segmentData = segments.get(templateParameter.getSegment());
        if (segmentData == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("Incorrect segment number '%d'. Context for '%s' segments only is defined.",
                            templateParameter.getSegment(), segments.size()));
        }

        SegmentTypeData segmentTypeData = segmentData.getData(templateParameter.getSegmentType());
        if (segmentTypeData == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("Context for '%s' segment type is not defined.", templateParameter.getSegmentType()));
        }

        SegmentContextParameters segmentParameterName = SegmentContextParameters.fromName(templateParameter.getName());
        if (segmentParameterName == null) {
            throw new UnknownTemplateParameterNameException(
                    templateParameter.toString(),
                    String.format("Unknown Segment Template Parameter Name '%s'. Supported Segment Parameter Names: %s'",
                            templateParameter.getName(), SegmentContextParameters.getSupportedContextParameters()));
        }

        String parameterValue = segmentTypeData.getParameterValue(segmentParameterName);
        if (parameterValue == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("'%s' parameter is not defined.", templateParameter.getName()));
        }
        return parameterValue;
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
