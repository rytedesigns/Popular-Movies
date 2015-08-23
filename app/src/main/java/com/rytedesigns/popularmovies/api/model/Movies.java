package com.rytedesigns.popularmovies.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ryan on 8/21/2015.
 */
public class Movies {
    @SerializedName("results")
    public List<Movie> movies;
}
