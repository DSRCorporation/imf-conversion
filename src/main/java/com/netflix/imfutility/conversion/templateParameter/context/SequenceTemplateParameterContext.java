package com.netflix.imfutility.conversion.templateParameter.context;

import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.TemplateParameter;
import com.netflix.imfutility.conversion.templateParameter.exception.TemplateParameterNotFoundException;
import com.netflix.imfutility.conversion.templateParameter.exception.UnknownTemplateParameterNameException;
import com.netflix.imfutility.xsd.conversion.SequenceType;

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

    private Map<SequenceType, SequenceData> sequences = new HashMap<>();

    public void initDefaultSequenceParameters(SequenceType seqType, int sequenceCount) {
        for (int seq = 0; seq < sequenceCount; seq++) {
            doAddParameter(seqType, seq, SequenceContextParameters.NUM, String.valueOf(seq));
            doAddParameter(seqType, seq, SequenceContextParameters.TYPE, seqType.value());
        }
    }

    public void addSequenceParameter(SequenceType seqType, int seq, SequenceContextParameters paramName, String paramValue) {
        doAddParameter(seqType, seq, paramName, paramValue);
    }

    private void doAddParameter(SequenceType seqType, int seq, SequenceContextParameters paramName, String paramValue) {
        SequenceData sequenceData = sequences.get(seqType);
        if (sequenceData == null) {
            sequenceData = new SequenceData();
            sequences.put(seqType, sequenceData);
        }
        sequenceData.addParameter(seq, paramName, paramValue);
    }

    public int getSequenceCount(SequenceType sequenceType) {
        SequenceData sequenceData = sequences.get(sequenceType);
        if (sequenceData == null) {
            return 0;
        }
        return sequenceData.getSequenceCount();
    }

    @Override
    public String resolveTemplateParameter(TemplateParameter templateParameter, ContextInfo contextInfo) {
        if (contextInfo.getSequence() < 0) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("Incorrect sequence number '%d'. Sequence number must be specified for a sequence template parameter.",
                            contextInfo.getSequence()));
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

        SequenceParametersData parameterData = sequenceData.getParameterData(contextInfo.getSequence());
        if (parameterData == null) {
            throw new TemplateParameterNotFoundException(
                    templateParameter.toString(),
                    String.format("Sequence Context for %d sequence is not defined. Context for %d sequences only are defined.",
                            contextInfo.getSequence(), sequenceData.getSequenceCount()));
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

        private final Map<Integer, SequenceParametersData> sequenceParams = new HashMap<>();

        public SequenceParametersData getParameterData(int sequenceNum) {
            return sequenceParams.get(sequenceNum);
        }

        public int getSequenceCount() {
            return sequenceParams.size();
        }

        public void addParameter(int sequenceNum, SequenceContextParameters paramName, String paramValue) {
            SequenceParametersData sequenceParamData = sequenceParams.get(sequenceNum);
            if (sequenceParamData == null) {
                sequenceParamData = new SequenceParametersData();
                sequenceParams.put(sequenceNum, sequenceParamData);
            }
            sequenceParamData.addParameter(paramName, paramValue);

            sequenceParams.put(sequenceNum, sequenceParamData);
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
