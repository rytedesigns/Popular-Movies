package com.rytedesigns.popularmovies.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.rytedesigns.popularmovies.R;
import com.rytedesigns.popularmovies.Utility;
import com.rytedesigns.popularmovies.adapter.MovieImageAdapter;
import com.rytedesigns.popularmovies.data.MovieContract.MovieEntry;
import com.rytedesigns.popularmovies.sync.MovieSyncAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    public static final int COL_MOVIE_ID = 0;

    public static final int COL_POSTER_PATH = 1;

    public static final int COL_TITLE = 2;

    public static final int COL_FAVORITE = 3;

    private static final String[] MOVIE_COLUMNS = {
            MovieEntry.TABLE_NAME + "." + MovieEntry.COLUMN_MOVIE_ID,
            MovieEntry.COLUMN_POSTER_PATH,
            MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_FAVORITE
    };

    private static final String SELECTED_KEY = "selected_position";

    private static final int MOVIES_LOADER = 0;

    @InjectView(R.id.movieGridView)
    public GridView mMovieGridView;

    private int mPosition = GridView.INVALID_POSITION;

    private MovieImageAdapter mMovieImageAdapter;

    public MainFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(LOG_TAG, "onCreateView");

        // The MovieImageAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mMovieImageAdapter = new MovieImageAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.inject(this, rootView);

        // Get a reference to the ListView, and attach this adapter to it.
        mMovieGridView.setAdapter(mMovieImageAdapter);


        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY))
        {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Log.e(LOG_TAG, "MADE IT HERE IN onActivityCreated");

        getLoaderManager().initLoader(MOVIES_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    // since we read the location when we create the loader, all we need to do is restart things
    public void onDisplayPreferenceChanged()
    {
        updateMovies();

        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
    }

    private void updateMovies()
    {
        MovieSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != GridView.INVALID_POSITION)
        {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        // Sort order:
        String sortOrder = Utility.getPreferredSortOrder(getActivity()).replace(".", " ");

        String filter = null;

        String[] filterArgs = null;

        if (Utility.getPreferredDisplayFavorites(getActivity()))
        {
            filter = MovieEntry.COLUMN_FAVORITE + " = ?";

            filterArgs = new String[]{Integer.toString(1)};
        }

        Uri movieUri = MovieEntry.CONTENT_URI;

        Log.d(LOG_TAG, "onCreateLoader. Now starting to load data from CursorLoader");

        return new CursorLoader(getActivity(),
                                movieUri,
                                MOVIE_COLUMNS,
                                filter,
                                filterArgs,
                                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        Log.d(LOG_TAG, "Sync Complete.");

        mMovieImageAdapter.swapCursor(data);

        if (mPosition != GridView.INVALID_POSITION)
        {
            mMovieGridView.smoothScrollToPosition(mPosition);
        }
        else
        {
            mPosition = GridView.SCROLLBAR_POSITION_DEFAULT;

            mMovieGridView.post(new Runnable()
            {
                @Override
                public void run()
                {
                    mMovieGridView.performItemClick(
                            mMovieGridView.getAdapter().getView(mPosition, null, null),
                            mPosition,
                            mMovieGridView.getAdapter().getItemId(mPosition));
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        Log.d(LOG_TAG, "Loader Reset.");

        mMovieImageAdapter.swapCursor(null);
    }

    @OnItemClick(R.id.movieGridView)
    public void onItemClick(int position)
    {
        Log.e(LOG_TAG, "MADE IT HERE IN onItemClick");
        // CursorAdapter returns a cursor at the correct position for getItem(), or null
        // if it cannot seek to that position.
        Cursor cursor = (Cursor) mMovieImageAdapter.getItem(position);

        if (cursor != null)
        {
            ((Callbacks) getActivity())
                    .onItemSelected(MovieEntry.buildMovieWithId(cursor.getLong(COL_MOVIE_ID)));
        }

        mPosition = position;
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks
    {
        /**
         * Callback for when an item has been selected.
         */
        void onItemSelected(Uri contentUri);
    }
}
