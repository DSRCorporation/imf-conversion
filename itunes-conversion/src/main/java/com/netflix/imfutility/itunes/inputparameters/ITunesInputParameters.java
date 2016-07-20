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

import com.netflix.imfutility.inputparameters.ImfUtilityInputParameters;
import com.netflix.imfutility.itunes.ITunesConversionConstants;
import com.netflix.imfutility.resources.ResourceHelper;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A wrapper on command line arguments with helper methods to get input parameters obtained from the command line.
 * Specified for iTunes format.
 */
public class ITunesInputParameters extends ImfUtilityInputParameters {

    private final ITunesCmdLineArgs cmdLineArgs;
    private final ITunesDefaultTools defaultTools;

    public ITunesInputParameters(ITunesCmdLineArgs cmdLineArgs, ITunesDefaultTools defaultTools) {
        super(cmdLineArgs, defaultTools);
        this.cmdLineArgs = cmdLineArgs;
        this.defaultTools = defaultTools;
    }


    @Override
    public InputStream getDefaultConversionXml() {
        return ResourceHelper.getResourceInputStream(ITunesConversionConstants.CONVERSION_XML);
    }

    @Override
    public String getDefaultConversionXmlPath() {
        return ITunesConversionConstants.CONVERSION_XML;
    }

    /**
     * Gets a metadata file as specified via command line arguments or null if it's not specified.
     *
     * @return a metadata file as specified via command line arguments or null if it's not specified.
     */
    public File getMetadataFile() {
        if (cmdLineArgs.getMetadata() == null) {
            return null;
        }
        return new File(cmdLineArgs.getMetadata());
    }

    /**
     * Gets an audiomap file as specified via command line arguments or null if it's not specified.
     *
     * @return an audiomap file as specified via command line arguments or null if it's not specified.
     */
    public File getAudiomapFile() {
        if (cmdLineArgs.getAudioMap() == null) {
            return null;
        }
        return new File(cmdLineArgs.getAudioMap());
    }

    /**
     * Gets a trailer file as specified via command line arguments or null if it's not specified.
     *
     * @return a trailer file as specified via command line arguments or null if it's not specified.
     */
    public File getTrailerFile() {
        if (cmdLineArgs.getTrailer() == null) {
            return null;
        }
        return new File(cmdLineArgs.getTrailer());
    }

    /**
     * Gets a poster file as specified via command line arguments or null if it's not specified.
     *
     * @return a poster file as specified via command line arguments or null if it's not specified.
     */
    public File getPosterFile() {
        if (cmdLineArgs.getPoster() == null) {
            return null;
        }
        return new File(cmdLineArgs.getPoster());
    }

    /**
     * Gets a chapters file as specified via command line arguments or null if it's not specified.
     *
     * @return a chapters file as specified via command line arguments or null if it's not specified.
     */
    public File getChaptersFile() {
        if (cmdLineArgs.getChapters() == null) {
            return null;
        }
        return new File(cmdLineArgs.getChapters());
    }

    /**
     * Gets a trailer file as specified via command line arguments or null if it's not specified.
     *
     * @return a trailer file as specified via command line arguments or null if it's not specified.
     */
    public List<File> getCcFiles() {
        if (cmdLineArgs.getCc() == null || cmdLineArgs.getCc().isEmpty()) {
            return null;
        }
        return cmdLineArgs.getCc().stream()
                .filter(Predicate.isEqual(null).negate())
                .map(File::new).collect(Collectors.toList());
    }

    @Override
    public ITunesCmdLineArgs getCmdLineArgs() {
        return cmdLineArgs;
    }
}
