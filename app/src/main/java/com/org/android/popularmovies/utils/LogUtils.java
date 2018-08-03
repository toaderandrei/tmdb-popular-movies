package com.org.android.popularmovies.utils;

import android.util.Log;

/**
 * Created by toaderandrei on 16/10/15.
 */
public class LogUtils {

    private static int logLevel = 6;

    private static final String TAG = LogUtils.class.getCanonicalName();

    public static void setLogLevel(int _logLevel) {
        logLevel = _logLevel;
    }

    public static void debug(String message) {
        if (logLevel <= Log.DEBUG) {
            Log.d(TAG, message);
        }
    }

    public static void info(String message) {
        if (logLevel <= Log.INFO) {
            Log.d(TAG, message);
        }

    }

    public static void exception(String message) {
        if (logLevel <= Log.ERROR) {
            Log.d(TAG, message);
        }

    }
}
