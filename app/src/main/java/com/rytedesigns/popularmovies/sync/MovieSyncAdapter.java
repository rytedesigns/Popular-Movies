package com.rytedesigns.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.rytedesigns.popularmovies.R;
import com.rytedesigns.popularmovies.Utility;
import com.rytedesigns.popularmovies.api.TheMovieDatabaseService;
import com.rytedesigns.popularmovies.api.model.Movie;
import com.rytedesigns.popularmovies.api.model.Movies;
import com.rytedesigns.popularmovies.api.model.Review;
import com.rytedesigns.popularmovies.api.model.YouTubeTrailer;
import com.rytedesigns.popularmovies.data.MovieContract;

import java.util.List;
import java.util.Vector;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter
{
    // time in seconds when to sync
    private static final int HOUR_IN_SECONDS = 3600;
    private static final int SYNC_INTERVAL = HOUR_IN_SECONDS * 12;
    private static final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    public MovieSyncAdapter(Context context, boolean autoInitialize)
    {
        super(context, autoInitialize);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    private static void configurePeriodicSync(Context context, int syncInterval, int flexTime)
    {
        Log.d(LOG_TAG, "configurePeriodicSync");

        Account account = getSyncAccount(context);

        String authority = context.getString(R.string.content_provider_authority);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        }
        else
        {
            ContentResolver.addPeriodicSync(account,
                                            authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context)
    {
        Log.d(LOG_TAG, "syncImmediately");

        Bundle bundle = new Bundle();

        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        ContentResolver.requestSync(getSyncAccount(context),
                                    context.getString(R.string.content_provider_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    private static Account getSyncAccount(Context context)
    {
        Log.d(LOG_TAG, "getSyncAccount");

        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount))
        {
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null))
            {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }

        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context)
    {
        /*
         * Since we've created an account
         */
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_provider_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context)
    {
        Log.d(LOG_TAG, "initializeSyncAdapter");

        getSyncAccount(context);

        syncImmediately(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult)
    {
        Log.d(LOG_TAG, "Starting sync");

        getContext().getContentResolver().delete(MovieContract.TrailerEntry.CONTENT_URI, null, null);

        getContext().getContentResolver().delete(MovieContract.ReviewEntry.CONTENT_URI, null, null);

        String sortOrder = Utility.getPreferredSortOrder(getContext());

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getContext().getString(R.string.base_api_url))
                .build();

        TheMovieDatabaseService movieService = restAdapter.create(TheMovieDatabaseService.class);

        movieService.discoverTopMovies(getContext().getString(R.string.api_key), sortOrder, new Callback<Movies>()
        {
            @Override
            public void success(Movies movies, Response response)
            {
                Log.d(LOG_TAG, "onPreformSync Movie Size: " + movies.movies.size());

                getMovieData(movies.movies);
            }

            @Override
            public void failure(RetrofitError error)
            {
                Log.d(LOG_TAG, "Error: " + error);
            }
        });
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p/>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getMovieData(List<Movie> movies)
    {
        for (Movie movie : movies)
        {
            // Insert the new movie information into the database
            final Vector<ContentValues> trailerVector = new Vector<>(movies.size());

            final Vector<ContentValues> reviewVector = new Vector<>(movies.size());

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(getContext().getString(R.string.base_api_url))
                    .build();

            TheMovieDatabaseService movieService = restAdapter.create(TheMovieDatabaseService.class);

            movieService.getMovie(movie.id, getContext().getString(R.string.api_key), "trailers,reviews", new Callback<Movie>()
            {
                @Override
                public void success(Movie movie, Response response)
                {
                    ContentValues movieValues = new ContentValues();

                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.id);

                    movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.title);

                    Log.e("TEST", "getMovieData Movie Title: " + movie.title);

                    movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.releaseDate);

                    movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.voteAverage);

                    movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, movie.voteCount);

                    movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.overview);

                    movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.posterPath);

                    movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, movie.popularity);

                    movieValues.put(MovieContract.MovieEntry.COLUMN_RUNTIME, movie.runtime);

                    getContext().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieValues);

                    for (YouTubeTrailer trailer : movie.trailers.youTubTrailers)
                    {
                        // Trailer
                        ContentValues trailerValues = new ContentValues();

                        trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movie.id);

                        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME, trailer.trailerName);

                        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_SIZE, trailer.trailerSize);

                        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_SOURCE_ID, trailer.trailerSource);

                        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_TYPE, trailer.trailerType);

                        trailerVector.add(trailerValues);
                    }

                    if (trailerVector.size() > 0)
                    {
                        Log.e(LOG_TAG, trailerVector.size() + "");

                        ContentValues[] cvArray = new ContentValues[trailerVector.size()];

                        trailerVector.toArray(cvArray);

                        int rowsCreated = getContext().getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, cvArray);

                        Log.d(LOG_TAG, "Trailer Created: " + rowsCreated);
                    }

                    if (movie.reviews != null && movie.reviews.reviews != null)
                    {
                        for (Review review : movie.reviews.reviews)
                        {
                            // Trailer
                            ContentValues reviewValues = new ContentValues();

                            reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movie.id);

                            reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, review.author);

                            reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT, review.content);

                            reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, review.id);

                            reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_URL, review.url);

                            reviewVector.add(reviewValues);
                        }

                        if (reviewVector.size() > 0)
                        {
                            Log.e(LOG_TAG, reviewVector.size() + "");

                            ContentValues[] cvArray = new ContentValues[reviewVector.size()];

                            reviewVector.toArray(cvArray);

                            int rowsCreated = getContext().getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, cvArray);

                            Log.d(LOG_TAG, "Review Created: " + rowsCreated);
                        }
                    }
                }

                @Override
                public void failure(RetrofitError error)
                {
                    Log.d(LOG_TAG, "Error: " + error);
                }
            });
        }
    }
}

