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
import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;
import com.netflix.imfutility.conversion.templateParameter.exception.UnknownTemplateParameterNameException;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.SequenceType;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Sequence (virtual track) Template Parameter Context.
 * <ul>
 * <li>It's used to replace sequence template parameters in conversion operations</li>
 * <li>May contain any only supported sequence parameters (see {@link SequenceContextParameters}</li>
 * <li>Created dynamically in the code when analyzing CPL.</li>
 * <li>Contains the following information for each sequence:
 * <ul>
 * <li>Sequence UUID</li>
 * <li>Sequence number</li>
 * <li>Sequence type</li>
 * <li>Audio source parameters (such sample rate, bits per sample, etc.)</li>
 * <li>Video source parameters (such as fps, size, bit depth, pixel format, etc.)</li>
 * </ul>
 * </li>
 * <li>Audio and video source parameters are filled by {@link com.netflix.imfutility.mediainfo.MediaInfoContextBuilder}
 * while calling an external media info tool for each essence and each type.</li>
 * <li>It's assumed that all segments from a sequence (virtual track) must have equal audio and video parameters.</li>
 * </ul>
 */
public class SequenceTemplateParameterContext implements ITemplateParameterContext {

    private static class SequenceData extends ContextData<SequenceUUID, SequenceContextParameters> {
    }

    private final Map<SequenceType, SequenceData> sequences = new LinkedHashMap<>();

    /**
     * Inits a sequence (virtual track) parameter defined by the given UUID. Defines default parameters
     * (such as Sequence UUID, type and number).
     * The method must be called for each sequence before adding another parameters.
     *
     * @param seqType sequence type
     * @param uuid    sequence UUID.
     * @return this sequence template parameters context.
     */
    public SequenceTemplateParameterContext initSequence(SequenceType seqType, SequenceUUID uuid) {
        if (!sequences.containsKey(seqType) || !sequences.get(seqType).contains(uuid)) {
            int seqNum = getSequenceCount(seqType);
            doAddParameter(seqType, uuid, SequenceContextParameters.UUID, uuid.getUuid());
            doAddParameter(seqType, uuid, SequenceContextParameters.TYPE, seqType.value());
            doAddParameter(seqType, uuid, SequenceContextParameters.NUM, String.valueOf(seqNum));
        }
        return this;
    }

    /**
     * Adds a sequence (virtual track) parameter.
     *
     * @param seqType    sequence type
     * @param uuid       sequence UUID.
     * @param paramName  a enum defining the parameter name.
     * @param paramValue parameter value
     * @return this sequence template parameters context.
     */
    public SequenceTemplateParameterContext addSequenceParameter(SequenceType seqType, SequenceUUID uuid,
                                                                 SequenceContextParameters paramName, String paramValue) {
        initSequence(seqType, uuid);
        doAddParameter(seqType, uuid, paramName, paramValue);
        return this;
    }

    private void doAddParameter(SequenceType seqType, SequenceUUID uuid, SequenceContextParameters paramName, String paramValue) {
        SequenceData sequenceData = sequences.get(seqType);
        if (sequenceData == null) {
            sequenceData = new SequenceData();
            sequences.put(seqType, sequenceData);
        }
        sequenceData.addParameter(uuid, paramName, paramValue);
    }

    /**
     * Gets total count of sequences (virtual tracks) of the given type.
     *
     * @param sequenceType sequence type
     * @return total count of sequences (virtual tracks) of the given type.
     */
    public int getSequenceCount(SequenceType sequenceType) {
        SequenceData sequenceData = sequences.get(sequenceType);
        if (sequenceData == null) {
            return 0;
        }
        return sequenceData.getCount();
    }

    /**
     * Gets all Sequences UUIDs of the given type. The order of the UUIDS is the order as they were added.
     *
     * @param sequenceType sequence type
     * @return all Sequences UUIDs of the given type. The order of the UUIDS is the order as they were added.
     */
    public Collection<SequenceUUID> getUuids(SequenceType sequenceType) {
        SequenceData sequenceData = sequences.get(sequenceType);
        if (sequenceData == null) {
            return Collections.emptyList();
        }
        return sequenceData.getUuids();
    }

    /**
     * Gets all added sequence types.
     *
     * @return all added sequence types.
     */
    public Collection<SequenceType> getSequenceTypes() {
        return sequences.keySet();
    }

    public String getParameterValue(SequenceContextParameters seqParameter, ContextInfo contextInfo) {
        return getParameterValue(
                new TemplateParameter(TemplateParameterContext.SEQUENCE, seqParameter.getName()),
                seqParameter,
                contextInfo);
    }

    /**
     * Resolves the given parameter.
     * The returned value is never null.
     * A runtime exception is thrown if parameter can not be resolved.
     *
     * @param templateParameter the template parameter to be resolved.
     * @param contextInfo       a context info. Must contain information about the sequence (UUID and type).
     * @return resolved parameter value as a string. Never null.
     */
    @Override
    public String resolveTemplateParameter(TemplateParameter templateParameter, ContextInfo contextInfo) {
        SequenceContextParameters sequenceParameter = SequenceContextParameters.fromName(templateParameter.getName());
        if (sequenceParameter == null) {
            throw new UnknownTemplateParameterNameException(
                    templateParameter.toString(),
                    String.format("Unknown Sequence Template Parameter Name '%s'. Supported Sequence Parameter Names: %s'",
                            templateParameter.getName(), SequenceContextParameters.getSupportedContextParameters()));
        }

        return getParameterValue(templateParameter, sequenceParameter, contextInfo);
    }

    private String getParameterValue(TemplateParameter templateParameter, SequenceContextParameters seqParameter, ContextInfo contextInfo) {
        if (contextInfo.getSequenceUuid() == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    "Sequence UUID is not specified. Sequence UUID is required for a sequence template parameter.");
        }
        if (contextInfo.getSequenceType() == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(), "Sequence type must be specified for a sequence template parameter.");
        }

        SequenceData sequenceData = sequences.get(contextInfo.getSequenceType());
        if (sequenceData == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("Sequence Context for '%s' sequence type is not defined. Context for %d types only is defined.",
                            contextInfo.getSequenceType().value(), sequences.size()));
        }

        ContextParameterData<SequenceContextParameters> parameterData = sequenceData.getParameterData(contextInfo.getSequenceUuid());
        if (parameterData == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("Sequence Context for '%s' sequence is not defined. Context for %d sequences only are defined.",
                            contextInfo.getSequenceUuid(), sequenceData.getCount()));
        }

        String parameterValue = parameterData.getParameterValue(seqParameter);
        if (parameterValue == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("'%s' parameter is not defined.", templateParameter.getName()));
        }
        return parameterValue;
    }


}
