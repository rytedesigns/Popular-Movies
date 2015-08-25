package com.rytedesigns.popularmovies.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Reviews
{
    @SerializedName("id")
    public int id;

    @SerializedName("results")
    public List<Review> reviews;
}
