package com.rytedesigns.popularmovies.api;

import com.rytedesigns.popularmovies.api.model.Movie;
import com.rytedesigns.popularmovies.api.model.Movies;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface TheMovieDatabaseService {
    @GET("/discover/movie")
    void discoverTopMovies(@Query("api_key") String apiKey, @Query("sort_by") String sortOrder, Callback<Movies> callback);

    @GET("/movie/{id}")
    void getMovie(@Path("id") int id, @Query("api_key") String apiKey, @Query("append_to_response") String appendToResponse, Callback<Movie> callback);

    @GET("/movie/{id}/trailers")
    void getTrailers(@Path("id") int id, @Query("api_key") String apiKey, Callback<Movie> callback);

    @GET("/movie/{id}/reviews")
    void getReviews(@Path("id") int id, @Query("api_key") String apiKey, Callback<Movie> callback);
}
