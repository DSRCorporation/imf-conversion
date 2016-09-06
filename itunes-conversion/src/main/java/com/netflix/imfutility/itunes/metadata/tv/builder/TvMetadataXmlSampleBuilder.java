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
package com.netflix.imfutility.itunes.metadata.tv.builder;


import com.apple.itunes.importer.tv.FilmRating;
import com.apple.itunes.importer.tv.ObjectFactory;
import com.apple.itunes.importer.tv.PackageType;
import com.apple.itunes.importer.tv.Preview;
import com.apple.itunes.importer.tv.PreviewDefinitionList;
import com.apple.itunes.importer.tv.ProductItem;
import com.apple.itunes.importer.tv.ProductList;
import com.apple.itunes.importer.tv.Ratings;
import com.apple.itunes.importer.tv.Video;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Builder for creating sample metadata for iTunes tv specification.
 * Only required fields will be filled by stub values.
 * (see {@link com.netflix.imfutility.itunes.metadata.tv.TvMetadataXmlProvider}).
 */
public final class TvMetadataXmlSampleBuilder {

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    private static final String PACKAGE_VERSION = "tv5.2";
    private static final String LOCALE = "en-US";
    private static final String TERRITORY = "US";
    private static final String PROVIDER = "Required Provider";
    private static final String VIDEO_TYPE = "tv";
    private static final String VIDEO_SUB_TYPE = "episode";
    private static final String CONTAINER_ID = "CONTAINER_001";
    private static final String CONTAINER_POSITION = "1";
    private static final String VENDOR_ID = "vendor_id";
    private static final String EPISODE_PRODUCTION_NUMBER = "101";
    private static final String TITLE = "Required Title";
    private static final String DESCRIPTION = "Required Description";
    private static final String RATING_SYSTEM = "us-tv";
    private static final String RATING = "TV-G";
    private static final String PREVIEW_TIMECODE_FORMAT = "qt_text";
    private static final String PREVIEW_START_TIME = "00:00:00";
    private static final String PREVIEW_END_TIME = "00:01:00";
    private static final String PREVIEW_IMAGE_TIME = "00:02:00";

    private TvMetadataXmlSampleBuilder() {
    }

    // Package

    public static PackageType buildPackage() {
        PackageType packageType = new PackageType();
        packageType.setVersion(PACKAGE_VERSION);
        packageType.getLanguage().add(LOCALE);
        packageType.getProvider().add(PROVIDER);
        packageType.getVideo().add(buildVideo());
        return packageType;
    }

    //  Video

    private static Video buildVideo() {
        Video video = new Video();
        video.getType().add(VIDEO_TYPE);
        video.getSubtype().add(VIDEO_SUB_TYPE);
        video.getContainerId().add(CONTAINER_ID);
        video.getContainerPosition().add(CONTAINER_POSITION);
        video.getVendorId().add(VENDOR_ID);
        video.getEpisodeProductionNumber().add(EPISODE_PRODUCTION_NUMBER);
        video.getOriginalSpokenLocale().add(LOCALE);
        video.getTitle().add(TITLE);
        video.getStudioReleaseTitle().add(TITLE);
        video.getDescription().add(DESCRIPTION);
        video.getReleaseDate().add(getTodayFormattedDate());
        video.getRatings().add(buildRatings());
        video.getPreviews().add(buildPreviews());
        video.getProducts().add(buildProducts());
        return video;
    }

    // Ratings

    private static Ratings buildRatings() {
        Ratings ratings = new Ratings();
        ratings.getRatingOrAdvisory().add(buildRating());
        return ratings;
    }

    private static FilmRating buildRating() {
        FilmRating rating = new FilmRating();
        rating.setSystem(RATING_SYSTEM);
        rating.setCode(RATING);
        return rating;
    }

    //  Preview

    private static PreviewDefinitionList buildPreviews() {
        PreviewDefinitionList previews = new PreviewDefinitionList();
        previews.getPreview().add(buildPreview());
        return previews;
    }

    private static Preview buildPreview() {
        Preview preview = new Preview();
        preview.setTimecodeFormat(PREVIEW_TIMECODE_FORMAT);
        preview.setStartTime(PREVIEW_START_TIME);
        preview.setEndTime(PREVIEW_END_TIME);
        preview.setImageTime(PREVIEW_IMAGE_TIME);
        return preview;
    }

    // Products

    private static ProductList buildProducts() {
        ProductList products = new ProductList();
        products.getProduct().add(buildProduct());
        return products;
    }

    private static ProductItem buildProduct() {
        ProductItem product = new ProductItem();
        product.getTerritoryOrClearedForSaleOrClearedForTicketmaster().add(
                OBJECT_FACTORY.createTerritory(TERRITORY));
        product.getTerritoryOrClearedForSaleOrClearedForTicketmaster().add(
                OBJECT_FACTORY.createClearedForSale(Boolean.FALSE.toString()));
        return product;
    }

    private static String getTodayFormattedDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
    }
}
