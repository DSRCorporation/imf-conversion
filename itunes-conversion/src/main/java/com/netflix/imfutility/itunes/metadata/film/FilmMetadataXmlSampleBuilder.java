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
package com.netflix.imfutility.itunes.metadata.film;

import com.netflix.imfutility.generated.itunes.metadata.Cast;
import com.netflix.imfutility.generated.itunes.metadata.Crew;
import com.netflix.imfutility.generated.itunes.metadata.FilmRating;
import com.netflix.imfutility.generated.itunes.metadata.Genre;
import com.netflix.imfutility.generated.itunes.metadata.GenreList;
import com.netflix.imfutility.generated.itunes.metadata.LocalizableMovieParticipant;
import com.netflix.imfutility.generated.itunes.metadata.LocalizableMovieParticipant.Roles;
import com.netflix.imfutility.generated.itunes.metadata.MovieRole;
import com.netflix.imfutility.generated.itunes.metadata.ObjectFactory;
import com.netflix.imfutility.generated.itunes.metadata.PackageType;
import com.netflix.imfutility.generated.itunes.metadata.ProductItem;
import com.netflix.imfutility.generated.itunes.metadata.ProductList;
import com.netflix.imfutility.generated.itunes.metadata.Ratings;
import com.netflix.imfutility.generated.itunes.metadata.Video;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Builder for creating sample metadata for iTunes film specification.
 * Only required fields will be filled by stub values.
 * (see {@link com.netflix.imfutility.itunes.metadata.film.FilmMetadataXmlProvider}).
 */
public final class FilmMetadataXmlSampleBuilder {

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    private FilmMetadataXmlSampleBuilder() {
    }

    // Package

    public static PackageType buildPackage() {
        PackageType packageType = new PackageType();
        packageType.setVersion("film5.2");
        packageType.getLanguage().add("en-US");
        packageType.getProvider().add("Reuqired Provider");
        packageType.getVideo().add(buildVideo());
        return packageType;
    }

    //  Video

    private static Video buildVideo() {
        Video video = new Video();
        video.getType().add("film");
        video.getSubtype().add("feature");
        video.getVendorId().add("vendor_id");
        video.getCountry().add("US");
        video.getOriginalSpokenLocale().add("en-US");
        video.getTitle().add("Required title");
        video.getSynopsis().add("Required synopsis");
        video.getProductionCompany().add("Required company");
        video.getCopyrightCline().add(String.format("%4s Required Company", Calendar.getInstance().get(Calendar.YEAR)));
        video.getTheatricalReleaseDate().add(getTodayFormattedDate());
        video.getGenres().add(buildGenres());
        video.getRatings().add(buildRatings());
        video.getCast().add(buildCast());
        video.getCrew().add(buildCrew());
        video.getProducts().add(buildProducts());
        return video;
    }

    //  Genres

    private static GenreList buildGenres() {
        GenreList genres = new GenreList();
        genres.getGenre().add(buildGenre());
        return genres;
    }

    private static Genre buildGenre() {
        Genre genre = new Genre();
        genre.setCode("CLASSICS-00");
        return genre;
    }

    // Ratings

    private static Ratings buildRatings() {
        Ratings ratings = new Ratings();
        ratings.getRatingOrAdvisory().add(buildRating());
        return ratings;
    }

    private static FilmRating buildRating() {
        FilmRating rating = new FilmRating();
        rating.setSystem("mpaa");
        rating.setValue("G");
        return rating;
    }

    //  Cast

    private static Cast buildCast() {
        Cast cast = new Cast();
        cast.getActorOrCastMember().add(buildCastMember());
        return cast;
    }

    private static LocalizableMovieParticipant buildCastMember() {
        LocalizableMovieParticipant castMember = new LocalizableMovieParticipant();
        castMember.setBilling("top");
        castMember.getAppleIdOrReadOnlyInfoOrDisplayName().add(
                OBJECT_FACTORY.createLocalizableMovieParticipantDisplayName("Required cast member display name"));
        castMember.getAppleIdOrReadOnlyInfoOrDisplayName().add(
                OBJECT_FACTORY.createCharacterName("Required cast member character name"));
        return castMember;
    }

    //  Crew

    private static Crew buildCrew() {
        Crew crew = new Crew();
        crew.getMemberOrCrewMember().add(buildCrewMember());
        return crew;
    }

    private static LocalizableMovieParticipant buildCrewMember() {
        LocalizableMovieParticipant crewMember = new LocalizableMovieParticipant();
        crewMember.setBilling("top");
        crewMember.getAppleIdOrReadOnlyInfoOrDisplayName().add(
                OBJECT_FACTORY.createLocalizableMovieParticipantDisplayName("Required crew member display name"));
        crewMember.getAppleIdOrReadOnlyInfoOrDisplayName().add(
                OBJECT_FACTORY.createLocalizableMovieParticipantRoles(buildCrewRoles()));
        return crewMember;
    }

    private static Roles buildCrewRoles() {
        Roles roles = new Roles();
        roles.getRole().add(buildCrewRole());
        return roles;
    }

    private static MovieRole buildCrewRole() {
        MovieRole role = new MovieRole();
        role.setValue("director");
        return role;
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
                OBJECT_FACTORY.createTerritory("WW"));
        product.getTerritoryOrClearedForSaleOrClearedForTicketmaster().add(
                OBJECT_FACTORY.createClearedForSale(Boolean.FALSE.toString()));
        return product;
    }

    private static String getTodayFormattedDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
    }
}
