package com.netflix.imfutility.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.File;

/**
 * Created by Alexander on 5/14/2016.
 */
public class XmlParsingNamespaceHandler extends XmlParsingHandler {

    private String namespace;

    /**
     * Constructor.
     *
     * @param xml the XML file to be parsed
     */
    public XmlParsingNamespaceHandler(File xml) {
        super(xml);
    }

    public String getNamespace() {
        return namespace;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (this.namespace == null) {
            this.namespace = uri;
        }
        super.startElement(uri, localName, qName, atts);
    }

}
