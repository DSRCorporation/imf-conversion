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
package com.netflix.imfutility.itunes.destcontext;

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.conversion.ConversionXmlProvider;
import com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters;
import com.netflix.imfutility.itunes.ITunesFormat;
import com.netflix.imfutility.itunes.inputparameters.ITunesInputParameters;
import com.netflix.imfutility.xml.XmlParsingException;
import com.netflix.imfutility.xsd.conversion.DestContextTypeMap;

import java.io.InputStream;
import java.util.Comparator;

import static com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters.FRAME_RATE;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters.HEIGHT;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters.INTERLACED;
import static com.netflix.imfutility.conversion.templateParameter.context.parameters.DestContextParameters.WIDTH;

/**
 * Prints all supported iTunes destination video formats.
 */
public class DestFormatPrinter {
    private final ConversionXmlProvider conversionProvider;

    public DestFormatPrinter(ITunesInputParameters inputParameters) {
        InputStream defaultConversionXml = inputParameters.getDefaultConversionXml();
        if (defaultConversionXml == null) {
            throw new ConversionException("Default conversion.xml is not found");
        }
        try {
            this.conversionProvider = new ConversionXmlProvider(
                    defaultConversionXml, inputParameters.getDefaultConversionXmlPath(), new ITunesFormat());
        } catch (XmlParsingException e) {
            throw new ConversionException("Can not parse default conversion.xml");
        }
    }

    public void printFormats() {
        conversionProvider.getFormat().getDestContexts().getMap().values().stream()
                .sorted(Comparator.comparing(DestContextTypeMap::getName))
                .forEachOrdered(this::printFormat);
    }

    private void printFormat(DestContextTypeMap destContext) {
        System.out.println(String.format("%-25s - %-4s x %-4s, %-10s fps, %s",
                destContext.getName(),
                getParameterValue(destContext, WIDTH),
                getParameterValue(destContext, HEIGHT),
                getParameterValue(destContext, FRAME_RATE),
                destContext.getMap().get(INTERLACED.getName()) == null ? "progressive" : "interlaced"
        ));
    }

    private String getParameterValue(DestContextTypeMap destContext, DestContextParameters parameter) {
        return destContext.getMap().get(parameter.getName()).getValue();
    }

}
