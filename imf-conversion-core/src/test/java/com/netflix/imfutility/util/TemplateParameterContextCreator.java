/*
 * Copyright (C) 2016 Netflix, Inc.
 *
 * This file is part of IMF Conversion Utility.
 *
 * IMF Conversion Utility is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * IMF Conversion Utility is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with IMF Conversion Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.netflix.imfutility.util;

import com.netflix.imfutility.FakeFormat;
import com.netflix.imfutility.config.ConfigXmlProvider;
import com.netflix.imfutility.conversion.ConversionXmlProvider;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfoBuilder;
import com.netflix.imfutility.conversion.templateParameter.context.ResourceKey;
import com.netflix.imfutility.conversion.templateParameter.context.ResourceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.SegmentTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.SequenceTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.ResourceContextParameters;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SegmentContextParameters;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.SequenceContextParameters;
import com.netflix.imfutility.cpl.uuid.ResourceUUID;
import com.netflix.imfutility.cpl.uuid.SegmentUUID;
import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.generated.conversion.DestContextParamType;
import com.netflix.imfutility.generated.conversion.SequenceType;
import com.netflix.imfutility.resources.ResourceHelper;
import com.netflix.imfutility.xsd.conversion.DestContextTypeMap;

import java.io.File;
import java.util.EnumSet;

import static com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters.DURATION;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters.FRAME_RATE;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters.HEIGHT;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters.INTERLACED;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters.WIDTH;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Helper class to fill segment, sequence and resource contexts (it emulates parsing a CPL).
 */
public final class TemplateParameterContextCreator {

    public static final String SEGMENT_UUID_FORMAT = "urn:uuid:segm:%d";
    public static final String SEQUENCE_UUID_FORMAT = "urn:uuid:seq:%s:%d";
    public static final String RESOURCE_UUID_FORMAT = "urn:uuid:res:segm-%d-seq-%d-%s-%d-%d";
    public static final String RESOURCE_PARAMETER_FORMAT = "%s-%s";

    public static final String WORKING_DIR = "ImfUtilityTest";

    private TemplateParameterContextCreator() {
    }

    public static TemplateParameterContextProvider createDefaultContextProvider() throws Exception {
        ConfigXmlProvider configProvider = new ConfigXmlProvider(ConfigUtils.getCorrectConfigXml(), ConfigUtils.getCorrectConfigXmlPath());
        ConversionXmlProvider conversionProvider = new ConversionXmlProvider(ConversionUtils.getCorrectConversionXml(),
                ConversionUtils.getCorrectConversionXmlPath(), new FakeFormat());
        TemplateParameterContextProvider contextProvider = new TemplateParameterContextProvider(configProvider, conversionProvider, getWorkingDir());
        initEmptyDestContext(contextProvider);
        return contextProvider;
    }

    public static File getWorkingDir() {
        return new File(TemplateParameterContextCreator.getCurrentTmpDir(), WORKING_DIR);
    }


    public static TemplateParameterContextProvider createDefaultContextProviderWithCPLContext(
            int segmentCount, int seqCount, int resourceCount) throws Exception {
        TemplateParameterContextProvider contextProvider = createDefaultContextProvider();
        fillCPLContext(contextProvider, segmentCount, seqCount, resourceCount);
        return contextProvider;
    }

    public static TemplateParameterContextProvider createDefaultContextProviderWithCPLContext(
            int segmentCount, int seqCount, int resourceCount, EnumSet<SequenceType> sequenceTypes) throws Exception {
        TemplateParameterContextProvider contextProvider = createDefaultContextProvider();
        fillCPLContext(contextProvider, segmentCount, seqCount, resourceCount, 1, sequenceTypes);
        return contextProvider;
    }

    public static TemplateParameterContextProvider createDefaultContextProviderWithCPLContext(
            int segmentCount, int seqCount, int resourceCount, int repeatCountForResource,
            EnumSet<SequenceType> sequenceTypes) throws Exception {
        TemplateParameterContextProvider contextProvider = createDefaultContextProvider();
        fillCPLContext(contextProvider, segmentCount, seqCount, resourceCount, repeatCountForResource, sequenceTypes);
        return contextProvider;
    }

    public static TemplateParameterContextProvider createDefaultContextProviderWithDestContext(
            DestContextTypeMap destContextMap) throws Exception {
        TemplateParameterContextProvider contextProvider = createDefaultContextProvider();
        contextProvider.getDestContext().setDestContextMap(destContextMap);
        return contextProvider;
    }

