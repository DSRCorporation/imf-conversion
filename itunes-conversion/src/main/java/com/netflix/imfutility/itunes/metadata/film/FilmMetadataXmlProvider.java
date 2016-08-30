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
package com.netflix.imfutility.itunes.metadata.film;

import com.netflix.imfutility.generated.itunes.metadata.AssetFile;
import com.netflix.imfutility.generated.itunes.metadata.AssetItem;
import com.netflix.imfutility.generated.itunes.metadata.AssetList;
import com.netflix.imfutility.generated.itunes.metadata.Attribute;
import com.netflix.imfutility.generated.itunes.metadata.ChapterItem;
import com.netflix.imfutility.generated.itunes.metadata.ChapterList;
import com.netflix.imfutility.generated.itunes.metadata.Checksum;
import com.netflix.imfutility.generated.itunes.metadata.DataFileRole;
import com.netflix.imfutility.generated.itunes.metadata.PackageType;
import com.netflix.imfutility.generated.itunes.metadata.Territories;
import com.netflix.imfutility.generated.itunes.metadata.Video;
import com.netflix.imfutility.generated.itunes.metadata.VideoLocale;
import com.netflix.imfutility.itunes.asset.bean.Asset;
import com.netflix.imfutility.itunes.asset.bean.AssetRole;
import com.netflix.imfutility.itunes.asset.bean.AssetType;
import com.netflix.imfutility.itunes.asset.bean.ChapterAsset;
import com.netflix.imfutility.itunes.asset.bean.VideoAsset;
import com.netflix.imfutility.itunes.metadata.MetadataXmlProvider;
import com.netflix.imfutility.itunes.metadata.film.wrap.ChapterItemWrapper;
import com.netflix.imfutility.itunes.metadata.film.wrap.FileWrapper;
import com.netflix.imfutility.xml.XmlParsingException;
import org.apache.commons.lang3.LocaleUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Metadata provider for iTunes film specification.
 */
public class FilmMetadataXmlProvider extends MetadataXmlProvider<PackageType> {

    private final Video video;

    public FilmMetadataXmlProvider() throws FileNotFoundException, XmlParsingException {
        this(null);
    }

    public FilmMetadataXmlProvider(File metadataFile) throws FileNotFoundException, XmlParsingException {
        super(FilmMetadataInfo.INSTANCE, metadataFile);
        this.video = getSingleValue(rootElement.getVideo());
    }

    @Override
    public void updateVendorId(String vendorId) {
        setSingleValue(video.getVendorId(), vendorId);
    }

    @Override
    protected PackageType generateDefaultMetadata() {
        return FilmMetadataXmlSampleBuilder.buildPackage();
    }


    @Override
    public void setLocale(Locale locale) {
        String str = locale.toString().replace("_", "-");
        setSingleValue(rootElement.getLanguage(), str);
        setSingleValue(video.getOriginalSpokenLocale(), str);
    }

    @Override
    public Locale getLocale() {
        return LocaleUtils.toLocale(getSingleValue(rootElement.getLanguage()).replace("-", "_"));
    }

    // Asset processing

    @Override
    public void appendAsset(Asset asset) {
        AssetItem assetItem = asset.getType() == AssetType.FULL
                ? ensureFullAssetItemCreated()
                : createAssetItem(asset.getType(), createTerritories());
        assetItem.getReadOnlyInfoOrTerritoriesOrDataFile().add(createAssetFile(asset));
    }

    @Override
    public List<Locale> getLocalesByRole(AssetRole role) {
        return ensureFullAssetItemCreated().getReadOnlyInfoOrTerritoriesOrDataFile().stream()
                .filter(AssetFile.class::isInstance)
                .map(AssetFile.class::cast)
                .filter(assetFile -> assetFile.getRole().value().equals(role.getName()))
                .filter(assetFile -> getSingleValue(assetFile.getLocale()) != null)
                .map(assetFile -> LocaleUtils.toLocale(getSingleValue(assetFile.getLocale()).getName()))
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

    //  Chapters processing

    @Override
    public void appendChaptersTimeCode(String timeCode) {
        ensureChaptersCreated().getTimecodeFormatOrChapter().add(timeCode);
    }

    @Override
    public void appendChapterAsset(ChapterAsset asset) {
        FileWrapper fileWrapper = new FileWrapper(context);
        fileWrapper.setFileName(asset.getFileName());
        fileWrapper.setSize(String.valueOf(asset.getSize()));
        fileWrapper.setChecksum(createChecksum(asset));

        ChapterItemWrapper wrapper = new ChapterItemWrapper(context, new ChapterItem());

        wrapper.setStartTime(asset.getInputChapterItem().getStartTime());
        wrapper.setTitle(asset.getInputChapterItem().getTitle());
        wrapper.setArtworkFile(fileWrapper.getInner());

        ensureChaptersCreated().getTimecodeFormatOrChapter().add(wrapper.getInner());
    }

    private ChapterList ensureChaptersCreated() {
        ChapterList chapterList = getSingleValue(video.getChapters());
        if (chapterList == null) {
            chapterList = new ChapterList();
            setSingleValue(video.getChapters(), chapterList);
        }
        return chapterList;
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

        if (asset instanceof VideoAsset) {
            if (((VideoAsset) asset).isCropToZero()) {
                assetFile.getAttribute().addAll(createZeroCropAttributeList());
            }
        }

        return assetFile;
    }

    private List<Attribute> createZeroCropAttributeList() {
        return Stream.of("crop.top", "crop.bottom", "crop.left", "crop.right")
                .map(this::createZeroCropAttribute)
                .collect(Collectors.toList());
    }

    private Attribute createZeroCropAttribute(String name) {
        Attribute attribute = new Attribute();
        attribute.setName(name);
        attribute.setContent("0");
        return attribute;
    }

    private VideoLocale createVideoLocale(Locale locale) {
        VideoLocale videoLocale = new VideoLocale();
        videoLocale.setName(locale.toString().replace("_", "-"));
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
        territories.getTerritory().add("WW");
        return territories;
    }
}
