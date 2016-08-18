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
import com.netflix.imfutility.itunes.asset.distribute.CopyAssetStrategy;
import com.netflix.imfutility.itunes.image.ImageValidationException;
import com.netflix.imfutility.itunes.image.ImageValidator;
import com.netflix.imfutility.itunes.xmlprovider.MetadataXmlProvider;
import com.netflix.imfutility.itunes.xmlprovider.builder.file.DataFileTagBuilder;
import org.apache.commons.math3.fraction.BigFraction;

import java.io.File;

import static com.netflix.imfutility.itunes.asset.AssetProcessorConstants.POSTER_AR_DENOMINATOR;
import static com.netflix.imfutility.itunes.asset.AssetProcessorConstants.POSTER_AR_NUMERATOR;
import static com.netflix.imfutility.itunes.asset.AssetProcessorConstants.POSTER_MIN_HEIGHT;
import static com.netflix.imfutility.itunes.asset.AssetProcessorConstants.POSTER_MIN_WIDTH;
import static com.netflix.imfutility.itunes.asset.AssetProcessorConstants.POSTER_TYPE;

/**
 * Asset processor specified for poster managing.
 */
public class PosterAssetProcessor extends AssetProcessor<DataFileType> {

    private String vendorId;

    public PosterAssetProcessor(MetadataXmlProvider metadataXmlProvider, File destDir) {
        super(metadataXmlProvider, destDir);
        setDistributeAssetStrategy(new CopyAssetStrategy());
    }

    public PosterAssetProcessor setVendorId(String vendorId) {
        this.vendorId = vendorId;
        return this;
    }

    @Override
    protected boolean checkMandatoryParams() {
        return vendorId != null;
    }

    @Override
    protected void validate(File assetFile) throws ImageValidationException {
        ImageValidator validator = new ImageValidator(assetFile, POSTER_TYPE);
        validator.validateSize(POSTER_MIN_WIDTH, POSTER_MIN_HEIGHT);
        validator.validateAspectRatio(new BigFraction(POSTER_AR_NUMERATOR).divide(POSTER_AR_DENOMINATOR));
        validator.validateJpeg();
        validator.validateRGBColorSpace();
    }

    @Override
    protected DataFileType buildMetadata(File assetFile) {
        //  poster do not need locale and role info
        return new DataFileTagBuilder(assetFile, getDestFileName(assetFile))
                .build();
    }

    @Override
    protected void appendMetadata(DataFileType tag) {
        metadataXmlProvider.appendAssetDataFile(tag, AssetTypeType.ARTWORK);
    }

    @Override
    protected String getDestFileName(File assetFile) {
        return vendorId + ".jpg";
    }
}
