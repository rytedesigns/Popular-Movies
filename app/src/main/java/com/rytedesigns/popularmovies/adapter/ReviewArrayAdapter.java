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

public class ReviewArrayAdapter extends CursorAdapter
{
    private static final String LOG_TAG = MovieImageAdapter.class.getSimpleName();

    public ReviewArrayAdapter(Context context, Cursor c, int flags)
    {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        View rootView = LayoutInflater.from(context).inflate(R.layout.list_item_review, parent, false);

        ViewHolder viewHolder = new ViewHolder(rootView);

        rootView.setTag(viewHolder);

        return rootView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        Log.d(LOG_TAG, "Review Sync Finished now display.");

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        Log.d(LOG_TAG, "cursor size: " + cursor.getCount());

        if (cursor.getCount() > 0) {
            String reviewAuthor = cursor.getString(MovieDetailsFragment.REVIEW_COL_REVIEW_AUTHOR);

            String reviewContent = cursor.getString(MovieDetailsFragment.REVIEW_COL_REVIEW_CONTENT);

            if (reviewAuthor != null) {
                viewHolder.mReviewAuthorTextView.setText(reviewAuthor);
            }

            if (reviewContent != null) {
                viewHolder.mReviewContentTextView.setText(reviewContent);
            }
        }
    }

    /**
     * Cache the poster for the grid.
     */
    public static class ViewHolder {
        @InjectView(R.id.reviewAuthorTextView)
        public TextView mReviewAuthorTextView;

        @InjectView(R.id.reviewContentTextView)
        public TextView mReviewContentTextView;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
