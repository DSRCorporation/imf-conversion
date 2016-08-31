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

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.itunes.asset.type.Asset;
import com.netflix.imfutility.itunes.asset.distribute.DistributeAssetStrategy;
import com.netflix.imfutility.itunes.metadata.MetadataXmlProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

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
 * @param <T> type of processed asset.
 */
public abstract class AssetProcessor<T extends Asset> {
    private final Logger logger = LoggerFactory.getLogger(AssetProcessor.class);

    protected final MetadataXmlProvider<?> metadataXmlProvider;
    protected final File destDir;

    private DistributeAssetStrategy distributeAssetStrategy;

    public AssetProcessor(MetadataXmlProvider<?> metadataXmlProvider, File destDir) {
        this.metadataXmlProvider = metadataXmlProvider;
        this.destDir = destDir;
    }

    public void setDistributeAssetStrategy(DistributeAssetStrategy distributeAssetStrategy) {
        this.distributeAssetStrategy = distributeAssetStrategy;
    }

    public void process(File assetFile) throws AssetValidationException, IOException {
        logger.info("Processing asset {}...", assetFile.getName());

        if (!assetFile.exists() || !assetFile.isFile()) {
            throw new ConversionException(String.format(
                    "Asset file '%s' must be an existing file", assetFile.getName()));
        }

        if (!checkMandatoryParams()) {
            throw new AssetValidationException("Mandatory input parameters for processor must be set");
        }

        validate(assetFile);
        appendAsset(buildAsset(assetFile));
        distribute(assetFile);

        logger.info("Processed asset: OK\n");
    }

    protected void appendAsset(T asset) {
        metadataXmlProvider.appendAsset(asset);
    }

    protected void distribute(File assetFile) throws IOException {
        if (distributeAssetStrategy != null) {
            distributeAssetStrategy.distribute(assetFile, destDir, getDestFileName(assetFile));
        }
    }

    protected abstract boolean checkMandatoryParams();

    protected abstract void validate(File assetFile) throws AssetValidationException;

    protected abstract T buildAsset(File assetFile);

    protected abstract String getDestFileName(File assetFile);
}
