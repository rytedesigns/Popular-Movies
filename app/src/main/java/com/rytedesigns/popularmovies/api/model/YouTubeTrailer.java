package com.rytedesigns.popularmovies.api.model;

import com.google.gson.annotations.SerializedName;

public class YouTubeTrailer
{
    @SerializedName("name")
    public String trailerName;

    @SerializedName("size")
    public String trailerSize;

    @SerializedName("source")
    public String trailerSource;

    @SerializedName("type")
    public String trailerType;
}
