package com.rytedesigns.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

public class MovieProvider extends ContentProvider {
    public static final String LOG_TAG = MovieProvider.class.getSimpleName();

    public static final int MOVIE = 100;

    public static final int MOVIE_WITH_ID = 101;

    public static final int TRAILER = 200;

    public static final int TRAILER_WITH_MOVIE_ID = 201;

    public static final int REVIEW = 300;

    public static final int REVIEW_WITH_MOVIE_ID = 301;

    private static final UriMatcher mUriMatcher = createUriMatcher();

    private SQLiteOpenHelper mOpenHelper;

    /**
     * Create the {@code UriMatcher} for pointing records into the DB
     *
     * @return A {@code UriMatcher} instance
     */
    public static UriMatcher createUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);

        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_WITH_ID);

        matcher.addURI(authority, MovieContract.PATH_TRAILER, TRAILER);

        matcher.addURI(authority, MovieContract.PATH_TRAILER + "/#", TRAILER_WITH_MOVIE_ID);

        matcher.addURI(authority, MovieContract.PATH_REVIEW, REVIEW);

        matcher.addURI(authority, MovieContract.PATH_REVIEW + "/#", REVIEW_WITH_MOVIE_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());

        return true;
    }

    /**
     * Queries the DB for the data requested by the caller of the method
     *
     * @param uri           The Uri of the data queried, containing information about where to search
     * @param projection    An array of required columns from the table ({@code null} for all columns)
     * @param selection     The where clause
     * @param selectionArgs The arguments of the where clause
     * @param sortOrder     The order of sorting the movies
     * @return A {@code Cursor} instance with the data
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        Cursor cursor;

        switch (mUriMatcher.match(uri))
        {
            case MOVIE:
            {
                cursor = db.query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            case MOVIE_WITH_ID:
            {
                Log.d(LOG_TAG, "Querying data using the following uri " + uri);

                long id = MovieContract.MovieEntry.getIdFromUri(uri);

                Log.d(LOG_TAG, "retrieved id " + id);

                cursor = db.query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{Long.toString(id)},
                        null,
                        null,
                        sortOrder);

                break;
            }

            case TRAILER:
            {
                cursor = db.query(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            case TRAILER_WITH_MOVIE_ID:
            {
                Log.d(LOG_TAG, "Querying data using the following uri " + uri);

                long id = MovieContract.TrailerEntry.getMovieIdFromUri(uri);

                Log.d(LOG_TAG, "retrieved id " + id);

                cursor = db.query(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{Long.toString(id)},
                        null,
                        null,
                        sortOrder);

                break;
            }

            case REVIEW:
            {
                cursor = db.query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            case REVIEW_WITH_MOVIE_ID:
            {
                Log.d(LOG_TAG, "Querying data using the following uri " + uri);

                long id = MovieContract.ReviewEntry.getMovieIdFromUri(uri);

                Log.d(LOG_TAG, "retrieved id " + id);

                cursor = db.query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{Long.toString(id)},
                        null,
                        null,
                        sortOrder);

                break;
            }


            default:
            {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        cursor.moveToFirst();

        return cursor;
    }

    /**
     * Returns the type of the item pointed by a given URI
     *
     * @param uri A given content URI pointing to data in the sqlite database
     * @return The type data pointed by the uri
     */
    @Override
    public String getType(Uri uri) {
        int match = mUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;

            case MOVIE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;

            case REVIEW:
                return MovieContract.ReviewEntry.CONTENT_TYPE;

            case REVIEW_WITH_MOVIE_ID:
                return MovieContract.ReviewEntry.CONTENT_ITEM_TYPE;

            case TRAILER:
                return MovieContract.TrailerEntry.CONTENT_TYPE;

            case TRAILER_WITH_MOVIE_ID:
                return MovieContract.TrailerEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * Insert a new record into the table pointed by the Uri
     *
     * @param uri    The Uri that should point into a table
     * @param values Record to add into the table
     * @return A Content Uri with the ID of the inserted row
     */
    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int uriType = mUriMatcher.match(uri);

        Uri insertionUri;

        long insertedId;

        switch (uriType)
        {
            case MOVIE:
            {
                Cursor cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        new String[]{MovieContract.MovieEntry.COLUMN_MOVIE_ID},
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{values.get(MovieContract.MovieEntry.COLUMN_MOVIE_ID).toString()},
                        null,
                        null,
                        null);

                if (cursor.moveToFirst())
                {
                    insertionUri = MovieContract.MovieEntry.buildMovieWithId(cursor.getInt(0));

                    db.update(MovieContract.MovieEntry.TABLE_NAME, values, MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?", new String[]{Long.toString(cursor.getInt(0))});
                }
                else
                {
                    insertedId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);

                    if (insertedId > 0)
                    {
                        insertionUri = MovieContract.MovieEntry.buildMovieWithId(insertedId);
                    }
                    else
                    {
                        throw new SQLException("Failed to insert row into " + uri);
                    }
                }

                break;
            }

            case TRAILER: {
                insertedId = db.insertWithOnConflict(MovieContract.TrailerEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

                if (insertedId > 0) {
                    insertionUri = MovieContract.TrailerEntry.buildTrailerWithId(insertedId);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }

            case REVIEW: {
                insertedId = db.insertWithOnConflict(MovieContract.ReviewEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

                if (insertedId > 0) {
                    insertionUri = MovieContract.ReviewEntry.buildTrailerWithId(insertedId);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri " + uri);
            }
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return insertionUri;
    }

    /**
     * Deletes the records in the table pointed by the URI and match the other args
     *
     * @param uri           Uri of the table where to search the records to delete
     * @param selection     A selection criteria to apply when filtering rows. If {@code null} then all
     *                      rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by the values
     *                      from selectionArgs, in order that they appear in the selection. The values
     *                      will be bound as Strings.
     * @return The number of deleted rows
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        int rowsDeleted;

        //delete all rows and return the number of records deleted
        if (selection == null) {
            selection = "1";
        }

        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case TRAILER:
                rowsDeleted = db.delete(MovieContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case REVIEW:
                rowsDeleted = db.delete(MovieContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }


    /**
     * Updates a record on the table pointed by the URI.
     *
     * @param uri           Uri of the table where to search the records to update
     * @param values        The new set of {@code ContentValues} to replace the old ones
     * @param selection     A selection criteria to apply when filtering rows. If {@code null} then all
     *                      rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by the values
     *                      from selectionArgs, in order that they appear in the selection. The values
     *                      will be bound as Strings.
     * @return The number of updated rows
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int match = mUriMatcher.match(uri);

        int rowsUpdated;

        switch (match)
        {
            case MOVIE:
            {
                rowsUpdated = db.update(
                        MovieContract.MovieEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }

            case TRAILER:
            {
                rowsUpdated = db.update(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);

                break;
            }

            case REVIEW:
            {
                rowsUpdated = db.update(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);

                break;
            }

            default:
            {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        Log.e("update", "rows update: "+ rowsUpdated);

        if (rowsUpdated != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    /**
     * This method is used for mass insertion into the database
     *
     * @param uri    The content:// URI of the insertion request.
     * @param values An array of sets of column_name/value pairs to add to the database. This must not be null.
     * @return The number of values that were inserted.
     */
    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values)
    {
        int count = 0;

        for (ContentValues item : values)
        {
            Uri id = insert(uri, item);

            if (id != null)
            {
                count++;
            }
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }
}