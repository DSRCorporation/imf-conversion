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
package com.netflix.imfutility.itunes.metadata.film.wrap;

import com.netflix.imfutility.generated.itunes.metadata.film.ChapterItem;
import com.netflix.imfutility.generated.itunes.metadata.film.File;
import com.netflix.imfutility.generated.itunes.metadata.film.NonEmptyLocalizableTextElement;
import com.netflix.imfutility.itunes.metadata.ElemWrapper;

import javax.xml.bind.JAXBContext;
import java.util.List;

/**
 * Wrapper for {@link ChapterItem}.
 */
public class ChapterItemWrapper extends ElemWrapper<ChapterItem> {

    private static final String START_TIME = "start_time";
    private static final String TITLE = "title";
    private static final String ARTWORK_FILE = "artwork_file";

    private List<Object> mixed;

    public ChapterItemWrapper(JAXBContext context, ChapterItem chapterItem) {
        super(context, chapterItem);
        this.mixed = inner.getStartTimeOrChapterStartTimeOrTitle();
    }

    public void setStartTime(String startTime) {
        mixed.add(objectFactory.createStartTime(startTime));
    }

    public String getStartTime() {
        return getElemValueByName(mixed, START_TIME, String.class);
    }

    public void setTitle(NonEmptyLocalizableTextElement title) {
        mixed.add(objectFactory.createChapterItemTitle(title));
    }

    public NonEmptyLocalizableTextElement getTitle() {
        return getElemValueByName(mixed, TITLE, NonEmptyLocalizableTextElement.class);
    }

    public void setArtworkFile(File file) {
        mixed.add(objectFactory.createArtworkFile(file));
    }

    public File getArtworkFile() {
        return getElemValueByName(mixed, ARTWORK_FILE, File.class);
    }
}
