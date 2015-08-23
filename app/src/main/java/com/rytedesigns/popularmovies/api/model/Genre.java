package com.rytedesigns.popularmovies.api.model;

import com.google.gson.annotations.SerializedName;

public class Genre {
    @SerializedName("id")
    public int id;

    @SerializedName("name")
    public String name;
}
