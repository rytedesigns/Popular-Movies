package com.rytedesigns.popularmovies.fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rytedesigns.popularmovies.R;
import com.rytedesigns.popularmovies.Utility;
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
public class MovieDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
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
    private static final int MOVIE_DETAIL_LOADER = 1;
    private static final int TRAILER_LOADER = 2;
    private static final int REVIEW_LOADER = 3;

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

    @InjectView(R.id.trailerListView)
    public ListView mTrailerListView;

    @InjectView(R.id.reviewListView)
    public ListView mReviewListView;

    private ShareActionProvider mShareActionProvider;

    private TrailerArrayAdapter mTrailerAdapter;

    private ReviewArrayAdapter mReviewAdapter;

    private Uri mUri;

    private int movieId;

    private boolean isFavorite;

    public MovieDetailsFragment()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        movieId = -1;

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.e("TEST METHODS", "onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)");

        Bundle arguments = getArguments();

        if (arguments != null)
        {
            Log.e("TEST", "URI: " + arguments.getParcelable(MovieDetailsFragment.MOVIE_URI_KEY));

            mUri = arguments.getParcelable(MovieDetailsFragment.MOVIE_URI_KEY);
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);

        ButterKnife.inject(this, rootView);

        // The TrailerArrayAdapter will take data from a source and use it to populate the ListView it's attached to.
        mTrailerAdapter = new TrailerArrayAdapter(getActivity(), null, 0);

        mTrailerListView.setAdapter(mTrailerAdapter);

        // The ReviewArrayAdapter will take data from a source and use it to populate the ListView it's attached to.
        mReviewAdapter = new ReviewArrayAdapter(getActivity(), null, 0);

        mReviewListView.setAdapter(mReviewAdapter);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        Log.e("TEST METHODS", "onCreateOptionsMenu(Menu menu, MenuInflater inflater)");

        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_movie_details, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        Log.e("TEST METHODS - 1", "mShareActionProvider is " + (mShareActionProvider != null ? "not null" : "null"));

        mShareActionProvider = new ShareActionProvider(getActivity());

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (movieId > 0)
        {
            Log.e("TEST METHODS - 1", "movieId > -1 = true");

            mShareActionProvider.setShareIntent(createShareMovieIntent());

            MenuItemCompat.setActionProvider(menuItem, mShareActionProvider);
        }
    }

    private Intent createShareMovieIntent()
    {
        Log.e("TEST METHODS", "createShareMovieIntent()");

        Log.e("TEST", "createShareMovieIntent: " + mUri);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        shareIntent.setType("text/plain");

        if (mTrailerAdapter != null && mTrailerAdapter.getCursor() != null)
        {
            String youtubeId = ((Cursor) mTrailerAdapter.getItem(0)).getString(TRAILER_COL_SOURCE_ID);

            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.youtube_url) + youtubeId + "\n" + TRAILER_SHARE_HASHTAG);

            Log.e("TEST", "Share Intent Text: " + getString(R.string.youtube_url) + youtubeId + "\n" + TRAILER_SHARE_HASHTAG);

            Log.e("TEST", "createShareMovieIntent: " + shareIntent.getStringExtra(Intent.EXTRA_TEXT));

            return shareIntent;
        }

        return null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Log.e("TEST METHODS", "onActivityCreated(Bundle savedInstanceState)");

        Log.e("TEST", "onActivityCreated:" + mUri);

        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        Log.e("TEST METHODS", "onCreateLoader(int id, Bundle args)");

        Log.e("TEST", " mUri: " + mUri);

        CursorLoader cursorLoader = null;

        if (null != mUri)
        {
            switch (id)
            {
                case MOVIE_DETAIL_LOADER:
                {
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

                case TRAILER_LOADER:
                {
                    if (movieId != -1)
                    {
                        Uri trailerUri = TrailerEntry.buildTrailerWithId(movieId);

                        Log.e("TEST", "Creating Loader for trailer loader using uri " + trailerUri);

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

                case REVIEW_LOADER:
                {
                    if (movieId != -1)
                    {
                        Uri reviewUri = ReviewEntry.buildTrailerWithId(movieId);;

                        Log.e("TEST", "Creating Loader for review loader using uri " + reviewUri);

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

                default:
                {
                    Log.e("TEST", "Unable to create Loader using uri: " + mUri);

                    break;
                }
            }
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        Log.e("TEST METHODS", "onLoadFinished(Loader<Cursor> loader, Cursor data)");

        Log.e(LOG_TAG, "Checking returned cursor. Cursor size is " + data.getCount());

        switch (loader.getId())
        {
            case MOVIE_DETAIL_LOADER:
            {
                Log.e(LOG_TAG, "MOVIE_DETAIL_LOADER will be handled");

                Log.e("TEST", "data: " + data.getCount());

                handleMovieLoader(loader, data);

                break;
            }

            case TRAILER_LOADER: {

                Log.e(LOG_TAG, "Trailer_Loader will be handled");

                Log.e("TEST", "data: " + data.getCount());

                handleTrailerLoader(loader, data);

                break;
            }

            case REVIEW_LOADER: {

                Log.e(LOG_TAG, "REVIEW_LOADER will be handled");

                Log.e("TEST", "data: " + data.getCount());

                handleReviewLoader(loader, data);

                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e("TEST METHODS", "onLoaderReset(Loader<Cursor> loader)");
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

    private void handleMovieLoader(Loader<Cursor> loader, Cursor data)
    {
        if (data.moveToFirst())
        {
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

            mReleaseDateTextView.setText(Utility.getYearFromReleaseDate(data.getString(MOVIE_COL_RELEASE_DATE)));

            mRuntimeTextView.setText(data.getString(MOVIE_COL_RUNTIME) + "min");

            mRatingTextView.setText(data.getString(MOVIE_COL_VOTE_AVERAGE) + " / 10");

            mOverviewTextView.setText(data.getString(MOVIE_COL_OVERVIEW));

            mTitleTextView.setText(data.getString(MOVIE_COL_MOVIE_TITLE));

            isFavorite = data.getInt(MOVIE_COL_FAVORITE) == 1;

            mFavoriteImageView.setVisibility(isFavorite ? View.VISIBLE : View.GONE);

            // Get the Trailers for this movie
            getLoaderManager().initLoader(TRAILER_LOADER, null, this);

            // Get the Reviews for this movie
            getLoaderManager().initLoader(REVIEW_LOADER, null, this);

            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mShareActionProvider != null)
            {
                mShareActionProvider.setShareIntent(createShareMovieIntent());
            }
        }
    }

    private void handleTrailerLoader(Loader<Cursor> loader, Cursor data)
    {
        Log.d(LOG_TAG, "handleTrailerLoader");

        if (data.moveToFirst())
        {
            do {
                StringBuilder row = new StringBuilder();

                for (String columnName : data.getColumnNames())
                {
                    int columnIndex = data.getColumnIndex(columnName);

                    switch (data.getType(columnIndex))
                    {
                        case Cursor.FIELD_TYPE_INTEGER:
                            row.append(columnName).append(":").append(data.getInt(columnIndex)).append(" | ");
                            break;

                        case Cursor.FIELD_TYPE_STRING:
                            row.append(columnName).append(":").append(data.getString(columnIndex)).append(" | ");
                            break;
                    }
                }

                Log.d(LOG_TAG, row.toString());
            }
            while (data.moveToNext());
        }

        mTrailerAdapter.swapCursor(data);
    }

    private void handleReviewLoader(Loader<Cursor> loader, Cursor data)
    {
        Log.d(LOG_TAG, "handleReviewLoader");

        if (data.moveToFirst())
        {
            do {
                StringBuilder row = new StringBuilder();

                for (String columnName : data.getColumnNames())
                {
                    int columnIndex = data.getColumnIndex(columnName);

                    switch (data.getType(columnIndex))
                    {
                        case Cursor.FIELD_TYPE_INTEGER:
                            row.append(columnName).append(":").append(data.getInt(columnIndex)).append(" | ");
                            break;

                        case Cursor.FIELD_TYPE_STRING:
                            row.append(columnName).append(":").append(data.getString(columnIndex)).append(" | ");
                            break;
                    }
                }

                Log.d(LOG_TAG, row.toString());
            }
            while (data.moveToNext());
        }

        mReviewAdapter.swapCursor(data);
    }

    @OnClick(R.id.favoriteMovieButton)
    public void onClick(View view)
    {
        Log.e("TEST METHODS", "onClick(View view)");
        if (movieId != -1)
        {
            if (!isFavorite)
            {
                isFavorite = true;

                mFavoriteImageView.setVisibility(View.VISIBLE);
            }
            else
            {
                isFavorite = false;

                mFavoriteImageView.setVisibility(View.GONE);
            }

            ContentValues values = new ContentValues();

            values.put(MovieEntry.COLUMN_FAVORITE, isFavorite ? 1 : 0);

            ContentResolver resolver = getActivity().getContentResolver();

            int value = resolver.update(MovieEntry.CONTENT_URI, values, MovieEntry.COLUMN_MOVIE_ID + "=?", new String[]{String.valueOf(movieId)});

            Log.d(LOG_TAG, "Value of the update: " + value);
        }
    }

    @OnItemClick(R.id.trailerListView)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Log.e("TEST METHODS", "onItemClick(AdapterView<?> parent, View view, int position, long id)");
        String youtubeId = ((Cursor) mTrailerAdapter.getItem(position)).getString(TRAILER_COL_SOURCE_ID);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.youtube_url) + youtubeId));

        startActivity(intent);
    }
}