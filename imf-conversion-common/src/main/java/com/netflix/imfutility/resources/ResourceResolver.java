/*
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
package com.netflix.imfutility.resources;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Resolve resource input for schema validation.
 */
public class ResourceResolver implements LSResourceResolver {

    private final Collection<String> paths;

    public ResourceResolver(String... paths) {
        if (paths == null || paths.length == 0) {
            this.paths = Collections.emptyList();
            return;
        }
        this.paths = Arrays.asList(paths);
    }

    public ResourceResolver(Collection<String> paths) {
        this.paths = paths;
    }

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        InputStream resourceAsStream = tryToGetResource(systemId);
        return new Input(publicId, systemId, resourceAsStream);
    }

    private InputStream tryToGetResource(String systemId) {
        //  trying to get resource into specified paths
        for (String path : paths) {
            if (path != null && !path.isEmpty()) {
                InputStream inputStream = ResourceHelper.getResourceInputStreamImpl(getResourcePath(path, systemId));

                if (inputStream != null) {
                    return inputStream;
                }
            }
        }
        // tying to get resource from root
        return ResourceHelper.getResourceInputStream(systemId);
    }

    private String getResourcePath(String path, String systemId) {
        if (systemId.startsWith("..")) {
            systemId = systemId.substring(3, systemId.length());
            if (path.contains("/")) {
                path = path.substring(0, path.lastIndexOf("/"));
            }
        }
        return path + "/" + systemId;
    }

    /**
     * Custom input for {@link ResourceResolver}.
     */
    public static class Input implements LSInput {

        private String publicId;
        private String systemId;
        private BufferedInputStream inputStream;

        public Input(String publicId, String systemId, InputStream inputStream) {
            this.publicId = publicId;
            this.systemId = systemId;
            this.inputStream = new BufferedInputStream(inputStream);
        }

        @Override
        public Reader getCharacterStream() {
            return null;
        }

        @Override
        public void setCharacterStream(Reader characterStream) {

        }

        @Override
        public InputStream getByteStream() {
            return null;
        }

        @Override
        public void setByteStream(InputStream byteStream) {

        }

        @Override
        public String getStringData() {
            synchronized (inputStream) {
                try {
                    byte[] input = new byte[inputStream.available()];
                    inputStream.read(input);
                    String contents = new String(input, "UTF-8");
                    return contents;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public void setStringData(String stringData) {

        }

        @Override
        public String getSystemId() {
            return systemId;
        }

        @Override
        public void setSystemId(String systemId) {
            this.systemId = systemId;
        }

        @Override
        public String getPublicId() {
            return publicId;
        }

        @Override
        public void setPublicId(String publicId) {
            this.publicId = publicId;
        }

        @Override
        public String getBaseURI() {
            return null;
        }

        @Override
        public void setBaseURI(String baseURI) {

        }

        @Override
        public String getEncoding() {
            return null;
        }

        @Override
        public void setEncoding(String encoding) {

        }

        @Override
        public boolean getCertifiedText() {
            return false;
        }

        @Override
        public void setCertifiedText(boolean certifiedText) {

        }
    }
}
