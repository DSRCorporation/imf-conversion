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

/**
 * Constants for all asset processors.
 */
public final class AssetProcessorConstants {

    private AssetProcessorConstants() {
    }

    // chapter constants
    public static final String CHAPTER_TYPE = "Chapter";
    public static final int CHAPTER_MIN_WIDTH = 640;
    public static final int CHAPTER_MIN_INDEX = 1;
    public static final int CHAPTER_MAX_INDEX = 99;

    // poster constants
    public static final String POSTER_TYPE = "Poster";
    public static final int POSTER_MIN_WIDTH = 1400;
    public static final int POSTER_MIN_HEIGHT = 2100;
    public static final int POSTER_AR_NUMERATOR = 2;
    public static final int POSTER_AR_DENOMINATOR = 3;

    // trailer constants
    public static final String MOV_FORMAT = "QuickTime / MOV";
}
