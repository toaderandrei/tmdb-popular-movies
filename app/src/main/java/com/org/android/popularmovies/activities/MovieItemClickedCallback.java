package com.org.android.popularmovies.activities;

import com.org.android.popularmovies.model.MovieItem;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;

/**
 * Interface that defines the updating of the songs.
 */
public interface MovieItemClickedCallback {
    /**
     * method call for when a click on the poster occurred.
     *
     * @param item the item to which the poster belongs.
     * @param pos  position of the item.
     */
    void onMovieItemClicked(MovieItem item, int pos);

    /**
     * click listener for when the favored button is clicked.
     *
     * @param item     the item that is clicked
     * @param callback the callback.
     */
    void onFavoredMovieItemClicked(MovieItem item, SyncCallback<MovieItem> callback);

    void loadMovieFavoriteStatusIfExistsInDb(MovieItem item, SyncCallback<MovieItem> mCallback);
}
