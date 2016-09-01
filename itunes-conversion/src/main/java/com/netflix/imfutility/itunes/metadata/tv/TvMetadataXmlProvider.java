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
package com.netflix.imfutility.itunes.metadata.tv;

import com.apple.itunes.importer.tv.Accessibility;
import com.apple.itunes.importer.tv.AccessibilityInfo;
import com.apple.itunes.importer.tv.AccessibilityRole;
import com.apple.itunes.importer.tv.AssetFile;
import com.apple.itunes.importer.tv.AssetItem;
import com.apple.itunes.importer.tv.AssetList;
import com.apple.itunes.importer.tv.Attribute;
import com.apple.itunes.importer.tv.Checksum;
import com.apple.itunes.importer.tv.DataFileRole;
import com.apple.itunes.importer.tv.PackageType;
import com.apple.itunes.importer.tv.Territories;
import com.apple.itunes.importer.tv.Video;
import com.apple.itunes.importer.tv.VideoLocale;
import com.netflix.imfutility.itunes.asset.type.Asset;
import com.netflix.imfutility.itunes.asset.type.AssetRole;
import com.netflix.imfutility.itunes.asset.type.AssetType;
import com.netflix.imfutility.itunes.asset.type.ChapterAsset;
import com.netflix.imfutility.itunes.locale.LocaleHelper;
import com.netflix.imfutility.itunes.metadata.MetadataXmlProvider;
import com.netflix.imfutility.itunes.metadata.tv.builder.TvMetadataXmlSampleBuilder;
import com.netflix.imfutility.xml.XmlParsingException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Metadata provider for iTunes tv specification.
 */
public class TvMetadataXmlProvider extends MetadataXmlProvider<PackageType> {

    private static final String DEFAULT_TERRITORY = "WW";
    private static final String[] CROP_ATTRUBUTE_NAMES = new String[]{"crop.top", "crop.bottom", "crop.left", "crop.right"};

    private final Video video;

    public TvMetadataXmlProvider() throws FileNotFoundException, XmlParsingException {
        this(null);
    }

    public TvMetadataXmlProvider(File metadataFile) throws FileNotFoundException, XmlParsingException {
        super(TvMetadataDescriptor.INSTANCE, metadataFile);
        this.video = getSingleValue(rootElement.getVideo());
    }

    @Override
    public void updateVendorId(String vendorId) {
        setSingleValue(video.getVendorId(), vendorId);
    }

    @Override
    protected PackageType generateDefaultMetadata() {
        return TvMetadataXmlSampleBuilder.buildPackage();
    }


    @Override
    public void setLocale(Locale locale) {
        String str = LocaleHelper.toITunesLocale(locale);
        setSingleValue(rootElement.getLanguage(), str);
        setSingleValue(video.getOriginalSpokenLocale(), str);
    }

    @Override
    public Locale getLocale() {
        return LocaleHelper.fromITunesLocale(getSingleValue(rootElement.getLanguage()));
    }

    // Asset processing

    @Override
    public void appendAsset(Asset asset) {
        AssetItem assetItem = asset.getType() == AssetType.FULL
                ? ensureFullAssetItemCreated()
                : createAssetItem(asset.getType(), createTerritories());
        assetItem.getReadOnlyInfoOrTerritoriesOrDataFile().add(createAssetFile(asset));

        if (asset.getRole() == AssetRole.CAPTIONS) {
            ensureCaptionsAccessibility();
        }
    }

    @Override
    public List<Locale> getLocalesByRole(AssetRole role) {
        return ensureFullAssetItemCreated().getReadOnlyInfoOrTerritoriesOrDataFile().stream()
                .filter(AssetFile.class::isInstance)
                .map(AssetFile.class::cast)
                .filter(assetFile -> Objects.equals(assetFile.getRole().value(), role.getName()))
                .map(assetFile -> getSingleValue(assetFile.getLocale()))
                .filter(Objects::nonNull)
                .map(VideoLocale::getName)
                .map(LocaleHelper::fromITunesLocale)
                .collect(Collectors.toList());
    }

