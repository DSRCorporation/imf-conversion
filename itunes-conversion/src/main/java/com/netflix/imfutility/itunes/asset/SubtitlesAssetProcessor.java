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

import com.netflix.imfutility.itunes.asset.bean.Asset;
import com.netflix.imfutility.itunes.asset.bean.AssetRole;
import com.netflix.imfutility.itunes.asset.bean.AssetType;
import com.netflix.imfutility.itunes.asset.builder.DefaultAssetBuilder;
import com.netflix.imfutility.itunes.asset.distribute.MoveAssetStrategy;
import com.netflix.imfutility.itunes.metadata.MetadataXmlProvider;

import java.io.File;
import java.util.Locale;

/**
 * Asset processor specified for subtitles managing.
 */
public class SubtitlesAssetProcessor extends AssetProcessor<Asset> {

    private Locale locale;

    public SubtitlesAssetProcessor(MetadataXmlProvider<?> metadataXmlProvider, File destDir) {
        super(metadataXmlProvider, destDir);
        setDistributeAssetStrategy(new MoveAssetStrategy());
    }

    public SubtitlesAssetProcessor setLocale(Locale locale) {
        this.locale = locale;
        return this;
    }

    @Override
    protected boolean checkMandatoryParams() {
        return locale != null;
    }

    @Override
    protected void validate(File assetFile) throws AssetValidationException {
        validateDuplicateLocales();
    }

    @Override
    protected Asset buildAsset(File assetFile) {
        return new DefaultAssetBuilder(assetFile, getDestFileName(assetFile))
                .setType(AssetType.FULL)
                .setRole(AssetRole.SUBTITLES)
                .setLocale(locale)
                .build();
    }

    @Override
    protected String getDestFileName(File assetFile) {
        return "subtitles_" + locale.toString().replace("-", "_").toUpperCase() + ".itt";
    }

    private void validateDuplicateLocales() {
        boolean duplicate = metadataXmlProvider.getLocalesByRole(AssetRole.SUBTITLES).contains(locale);

        if (duplicate) {
            throw new AssetValidationException(String.format(
                    "Subtitles locale validation failed. Metadata already contains subtitles for %s locale.", locale.getDisplayName()));
        }
    }
}
