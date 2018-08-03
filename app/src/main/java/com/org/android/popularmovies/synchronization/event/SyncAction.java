package com.org.android.popularmovies.synchronization.event;

/**
 * Sync actions.
 * */
public enum SyncAction {

    GET_MOVIES(0),
    GET_MOVIE(1),
    GET_MOVIE_TRAILER(2),
    GET_GENRES(3),
    GET_REVIEWS(4);

    private int id;

    private SyncAction(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
