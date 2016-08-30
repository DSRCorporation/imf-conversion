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
package com.netflix.imfutility.itunes.chapters.builder;

import com.netflix.imfutility.generated.itunes.chapters.InputChapterItem;
import com.netflix.imfutility.generated.itunes.chapters.InputChapterList;
import com.netflix.imfutility.generated.itunes.metadata.NonEmptyLocalizableTextElement;

/**
 * Builder for creating sample chapters specified for iTunes.
 * (see {@link com.netflix.imfutility.itunes.chapters.ChaptersXmlProvider}.
 */
public final class ChaptersXmlSampleBuilder {

    private ChaptersXmlSampleBuilder() {
    }

    public static InputChapterList buildInputChapters() {
        InputChapterList chapters = new InputChapterList();
        chapters.setBasedir(".");
        chapters.setTimecodeFormat("qt_text");
        chapters.getInputChapter().add(buildInputChapter());
        return chapters;
    }

    public static InputChapterItem buildInputChapter() {
        InputChapterItem chapter = new InputChapterItem();
        chapter.setStartTime("00:00:00");
        chapter.setTitle(buildTitle());
        chapter.setFileName("chapter01.jpg");
        return chapter;
    }

    private static NonEmptyLocalizableTextElement buildTitle() {
        NonEmptyLocalizableTextElement title = new NonEmptyLocalizableTextElement();
        title.setLocale("en-US");
        title.setValue("Required chapter title");
        return title;
    }

}
