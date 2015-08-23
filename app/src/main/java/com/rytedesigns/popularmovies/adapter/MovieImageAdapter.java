package com.rytedesigns.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.rytedesigns.popularmovies.R;
import com.rytedesigns.popularmovies.fragment.MainFragment;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * {@link MovieImageAdapter} exposes a grid of movies
 * from a {@link Cursor} to a {@link android.widget.GridView}.
 */
public class MovieImageAdapter extends CursorAdapter {
    private static final String LOG_TAG = MovieImageAdapter.class.getSimpleName();

    private static String baseImageUrl;


    public MovieImageAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);

        baseImageUrl = context.getString(R.string.base_poster_url);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.grid_item_movie, parent, false);

        ViewHolder viewHolder = new ViewHolder(rootView);

        rootView.setTag(viewHolder);

        return rootView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.d(LOG_TAG, "Sync Finished now display.");

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        Log.d(LOG_TAG, "cursor size: " + cursor.getCount());

        if (cursor.getCount() > 0) {
            String posterPath = cursor.getString(MainFragment.COL_POSTER_PATH);

            int id = cursor.getInt(MainFragment.COL_ID);

            int movieId = cursor.getInt(MainFragment.COL_MOVIE_ID);

            Log.d(LOG_TAG, "Cursor ID: " + id + "\nMovie ID: " + movieId);

            if (cursor.getInt(MainFragment.COL_FAVORITE) == 1) {
                viewHolder.favoriteImageView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.favoriteImageView.setVisibility(View.GONE);
            }

            Log.d(LOG_TAG, baseImageUrl + posterPath);

            if (posterPath != null && posterPath.length() > 0) {
                Log.d(LOG_TAG, "loading image");

                Picasso.with(context)
                        .load(baseImageUrl + posterPath)
                        .placeholder(R.drawable.movie_poster_placeholder)
                        .into(viewHolder.moviePosterImageView);
            } else {
                Picasso.with(context)
                        .load(R.drawable.movie_poster_placeholder)
                        .placeholder(R.drawable.movie_poster_placeholder)
                        .into(viewHolder.moviePosterImageView);
            }
        } else {
            Picasso.with(context)
                    .load(R.drawable.movie_poster_placeholder)
                    .placeholder(R.drawable.movie_poster_placeholder)
                    .into(viewHolder.moviePosterImageView);
        }
    }

    /**
     * Cache the poster for the grid.
     */
    public static class ViewHolder {
        @InjectView(R.id.movieImageView)
        public ImageView moviePosterImageView;

        @InjectView(R.id.favoriteImageView)
        public ImageView favoriteImageView;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
