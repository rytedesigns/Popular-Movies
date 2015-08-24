package com.rytedesigns.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.rytedesigns.popularmovies.R;
import com.rytedesigns.popularmovies.fragment.MovieDetailsFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TrailerArrayAdapter extends CursorAdapter
{
    private static final String LOG_TAG = MovieImageAdapter.class.getSimpleName();

    public TrailerArrayAdapter(Context context, Cursor c, int flags)
    {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        View rootView = LayoutInflater.from(context).inflate(R.layout.list_item_trailer, parent, false);

        ViewHolder viewHolder = new ViewHolder(rootView);

        rootView.setTag(viewHolder);

        return rootView;
    }

    @Override
    public int getCount()
    {
        if (getCursor() != null)
        {
            return getCursor().getCount();
        }

        return 0;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        Log.d(LOG_TAG, "Trailer Sync Finished now display.");

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        if (cursor.getCount() > 0)
        {
            String trailerName = cursor.getString(MovieDetailsFragment.TRAILER_COL_TRAILER_NAME);

            if (trailerName != null)
            {
                viewHolder.mTrailerNameTextView.setText(trailerName);
            }
        }
    }

    /**
     * Cache the poster for the grid.
     */
    public static class ViewHolder
    {
        @InjectView(R.id.trailerNameTextView)
        public TextView mTrailerNameTextView;

        public ViewHolder(View view)
        {
            ButterKnife.inject(this, view);
        }
    }
}
