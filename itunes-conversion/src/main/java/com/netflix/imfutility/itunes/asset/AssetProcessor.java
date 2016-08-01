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
package com.netflix.imfutility.itunes.asset;

import com.netflix.imfutility.itunes.xmlprovider.MetadataXmlProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * Abstract class to manage asset processing.
 * Includes 4 steps:
 * <ul>
 * <li>Asset validation</li>
 * <li>Asset metadata info generation</li>
 * <li>Asset metadata info appending</li>
 * <li>Asset distribution to destination package</li>
 * </ul>
 *
 * @param <T> metadata tag class
 */
public abstract class AssetProcessor<T> {
    protected final MetadataXmlProvider metadataXmlProvider;
    protected final File destDir;

    public AssetProcessor(MetadataXmlProvider metadataXmlProvider, File destDir) {
        this.metadataXmlProvider = metadataXmlProvider;
        this.destDir = destDir;
    }

    public void process(File assetFile) throws AssetValidationException, IOException {
        if (!checkInput(assetFile)) {
            throw new AssetValidationException("Mandatory parameters for processor must be set");
        }
        validate(assetFile);
        appendMetadata(buildMetadata(assetFile));
        distribute(assetFile);
    }

    protected boolean checkInput(File assetFile) {
        return assetFile.exists() && assetFile.isFile();
    }

    protected abstract void validate(File assetFile) throws AssetValidationException;

    protected abstract T buildMetadata(File assetFile);

    protected abstract void appendMetadata(T tag);

    protected void distribute(File assetFile) throws IOException {
        File destFile = new File(destDir, getFileName());
        try (OutputStream destOut = new FileOutputStream(destFile)) {
            Files.copy(assetFile.toPath(), destOut);
        }
    }

    protected abstract String getFileName();
}
