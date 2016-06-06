package com.netflix.imfutility.cpl;

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.asset.AssetMap;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;
import com.netflix.imfutility.cpl._2013.Cpl2013ContextBuilder;
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
     * @param cplXml a full path to the input CPL file.
     * @throws XmlParsingException   if input is not a valid XML or it doesn't pass XSD validation
     * @throws FileNotFoundException if the input path doesn't define a file.
     */
    public void build(String cplXml) throws XmlParsingException, FileNotFoundException {
        File cplFile = new File(cplXml);
        if (!cplFile.isFile()) {
            throw new FileNotFoundException(String.format("Invalid CPL file: '%s' not found", cplFile.getAbsolutePath()));
        }

        // 1. get the CPL namespace
        String cplNamespaceStr = XmlParser.getNamespace(cplFile);
        CplNamespace cplNamespace = CplNamespace.fromName(cplNamespaceStr);
        if (cplNamespace == null) {
            throw new ConversionException(String.format(
                    "CPL '%s' has unsupported namespace '%s'",
                    cplFile.getAbsolutePath(), cplNamespaceStr));
        }

        // 2. call a CPL parser depending on the namespace.
        switch (cplNamespace) {
            case CPL_2013:
                new Cpl2013ContextBuilder(contextProvider, assetMap).build(cplFile);
                break;
            case CPL_2016:
                // TODO
                break;
        }
    }

}
