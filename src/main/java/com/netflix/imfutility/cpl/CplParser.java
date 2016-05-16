package com.netflix.imfutility.cpl;

import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.cpl._2013.Cpl2013Parser;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;

import java.io.File;

/**
 * Created by Alexander on 5/14/2016.
 */
public class CplParser {

    private final TemplateParameterContextProvider contextProvider;
    private final AssetMap assetMap;

    public CplParser(TemplateParameterContextProvider contextProvider, AssetMap assetMap) {
        this.contextProvider = contextProvider;
        this.assetMap = assetMap;
    }

    public void parse(String cplXml) throws XmlParsingException {
        // 1. get the CPL namespace
        File cplFile = new File(cplXml);
        String cplNamespaceStr = XmlParser.getNamespace(cplFile);
        CplNamespace cplNamespace = CplNamespace.fromName(cplNamespaceStr);
        if (cplNamespace == null) {
            throw new RuntimeException(String.format(
                    "CPL '%s' has unsupported namespace '%s'",
                    cplXml, cplNamespaceStr));
        }

        // 2. call a CPL parser depending on the namespace.
        switch (cplNamespace) {
            case CPL_2013:
                new Cpl2013Parser(contextProvider, assetMap).parse(cplXml);
                break;
            case CPL_2016:
                // TODO
                break;
        }
    }

}
