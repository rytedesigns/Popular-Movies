package com.rytedesigns.popularmovies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.rytedesigns.popularmovies.R;
import com.rytedesigns.popularmovies.fragment.MovieDetailsFragment;

public class MovieDetailsActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_details);// Show the Up button in the action bar.

        if (savedInstanceState == null)
        {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();

            arguments.putParcelable(MovieDetailsFragment.MOVIE_URI_KEY, getIntent().getData());

            Log.e("TEST", getIntent().getData() + "");

            MovieDetailsFragment fragment = new MovieDetailsFragment();

            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_details_container, fragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.clear();

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_details, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:

                startActivity(new Intent(this, SettingsActivity.class));

                return true;

            default:

                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
