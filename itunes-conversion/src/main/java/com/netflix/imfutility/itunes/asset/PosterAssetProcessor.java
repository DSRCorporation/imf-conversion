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
import com.netflix.imfutility.generated.itunes.metadata.DataFileType;
import com.netflix.imfutility.itunes.image.ImageValidationException;
import com.netflix.imfutility.itunes.image.ImageValidator;
import com.netflix.imfutility.itunes.xmlprovider.MetadataXmlProvider;
import com.netflix.imfutility.itunes.xmlprovider.builder.file.DataFileTagBuilder;
import org.apache.commons.math3.fraction.BigFraction;

import java.io.File;

/**
 * Asset processor specified for poster managing.
 */
public class PosterAssetProcessor extends AssetProcessor<DataFileType> {

    private String vendorId;

    public PosterAssetProcessor(MetadataXmlProvider metadataXmlProvider, File destDir) {
        super(metadataXmlProvider, destDir);
    }

    public PosterAssetProcessor setVendorId(String vendorId) {
        this.vendorId = vendorId;
        return this;
    }

    @Override
    protected boolean checkInput(File assetFile) {
        return super.checkInput(assetFile) && vendorId != null;
    }

    @Override
    protected void validate(File assetFile) throws ImageValidationException {
        ImageValidator validator = new ImageValidator(assetFile, "Poster");
        validator.validateSize(1400, 2100);
        validator.validateAspectRatio(new BigFraction(2).divide(3));
        validator.validateContentType("image/jpeg", "JPEG");
        validator.validateRGBColorSpace();
    }

    @Override
    protected DataFileType buildMetadata(File assetFile) {
        //  poster do not need locale and role info
        return new DataFileTagBuilder(assetFile, getFileName())
                .build();
    }

    @Override
    protected void appendMetadata(DataFileType tag) {
        metadataXmlProvider.appendAsset(tag, AssetTypeType.ARTWORK);
    }

    @Override
    protected String getFileName() {
        return vendorId + ".jpg";
    }
}
