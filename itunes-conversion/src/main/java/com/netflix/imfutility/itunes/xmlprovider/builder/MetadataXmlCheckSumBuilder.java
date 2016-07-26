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

import com.netflix.imfutility.generated.itunes.metadata.CheckSumType;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Builder for creating CheckSum metadata info for input file.
 * Generates MD5 hash for input.
 * (see {@link CheckSumType}).
 */
public class MetadataXmlCheckSumBuilder {

    protected File assetFile;

    public MetadataXmlCheckSumBuilder(File assetFile) {
        this.assetFile = assetFile;
    }

    public CheckSumType buildCheckSum() {
        CheckSumType checkSum = new CheckSumType();
        checkSum.setType("md5");
        checkSum.setValue(md5(assetFile));
        return checkSum;
    }

    private static byte[] md5(File file) {
        try {
            return DigestUtils.md5(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
