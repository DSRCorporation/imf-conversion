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

import com.netflix.imfutility.generated.itunes.chapters.InputChapterItem;
import com.netflix.imfutility.itunes.asset.bean.ChapterAsset;
import com.netflix.imfutility.itunes.asset.builder.ChapterAssetBuilder;
import com.netflix.imfutility.itunes.asset.distribute.CopyAssetStrategy;
import com.netflix.imfutility.itunes.image.ImageValidator;
import com.netflix.imfutility.itunes.metadata.MetadataXmlProvider;
import org.apache.commons.math3.fraction.BigFraction;

import java.io.File;

import static com.netflix.imfutility.itunes.asset.AssetProcessorConstants.CHAPTER_MAX_INDEX;
import static com.netflix.imfutility.itunes.asset.AssetProcessorConstants.CHAPTER_MIN_INDEX;
import static com.netflix.imfutility.itunes.asset.AssetProcessorConstants.CHAPTER_MIN_WIDTH;
import static com.netflix.imfutility.itunes.asset.AssetProcessorConstants.CHAPTER_TYPE;

/**
 * Asset processor specified for chapter image managing.
 */
public class ChapterAssetProcessor extends AssetProcessor<ChapterAsset> {

    private Integer chapterIndex;
    private InputChapterItem inputChapterItem;
    private BigFraction aspectRatio;

    public ChapterAssetProcessor(MetadataXmlProvider<?> metadataXmlProvider, File destDir) {
        super(metadataXmlProvider, destDir);
        setDistributeAssetStrategy(new CopyAssetStrategy());
    }

    public ChapterAssetProcessor setChapterIndex(Integer chapterIndex) {
        this.chapterIndex = chapterIndex;
        return this;
    }

    public ChapterAssetProcessor setInputChapterItem(InputChapterItem inputChapterItem) {
        this.inputChapterItem = inputChapterItem;
        return this;
    }

    public ChapterAssetProcessor setAspectRatio(BigFraction aspectRatio) {
        this.aspectRatio = aspectRatio;
        return this;
    }

    @Override
    protected boolean checkMandatoryParams() {
        return chapterIndex != null
                && !(chapterIndex < CHAPTER_MIN_INDEX || chapterIndex > CHAPTER_MAX_INDEX)
                && aspectRatio != null
                && inputChapterItem != null;
    }

    @Override
    protected void validate(File assetFile) throws AssetValidationException {
        ImageValidator validator = new ImageValidator(assetFile, CHAPTER_TYPE);
        //  validate only chapter width
        validator.validateSize(CHAPTER_MIN_WIDTH, null);
        validator.validateAspectRatio(aspectRatio);
        validator.validateJpeg();
        validator.validateRGBColorSpace();
    }

    @Override
    protected ChapterAsset buildAsset(File assetFile) {
        return new ChapterAssetBuilder(assetFile, getDestFileName(assetFile))
                .setInputChapterItem(inputChapterItem)
                .build();
    }

    @Override
    protected void appendAsset(ChapterAsset asset) {
        (metadataXmlProvider).appendChapterAsset(asset);
    }

    @Override
    protected String getDestFileName(File assetFile) {
        return "chapter" + String.format("%02d", chapterIndex) + ".jpg";
    }
}
