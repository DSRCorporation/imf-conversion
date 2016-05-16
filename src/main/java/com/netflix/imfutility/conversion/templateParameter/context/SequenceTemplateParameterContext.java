package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;
import com.netflix.imfutility.conversion.templateParameter.exception.UnknownTemplateParameterNameException;
import com.netflix.imfutility.xsd.conversion.SequenceType;

import java.util.*;


/**
 * Sequence Template Parameter Context.
 * <ul>
 * <li>It's used to replace sequence template parameters in conversion operations</li>
 * <li>May contain any only supported sequence parameters (see {@link SequenceContextParameters}</li>
 * <li>Created dynamically in the code when analyzing CPL.</li>
 * </ul>
 */
public class SequenceTemplateParameterContext implements ITemplateParameterContext {

    private Map<SequenceType, SequenceData> sequences = new HashMap<>();

    public SequenceTemplateParameterContext initSequence(SequenceType seqType, String uuid) {
        if (!sequences.containsKey(seqType) || !sequences.get(seqType).contains(uuid)) {
            int seqNum = getSequenceCount(seqType);
            doAddParameter(seqType, uuid, SequenceContextParameters.UUID, uuid);
            doAddParameter(seqType, uuid, SequenceContextParameters.TYPE, seqType.value());
            doAddParameter(seqType, uuid, SequenceContextParameters.NUM, String.valueOf(seqNum));
        }
        return this;
    }

    public SequenceTemplateParameterContext addSequenceParameter(SequenceType seqType, String uuid, SequenceContextParameters paramName, String paramValue) {
        initSequence(seqType, uuid);
        doAddParameter(seqType, uuid, paramName, paramValue);
        return this;
    }

    private void doAddParameter(SequenceType seqType, String uuid, SequenceContextParameters paramName, String paramValue) {
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
        return sequenceData.getSequenceCount();
    }

    public Collection<String> getUuids(SequenceType sequenceType) {
        SequenceData sequenceData = sequences.get(sequenceType);
        if (sequenceData == null) {
            return Collections.EMPTY_LIST;
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

        SequenceParametersData parameterData = sequenceData.getParameterData(contextInfo.getSequenceUuid());
        if (parameterData == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("Sequence Context for '%s' sequence is not defined. Context for %d sequences only are defined.",
                            contextInfo.getSequenceUuid(), sequenceData.getSequenceCount()));
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

    private static class SequenceData {

        private final Map<String, SequenceParametersData> sequenceParams = new LinkedHashMap<>();

        public Collection<String> getUuids() {
            return sequenceParams.keySet();
        }

        public SequenceParametersData getParameterData(String uuid) {
            return sequenceParams.get(uuid);
        }

        public int getSequenceCount() {
            return sequenceParams.size();
        }

        public boolean contains(String uuid) {
            return sequenceParams.containsKey(uuid);
        }

        public void addParameter(String uuid, SequenceContextParameters paramName, String paramValue) {
            SequenceParametersData sequenceParamData = sequenceParams.get(uuid);
            if (sequenceParamData == null) {
                sequenceParamData = new SequenceParametersData();
                sequenceParams.put(uuid, sequenceParamData);
            }
            sequenceParamData.addParameter(paramName, paramValue);
        }
    }

    private static class SequenceParametersData {

        private final Map<SequenceContextParameters, String> params = new HashMap<>();

        public String getParameterValue(SequenceContextParameters param) {
            return params.get(param);
        }

        public void addParameter(SequenceContextParameters paramName, String paramValue) {
            params.put(paramName, paramValue);
        }
    }

}
