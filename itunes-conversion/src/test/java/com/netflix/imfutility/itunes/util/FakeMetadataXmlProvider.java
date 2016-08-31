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
package com.netflix.imfutility.itunes.util;

import com.netflix.imfutility.itunes.asset.type.Asset;
import com.netflix.imfutility.itunes.asset.type.AssetRole;
import com.netflix.imfutility.itunes.asset.type.ChapterAsset;
import com.netflix.imfutility.itunes.metadata.MetadataDescriptor;
import com.netflix.imfutility.itunes.metadata.MetadataXmlProvider;
import com.netflix.imfutility.itunes.util.FakeMetadataXmlProvider.FakeRoot;
import com.netflix.imfutility.xml.XmlParsingException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Fake metadata provider for testing needs.
 */
public class FakeMetadataXmlProvider extends MetadataXmlProvider<FakeRoot> {

    public FakeMetadataXmlProvider() throws FileNotFoundException, XmlParsingException {
        super(new FakeMetadataDescriptor());
    }

    @Override
    public void setLocale(Locale locale) {
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    public void updateVendorId(String vendorId) {
    }

    @Override
    protected FakeRoot generateDefaultMetadata() {
        return new FakeRoot();
    }

    @Override
    public void appendAsset(Asset dataFile) {
        rootElement.getAssets().add(dataFile);
    }

    @Override
    public List<Locale> getLocalesByRole(AssetRole role) {
        return rootElement.getAssets().stream().
                filter(asset -> asset.getRole() == role)
                .map(Asset::getLocale)
                .collect(Collectors.toList());
    }

    @Override
    public void appendChaptersTimeCode(String timeCode) {
    }

    @Override
    public void appendChapterAsset(ChapterAsset chapterAsset) {
        rootElement.getChapterAssets().add(chapterAsset);
    }

    public static class FakeRoot {
        private final List<Asset> assets = new ArrayList<>();
        private final List<ChapterAsset> chapterAssets = new ArrayList<>();

        public List<Asset> getAssets() {
            return assets;
        }

        public List<ChapterAsset> getChapterAssets() {
            return chapterAssets;
        }
    }

    public static class FakeMetadataDescriptor implements MetadataDescriptor<FakeRoot> {

        @Override
        public Class<FakeRoot> getMetadataClass() {
            return FakeRoot.class;
        }

        @Override
        public String getMetadataSchema() {
            return "fakeschema";
        }

        @Override
        public String getMetadataPackage() {
            return "fakepackage";
        }

        @Override
        public String getMetadataNamespace() {
            return "fakenamespace";
        }

        @Override
        public String getMetadataRoot() {
            return "fakeroot";
        }
    }

}
