package com.rytedesigns.popularmovies.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.rytedesigns.popularmovies.R;
import com.rytedesigns.popularmovies.Utility;
import com.rytedesigns.popularmovies.fragment.MainFragment;
import com.rytedesigns.popularmovies.fragment.MovieDetailsFragment;
import com.rytedesigns.popularmovies.sync.MovieSyncAdapter;

public class MainActivity extends AppCompatActivity implements MainFragment.Callbacks {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String MOVIE_DETAIL_FRAGMENT_TAG = "MDFTAG";

    private boolean mTwoPane;

    private String mSortOrder;

    private boolean mShowFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mSortOrder = Utility.getPreferredSortOrder(this);

        mShowFavorites = Utility.getPreferredDisplayFavorites(this);

        if (findViewById(R.id.movie_details_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_details_container, new MovieDetailsFragment(), MOVIE_DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

        MovieSyncAdapter.syncImmediately(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String sortOrder = Utility.getPreferredSortOrder(this);

        boolean showFavorites = Utility.getPreferredDisplayFavorites(this);

        // update the location in our second pane using the fragment manager
        if ((sortOrder != null && !sortOrder.equals(mSortOrder)) || showFavorites != mShowFavorites) {
            MainFragment discoverMovieFragment = (MainFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_movies);

            if (null != discoverMovieFragment) {
                discoverMovieFragment.onDisplayPreferenceChanged();
            }

            mSortOrder = sortOrder;

            mShowFavorites = showFavorites;
        }
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();

            args.putParcelable(MovieDetailsFragment.MOVIE_URI_KEY, contentUri);

            MovieDetailsFragment fragment = new MovieDetailsFragment();

            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_details_container, fragment, MOVIE_DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailsActivity.class).setData(contentUri);

            startActivity(intent);
        }
    }
}
