package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;
import com.netflix.imfutility.conversion.templateParameter.exception.UnknownTemplateParameterNameException;
import com.netflix.imfutility.xsd.conversion.SequenceType;

import java.util.HashMap;
import java.util.Map;

/**
 * Resource Template Parameter Context.
 * <ul>
 * <li>It's used to replace resource template parameters in conversion operations</li>
 * <li>May contain any only supported resource parameters (see {@link ResourceContextParameters}</li>
 * <li>Created dynamically in the code when analyzing CPL.</li>
 * </ul>
 */
public class ResourceTemplateParameterContext implements ITemplateParameterContext {

    private final Map<ResourceKey, ResourceData> resources = new HashMap<>();

    public void initDefaultResourceParameters(ResourceKey resourceKey, int resourceCount) {
        for (int res = 0; res < resourceCount; res++) {
            doAddParameter(resourceKey, res, ResourceContextParameters.NUM, String.valueOf(res));
        }
    }

    public void addResourceParameter(ResourceKey resourceKey, int resource, ResourceContextParameters paramName, String paramValue) {
        doAddParameter(resourceKey, resource, paramName, paramValue);
    }

    private void doAddParameter(ResourceKey resourceKey, int resource, ResourceContextParameters paramName, String paramValue) {
        ResourceData resourceData = resources.get(resourceKey);
        if (resourceData == null) {
            resourceData = new ResourceData();
            resources.put(resourceKey, resourceData);
        }
        resourceData.addParameter(resource, paramName, paramValue);
    }

    public int getResourceCount(int segmentNum, int sequenceNum, SequenceType sequenceType) {
        ResourceKey resourceKey = new ResourceKey(segmentNum, sequenceNum, sequenceType);
        ResourceData resourceData = resources.get(resourceKey);
        if (resourceData == null) {
            return 0;
        }
        return resourceData.getResourceCount();
    }

    @Override
    public String resolveTemplateParameter(TemplateParameter templateParameter, ContextInfo contextInfo) {
        if (contextInfo.getSegment() < 0) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("Incorrect segment number '%d'. Segment number must be specified for a resource template parameter.",
                            contextInfo.getSegment()));
        }
        if (contextInfo.getSequence() < 0) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("Incorrect sequence number '%d'. Sequence number must be specified for a resource template parameter.",
                            contextInfo.getSequence()));
        }
        if (contextInfo.getResource() < 0) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("Incorrect resource number '%d'. Resource number must be specified for a resource template parameter.",
                            contextInfo.getSequence()));
        }
        if (contextInfo.getSequenceType() == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(), "Sequence type must be specified for a resource template parameter.");
        }

        ResourceKey resourceKey = new ResourceKey(contextInfo.getSegment(), contextInfo.getSequence(), contextInfo.getSequenceType());
        ResourceData resourceData = resources.get(resourceKey);
        if (resourceData == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("Resource Context for %d sequence, '%s' sequence type and %d segment is not defined.",
                            contextInfo.getSequence(), contextInfo.getSequenceType().value(), contextInfo.getSegment()));
        }

        ResourceParameterData parameterData = resourceData.getParameterData(contextInfo.getResource());
        if (parameterData == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("Resource Context for %d resource is not defined. Context for %d resources only are defined.",
                            contextInfo.getSequence(), resourceData.getResourceCount()));
        }

        ResourceContextParameters resourceParameterName = ResourceContextParameters.fromName(templateParameter.getName());
        if (resourceParameterName == null) {
            throw new UnknownTemplateParameterNameException(
                    templateParameter.toString(),
                    String.format("Unknown Resource Template Parameter Name '%s'. Supported Resource Parameter Names: %s'",
                            templateParameter.getName(), ResourceContextParameters.getSupportedContextParameters()));
        }

        String parameterValue = parameterData.getParameterValue(resourceParameterName);
        if (parameterValue == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("'%s' parameter is not defined for for %d sequence, '%s' sequence type and %d segment",
                            templateParameter.getName(),
                            contextInfo.getSequence(), contextInfo.getSequenceType().value(), contextInfo.getSegment()));
        }
        return parameterValue;
    }


    private static class ResourceData {

        private final Map<Integer, ResourceParameterData> resourceParams = new HashMap<>();

        public ResourceParameterData getParameterData(int resourceNum) {
            return resourceParams.get(resourceNum);
        }

        public int getResourceCount() {
            return resourceParams.size();
        }

        public void addParameter(int resourceNum, ResourceContextParameters paramName, String paramValue) {
            ResourceParameterData resourceParamData = resourceParams.get(resourceNum);
            if (resourceParamData == null) {
                resourceParamData = new ResourceParameterData();
                resourceParams.put(resourceNum, resourceParamData);
            }
            resourceParamData.addParameter(paramName, paramValue);

            resourceParams.put(resourceNum, resourceParamData);
        }
    }

    private static class ResourceParameterData {

        private final Map<ResourceContextParameters, String> params = new HashMap<>();

        public String getParameterValue(ResourceContextParameters param) {
            return params.get(param);
        }

        public void addParameter(ResourceContextParameters paramName, String paramValue) {
            params.put(paramName, paramValue);
        }
    }

}
