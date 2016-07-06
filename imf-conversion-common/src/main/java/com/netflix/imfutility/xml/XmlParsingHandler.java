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
package com.netflix.imfutility.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Alexandr on 5/6/2016.
 * A helper to get human readable errors of an xml when loading and parsing.
 */
public class XmlParsingHandler implements ContentHandler, ErrorHandler {

    final Logger logger = LoggerFactory.getLogger(XmlParsingHandler.class);

    /**
     * Input XML file.
     */
    private final String xml;
    /**
     * A node name that is being processed
     */
    private String qname;

    /**
     * A stack of current parsed nodes.
     */
    private final Stack<String> qnames = new Stack<>();
    /**
     * A collection of all found errors.
     */
    private final List<String> errorMessages = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param xml the XML file to be parsed
     */
    public XmlParsingHandler(String xml) {
        this.xml = xml;
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
    }

    public void endDocument() throws SAXException {
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        qnames.pop();
        qname = qnames.size() > 0 ? qnames.lastElement() : "root";
    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }

    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void skippedEntity(String name) throws SAXException {
    }

    public void startDocument() throws SAXException {
    }

    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        qnames.push(qName);
        qname = qName;
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
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

    /**
     * Returns all found errors.
     *
     * @return a collection with all found errors.
     */
    public List<String> getParsingErrors() {
        return errorMessages;
    }

    /**
     * Registers error.
     *
     * @param exception current SAXParseException with error description.
     */
    private void registerError(SAXParseException exception) {
        StringBuilder errorMessage = new StringBuilder();

        errorMessage.append(xml).append(": ");
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
