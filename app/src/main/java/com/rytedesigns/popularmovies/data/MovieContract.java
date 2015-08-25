package com.rytedesigns.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract
{
    public static final String CONTENT_AUTHORITY = "com.rytedesigns.popularmovies.provider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movies";

    public static final String PATH_TRAILER = "trailers";

    public static final String PATH_REVIEW = "reviews";

    /**
     * Inner class that defines the table contents of the movies table
     */
    public static final class MovieEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movies";

        // columns
        public static final String COLUMN_MOVIE_ID = "_id";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        public static final String COLUMN_VOTE_COUNT = "vote_count";

        public static final String COLUMN_OVERVIEW = "overview";

        public static final String COLUMN_POSTER_PATH = "poster_path";

        public static final String COLUMN_POPULARITY = "popularity";

        public static final String COLUMN_RUNTIME = "runtime";

        public static final String COLUMN_FAVORITE = "favorite";

        /**
         * Build a Uri for a record of the table, using the ID
         *
         * @param id The ID of the record
         *           n A new Uri with the given ID appended to the end of the path
         */
        public static Uri buildMovieWithId(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /**
         * Parse the ID of a record, or return -1 instead
         *
         * @param uri The Uri of the record
         * @return The Id of the record or -1 if this doesn't apply
         */
        public static long getIdFromUri(Uri uri)
        {
            return ContentUris.parseId(uri);
        }
    }

    public static final class TrailerEntry implements BaseColumns
    {
        // Content URI for the TrailerEntry
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        // Constant strings to tell the difference between a list of items (CONTENT_TYPE)
        // and a singe item (CONTENT_ITEM_TYPE)
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        public static final String TABLE_NAME = "trailers";

        // columns
        public static final String COLUMN_TRAILER_NAME = "trailer_name";

        public static final String COLUMN_TRAILER_SIZE = "trailer_size";

        public static final String COLUMN_TRAILER_SOURCE_ID = "trailer_source";

        public static final String COLUMN_TRAILER_TYPE = "trailer_type";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        /**
         * Get the movie ID in the URI (the ID from the Backend)
         *
         * @param uri The trailer's URI with the movie ID
         * @return The movie ID or -1 if doesn't exist
         */
        public static long getMovieIdFromUri(Uri uri)
        {
            return ContentUris.parseId(uri);
        }

        /**
         * Creates a trailer uri with the movie id (from the backend) appended
         *
         * @param movieId The movie ID
         * @return the URI of the trailer
         */
        public static Uri buildTrailerWithId(long movieId)
        {
            return ContentUris.withAppendedId(CONTENT_URI, movieId);
        }
    }

    public static final class ReviewEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static final String TABLE_NAME = "reviews";

        public static final String COLUMN_REVIEW_AUTHOR = "author";

        public static final String COLUMN_REVIEW_CONTENT = "content";

        public static final String COLUMN_REVIEW_ID = "review_id";

        public static final String COLUMN_REVIEW_URL = "url";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        /**
         * Get the movie ID in the URI (the ID from the Backend)
         *
         * @param uri The Uri of the review with the movie id appended
         * @return The ID of the movie, or -1 if doesn't exist
         */
        public static long getMovieIdFromUri(Uri uri)
        {
            return ContentUris.parseId(uri);
        }

        /**
         * Creates a trailer uri with the movie id (from the backend) appended
         *
         * @param insertedId The ID of the movie
         * @return The uri of the review
         */
        public static Uri buildTrailerWithId(long insertedId)
        {
            return ContentUris.withAppendedId(CONTENT_URI, insertedId);
        }
    }
}