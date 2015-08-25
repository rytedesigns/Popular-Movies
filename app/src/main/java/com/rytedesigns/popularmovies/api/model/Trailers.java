package com.rytedesigns.popularmovies.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Trailers
{
    @SerializedName("id")
    public int id;

    @SerializedName("youtube")
    public List<YouTubeTrailer> youTubTrailers;
}
