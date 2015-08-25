package com.rytedesigns.popularmovies.api.model;

import com.google.gson.annotations.SerializedName;

public class Review
{
    @SerializedName("id")
    public String id;

    @SerializedName("author")
    public String author;

    @SerializedName("content")
    public String content;

    @SerializedName("url")
    public String url;
}