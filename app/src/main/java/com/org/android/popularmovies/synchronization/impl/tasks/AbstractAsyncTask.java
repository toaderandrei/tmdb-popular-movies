package com.org.android.popularmovies.synchronization.impl.tasks;

import android.database.SQLException;

import com.org.android.popularmovies.application.PopularMoviesApp;
import com.org.android.popularmovies.controller.DataController;
import com.org.android.popularmovies.httpclient.IHttpClient;

/**
 * Abstract methods that describe the synchronous tasks.
 * */
public abstract class AbstractAsyncTask {


    protected IHttpClient getHttpClient() {
        return PopularMoviesApp.getInstance().getHttpClient();
    }

    protected DataController getDataController() {
        return PopularMoviesApp.getInstance().getDataController();
    }

    public abstract void synchronize() throws Exception;

    public abstract int getAction();

}
