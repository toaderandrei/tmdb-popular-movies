package com.org.android.popularmovies.activities;

import com.org.android.popularmovies.model.MovieItem;

/**
 * Created by andrei on 11/28/15.
 */
public interface BaseActivityCallback {
    void onError();

    void onSuccess();

    /**
     * callback for when a click on the movie has occurred.
     */
    void onMovieClickedCallback(final MovieItem item);

    /**
     * clear the image caches.
     */
    void clearCaches();

}
