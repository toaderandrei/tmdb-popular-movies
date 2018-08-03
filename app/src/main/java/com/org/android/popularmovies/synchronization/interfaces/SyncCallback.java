package com.org.android.popularmovies.synchronization.interfaces;

/**
 * Callback interface that is called at the end of the request.
 */
public interface SyncCallback<T> {
    /**
     * callback that is run on the UI thread for
     * publishing the results.
     *
     * @param message the message that comes together with the data.
     * @param object  the actual data that needs to be used on the UI thread.
     * @param event   the event associated with the callback. E.g
     *                we can have GET_MOVIES, GET_VIDEOS, etc.
     */
    void onUpdateUI(String message, T object, int event);
}
