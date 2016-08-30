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
package com.netflix.imfutility.itunes.asset.builder;

import com.netflix.imfutility.itunes.asset.bean.Asset;
import com.netflix.imfutility.itunes.asset.bean.AssetRole;
import com.netflix.imfutility.itunes.asset.bean.AssetType;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * Common builder for creating iTunes metadata asset info.
 * Also generates MD5 hash for input.
 * (see {@link Asset}).
 *
 * @param <T> destination asset type.
 */
public abstract class AssetBuilder<T extends Asset> {
    private static final String MD5_CHECKSUM_TYPE = "md5";

    protected final File file;
    protected final String fileName;

    private AssetType type;
    private AssetRole role;
    private Locale locale;

    public AssetBuilder(File file) {
        this(file, file.getName());
    }

    public AssetBuilder(File file, String fileName) {
        this.file = file;
        this.fileName = fileName;
    }

    public AssetBuilder<T> setType(AssetType type) {
        this.type = type;
        return this;
    }

    public AssetBuilder<T> setRole(AssetRole role) {
        this.role = role;
        return this;
    }

    public AssetBuilder<T> setLocale(Locale locale) {
        this.locale = locale;
        return this;
    }

    public T build() {
        T asset = create();
        buildImpl(asset);
        return asset;
    }

    protected void buildImpl(T asset) {
        asset.setType(type);
        asset.setRole(role);
        asset.setLocale(locale);
        asset.setFileName(fileName);
        asset.setSize(String.valueOf(file.length()));
        asset.setChecksumType(MD5_CHECKSUM_TYPE);
        asset.setChecksum(md5(file));
    }

    protected abstract T create();

    private static String md5(File file) {
        try (FileInputStream stream = new FileInputStream(file)) {
            return DigestUtils.md5Hex(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
