package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;
import com.netflix.imfutility.conversion.templateParameter.exception.UnknownTemplateParameterNameException;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.xsd.conversion.SequenceType;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Sequence Template Parameter Context.
 * <ul>
 * <li>It's used to replace sequence template parameters in conversion operations</li>
 * <li>May contain any only supported sequence parameters (see {@link SequenceContextParameters}</li>
 * <li>Created dynamically in the code when analyzing CPL.</li>
 * </ul>
 */
public class SequenceTemplateParameterContext implements ITemplateParameterContext {

    private static class SequenceData extends ContextData<SequenceUUID, SequenceContextParameters> {
    }

    private final Map<SequenceType, SequenceData> sequences = new HashMap<>();

    public SequenceTemplateParameterContext initSequence(SequenceType seqType, SequenceUUID uuid) {
        if (!sequences.containsKey(seqType) || !sequences.get(seqType).contains(uuid)) {
            int seqNum = getSequenceCount(seqType);
            doAddParameter(seqType, uuid, SequenceContextParameters.UUID, uuid.getUuid());
            doAddParameter(seqType, uuid, SequenceContextParameters.TYPE, seqType.value());
            doAddParameter(seqType, uuid, SequenceContextParameters.NUM, String.valueOf(seqNum));
        }
        return this;
    }

    public SequenceTemplateParameterContext addSequenceParameter(SequenceType seqType, SequenceUUID uuid, SequenceContextParameters paramName, String paramValue) {
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

    public int getSequenceCount(SequenceType sequenceType) {
        SequenceData sequenceData = sequences.get(sequenceType);
        if (sequenceData == null) {
            return 0;
        }
        return sequenceData.getCount();
    }

    public Collection<SequenceUUID> getUuids(SequenceType sequenceType) {
        SequenceData sequenceData = sequences.get(sequenceType);
        if (sequenceData == null) {
            return Collections.emptyList();
        }
        return sequenceData.getUuids();
    }

    @Override
    public String resolveTemplateParameter(TemplateParameter templateParameter, ContextInfo contextInfo) {
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

        SequenceContextParameters sequenceParameterName = SequenceContextParameters.fromName(templateParameter.getName());
        if (sequenceParameterName == null) {
            throw new UnknownTemplateParameterNameException(
                    templateParameter.toString(),
                    String.format("Unknown Sequence Template Parameter Name '%s'. Supported Sequence Parameter Names: %s'",
                            templateParameter.getName(), SequenceContextParameters.getSupportedContextParameters()));
        }

        String parameterValue = parameterData.getParameterValue(sequenceParameterName);
        if (parameterValue == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("'%s' parameter is not defined.", templateParameter.getName()));
        }
        return parameterValue;
    }

}
