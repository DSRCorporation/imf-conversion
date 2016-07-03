package com.netflix.imfutility.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Created by Alexander on 5/14/2016.
 * A helper to get human readable errors of an xml when loading and parsing, and get the namespace of the root element.
 */
public class XmlParsingNamespaceHandler extends XmlParsingHandler {

    private String namespace;

    /**
     * Constructor.
     *
     * @param xml the XML file to be parsed
     */
    public XmlParsingNamespaceHandler(String xml) {
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
