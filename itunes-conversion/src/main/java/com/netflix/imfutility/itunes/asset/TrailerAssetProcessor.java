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

import com.netflix.imfutility.generated.itunes.metadata.AssetTypeType;
import com.netflix.imfutility.generated.itunes.metadata.DataFileRoleType;
import com.netflix.imfutility.generated.itunes.metadata.DataFileType;
import com.netflix.imfutility.generated.itunes.metadata.LocaleType;
import com.netflix.imfutility.generated.mediainfo.FormatType;
import com.netflix.imfutility.itunes.asset.distribute.CopyAssetStrategy;
import com.netflix.imfutility.itunes.xmlprovider.MetadataXmlProvider;
import com.netflix.imfutility.itunes.xmlprovider.builder.file.DataFileTagBuilder;

import java.io.File;

import static com.netflix.imfutility.itunes.asset.AssetProcessorConstants.MOV_FORMAT;

/**
 * Asset processor specified for trailer managing.
 */
public class TrailerAssetProcessor extends AssetProcessor<DataFileType> {

    private String vendorId;
    private FormatType format;
    private LocaleType locale;

    public TrailerAssetProcessor(MetadataXmlProvider metadataXmlProvider, File destDir) {
        super(metadataXmlProvider, destDir);
        setDistributeAssetStrategy(new CopyAssetStrategy());
    }

    public TrailerAssetProcessor setVendorId(String vendorId) {
        this.vendorId = vendorId;
        return this;
    }

    public TrailerAssetProcessor setFormat(FormatType format) {
        this.format = format;
        return this;
    }

    public TrailerAssetProcessor setLocale(LocaleType locale) {
        this.locale = locale;
        return this;
    }

    @Override
    protected boolean checkMandatoryParams() {
        return vendorId != null
                && format != null
                && locale != null;
    }

    @Override
    protected void validate(File assetFile) throws AssetValidationException {
        if (!format.getFormatLongName().equals(MOV_FORMAT)) {
            throw new AssetValidationException("Trailer must be an MOV container");
        }
    }

    @Override
    protected DataFileType buildMetadata(File assetFile) {
        return new DataFileTagBuilder(assetFile, getDestFileName(assetFile))
                .setLocale(locale)
                .setRole(DataFileRoleType.SOURCE)
                .setCropToZero(true)
                .build();
    }

    @Override
    protected void appendMetadata(DataFileType tag) {
        metadataXmlProvider.appendAssetDataFile(tag, AssetTypeType.PREVIEW);
    }

    @Override
    protected String getDestFileName(File assetFile) {
        return vendorId + "-preview.mov";
    }
}
