/*
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
package com.netflix.imfutility.itunes.mediainfo;

import com.netflix.imfutility.conversion.executor.strategy.ExecuteStrategyFactory;
import com.netflix.imfutility.conversion.executor.strategy.OperationInfo;
import com.netflix.imfutility.conversion.templateParameter.ContextInfo;
import com.netflix.imfutility.conversion.templateParameter.context.DynamicTemplateParameterContext;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.generated.conversion.MediaInfoCommandType;
import com.netflix.imfutility.generated.mediainfo.FfprobeType;
import com.netflix.imfutility.mediainfo.MediaInfoException;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import com.netflix.imfutility.xsd.conversion.MediaInfoCommandOthersTypeMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.netflix.imfutility.CoreConstants.MEDIAINFO_PACKAGE;

/**
 * Media info builder for random asset.
 */
public class SimpleMediaInfoBuilder {

    private final TemplateParameterContextProvider contextProvider;
    private final ExecuteStrategyFactory executeStrategyFactory;

    private String commandName;
    private String inputDynamicParam;
    private String outputDynamycParam;

    public SimpleMediaInfoBuilder(TemplateParameterContextProvider contextProvider, ExecuteStrategyFactory executeStrategyFactory) {
        this.contextProvider = contextProvider;
        this.executeStrategyFactory = executeStrategyFactory;
    }

    public SimpleMediaInfoBuilder setCommandName(String commandName) {
        this.commandName = commandName;
        return this;
    }

    public SimpleMediaInfoBuilder setInputDynamicParam(String inputDynamicParam) {
        this.inputDynamicParam = inputDynamicParam;
        return this;
    }

    public SimpleMediaInfoBuilder setOutputDynamycParam(String outputDynamycParam) {
        this.outputDynamycParam = outputDynamycParam;
        return this;
    }

    private void initDynamicContext(File inputFile, File outputFile) {
        DynamicTemplateParameterContext dynamicContext = contextProvider.getDynamicContext();
        dynamicContext.addParameter(inputDynamicParam, inputFile.getAbsolutePath());
        dynamicContext.addParameter(outputDynamycParam, outputFile.getAbsolutePath(), true);
    }

    private MediaInfoCommandType getMediaInfoCommand() throws MediaInfoException {
        MediaInfoCommandOthersTypeMap commandsMap = contextProvider.getConversionProvider()
                .getFormat()
                .getMediaInfoCommandOthers();

        checkMediaInfoCommand(commandsMap, commandName);

        return commandsMap.getMap().get(commandName);
    }

    private void checkMediaInfoCommand(MediaInfoCommandOthersTypeMap commandsMap, String commandName) throws MediaInfoException {
        if (!commandsMap.getMap().containsKey(commandName)) {
            throw new MediaInfoException(commandName, String.format(
                    "%s command must be set in conversion.xml", commandName));
        }
    }

    private void checkFile(File file, String type) throws FileNotFoundException {
        if (!file.isFile()) {
            throw new FileNotFoundException(String.format(
                    "Invalid media info %s file: '%s' not found", type, file.getAbsolutePath()));
        }
    }

    protected File getOutputFile() {
        return new File(contextProvider.getWorkingDir(), outputDynamycParam + ".xml");
    }

    private void executeMediaInfoCommand(MediaInfoCommandType mediaInfoCommand, File inputFile, File outputFile) throws IOException {
        String operationName = String.format("%s_%s", mediaInfoCommand.getClass().getSimpleName(), inputFile.getName());
        OperationInfo operationInfo = new OperationInfo(mediaInfoCommand.getValue(),
                operationName, ContextInfo.EMPTY, false, outputFile);
        executeStrategyFactory.createExecuteOnceStrategy(contextProvider).execute(operationInfo);
    }

    protected FfprobeType parseOutputFile(File outputFile) throws XmlParsingException, FileNotFoundException {
        checkFile(outputFile, "output");
        // do not validate according to XSD as sometimes the output may contain not all required attributes
        return XmlParser.parse(outputFile, null, MEDIAINFO_PACKAGE, FfprobeType.class);
    }

    public FfprobeType build(File inputFile) throws XmlParsingException, IOException, MediaInfoException {
        checkFile(inputFile, "input");
        File outputFile = getOutputFile();

        initDynamicContext(inputFile, outputFile);
        executeMediaInfoCommand(getMediaInfoCommand(), inputFile, outputFile);

        return parseOutputFile(outputFile);
    }

}
