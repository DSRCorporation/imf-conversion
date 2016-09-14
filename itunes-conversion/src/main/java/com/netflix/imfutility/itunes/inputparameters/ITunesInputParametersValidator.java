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
package com.netflix.imfutility.itunes.inputparameters;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.netflix.imfutility.itunes.ITunesMode;
import com.netflix.imfutility.itunes.locale.LocaleValidationException;
import com.netflix.imfutility.itunes.locale.LocaleValidator;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

/**
 * Validator of input parameters specific for iTunes format. It checks whether all required parameters depending on the
 * mode are specified via command lines arguments. Only iTunes-specific parameters are validated.
 */
public final class ITunesInputParametersValidator {

    /**
     * Checks whether all required parameters depending on the  mode are specified via command lines arguments.
     * Only iTunes-specific parameters are validated.
     *
     * @param inputParameters the input parameters to be validated.
     * @throws ArgumentValidationException if some of the parameters are missing
     */
    public static void validateCmdLineArguments(ITunesInputParameters inputParameters) throws ArgumentValidationException {
        switch (inputParameters.getCmdLineArgs().getMode()) {
            case convert:
                validateConvertMode(inputParameters);
                break;
            case metadata:
                validateMetadataMode(inputParameters);
                break;
            case audiomap:
                validateAudiomapMode(inputParameters);
                break;
            case chapters:
                validateChaptersMode(inputParameters);
                break;
            default: // nothing
        }
    }

    private static void validateConvertMode(ITunesInputParameters inputParameters) throws ArgumentValidationException {
        validateVendorId(inputParameters.getCmdLineArgs().getVendorId());
        validateFallbackLocale(inputParameters.getCmdLineArgs().getFallbackLocale());

        // metadata.xml may be null. in this case a default metadata is created.
        validateFile(inputParameters.getMetadataFile(), "Metadata");
        // audiomap.xml may be null. in this case a default audiomap is created.
        validateFile(inputParameters.getAudiomapFile(), "Audiomap");
        // trailer may be null.
        validateFile(inputParameters.getTrailerFile(), "Trailer");
        // poster may be null.
        validateFile(inputParameters.getPosterFile(), "Poster");
        // chapters may be null. in this case a default chapters.xml is created.
        validateFile(inputParameters.getChaptersFile(), "Chapters");
        // cc may be null.
        validateFile(inputParameters.getCcFile(), "Closed captions");
        // subtitles may be null.
        validateFiles(inputParameters.getSubFiles(), "Subtitles");
    }

    private static void validateVendorId(String vendorId) throws ArgumentValidationException {
        if (vendorId == null || vendorId.isEmpty()) {
            throw new ArgumentValidationException(String.format(
                    "Vendor identifier (--vendor-id) must be specified in '%s' mode", ITunesMode.convert.name()));
        }

        if (!vendorId.matches("[a-zA-Z0-9_]{6,}")) {
            throw new ArgumentValidationException(String.format(
                    "Vendor identifier '%s' can only contain alphanumeric symbols and underscores "
                            + "and must be at least six characters long", vendorId));
        }
    }

    private static void validateMetadataMode(ITunesInputParameters inputParameters) throws ArgumentValidationException {
        validateOutput(inputParameters, "Metadata.xml", ITunesMode.metadata);
    }

    private static void validateAudiomapMode(ITunesInputParameters inputParameters) throws ArgumentValidationException {
        validateOutput(inputParameters, "Audiomap.xml", ITunesMode.audiomap);
    }

    private static void validateChaptersMode(ITunesInputParameters inputParameters) throws ArgumentValidationException {
        validateOutput(inputParameters, "Chapters.xml", ITunesMode.chapters);
    }

    private static void validateOutput(ITunesInputParameters inputParameters, String fileName, ITunesMode mode)
            throws ArgumentValidationException {
        if (inputParameters.getCmdLineArgs().getOutput() == null) {
            throw new ArgumentValidationException(String.format(
                    "%s output path (-o, --output) must be specified in '%s' mode", fileName, mode.name()));
        }
    }

    private static void validateFile(File file, String fileType) throws ArgumentValidationException {
        if (file != null && !file.isFile()) {
            throw new ArgumentValidationException(String.format(
                    "%s file '%s' must be an existing file", fileType, file.getAbsolutePath()));
        }
    }

    private static void validateFiles(List<File> files, String fileType) throws ArgumentValidationException {
        if (files != null && !files.isEmpty()) {
            files.forEach((file) -> validateFile(file, fileType));
        }
    }

    private static void validateFallbackLocale(String fallbackLocale) throws ArgumentValidationException {
        if (StringUtils.isBlank(fallbackLocale)) {
            return;
        }

        try {
            LocaleValidator.validateLocale(fallbackLocale);
        } catch (LocaleValidationException e) {
            throw new ArgumentValidationException("Fallback locale validation failed.", e);
        }
    }


    private ITunesInputParametersValidator() {
    }
}
