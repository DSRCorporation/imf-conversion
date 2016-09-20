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
package com.netflix.imfutility.itunes.metadata.factory;

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.itunes.ITunesPackageType;
import com.netflix.imfutility.itunes.metadata.MetadataXmlProvider;
import com.netflix.imfutility.itunes.metadata.film.FilmMetadataXmlProvider;
import com.netflix.imfutility.itunes.metadata.tv.TvMetadataXmlProvider;
import com.netflix.imfutility.itunes.util.MetadataUtils;
import org.junit.Test;

import java.io.File;

import static junit.framework.TestCase.assertTrue;


/**
 * Tests correct creation of appropriate MetadataXmlProvider.
 * (see {@link MetadataXmlProviderFactory ).
 */
public class MetadataXmlProviderFactoryTest {

    @Test
    public void testCorrectFilmProviderCreation() throws Exception {
        MetadataXmlProvider<?> provider = MetadataXmlProviderFactory.createProvider(
                MetadataUtils.getCorrectFilmMetadataXml(), ITunesPackageType.tv);

        assertTrue(provider instanceof FilmMetadataXmlProvider);
    }

    @Test
    public void testCorrectTvProviderCreation() throws Exception {
        MetadataXmlProvider<?> provider = MetadataXmlProviderFactory.createProvider(
                MetadataUtils.getCorrectTvMetadataXml(), ITunesPackageType.film);

        assertTrue(provider instanceof TvMetadataXmlProvider);
    }

    @Test
    public void testCorrectFallbackFilmProviderCreation() throws Exception {
        MetadataXmlProvider<?> provider = MetadataXmlProviderFactory.createProvider(
                null, ITunesPackageType.film);

        assertTrue(provider instanceof FilmMetadataXmlProvider);
    }

    @Test
    public void testCorrectFallbackTvProviderCreation() throws Exception {
        MetadataXmlProvider<?> provider = MetadataXmlProviderFactory.createProvider(
                null, ITunesPackageType.tv);

        assertTrue(provider instanceof TvMetadataXmlProvider);
    }

    @Test(expected = ConversionException.class)
    public void testUnknownTypeMetadataFile() throws Exception {
        MetadataXmlProviderFactory.createProvider(MetadataUtils.getUnknownVersionMetadataXml(), ITunesPackageType.tv);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidMetadataFile() throws Exception {
        MetadataXmlProviderFactory.createProvider(new File("invalid-path"), ITunesPackageType.tv);
    }
}
