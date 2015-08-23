package com.rytedesigns.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MovieSyncService extends Service {
    private static final String LOG_TAG = MovieSyncService.class.getSimpleName();

    private static final Object mSyncAdapterLock = new Object();

    private static MovieSyncAdapter mMovieSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate - MovieSyncService");

        synchronized (mSyncAdapterLock) {
            if (mMovieSyncAdapter == null) {
                mMovieSyncAdapter = new MovieSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMovieSyncAdapter.getSyncAdapterBinder();
    }
}

