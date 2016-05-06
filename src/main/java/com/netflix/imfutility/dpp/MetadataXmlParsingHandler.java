package com.netflix.imfutility.dpp;

import com.netflix.imfutility.conversion.executor.ConversionExecutorOnce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.util.Vector;

/**
 * Created by Alexandr on 5/6/2016.
 */
public class MetadataXmlParsingHandler implements ContentHandler, ErrorHandler {

    final Logger logger = LoggerFactory.getLogger(MetadataXmlParsingHandler.class);

    private ContentHandler contentHandler;
    private String qname;
    private String namespaceURI;

    private Vector<String> errorMessages = new Vector<String>();

    public MetadataXmlParsingHandler(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        contentHandler.characters(ch, start, length);
    }

    public void endDocument() throws SAXException {
        contentHandler.endDocument();
    }

    public void endElement(String uri, String localName, String qName)
                throws SAXException {
        qname = qName;
        namespaceURI = uri;
        contentHandler.endElement(uri, localName, qName);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        contentHandler.endPrefixMapping(prefix);
    }

    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
        contentHandler.ignorableWhitespace(ch, start, length);
    }

    public void processingInstruction(String target, String data)
            throws SAXException {
        contentHandler.processingInstruction(target, data);
    }

    public void setDocumentLocator(Locator locator) {
        contentHandler.setDocumentLocator(locator);
    }

    public void skippedEntity(String name) throws SAXException {
        contentHandler.skippedEntity(name);
    }

    public void startDocument() throws SAXException {
        contentHandler.startDocument();
    }

    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        qname = qName;
        namespaceURI = uri;
        contentHandler.startElement(uri, localName, qName, atts);
    }

    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
        contentHandler.startPrefixMapping(prefix, uri);
    }

    public void error(SAXParseException exception) throws SAXException {
        registerError(exception);
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        registerError(exception);
    }

    public void warning(SAXParseException exception) throws SAXException {
        registerError(exception);
    }

    public Vector<String> getParsingErrors() {
        return errorMessages;
    }

    private void registerError(SAXParseException exception) {
        StringBuilder errorMessage = new StringBuilder();;
        errorMessage.append("Line ").append(exception.getLineNumber()).append(", ");
        errorMessage.append("column ").append(exception.getColumnNumber());

        if (qname != null) {
            errorMessage.append(", ").append("node <").append(qname).append(">");
        }
        errorMessage.append(". ").append(exception.getLocalizedMessage());

        errorMessages.add(errorMessage.toString());

        logger.error(errorMessage.toString());
    }
}
