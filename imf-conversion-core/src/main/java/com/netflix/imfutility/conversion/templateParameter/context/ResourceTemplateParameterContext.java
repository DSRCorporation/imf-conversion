/**
 * Copyright (C) 2016 Netflix, Inc.
 *
 *     This file is part of IMF Conversion Utility.
 *
 *     IMF Conversion Utility is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     IMF Conversion Utility is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with IMF Conversion Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfoBuilder;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;
import com.netflix.imfutility.conversion.templateParameter.exception.UnknownTemplateParameterNameException;
import com.netflix.imfutility.cpl.uuid.ResourceUUID;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Resource Template Parameter Context.
 * <ul>
 * <li>It's used to replace resource template parameters in conversion operations</li>
 * <li>May contain only supported resource parameters (see {@link ResourceContextParameters}</li>
 * <li>Created dynamically in the code when analyzing CPL.</li>
 * <li>Contains the following information for each resource within each segment of each sequence (virtual track):
 * <ul>
 * <li>Essence full path</li>
 * <li>Start time</li>
 * <li>Duration</li>
 * <li>Resource UUID</li>
 * <li>Resource number</li>
 * </ul>
 * </li>
 * </ul>
 */
public class ResourceTemplateParameterContext implements ITemplateParameterContext {

    private static class ResourceData extends ContextData<ResourceUUID, ResourceContextParameters> {
    }

    private final Map<ResourceKey, ResourceData> resources = new LinkedHashMap<>();

    /**
     * Inits a resource parameter defined by the given key and UUID. Defines default parameters (such as Resource UUID and number).
     * The method must be called for each resource before adding another parameters.
     *
     * @param resourceKey a resource key defining the parameter.
     * @param uuid        resource UUID.
     * @return this resource template parameters context.
     */
    public ResourceTemplateParameterContext initResource(ResourceKey resourceKey, ResourceUUID uuid) {
        if (!resources.containsKey(resourceKey) || !resources.get(resourceKey).contains(uuid)) {
            int resourceNum = getResourceCount(resourceKey);
            doAddParameter(resourceKey, uuid, ResourceContextParameters.UUID, uuid.getUuid());
            doAddParameter(resourceKey, uuid, ResourceContextParameters.NUM, String.valueOf(resourceNum));
        }
        return this;
    }

    /**
     * Adds a resource parameter.
     *
     * @param resourceKey a resource key defining the parameter.
     * @param uuid        resource UUID.
     * @param paramName   a enum defining the parameter name.
     * @param paramValue  parameter value
     * @return this resource template parameters context.
     */
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

    /**
     * @param resourceKey a resource key defining the parameter.
     * @return total count of resources for the segment and sequence (virtual track) defined by the given key.
     */
    public int getResourceCount(ResourceKey resourceKey) {
        ResourceData resourceData = resources.get(resourceKey);
        if (resourceData == null) {
            return 0;
        }
        return resourceData.getCount();
    }

    /**
     * @param resourceKey a resource key defining the parameter.
     * @return all Resource UUIDs for the segment and sequence (virtual track) defined by the given key.
     * The order of the UUIDS is the order as they were added.
     */
    public Collection<ResourceUUID> getUuids(ResourceKey resourceKey) {
        ResourceData resourceData = resources.get(resourceKey);
        if (resourceData == null) {
            return Collections.emptyList();
        }
        return resourceData.getUuids();
    }

    /**
     * How many times the resource should be repeated.
     *
     * @param resourceKey  a resource key defining the parameter.
     * @param resourceUuid a resource UUID defining the parameter.
     * @return a positive number of how many times the resource should be repeated (default is 1).
     */
    public long getRepeatCount(ResourceKey resourceKey, ResourceUUID resourceUuid) {
        ContextInfo contextInfo = new ContextInfoBuilder()
                .setSequenceUuid(resourceKey.getSequenceUuid())
                .setSequenceType(resourceKey.getSequenceType())
                .setSegmentUuid(resourceKey.getSegmentUuid())
                .setResourceUuid(resourceUuid)
                .build();
        String repeatCountStr = getParameterValue(ResourceContextParameters.REPEAT_COUNT, contextInfo);
        return Long.parseLong(repeatCountStr);
    }

    /**
     * @param resourceParameter a enum defining the parameter name.
     * @param contextInfo       a context info. Must  contain information about segment, sequence and resource.
     * @return resolved parameter value as a string. Never null.
     */
    public String getParameterValue(ResourceContextParameters resourceParameter, ContextInfo contextInfo) {
        return getParameterValue(
                new TemplateParameter(TemplateParameterContext.RESOURCE, resourceParameter.getName()),
                resourceParameter,
                contextInfo);
    }

    /**
     * Resolves the given parameter.
     * The returned value is never null.
     * A runtime exception is thrown if parameter can not be resolved.
     *
     * @param templateParameter the template parameter to be resolved.
     * @param contextInfo       a context info. Must  contain information about segment, sequence and resource.
     * @return resolved parameter value as a string. Never null.
     */
    @Override
    public String resolveTemplateParameter(TemplateParameter templateParameter, ContextInfo contextInfo) {
        ResourceContextParameters resourceParameter = ResourceContextParameters.fromName(templateParameter.getName());
        if (resourceParameter == null) {
            throw new UnknownTemplateParameterNameException(
                    templateParameter.toString(),
                    String.format("Unknown Resource Template Parameter Name '%s'. Supported Resource Parameter Names: %s'",
                            templateParameter.getName(), ResourceContextParameters.getSupportedContextParameters()));
        }

        return getParameterValue(templateParameter, resourceParameter, contextInfo);
    }

    private String getParameterValue(TemplateParameter templateParameter, ResourceContextParameters resourceParameter, ContextInfo contextInfo) {
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

        ContextParameterData<ResourceContextParameters> parameterData = resourceData.getParameterData(contextInfo.getResourceUuid());
        if (parameterData == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("Resource Context for %s resource is not defined. Context for %d resources only are defined.",
                            contextInfo.getSequenceUuid(), resourceData.getCount()));
        }

        String parameterValue = parameterData.getParameterValue(resourceParameter);
        if (parameterValue == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("'%s' parameter is not defined for for %s sequence, '%s' sequence type and %s segment",
                            templateParameter.getName(),
                            contextInfo.getSequenceUuid(), contextInfo.getSequenceType().value(), contextInfo.getSegmentUuid()));
        }
        return parameterValue;
    }


}
