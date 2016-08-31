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

import com.netflix.imfutility.itunes.asset.type.Asset;
import com.netflix.imfutility.itunes.asset.type.AssetRole;
import com.netflix.imfutility.itunes.asset.type.AssetType;
import com.netflix.imfutility.itunes.asset.builder.DefaultAssetBuilder;
import com.netflix.imfutility.itunes.asset.distribute.CopyAssetStrategy;
import com.netflix.imfutility.itunes.locale.LocaleValidationException;
import com.netflix.imfutility.itunes.locale.LocaleValidator;
import com.netflix.imfutility.itunes.metadata.MetadataXmlProvider;
import org.apache.commons.lang3.LocaleUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

import static com.netflix.imfutility.itunes.asset.AssetProcessorConstants.DEFAULT_CC_LOCALE;
import static com.netflix.imfutility.itunes.asset.AssetProcessorConstants.SCC_SIGNATURE;

/**
 * Asset processor specified for captions managing.
 */
public class CaptionsAssetProcessor extends AssetProcessor<Asset> {

    private String vendorId;

    public CaptionsAssetProcessor(MetadataXmlProvider<?> metadataXmlProvider, File destDir) {
        super(metadataXmlProvider, destDir);
        setDistributeAssetStrategy(new CopyAssetStrategy());
    }

    public CaptionsAssetProcessor setVendorId(String vendorId) {
        this.vendorId = vendorId;
        return this;
    }

    @Override
    protected boolean checkMandatoryParams() {
        return vendorId != null;
    }

    @Override
    protected void validate(File assetFile) throws AssetValidationException {
        validateFormat(assetFile);
        validateLocale(assetFile);
        validateDuplicateLocales(assetFile);
    }

    @Override
    protected Asset buildAsset(File assetFile) {
        return new DefaultAssetBuilder(assetFile, getDestFileName(assetFile))
                .setType(AssetType.FULL)
                .setRole(AssetRole.CAPTIONS)
                .setLocale(getLocaleFromFileName(assetFile))
                .build();
    }

    @Override
    protected String getDestFileName(File assetFile) {
        return vendorId + "-" + getLanguageFromFileName(assetFile) + ".scc";
    }

    private void validateFormat(File assetFile) throws AssetValidationException {
        try (BufferedReader reader = Files.newBufferedReader(assetFile.toPath(), StandardCharsets.US_ASCII)) {
            if (!Objects.equals(reader.readLine(), SCC_SIGNATURE)) {
                throw new AssetValidationException("Closed captions must be in SCC format");
            }
        } catch (IOException e) {
            throw new AssetValidationException(String.format(
                    "Can't read file %s", assetFile.getName()), e);
        }
    }

    private void validateLocale(File assetFile) throws AssetValidationException {
        try {
            LocaleValidator.validateLocale(getPostfix(assetFile.getName()));
        } catch (LocaleValidationException e) {
            throw new AssetValidationException("Captions locale validation failed. "
                    + "Filename must fit pattern <filename>-xx_XX.scc, where xx_XX - existing locale", e);
        }

    }

    private void validateDuplicateLocales(File assetFile) {
        Locale locale = getLocaleFromFileName(assetFile);

        // search for duplicates by main language
        Stream<Locale> stream = metadataXmlProvider.getLocalesByRole(AssetRole.CAPTIONS).stream();

        boolean duplicate = stream
                .map(Locale::getLanguage)
                .anyMatch(lang -> lang.equals(locale.getLanguage()));

        if (duplicate) {
            throw new AssetValidationException(String.format(
                    "Captions locale validation failed. Metadata already contains captions for %s locale.", locale.getDisplayName()));
        }
    }

    private String getLanguageFromFileName(File assetFile) {
        return getLocaleFromFileName(assetFile).getDisplayLanguage(DEFAULT_CC_LOCALE).toLowerCase();
    }

    private Locale getLocaleFromFileName(File assetFile) {
        return LocaleUtils.toLocale(getPostfix(assetFile.getName()));
    }

    private String getPostfix(String fileName) {
        if (!fileName.contains("-") || !fileName.contains(".")) {
            return null;
        }

        if (fileName.lastIndexOf(".") < fileName.lastIndexOf("-")) {
            return null;
        }

        return fileName.substring(fileName.lastIndexOf("-") + 1, fileName.lastIndexOf("."));
    }
}
