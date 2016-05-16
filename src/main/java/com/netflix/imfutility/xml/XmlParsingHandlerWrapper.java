package com.netflix.imfutility.xml;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.io.File;

/**
 * Created by Alexandr on 5/6/2016.
 * A helper to get human readable errors of an xml when loading and parsing.
 */
public class XmlParsingHandlerWrapper extends XmlParsingHandler {

    /**
     * Unmarshaller content handler that actually parses the xml.
     */
    private ContentHandler contentHandler;

    /**
     * Constructor.
     *
     * @param contentHandler Unmarshaller content handler that actually parses the metadata.xml.
     * @param xml            the XML file to be parsed
     */
    public XmlParsingHandlerWrapper(ContentHandler contentHandler, File xml) {
        super(xml);
        this.contentHandler = contentHandler;
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        contentHandler.characters(ch, start, length);
    }

    public void endDocument() throws SAXException {
        super.endDocument();
        contentHandler.endDocument();
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        super.endElement(uri, localName, qName);
        contentHandler.endElement(uri, localName, qName);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        super.endPrefixMapping(prefix);
        contentHandler.endPrefixMapping(prefix);
    }

    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
        super.ignorableWhitespace(ch, start, length);
        contentHandler.ignorableWhitespace(ch, start, length);
    }

    public void processingInstruction(String target, String data)
            throws SAXException {
        super.processingInstruction(target, data);
        contentHandler.processingInstruction(target, data);
    }

    public void setDocumentLocator(Locator locator) {
        super.setDocumentLocator(locator);
        contentHandler.setDocumentLocator(locator);
    }

    public void skippedEntity(String name) throws SAXException {
        super.skippedEntity(name);
        contentHandler.skippedEntity(name);
    }

    public void startDocument() throws SAXException {
        super.startDocument();
        contentHandler.startDocument();
    }

    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        super.startElement(uri, localName, qName, atts);
        contentHandler.startElement(uri, localName, qName, atts);
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        super.startPrefixMapping(prefix, uri);
        contentHandler.startPrefixMapping(prefix, uri);
    }


}