    private AssetList ensureAssetListCreated() {
        AssetList assetList = getSingleValue(video.getAssets());
        if (assetList == null) {
            assetList = new AssetList();
            setSingleValue(video.getAssets(), assetList);
        }
        return assetList;
    }

    private AssetItem ensureFullAssetItemCreated() {
        return ensureAssetListCreated().getAssetOrAccessibilityInfo().stream()
                .filter(AssetItem.class::isInstance)
                .map(AssetItem.class::cast)
                .filter(asset -> asset.getType().equals(AssetType.FULL.getName()))
                .findFirst()
                .orElseGet(() -> createAssetItem(AssetType.FULL, null));
    }

    private AssetItem createAssetItem(AssetType type, Territories territories) {
        AssetItem assetItem = new AssetItem();
        assetItem.setType(type.getName());
        if (territories != null) {
            assetItem.getReadOnlyInfoOrTerritoriesOrDataFile().add(territories);
        }

        ensureAssetListCreated().getAssetOrAccessibilityInfo().add(assetItem);
        return assetItem;
    }

    //  Chapters processing (unsupported)

    @Override
    public void appendChaptersTimeCode(String timeCode) {
        throw new UnsupportedOperationException("Chapters are not allowed for iTunes TV package.");
    }

    @Override
    public void appendChapterAsset(ChapterAsset asset) {
        throw new UnsupportedOperationException("Chapters are not allowed for iTunes TV package.");
    }

    private AssetFile createAssetFile(Asset asset) {
        AssetFile assetFile = new AssetFile();
        setSingleValue(assetFile.getFileName(), asset.getFileName());
        setSingleValue(assetFile.getChecksum(), createChecksum(asset));
        setSingleValue(assetFile.getSize(), String.valueOf(asset.getSize()));
        if (asset.getLocale() != null) {
            setSingleValue(assetFile.getLocale(), createVideoLocale(asset.getLocale()));
        }
        if (asset.getRole() != null) {
            assetFile.setRole(DataFileRole.fromValue(asset.getRole().getName()));
        }

        if (asset.getRole() == AssetRole.SOURCE) {
            assetFile.getAttribute().addAll(createZeroCropAttributeList());
        }

        return assetFile;
    }

    private List<Attribute> createZeroCropAttributeList() {
        return Stream.of(CROP_ATTRUBUTE_NAMES)
                .map(name -> createAttribute(name, "0"))
                .collect(Collectors.toList());
    }

    private Attribute createAttribute(String name, String content) {
        Attribute attribute = new Attribute();
        attribute.setName(name);
        attribute.setContent(content);
        return attribute;
    }

    private VideoLocale createVideoLocale(Locale locale) {
        VideoLocale videoLocale = new VideoLocale();
        videoLocale.setName(LocaleHelper.toITunesLocale(locale));
        return videoLocale;
    }

    private Checksum createChecksum(Asset asset) {
        Checksum checksum = new Checksum();
        checksum.setType(asset.getChecksumType());
        checksum.setValue(asset.getChecksum());
        return checksum;
    }

    private Territories createTerritories() {
        //  create WW territory by default
        Territories territories = new Territories();
        territories.getTerritory().add(DEFAULT_TERRITORY);
        return territories;
    }

    private AccessibilityInfo ensureCaptionsAccessibility() {
        Accessibility accessibility = new Accessibility();
        accessibility.setRole(AccessibilityRole.CAPTIONS);
        accessibility.setAvailable(true);

        AccessibilityInfo info = getSingleValue(video.getAccessibilityInfo());
        if (info == null) {
            info = new AccessibilityInfo();
            setSingleValue(video.getAccessibilityInfo(), info);
        }

        info.getReadOnlyInfoOrAccessibility().add(accessibility);
        return info;
    }
}
