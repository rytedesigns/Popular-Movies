package com.rytedesigns.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ryan on 8/21/2015.
 */
public class Utility {

    public static boolean getPreferredDisplayFavorites(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (preferences != null) {
            return preferences.getBoolean(context.getString(R.string.pref_display_favorites_key), false);
        }

        return false;
    }

    public static String getPreferredSortOrder(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (preferences != null) {
            return preferences.getString(context.getString(R.string.pref_sort_order_key), context.getString(R.string.pref_sort_order_default));
        }

        return context.getString(R.string.pref_sort_order_default);
    }
}
