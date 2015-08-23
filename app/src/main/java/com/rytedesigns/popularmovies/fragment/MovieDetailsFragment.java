package com.rytedesigns.popularmovies.fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rytedesigns.popularmovies.R;
import com.rytedesigns.popularmovies.adapter.ReviewArrayAdapter;
import com.rytedesigns.popularmovies.adapter.TrailerArrayAdapter;
import com.rytedesigns.popularmovies.data.MovieContract.MovieEntry;
import com.rytedesigns.popularmovies.data.MovieContract.ReviewEntry;
import com.rytedesigns.popularmovies.data.MovieContract.TrailerEntry;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String MOVIE_URI_KEY = "movie_uri_key";
    // These indices are tied to MOVIE_DETAIL_COLUMNS.  If MOVIE_DETAIL_COLUMNS changes, these must change.
    public static final int MOVIE_COL_ID = 0;
    public static final int MOVIE_COL_MOVIE_ID = 1;
    public static final int MOVIE_COL_MOVIE_TITLE = 2;
    public static final int MOVIE_COL_RELEASE_DATE = 3;
    public static final int MOVIE_COL_VOTE_AVERAGE = 4;
    public static final int MOVIE_COL_VOTE_COUNT = 5;
    public static final int MOVIE_COL_OVERVIEW = 6;
    public static final int MOVIE_COL_POSTER_PATH = 7;
    public static final int MOVIE_COL_POPULARITY = 8;
    public static final int MOVIE_COL_RUNTIME = 9;
    public static final int MOVIE_COL_FAVORITE = 10;
    // These indices are tied to MOVIE_TRAILER_COLUMNS.  If MOVIE_TRAILER_COLUMNS changes, these must change.
    public static final int TRAILER_COL_ID = 0;
    public static final int TRAILER_COL_MOVIE_ID = 1;
    public static final int TRAILER_COL_SOURCE_ID = 2;
    public static final int TRAILER_COL_TRAILER_NAME = 3;
    public static final int TRAILER_COL_TRAILER_SIZE = 4;
    public static final int TRAILER_COL_TRAILER_TYPE = 5;
    // These indices are tied to MOVIE_REVIEW_COLUMNS.  If MOVIE_REVIEW_COLUMNS changes, these must change.
    public static final int REVIEW_COL_ID = 0;
    public static final int REVIEW_COL_MOVIE_ID = 1;
    public static final int REVIEW_COL_REVIEW_AUTHOR = 2;
    public static final int REVIEW_COL_REVIEW_CONTENT = 3;
    public static final int REVIEW_COL_REVIEW_ID = 4;
    public static final int REVIEW_COL_REVIEW_URL = 5;
    private static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();
    private static final String TRAILER_SHARE_HASHTAG = " #PopularMovie";
    private static final int MOVIE_DETAIL_LOADER = 0;
    private static final int TRAILER_LOADER = 1;
    private static final int REVIEW_LOADER = 2;

    private static final String[] MOVIE_DETAIL_COLUMNS = {
            MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
            MovieEntry.COLUMN_MOVIE_ID,
            MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieEntry.COLUMN_VOTE_COUNT,
            MovieEntry.COLUMN_OVERVIEW,
            MovieEntry.COLUMN_POSTER_PATH,
            MovieEntry.COLUMN_POPULARITY,
            MovieEntry.COLUMN_RUNTIME,
            MovieEntry.COLUMN_FAVORITE
    };

    private static final String[] MOVIE_TRAILER_COLUMNS = {
            TrailerEntry.TABLE_NAME + "." + TrailerEntry._ID,
            TrailerEntry.COLUMN_MOVIE_ID,
            TrailerEntry.COLUMN_TRAILER_SOURCE_ID,
            TrailerEntry.COLUMN_TRAILER_NAME,
            TrailerEntry.COLUMN_TRAILER_SIZE,
            TrailerEntry.COLUMN_TRAILER_TYPE
    };

    private static final String[] MOVIE_REVIEW_COLUMNS = {
            ReviewEntry.TABLE_NAME + "." + ReviewEntry._ID,
            ReviewEntry.COLUMN_MOVIE_ID,
            ReviewEntry.COLUMN_REVIEW_AUTHOR,
            ReviewEntry.COLUMN_REVIEW_CONTENT,
            ReviewEntry.COLUMN_REVIEW_ID,
            ReviewEntry.COLUMN_REVIEW_URL
    };

    @InjectView(R.id.titleTextView)
    public TextView mTitleTextView;

    @InjectView(R.id.posterImageView)
    public ImageView mPosterImageView;

    @InjectView(R.id.releaseDateTextView)
    public TextView mReleaseDateTextView;

    @InjectView(R.id.runtimeTimeView)
    public TextView mRuntimeTextView;

    @InjectView(R.id.ratingTextView)
    public TextView mRatingTextView;
    
    @InjectView(R.id.favoriteImageView)
    public ImageView mFavoriteImageView;

    @InjectView(R.id.overviewTextView)
    public TextView mOverviewTextView;

    private ShareActionProvider mShareActionProvider;

    private TrailerArrayAdapter mTrailerAdapter;

    private ReviewArrayAdapter mReviewAdapter;

    private Uri mUri;

    private int movieId;

    private boolean isFavorite;

    public MovieDetailsFragment() {
        movieId = -1;

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Bundle arguments = getArguments();

        if (arguments != null)
        {
            mUri = arguments.getParcelable(MovieDetailsFragment.MOVIE_URI_KEY);
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);

        ButterKnife.inject(this, rootView);

        // The TrailerArrayAdapter will take data from a source and use it to populate the ListView it's attached to.
        mTrailerAdapter = new TrailerArrayAdapter(getActivity(), null, 0);

        // The ReviewArrayAdapter will take data from a source and use it to populate the ListView it's attached to.
        mReviewAdapter = new ReviewArrayAdapter(getActivity(), null, 0);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_movie_details, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mShareActionProvider != null && movieId != -1)
        {
            mShareActionProvider.setShareIntent(createShareMovieIntent());
        }
    }

    private Intent createShareMovieIntent()
    {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        shareIntent.addFlags(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? Intent.FLAG_ACTIVITY_NEW_DOCUMENT : Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        shareIntent.setType("text/plain");

        if (mTrailerAdapter != null && mTrailerAdapter.getCursor() != null) {
            String youtubeId = ((Cursor) mTrailerAdapter.getItem(0)).getString(TRAILER_COL_SOURCE_ID);

            shareIntent.putExtra(Intent.EXTRA_TEXT, "http://www.youtube.com/watch?v=" + youtubeId);

            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.youtube_url) + youtubeId + "\n" + TRAILER_SHARE_HASHTAG);

            return shareIntent;
        }

        return null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e(LOG_TAG, "Creating Loader using uri " + mUri);

        CursorLoader cursorLoader = null;

        if (null != mUri) {
            switch (id) {
                case MOVIE_DETAIL_LOADER: {
                    Log.e(LOG_TAG, "Creating Loader for movie detail loader using uri " + mUri);
                    // Now create and return a CursorLoader that will take care of
                    // creating a Cursor for the data being displayed.
                    cursorLoader = new CursorLoader(
                            getActivity(),
                            mUri,
                            MOVIE_DETAIL_COLUMNS,
                            null,
                            null,
                            null
                    );

                    break;
                }

                case TRAILER_LOADER: {
                    if (movieId != -1) {
                        Uri trailerUri = TrailerEntry.CONTENT_URI;

                        Log.e(LOG_TAG, "Creating Loader for trailer loader using uri " + trailerUri);

                        // Now create and return a CursorLoader that will take care of
                        // creating a Cursor for the data being displayed.
                        cursorLoader = new CursorLoader(
                                getActivity(),
                                trailerUri,
                                MOVIE_TRAILER_COLUMNS,
                                TrailerEntry.COLUMN_MOVIE_ID + " = ?",
                                new String[]{Long.toString(movieId)},
                                null
                        );
                    }

                    break;
                }

                case REVIEW_LOADER: {
                    if (movieId != -1) {
                        Uri reviewUri = ReviewEntry.CONTENT_URI;

                        Log.e(LOG_TAG, "Creating Loader for review loader using uri " + reviewUri);

                        // Now create and return a CursorLoader that will take care of
                        // creating a Cursor for the data being displayed.
                        cursorLoader = new CursorLoader(
                                getActivity(),
                                reviewUri,
                                MOVIE_REVIEW_COLUMNS,
                                TrailerEntry.COLUMN_MOVIE_ID + " = ?",
                                new String[]{Long.toString(movieId)},
                                null
                        );
                    }

                    break;
                }
            }
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e(LOG_TAG, "Checking returned cursor. Cursor size is " + data.getCount());

        switch (loader.getId()) {
            case MOVIE_DETAIL_LOADER: {

                Log.e(LOG_TAG, "MOVIE_DETAIL_LOADER will be handled");

                Log.e(LOG_TAG, "data: " + data.getCount());

                handleMovieLoader(loader, data);

                break;
            }

            case TRAILER_LOADER: {

                Log.e(LOG_TAG, "Trailer_Loader will be handled");

                Log.e(LOG_TAG, "data: " + data.getCount());

                handleTrailerLoader(loader, data);

                break;
            }

            case REVIEW_LOADER: {

                Log.e(LOG_TAG, "REVIEW_LOADER will be handled");

                Log.e(LOG_TAG, "data: " + data.getCount());

                handleReviewLoader(loader, data);

                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(LOG_TAG, "Loader Reset.");

        switch (loader.getId()) {
            case TRAILER_LOADER: {
                mTrailerAdapter.swapCursor(null);

                break;
            }

            case REVIEW_LOADER: {
                mReviewAdapter.swapCursor(null);

                break;
            }
        }
    }

    private void handleMovieLoader(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            // Read weather condition ID from cursor
            String posterPath = getString(R.string.base_poster_url) + data.getString(MOVIE_COL_POSTER_PATH);

            movieId = data.getInt(MOVIE_COL_MOVIE_ID);

            if (null != posterPath && mPosterImageView != null) {
                Picasso.with(getActivity())
                        .load(posterPath)
                        .placeholder(R.drawable.movie_poster_placeholder)
                        .error(R.drawable.movie_poster_placeholder)
                        .into(mPosterImageView);
            }

            mReleaseDateTextView.setText(data.getString(MOVIE_COL_RELEASE_DATE));

            mRuntimeTextView.setText(data.getString(MOVIE_COL_RUNTIME) + "min");

            mRatingTextView.setText(data.getString(MOVIE_COL_VOTE_AVERAGE) + " / 10");

            mOverviewTextView.setText(data.getString(MOVIE_COL_OVERVIEW));

            mTitleTextView.setText(data.getString(MOVIE_COL_MOVIE_TITLE));

            isFavorite = data.getInt(MOVIE_COL_FAVORITE) == 1;

            mFavoriteImageView.setVisibility(isFavorite ? View.VISIBLE : View.GONE);

            Log.e(LOG_TAG, "Is Favorite: " + isFavorite);

            // Get the Trailers for this movie
            getLoaderManager().initLoader(TRAILER_LOADER, null, this);

            // Get the Reviews for this movie
            getLoaderManager().initLoader(REVIEW_LOADER, null, this);

            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareMovieIntent());
            }
        }
    }

    private void handleTrailerLoader(Loader<Cursor> loader, Cursor data) {
        Log.e(LOG_TAG, "handleTrailerLoader");
        mTrailerAdapter.swapCursor(data);
    }

    private void handleReviewLoader(Loader<Cursor> loader, Cursor data) {
        Log.e(LOG_TAG, "handleReviewLoader");
        mReviewAdapter.swapCursor(data);
    }

    @OnClick(R.id.favoriteMovieButton)
    public void onClick(View view) {
        if (!isFavorite) {
            isFavorite = true;

            mFavoriteImageView.setVisibility(View.VISIBLE);
        } else {
            isFavorite = false;

            mFavoriteImageView.setVisibility(View.GONE);
        }

        ContentValues values = new ContentValues();

        values.put(MovieEntry.COLUMN_FAVORITE, isFavorite ? 1 : 0);

        ContentResolver resolver = getActivity().getContentResolver();

        int value = resolver.update(MovieEntry.CONTENT_URI, values, MovieEntry.COLUMN_MOVIE_ID + "=?", new String[]{String.valueOf(movieId)});

        Log.e(LOG_TAG, "Value of the update: " + value);
    }

    @OnItemClick(R.id.trailerListView)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String youtubeId = ((Cursor) mTrailerAdapter.getItem(position)).getString(TRAILER_COL_SOURCE_ID);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + youtubeId));

        startActivity(intent);
    }
}