package com.rytedesigns.popularmovies.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Movies
{
    @SerializedName("results")
    public List<Movie> movies;

    @SerializedName("total_pages")
    public int totalPages;
}
