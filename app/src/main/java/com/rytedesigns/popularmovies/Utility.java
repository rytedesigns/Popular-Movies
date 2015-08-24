package com.rytedesigns.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Utility
{
    public static boolean getPreferredDisplayFavorites(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        return preferences != null && preferences.getBoolean(context.getString(R.string.pref_display_favorites_key), false);

    }

    public static String getPreferredSortOrder(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (preferences != null)
        {
            return preferences.getString(context.getString(R.string.pref_sort_order_key), context.getString(R.string.pref_sort_order_default));
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
