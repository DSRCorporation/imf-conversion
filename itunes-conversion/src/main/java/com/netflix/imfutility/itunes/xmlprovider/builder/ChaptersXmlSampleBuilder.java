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

import com.netflix.imfutility.generated.itunes.metadata.ChapterInputType;
import com.netflix.imfutility.generated.itunes.metadata.ChaptersInputType;

/**
 * Builder for creating sample chapters specified for iTunes.
 * (see {@link com.netflix.imfutility.itunes.xmlprovider.ChaptersXmlProvider}.
 */
public final class ChaptersXmlSampleBuilder {

    private ChaptersXmlSampleBuilder() {
    }

    public static ChaptersInputType buildInputChapters() {
        ChaptersInputType chapters = new ChaptersInputType();
        chapters.setBasedir(".");
        chapters.setTimecodeFormat("qt_text");
        chapters.getChapter().add(buildInputChapter());
        return chapters;
    }

    public static ChapterInputType buildInputChapter() {
        ChapterInputType chapter = new ChapterInputType();
        chapter.setStartTime("00:00:00");
        chapter.setTitle(MetadataXmlSampleBuilder.buildTitle());
        chapter.setFileName("chapter01.jpg");
        return chapter;
    }
}
