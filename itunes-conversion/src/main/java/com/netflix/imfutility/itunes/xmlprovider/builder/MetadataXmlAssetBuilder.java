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
package com.netflix.imfutility.itunes.xmlprovider.builder;

import com.netflix.imfutility.generated.itunes.metadata.AssetType;
import com.netflix.imfutility.generated.itunes.metadata.DataFileRoleType;
import com.netflix.imfutility.generated.itunes.metadata.DataFileType;
import com.netflix.imfutility.generated.itunes.metadata.LocaleType;
import com.netflix.imfutility.generated.itunes.metadata.TerritoriesType;

import java.io.File;
import java.math.BigInteger;

/**
 * Builder for creating iTunes Asset metadata info for input file.
 * Also generates MD5 hash for input.
 * (see {@link DataFileType}).
 */
public class MetadataXmlAssetBuilder extends MetadataXmlCheckSumBuilder {

    public MetadataXmlAssetBuilder(File assetFile) {
        super(assetFile);
    }

    public AssetType buildAsset(TerritoriesType territories) {
        AssetType asset = new AssetType();
        asset.setTerritories(territories);
        return asset;
    }

    public DataFileType buildDataFile(DataFileRoleType role, LocaleType locale) {
        DataFileType dataFile = new DataFileType();
        dataFile.setRole(role);
        if (locale != null) {
            dataFile.setLocale(locale);
        }
        dataFile.setFileName(assetFile.getName());
        dataFile.setSize(BigInteger.valueOf(assetFile.length()));
        dataFile.setChecksum(buildCheckSum());
        return dataFile;
    }


}
