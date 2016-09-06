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

import com.netflix.imfutility.itunes.asset.builder.DefaultAssetBuilder;
import com.netflix.imfutility.itunes.asset.distribute.CopyAssetStrategy;
import com.netflix.imfutility.itunes.asset.type.Asset;
import com.netflix.imfutility.itunes.asset.type.AssetRole;
import com.netflix.imfutility.itunes.asset.type.AssetType;
import com.netflix.imfutility.itunes.metadata.MetadataXmlProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Objects;

import static com.netflix.imfutility.itunes.asset.AssetProcessorConstants.DEFAULT_CC_LOCALE;
import static com.netflix.imfutility.itunes.asset.AssetProcessorConstants.SCC_SIGNATURE;

/**
 * Asset processor specified for captions managing.
 */
public class CaptionsAssetProcessor extends AssetProcessor<Asset> {

    private String vendorId;
    private Locale locale;

    public CaptionsAssetProcessor(MetadataXmlProvider<?> metadataXmlProvider, File destDir) {
        super(metadataXmlProvider, destDir);
        setDistributeAssetStrategy(new CopyAssetStrategy());
    }

    public CaptionsAssetProcessor setVendorId(String vendorId) {
        this.vendorId = vendorId;
        return this;
    }

    public CaptionsAssetProcessor setLocale(Locale locale) {
        this.locale = locale;
        return this;
    }

    @Override
    protected boolean checkMandatoryParams() {
        return vendorId != null && locale != null;
    }

    @Override
    protected void validate(File assetFile) throws AssetValidationException {
        validateFormat(assetFile);
    }

    @Override
    protected Asset buildAsset(File assetFile) {
        return new DefaultAssetBuilder(assetFile, getDestFileName(assetFile))
                .setType(AssetType.FULL)
                .setRole(AssetRole.CAPTIONS)
                .setLocale(locale)
                .build();
    }

    @Override
    protected String getDestFileName(File assetFile) {
        return vendorId + "-" + locale.getDisplayLanguage(DEFAULT_CC_LOCALE).toLowerCase() + ".scc";
    }

    private void validateFormat(File assetFile) throws AssetValidationException {
        try (BufferedReader reader = Files.newBufferedReader(assetFile.toPath(), StandardCharsets.US_ASCII)) {
            if (!Objects.equals(reader.readLine(), SCC_SIGNATURE)) {
                throw new AssetValidationException("Closed captions must be in SCC format");
            }
        } catch (IOException e) {
            throw new AssetValidationException(String.format(
                    "Can't read file %s", assetFile.getName()), e);
        }
    }

}
