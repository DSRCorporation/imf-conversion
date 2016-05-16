package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;
import com.netflix.imfutility.conversion.templateParameter.exception.UnknownTemplateParameterNameException;
import com.netflix.imfutility.cpl.uuid.ResourceUUID;

import java.util.*;

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

    public ResourceTemplateParameterContext initResource(ResourceKey resourceKey, ResourceUUID uuid) {
        if (!resources.containsKey(resourceKey) || !resources.get(resourceKey).contains(uuid)) {
            int resourceNum = getResourceCount(resourceKey);
            doAddParameter(resourceKey, uuid, ResourceContextParameters.UUID, uuid.getUuid());
            doAddParameter(resourceKey, uuid, ResourceContextParameters.NUM, String.valueOf(resourceNum));
        }
        return this;
    }

    public ResourceTemplateParameterContext addResourceParameter(ResourceKey resourceKey, ResourceUUID uuid, ResourceContextParameters paramName, String paramValue) {
        initResource(resourceKey, uuid);
        doAddParameter(resourceKey, uuid, paramName, paramValue);
        return this;
    }

    private void doAddParameter(ResourceKey resourceKey, ResourceUUID uuid, ResourceContextParameters paramName, String paramValue) {
        ResourceData resourceData = resources.get(resourceKey);
        if (resourceData == null) {
            resourceData = new ResourceData();
            resources.put(resourceKey, resourceData);
        }
        resourceData.addParameter(uuid, paramName, paramValue);
    }

    public int getResourceCount(ResourceKey resourceKey) {
        ResourceData resourceData = resources.get(resourceKey);
        if (resourceData == null) {
            return 0;
        }
        return resourceData.getResourceCount();
    }

    public Collection<ResourceUUID> getUuids(ResourceKey resourceKey) {
        ResourceData resourceData = resources.get(resourceKey);
        if (resourceData == null) {
            return Collections.EMPTY_LIST;
        }
        return resourceData.getUuids();
    }

    @Override
    public String resolveTemplateParameter(TemplateParameter templateParameter, ContextInfo contextInfo) {
        if (contextInfo.getSegmentUuid() == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(), "Segment UUID is not specified. Segment UUID is required for a resource template parameter.");
        }
        if (contextInfo.getSequenceUuid() == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(), "Sequence UUID is not specified. Sequence UUID is required for a resource template parameter.");
        }
        if (contextInfo.getResourceUuid() == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(), "Resource UUID is not specified. Resource UUID must be specified for a resource template parameter.");
        }
        if (contextInfo.getSequenceType() == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(), "Sequence type must be specified for a resource template parameter.");
        }

        ResourceKey resourceKey = ResourceKey.create(contextInfo.getSegmentUuid(), contextInfo.getSequenceUuid(), contextInfo.getSequenceType());
        ResourceData resourceData = resources.get(resourceKey);
        if (resourceData == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("Resource Context for %s sequence, '%s' sequence type and %s segment is not defined.",
                            contextInfo.getSequenceUuid(), contextInfo.getSequenceType().value(), contextInfo.getSegmentUuid()));
        }

        ResourceParameterData parameterData = resourceData.getParameterData(contextInfo.getResourceUuid());
        if (parameterData == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("Resource Context for %s resource is not defined. Context for %d resources only are defined.",
                            contextInfo.getSequenceUuid(), resourceData.getResourceCount()));
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
                    String.format("'%s' parameter is not defined for for %s sequence, '%s' sequence type and %s segment",
                            templateParameter.getName(),
                            contextInfo.getSequenceUuid(), contextInfo.getSequenceType().value(), contextInfo.getSegmentUuid()));
        }
        return parameterValue;
    }

    private static class ResourceData {

        private final Map<ResourceUUID, ResourceParameterData> resourceParams = new LinkedHashMap<>();

        public Collection<ResourceUUID> getUuids() {
            return resourceParams.keySet();
        }

        public ResourceParameterData getParameterData(ResourceUUID uuid) {
            return resourceParams.get(uuid);
        }

        public int getResourceCount() {
            return resourceParams.size();
        }

        public boolean contains(ResourceUUID uuid) {
            return resourceParams.containsKey(uuid);
        }

        public void addParameter(ResourceUUID uuid, ResourceContextParameters paramName, String paramValue) {
            ResourceParameterData resourceParamData = resourceParams.get(uuid);
            if (resourceParamData == null) {
                resourceParamData = new ResourceParameterData();
                resourceParams.put(uuid, resourceParamData);
            }
            resourceParamData.addParameter(paramName, paramValue);
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
