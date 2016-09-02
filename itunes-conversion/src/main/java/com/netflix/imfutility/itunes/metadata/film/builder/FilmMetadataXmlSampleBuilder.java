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
package com.netflix.imfutility.itunes.metadata.film.builder;


import com.apple.itunes.importer.film.Cast;
import com.apple.itunes.importer.film.Character;
import com.apple.itunes.importer.film.Characters;
import com.apple.itunes.importer.film.Crew;
import com.apple.itunes.importer.film.FilmRating;
import com.apple.itunes.importer.film.Genre;
import com.apple.itunes.importer.film.GenreList;
import com.apple.itunes.importer.film.LocalizableMovieParticipant;
import com.apple.itunes.importer.film.LocalizableMovieParticipant.Roles;
import com.apple.itunes.importer.film.MovieRole;
import com.apple.itunes.importer.film.ObjectFactory;
import com.apple.itunes.importer.film.PackageType;
import com.apple.itunes.importer.film.ProductItem;
import com.apple.itunes.importer.film.ProductList;
import com.apple.itunes.importer.film.Ratings;
import com.apple.itunes.importer.film.Video;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Builder for creating sample metadata for iTunes film specification.
 * Only required fields will be filled by stub values.
 * (see {@link com.netflix.imfutility.itunes.metadata.film.FilmMetadataXmlProvider}).
 */
public final class FilmMetadataXmlSampleBuilder {

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    private static final String PACKAGE_VERSION = "film5.2";
    private static final String LOCALE = "en-US";
    private static final String COUNTRY = "US";
    private static final String TERRITORY = "WW";
    private static final String PROVIDER = "Required Provider";
    private static final String VIDEO_TYPE = "film";
    private static final String VIDEO_SUB_TYPE = "feature";
    private static final String VENDOR_ID = "vendor_id";
    private static final String TITLE = "Required Title";
    private static final String SYNOPSIS = "Required Synopsis";
    private static final String COMPANY = "Required Company";
    private static final String COPYRIGHT = "%4s " + COMPANY;
    private static final String GENRE_CODE = "CLASSICS-00";
    private static final String RATING_SYSTEM = "mpaa";
    private static final String RATING = "G";
    private static final String BILLING = "top";
    private static final String CREW_ROLE = "director";
    private static final String CAST_MEMBER_DISPLAY_NAME = "Required cast member display name";
    private static final String CHARACTER_NAME = "Required cast member character name";
    private static final String REFERENCE_ID = "REFERENCE_ID";
    private static final String CREW_MEMBER_DISPLAY_NAME = "Required crew member display name";

    private FilmMetadataXmlSampleBuilder() {
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
        video.getVendorId().add(VENDOR_ID);
        video.getCountry().add(COUNTRY);
        video.getOriginalSpokenLocale().add(LOCALE);
        video.getTitle().add(TITLE);
        video.getStudioReleaseTitle().add(TITLE);
        video.getSynopsis().add(SYNOPSIS);
        video.getProductionCompany().add(COMPANY);
        video.getCopyrightCline().add(String.format(COPYRIGHT, Calendar.getInstance().get(Calendar.YEAR)));
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
        genre.setCode(GENRE_CODE);
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
        rating.setSystem(RATING_SYSTEM);
        rating.setCode(RATING);
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
        castMember.setBilling(BILLING);
        castMember.getAppleIdOrReadOnlyInfoOrDisplayName().add(
                OBJECT_FACTORY.createLocalizableMovieParticipantDisplayName(CAST_MEMBER_DISPLAY_NAME));
        castMember.getAppleIdOrReadOnlyInfoOrDisplayName().add(
                OBJECT_FACTORY.createCharacters(buildCharacters()));
        return castMember;
    }

    private static Characters buildCharacters() {
        Characters characters = new Characters();
        characters.getCharacter().add(buildCharacter());
        return characters;
    }

    private static Character buildCharacter() {
        Character character = new Character();
        character.getCharacterNameOrReferenceIdOrCharacterNote().add(
                OBJECT_FACTORY.createReferenceId(REFERENCE_ID));
        character.getCharacterNameOrReferenceIdOrCharacterNote().add(
                OBJECT_FACTORY.createCharacterName(CHARACTER_NAME));
        return character;
    }


    //  Crew

    private static Crew buildCrew() {
        Crew crew = new Crew();
        crew.getMemberOrCrewMember().add(buildCrewMember());
        return crew;
    }

    private static LocalizableMovieParticipant buildCrewMember() {
        LocalizableMovieParticipant crewMember = new LocalizableMovieParticipant();
        crewMember.setBilling(BILLING);
        crewMember.getAppleIdOrReadOnlyInfoOrDisplayName().add(
                OBJECT_FACTORY.createLocalizableMovieParticipantDisplayName(CREW_MEMBER_DISPLAY_NAME));
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
        role.setValue(CREW_ROLE);
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
                OBJECT_FACTORY.createTerritory(TERRITORY));
        product.getTerritoryOrClearedForSaleOrClearedForTicketmaster().add(
                OBJECT_FACTORY.createClearedForSale(Boolean.FALSE.toString()));
        return product;
    }

    private static String getTodayFormattedDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
    }
}
