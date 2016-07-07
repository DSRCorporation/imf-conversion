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
    public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
        if (this.namespace == null) {
            this.namespace = uri;
        }
        super.startElement(uri, localName, qName, attrs);
    }

}
