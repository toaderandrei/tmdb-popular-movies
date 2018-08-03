package com.org.android.popularmovies.application;

import android.app.Application;
import android.os.Handler;
import android.os.HandlerThread;

import com.org.android.popularmovies.controller.DataController;
import com.org.android.popularmovies.httpclient.HttpClient;
import com.org.android.popularmovies.httpclient.IHttpClient;
import com.org.android.popularmovies.synchronization.impl.thread.SyncManagerImpl;
import com.org.android.popularmovies.synchronization.interfaces.SyncManager;
import com.org.android.popularmovies.utils.LogUtils;

/**
 * Wrapper for the application. It retrieves via singleton the application's instance.
 */
public class PopularMoviesApp extends Application {

    private static PopularMoviesApp instance = null;

    private HandlerThread handlerThread = null;
    private Handler handler = null;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.setLogLevel(3);
        PopularMoviesApp.instance = this;
    }

    public PopularMoviesApp() {
        handlerThread = new HandlerThread("AsyncThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        getSyncManager().startThread();
    }

    public void runAsync(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        handler.post(runnable);
    }

    public static PopularMoviesApp getInstance() {
        return instance;
    }

    public SyncManager getSyncManager() {
        return SyncManagerImpl.getInstance();
    }

    public IHttpClient getHttpClient() {
        return HttpClient.getInstance();
    }

    public DataController getDataController() {
        return DataController.getInstance();
    }
}