    public static TemplateParameterContextProvider createDefaultContextProvider(
            String conversionXmlPath) throws Exception {
        ConfigXmlProvider configProvider = new ConfigXmlProvider(ConfigUtils.getCorrectConfigXml(), ConfigUtils.getCorrectConfigXmlPath());
        ConversionXmlProvider conversionProvider = new ConversionXmlProvider(ResourceHelper.getResourceInputStream(conversionXmlPath),
                conversionXmlPath, new FakeFormat());
        TemplateParameterContextProvider contextProvider = new TemplateParameterContextProvider(configProvider, conversionProvider,
                getWorkingDir());
        initEmptyDestContext(contextProvider);
        return contextProvider;
    }


    public static File getCurrentTmpDir() {
        String tempDir = System.getProperty("java.io.tmpdir");
        if (tempDir == null) {
            return new File(".");
        }
        return new File(tempDir);
    }

    public static void fillCPLContext(TemplateParameterContextProvider contextProvider, int segmentCount, int seqCount, int resourceCount) {
        fillCPLContext(contextProvider, segmentCount, seqCount, resourceCount, 1, EnumSet.allOf(SequenceType.class));
    }

    public static void fillCPLContext(TemplateParameterContextProvider contextProvider,
                                      int segmentCount, int seqCount, int resourceCount, int repeatCountForResource) {
        fillCPLContext(contextProvider, segmentCount, seqCount, resourceCount, repeatCountForResource, EnumSet.allOf(SequenceType.class));
    }

    public static void fillCPLContext(TemplateParameterContextProvider contextProvider, int segmentCount,
                                      int seqCount, int resourceCount, int repeatCountForResource,
                                      EnumSet<SequenceType> sequenceTypes) {
        // init segment ctxt
        SegmentTemplateParameterContext segmentContext = contextProvider.getSegmentContext();
        for (int i = 0; i < segmentCount; i++) {
            segmentContext.initSegment(getSegmentUuid(i));
        }

        // init sequence ctxt
        SequenceTemplateParameterContext sequenceContext = contextProvider.getSequenceContext();
        for (SequenceType seqType : sequenceTypes) {
            for (int i = 0; i < seqCount; i++) {
                sequenceContext.initSequence(seqType, getSequenceUuid(i, seqType));
            }
        }

        // init resource ctxt
        ResourceTemplateParameterContext resourceContext = contextProvider.getResourceContext();
        for (int segm = 0; segm < segmentCount; segm++) {
            for (int seq = 0; seq < seqCount; seq++) {
                for (int res = 0; res < resourceCount; res++) {
                    for (SequenceType seqType : sequenceTypes) {
                        for (int repeat = 0; repeat < repeatCountForResource; repeat++) {
                            ResourceKey resourceKey = ResourceKey.create(getSegmentUuid(segm), getSequenceUuid(seq, seqType), seqType);
                            ResourceUUID resourceUuid = getResourceUuid(segm, seq, seqType, res, repeat);

                            // init default params
                            resourceContext.initResource(resourceKey, resourceUuid);

                            // init essence, startTime and duration
                            fillResourceParam(resourceContext, resourceKey, resourceUuid,
                                    ResourceContextParameters.ESSENCE);
                            fillResourceParam(resourceContext, resourceKey, resourceUuid,
                                    ResourceContextParameters.DURATION_TIMECODE);
                            fillResourceParam(resourceContext, resourceKey, resourceUuid,
                                    ResourceContextParameters.DURATION_EDIT_UNIT);
                            fillResourceParam(resourceContext, resourceKey, resourceUuid,
                                    ResourceContextParameters.DURATION_FRAME_EDIT_UNIT);
                            fillResourceParam(resourceContext, resourceKey, resourceUuid,
                                    ResourceContextParameters.START_TIME_TIMECODE);
                            fillResourceParam(resourceContext, resourceKey, resourceUuid,
                                    ResourceContextParameters.START_TIME_EDIT_UNIT);
                            fillResourceParam(resourceContext, resourceKey, resourceUuid,
                                    ResourceContextParameters.START_TIME_FRAME_EDIT_UNIT);
                            fillResourceParam(resourceContext, resourceKey, resourceUuid,
                                    ResourceContextParameters.REPEAT_COUNT, String.valueOf(repeatCountForResource));
                        }
                    }
                }
            }
        }
    }

    public static SegmentUUID getSegmentUuid(int segm) {
        return SegmentUUID.create(
                String.format(SEGMENT_UUID_FORMAT, segm));
    }

    public static SequenceUUID getSequenceUuid(int seq, SequenceType seqType) {
        return SequenceUUID.create(
                String.format(SEQUENCE_UUID_FORMAT, seqType.value(), seq));
    }

