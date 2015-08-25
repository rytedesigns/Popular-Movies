package com.rytedesigns.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utility
{
    public static boolean getPreferredDisplayFavorites(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (preferences != null)
        {
            boolean hasFavorite = preferences.getBoolean(context.getString(R.string.pref_display_favorites_key), false);

            if (!preferences.contains(context.getString(R.string.pref_sort_order_key)))
            {
                preferences.edit().putBoolean(context.getString(R.string.pref_display_favorites_key), false).apply();

                hasFavorite = preferences.getBoolean(context.getString(R.string.pref_display_favorites_key), false);
            }

            return hasFavorite;
        }

        return false;
    }

    public static String getPreferredSortOrder(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (preferences != null)
        {
            String sortOrder = preferences.getString(context.getString(R.string.pref_sort_order_key), context.getString(R.string.pref_sort_order_default));

            if (sortOrder.equals("-1"))
            {
                preferences.edit().putString(context.getString(R.string.pref_sort_order_key), context.getString(R.string.pref_sort_order_default)).apply();

                sortOrder = preferences.getString(context.getString(R.string.pref_sort_order_key), context.getString(R.string.pref_sort_order_default));
            }

            return sortOrder;
        }

        return context.getString(R.string.pref_sort_order_default);
    }

    public static String getYearFromReleaseDate(String releaseDate)
    {
        String[] date = releaseDate.split("-");

        if (date.length > 0)
        {
            return date[0];
        }

        return "";
    }
}
