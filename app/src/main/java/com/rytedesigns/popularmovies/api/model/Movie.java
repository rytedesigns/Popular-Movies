package com.rytedesigns.popularmovies.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Movie {
    @SerializedName("adult")
    public boolean adult = false;

    @SerializedName("backdrop_path")
    public String backdropPath;

    @SerializedName("budget")
    public int budget = 150000000;

    @SerializedName("genres")
    public List<Genre> genres;

    @SerializedName("homepage")
    public String homepage;

    @SerializedName("id")
    public int id = 76341;

    @SerializedName("imdb_id")
    public String imdbId;

    @SerializedName("original_language")
    public String originalLanguage;

    @SerializedName("original_title")
    public String originalTitle;

    @SerializedName("overview")
    public String overview;

    @SerializedName("popularity")
    public double popularity;

    @SerializedName("poster_path")
    public String posterPath;

    @SerializedName("production_companies")
    public List<ProductionCompany> productionCompanies;

    @SerializedName("production_countries")
    public List<ProductionCountry> productionCountries;

    @SerializedName("releaseDate")
    public String releaseDate;

    @SerializedName("revenue")
    public int revenue;

    @SerializedName("runtime")
    public int runtime;

    @SerializedName("status")
    public String status;

    @SerializedName("tagline")
    public String tagline;

    @SerializedName("title")
    public String title;

    @SerializedName("video")
    public boolean video;

    @SerializedName("vote_average")
    public double voteAverage;

    @SerializedName("vote_count")
    public int voteCount;

    @SerializedName("trailers")
    public Trailers trailers;

    @SerializedName("reviews")
    public Reviews reviews;
}
