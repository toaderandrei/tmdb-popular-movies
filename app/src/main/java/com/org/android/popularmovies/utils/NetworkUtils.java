package com.org.android.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.org.android.popularmovies.application.PopularMoviesApp;

/**
 * Network utility class.
 */
public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getCanonicalName();

    public static boolean hasConnectivity() {
        final ConnectivityManager cm =
                (ConnectivityManager) PopularMoviesApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
            Log.e(TAG, "checkConnection - no connection found");
            return false;
        }
        return true;
    }
}
