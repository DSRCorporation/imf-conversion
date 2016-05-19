package com.netflix.imfutility.cpl;

import com.netflix.imfutility.asset.AssetMap;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.cpl._2013.Cpl2013ContextBuilder;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;

import java.io.File;

/**
 * A CPL parser. It analyzes the CPL namespace a chooses an appropriate CPL parser implementation which can process the CPL namespace.
 * <ul>
 * <li>Parses the given CPL</li>
 * <li>Fills segment, sequence and resource contexts, so conversion can be started using context parameters.</li>
 * </ul>
 */
public class CplContextBuilder {

    private final TemplateParameterContextProvider contextProvider;
    private final AssetMap assetMap;

    public CplContextBuilder(TemplateParameterContextProvider contextProvider, AssetMap assetMap) {
        this.contextProvider = contextProvider;
        this.assetMap = assetMap;
    }

    public void build(String cplXml) throws XmlParsingException {
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
                new Cpl2013ContextBuilder(contextProvider, assetMap).build(cplXml);
                break;
            case CPL_2016:
                // TODO
                break;
        }
    }

}
