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
package com.netflix.imfutility.cpl;

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.asset.AssetMap;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.cpl._2013.Cpl2013ContextBuilder;
import com.netflix.imfutility.cpl._2016.Cpl2016ContextBuilder;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * A CPL parser. It analyzes the CPL namespace and chooses an appropriate CPL parser implementation which can process the CPL namespace.
 * <ul>
 * <li>Parses the given CPL</li>
 * <li>Fills segment, sequence and resource contexts, so conversion can be started using context parameters
 * (see {@link com.netflix.imfutility.conversion.templateParameter.context.SequenceTemplateParameterContext},
 * {@link com.netflix.imfutility.conversion.templateParameter.context.SegmentTemplateParameterContext},
 * {@link com.netflix.imfutility.conversion.templateParameter.context.ResourceTemplateParameterContext}).</li>
 * <p>
 * </ul>
 * <p>
 * Currently only "http://www.smpte-ra.org/schemas/2067-3/2013" namespace is supported.
 * </p>
 */
public class CplContextBuilder {

    private final TemplateParameterContextProvider contextProvider;
    private final AssetMap assetMap;

    public CplContextBuilder(TemplateParameterContextProvider contextProvider, AssetMap assetMap) {
        this.contextProvider = contextProvider;
        this.assetMap = assetMap;
    }

    /**
     * Parses the given CPL file and fills sequence, segment and resource contexts.
     *
     * @param cplFile a full path to the input CPL file.
     * @throws XmlParsingException   if input is not a valid XML or it doesn't pass XSD validation
     * @throws FileNotFoundException if the input path doesn't define a file.
     */
    public void build(File cplFile) throws XmlParsingException, FileNotFoundException {
        if (!cplFile.isFile()) {
            throw new FileNotFoundException(String.format("Invalid CPL file: '%s' not found", cplFile.getAbsolutePath()));
        }

        // 1. get the CPL namespace
        String cplNamespaceStr = XmlParser.getNamespace(cplFile);
        CplNamespace cplNamespace = CplNamespace.fromName(cplNamespaceStr);
        if (cplNamespace == null) {
            throw new ConversionException(String.format(
                    "CPL '%s' has unsupported namespace '%s'. Currently we support only the following namespaces: %s",
                    cplFile.getAbsolutePath(), cplNamespaceStr, CplNamespace.getSupportedNamespaces()));
        }

        // 2. call a CPL parser depending on the namespace.
        switch (cplNamespace) {
            case CPL_2013:
                new Cpl2013ContextBuilder(contextProvider, assetMap).build(cplFile);
                break;
            case CPL_2016:
                new Cpl2016ContextBuilder(contextProvider, assetMap).build(cplFile);
                break;
            default:
                throw new ConversionException(
                        String.format(
                                "Unsupported IMF namespace '%s'. Currently we support only '%s' and '%s'",
                                cplNamespace, CplNamespace.CPL_2013.getName(), CplNamespace.CPL_2016.getName()
                        ));
        }
    }

}