    public static ResourceUUID getResourceUuid(int segm, int seq, SequenceType seqType, int res, int repeat) {
        return ResourceUUID.create(
                String.format(RESOURCE_UUID_FORMAT, segm, seq, seqType.value(), res, repeat), repeat);
    }

    public static void addResourceContextParameter(TemplateParameterContextProvider contextProvider, int segm, int seq,
                                                   SequenceType seqType, int res, int repeat,
                                                   ResourceContextParameters param, String paramValue) {
        contextProvider.getResourceContext().addResourceParameter(
                ResourceKey.create(getSegmentUuid(segm), getSequenceUuid(seq, seqType), seqType),
                getResourceUuid(segm, seq, seqType, res, repeat),
                param,
                paramValue);
    }

    public static void addResourceContextParameter(TemplateParameterContextProvider contextProvider, int segm, int seq,
                                                   SequenceType seqType, int res,
                                                   ResourceContextParameters param, String paramValue) {
        addResourceContextParameter(contextProvider, segm, seq, seqType, res, 0, param, paramValue);
    }

    public static void addSequenceContextParameter(TemplateParameterContextProvider contextProvider, int seq, SequenceType seqType,
                                                   SequenceContextParameters param, String paramValue) {
        contextProvider.getSequenceContext().addSequenceParameter(
                seqType,
                getSequenceUuid(seq, seqType),
                param,
                paramValue);
    }

    public static void addSegmentContextParameter(TemplateParameterContextProvider contextProvider, int segm,
                                                  SegmentContextParameters param, String paramValue) {
        contextProvider.getSegmentContext().addSegmentParameter(
                getSegmentUuid(segm),
                param,
                paramValue);
    }

    public static ContextInfo createResourceContextInfo(int segm, int seq, SequenceType seqType, int res, int repeat) {
        return new ContextInfoBuilder()
                .setSequenceType(seqType)
                .setSegmentUuid(getSegmentUuid(segm))
                .setSequenceUuid(getSequenceUuid(seq, seqType))
                .setResourceUuid(getResourceUuid(segm, seq, seqType, res, repeat))
                .build();
    }

    public static ContextInfo createSequenceContextInfo(int seq, SequenceType seqType) {
        return new ContextInfoBuilder()
                .setSequenceType(seqType)
                .setSequenceType(seqType)
                .build();
    }

    public static ContextInfo createSegmentContextInfo(int segm) {
        return new ContextInfoBuilder()
                .setSegmentUuid(getSegmentUuid(segm))
                .build();
    }

    private static void fillResourceParam(ResourceTemplateParameterContext resourceContext, ResourceKey resourceKey,
                                          ResourceUUID resourceUuid, ResourceContextParameters resParam) {
        resourceContext.addResourceParameter(
                resourceKey,
                resourceUuid,
                resParam,
                String.format(RESOURCE_PARAMETER_FORMAT, resourceUuid, resParam.getName())
        );
    }

    private static void fillResourceParam(ResourceTemplateParameterContext resourceContext, ResourceKey resourceKey,
                                          ResourceUUID resourceUuid, ResourceContextParameters resParam, String value) {
        resourceContext.addResourceParameter(
                resourceKey,
                resourceUuid,
                resParam,
                value
        );
    }

    public static void assertResourceParameter(String value, ResourceUUID resourceUuid, ResourceContextParameters resParam) {
        String expectedValue = String.format(RESOURCE_PARAMETER_FORMAT, resourceUuid.getUuid(), resParam.getName());
        assertNotNull(value);
        assertEquals(expectedValue, value);
    }

    public static DestContextTypeMap createDestContextMap(String name,
                                                          String width,
                                                          String height,
                                                          String frameRate,
                                                          String interlaced,
                                                          String duration) {
        DestContextTypeMap contextMap = new DestContextTypeMap();
        contextMap.setName(name);

        putDestContextValue(WIDTH.getName(), width, contextMap);
        putDestContextValue(HEIGHT.getName(), height, contextMap);
        putDestContextValue(FRAME_RATE.getName(), frameRate, contextMap);
        putDestContextValue(INTERLACED.getName(), interlaced, contextMap);
        putDestContextValue(DURATION.getName(), duration, contextMap);

        return contextMap;
    }

    public static void initEmptyDestContext(TemplateParameterContextProvider contextProvider) {
        DestContextTypeMap map = new DestContextTypeMap();
        map.setName("test");
        contextProvider.getDestContext().setDestContextMap(map);
    }

    public static void putDestContextValue(String paramName, String paramValue, DestContextTypeMap contextMap) {
        DestContextParamType param = new DestContextParamType();
        param.setName(paramName);
        param.setValue(paramValue);

        contextMap.getMap().put(paramName, param);
    }

}
