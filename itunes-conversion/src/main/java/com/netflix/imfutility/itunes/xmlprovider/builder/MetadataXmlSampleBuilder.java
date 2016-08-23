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

import com.netflix.imfutility.generated.itunes.metadata.BillingType;
import com.netflix.imfutility.generated.itunes.metadata.CastMemberType;
import com.netflix.imfutility.generated.itunes.metadata.CastType;
import com.netflix.imfutility.generated.itunes.metadata.CrewMemberType;
import com.netflix.imfutility.generated.itunes.metadata.CrewRoleType;
import com.netflix.imfutility.generated.itunes.metadata.CrewRolesType;
import com.netflix.imfutility.generated.itunes.metadata.CrewType;
import com.netflix.imfutility.generated.itunes.metadata.GenreType;
import com.netflix.imfutility.generated.itunes.metadata.GenresType;
import com.netflix.imfutility.generated.itunes.metadata.ISO3166CountryCode;
import com.netflix.imfutility.generated.itunes.metadata.PackageType;
import com.netflix.imfutility.generated.itunes.metadata.ProductType;
import com.netflix.imfutility.generated.itunes.metadata.ProductsType;
import com.netflix.imfutility.generated.itunes.metadata.RatingSystemType;
import com.netflix.imfutility.generated.itunes.metadata.RatingType;
import com.netflix.imfutility.generated.itunes.metadata.RatingsType;
import com.netflix.imfutility.generated.itunes.metadata.VideoType;
import com.netflix.imfutility.generated.itunes.metadata.VideoTypeSubType;
import com.netflix.imfutility.generated.itunes.metadata.VideoTypeType;
import com.netflix.imfutility.generated.itunes.metadata.WWType;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Builder for creating sample metadata specified for iTunes.
 * Only required fields will be filled by stub values.
 * (see {@link com.netflix.imfutility.itunes.xmlprovider.MetadataXmlProvider}).
 */
public final class MetadataXmlSampleBuilder {

    private MetadataXmlSampleBuilder() {
    }

    // Package

    public static PackageType buildPackage() throws DatatypeConfigurationException {
        PackageType packageType = new PackageType();
        packageType.setVersion("film5.0");
        packageType.setLanguage("en-US");
        packageType.setProvider("Required company");
        packageType.setVideo(buildVideo());
        return packageType;
    }

    //  Video

    private static VideoType buildVideo() throws DatatypeConfigurationException {
        VideoType video = new VideoType();
        video.setType(VideoTypeType.FILM);
        video.setSubtype(VideoTypeSubType.FEATURE);
        video.setVendorId("000000000000");
        video.setCountry(ISO3166CountryCode.US);
        video.setOriginalSpokenLocale("en-US");
        video.setTitle("Required title");
        video.setSynopsis("Required synopsis");
        video.setProductionCompany("Required company");
        video.setCopyrightCline(String.format("%4s Required Company", Calendar.getInstance().get(Calendar.YEAR)));
        video.setTheatricalReleaseDate(getTodayDateWithClearedTimezone());
        video.setGenres(buildGenres());
        video.setRatings(buildRatings());
        video.setCast(buildCast());
        video.setCrew(buildCrew());
        video.setProducts(buildProducts());
        return video;
    }

    //  Genres

    private static GenresType buildGenres() {
        GenresType genres = new GenresType();
        genres.getGenre().add(GenreType.CLASSICS);
        return genres;
    }

    // Ratings

    private static RatingsType buildRatings() {
        RatingsType ratings = new RatingsType();
        ratings.getRating().add(buildRating());
        return ratings;
    }

    private static RatingType buildRating() {
        RatingType rating = new RatingType();
        rating.setSystem(RatingSystemType.MPAA);
        rating.setValue("G");
        return rating;
    }

    //  Cast

    private static CastType buildCast() {
        CastType cast = new CastType();
        cast.getCastMember().add(buildCastMember());
        return cast;
    }

    private static CastMemberType buildCastMember() {
        CastMemberType castMember = new CastMemberType();
        castMember.setBilling(BillingType.TOP);
        castMember.setDisplayName("Required cast member display name");
        castMember.setCharacterName("Required cast member character name");
        return castMember;
    }

    //  Crew

    private static CrewType buildCrew() {
        CrewType crew = new CrewType();
        crew.getCrewMember().add(buildCrewMember());
        return crew;
    }

    private static CrewMemberType buildCrewMember() {
        CrewMemberType crewMember = new CrewMemberType();
        crewMember.setBilling(BillingType.TOP);
        crewMember.setDisplayName("Required crew member name");
        crewMember.setRoles(buildCrewRoles());
        return crewMember;
    }

    private static CrewRolesType buildCrewRoles() {
        CrewRolesType crewRoles = new CrewRolesType();
        crewRoles.getRole().add(CrewRoleType.DIRECTOR);
        return crewRoles;
    }

    // Products

    private static ProductsType buildProducts() {
        ProductsType products = new ProductsType();
        products.getProduct().add(buildProduct());
        return products;
    }

    private static ProductType buildProduct() {
        ProductType product = new ProductType();
        product.setTerritory(WWType.WW.value());
        product.setClearedForSale(true);
        return product;
    }

    private static XMLGregorianCalendar getTodayDateWithClearedTimezone() throws DatatypeConfigurationException {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.clear(Calendar.ZONE_OFFSET);

        XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        xmlCalendar.setTimezone(DatatypeConstants.FIELD_UNDEFINED);

        return xmlCalendar;
    }
}
